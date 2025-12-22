package fr.upc.g5e4.controller;

import org.jbox2d.dynamics.World;
import org.jbox2d.common.Vec2;
import fr.upc.g5e4.model.CharacterModel;
import fr.upc.g5e4.model.Item;
import fr.upc.g5e4.model.Weapon;
import fr.upc.g5e4.model.Projectile;
import fr.upc.g5e4.model.GroundModel;
import fr.upc.g5e4.view.ItemView;
import java.util.ArrayList;
import java.util.List;
import fr.upc.g5e4.model.SoundPlayer;

public class ItemController {
    private final ItemView view;
    private World world;
    private CharacterModel[] allWorms;
    private GroundModel groundModel;
    private List<Projectile> activeProjectiles;

    public ItemController(ItemView view) {
        this.view = view;
        this.activeProjectiles = new ArrayList<>();
    }

    /**
     * Définit le monde physique (non utilisé dans le système Graphwar)
     * @param world Le monde JBox2D
     */
    public void setWorld(World world) {
        this.world = world;
    }

    /**
     * Définit le tableau de tous les worms pour la détection de collision
     * @param allWorms Tableau de tous les worms du jeu
     */
    public void setAllWorms(CharacterModel[] allWorms) {
        this.allWorms = allWorms;
    }
    
    /**
     * définit le modèle du sol pour la détection de collision ( a utilisé pour la dégradation du sol)
     * @param groundModel Le modèle du terrain
     */
    public void setGroundModel(GroundModel groundModel) {
        this.groundModel = groundModel;
    }

    /**
     * Utilise un item standard
     * @param item L'item à utiliser
     */
    public void use(Item item){
        boolean ok = item.use();
        view.showUseResult(ok);
        view.showItem(item);
    }

    /**
     * Utilise une arme avec projectile pour tirer dans une direction (système Graphwar)
     * @param weapon L'arme à utiliser
     * @param shooter Le worm qui tire
     * @param direction Direction du tir (1 pour droite, -1 pour gauche)
     */
    public void useWeaponOnTarget(Weapon weapon, CharacterModel shooter, int direction) {
        if (allWorms == null) {
            return;
        }

        if (weapon.use()) {
            SoundPlayer.play("/sfx/gunshot.wav");

            Projectile projectile = weapon.fire(shooter, world, allWorms, direction);
            if (projectile != null) {
                activeProjectiles.add(projectile);
                System.out.println("Projectile lancé à l'angle: " + String.format("%.0f°", projectile.getShootingAngle()));
            }
            view.showItem(weapon);
        } else {
            view.showUseResult(false);
        }
    }
    
    /**
     * met à jour les projectiles actifs et détecte les collisions
     * @param deltaTime temps écoulé en secondes
     */
    public void updateProjectiles(float deltaTime) {
        List<Projectile> toRemove = new ArrayList<>();
        // met à jour chaque projectile
        for (Projectile projectile : activeProjectiles) {
            if (!projectile.update(deltaTime)) {
                toRemove.add(projectile);
                continue;
            }
            
            // permet de détecter le sol
            if (groundModel != null && hitGround(projectile)) {
                toRemove.add(projectile);
                continue;
            }
            
            // détection de collision avec les worms
            Vec2 projectilePos = projectile.getCurrentPosition();
            for (CharacterModel worm : allWorms) {
                if (worm != null && !worm.isDead()) {
                    // ignorer le tireur
                    if (worm == projectile.getShooter()) {
                        continue;
                    }
                    
                    Vec2 wormPos = worm.getBody().getPosition();
                    float distance = Math.abs(projectilePos.x - wormPos.x) + 
                                    Math.abs(projectilePos.y - wormPos.y);
                    
                    // Si le projectile est proche du worm 
                    if (distance < 0.7f) {
                        worm.dealDamage(projectile.getSourceWeapon().getDamage(), projectile.getShooter());
                        
                        // Calculer la direction de l'impact (du projectile vers le worm)
                        Vec2 impactDirection = new Vec2(
                            wormPos.x - projectilePos.x,
                            wormPos.y - projectilePos.y
                        );
                        
                        float impactForce = (float)(projectile.getSourceWeapon().getKnockbackMultiplier());
                        
                        // Appliquer l'impact au worm
                        worm.applyImpact(impactDirection, impactForce);
                        
                        System.out.println("Cible " + worm.getName() + " touchée avec " + 
                                         projectile.getSourceWeapon().getDamage() + " damage (impact: " + 
                                         String.format("%.2f", impactForce) + ")");
                        toRemove.add(projectile);
                        break;
                    }
                }
            }
        }
        
        // supp les projectiles morts
        activeProjectiles.removeAll(toRemove);
    }
    
    /**
     * vérifie si un projectile a touché le sol
     * @param projectile le projectile
     * @return true si le projectile a touché le sol
     */
    private boolean hitGround(Projectile projectile) {
        Vec2 pos = projectile.getCurrentPosition();
        
        //convertit la position monde en position écran pour vérifier la hauteur du terrain
        float PPM = 30f;
        int heightPx = 720;  // hauteur de l'écran en pixels
        
        // position écran du projectile
        float screenY = (heightPx - (pos.y * PPM));
        
        //obtenir la hauteur du sol à la position X du projectile
        float groundHeightPx = groundModel.getGroundHeightAt(pos.x * PPM);
        
        // le projectile a touché le sol si screenY >= groundHeightPx
        return screenY >= groundHeightPx;
    }
    
    /**
     * retourne la liste des projectiles actifs
     */
    public List<Projectile> getActiveProjectiles() {
        return activeProjectiles;
    }
}

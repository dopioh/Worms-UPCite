package fr.upc.g5e4.controller;

import org.jbox2d.dynamics.World;
import org.jbox2d.common.Vec2;
import fr.upc.g5e4.model.CharacterModel;
import fr.upc.g5e4.model.Item;
import fr.upc.g5e4.model.Weapon;
import fr.upc.g5e4.model.Projectile;
import fr.upc.g5e4.model.BlockGridModel;
import fr.upc.g5e4.model.WindModel;
import fr.upc.g5e4.model.GameManager;
import fr.upc.g5e4.view.ItemView;
import java.util.ArrayList;
import java.util.List;
import fr.upc.g5e4.model.SoundPlayer;

public class ItemController {
    private final ItemView view;
    private World world;
    private CharacterModel[] allWorms;
    private BlockGridModel blockGridModel;
    private WindModel windModel;
    private List<Projectile> activeProjectiles;
    private GameManager gameManager;

    public ItemController(ItemView view) {
        this.view = view;
        this.activeProjectiles = new ArrayList<>();
    }
    
    /**
     * Définit le GameManager pour le tracking des statistiques
     * @param gameManager Le gestionnaire de jeu
     */
    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * Définit le monde physique (non utilisé dans le système Graphwar)
     * @param world Le monde JBox2D
     */
    public void setWorld(World world) {
        this.world = world;
    }

    /**
     * définit le modele du vent
     * @param windModel le modele du vent
     */
    public void setWindModel(WindModel windModel) {
        this.windModel = windModel;
    }

    /**
     * Définit le tableau de tous les worms pour la détection de collision
     * @param allWorms Tableau de tous les worms du jeu
     */
    public void setAllWorms(CharacterModel[] allWorms) {
        this.allWorms = allWorms;
    }
    
    /**
     * définit le BlockGridModel pour les dégâts au terrain
     * @param blockGridModel Le modèle de grille de blocs
     */
    public void setBlockGridModel(BlockGridModel blockGridModel) {
        this.blockGridModel = blockGridModel;
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
            SoundPlayer.play("/sfx/gunshot.wav"); // SFX au tir

            Projectile projectile = weapon.fire(shooter, world, allWorms, direction, windModel);
            if (projectile != null) {
                activeProjectiles.add(projectile);
                System.out.println("Projectile lancé à l'angle: " + String.format("%.0f°", projectile.getShootingAngle()));
                
                // Tracker le tir
                if (gameManager != null) {
                    gameManager.getCurrentTeamStats().incrementShotsFired();
                }
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
            if (!projectile.update(deltaTime)) { // retourne faux si le projectile a dépassé sa portée
                applyTerrainDamage(projectile); 
                toRemove.add(projectile);
                continue;
            }
            
            // permet de détecter le sol
            if (hitGround(projectile)) {
                applyTerrainDamage(projectile);
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
                        double damage = projectile.getSourceWeapon().getDamage();
                        boolean wasAlive = !worm.isDead();
                        
                        worm.dealDamage(damage, projectile.getShooter());
                        
                        // Tracker les stats
                        if (gameManager != null) {
                            gameManager.getCurrentTeamStats().incrementShotsHit();
                            gameManager.getCurrentTeamStats().addDamage((int)damage);
                            
                            // Si le worm est mort suite aux dégâts
                            if (wasAlive && worm.isDead()) {
                                gameManager.getCurrentTeamStats().incrementKills();
                            }
                        }
                        
                        // Calculer la direction de l'impact (du projectile vers le worm)
                        Vec2 impactDirection = new Vec2(
                            wormPos.x - projectilePos.x,
                            wormPos.y - projectilePos.y
                        );
                        
                        float impactForce = (float)(projectile.getSourceWeapon().getKnockbackMultiplier());
                        
                        // Appliquer l'impact au worm
                        worm.applyImpact(impactDirection, impactForce);
                        
                        System.out.println("Cible " + worm.getName() + " touchée avec " + 
                                         damage + " damage (impact: " + 
                                         String.format("%.2f", impactForce) + ")");
                        applyTerrainDamage(projectile);
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
        if (blockGridModel == null) {
            return false;
        }
        
        Vec2 pos = projectile.getCurrentPosition();
        float PPM = 30f; // pixel par mètre
        
        //convertit la position monde en pixels
        int pixelX = (int)(pos.x * PPM);
        int pixelY = (int)(blockGridModel.getPixelHeight() - pos.y * PPM);
        
        // verifie si le projectile touche un bloc solide de la grille
        return blockGridModel.isSolidAt(pixelX, pixelY);
    }
    
    /**
     * applique les degats au terrain quand le projectile touche le sol
     * @param projectile Le projectile qui impacte le terrain
     */
    private void applyTerrainDamage(Projectile projectile) {
        if (blockGridModel == null) {
            return;
        }
        
        Weapon weapon = projectile.getSourceWeapon(); // arme qui a tiré le projectile
        if (weapon == null || weapon.getTerrainDamage() == 0) {
            return;
        }
        
        Vec2 projectilePos = projectile.getCurrentPosition();
        
        // convertit la position du monde (JBox2D en mètres) en coordonnées de grille
        float PPM = 30f;
        
        // convertir de metres en pixels
        int pixelX = (int)(projectilePos.x * PPM);
        // Y est inversé entre JBox2D (Y augmente vers le haut) et pixels (Y augmente vers le bas)
        int pixelY = (int)(blockGridModel.getPixelHeight() - projectilePos.y * PPM);
        
        // convertir de pixels en coordonnées de grille
        int centerGridX = blockGridModel.pixelToGridX(pixelX);
        int centerGridY = blockGridModel.pixelToGridY(pixelY);
        
        // appliquer les degats au terrain
        if (weapon.getTerrainBlastRadius() > 0) {
            // dégats en zone 
            blockGridModel.damageBlocksInRadius(centerGridX, centerGridY, weapon.getTerrainBlastRadius(), weapon.getTerrainDamage());
            System.out.println("[" + weapon.getName() + "] Dégâts terrain en zone : " + weapon.getTerrainDamage() + " PV, rayon " + weapon.getTerrainBlastRadius());
        } else {
            // dégats ponctuels
            blockGridModel.damageBlock(centerGridX, centerGridY, weapon.getTerrainDamage());
            System.out.println("[" + weapon.getName() + "] Dégâts terrain ponctuels : " + weapon.getTerrainDamage() + " PV");
        }
    }
    
    /**
     * retourne la liste des projectiles actifs
     */
    public List<Projectile> getActiveProjectiles() {
        return activeProjectiles;
    }
}

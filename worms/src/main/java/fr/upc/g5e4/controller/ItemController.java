package fr.upc.g5e4.controller;

import org.jbox2d.dynamics.World;
import org.jbox2d.common.Vec2;
import fr.upc.g5e4.model.CharacterModel;
import fr.upc.g5e4.model.Item;
import fr.upc.g5e4.model.Weapon;
import fr.upc.g5e4.model.ProjectileModel;
import fr.upc.g5e4.model.SoundPlayer;
import fr.upc.g5e4.model.BlockGridModel;
import fr.upc.g5e4.model.PhysicsWorld;
import fr.upc.g5e4.model.WindModel;
import fr.upc.g5e4.model.GameManager;
import fr.upc.g5e4.view.ItemView;
import java.util.ArrayList;
import java.util.List;

public class ItemController {
    private final ItemView view;
    private World world;
    private PhysicsWorld physicsWorld;
    private CharacterModel[] allWorms;
    private BlockGridModel blockGridModel;
    private WindModel windModel;
    private List<ProjectileModel> activeProjectiles;
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
     * Définit le monde physique JBox2D
     * @param world Le monde JBox2D
     */
    public void setWorld(World world) {
        this.world = world;
    }
    
    /**
     * définit le PhysicsWorld pour reconstruire le terrain après destruction
     * @param physicsWorld Le gestionnaire de physique
     */
    public void setPhysicsWorld(PhysicsWorld physicsWorld) {
        this.physicsWorld = physicsWorld;
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
     * Utilise une arme avec projectile pour tirer dans une direction 
     * @param weapon L'arme à utiliser
     * @param shooter Le worm qui tire
     * @param direction Direction du tir (1 pour droite, -1 pour gauche)
     */
    public void useWeaponOnTarget(Weapon weapon, CharacterModel shooter, int direction) {
        if (allWorms == null) {
            return;
        }

        if (weapon.use()) {
            // on fait jouer le son de l'arme
            String soundPath = getWeaponSoundPath(weapon);
            if (soundPath != null) {
                SoundPlayer.playWeaponSound(soundPath);
            }

            ProjectileModel projectile = weapon.fire(shooter, world, allWorms, direction, windModel);
            if (projectile != null) {
                activeProjectiles.add(projectile);
                System.out.println("projectile lancé avec un angle de " + String.format("%.0f°", projectile.getShootingAngle()));
                
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
        List<ProjectileModel> toRemove = new ArrayList<>();
        // met à jour chaque projectile
        for (ProjectileModel projectile : activeProjectiles) {
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
                        
                        System.out.println(worm.getName() + " touché " + damage + " dégâts infligés : " + String.format("%.2f", impactForce) + ")");
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
    private boolean hitGround(ProjectileModel projectile) {
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
    private void applyTerrainDamage(ProjectileModel projectile) {
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
        boolean modified = false; // indique si le terrain a été modifié
        System.out.println("impact sur le terrain à la position (" + centerGridX + ", " + centerGridY + ")");
        
        // appliquer les degats au terrain
        if (weapon.getTerrainBlastRadius() > 0) {
            // dégats en zone 
            blockGridModel.damageBlocksInRadius(centerGridX, centerGridY, weapon.getTerrainBlastRadius(), weapon.getTerrainDamage());
            System.out.println(weapon.getName() + " inflige " + weapon.getTerrainDamage() + " dégâts au terrain (rayon de " + weapon.getTerrainBlastRadius() + " blocs)");
            modified = true;
        } else {
            // dégats ponctuels
            if (blockGridModel.damageBlock(centerGridX, centerGridY, weapon.getTerrainDamage())) {
                modified = true;
            }
            System.out.println(weapon.getName() + " inflige " + weapon.getTerrainDamage() + " dégâts au terrain");
        }
        
        System.out.println("terrain modifié : " + (modified ? "oui" : "non"));
        
        // appliquer les dégats d'explosion 
        applyExplosionDamageToWorms(projectile);
        
        // on supprime les blocks flottants après destruction
        if (modified) {
            // int searchRadius = weapon.getTerrainBlastRadius() > 0 ? (int)(weapon.getTerrainBlastRadius() * 4) : 20; // SI ON MANQUE DE RESSOURCES
            int floatingRemoved = blockGridModel.removeFloatingBlocks(centerGridX, centerGridY, 200);
            if (floatingRemoved > 0) {
                System.out.println("[TERRAIN] " + floatingRemoved + " blocs flottants supprimés (rayon de recherche: 200)"); 
            }
        }
        
        // Reconstruire la physique si des blocs ont été détruits
        if (modified && physicsWorld != null) {
            System.out.println("[TERRAIN] Appel de rebuildGroundPhysics...");
            physicsWorld.rebuildGroundPhysics(blockGridModel);
        } else if (modified && physicsWorld == null) {
            System.out.println("[TERRAIN] ERROR: physicsWorld est NULL !");
        }
    }
    
    /**
     * applique les dégats de zone
     * @param projectile Le projectile qui a explosé
     */
    private void applyExplosionDamageToWorms(ProjectileModel projectile) {
        if (allWorms == null) {
            return;
        }
        
        Weapon weapon = projectile.getSourceWeapon(); // arme qui a tiré le projectile
        if (weapon == null || weapon.getBlastRadius() <= 0) {
            return; // pas d'explosion de zone
        }
        
        Vec2 explosionCenter = projectile.getCurrentPosition();
        float blastRadiusInMeters = (float) weapon.getBlastRadius();
        int baseDamage = weapon.getDamage();
        double knockbackMultiplier = weapon.getKnockbackMultiplier();
        
        CharacterModel shooter = projectile.getShooter();
        
        // parcourir tous les worms
        for (CharacterModel worm : allWorms) {
            if (worm == null || worm.isDead()) {
                continue;
            }
            
            // calculer la distance entre le centre de l'explosion et le worm
            Vec2 wormPos = worm.getBody().getPosition();
            float distanceX = wormPos.x - explosionCenter.x;
            float distanceY = wormPos.y - explosionCenter.y;
            float distance = (float) Math.sqrt(distanceX * distanceX + distanceY * distanceY);
            
            // vérifier si le worm est dans le rayon d'explosion
            if (distance <= blastRadiusInMeters) {
                // les dégats diminuent avec la distance (plus proche = plus de dégats)
                double damageMultiplier = 1.0 - (distance / blastRadiusInMeters);
                int actualDamage = Math.max(1, (int) (baseDamage * damageMultiplier));
                
                // appliquer les dégats
                worm.dealDamage(actualDamage, shooter);
                System.out.println(worm.getName() + " subit " + actualDamage + " dégats d'explosion (à " + String.format("%.2f", distance) + "m)");
                
                // appliquer un impact de recul
                if (distance > 0) {
                    // direction de l'impact (depuis le centre vers le worm)
                    Vec2 impactDirection = new Vec2(distanceX / distance, distanceY / distance);
                    float impactForce = (float) (knockbackMultiplier * (1.0 - (distance / blastRadiusInMeters)) * 10.0f);
                    worm.applyImpact(impactDirection, impactForce);
                    System.out.println("explosion appliquée avec une force de " + String.format("%.2f", impactForce));
                }
            }
        }
    }
    
    /**
     * retourne la liste des projectiles actifs
     */
    public List<ProjectileModel> getActiveProjectiles() {
        return activeProjectiles;
    }
    
    /**
     * Retourne le chemin du son pour une arme donnée
     * @param weapon l'arme pour laquelle obtenir le son
     * @return le chemin du son dans resources/sfx/ ou null si non trouvé
     */
    private String getWeaponSoundPath(Weapon weapon) {
        if (weapon == null) {
            return null;
        }

        String className = weapon.getClass().getSimpleName();
        String fileName = className.toLowerCase();
        String wavPath = "/sfx/" + fileName + "-sound.wav";
        
        // si le fichier son existe (obligatoirement en .wav)
        if (SoundPlayer.class.getResourceAsStream(wavPath) != null) {
            return wavPath;
        }
        
        // si on trouve pas le fichier son
        System.err.println("Aucun fichier son trouvé pour l'arme: " + className);
        return null;
    }
    
    /**
     *utilise la Foreuse pour casser un bloc au clic
     * @param pixelX position X du clic en pixels
     * @param pixelY position Y du clic en pixels
     */
    /**
     * vérifie que le clic est à portée du personnage actif
     * @param pixelX position X du clic en pixels
     * @param pixelY position Y du clic en pixels
     * @param activeWorm le worm actif
     * @param maxRange portée maximale en mètres
     * @return true si à portée, false sinon
     */
    private boolean isDrillRange(int pixelX, int pixelY, CharacterModel activeWorm, float maxRange) {
        if (activeWorm == null || blockGridModel == null) return true;
        
        Vec2 wormPos = activeWorm.getBody().getPosition();
        float wormX = wormPos.x;
        float wormY = wormPos.y;
        
        // convertir les coordonnées du clic de pixels en mètres avec inversion Y
        float ppm = CharacterModel.getPPM();
        float blockGridHeight = blockGridModel.getHeight();
        float clickX = pixelX / ppm;
        float clickY = (blockGridHeight - pixelY) / ppm;
        
        // calculer la distance entre le worm et le clic
        float dx = clickX - wormX;
        float dy = clickY - wormY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (distance > maxRange) {
            System.out.println("drill: trop loin du personnage ! (distance: " + String.format("%.1f", distance) + "m, max: " + maxRange + "m)");
            return false;
        }
        
        return true;
    }
    
    public boolean drillBreakBlocks(int pixelX, int pixelY, CharacterModel activeWorm) {
        if (blockGridModel == null) return false;
        
        // vérifier que le clic est à portée du personnage
        if (!isDrillRange(pixelX, pixelY, activeWorm, 1.5f)) {
            return false;
        }
        
        // convertir en coordonnées grille
        int gridX = blockGridModel.pixelToGridX(pixelX);
        int gridY = blockGridModel.pixelToGridY(pixelY);
        
        boolean anyBlockDestroyed = false;
        
        // casser les blocs dans une zone 2x2
        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                int targetX = gridX + x;
                int targetY = gridY + y;
                
                if (blockGridModel.destroyBlock(targetX, targetY)) {
                    anyBlockDestroyed = true;
                }
            }
        }
        
        if (anyBlockDestroyed) {
            //supprimer les blocs flottants
            blockGridModel.removeFloatingBlocks(gridX, gridY, 5);
            
            //recalculer la physique
            if (blockGridModel.isDirty()) {
                physicsWorld.recreateGroundFromBlockGrid(blockGridModel);
                blockGridModel.clearDirty();
                SoundPlayer.play("/sfx/drill-break.wav");
            }
        }
        
        return anyBlockDestroyed;
    }
    
    /**
     * utilise la Foreuse pour poser un bloc au clic
     * @param pixelX position X du clic en pixels
     * @param pixelY position Y du clic en pixels
     * @param blockType type de bloc à poser
     * @param activeWorm le worm actif
     */
    public boolean drillPlaceBlock(int pixelX, int pixelY, fr.upc.g5e4.model.BlockType blockType, CharacterModel activeWorm) {
        if (blockGridModel == null) return false;
        
        // vérifier que le clic est à portée du personnage
        if (!isDrillRange(pixelX, pixelY, activeWorm, 1.0f)) {
            return false;
        }
        
        // convertir en coordonnées grille
        int gridX = blockGridModel.pixelToGridX(pixelX);
        int gridY = blockGridModel.pixelToGridY(pixelY);
        
        // vérifier que la position est vide (AIR)
        if (blockGridModel.getBlock(gridX, gridY) == fr.upc.g5e4.model.BlockType.AIR) {
            blockGridModel.setBlock(gridX, gridY, blockType);
            
            // recalculer la physique
            if (blockGridModel.isDirty()) {
                physicsWorld.recreateGroundFromBlockGrid(blockGridModel);
                blockGridModel.clearDirty();
                SoundPlayer.play("/sfx/drill-place.wav");
            }
            return true;
        }
        
        return false;
    }

    /**
     * utilise le Teleporter pour se téléporter à une position
     * @param worldPixelX position X du clic en pixels monde
     * @param worldPixelY position Y du clic en pixels monde
     * @param worm le worm à téléporter
     */
    public void teleportWorm(int worldPixelX, int worldPixelY, CharacterModel worm) {
        if (worm == null || blockGridModel == null) return;
        
        // vérifier s'il y a un bloc solide à cette position
        if (blockGridModel.isSolidAt(worldPixelX, worldPixelY)) {
            return;
        }
        
        // convertir pixels monde en mètres JBox2D avec inversion Y
        float ppm = CharacterModel.getPPM();
        float blockGridHeightPixels = blockGridModel.getHeight();
        
        float targetWorldX = worldPixelX / ppm;
        float targetWorldY = (blockGridHeightPixels - worldPixelY) / ppm;
        
        // téléporter le worm
        worm.getBody().setTransform(new org.jbox2d.common.Vec2(targetWorldX, targetWorldY), worm.getBody().getAngle());
        
        //permet de faire tomber le worm s'il est en l'air pendant la tp
        FallIfInTheAir(worm);
        
        SoundPlayer.play("/sfx/teleporter-sound.wav");
    }
    
    /**
     * vérifie si le worm est en l'air et applique une force vers le bas pour faire tomber
     * @param worm le worm à vérifier
     */
    private void FallIfInTheAir(CharacterModel worm) {
        if (worm == null) return;
        
        // vérifier si le worm est en l'air
        if (!worm.isOnGround()) {
            // appliquer une petite impulsion vers le bas pour initier la chute
            org.jbox2d.common.Vec2 downwardForce = new org.jbox2d.common.Vec2(0, -0.1f);
            worm.getBody().applyLinearImpulse(downwardForce, worm.getBody().getWorldCenter());
        }
    }
}



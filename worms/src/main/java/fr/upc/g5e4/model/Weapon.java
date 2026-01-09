package fr.upc.g5e4.model;
import org.jbox2d.common.Vec2;
//import org.jbox2d.dynamics.Body;

/**
 * Armes : ajout de damage, blastRadius et speed.
 */

public abstract class Weapon extends Item {
    
    private final int damage;         
    private final double blastRadius; // rayon d'explosion (0 si aucun)
    private final double speed;       // vitesse de projectile ou déplacement
    private final double range;    // portée de l'arme
    private final double knockbackMultiplier; // multiplicateur de recul
    private int terrainDamage = 0;    // dégâts causés au terrain (en PV de blocs)
    private double terrainBlastRadius = 0.0; // rayon de dégâts au terrain en blocs
    private double shootingAngle = 0;  // angle de tir en degrés (0-360)
    private TrajectoryModel trajectoryCalculator;  // calculatrice de trajectoire spécifique à l'arme

    protected Weapon(String name, int maxUsages, boolean isInfinite,
                     int damage, double blastRadius, double speed, int range, double knockbackMultiplier) {
        super(name, ItemType.WEAPON, maxUsages, isInfinite);
        if (damage < 0 || blastRadius < 0 || speed < 0 || range < 0)
            throw new IllegalArgumentException("damage/blastRadius/speed/range doivent être >= 0");
        if (knockbackMultiplier < 0)
            throw new IllegalArgumentException("knockbackMultiplier doit être >= 0");
        this.damage = damage;
        this.blastRadius = blastRadius;
        this.speed = speed;
        this.range = range;
        this.knockbackMultiplier = knockbackMultiplier;
    }

    public int getDamage() { return damage; }
    public double getBlastRadius() { return blastRadius; }
    public double getSpeed() { return speed; }
    public double getRange() { return range; }
    public double getKnockbackMultiplier() { return knockbackMultiplier; }
    public int getTerrainDamage() { return terrainDamage; }
    public double getTerrainBlastRadius() { return terrainBlastRadius; }
    
    /**
     * définit les dégats causés au terrain
     * @param terrainDamage dégats au terrain (PV de blocs)
     */
    public void setTerrainDamage(int terrainDamage) {
        this.terrainDamage = Math.max(0, terrainDamage);
    }
    
    /**
     * définit le rayon de dégats au terrain
     * @param terrainBlastRadius rayon en nombre de blocs
     */
    public void setTerrainBlastRadius(double terrainBlastRadius) {
        this.terrainBlastRadius = Math.max(0, terrainBlastRadius);
    }
    
    /**
     * définit le calculateur de trajectoire personnalisé pour une arme
     */
    public void setTrajectoryCalculator(TrajectoryModel calculator) {
        this.trajectoryCalculator = calculator;
    }
    
    /**
     * obtient le calculateur de trajectoire de cette arme
     */
    public TrajectoryModel getTrajectoryCalculator() {
        return this.trajectoryCalculator;
    }
    
    /**
     * Définit l'angle de tir en degrés (0-360)
     */
    public void setShootingAngle(double angle) {
        this.shootingAngle = angle % 360;
    }
    
    /**
     * Obtient l'angle de tir
     */
    public double getShootingAngle() {
        return this.shootingAngle;
    }
    

    /**
     * tire un projectile avec une trajectoire (sans vent)
     * @param shooter Le worm qui tire
     * @param world Le monde physique
     * @param allWorms Tableau de tous les worms du jeu
     * @param direction direction du tir (1 pour droite, -1 pour gauche)
     */
    public ProjectileModel fire(CharacterModel shooter, org.jbox2d.dynamics.World world, 
        CharacterModel[] allWorms, int direction) {
        return fire(shooter, world, allWorms, direction, null);
    }

    /**
     * tire un projectile avec une trajectoire 
     * @param shooter le worm qui tire
     * @param world le monde physique
     * @param allWorms tableau de tous les worms du jeu
     * @param direction direction du tir (1 pour droite, -1 pour gauche)
     * @param windModel le modele du vent (peut être null)
     */
    public ProjectileModel fire(CharacterModel shooter, org.jbox2d.dynamics.World world, 
        CharacterModel[] allWorms, int direction, WindModel windModel) {
        Vec2 shooterPos = shooter.getBody().getPosition();
        
        // calculer la portée modifiée par le vent
        double adjustedRange = this.range;
        if (windModel != null) {
            // appliquer l'effet du vent sur la portée
            adjustedRange = applyWindEffect(this.range, this.shootingAngle, windModel, direction);
        }
        
        // créer le projectile avec la portée ajustée
        ProjectileModel projectile = new ProjectileModel(this, shooter, shooterPos, adjustedRange, this.speed, this.shootingAngle);
        String windInfo = windModel != null ? String.format(" (vent: %s %.0f%% -> portée ajustée: %.1f)", 
            windModel.getDir().label, windModel.getStrength() * 100, adjustedRange) : "";
        System.out.println("[" + this.getName() + "] Tir à angle " + String.format("%.0f°", shootingAngle) + 
                         " portée=" + this.range + windInfo);
        
        return projectile;
    }

    /**
     * applique l'effet du vent sur la portée du projectile
     * @param baseRange portée de base
     * @param shootingAngle angle de tir en degrés
     * @param windModel modele du vent
     * @param shooterDirection direction du tireur (1 pour droite, -1 pour gauche)
     * @return portée ajustée par le vent
     */
    private double applyWindEffect(double baseRange, double shootingAngle, WindModel windModel, int shooterDirection) {
        if (windModel == null || windModel.getStrength() == 0) {
            return baseRange;
        }
        
        // direction du vent en angles (0 = Est, 90 = Nord, etc.)
        WindModel.Dir windDir = windModel.getDir();
        float windStrength = windModel.getStrength();
        
        // calculer l'angle du vent
        float windAngle = getWindAngleInDegrees(windDir);
        
        // convertir les angles de tir en angle de direction (0 = Est)
        // l'angle de tir est relatif au tireur (shootingAngle = 45 peut être vers haut-droit ou haut-gauche)
        double trajectoryAngle = shootingAngle;
        if (shooterDirection < 0) {
            // si le tireur regarde à gauche, inverser l'angle
            trajectoryAngle = 180 - shootingAngle;
        }
        
        // calculer la composante horizontale (qui affecte la portée)
        double projectileX = Math.cos(Math.toRadians(trajectoryAngle));
        double windX = Math.cos(Math.toRadians(windAngle));
        
        // calcul pour déterminer la ligne entre le vent et la trajectoire
        // 1.0 = vent exactement dans la même direction
        // -1.0 = vent exactement contraire
        // 0.0 = vent perpendiculaire
        double alignment = projectileX * windX;
        
        // appliquer l'effet du vent
        // effet positif (vent dans le même sens) : augmente la portée
        // effet négatif (vent contraire) : diminue la portée
        double windEffect = 1.0 + (alignment * windStrength * 0.3); // 0.3 = facteur d'effet max
        
        return baseRange * windEffect;
    }

    /**
     * convertit une direction du vent en angle en degrés (0 = Est, 90 = Nord)
     */
    private float getWindAngleInDegrees(WindModel.Dir dir) {
        return switch (dir) {
            case E -> 0f;      // Est
            case NE -> 45f;    // Nord-Est
            case N -> 90f;     // Nord
            case NO -> 135f;   // Nord-Ouest
            case O -> 180f;    // Ouest
            case SO -> 225f;   // Sud-Ouest
            case S -> 270f;    // Sud
            case SE -> 315f;   // Sud-Est
        };
    }

}

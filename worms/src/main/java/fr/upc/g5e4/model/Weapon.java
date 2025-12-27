package fr.upc.g5e4.model;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

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
    private TrajectoryInterface trajectoryCalculator;  // calculatrice de trajectoire spécifique à l'arme

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
    public void setTrajectoryCalculator(TrajectoryInterface calculator) {
        this.trajectoryCalculator = calculator;
    }
    
    /**
     * obtient le calculateur de trajectoire de cette arme
     */
    public TrajectoryInterface getTrajectoryCalculator() {
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
     * Tire un projectile avec une trajectoire 
     * @param shooter Le worm qui tire
     * @param allWorms Tableau de tous les worms du jeu
     */
    public Projectile fire(CharacterModel shooter, org.jbox2d.dynamics.World world, 
        CharacterModel[] allWorms, int direction) {
        Vec2 shooterPos = shooter.getBody().getPosition();
        
        // créer le projectile avec l'angle de tir
        Projectile projectile = new Projectile(this, shooter, shooterPos, this.range, this.speed, this.shootingAngle);
        System.out.println("[" + this.getName() + "] Tir à angle " + String.format("%.0f°", shootingAngle) + 
                         " portée=" + this.range);
        
        return projectile;
    }

}

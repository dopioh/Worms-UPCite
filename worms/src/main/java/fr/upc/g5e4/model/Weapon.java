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
    private final int range;          // portée de l'arme
    private final double knockbackMultiplier; // multiplicateur de recul
    private double shootingAngle = 0;  // angle de tir en degrés (0-360)

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
    public int getRange() { return range; }
    public double getKnockbackMultiplier() { return knockbackMultiplier; }
    
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
     * Getter pour amplitude (gardé pour compatibilité)
     */
    public double getCurveAmplitude() {
        return 0.5;  // Valeur par défaut
    }

    /**
     * Tire un projectile avec une trajectoire basée sur l'angle de tir
     * @param shooter Le worm qui tire
     * @param world Le monde physique JBox2D (non utilisé, mais gardé pour compatibilité)
     * @param allWorms Tableau de tous les worms du jeu
     * @param direction Direction du tir (non utilisé maintenant, déterminé par l'angle)
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

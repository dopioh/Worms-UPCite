package fr.upc.g5e4.item.model;

/**
 * Armes : ajout de damage, blastRadius et speed.
 */

public abstract class Weapon extends Item {
    
    private final int damage;         
    private final double blastRadius; // rayon d'explosion (0 si aucun)
    private final double speed;       // vitesse de projectile ou déplacement

    protected Weapon(String name, int maxUsages, boolean isInfinite,
                     int damage, double blastRadius, double speed) {
        super(name, ItemType.WEAPON, maxUsages, isInfinite);
        if (damage < 0 || blastRadius < 0 || speed < 0)
            throw new IllegalArgumentException("damage/blastRadius/speed doivent être >= 0");
        this.damage = damage;
        this.blastRadius = blastRadius;
        this.speed = speed;
    }

    public int getDamage() { return damage; }
    public double getBlastRadius() { return blastRadius; }
    public double getSpeed() { return speed; }

}

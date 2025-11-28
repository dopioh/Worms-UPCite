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

    /**
     * Tire un rayon depuis le worm attaquant pour trouver une cible et infliger des dégâts
     * @param shooter Le worm qui tire
     * @param world Le monde physique JBox2D
     * @param allWorms Tableau de tous les worms du jeu
     * @param direction Direction du tir (1 pour droite, -1 pour gauche)
     */
    public void fire(CharacterModel shooter, org.jbox2d.dynamics.World world, 
                               CharacterModel[] allWorms, int direction) {
        Vec2 shooterPos = shooter.getBody().getPosition();
        float rayDistance = 20f; //Porter max du tir
        Vec2 rayEnd = new Vec2(
            shooterPos.x + (direction * rayDistance), 
            shooterPos.y
        );
        
        final CharacterModel[] target = {null};
        
        world.raycast((fixture, point, normal, fraction) -> { 
            Body hitBody = fixture.getBody(); //Le corps touché par le rayon
            
            for (CharacterModel worm : allWorms) {
                if (worm != null && worm.getBody() == hitBody && !worm.isDead()) { 
                    target[0] = worm;
                    return 0;
                }
            }
            return 1;
        }, shooterPos, rayEnd);
        
        if (target[0] != null) {
            target[0].dealDamage(this.getDamage(), shooter);
        } else {
        }
    }

}

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
    private final int range;       // portée de l'arme
    private double shootAngle = 0;   // angle de tir (0 = horizontal)

    protected Weapon(String name, int maxUsages, boolean isInfinite,
                     int damage, double blastRadius, double speed, int range) {
        super(name, ItemType.WEAPON, maxUsages, isInfinite);
        if (damage < 0 || blastRadius < 0 || speed < 0 || range < 0)
            throw new IllegalArgumentException("damage/blastRadius/speed/range doivent être >= 0");
        this.damage = damage;
        this.blastRadius = blastRadius;
        this.speed = speed;
        this.range = range;
    }

    public int getDamage() { return damage; }
    public double getBlastRadius() { return blastRadius; }
    public double getSpeed() { return speed; }
    public int getRange() { return range; }
    
    /**
     *définit l'angle de tir
     *@param angle L'angle en radians
     */
    public void setShootingAngle(double angle) {
        this.shootAngle = angle;
    }
    
    /**
     * Obtient l'angle de tir
     * @return L'angle en radians
     */
    public double getShootAngle() {
        return this.shootAngle;
    }

    /**
     * Tire un rayon depuis le worm attaquant pour trouver une cible et infliger des dégâts
     * utilise l'angle de tir pour calculer la direction
     * @param shooter Le worm qui tire
     * @param world Le monde physique JBox2D
     * @param allWorms Tableau de tous les worms du jeu
     * @param direction Direction du tir (1 pour droite, -1 pour gauche)
     */
    public void fire(CharacterModel shooter, org.jbox2d.dynamics.World world, 
                               CharacterModel[] allWorms, int direction) {
        Vec2 shooterPos = shooter.getBody().getPosition();
        float rayDistance = this.getRange(); //Portée max du tir
        
        //calcul du rayon en utilisant l'angle
        float rayEndX = shooterPos.x + (float)(rayDistance * Math.cos(shootAngle));
        float rayEndY = shooterPos.y + (float)(rayDistance * Math.sin(shootAngle));
        
        Vec2 rayEnd = new Vec2(rayEndX, rayEndY);
        
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
            System.out.println("Cible " + target[0].getName() + " touché " + this.getDamage() + " damage.");
        } else {
            System.out.println("Aucune cible touchée.");
        }
    }

}

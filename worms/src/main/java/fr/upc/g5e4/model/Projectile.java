package fr.upc.g5e4.model;

import org.jbox2d.common.Vec2;

/**
 * Classe représentant un projectile avec une trajectoire courbe mathématique
 */
public class Projectile {
    private Weapon sourceWeapon;
    private CharacterModel shooter;
    private Vec2 startPosition;
    private double range;
    private double speed;
    private double shootingAngle;  // angle en degrés (0-360)
    private float lifetime;
    private static final float MAX_LIFETIME = 10f;  // durée de vie maximale
    
    /**
     * constructeur du projectile avec trajectoire
     * @param weapon l'arme qui tire
     * @param shooter le worms qui tire
     * @param position position initiale
     * @param range portée du projectile
     * @param speed vitesse (utilisée pour calculer la durée du trajet)
     * @param shootingAngle Angle de tir 
     */
    public Projectile(Weapon weapon, CharacterModel shooter, Vec2 position,
                             double range, double speed, double shootingAngle) {
        this.sourceWeapon = weapon;
        this.shooter = shooter;
        this.startPosition = new Vec2(position);
        this.range = range;
        this.speed = speed;
        this.shootingAngle = shootingAngle;
        this.lifetime = 0f;
    }
    
    /**
     * calcule la position du projectile à un temps donné
     * 
     * @param t Temps entre 0 et 1 (0 = départ, 1 = fin)
     * @return Position du projectile
     */
    public Vec2 getPositionAtTime(float t) {
        // t doit être entre 0 et 1
        t = Math.max(0, Math.min(1, t));
        
        // convertir l'angle en radians
        double angleRad = Math.toRadians(shootingAngle);
        
        // la trajectoire parcourt la distance 'range' toujours horizontalement
        float horizontalDistance = (float)(range * t);
        
        // X et Y varient selon l'angle de tir
        float x = startPosition.x + (float)(horizontalDistance * Math.cos(angleRad));
        float y = startPosition.y + (float)(horizontalDistance * Math.sin(angleRad));
        
        return new Vec2(x, y);
    }
    
    /**
     * met à jour la durée de vie du projectile
     * @param deltaTime Temps écoulé en secondes
     * @return true si le projectile est encore vivant
     */
    public boolean update(float deltaTime) {
        lifetime += deltaTime;
        return lifetime < MAX_LIFETIME;
    }
    
    /**
     * obtient la position actuelle du projectile
     * @return position basée sur la durée de vie
     */
    public Vec2 getCurrentPosition() {
        // calculer t en fonction de la durée (durée totale ≈ range / speed)
        float totalDuration = (float)(range / speed);
        float t = Math.min(lifetime / totalDuration, 1.0f);
        return getPositionAtTime(t);
    }
    
    /**
     * obtient l'angle de tir du projectile
     */
    public double getShootingAngle() {
        return shootingAngle;
    }
    
    /**
     * obtient la portée du projectile
     */
    public double getRange() {
        return range;
    }
    
    /**
     * obtient le worms qui a tiré le projectile
     */
    public CharacterModel getShooter() {
        return shooter;
    }
    
    /**
     * obtient l'arme qui a tiré le projectile
     */
    public Weapon getSourceWeapon() {
        return sourceWeapon;
    }
}

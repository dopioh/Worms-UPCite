package fr.upc.g5e4.model.trajectories;

import org.jbox2d.common.Vec2;
import fr.upc.g5e4.model.TrajectoryInterface;

/**
 * trajectoire rapide (faible gravité)
 * utilisée pour les projectiles rapides 
 */
public class FastTrajectory implements TrajectoryInterface {
    private static final float DROP_COEFFICIENT = 2.0f; // chute minime
    
    @Override
    public Vec2 calculatePosition(float t, Vec2 startPos, double range, double angle) {
        t = Math.max(0, Math.min(1, t));
        
        double angleRad = angle;
        float horizontalDistance = (float)(range * t);
        
        // position basée sur l'angle
        float x = startPos.x + (float)(horizontalDistance * Math.cos(angleRad));
        float y = startPos.y + (float)(horizontalDistance * Math.sin(angleRad));
        
        // faible chute (projectile rapide)
        float gravityEffect = DROP_COEFFICIENT * (t * t);
        y -= gravityEffect;
        
        return new Vec2(x, y);
    }
    
    @Override
    public float getDropCoefficient() {
        return DROP_COEFFICIENT;
    }
}

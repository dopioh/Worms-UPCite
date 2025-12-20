package fr.upc.g5e4.model.trajectories;

import org.jbox2d.common.Vec2;
import fr.upc.g5e4.model.TrajectoryInterface;

/**
 * trajectoire lourde (forte gravité)
 * utilisée par les projectiles lourds 
 */
public class HeavyTrajectory implements TrajectoryInterface {
    private static final float DROP_COEFFICIENT = 12.0f; // forte chute
    
    @Override
    public Vec2 calculatePosition(float t, Vec2 startPos, double range, double angle) {
        t = Math.max(0, Math.min(1, t));
        
        double angleRad = angle;
        float horizontalDistance = (float)(range * t);
        
        // position basée sur l'angle
        float x = startPos.x + (float)(horizontalDistance * Math.cos(angleRad));
        float y = startPos.y + (float)(horizontalDistance * Math.sin(angleRad));
        
        // forte chute parabolique
        float gravityEffect = DROP_COEFFICIENT * (t * t);
        y -= gravityEffect;
        
        return new Vec2(x, y);
    }
    
    @Override
    public float getDropCoefficient() {
        return DROP_COEFFICIENT;
    }
}

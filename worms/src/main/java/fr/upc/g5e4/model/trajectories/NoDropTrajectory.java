package fr.upc.g5e4.model.trajectories;

import org.jbox2d.common.Vec2;
import fr.upc.g5e4.model.TrajectoryInterface;

/**
 * trajectoire sans chute (ligne droite)
 * utilisée par les armes de mêlée 
 */
public class NoDropTrajectory implements TrajectoryInterface {
    private static final float DROP_COEFFICIENT = 0.0f; // pas de chute
    
    @Override
    public Vec2 calculatePosition(float t, Vec2 startPos, double range, double angle) {
        t = Math.max(0, Math.min(1, t));
        
        double angleRad = angle;
        float horizontalDistance = (float)(range * t);
        
        // position basée sur l'angle - sans gravité
        float x = startPos.x + (float)(horizontalDistance * Math.cos(angleRad));
        float y = startPos.y + (float)(horizontalDistance * Math.sin(angleRad));
        
        return new Vec2(x, y);
    }
    
    @Override
    public float getDropCoefficient() {
        return DROP_COEFFICIENT;
    }
}

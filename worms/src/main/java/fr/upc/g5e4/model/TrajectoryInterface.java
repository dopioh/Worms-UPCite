package fr.upc.g5e4.model;

import org.jbox2d.common.Vec2;

/**
 * Interface définissant comment un projectile suit sa trajectoire
 * Chaque arme peut avoir sa propre courbe balistique
 */
public interface TrajectoryCalculator {
    /**
     * Calcule la position du projectile à un temps normalisé
     * @param t Temps entre 0 et 1 (0 = départ, 1 = fin)
     * @param startPos Position de départ
     * @param range Portée du projectile
     * @param angle Angle de tir en radians
     * @return Position calculée
     */
    Vec2 calculatePosition(float t, Vec2 startPos, double range, double angle);
    
    /**
     * Obtient le coefficient de drop (gravité) de cette trajectoire
     * @return Coefficient de drop (plus élevé = plus de chute)
     */
    float getDropCoefficient();
}

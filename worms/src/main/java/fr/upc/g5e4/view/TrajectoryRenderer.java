package fr.upc.g5e4.view;

import java.awt.*;
import java.awt.geom.Line2D;
import org.jbox2d.dynamics.World;
import org.jbox2d.common.Vec2;

import fr.upc.g5e4.model.CharacterModel;

/**
 * Classe pour afficher la trajectoire de tir comme dans Angry Birds
 * @param shooter le worm qui tire
 * @param shootingAngle angle de tir en radians
 * @param range portée du tir
 * @param isAiming indique si le joueur est en train de viser
 */
public class TrajectoryRenderer {
    private CharacterModel shooter;
    private double shootingAngle;
    private double range;
    private boolean isAiming = false;
    private static final int TRAJECTORY_POINTS = 20;
    private static final Color TRAJECTORY_COLOR = new Color(255, 200, 0, 180); // Orange semi-transparent
    private static final Color TRAJECTORY_LINE_COLOR = new Color(255, 150, 0, 200); // Orange foncé
    private static final float PPM = 30f;

    public TrajectoryRenderer() {
        this.range = 30;
    }

    /**
     * définit l'angle de tir
     */
    public void setShootingAngle(double angle) {
        this.shootingAngle = angle;
    }

    /**
     * définit le worm qui tire
     */
    public void setShooter(CharacterModel shooter) {
        this.shooter = shooter;
    }

    /**
     * définit la portée
     */
    public void setRange(double range) {
        this.range = range;
    }

    /**
     * active/désactive l'affichage de la trajectoire
     */
    public void setAiming(boolean aiming) {
        this.isAiming = aiming;
    }

    /**
     * Récupère l'état de visée
     */
    public boolean isAiming() {
        return this.isAiming;
    }

    /**
     * Récupère l'angle de tir
     */
    public double getAngle() {
        return this.shootingAngle;
    }

    /**
     * Récupère la portée
     */
    public double getRange() {
        return this.range;
    }

    /**
     * dessine la trajectoire de tir
     */
    public void draw(Graphics2D g2, World world) {
        if (!isAiming || shooter == null) {
            return;
        }

        Vec2 shooterPos = shooter.getBody().getPosition();
        float startX = shooterPos.x * PPM;
        float startY = (10 - shooterPos.y) * PPM;

        // calculer les points de la trajectoire
        float[] trajectoryX = new float[TRAJECTORY_POINTS + 1];
        float[] trajectoryY = new float[TRAJECTORY_POINTS + 1];

        trajectoryX[0] = startX;
        trajectoryY[0] = startY;

        // simulation de la trajectoire avec une parabole approximée
        for (int i = 1; i <= TRAJECTORY_POINTS; i++) {
            float t = (float) i / TRAJECTORY_POINTS;
            float distance = (float) (range * t);
            
            // calculer la position avec l'angle
            float x = startX + (float) (distance * Math.cos(shootingAngle));
            float y = startY - (float) (distance * Math.sin(shootingAngle));
            
            // ajouter une petite gravité pour la parabole 
            float gravity = (float) (0.5f * 9.81 * t * t * PPM);
            y += gravity;

            trajectoryX[i] = x;
            trajectoryY[i] = y;
        }

        // dessiner la ligne de trajectoire
        g2.setColor(TRAJECTORY_LINE_COLOR);
        g2.setStroke(new BasicStroke(2.0f));
        for (int i = 0; i < TRAJECTORY_POINTS; i++) {
            g2.drawLine(
                (int) trajectoryX[i], (int) trajectoryY[i],
                (int) trajectoryX[i + 1], (int) trajectoryY[i + 1]
            );
        }

        // dessiner les points de trajectoire
        g2.setColor(TRAJECTORY_COLOR);
        int pointSize = 4;
        for (int i = 0; i <= TRAJECTORY_POINTS; i++) {
            g2.fillOval(
                (int) (trajectoryX[i] - pointSize / 2),
                (int) (trajectoryY[i] - pointSize / 2),
                pointSize,
                pointSize
            );
        }

    }

    /**
     *calcule l'angle basé sur la position du curseur
     */
    public void updateAngleFromMouse(int mouseX, int mouseY) {
        if (shooter == null) return;

        Vec2 shooterPos = shooter.getBody().getPosition();
        float shooterScreenX = shooterPos.x * PPM;
        float shooterScreenY = (10 - shooterPos.y) * PPM;

        float dx = mouseX - shooterScreenX;
        float dy = shooterScreenY - mouseY;

        this.shootingAngle = Math.atan2(dy, dx);
    }
}

package fr.upc.g5e4.view;

import java.awt.*;
import org.jbox2d.dynamics.World;
import org.jbox2d.common.Vec2;

import fr.upc.g5e4.model.CharacterModel;
import fr.upc.g5e4.model.Weapon;

/**
 * classe pour afficher la trajectoire de tir
 * système de visée 360°
 */
public class TrajectoryRenderer {
    private CharacterModel shooter;
    private Weapon currentWeapon;
    private double shootingAngle = 0;  // angle en degrés (0-360)
    private boolean isAiming = false;
    private static final int TRAJECTORY_POINTS = 50;
    private static final Color TRAJECTORY_COLOR = new Color(255, 200, 0, 180); // Orange semi-transparent
    private static final Color TRAJECTORY_LINE_COLOR = new Color(255, 150, 0, 200); // Orange foncé
    private static final float PPM = 30f;

    public TrajectoryRenderer() {
    }

    /**
     * définit l'angle de tir en degrés (0-360)
     */
    public void setShootingAngle(double angle) {
        this.shootingAngle = angle % 360;
    }

    /**
     * définit le worm qui tire
     */
    public void setShooter(CharacterModel shooter) {
        this.shooter = shooter;
    }

    /**
     * définit l'arme actuelle
     */
    public void setWeapon(Weapon weapon) {
        this.currentWeapon = weapon;
    }

    /**
     * active/désactive l'affichage de la trajectoire
     */
    public void setAiming(boolean aiming) {
        this.isAiming = aiming;
    }

    /**
     * récupère l'état de visée
     */
    public boolean isAiming() {
        return this.isAiming;
    }

    /**
     * dessine la trajectoire de tir basée sur l'angle de visée
     */
    public void draw(Graphics2D g2, World world) {
        if (!isAiming || shooter == null || currentWeapon == null || g2 == null) {
            return;
        }
        
        // obtenir la position du tireur
        Vec2 shooterPos = shooter.getBody().getPosition();
        double range = currentWeapon.getRange();

        // convertir l'angle en radians
        double angleRad = Math.toRadians(shootingAngle);

        // calculer les points de la trajectoire
        float[] trajectoryWorldX = new float[TRAJECTORY_POINTS + 1];
        float[] trajectoryWorldY = new float[TRAJECTORY_POINTS + 1];

        trajectoryWorldX[0] = shooterPos.x;
        trajectoryWorldY[0] = shooterPos.y;
        System.out.println(" weapon=" + currentWeapon.getName());

        // générer les points avec trajectoire parabolique
        for (int i = 1; i <= TRAJECTORY_POINTS; i++) {
            float t = (float) i / TRAJECTORY_POINTS;  // t entre 0 et 1
            
            //la trajectoire parcourt la distance 'range' toujours horizontalement
            float horizontalDistance = (float)(range * t);
            
            //position basée sur l'angle
            float worldX = shooterPos.x + (float)(horizontalDistance * Math.cos(angleRad));
            float worldY = shooterPos.y + (float)(horizontalDistance * Math.sin(angleRad));
            
            //ajouter un effet de chute (parabole simple)
            float gravityEffect = 9.81f * (t * t); // effet 
            worldY -= gravityEffect;
            trajectoryWorldX[i] = worldX;
            trajectoryWorldY[i] = worldY;
        }

        //convertir en coordonnées écran pour affichage
        float[] trajectoryX = new float[TRAJECTORY_POINTS + 1];
        float[] trajectoryY = new float[TRAJECTORY_POINTS + 1];
        
        for (int i = 0; i <= TRAJECTORY_POINTS; i++) {
            trajectoryX[i] = trajectoryWorldX[i] * PPM;
            trajectoryY[i] = (24 - trajectoryWorldY[i]) * PPM;  // Conversion et inversion Y
        }

        // déssiner la ligne de trajectoire
        g2.setColor(TRAJECTORY_LINE_COLOR);
        g2.setStroke(new BasicStroke(2.0f));
        for (int i = 0; i < TRAJECTORY_POINTS; i++) {
            g2.drawLine(
                (int) trajectoryX[i], (int) trajectoryY[i],
                (int) trajectoryX[i + 1], (int) trajectoryY[i + 1]
            );
        }

        //dessiner les points de trajectoire
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
}

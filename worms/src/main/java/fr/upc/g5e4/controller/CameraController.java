package fr.upc.g5e4.controller;

import fr.upc.g5e4.model.CharacterModel;
import fr.upc.g5e4.model.camera.CameraModel;


/*
 * CameraController gère la caméra dans le jeu.
 * * Elle permet de :
 * - Déplacer la caméra avec les flèches
 * - Zoomer et dézoomer sur le personnage
 * - Centrer la caméra sur le personnage
 * - Suivre automatiquement le personnage si souhaité
 *
 */

public class CameraController {

    private final CameraModel camera;
    private CharacterModel personnage;

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;

    private static final double MOVE_STEP = 20.0;

    private boolean followPlayer = false;

    public CameraController(CameraModel camera, CharacterModel personnage) {
        this.camera = camera;
        this.personnage = personnage;
    }

    // Activation & désactivation des touches
    public void activateLeft() {
        leftPressed = true;
        followPlayer = false;
    }

    public void deactivateLeft() {
        leftPressed = false;
    }

    public void activateRight() {
        rightPressed = true;
        followPlayer = false;
    }

    public void deactivateRight() {
        rightPressed = false;
    }

    public void activateUp() {
        upPressed = true;
        followPlayer = false;
    }

    public void deactivateUp() {
        upPressed = false;
    }

    public void activateDown() {
        downPressed = true;
        followPlayer = false;
    }

    public void deactivateDown() {
        downPressed = false;
    }

    // Zoom avant et arrière
    public void zoomIn() {
        camera.zoomTarget(camera.getZoomFactor() * 1.2);
        camera.centerTargetOn(personnage);
    }

    public void zoomOut() {
        camera.zoomTarget(camera.getZoomFactor() / 1.2);
        camera.centerTargetOn(personnage);
    }

    // Centre sur le personnage et active le suivi
    public void centerOnCharacter() {
        camera.centerTargetOn(personnage);
        followPlayer = true;
    }

    // Met à jour la caméra en fonction des touches enfoncées

    public void update() {
        double deltaX = 0;
        double deltaY = 0;

        if (leftPressed)
            deltaX -= MOVE_STEP;
        if (rightPressed)
            deltaX += MOVE_STEP;
        if (upPressed)
            deltaY -= MOVE_STEP;
        if (downPressed)
            deltaY += MOVE_STEP;

        // On déplace la CIBLE, pas la caméra directement
        if (deltaX != 0 || deltaY != 0) {
            camera.moveTarget(deltaX, deltaY);
        }

        /*
         * Suivi automatique si activé.
         * A l'avenir, on pourrais laisser le choix a l'utilisateur d'activer cette
         * fonction dans les options.
         */
        if (followPlayer) {
            camera.centerTargetOn(personnage);
        }
        camera.updateSmoothPosition();
    }
    
    /**
     * Change le worm cible de la caméra
     * @param newTarget le nouveau worm à suivre
     */
    public void setTargetCharacter(CharacterModel newTarget) {
        this.personnage = newTarget;
    }
}

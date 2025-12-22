package fr.upc.g5e4.controller;

import fr.upc.g5e4.model.CharacterModel;
import fr.upc.g5e4.model.camera.CameraModel;

/*
 * CameraController gère la caméra dans le jeu.
 * - Déplacement caméra
 * - Zoom
 * - Centrage et suivi du personnage
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

    // Centre immédiatement sur le personnage et active le suivi
    public void centerOnCharacter() {
        if (personnage == null) return;
        camera.centerOn(personnage);
        camera.updateSmoothPosition();
        followPlayer = true;
    }

    // Met à jour la caméra en fonction des touches enfoncées
    public void update() {
        double deltaX = 0;
        double deltaY = 0;

        if (leftPressed) deltaX -= MOVE_STEP;
        if (rightPressed) deltaX += MOVE_STEP;
        if (upPressed) deltaY -= MOVE_STEP;
        if (downPressed) deltaY += MOVE_STEP;

        if (deltaX != 0 || deltaY != 0) {
            camera.moveTarget(deltaX, deltaY);
        }

        if (followPlayer && personnage != null) {
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
        if (newTarget != null) {
            camera.centerOn(newTarget);
            camera.updateSmoothPosition();
        }
    }
    
    /**
     * Appelé après un changement de taille de vue (ex: passage en plein écran).
     * Centre immédiatement la caméra sur le personnage pour éviter le glissement.
     */
    public void onViewSizeChanged() {
        if (personnage != null) {
            camera.centerOn(personnage);
            camera.syncPositions();
        }
    }
}

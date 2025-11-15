package fr.upc.g5e4.model.camera;

import fr.upc.g5e4.model.CharacterModel;

/*
 * CameraModel représente la caméra du jeu.
 * Elle gère la position de la vue dans le monde, le zoom, 
 * le centrage sur le personnage et les limites du monde.
 *
 * Cette classe permet de :
 *  - Déplacer la caméra dans le monde
 *  - Zoomer/dézoomer sans dépasser les limites
 *  - Centrer la caméra sur un personnage
 *
 */

public class CameraModel {
    private double x;
    private double y;
    private final int viewWidth;
    private final int viewHeight;
    private final int worldWidth;
    private final int worldHeight;
    private double zoomFactor = 1.0;

    // Zoom minimum calculé automatiquement
    private final double minZoom;

    public CameraModel(int viewWidth, int viewHeight, int worldWidth, int worldHeight, double initialZoom) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;

        // Calcul du zoom minimum pour que la fenêtre ne dépasse jamais la taille de la
        // map
        double minZoomX = (double) viewWidth / worldWidth;
        double minZoomY = (double) viewHeight / worldHeight;
        this.minZoom = Math.max(minZoomX, minZoomY); // le zoom minimal nécessaire pour voir toute la map

        this.zoomFactor = Math.max(initialZoom, minZoom);
        this.x = 0;
        this.y = 0;
    }

    public void centerOn(CharacterModel personnage) {
        if (personnage == null)
            return;

        double targetX = personnage.getXcord();
        double targetY = personnage.getYcord();

        // Centrage après zoom
        this.x = targetX - getViewWidth() / (2.0 * zoomFactor);
        this.y = targetY - getViewHeight() / (2.0 * zoomFactor);

        clamp();
    }

    public void move(double deltaX, double deltaY) {
        this.x += deltaX / zoomFactor; // tenir compte du zoom
        this.y += deltaY / zoomFactor;
        clamp();
    }

    public void zoom(double factor, CharacterModel personnage) {
        if (factor <= 0 || personnage == null)
            return;

        // On limite le zoom pour ne jamais voir les bordures blanches/noires
        zoomFactor = Math.max(factor, minZoom);

        // Recentrage après zoom
        centerOn(personnage);
    }

    private void clamp() {
        double maxX = worldWidth - getViewWidth() / zoomFactor;
        double maxY = worldHeight - getViewHeight() / zoomFactor;

        if (x < 0)
            x = 0;
        if (y < 0)
            y = 0;
        if (x > maxX)
            x = maxX;
        if (y > maxY)
            y = maxY;
    }

    // Getters
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZoomFactor() {
        return zoomFactor;
    }

    public int getViewWidth() {
        return viewWidth;
    }

    public int getViewHeight() {
        return viewHeight;
    }

    public int getZoomedViewWidth() {
        return (int) (viewWidth / zoomFactor);
    }

    public int getZoomedViewHeight() {
        return (int) (viewHeight / zoomFactor);
    }

}

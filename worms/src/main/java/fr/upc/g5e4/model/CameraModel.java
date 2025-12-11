package fr.upc.g5e4.model;

import org.jbox2d.dynamics.Body;

/*
 * CameraModel représente la caméra du jeu.
 * Elle gère la position de la vue dans le monde, le zoom, 
 * le centrage sur le personnage et les limites du monde.
 */
public class CameraModel {
    private double x;
    private double y;
    private int viewWidth;
    private int viewHeight;
    private final int worldWidth;
    private final int worldHeight;
    private double zoomFactor = 1.0;

    private double minZoom;

    private double targetX;
    private double targetY;
    private double targetZoom;
    private boolean isInitialized = false;
    private final double smoothSpeed = 0.1;

    public CameraModel(int viewWidth, int viewHeight, int worldWidth, int worldHeight, double initialZoom) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;

        double minZoomX = (double) viewWidth / worldWidth;
        double minZoomY = (double) viewHeight / worldHeight;
        this.minZoom = Math.max(minZoomX, minZoomY);

        this.zoomFactor = Math.max(initialZoom, minZoom);
        this.x = 0;
        this.y = 0;
        this.targetX = this.x;
        this.targetY = this.y;
        this.targetZoom = this.zoomFactor;
        this.isInitialized = true;
    }

    /**
     * Centre la caméra sur le personnage.
     * Si le personnage a un Body JBox2D, on utilise la position physique (plus fiable).
     * Sinon on utilise getXcord/getYcord.
     */
    public void centerOn(CharacterModel personnage) {
        if (personnage == null) return;

        double px; // position en pixels X
        double py; // position en pixels Y (coordonnée Swing, 0 en haut)

        Body body = null;
        try {
            body = personnage.getBody();
        } catch (Exception ignored) {}

        if (body != null) {
            // conversion du body (mètres) en pixels
            float ppm = CharacterModel.getPPM();
            px = body.getPosition().x * ppm;
            // body y est en coordonnées JBox2D (origine bas), on convertit en pixels Swing (origine haut)
            py = worldHeight - (body.getPosition().y * ppm);
        } else {
            // fallback sur les coordonnées stockées
            px = personnage.getXcord();
            py = personnage.getYcord();
        }

        // Centrage après zoom
        this.x = px - getViewWidth() / (2.0 * zoomFactor);
        this.y = py - getViewHeight() / (2.0 * zoomFactor);

        clamp();

        // synchroniser les targets pour éviter retour en arrière
        this.targetX = this.x;
        this.targetY = this.y;
    }

    public void move(double deltaX, double deltaY) {
        this.x += deltaX / zoomFactor;
        this.y += deltaY / zoomFactor;
        clamp();
        this.targetX = this.x;
        this.targetY = this.y;
    }

    public void zoom(double factor, CharacterModel personnage) {
        if (factor <= 0 || personnage == null) return;

        zoomFactor = Math.max(factor, minZoom);
        centerOn(personnage);
        this.targetZoom = this.zoomFactor;
    }

    private void clamp() {
        double maxX = worldWidth - getViewWidth() / zoomFactor;
        double maxY = worldHeight - getViewHeight() / zoomFactor;

        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x > maxX) x = maxX;
        if (y > maxY) y = maxY;
    }

    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZoomFactor() { return zoomFactor; }
    public int getViewWidth() { return viewWidth; }
    public int getViewHeight() { return viewHeight; }
    public int getZoomedViewWidth() { return (int) (viewWidth / zoomFactor); }
    public int getZoomedViewHeight() { return (int) (viewHeight / zoomFactor); }

    public void updateSmoothPosition() {
        if (!isInitialized) return;

        double diffZoom = targetZoom - zoomFactor;
        if (Math.abs(diffZoom) > 0.001) {
            zoomFactor += diffZoom * smoothSpeed;
        } else {
            zoomFactor = targetZoom;
        }

        double diffX = targetX - x;
        double diffY = targetY - y;

        if (Math.abs(diffX) > 0.1) x += diffX * smoothSpeed;
        else x = targetX;

        if (Math.abs(diffY) > 0.1) y += diffY * smoothSpeed;
        else y = targetY;

        clamp();
    }

    public void moveTarget(double deltaX, double deltaY) {
        this.targetX += deltaX / zoomFactor;
        this.targetY += deltaY / zoomFactor;
        clampTarget();
    }

    public void centerTargetOn(CharacterModel personnage) {
        if (personnage == null) return;

        double pX;
        double pY;

        Body body = null;
        try {
            body = personnage.getBody();
        } catch (Exception ignored) {}

        if (body != null) {
            float ppm = CharacterModel.getPPM();
            pX = body.getPosition().x * ppm;
            pY = worldHeight - (body.getPosition().y * ppm);
        } else {
            pX = personnage.getXcord();
            pY = personnage.getYcord();
        }

        // utiliser targetZoom pour le calcul (zoom cible)
        this.targetX = pX - getViewWidth() / (2.0 * targetZoom);
        this.targetY = pY - getViewHeight() / (2.0 * targetZoom);

        clampTarget();
    }

    public void zoomTarget(double factor) {
        this.targetZoom = Math.max(factor, minZoom);
    }

    private void clampTarget() {
        double maxX = worldWidth - getViewWidth() / targetZoom;
        double maxY = worldHeight - getViewHeight() / targetZoom;

        if (targetX < 0) targetX = 0;
        if (targetY < 0) targetY = 0;
        if (targetX > maxX) targetX = maxX;
        if (targetY > maxY) targetY = maxY;
    }

    public void setViewSize(int newViewWidth, int newViewHeight) {
        double zoomRatio = zoomFactor / minZoom;
        double centerX = x + (viewWidth / zoomFactor) / 2.0;
        double centerY = y + (viewHeight / zoomFactor) / 2.0;

        this.viewWidth = newViewWidth;
        this.viewHeight = newViewHeight;

        double minZoomX = (double) newViewWidth / worldWidth;
        double minZoomY = (double) newViewHeight / worldHeight;
        this.minZoom = Math.max(minZoomX, minZoomY);

        this.zoomFactor = minZoom * zoomRatio;

        this.x = centerX - (viewWidth / zoomFactor) / 2.0;
        this.y = centerY - (viewHeight / zoomFactor) / 2.0;

        this.targetX = this.x;
        this.targetY = this.y;
        this.targetZoom = this.zoomFactor;

        clamp();
    }
}

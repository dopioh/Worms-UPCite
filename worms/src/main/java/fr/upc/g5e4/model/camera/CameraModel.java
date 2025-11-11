package fr.upc.g5e4.model.camera;

import fr.upc.g5e4.model.PersonnageModel;

/**
 * Gère la position et les limites de la caméra.
 * La caméra suit un personnage tout en restant dans les limites du terrain.
 */
public class CameraModel {
    private double x;
    private double y;
    private final int viewWidth;
    private final int viewHeight;
    private final int worldWidth;
    private final int worldHeight;
    private double zoomFactor = 1.0; // J'ai defini 1 comme le zoom par défaut

    /**
     * @param viewWidth   largeur visible (fenêtre)
     * @param viewHeight  hauteur visible (fenêtre)
     * @param worldWidth  largeur totale du monde (terrain)
     * @param worldHeight hauteur totale du monde
     * @param zoomFactor  facteur du zoom
     */
    public CameraModel(int viewWidth, int viewHeight, int worldWidth, int worldHeight, double initialZoom) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.x = 0;
        this.y = 0;
        this.zoomFactor = initialZoom > 0 ? initialZoom : 1.0;
    }

    /**
     * Centre la caméra sur le personnage donné.
     * 
     * @param personnage le modèle du personnage à suivre
     */
    public void centerOn(PersonnageModel personnage) {
        if (personnage == null)
            return;

        double targetX = personnage.getXcord();
        double targetY = personnage.getYcord();

        // Utiliser les tailles pour le zoom
        this.x = targetX - getViewWidth() / 2.0;
        this.y = targetY - getViewHeight() / 2.0;

        clamp();
    }

    /**
     * Empêche la caméra de sortir des limites du monde.
     */
    private void clamp() {
        int vw = getViewWidth(); // taille après zoom
        int vh = getViewHeight();

        if (x < 0)
            x = 0;
        if (y < 0)
            y = 0;
        if (x > worldWidth - vw)
            x = worldWidth - vw;
        if (y > worldHeight - vh)
            y = worldHeight - vh;
    }

    // Déplace la caméra d'un certain ecqrt delta
    public void move(double deltaX, double deltaY) {
        this.x += deltaX;
        this.y += deltaY;
        clamp();
    }

    /**
     * Zoom de la caméra.
     * facteur > 1 = zoom avant, facteur < 1 = zoom arrière
     * Contrainte sur la methode : le centrage doit rester sur le personnage
     */
    public void zoom(double factor) {
        if (factor <= 0)
            return;

        double centerX = x + getViewWidth() / 2.0;
        double centerY = y + getViewHeight() / 2.0;

        zoomFactor = factor;

        x = centerX - getViewWidth() / 2.0;
        y = centerY - getViewHeight() / 2.0;

        clamp();
    }

    // --- Getters ---
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
        return (int) (viewWidth / zoomFactor);
    }

    public int getViewHeight() {
        return (int) (viewHeight / zoomFactor);
    }

}

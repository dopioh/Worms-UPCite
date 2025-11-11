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

    /**
     * @param viewWidth largeur visible (fenêtre)
     * @param viewHeight hauteur visible (fenêtre)
     * @param worldWidth largeur totale du monde (terrain)
     * @param worldHeight hauteur totale du monde
     */
    public CameraModel(int viewWidth, int viewHeight, int worldWidth, int worldHeight) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.x = 0;
        this.y = 0;
    }

    /**
     * Centre la caméra sur le personnage donné.
     * @param personnage le modèle du personnage à suivre
     */
    public void centerOn(PersonnageModel personnage) {
        if (personnage == null) return;

        double targetX = personnage.getXcord();
        double targetY = personnage.getYcord();

        this.x = targetX - viewWidth / 2;
        this.y = targetY - viewHeight / 2;
        clamp();
    }

    /**
     * Empêche la caméra de sortir des limites du monde.
     */
    private void clamp() {
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x > worldWidth - viewWidth) x = worldWidth - viewWidth;
        if (y > worldHeight - viewHeight) y = worldHeight - viewHeight;
    }

    // --- Getters ---
    public double getX() { return x; }
    public double getY() { return y; }
    public int getViewWidth() { return viewWidth; }
    public int getViewHeight() { return viewHeight; }
}

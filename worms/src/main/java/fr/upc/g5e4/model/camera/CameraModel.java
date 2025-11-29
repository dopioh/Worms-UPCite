package fr.upc.g5e4.model.camera;

import fr.upc.g5e4.model.CharacterModel;

/*
 * CameraModel représente la caméra du jeu.
 * Elle gère la position de la vue dans le monde, le zoom, 
 * le centrage sur le personnage et les limites du monde.
 *
 * Cette classe permet de :
 * - Déplacer la caméra dans le monde
 * - Zoomer/dézoomer sans dépasser les limites
 * - Centrer la caméra sur un personnage
 *
 */

public class CameraModel {
    private double x;
    private double y;
    private int viewWidth;
    private int viewHeight;
    private final int worldWidth;
    private final int worldHeight;
    private double zoomFactor = 1.0;

    // Zoom minimum calculé automatiquement
    private double minZoom;

    // Ces variables stockent la position "voulue" par la caméra.
    // La position réelle (x, y) rejoindra ces cibles progressivement.
    private double targetX;
    private double targetY;
    private double targetZoom;
    private boolean isInitialized = false; // Pour synchroniser au démarrage
    private final double smoothSpeed = 0.1; // Facteur de vittesse (0.05 = lent, 0.5 = rapide)


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
        this.targetX = this.x;
        this.targetY = this.y;
        this.targetZoom = this.zoomFactor;
        this.isInitialized = true;
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

        // Si on utilise la vieille méthode centerOn, on force aussi les targets
        // pour éviter que la caméra ne reparte en arrière.
        this.targetX = this.x;
        this.targetY = this.y;
    }

    public void move(double deltaX, double deltaY) {
        this.x += deltaX / zoomFactor; // tenir compte du zoom
        this.y += deltaY / zoomFactor;
        clamp();
        this.targetX = this.x;
        this.targetY = this.y;
    }

    public void zoom(double factor, CharacterModel personnage) {
        if (factor <= 0 || personnage == null)
            return;

        // On limite le zoom pour ne jamais voir les bordures blanches/noires
        zoomFactor = Math.max(factor, minZoom);

        // Recentrage après zoom
        centerOn(personnage);

        // --- AJOUT SPRINT 2 : Synchronisation brutale ---
        this.targetZoom = this.zoomFactor;
        // ------------------------------------------------
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

    /*
     * Met à jour la position réelle en glissant vers la position cible.
     * !! Cette méthode doit être appelée à chaque frame dans le contrôleur !!
     */
    public void updateSmoothPosition() {
        if (!isInitialized)
            return;

        /* Glissement du zoom
         * On lisse le zoom avant de calculer les positions pour que le centrage soit
         * correct
         */
        double diffZoom = targetZoom - zoomFactor;
        if (Math.abs(diffZoom) > 0.001) {
            zoomFactor += diffZoom * smoothSpeed;
        } else {
            zoomFactor = targetZoom;
        }

        // Glissement de la Position (X et Y)
        // Formule trouvé : x += (cible - x) * vitesse
        double diffX = targetX - x;
        double diffY = targetY - y;

        // On applique le déplacement si on est pas encore arrivé (avec un petit écart)
        if (Math.abs(diffX) > 0.1)
            x += diffX * smoothSpeed;
        else
            x = targetX;

        if (Math.abs(diffY) > 0.1)
            y += diffY * smoothSpeed;
        else
            y = targetY;

        // On s'assure qu'on ne sort pas du monde (clamp utilise x/y courants)
        clamp();
    }

    // Définit la cible de déplacement (remplace move() pour le mode fluide).

    public void moveTarget(double deltaX, double deltaY) {
        // On modifie la cible, pas le x direct
        this.targetX += deltaX / zoomFactor;
        this.targetY += deltaY / zoomFactor;

        // On clamp la cible aussi pour éviter que la caméra "veuille" aller trop loin
        clampTarget();
    }

    // Définit la cible de centrage (remplace centerOn() pour le mode fluide).

    public void centerTargetOn(CharacterModel personnage) {
        if (personnage == null)
            return;

        double pX = personnage.getXcord();
        double pY = personnage.getYcord();

        // On calcule où la caméra DEVRAIT être
        this.targetX = pX - getViewWidth() / (2.0 * targetZoom);
        this.targetY = pY - getViewHeight() / (2.0 * targetZoom);

        clampTarget();
    }

    // Définit la cible de zoom
    public void zoomTarget(double factor) {
        // Limitation du zoom cible
        this.targetZoom = Math.max(factor, minZoom);
    }

    // Méthode aidante pour limiter les coordonées cibles aux bord de la map
    private void clampTarget() {
        double maxX = worldWidth - getViewWidth() / targetZoom;
        double maxY = worldHeight - getViewHeight() / targetZoom;

        if (targetX < 0)
            targetX = 0;
        if (targetY < 0)
            targetY = 0;
        if (targetX > maxX)
            targetX = maxX;
        if (targetY > maxY)
            targetY = maxY;
    }

    /**
     * Met à jour les dimensions de la vue (utilisé lors du passage en plein écran)
     * Conserve le même niveau de zoom relatif et la même position de caméra
     */
    public void setViewSize(int newViewWidth, int newViewHeight) {
        // Sauvegarder le ratio de zoom par rapport au minimum actuel
        double zoomRatio = zoomFactor / minZoom;
        
        // Sauvegarder le centre de la vue actuelle
        double centerX = x + (viewWidth / zoomFactor) / 2.0;
        double centerY = y + (viewHeight / zoomFactor) / 2.0;
        
        this.viewWidth = newViewWidth;
        this.viewHeight = newViewHeight;
        
        // Recalcul du zoom minimum
        double minZoomX = (double) newViewWidth / worldWidth;
        double minZoomY = (double) newViewHeight / worldHeight;
        this.minZoom = Math.max(minZoomX, minZoomY);
        
        // Appliquer le même ratio de zoom
        this.zoomFactor = minZoom * zoomRatio;
        
        // Recentrer la caméra sur le même point
        this.x = centerX - (viewWidth / zoomFactor) / 2.0;
        this.y = centerY - (viewHeight / zoomFactor) / 2.0;
        
        // Synchroniser les targets
        this.targetX = this.x;
        this.targetY = this.y;
        this.targetZoom = this.zoomFactor;
        
        clamp();
    }
}

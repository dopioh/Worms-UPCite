package fr.upc.g5e4.model;

/**
 * Classe utilitaire pour stocker les paramètres du jeu
 */
public class SkinSettings {
    
    /*
    Ici il faut faire un tableau de skin avec leur chemin et leur valeur bool lock 
    -> faire une classe qui relie un skin a sa valeur lock ? 
     */
    
    // Skin par défaut (modifier pour changer le skin du jeu)
    private static String currentSkin = "/wormNoel.png"; // "/worm.png" pour la version sans pixel
    
    // Méthode pour récupérer le skin actuel
    public static String getCurrentSkin() {
        return currentSkin;
    }
    
    // Méthode pour définir le skin actuel
    public static void setCurrentSkin(String skin) {
        currentSkin = skin;
    }
}

package fr.upc.g5e4.model;

/**
 * Classe utilitaire pour stocker les paramètres du jeu
 */
public class SettingsModel {
    
    // Skin par défaut (modifier pour changer le skin du jeu)
    private static String currentSkin = "/worm.png";
    
    // Méthode pour récupérer le skin actuel
    public static String getCurrentSkin() {
        return currentSkin;
    }
    
    // Méthode pour définir le skin actuel
    public static void setCurrentSkin(String skin) {
        currentSkin = skin;
    }
}

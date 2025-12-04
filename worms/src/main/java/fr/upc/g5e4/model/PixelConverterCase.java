package fr.upc.g5e4.model;

import fr.upc.g5e4.view.ConsoleView;

/**
 * Toute methode ici est statique
 * Convertit tout type d'entree (en pixel) en Case pour l'adapter a la vue terminal
 * Swing fonctionne en pixel
 * Lanterna fonctionne en Case
 */
public class PixelConverterCase {

    public static final int XPixel = ConsoleGameManager.pixelX;
    public static final int YPixel = ConsoleGameManager.pixelY; 

    public static final int XCase = ConsoleView.getXCase();
    public static final int YCase = ConsoleView.getYCase(); 

    //public static final int RATIO = 10;
    
    public static int transformCoordX(int pixel){
        return (pixel * XCase)/XPixel;
    }

    public static int transformCoordY(int pixel){
        return (pixel * YCase)/YPixel;
    }
}

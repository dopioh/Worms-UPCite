package fr.upc.g5e4;

//import org.jbox2d.dynamics.*;

public class GroundModel{
    private final int[] groundY;
    private final int width;
    private final int height;
    private final int waterLevel;

    public GroundModel(int[] groundY, int width, int height, int waterLevel) {
        this.groundY = groundY;
        this.width = width;
        this.height = height;
        this.waterLevel = waterLevel;
        System.out.println(this.width);
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getWaterLevel() { return waterLevel; }

    public int[] getGroundY() { return groundY; }


public int getGroundHeightAt(double pixel) {
    
    // nombre de points du terrain
    int n = groundY.length;
    
    // calcul de l'indice le plus proche
    int i = (int) (pixel * (n - 1) / width);
    
    // limite pour ne pas dépasser le tableau
    if (i < 0) i = 0;
    if (i >= n) i = n - 1;
    
    return groundY[i];
}

}

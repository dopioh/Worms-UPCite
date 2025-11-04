package fr.upc.g5e4.model;

/**
 * Initialise un terrain (Ground)
 * Seulement ses coordonnées
 * Utiliser pour creer son Body et son image
 * 
 * @see PhysicsWorld#createGroundFromModel 
 * @see GamePanel#paintComponent
 */
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


    /**
     * @param pixel : X donné 
     * 
     * Pour recuperer Y a un X donné
     */
    public int getGroundHeightAt(double pixel) {
    
        int n = groundY.length;
        int i = (int) (pixel * (n - 1) / width);
    
        if (i < 0){i = 0;} 
        if (i >= n) {i = n - 1;}
    
        return groundY[i];
    }

}

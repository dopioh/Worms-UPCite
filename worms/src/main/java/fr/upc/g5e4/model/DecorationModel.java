package fr.upc.g5e4.model;

/**
 * Décoration placée sur le terrain.
 */
public class DecorationModel {
    
    /** Types de décorations disponibles */
    public enum DecoType {
        // BENCH("Deco/Bench.png", 80, 30, true),
        // PLANT("Deco/Plant.png", 40, 35, true),
        // TREE1("Deco/Tree1.png", 70, 100, true),
        TREE2("Deco/Tree2.png", 70, 100, true);
        
        private final String imagePath;
        private final int width;
        private final int height;
        private final boolean needsFlatSurface;
        
        DecoType(String imagePath, int width, int height, boolean needsFlatSurface) {
            this.imagePath = imagePath;
            this.width = width;
            this.height = height;
            this.needsFlatSurface = needsFlatSurface;
        }
        
        public String getImagePath() { return imagePath; }
        public int getWidth() { return width; }
        public int getHeight() { return height; }
        public boolean needsFlatSurface() { return needsFlatSurface; }
    }
    
    private final DecoType type;
    private final int pixelX;
    private final int pixelY;
    private boolean visible;
    
    /**
     * Crée une nouvelle décoration.
     * @param type le type de décoration
     * @param pixelX position X en pixels
     * @param pixelY position Y en pixels (bas de la déco)
     * 
     * @author Ian
     */
    public DecorationModel(DecoType type, int pixelX, int pixelY) {
        this.type = type;
        this.pixelX = pixelX;
        this.pixelY = pixelY;
        this.visible = true;
    }
    
    public DecoType getType() { return type; }
    public int getPixelX() { return pixelX; }
    public int getPixelY() { return pixelY; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    
    /** @return position X pour le rendu (coin supérieur gauche) */
    public int getRenderX() {
        return pixelX - type.getWidth() / 2;
    }
    
    /** @return position Y pour le rendu (coin supérieur gauche) */
    public int getRenderY() {
        return pixelY - type.getHeight();
    }
}

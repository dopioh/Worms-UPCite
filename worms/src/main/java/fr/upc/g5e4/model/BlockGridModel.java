package fr.upc.g5e4.model;

/**
 * Modèle de terrain en grille de blocs
 * Gère une grille 2D de blocs avec pour possibilité de modification/destruction
 */
public class BlockGridModel {
    
    public static final int BLOCK_SIZE = 12;  // Taille d'un bloc en pixels
    
    private final BlockType[][] grid;         // la grille de blocs (colonnes, lignes)
    private final int gridWidth;              // nombre de colonnes
    private final int gridHeight;             // nombre de lignes
    private final int pixelWidth;             // largeur totale en pixels
    private final int pixelHeight;            // hauteur totale en pixels
    private final int waterLevel;             // niveau de l'eau en pixels
    
    private boolean dirty = true;             // indique si la grille a été modifiée (pour recalculer la physique)
    
    /**
     * Constructeur de la classe BlockGridModel
     * Crée une grille de blocs vide
     * @param pixelWidth largeur du terrain en pixels
     * @param pixelHeight hauteur du terrain en pixels
     * @param waterLevel niveau de l'eau en pixels
     */
    public BlockGridModel(int pixelWidth, int pixelHeight, int waterLevel) {
        this.pixelWidth = pixelWidth;
        this.pixelHeight = pixelHeight;
        this.waterLevel = waterLevel;
        this.gridWidth = (pixelWidth + BLOCK_SIZE - 1) / BLOCK_SIZE;
        this.gridHeight = (pixelHeight + BLOCK_SIZE - 1) / BLOCK_SIZE;
        // on crée la grille de blocs
        this.grid = new BlockType[gridWidth][gridHeight];
        
        // on initialise la grille avec de l'air partout
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                grid[x][y] = BlockType.AIR;
            }
        }
    }
    
    /**
     * On modifie la grille (remplie d'air) pour créer un terrain avec les hauteurs de groundY
     * @param groundY tableau des hauteurs Y pour chaque point
     */
    public void generateWithGroundY(int[] groundY) {
        // nombre de points dans groundY
        int points = groundY.length;
        
        for (int gridX = 0; gridX < gridWidth; gridX++) {
            // on calcule la position X en pixels pour ce bloc
            int pixelX = gridX * BLOCK_SIZE + BLOCK_SIZE / 2;
            
            // on interpole la hauteur du terrain à cette position X
            double ratio = (double) pixelX / pixelWidth * (points - 1);
            int index = (int) ratio;
            if (index >= points - 1) index = points - 2;
            
            double fraction = ratio - index;
            int surfaceY = (int) (groundY[index] * (1 - fraction) + groundY[index + 1] * fraction);
            
            // on convertit la position Y en index de grille
            int surfaceGridY = surfaceY / BLOCK_SIZE;
            int waterGridY = waterLevel / BLOCK_SIZE;
            
            // on remplit les blocs
            for (int gridY = 0; gridY < gridHeight; gridY++) {
                if (gridY < surfaceGridY) {
                    // au-dessus de la surface
                    if (gridY >= waterGridY) {
                        // sous le niveau d'eau = WATER
                        grid[gridX][gridY] = BlockType.WATER;
                    } else {
                        // au-dessus du niveau d'eau = AIR
                        grid[gridX][gridY] = BlockType.AIR;
                    }
                } else if (gridY == surfaceGridY) {
                    // surface = GRASS
                    grid[gridX][gridY] = BlockType.GRASS;
                } else if (gridY < surfaceGridY + 30) {
                    // sous la surface = DIRT
                    grid[gridX][gridY] = BlockType.DIRT;
                } else {
                    // profondeur = STONE
                    grid[gridX][gridY] = BlockType.STONE;
                }
            }
        }
        
        dirty = true;
    }
    
    /**
     * Récuperer le type de bloc à une position de grille (si hors limites, on retourne AIR)
     * @param gridX colonne
     * @param gridY ligne
     * @return le type de bloc (BlockType)
     */
    public BlockType getBlock(int gridX, int gridY) {
        if (gridX < 0 || gridX >= gridWidth || gridY < 0 || gridY >= gridHeight) {
            return BlockType.AIR; // si on est hors limite alors on retourne le type AIR (vide)
        }
        return grid[gridX][gridY];
    }
    
    /**
     * Définit le type de bloc à une position de grille
     * @param gridX colonne
     * @param gridY ligne
     * @param type nouveau type de bloc
     * @return true si le bloc a été modifié, false si hors limites
     */
    public boolean setBlock(int gridX, int gridY, BlockType type) {
        if (gridX < 0 || gridX >= gridWidth || gridY < 0 || gridY >= gridHeight) { 
            return false; // si on est hors limite alors on retourne false
        }
        if (grid[gridX][gridY] != type) { 
            grid[gridX][gridY] = type; // si le type du bloc != le type parametre on le modifie
            dirty = true; // on marque la grille comme dirty pour recalculer la physique
        }
        return true;
    }
    
    /**
     * détruire un bloc (le remplace par AIR)
     * @param gridX colonne
     * @param gridY ligne
     * @return true si un bloc a été détruit
     */
    public boolean destroyBlock(int gridX, int gridY) {
        // on check si le bloc est bien solide
        if (getBlock(gridX, gridY).isSolid()) {
            return setBlock(gridX, gridY, BlockType.AIR);
        }
        return false;
    }
    
    /**
     * Convertit une position X pixel en position de grille
     * @param pixelX position X en pixels
     * @return index de colonne
     */
    public int pixelToGridX(int pixelX) {
        return pixelX / BLOCK_SIZE;
    }
    
    /**
     * Convertit une position Y pixel en position de grille
     * @param pixelY position Y en pixels
     * @return index de ligne
     */
    public int pixelToGridY(int pixelY) {
        return pixelY / BLOCK_SIZE;
    }
    
    /**
     * Convertit une position X de grille en pixel
     * @param gridX index de colonne
     * @return position X en pixels
     */
    public int gridToPixelX(int gridX) {
        return gridX * BLOCK_SIZE;
    }
    
    /**
     * Convertit une position Y de grille en pixel
     * @param gridY index de ligne
     * @return position Y en pixels
     */
    public int gridToPixelY(int gridY) {
        return gridY * BLOCK_SIZE;
    }
    
    /**
     * Vérifie si un bloc à une position pixel est solide (car un bloc correspond a plusieurs pixels)
     * @param pixelX position X en pixels
     * @param pixelY position Y en pixels
     * @return true si le bloc est solide
     */
    public boolean isSolidAt(int pixelX, int pixelY) {
        return getBlock(pixelToGridX(pixelX), pixelToGridY(pixelY)).isSolid();
    }
    
    /**
     * Obtient la hauteur du terrain à une position X (hauteur en pixels)
     * Retourne la position Y du premier bloc solide depuis le haut
     * @param pixelX position X en pixels
     * @return position Y en pixels du sommet du terrain
     */
    public int getGroundHeightAt(int pixelX) {
        int gridX = pixelToGridX(pixelX);
        for (int gridY = 0; gridY < gridHeight; gridY++) {
            if (getBlock(gridX, gridY).isSolid()) {
                return gridY * BLOCK_SIZE;
            }
        }
        return pixelHeight;  // aucun bloc solide trouvé on renvoie la hauteur totale du terrain
    }
    
    // Getters utiles
    public int getGridWidth() { return gridWidth; }
    public int getGridHeight() { return gridHeight; }
    public int getPixelWidth() { return pixelWidth; }
    public int getPixelHeight() { return pixelHeight; }
    public int getWaterLevel() { return waterLevel; }
    public int getBlockSize() { return BLOCK_SIZE; }

    /**
     * Réinitialise le flag dirty (après reconstruction de la physique)
     */
    public void clearDirty() { dirty = false; }

    public boolean isDirty() { return dirty; }
    
    /**
     * retourne la largeur totale du terrain en pixels
     * @return largeur en pixels
     */
    public int getWidth() { return pixelWidth; }
    
    /**
     * retourne la hauteur totale du terrain en pixels
     * @return hauteur en pixels
     */
    public int getHeight() { return pixelHeight; }
}


package fr.upc.g5e4.model;

/**
 * permet de gérer les PV de chaque bloc du terrain
 */
public class BlockHealthGridModel {
    private final int[][] health;  // grille de santé parallèle à la grille de blocs
    private final int gridWidth;
    private final int gridHeight;
    
    /**
     * constructeur
     * @param gridWidth largeur de la grille en nombre de blocs
     * @param gridHeight hauteur de la grille en nombre de blocs
     */
    public BlockHealthGridModel(int gridWidth, int gridHeight) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.health = new int[gridWidth][gridHeight];
    }
    
    /**
     * initialise les PV en fonction de leur type
     * @param blockGrid la grille de blocs
     */
    public void initializeHealth(BlockType[][] blockGrid) {
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                if (blockGrid[x][y] != null) {
                    health[x][y] = blockGrid[x][y].getMaxHealth(); // PV max du type de bloc
                } else {
                    health[x][y] = 0; // pas de bloc = 0 PV
                }
            }
        }
    }
    
    /**
     * permet d'obtenir la santé actuelle d'un bloc
     * @param gridX colonne
     * @param gridY ligne
     * @return PV du bloc (0 = détruit, valeur négative à 0)
     */
    public int getHealth(int gridX, int gridY) {
        if (gridX < 0 || gridX >= gridWidth || gridY < 0 || gridY >= gridHeight) {
            return 0;
        }
        return Math.max(0, health[gridX][gridY]);  // encadrer à 0 minimum
    }
    
    /**
     * définit la santé d'un bloc
     * @param gridX colonne
     * @param gridY ligne
     * @param newHealth nouvelle santé
     */
    public void setHealth(int gridX, int gridY, int newHealth) {
        if (gridX >= 0 && gridX < gridWidth && gridY >= 0 && gridY < gridHeight) {
            health[gridX][gridY] = Math.max(0, newHealth); // encadrer à 0 minimum
        }
    }
    
    /**
     * applique des dégats à un bloc
     * @param gridX colonne
     * @param gridY ligne
     * @param damage dégats à appliquer
     * @return true si le bloc est maintenant détruit 
     */
    public boolean takeDamage(int gridX, int gridY, int damage) {
        if (gridX >= 0 && gridX < gridWidth && gridY >= 0 && gridY < gridHeight) {
            health[gridX][gridY] -= damage;
            return health[gridX][gridY] <= 0; // true si détruit
        }
        return false;
    }
    
    /**
     * obtenir le pourcentage de PV du bloc (0.0 = mort, 1.0 = pleine santé)
     * @param gridX colonne
     * @param gridY ligne
     * @param maxHealth santé max du bloc
     * @return pourcentage de santé
     */
    public double getHealthPercent(int gridX, int gridY, int maxHealth) {
        if (maxHealth == 0) return 0.0;
        int currentHealth = getHealth(gridX, gridY);
        return Math.max(0.0, Math.min(1.0, (double) currentHealth / maxHealth)); // encadrer entre 0.0 et 1.0
    }
    
    /**
     * réinitialise la santé d'un bloc à sa valeur max
     * @param gridX colonne
     * @param gridY ligne
     * @param maxHealth PV max du bloc
     */
    public void resetHealth(int gridX, int gridY, int maxHealth) {
        setHealth(gridX, gridY, maxHealth);
    }
    
    /**
     * Getter de la largeur de la grille
     */
    public int getGridWidth() {
        return gridWidth;
    }
    
    /**
     * Getter de la hauteur de la grille
     */
    public int getGridHeight() {
        return gridHeight;
    }
}

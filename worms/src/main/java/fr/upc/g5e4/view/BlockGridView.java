package fr.upc.g5e4.view;

import fr.upc.g5e4.model.BlockGridModel;
import fr.upc.g5e4.model.BlockType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fr.upc.g5e4.util.ImageCache;

/**
 * Vue de terrain en grille de blocs
 */
public class BlockGridView {
    
    private final Map<BlockType, BufferedImage> textures;
    private BufferedImage groundTexture;  // Texture générale (fallback)
    private boolean useTextures = false;
    
    // Couleur pré-créée pour les bordures
    private static final Color BORDER_COLOR = new Color(0, 0, 0, 30);
    private static final Color WATER_COLOR = new Color(75, 147, 240, 150);
    
    /**
     * Constructeur pour la vue de terrain en grille de blocs
     */
    public BlockGridView() {
        this.textures = new HashMap<>();
        //loadBlockTexture(BlockType.GRASS, "/GIGAWORMM.png");
    }
    
    /**
     * Charger une texture pour un type de bloc spécifique
     * @param type le type de bloc
     * @param path le chemin de la texture
     */
    private void loadBlockTexture(BlockType type, String path) {
        BufferedImage img = ImageCache.get(path);
        if (img != null) {
            textures.put(type, img);
        } else {
            System.err.println("BlockGridView: texture " + path + " non trouvée pour " + type);
        }
    }
    /**exemple pour charger une texture pour un type de bloc précis:
     * loadBlockTexture(BlockType.GRASS, "/grass.png");
     * loadBlockTexture(BlockType.DIRT, "/dirt.png");
     * loadBlockTexture(BlockType.STONE, "/stone.png");
     * loadBlockTexture(BlockType.WATER, "/water.png");
     * loadBlockTexture(BlockType.AIR, "/air.png");
    */
    
    /**
     * Dessiner la grille de blocs
     * @param g2d le contexte graphique
     * @param model le modèle de grille à dessiner
     * @param cameraX position X de la caméra
     * @param cameraY position Y de la caméra
     * @param viewWidth largeur visible en pixels
     * @param viewHeight hauteur visible en pixels
     * @param zoom facteur de zoom
     */
    
    public void drawAll(Graphics2D g2d, BlockGridModel model) {
        int blockSize = model.getBlockSize();
        
        for (int gridX = 0; gridX < model.getGridWidth(); gridX++) {
            for (int gridY = 0; gridY < model.getGridHeight(); gridY++) {
                BlockType block = model.getBlock(gridX, gridY);
                
                if (block == BlockType.AIR) {
                    continue; // on ne dessine pas les blocs AIR (vide)
                }

                // on calcule combien de pixels (x,y) on doit dessiner pour le bloc
                int pixelX = gridX * blockSize; 
                int pixelY = gridY * blockSize;
                
                // obtenir la santé actuelle et max du bloc
                int currentHealth = model.getHealthGrid().getHealth(gridX, gridY);
                int maxHealth = block.getMaxHealth();
                double healthPercent = model.getHealthGrid().getHealthPercent(gridX, gridY, maxHealth);
                
                // on peut alors dessiner le bloc
                drawBlock(g2d, block, pixelX, pixelY, blockSize, healthPercent, currentHealth, maxHealth);
            }
        }
    }
    
    /**
     * Dessiner un bloc individuel
     * @param g2d le contexte graphique
     * @param block le type de bloc
     * @param x position X en pixels
     * @param y position Y en pixels
     * @param size taille du bloc en pixels
     * @param healthPercent pourcentage de santé (0.0 = mort, 1.0 = pleine santé)
     * @param currentHealth santé actuelle du bloc
     * @param maxHealth santé max du bloc
     */
    private void drawBlock(Graphics2D g2d, BlockType block, int x, int y, int size, double healthPercent, int currentHealth, int maxHealth) {
        // on vérifie si on a une texture spécifique pour ce type (grace a Map)
        BufferedImage texture = textures.get(block);
        
        if (texture != null) { // si on a une texture spécifique on la dessine
            g2d.drawImage(texture, x, y, size, size, null);
        } else {
            // Utiliser la couleur par défaut, modifiée selon la santé
            Color blockColor = block.getColor();
            Color colorToUse = adjustColorByHealth(blockColor, healthPercent, currentHealth);
            g2d.setColor(colorToUse);
            g2d.fillRect(x, y, size, size);
        }
        
        // option : dessiner une bordure pour distinguer les blocs solides
        if (block.isSolid()) {
            g2d.setColor(BORDER_COLOR);
            g2d.drawRect(x, y, size - 1, size - 1);
        }
    }
    
    /**
     * ajuste la couleur d'un bloc selon son état de santé
     * les blocs ayant subit des dégats deviennent plus clairs
     * @param originalColor couleur originale du bloc
     * @param healthPercent pourcentage de santé (0.0 à 1.0)
     * @param currentHealth santé actuelle du bloc
     * @return la couleur ajustée
     */
    private Color adjustColorByHealth(Color originalColor, double healthPercent, int currentHealth) {
        if (healthPercent >= 1.0) {
            return originalColor;  // pleine santé = couleur originale
        }
        
        // éclaircie progressivement vers une couleur grise pâle quand endommagé
        // plus la santé diminue, plus ça devient clair/pâle
        int r = (int) (originalColor.getRed() * healthPercent + 200 * (1 - healthPercent));
        int g = (int) (originalColor.getGreen() * healthPercent + 200 * (1 - healthPercent));
        int b = (int) (originalColor.getBlue() * healthPercent + 200 * (1 - healthPercent));
        
        return new Color(
            Math.min(255, r),
            Math.min(255, g),
            Math.min(255, b),
            originalColor.getAlpha()
        );
    }
    
    /**
     * Dessiner l'eau
     * @param g2d le contexte graphique
     * @param model le modèle de grille
     * @param waterLevel le niveau de l'eau en pixels
     * @param width la largeur du terrain en pixels
     * @param height la hauteur du terrain en pixels
     */
    public void drawWater(Graphics2D g2d, BlockGridModel model) {
        int waterLevel = model.getWaterLevel();
        int width = model.getPixelWidth();
        int height = model.getPixelHeight();

        if (waterLevel < height) {
            g2d.setColor(WATER_COLOR);
            g2d.fillRect(0, waterLevel, width, height - waterLevel); // on remplit l'eau en faisant un gros rectangle
        }
    }
}


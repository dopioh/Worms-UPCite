package fr.upc.g5e4.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Gère la génération et la mise à jour des décorations sur le terrain
 * Les décorations sont placées aléatoirement sur les surfaces plates.
 * @author Ian
 */
public class DecorationManager {
    
    private static final int MIN_SPACING = 150;
    private static final int FLAT_TOLERANCE = 1;
    
    private final List<DecorationModel> decorations;
    private final BlockGridModel blockGrid;
    private final Random random;
    
    public DecorationManager(BlockGridModel blockGrid) {
        this.blockGrid = blockGrid;
        this.decorations = new ArrayList<>();
        this.random = new Random();
    }
    
    /**
     * Génère des décorations aléatoires sur le terrain.
     * @param count nombre approximatif de décorations à placer
     */
    public void generateDecorations(int count) {
        decorations.clear();
        int placed = 0; //combien de décos réussi à poser
        int attempts = 0; //nombre d'essais faits
        int maxAttempts = count * 10; //limite pour éviter boucle infinie
        
        while (placed < count && attempts < maxAttempts) {
            attempts++;
            
            // Position X aléatoire
            int pixelX = random.nextInt(blockGrid.getPixelWidth() - 100) + 50;
            
            // Trouver la surface à cette position
            int surfaceY = blockGrid.getGroundHeightAt(pixelX);
            
            // Ignorer si sous l'eau ou trop haut
            if (surfaceY >= blockGrid.getWaterLevel() - 20 || surfaceY < 50) {
                continue;
            }
            
            // Vérifier l'espacement avec les autres décos
            if (!hasEnoughSpacing(pixelX)) {
                continue;
            }
            
            // Choisir un type de déco aléatoire
            DecorationModel.DecoType type = pickRandomType();
            
            // Vérifier si la surface est assez plate pour ce type
            if (type.needsFlatSurface() && !isSurfaceFlat(pixelX, type.getWidth())) {
                // Surface pas assez plate, passer à l'essai suivant
                continue;
            }
            
            decorations.add(new DecorationModel(type, pixelX, surfaceY));
            placed++;
        }
    }
    
    /**
     * Vérifie si la surface est assez plate pour placer une déco
     * @param centerX centre X de la déco
     * @param width largeur de la déco
     * @return true si la surface est plate
     */
    private boolean isSurfaceFlat(int centerX, int width) {
        int halfWidth = width / 2;
        int startX = Math.max(0, centerX - halfWidth);
        int endX = Math.min(blockGrid.getPixelWidth() - 1, centerX + halfWidth);
        
        int centerY = blockGrid.getGroundHeightAt(centerX);
        
        for (int x = startX; x <= endX; x += 8) {
            int y = blockGrid.getGroundHeightAt(x);
            if (Math.abs(y - centerY) > FLAT_TOLERANCE * BlockGridModel.BLOCK_SIZE) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Vérifie l'espacement avec les décorations déjà placées
     * @param pixelX position X de la nouvelle déco
     * @return true si l'espacement est suffisant
     */
    private boolean hasEnoughSpacing(int pixelX) {
        for (DecorationModel deco : decorations) {
            if (Math.abs(deco.getPixelX() - pixelX) < MIN_SPACING) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Choisit un type de déco aléatoire
     */
    private DecorationModel.DecoType pickRandomType() {
        DecorationModel.DecoType[] types = DecorationModel.DecoType.values();
        return types[random.nextInt(types.length)];
    }
    

    
    /**
     * Une déco disparaît si le sol en dessous est détruit
     */
    public void updateVisibility() {
        for (DecorationModel deco : decorations) {
            if (!deco.isVisible()) continue;
            
            // Vérifier si le bloc juste sous la déco existe encore
            int gridX = blockGrid.pixelToGridX(deco.getPixelX());
            int gridY = blockGrid.pixelToGridY(deco.getPixelY());
            
            if (!blockGrid.getBlock(gridX, gridY).isSolid()) {
                deco.setVisible(false);
            }
        }
    }
    
    /**
     * @return liste des décorations visibles
     */
    public List<DecorationModel> getVisibleDecorations() {
        List<DecorationModel> visible = new ArrayList<>();
        for (DecorationModel deco : decorations) {
            if (deco.isVisible()) {
                visible.add(deco);
            }
        }
        return visible;
    }
    
    public List<DecorationModel> getAllDecorations() {
        return decorations;
    }
}

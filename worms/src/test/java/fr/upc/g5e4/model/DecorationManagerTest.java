package fr.upc.g5e4.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour le système de décorations
 */
public class DecorationManagerTest {

    private BlockGridModel blockGrid;
    private DecorationManager decorationManager;
    
    private static final int TEST_WIDTH = 1200;
    private static final int TEST_HEIGHT = 600;
    private static final int TEST_WATER_LEVEL = 500;

    @BeforeEach
    public void setUp() {
        // Créer un terrain de test
        blockGrid = new BlockGridModel(TEST_WIDTH, TEST_HEIGHT, TEST_WATER_LEVEL);
        
        // Créer un terrain simple avec une surface plate
        int[] groundY = new int[20];
        for (int i = 0; i < groundY.length; i++) {
            groundY[i] = 300; // surface plate à Y=300
        }
        blockGrid.generateWithGroundY(groundY);
        
        decorationManager = new DecorationManager(blockGrid);
    }

    @Test
    public void testGenerateDecorations() {
        // Générer 10 deco
        decorationManager.generateDecorations(10);
        
        List<DecorationModel> decos = decorationManager.getAllDecorations();
        
        // On devrait avoir des décorations (peut etre pas exactement 10 si pas assez de place)
        assertFalse(decos.isEmpty(), "Devrait avoir généré au moins quelques décorations");
        assertTrue(decos.size() <= 10, "Ne devrait pas dépasser le nombre demandé");
    }

    @Test
    public void testAllDecorationsVisible() {
        decorationManager.generateDecorations(5);
        
        List<DecorationModel> allDecos = decorationManager.getAllDecorations();
        List<DecorationModel> visibleDecos = decorationManager.getVisibleDecorations();
        
        // Au début toutes les décos doivent être visibles
        assertEquals(allDecos.size(), visibleDecos.size(), 
            "Toutes les décorations devraient être visibles au début");
        
        for (DecorationModel deco : allDecos) {
            assertTrue(deco.isVisible(), "Chaque déco devrait être visible au début");
        }
    }

    @Test
    public void testDecorationDisappearsWhenGroundDestroyed() {
        decorationManager.generateDecorations(3);
        
        List<DecorationModel> decos = decorationManager.getAllDecorations();
        assertFalse(decos.isEmpty(), "Devrait avoir des décorations");
        
        // Prendre la première deco
        DecorationModel deco = decos.get(0);
        int gridX = blockGrid.pixelToGridX(deco.getPixelX());
        int gridY = blockGrid.pixelToGridY(deco.getPixelY());
        
        // Détruire le sol sous la deco (3 blocs)
        for (int dy = 0; dy <= 2; dy++) {
            blockGrid.setBlock(gridX, gridY + dy, BlockType.AIR);
        }
        
        // Mettre à jour la visibilité
        decorationManager.updateVisibility();
        
        // La déco devrait être invisible maintenant
        assertFalse(deco.isVisible(), "La déco devrait disparaître quand le sol est détruit");
    }

    @Test
    public void testDecorationStaysVisibleWithGround() {
        decorationManager.generateDecorations(3);
        
        List<DecorationModel> decos = decorationManager.getAllDecorations();
        assertFalse(decos.isEmpty(), "Devrait avoir des décorations");
        
        DecorationModel deco = decos.get(0);
        
        // Mettre à jour sans détruire le sol
        decorationManager.updateVisibility();
        
        // La déco devrait rester visible
        assertTrue(deco.isVisible(), "La déco devrait rester visible si le sol existe");
    }

    @Test
    public void testDecorationsHaveCorrectBounds() {
        decorationManager.generateDecorations(5);
        
        List<DecorationModel> decos = decorationManager.getAllDecorations();
        
        for (DecorationModel deco : decos) {
            // Vérifier que les décos sont dans les limites du terrain
            assertTrue(deco.getPixelX() >= 0, "X devrait être >= 0");
            assertTrue(deco.getPixelX() < TEST_WIDTH, "X devrait être < largeur");
            assertTrue(deco.getPixelY() >= 0, "Y devrait être >= 0");
            assertTrue(deco.getPixelY() < TEST_HEIGHT, "Y devrait être < hauteur");
            
            // Les décos ne devraient pas être sous l'eau
            assertTrue(deco.getPixelY() < TEST_WATER_LEVEL, 
                "Les décos ne devraient pas être sous l'eau");
        }
    }

    @Test
    public void testDecorationTypes() {
        decorationManager.generateDecorations(10);
        
        List<DecorationModel> decos = decorationManager.getAllDecorations();
        
        for (DecorationModel deco : decos) {
            assertNotNull(deco.getType(), "Chaque déco devrait avoir un type");
            
            // Vérifier que le type a des dimensions valides
            assertTrue(deco.getType().getWidth() > 0, "Largeur doit être > 0");
            assertTrue(deco.getType().getHeight() > 0, "Hauteur doit être > 0");
            assertNotNull(deco.getType().getImagePath(), "Chemin d'image doit exister");
        }
    }

    @Test
    public void testClearDecorations() {
        decorationManager.generateDecorations(5);
        assertFalse(decorationManager.getAllDecorations().isEmpty(), 
            "Devrait avoir des décorations");
        
        // Regénérer (ce qui clear d'abord)
        decorationManager.generateDecorations(3);
        
        List<DecorationModel> decos = decorationManager.getAllDecorations();
        // Devrait avoir au max 3 nouvelles décos, pas 8 (5+3)
        assertTrue(decos.size() <= 3, "Clear devrait avoir été fait avant la nouvelle génération");
    }

    @Test
    public void testDecorationWithSlopedTerrain() {
        // Créer un terrain en pente
        int[] slopedGround = new int[20];
        for (int i = 0; i < slopedGround.length; i++) {
            slopedGround[i] = 200 + i * 20; // pente progressive
        }
        blockGrid.generateWithGroundY(slopedGround);
        
        DecorationManager slopedManager = new DecorationManager(blockGrid);
        slopedManager.generateDecorations(10);
        
        List<DecorationModel> decos = slopedManager.getAllDecorations();
        
        // Sur un terrain en pente, on devrait quand même avoir des lights
        // car elles n'ont pas besoin de surface plate
        assertTrue(decos.size() > 0, "Devrait quand même placer des lights sur terrain pentu");
    }
}

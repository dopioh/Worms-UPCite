package fr.upc.g5e4.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
            groundY[i] = 300;
        }
        blockGrid.generateWithGroundY(groundY);
        
        decorationManager = new DecorationManager(blockGrid);
    }

    @Test
    public void testGenerateDecorations() {
        // Générer 10 deco
        decorationManager.generateDecorations(10);
        
        List<DecorationModel> decos = decorationManager.getAllDecorations();
        
        assertFalse(decos.isEmpty());
        assertTrue(decos.size() <= 10);
    }

    @Test
    public void testAllDecorationsVisible() {
        decorationManager.generateDecorations(5);
        
        List<DecorationModel> allDecos = decorationManager.getAllDecorations();
        List<DecorationModel> visibleDecos = decorationManager.getVisibleDecorations();
        
        assertEquals(allDecos.size(), visibleDecos.size());
        
        for (DecorationModel deco : allDecos) {
            assertTrue(deco.isVisible(), "Chaque déco devrait être visible au début");
        }
    }

    @Test
    public void testDecorationDisappearsWhenGroundDestroyed() {
        decorationManager.generateDecorations(3);
        
        List<DecorationModel> decos = decorationManager.getAllDecorations();
        assertFalse(decos.isEmpty(), "Devrait avoir des décorations");
        
        DecorationModel deco = decos.get(0);
        int gridX = blockGrid.pixelToGridX(deco.getPixelX());
        int gridY = blockGrid.pixelToGridY(deco.getPixelY());
        
        for (int dy = 0; dy <= 2; dy++) {
            blockGrid.setBlock(gridX, gridY + dy, BlockType.AIR);
        }
        
        decorationManager.updateVisibility();
        
        assertFalse(deco.isVisible(), "La déco devrait disparaître quand le sol est détruit");
    }

    @Test
    public void testDecorationStaysVisibleWithGround() {
        decorationManager.generateDecorations(3);
        
        List<DecorationModel> decos = decorationManager.getAllDecorations();
        assertFalse(decos.isEmpty(), "Devrait avoir des décorations");
        
        DecorationModel deco = decos.get(0);
        
        decorationManager.updateVisibility();
        
        assertTrue(deco.isVisible(), "La déco devrait rester visible si le sol est intact");
    }

    @Test
    public void testDecorationsHaveCorrectBounds() {
        decorationManager.generateDecorations(5);
        
        List<DecorationModel> decos = decorationManager.getAllDecorations();
        
        for (DecorationModel deco : decos) {
            assertTrue(deco.getPixelX() >= 0);
            assertTrue(deco.getPixelX() < TEST_WIDTH);
            assertTrue(deco.getPixelY() >= 0);
            assertTrue(deco.getPixelY() < TEST_HEIGHT);
            assertTrue(deco.getPixelY() < TEST_WATER_LEVEL);
        }
    }

    @Test
    public void testDecorationTypes() {
        decorationManager.generateDecorations(10);
        
        List<DecorationModel> decos = decorationManager.getAllDecorations();
        
        for (DecorationModel deco : decos) {
            assertNotNull(deco.getType());
            assertTrue(deco.getType().getWidth() > 0);
            assertTrue(deco.getType().getHeight() > 0);
            assertNotNull(deco.getType().getImagePath());
        }
    }

    @Test
    public void testClearDecorations() {
        decorationManager.generateDecorations(5);
        assertFalse(decorationManager.getAllDecorations().isEmpty(), 
            "Devrait avoir des décorations");
        
        decorationManager.generateDecorations(3);
        
        List<DecorationModel> decos = decorationManager.getAllDecorations();
        assertTrue(decos.size() <= 3);
    }

}

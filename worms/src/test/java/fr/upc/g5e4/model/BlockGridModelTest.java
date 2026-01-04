package fr.upc.g5e4.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests pour la classe BlockGridModel
 */
public class BlockGridModelTest {

    private BlockGridModel model;
    private static final int TEST_WIDTH = 800;
    private static final int TEST_HEIGHT = 600;
    private static final int TEST_WATER_LEVEL = 500;

    @BeforeEach
    public void setUp() {
        // on crée un model de test
        model = new BlockGridModel(TEST_WIDTH, TEST_HEIGHT, TEST_WATER_LEVEL);
    }

    @Test
    public void testInitialization() {
        // on verifie que les dimensions sont respectées dans le model
        assertEquals(TEST_WIDTH, model.getPixelWidth());
        assertEquals(TEST_HEIGHT, model.getPixelHeight());
        assertEquals(TEST_WATER_LEVEL, model.getWaterLevel());
        assertEquals(BlockGridModel.BLOCK_SIZE, model.getBlockSize());
    }

    @Test
    public void testInitialGridFullAir() {
        // tous les blocs doivent être AIR au départ
        for (int x = 0; x < model.getGridWidth(); x++) {
            for (int y = 0; y < model.getGridHeight(); y++) {
                assertEquals(BlockType.AIR, model.getBlock(x, y));
            }
        }
    }

    @Test
    public void testSetBlock() {
        int gridX = 5;
        int gridY = 10;
        
        assertTrue(model.setBlock(gridX, gridY, BlockType.DIRT));
        assertEquals(BlockType.DIRT, model.getBlock(gridX, gridY));
        assertTrue(model.isDirty());
    }

    @Test
    public void testSetBlockOutOfBounds() {
        // test hors limites négatives
        assertFalse(model.setBlock(-1, 0, BlockType.DIRT));
        assertFalse(model.setBlock(0, -1, BlockType.DIRT));
        
        // test hors limites positives
        assertFalse(model.setBlock(model.getGridWidth(), 0, BlockType.DIRT));
        assertFalse(model.setBlock(0, model.getGridHeight(), BlockType.DIRT));
    }

    @Test
    public void testGetBlockOutOfBounds() {
        // les blocs hors limites doivent retourner AIR
        assertEquals(BlockType.AIR, model.getBlock(-1, 0));
        assertEquals(BlockType.AIR, model.getBlock(0, -1));
        assertEquals(BlockType.AIR, model.getBlock(model.getGridWidth(), 0));
        assertEquals(BlockType.AIR, model.getBlock(0, model.getGridHeight()));
    }

    @Test
    public void testDestroyBlock() {
        int gridX = 5;
        int gridY = 10;
        
        // d'abord créer un bloc solide
        model.setBlock(gridX, gridY, BlockType.DIRT);
        assertTrue(model.getBlock(gridX, gridY).isSolid());
        
        // puis on  le détruit
        assertTrue(model.destroyBlock(gridX, gridY));
        assertEquals(BlockType.AIR, model.getBlock(gridX, gridY));
        assertTrue(model.isDirty());
    }

    @Test
    public void testDestroyAirBlock() {
        // On ne peut pas détruire un bloc AIR (non solide)
        int gridX = 5;
        int gridY = 10;
        
        assertEquals(BlockType.AIR, model.getBlock(gridX, gridY));
        assertFalse(model.destroyBlock(gridX, gridY));
    }

    @Test
    public void testDestroyBlockOutOfBounds() {
        assertFalse(model.destroyBlock(-1, 0));
        assertFalse(model.destroyBlock(0, -1));
        assertFalse(model.destroyBlock(model.getGridWidth(), 0));
    }

    @Test
    public void testPixelToGridConversion() {
        int pixelX = 60; // 60 / 12 = 5
        int pixelY = 48; // 48 / 12 = 4
        
        assertEquals(5, model.pixelToGridX(pixelX));
        assertEquals(4, model.pixelToGridY(pixelY));
    }

    @Test
    public void testGridToPixelConversion() {
        int gridX = 5;
        int gridY = 4;
        
        assertEquals(80, model.gridToPixelX(gridX)); // 5 * 16 = 80
        assertEquals(64, model.gridToPixelY(gridY)); // 4 * 16 = 64
    }

    @Test
    public void testIsSolidAt() {
        int pixelX = 60;
        int pixelY = 48;
        
        // initialement tous les blocs sont AIR (non solide)
        assertFalse(model.isSolidAt(pixelX, pixelY));
        
        // on crée un bloc solide
        int gridX = model.pixelToGridX(pixelX);
        int gridY = model.pixelToGridY(pixelY);
        model.setBlock(gridX, gridY, BlockType.DIRT);
        
        assertTrue(model.isSolidAt(pixelX, pixelY));
    }

    @Test
    public void testGetGroundHeightAt() {
        // initialement tous les blocs sont AIR donc pas de sol
        int pixelX = 100;
        int groundHeight = model.getGroundHeightAt(pixelX);
        assertEquals(TEST_HEIGHT, groundHeight);
        
        // on crée un terrain à une certaine hauteur
        int gridX = model.pixelToGridX(pixelX);
        int gridY = 20; // position en grille
        model.setBlock(gridX, gridY, BlockType.GRASS);
        
        int newGroundHeight = model.getGroundHeightAt(pixelX);
        assertEquals(gridY * BlockGridModel.BLOCK_SIZE, newGroundHeight);
    }

    @Test
    public void testClearDirty() {
        model.setBlock(5, 5, BlockType.DIRT);
        assertTrue(model.isDirty());
        
        model.clearDirty();
        assertFalse(model.isDirty());
    }

    @Test
    public void testMultipleBlockTypes() {
        int gridX = 5;
        int gridY = 10;
        
        model.setBlock(gridX, gridY, BlockType.DIRT);
        assertEquals(BlockType.DIRT, model.getBlock(gridX, gridY));
        
        model.setBlock(gridX, gridY, BlockType.GRASS);
        assertEquals(BlockType.GRASS, model.getBlock(gridX, gridY));
        
        model.setBlock(gridX, gridY, BlockType.STONE);
        assertEquals(BlockType.STONE, model.getBlock(gridX, gridY));
        
        model.setBlock(gridX, gridY, BlockType.WATER);
        assertEquals(BlockType.WATER, model.getBlock(gridX, gridY));
    }
}


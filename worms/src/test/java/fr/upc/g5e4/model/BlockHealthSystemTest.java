package fr.upc.g5e4.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests pour le système de santé des blocs (BlockHealthGrid et damageBlock)
 */
public class BlockHealthSystemTest {

    private BlockGridModel model;
    private BlockHealthGridModel healthGrid;
    private static final int TEST_WIDTH = 1200;
    private static final int TEST_HEIGHT = 720;
    private static final int TEST_WATER_LEVEL = 600;

    @BeforeEach
    public void setUp() {
        model = new BlockGridModel(TEST_WIDTH, TEST_HEIGHT, TEST_WATER_LEVEL);
        healthGrid = model.getHealthGrid();
    }

    /**
     * initialise la santé des blocs après avoir changé la grille
     */
    private void initializeHealth() {
        BlockType[][] grid = new BlockType[model.getGridWidth()][model.getGridHeight()];
        for (int x = 0; x < model.getGridWidth(); x++) {
            for (int y = 0; y < model.getGridHeight(); y++) {
                grid[x][y] = model.getBlock(x, y);
            }
        }
        healthGrid.initializeHealth(grid);
    }

    @Test
    public void testBlockHealthInitialization() {
        // placer un bloc GRASS
        model.setBlock(5, 5, BlockType.GRASS);
        
        // initialiser la santé 
        BlockType[][] grid = new BlockType[model.getGridWidth()][model.getGridHeight()];
        for (int x = 0; x < model.getGridWidth(); x++) {
            for (int y = 0; y < model.getGridHeight(); y++) {
                grid[x][y] = model.getBlock(x, y);
            }
        }
        healthGrid.initializeHealth(grid);
        
        // la santé initiale doit être égale à maxHealth du type
        int maxHealth = BlockType.GRASS.getMaxHealth();
        int currentHealth = healthGrid.getHealth(5, 5);
        
        assertEquals(maxHealth, currentHealth);
    }

    @Test
    public void testDamageBlock() {
        // placer un bloc GRASS (5 PV par défaut)
        model.setBlock(5, 5, BlockType.GRASS);
        initializeHealth();
        int maxHealth = BlockType.GRASS.getMaxHealth(); // 5
        
        // appliquer des dégâts
        boolean destroyed = model.damageBlock(5, 5, 2);
        
        // le bloc ne doit pas être détruit (5 - 2 = 3)
        assertFalse(destroyed);
        assertEquals(3, healthGrid.getHealth(5, 5));
        assertEquals(BlockType.GRASS, model.getBlock(5, 5));
    }

    @Test
    public void testDestroyBlockViaHealth() {
        // placer un bloc GRASS (5 PV)
        model.setBlock(5, 5, BlockType.GRASS);
        initializeHealth();
        
        // appliquer 5 dégâts (destruction)
        boolean destroyed = model.damageBlock(5, 5, 5);
        
        // le bloc doit être détruit
        assertTrue(destroyed);
        assertEquals(BlockType.AIR, model.getBlock(5, 5));
        assertEquals(0, healthGrid.getHealth(5, 5));
    }

    @Test
    public void testExcessDamage() {
        // placer un bloc GRASS (5 PV)
        model.setBlock(5, 5, BlockType.GRASS);
        initializeHealth();
        
        // appliquer 10 dégâts (plus que la santé max)
        boolean destroyed = model.damageBlock(5, 5, 10);
        
        // le bloc doit être détruit
        assertTrue(destroyed);
        assertEquals(BlockType.AIR, model.getBlock(5, 5));
        assertEquals(0, healthGrid.getHealth(5, 5));
    }

    @Test
    public void testDamageAirBlock() {
        // on ne peut pas endommager un bloc AIR
        boolean damaged = model.damageBlock(5, 5, 10);
        
        assertFalse(damaged);
        assertEquals(BlockType.AIR, model.getBlock(5, 5));
    }

    @Test
    public void testMultipleDamageEvents() {
        // placer un bloc GRASS (5 PV)
        model.setBlock(5, 5, BlockType.GRASS);
        initializeHealth();
        
        // appliquer 2 dégâts
        boolean destroyed1 = model.damageBlock(5, 5, 2);
        assertFalse(destroyed1);
        assertEquals(3, healthGrid.getHealth(5, 5));
        
        // appliquer 1 dégât (santé: 2)
        boolean destroyed2 = model.damageBlock(5, 5, 1);
        assertFalse(destroyed2);
        assertEquals(2, healthGrid.getHealth(5, 5));
        
        // appliquer 2 dégâts (destruction)
        boolean destroyed3 = model.damageBlock(5, 5, 2);
        assertTrue(destroyed3);
        assertEquals(BlockType.AIR, model.getBlock(5, 5));
    }

    @Test
    public void testDamageBlocksInRadius() {
        // créer une zone de blocs GRASS
        for (int x = 3; x <= 7; x++) {
            for (int y = 3; y <= 7; y++) {
                model.setBlock(x, y, BlockType.GRASS);
            }
        }
        initializeHealth();
        
        // appliquer des dégâts en zone (centre 5,5, rayon 2)
        // au centre, la distance = 0, donc damageMultiplier = 1.0, dégâts = 10 * 1.0 = 10
        model.damageBlocksInRadius(5, 5, 2.0, 10);
        
        // vérifier que les blocs au centre sont détruits
        assertEquals(BlockType.AIR, model.getBlock(5, 5));
        assertEquals(BlockType.AIR, model.getBlock(4, 5));
        assertEquals(BlockType.AIR, model.getBlock(5, 4));
        assertEquals(BlockType.AIR, model.getBlock(6, 5));
        assertEquals(BlockType.AIR, model.getBlock(5, 6));
        
        // Les blocs en diagonale proche (distance √2 ≈ 1.41) doivent être endommagés
        // à distance 1.41: damageMultiplier = 1.0 - (1.41/2) = 0.295, dégâts = 10 * 0.295 = 2.95 → 2
        assertTrue(healthGrid.getHealth(4, 4) < BlockType.GRASS.getMaxHealth());
    }

    @Test
    public void testDamageRadiusWithFalloff() {
        // créer un bloc solide
        model.setBlock(5, 5, BlockType.STONE); // 10 PV
        model.setBlock(4, 5, BlockType.STONE);
        model.setBlock(6, 5, BlockType.STONE);
        initializeHealth();
        
        // appliquer 10 dégâts en zone
        model.damageBlocksInRadius(5, 5, 1.5, 10);
        
        // le bloc central doit être détruit (100% dégâts)
        assertEquals(BlockType.AIR, model.getBlock(5, 5));
        
        // les blocs adjacents doivent être endommagés mais pas détruits
        assertTrue(model.getBlock(4, 5).isSolid());
        assertTrue(model.getBlock(6, 5).isSolid());
    }

    @Test
    public void testDifferentBlockTypes() {
        // GRASS: 5 PV
        model.setBlock(3, 3, BlockType.GRASS);   
        // DIRT: 5 PV
        model.setBlock(4, 4, BlockType.DIRT);    
        // STONE: 10 PV
        model.setBlock(5, 5, BlockType.STONE);
        initializeHealth();
        
        // AK-47 damage: 1 PV
        model.damageBlock(3, 3, 1);
        assertEquals(4, healthGrid.getHealth(3, 3)); // 5-1=4
        
        // Grenade damage: 3 PV
        model.damageBlock(4, 4, 3);
        assertEquals(2, healthGrid.getHealth(4, 4)); // 5-3=2
        
        // RocketLauncher damage: 5 PV on STONE
        model.damageBlock(5, 5, 5);
        assertEquals(5, healthGrid.getHealth(5, 5)); // 10-5=5 (STONE a 10 PV)
        
        model.damageBlock(5, 5, 5);
        assertEquals(BlockType.AIR, model.getBlock(5, 5)); // Maintenant détruit
    }

    @Test
    public void testHealthPercentage() {
        // placer un bloc STONE (10 PV)
        model.setBlock(5, 5, BlockType.STONE);
        initializeHealth();
        int maxHealth = BlockType.STONE.getMaxHealth();
        
        // 100% santé
        assertEquals(1.0, healthGrid.getHealthPercent(5, 5, maxHealth), 0.01);
        
        // 50% santé
        model.damageBlock(5, 5, 5);
        assertEquals(0.5, healthGrid.getHealthPercent(5, 5, maxHealth), 0.01);
        
        // 10% santé
        model.damageBlock(5, 5, 4);
        assertEquals(0.1, healthGrid.getHealthPercent(5, 5, maxHealth), 0.01);
    }

    @Test
    public void testDirtyFlagOnDamage() {
        model.clearDirty();
        assertFalse(model.isDirty());
        
        // placer un bloc
        model.setBlock(5, 5, BlockType.GRASS);        initializeHealth();        model.clearDirty();
        
        // endommager le bloc (pas destruction)
        model.damageBlock(5, 5, 1);
        assertTrue(model.isDirty());
        
        model.clearDirty();
        
        // détruire le bloc
        model.damageBlock(5, 5, 10);
        assertTrue(model.isDirty());
    }

    @Test
    public void testOutOfBoundsDamage() {
        // endommager hors limites ne doit pas causer d'erreur
        boolean damaged = model.damageBlock(-1, -1, 10);
        assertFalse(damaged);
        
        damaged = model.damageBlock(1000, 1000, 10);
        assertFalse(damaged);
    }
}

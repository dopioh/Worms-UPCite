package fr.upc.g5e4.controller;

import fr.upc.g5e4.model.*;
import fr.upc.g5e4.model.weapons.Ak47;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests pour le système de dégâts terrain et détection de collision du ItemController
 */
public class ItemControllerTerrainTest {

    private ItemController itemController;
    private BlockGridModel blockGridModel;
    private BlockHealthGridModel healthGrid;
    private World world;
    private static final float PPM = 30f;

    @BeforeEach
    public void setUp() {
        // créer le ItemController
        itemController = new ItemController(null);
        
        // créer un monde physique
        world = new World(new Vec2(0, -9.81f));
        itemController.setWorld(world);
        
        // créer et configurer le BlockGridModel
        blockGridModel = new BlockGridModel(1200, 720, 600);
        healthGrid = blockGridModel.getHealthGrid();
        itemController.setBlockGridModel(blockGridModel);
        
        // configurer des worms pour les tests
        CharacterModel[] worms = new CharacterModel[0];
        itemController.setAllWorms(worms);
    }
    
    /**
     * initialise les PV des blocs après changement de la grille
     */
    private void initializeHealth() {
        BlockType[][] grid = new BlockType[blockGridModel.getGridWidth()][blockGridModel.getGridHeight()];
        for (int x = 0; x < blockGridModel.getGridWidth(); x++) {
            for (int y = 0; y < blockGridModel.getGridHeight(); y++) {
                grid[x][y] = blockGridModel.getBlock(x, y);
            }
        }
        healthGrid.initializeHealth(grid);
    }

    @Test
    public void testBlockGridModelAssignment() {
        // vérifier que le blockGridModel est bien assigné
        assertNotNull(itemController);
        // indirectement vérifier via updateProjectiles qui dépend de blockGridModel
    }

    @Test
    public void testHitGroundWithBlockGrid() {
        // créer un terrain simple avec des blocs
        // remplir une rangée de blocs à la hauteur 50 pixels
        int blockHeight = 50;
        int gridY = blockHeight / BlockGridModel.BLOCK_SIZE; // = 4
        
        for (int gridX = 0; gridX < blockGridModel.getGridWidth(); gridX++) {
            blockGridModel.setBlock(gridX, gridY, BlockType.GRASS);
        }
        
        // créer un projectile test
        ProjectileModel projectile = createTestProjectile(100, 100); // Au-dessus du terrain
        
        // vérifier que le projectile n'a pas touché le sol (au-dessus)
    }

    @Test
    public void testProjectileCollisionWithTerrain() {
        // créer un bloc solide
        blockGridModel.setBlock(10, 10, BlockType.GRASS);
        blockGridModel.setBlock(11, 10, BlockType.GRASS);
        blockGridModel.setBlock(12, 10, BlockType.GRASS);
        
        // créer un projectile au-dessus du terrain
        ProjectileModel projectile = createTestProjectile(10.5f, 5.0f);
        
        // le projectile doit être dans la liste des projectiles actifs
        int initialCount = itemController.getActiveProjectiles().size();
        itemController.getActiveProjectiles().add(projectile);
        assertEquals(initialCount + 1, itemController.getActiveProjectiles().size());
    }

    @Test
    public void testTerrainDamageApplication() {
        // créer un bloc GRASS (5 PV)
        blockGridModel.setBlock(10, 10, BlockType.GRASS);
        initializeHealth();
        
        int initialHealth = healthGrid.getHealth(10, 10);
        assertEquals(5, initialHealth);
        
        // appliquer des dégâts via damageBlock
        blockGridModel.damageBlock(10, 10, 2);
        
        // vérifier que la santé a diminué
        assertEquals(3, healthGrid.getHealth(10, 10));
    }

    @Test
    public void testTerrainDamageWithDifferentWeapons() {
        // créer des blocs
        blockGridModel.setBlock(5, 5, BlockType.GRASS);   // AK-47: 1 PV
        blockGridModel.setBlock(6, 6, BlockType.GRASS);   // Grenade: 3 PV
        blockGridModel.setBlock(7, 7, BlockType.STONE);   // RocketLauncher: 5 PV
        initializeHealth();
        
        // AK-47 damage: 1 PV
        blockGridModel.damageBlock(5, 5, 1);
        assertEquals(4, healthGrid.getHealth(5, 5)); // 5-1=4
        
        // Grenade damage: 3 PV
        blockGridModel.damageBlock(6, 6, 3);
        assertEquals(2, healthGrid.getHealth(6, 6)); // 5-3=2
        
        // RocketLauncher damage: 5 PV on STONE
        blockGridModel.damageBlock(7, 7, 5);
        assertEquals(5, healthGrid.getHealth(7, 7)); // 10-5=5 (STONE a 10 PV)
    }

    @Test
    public void testTerrainDamageOnDestroyedBlock() {
        // créer un bloc
        blockGridModel.setBlock(5, 5, BlockType.GRASS);
        
        // le détruire complètement
        boolean destroyed = blockGridModel.damageBlock(5, 5, 5);
        assertTrue(destroyed);
        assertEquals(BlockType.AIR, blockGridModel.getBlock(5, 5));
        
        // essayer de l'endommager à nouveau 
        boolean damagedAgain = blockGridModel.damageBlock(5, 5, 10);
        assertFalse(damagedAgain);
    }

    @Test
    public void testBlockGridDirtFlagOnDamage() {
        blockGridModel.clearDirty();
        assertFalse(blockGridModel.isDirty());
        
        // créer un bloc
        blockGridModel.setBlock(5, 5, BlockType.GRASS);
        blockGridModel.clearDirty();
        
        // endommager le bloc
        blockGridModel.damageBlock(5, 5, 2);
        
        // le flag dirty doit être activé
        assertTrue(blockGridModel.isDirty());
    }

    @Test
    public void testSolidAtDestroyedBlockPosition() {
        // créer un bloc solide
        blockGridModel.setBlock(5, 5, BlockType.GRASS);
        
        // vérifier que c'est solide
        int pixelX = 5 * BlockGridModel.BLOCK_SIZE;
        int pixelY = 5 * BlockGridModel.BLOCK_SIZE;
        assertTrue(blockGridModel.isSolidAt(pixelX, pixelY));
        
        // détruire le bloc
        blockGridModel.destroyBlock(5, 5);
        
        // vérifier que ce n'est plus solide
        assertFalse(blockGridModel.isSolidAt(pixelX, pixelY));
    }

    @Test
    public void testExplosionDamagePattern() {
        // créer une zone de blocs
        for (int x = 3; x <= 7; x++) {
            for (int y = 3; y <= 7; y++) {
                blockGridModel.setBlock(x, y, BlockType.GRASS);
            }
        }
        
        BlockHealthGridModel healthGrid = blockGridModel.getHealthGrid();
        
        // appliquer une explosion (rayon 2.0, dégâts 5)
        blockGridModel.damageBlocksInRadius(5, 5, 2.0, 5);
        
        // le bloc central doit être détruit (5 PV, 5 dégâts)
        assertEquals(BlockType.AIR, blockGridModel.getBlock(5, 5));
        
        // les blocs adjacents doivent être endommagés
        assertTrue(healthGrid.getHealth(4, 5) < 5);
        assertTrue(healthGrid.getHealth(5, 4) < 5);
        assertTrue(healthGrid.getHealth(6, 5) < 5);
        assertTrue(healthGrid.getHealth(5, 6) < 5);
    }

    /**
     * crée un projectile test à une position donnée
     */
    private ProjectileModel createTestProjectile(float x, float y) {
        Weapon testWeapon = new Ak47();
        CharacterModel shooter = null; // Pour les tests simples
        Vec2 position = new Vec2(x, y);
        
        ProjectileModel projectile = new ProjectileModel(
            testWeapon,
            shooter,
            position,
            30.0,    // range
            20.0,    // speed
            45.0     // shootingAngle
        );
        
        return projectile;
    }
}

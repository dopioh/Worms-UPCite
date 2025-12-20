package fr.upc.g5e4.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests pour l'enum BlockType
 */
public class BlockTypeTest {

    @Test
    public void testAllSolidBlocks() {
        assertTrue(BlockType.DIRT.isSolid());
        assertTrue(BlockType.GRASS.isSolid());
        assertTrue(BlockType.STONE.isSolid());
    }

    @Test
    public void testAllNonSolidBlocks() {
        assertFalse(BlockType.AIR.isSolid());
        assertFalse(BlockType.WATER.isSolid());
    }

    @Test
    public void testAllBlocksHaveColor() {
        for (BlockType type : BlockType.values()) {
            assertNotNull(type.getColor(), "le type " + type + " doit avoir une couleur");
        }
    }
}


package fr.upc.g5e4.item.model.weapons;

import org.junit.jupiter.api.Test;

import fr.upc.g5e4.model.ItemType;
import fr.upc.g5e4.model.weapons.BaseballBat;

import static org.junit.jupiter.api.Assertions.*;

class BaseballBatTest {
    
    @Test
    void BaseballBat_defaults_andUse() {
        var b = new BaseballBat();
        assertEquals("Baseball Bat", b.getName());
        assertEquals(ItemType.WEAPON, b.getObjectType());
        assertFalse(b.isInfinite());
        assertEquals(8, b.getMaxUsages());
        assertEquals(8, b.getRemainingUses());

        // 2 tirs
        assertTrue(b.use());
        assertEquals(7, b.getRemainingUses());
        assertTrue(b.use());
        assertEquals(6, b.getRemainingUses());
    }

}

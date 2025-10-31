package fr.upc.g5e4.item.model.weapons;

import fr.upc.g5e4.item.model.ItemType;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Ak47Test {

    @Test
    void ak47_defaults_andUse() {
        var ak = new Ak47();
        assertEquals("AK-47", ak.getName());
        assertEquals(ItemType.WEAPON, ak.getObjectType());
        assertFalse(ak.isInfinite());
        assertEquals(30, ak.getMaxUsages());
        assertEquals(30, ak.getRemainingUses());

        // 2 tirs
        assertTrue(ak.use());
        assertEquals(29, ak.getRemainingUses());
        assertTrue(ak.use());
        assertEquals(28, ak.getRemainingUses());
    }
}
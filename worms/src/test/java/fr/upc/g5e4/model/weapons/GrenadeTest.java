package fr.upc.g5e4.model.weapons;

import org.junit.jupiter.api.Test;

import fr.upc.g5e4.model.ItemType;
import fr.upc.g5e4.model.weapons.Grenade;

import static org.junit.jupiter.api.Assertions.*;

class GrenadeTest {
    
    @Test
    void Grenade_defaults_andUse() {
        var g = new Grenade();
        assertEquals("Grenade", g.getName());
        assertEquals(ItemType.WEAPON, g.getObjectType());
        assertFalse(g.isInfinite());
        assertEquals(3, g.getMaxUsages());
        assertEquals(3, g.getRemainingUses());

        // 2 tirs
        assertTrue(g.use());
        assertEquals(2, g.getRemainingUses());
        assertTrue(g.use());
        assertEquals(1, g.getRemainingUses());
    }

}

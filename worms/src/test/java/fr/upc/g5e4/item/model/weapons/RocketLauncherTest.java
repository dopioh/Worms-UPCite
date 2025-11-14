package fr.upc.g5e4.item.model.weapons;

import org.junit.jupiter.api.Test;

import fr.upc.g5e4.model.ItemType;
import fr.upc.g5e4.model.weapons.RocketLauncher;

import static org.junit.jupiter.api.Assertions.*;

class RocketLauncherTest {
    
    @Test
    void RocketLauncher_defaults_andUse() {
        var r = new RocketLauncher();
        assertEquals("Rocket Launcher", r.getName());
        assertEquals(ItemType.WEAPON, r.getObjectType());
        assertFalse(r.isInfinite());
        assertEquals(2, r.getMaxUsages());
        assertEquals(2, r.getRemainingUses());

        // 2 tirs
        assertTrue(r.use());
        assertEquals(1, r.getRemainingUses());
        assertTrue(r.use());
        assertEquals(0, r.getRemainingUses());
    }

}

package fr.upc.g5e4.item.model.gadgets;

import org.junit.jupiter.api.Test;

import fr.upc.g5e4.model.ItemType;
import fr.upc.g5e4.model.gadgets.Bumper;

import static org.junit.jupiter.api.Assertions.*;

class BumperTest {

    @Test
    void bumper_isInfinite_andUsable() {
        var b = new Bumper();
        assertEquals("Bumper", b.getName());
        assertEquals(ItemType.GADGET, b.getObjectType());
        assertTrue(b.isInfinite());
        assertTrue(b.isUsable());
        int before = b.getRemainingUses(); // Integer.MAX_VALUE

        assertTrue(b.use());               // toujours possible
        assertEquals(before, b.getRemainingUses()); // ne bouge pas
    }
}

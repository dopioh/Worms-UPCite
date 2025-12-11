package fr.upc.g5e4;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import fr.upc.g5e4.model.ItemType;
import fr.upc.g5e4.model.Weapon;

class ItemBasicsTest {

    /** Sous-classe de test : onUse() renvoie toujours true */
    static class TestWeapon extends Weapon {
        TestWeapon(int maxUsages, boolean infinite) {
            super("Dummy", maxUsages, infinite, 10, 0.0, 5.0, 10);
        }
        @Override
        protected boolean onUse() { return true; }
    }

    @Test
    void finiteItem_constructor_initializesFields() {
        var w = new TestWeapon(3, false);
        assertEquals("Dummy", w.getName());
        assertEquals(ItemType.WEAPON, w.getObjectType());
        assertEquals(3, w.getMaxUsages());
        assertFalse(w.isInfinite());
        assertEquals(3, w.getRemainingUses());
        assertTrue(w.isUsable());
    }

    @Test
    void finiteItem_use_decreasesRemainingUses() {
        var w = new TestWeapon(2, false);
        assertTrue(w.use());                 // 1/2 utilisé
        assertEquals(1, w.getRemainingUses());
        assertTrue(w.use());                 // 2/2 utilisé
        assertEquals(0, w.getRemainingUses());
        assertFalse(w.isUsable());           // plus d’usages
        assertFalse(w.use());                // ne peut plus s’utiliser
    }

    @Test
    void infiniteItem_neverDecrements_and_remainsUsable() {
        var w = new TestWeapon(0, true);    // 0 autorisé car infini
        int before = w.getRemainingUses();   // Integer.MAX_VALUE
        assertTrue(w.isInfinite());
        assertTrue(w.isUsable());
        assertTrue(w.use());
        assertEquals(before, w.getRemainingUses()); // ne bouge pas
    }

    @Test
    void finiteItem_refill_restoresMaxUsages() {
        var w = new TestWeapon(2, false);
        assertTrue(w.use());                 // passe à 1
        w.refill();
        assertEquals(2, w.getRemainingUses());
    }

    @Test
    void invalidValues_throwException() {
        // name vide
        assertThrows(IllegalArgumentException.class,
                () -> new Weapon("", 1, false, 1, 0, 1, 1) {
                    @Override protected boolean onUse() { return true; } 
                }); // pas le but de tester onUse, juste les conditions abstraite de Weapon oblige

        // cas "maxUsages = 0" pour un item non infini
        assertThrows(IllegalArgumentException.class,
                () -> new TestWeapon(0, false));
    }
}

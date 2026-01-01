package fr.upc.g5e4.Inventory;

import fr.upc.g5e4.model.InventoryModel;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class InventoryModelTest {

    // -------------------------
    // Tests InventoryJson methods
    // -------------------------

    @Test
    void hasRemainingUses_infinite_true() {
        InventoryModel.InventoryJson item = new InventoryModel.InventoryJson();
        item.isInfinite = true;
        item.remainingUses = null;
        assertTrue(item.hasRemainingUses());
    }

    @Test
    void hasRemainingUses_notInfinite_nullRemaining_false() {
        InventoryModel.InventoryJson item = new InventoryModel.InventoryJson();
        item.isInfinite = false;
        item.remainingUses = null;
        assertFalse(item.hasRemainingUses());
    }

    @Test
    void hasRemainingUses_notInfinite_positive_true() {
        InventoryModel.InventoryJson item = new InventoryModel.InventoryJson();
        item.isInfinite = false;
        item.remainingUses = 2;
        assertTrue(item.hasRemainingUses());
    }

    @Test
    void hasRemainingUses_notInfinite_zero_false() {
        InventoryModel.InventoryJson item = new InventoryModel.InventoryJson();
        item.isInfinite = false;
        item.remainingUses = 0;
        assertFalse(item.hasRemainingUses());
    }

    @Test
    void canUseThisTurn_false_whenMaxReached() {
        InventoryModel.InventoryJson item = new InventoryModel.InventoryJson();
        item.maxUsages = 2;
        item.useThisTurn = 2;
        item.isInfinite = true; 
        assertFalse(item.canUseThisTurn());
    }

    @Test
    void canUseThisTurn_true_whenUnderMax_andHasRemaining() {
        InventoryModel.InventoryJson item = new InventoryModel.InventoryJson();
        item.maxUsages = 2;
        item.useThisTurn = 1;
        item.isInfinite = false;
        item.remainingUses = 5;
        assertTrue(item.canUseThisTurn());
    }

    @Test
    void registerUseThisTurn_returnsFalse_whenCannotUse() {
        InventoryModel.InventoryJson item = new InventoryModel.InventoryJson();
        item.maxUsages = 1;
        item.useThisTurn = 1;
        item.isInfinite = true;

        assertFalse(item.registerUseThisTurn());
        assertEquals(1, item.useThisTurn);
    }

    @Test
    void registerUseThisTurn_incrementsUseThisTurn_andDecrementsRemaining_whenNotInfinite() {
        InventoryModel.InventoryJson item = new InventoryModel.InventoryJson();
        item.maxUsages = 3;
        item.useThisTurn = 0;
        item.isInfinite = false;
        item.remainingUses = 2;

        assertTrue(item.registerUseThisTurn());
        assertEquals(1, item.useThisTurn);
        assertEquals(1, item.remainingUses);
    }

    @Test
    void registerUseThisTurn_incrementsUseThisTurn_butDoesNotDecrement_whenInfinite() {
        InventoryModel.InventoryJson item = new InventoryModel.InventoryJson();
        item.maxUsages = 3;
        item.useThisTurn = 0;
        item.isInfinite = true;
        item.remainingUses = null;

        assertTrue(item.registerUseThisTurn());
        assertEquals(1, item.useThisTurn);
        assertNull(item.remainingUses);
    }

    @Test
    void resetTurn_setsUseThisTurnToZero() {
        InventoryModel.InventoryJson item = new InventoryModel.InventoryJson();
        item.useThisTurn = 2;
        item.resetTurn();
        assertEquals(0, item.useThisTurn);
    }

    @Test
    void getRemainingUseThisTurn_returnsDifference() {
        InventoryModel.InventoryJson item = new InventoryModel.InventoryJson();
        item.maxUsages = 5;
        item.useThisTurn = 2;
        assertEquals(3, item.getRemainingUseThisTurn());
    }

    @Test
    void getMaxUsages_returnsMaxUsages() {
        InventoryModel.InventoryJson item = new InventoryModel.InventoryJson();
        item.maxUsages = 7;
        assertEquals(7, item.getMaxUsages());
    }

    @Test
    void toString_formatsNameAndType() {
        InventoryModel.InventoryJson item = new InventoryModel.InventoryJson();
        item.name = "rocketlauncher";
        item.objectType = "weapon";
        assertEquals("rocketlauncher(weapon)", item.toString());
    }

    // -------------------------
    // Tests InventoryModel.load
    // -------------------------

    @Test
    void load_throwsIOException_whenResourceMissing() {
        assertThrows(IOException.class, () -> InventoryModel.load("/nope/missing-inventory.json"));
    }
}

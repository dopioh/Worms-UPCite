package fr.upc.g5e4.model;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test pour @see InventoryModel
 */
class InventoryModelTest {
    @Test
    void shouldFormatToString() {
        InventoryModel.InventoryJson item = new InventoryModel.InventoryJson();
        item.name = "AK";
        item.objectType = "weapon";

        assertEquals("AK(weapon)", item.toString());
    }

    @Test
    void RemainingUsesInfinite() {  
        InventoryModel.InventoryJson item = new InventoryModel.InventoryJson();
        item.isInfinite = true;
        item.remainingUses = 0;

        assertTrue(item.hasRemainingUses());
    }

    @Test
    void RemainingUsesWhenNullAndNotInfinite() {
        InventoryModel.InventoryJson item = new InventoryModel.InventoryJson();
        item.isInfinite = false;
        item.remainingUses = null;

        assertFalse(item.hasRemainingUses());
    }

    @Test
    void HaveRemainingUsesWhenPositive() {
        InventoryModel.InventoryJson item = new InventoryModel.InventoryJson();
        item.isInfinite = false;
        item.remainingUses = 2;

        assertTrue(item.hasRemainingUses());
    }

    @Test
    void NotBeUsableWhenMaxReached() {
        InventoryModel.InventoryJson item = new InventoryModel.InventoryJson();
        item.isInfinite = true; 
        item.maxUsages = 1;
        item.useThisTurn = 1;

        assertFalse(item.canUseThisTurn());
    }

    @Test
    void BeUsableWhenNotMaxAndHasRemainingUses() {
        InventoryModel.InventoryJson item = new InventoryModel.InventoryJson();
        item.isInfinite = false;
        item.maxUsages = 2;
        item.useThisTurn = 0;
        item.remainingUses = 1;

        assertTrue(item.canUseThisTurn());
    }

    @Test
    void registerUseAndDecreaseRemainingUses() {
        InventoryModel.InventoryJson item = new InventoryModel.InventoryJson();
        item.isInfinite = false;
        item.maxUsages = 3;
        item.remainingUses = 2;

        boolean ok = item.registerUseThisTurn();

        assertTrue(ok);
        assertEquals(1, item.useThisTurn);
        assertEquals(1, item.remainingUses);
    }

    @Test
    void RegisterUseWithoutDecreasingWhenInfinite() {
        InventoryModel.InventoryJson item = new InventoryModel.InventoryJson();
        item.isInfinite = true;
        item.maxUsages = 3;
        item.remainingUses = 2;

        boolean ok = item.registerUseThisTurn();

        assertTrue(ok);
        assertEquals(1, item.useThisTurn);
        assertEquals(2, item.remainingUses);
    }

    @Test
    void NotRegisterUseWhenNotUsable() {
        InventoryModel.InventoryJson item = new InventoryModel.InventoryJson();
        item.isInfinite = false;
        item.maxUsages = 1;
        item.remainingUses = 0;

        boolean ok = item.registerUseThisTurn();

        assertFalse(ok);
        assertEquals(0, item.useThisTurn);
        assertEquals(0, item.remainingUses);
    }

    @Test
    void resetTurn() {
        InventoryModel.InventoryJson item = new InventoryModel.InventoryJson();
        item.useThisTurn = 2;

        item.resetTurn();

        assertEquals(0, item.useThisTurn);
    }

    @Test
    void ReturnRemainingUsesThisTurn() {
        InventoryModel.InventoryJson item = new InventoryModel.InventoryJson();
        item.maxUsages = 3;
        item.useThisTurn = 1;

        assertEquals(2, item.getRemainingUseThisTurn());
    }

    @Test
    void throwWhenResourceMissing() {
        assertThrows(IOException.class, () -> InventoryModel.load("/does-not-exist.json"));
    }
}

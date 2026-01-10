package fr.upc.g5e4.Inventory;
import fr.upc.g5e4.model.InventoryModel.InventoryJson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryModelTest {

    private static InventoryJson newItem(String name,String objectType,int maxUsages,boolean isInfinite,Integer remainingUses) {
        InventoryJson it = new InventoryJson();
        it.name = name;
        it.objectType = objectType;
        it.maxUsages = maxUsages;
        it.isInfinite = isInfinite;
        it.remainingUses = remainingUses;
        it.useThisTurn = 0;
        return it;
    }

    // ToString 

    @Test
    void toStringp() {
        InventoryJson it = newItem("AK47", "weapon", 3, false, 10);
        assertEquals("AK47(weapon)", it.toString());
    }

    // HasRemainingUses

    @Test
    void hasRemainingUsesInfinite() {
        InventoryJson it = newItem("PotionHeal", "gadget", 1, true, null);
        assertTrue(it.hasRemainingUses());
    }

    @Test
    void hasRemainingUsesNotInfiniteNull() {
        InventoryJson it = newItem("Grenade", "weapon", 1, false, null);
        assertFalse(it.hasRemainingUses());
    }

    @Test
    void hasRemainingUsesPositive() {
        InventoryJson it = newItem("Grenade", "weapon", 1, false, 2);
        assertTrue(it.hasRemainingUses());
    }

    @Test
    void hasRemainingUsesZero() {
        InventoryJson it = newItem("Grenade", "weapon", 1, false, 0);
        assertFalse(it.hasRemainingUses());
    }

    @Test
    void hasRemainingUsesNegative() {
        InventoryJson it = newItem("Grenade", "weapon", 1, false, -1);
        assertFalse(it.hasRemainingUses());
    }

    //canUseThisTurn

    @Test
    void canUseThisTurnMaxUsages() {
        InventoryJson it = newItem("AK47", "weapon", 2, false, 10);
        it.useThisTurn = 2; 
        assertFalse(it.canUseThisTurn());
    }

    @Test
    void canUseThisTurnRemainingsUsesNull() {
        InventoryJson it = newItem("Grenade", "weapon", 2, false, 0);
        assertFalse(it.canUseThisTurn());
    }

    @Test
    void canUseThisTurn_HasRemainingUses() {
        InventoryJson it = newItem("Grenade", "weapon", 2, false, 1);
        it.useThisTurn = 0;
        assertTrue(it.canUseThisTurn());
    }

    @Test
    void canUseThisTurn_infini_maxusages() {
        InventoryJson it = newItem("PotionSpeed", "gadget", 1, true, null);
        assertTrue(it.canUseThisTurn());
        assertTrue(it.registerUseThisTurn());
        assertFalse(it.canUseThisTurn()); // maxUsages=1 atteint
    }

    @Test
    void canUseThisTurn_MaxUsagesNull() {
        InventoryJson it = newItem("Anything", "weapon", 0, true, null);
        assertFalse(it.canUseThisTurn());
    }

    //registerUseThisTurn

    @Test
    void registerUseThisTurn_shouldIncreaseUseThisTurnAndDecreaseRemainingUses_whenNotInfinite() {
        InventoryJson it = newItem("Grenade", "weapon", 3, false, 2);

        assertTrue(it.registerUseThisTurn());
        assertEquals(1, it.useThisTurn);
        assertEquals(1, it.remainingUses);

        assertTrue(it.registerUseThisTurn());
        assertEquals(2, it.useThisTurn);
        assertEquals(0, it.remainingUses);

        assertFalse(it.registerUseThisTurn());
        assertEquals(2, it.useThisTurn);
        assertEquals(0, it.remainingUses);
    }

    @Test
    void registerUseThisTurn_shouldNotDecreaseRemainingUses_whenInfinite() {
        InventoryJson it = newItem("PotionSpeed", "gadget", 2, true, 999);

        assertTrue(it.registerUseThisTurn());
        assertEquals(1, it.useThisTurn);
        assertEquals(999, it.remainingUses);

        assertTrue(it.registerUseThisTurn());
        assertEquals(2, it.useThisTurn);
        assertEquals(999, it.remainingUses);

        // Limite par tour atteinte
        assertFalse(it.registerUseThisTurn());
        assertEquals(2, it.useThisTurn);
        assertEquals(999, it.remainingUses);
    }

    @Test
    void registerUseThisTurn_whenRemainingUsesIsNullAndNotInfiniteShouldFail() {
        InventoryJson it = newItem("Mystery", "weapon", 2, false, null);

        assertFalse(it.registerUseThisTurn());
        assertEquals(0, it.useThisTurn);
        assertNull(it.remainingUses);
    }

    @Test
    void registerUseThisTurn_whenAtMaxUsagesShouldFailAndNotChangeState() {
        InventoryJson it = newItem("AK47", "weapon", 1, false, 10);
        it.useThisTurn = 1;

        assertFalse(it.registerUseThisTurn());
        assertEquals(1, it.useThisTurn);
        assertEquals(10, it.remainingUses);
    }

    // resetTurn

    @Test
    void resetTurn_shouldSetUseThisTurnToZero() {
        InventoryJson it = newItem("AK47", "weapon", 3, false, 10);
        it.useThisTurn = 2;

        it.resetTurn();
        assertEquals(0, it.useThisTurn);
    }

    @Test
    void resetTurn_shouldNotAffectRemainingUses() {
        InventoryJson it = newItem("AK47", "weapon", 3, false, 10);
        it.registerUseThisTurn(); 
        it.registerUseThisTurn(); // remainingUses  8

        assertEquals(2, it.useThisTurn);
        assertEquals(8, it.remainingUses);

        it.resetTurn();
        assertEquals(0, it.useThisTurn);
        assertEquals(8, it.remainingUses);
    }

    // getRemainingUseThisTurn() / getMaxUsages() 

    @Test
    void getMaxUsages_shouldReturnMaxUsages() {
        InventoryJson it = newItem("AK47", "weapon", 7, false, 10);
        assertEquals(7, it.getMaxUsages());
    }

    @Test
    void getRemainingUseThisTurn_shouldReturnMaxMinusUseThisTurn() {
        InventoryJson it = newItem("AK47", "weapon", 3, false, 10);
        assertEquals(3, it.getRemainingUseThisTurn());

        it.registerUseThisTurn();
        assertEquals(2, it.getRemainingUseThisTurn());

        it.registerUseThisTurn();
        assertEquals(1, it.getRemainingUseThisTurn());
    }

    @Test
    void getRemainingUseThisTurn_canBeZeroWhenMaxReached() {
        InventoryJson it = newItem("AK47", "weapon", 2, false, 10);
        it.registerUseThisTurn();
        it.registerUseThisTurn();
        assertEquals(0, it.getRemainingUseThisTurn());
        assertFalse(it.canUseThisTurn());
    }
}

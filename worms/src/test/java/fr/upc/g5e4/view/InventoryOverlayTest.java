package fr.upc.g5e4.view;

import fr.upc.g5e4.model.InventoryModel.InventoryJson;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryOverlayTest {

    @Test
    void inventoryVisibleToggle() {
        InventoryOverlay overlay = new InventoryOverlay(List.of());
        overlay.setSize(800, 600);

        overlay.setInventoryVisible(true);
        assertTrue(overlay.isInventoryVisible());

        overlay.setInventoryVisible(false);
        assertFalse(overlay.isInventoryVisible());
    }

    @Test
    void containsFalseWhenHidden() {
        InventoryOverlay overlay = new InventoryOverlay(List.of());
        overlay.setSize(800, 600);

        overlay.setInventoryVisible(false);
        assertFalse(overlay.contains(10, 10));
    }

    @Test
    void containsTrueWhenVisible() {
        InventoryOverlay overlay = new InventoryOverlay(List.of());
        overlay.setSize(800, 600);

        overlay.setInventoryVisible(true);
        assertTrue(overlay.contains(10, 10));
    }

    @Test
    void setEquippedItemStoreKey() {
        InventoryOverlay overlay = new InventoryOverlay(List.of());

        InventoryJson item = new InventoryJson();
        item.name = "AK47";
        item.objectType = "WEAPON";

        overlay.setEquippedItem(item);

        assertNotNull(overlay.getEquippedItem());
        assertEquals("AK47", overlay.getEquippedItem().name);
    }

    @Test
    void clearEquippedReset() {
        InventoryOverlay overlay = new InventoryOverlay(List.of());

        InventoryJson item = new InventoryJson();
        item.name = "PotionHeal";
        item.objectType = "GADGET";

        overlay.setEquippedItem(item);
        assertNotNull(overlay.getEquippedItem());

        overlay.clearEquipped();
        assertNull(overlay.getEquippedItem());
    }

    @Test
    void getEquippedGadgetReturnNullIfNotGadget() {
        InventoryOverlay overlay = new InventoryOverlay(List.of());

        InventoryJson item = new InventoryJson();
        item.name = "AK47";
        item.objectType = "WEAPON";

        overlay.setEquippedItem(item);

        assertNull(overlay.getEquippedGadget());
    }

    @Test
    void getEquippedGadgetReturnGadget() {
        InventoryOverlay overlay = new InventoryOverlay(List.of());

        InventoryJson item = new InventoryJson();
        item.name = "PotionHeal";
        item.objectType = "GADGET";

        overlay.setEquippedItem(item);

        assertNotNull(overlay.getEquippedGadget());
        assertEquals("PotionHeal", overlay.getEquippedGadget().name);
    }

    @Test
    void useGadgetByIndexCallListenerWhenHidden() {
        InventoryOverlay overlay = new InventoryOverlay(List.of());
        overlay.setSize(800, 600);
        overlay.setInventoryVisible(false);

        InventoryOverlay.Listener listener = mock(InventoryOverlay.Listener.class);
        overlay.setListener(listener);

        overlay.useGadgetByIndex(2);

        verify(listener).onUseGadgetByIndex(2);
    }

    @Test
    void useGadgetByIndexNoCallWhenVisible() {
        InventoryOverlay overlay = new InventoryOverlay(List.of());
        overlay.setSize(800, 600);
        overlay.setInventoryVisible(true);

        InventoryOverlay.Listener listener = mock(InventoryOverlay.Listener.class);
        overlay.setListener(listener);

        overlay.useGadgetByIndex(1);

        verify(listener, never()).onUseGadgetByIndex(anyInt());
    }

    @Test
    void useEquippedGadgetCallListenerWhenHidden() {
        InventoryOverlay overlay = new InventoryOverlay(List.of());
        overlay.setSize(800, 600);
        overlay.setInventoryVisible(false);

        InventoryOverlay.Listener listener = mock(InventoryOverlay.Listener.class);
        overlay.setListener(listener);

        overlay.useEquippedGadget();

        verify(listener).onUseEquippedGadget();
    }

    @Test
    void useEquippedGadgetNoCallWhenVisible() {
        InventoryOverlay overlay = new InventoryOverlay(List.of());
        overlay.setSize(800, 600);
        overlay.setInventoryVisible(true);

        InventoryOverlay.Listener listener = mock(InventoryOverlay.Listener.class);
        overlay.setListener(listener);

        overlay.useEquippedGadget();

        verify(listener, never()).onUseEquippedGadget();
    }

    @Test
    void notifyGadgetUsedRunCallback() {
        InventoryOverlay overlay = new InventoryOverlay(List.of());

        AtomicInteger c = new AtomicInteger(0);
        overlay.setOnGadgetUse(c::incrementAndGet);

        overlay.notifyGadgetUsed();

        assertEquals(1, c.get());
    }
}

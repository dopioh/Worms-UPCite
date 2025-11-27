package fr.upc.g5e4.Inventory;

import fr.upc.g5e4.view.InventoryOverlay;
import fr.upc.g5e4.model.InventoryModel.InventoryJson;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryOverlayTest {

    @Test
    void testSetInventoryVisible() {
        InventoryOverlay overlay = new InventoryOverlay(Collections.<InventoryJson>emptyList());

        assertFalse(overlay.isInventoryVisible());
        overlay.setInventoryVisible(true);
        assertTrue(overlay.isInventoryVisible());
    }
}

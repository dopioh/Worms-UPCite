package fr.upc.g5e4.view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HelpOverlayTest {

    @Test
    void helpVisibleTrue() {
        HelpOverlay overlay = new HelpOverlay();

        overlay.setHelpVisible(true);

        assertTrue(overlay.isHelpVisible());
    }

    @Test
    void helpVisibleFalse() {
        HelpOverlay overlay = new HelpOverlay();

        overlay.setHelpVisible(false);

        assertFalse(overlay.isHelpVisible());
    }

    @Test
    void containsReturnFalseWhenHidden() {
        HelpOverlay overlay = new HelpOverlay();
        overlay.setSize(800, 600);

        assertFalse(overlay.contains(100, 100));
    }

    @Test
    void containsReturnTrueWhenVisibleAndInside() {
        HelpOverlay overlay = new HelpOverlay();
        overlay.setSize(800, 600);
        overlay.setHelpVisible(true);

        assertTrue(overlay.contains(100, 100));
    }
}

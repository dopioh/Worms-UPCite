package fr.upc.g5e4.view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PauseOverlayTest {

    @Test
    void pauseVisibleToggle() {
        PauseOverlay overlay = new PauseOverlay();

        overlay.setPauseVisible(true);
        assertTrue(overlay.isPauseVisible());

        overlay.setPauseVisible(false);
        assertFalse(overlay.isPauseVisible());
    }

    @Test
    void containsFalseWhenHidden() {
        PauseOverlay overlay = new PauseOverlay();
        overlay.setSize(800, 600);

        overlay.setPauseVisible(false);
        assertFalse(overlay.contains(10, 10));
    }

    @Test
    void containsTrueWhenVisible() {
        PauseOverlay overlay = new PauseOverlay();
        overlay.setSize(800, 600);

        overlay.setPauseVisible(true);
        assertTrue(overlay.contains(10, 10));
    }

    @Test
    void setFullScreenOnNoCrash() {
        PauseOverlay overlay = new PauseOverlay();

        assertDoesNotThrow(() -> overlay.setFullScreenOn(true));
        assertDoesNotThrow(() -> overlay.setFullScreenOn(false));
    }

    @Test
    void setSoundOnNoCrash() {
        PauseOverlay overlay = new PauseOverlay();

        assertDoesNotThrow(() -> overlay.setSoundOn(true));
        assertDoesNotThrow(() -> overlay.setSoundOn(false));
    }

    @Test
    void setVentOnNoCrash() {
        PauseOverlay overlay = new PauseOverlay();

        assertDoesNotThrow(() -> overlay.setVentOn(true));
        assertDoesNotThrow(() -> overlay.setVentOn(false));
    }

    @Test
    void setTeamKillOnNoCrash() {
        PauseOverlay overlay = new PauseOverlay();

        assertDoesNotThrow(() -> overlay.setTeamKillOn(true));
        assertDoesNotThrow(() -> overlay.setTeamKillOn(false));
    }
}

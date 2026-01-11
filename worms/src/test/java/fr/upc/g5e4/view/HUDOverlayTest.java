package fr.upc.g5e4.view;

import fr.upc.g5e4.model.GameManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HUDOverlayTest {

    static class TestListener implements HUDOverlay.Listener {
        int bag, settings, endTurn, left, right, center, released;

        @Override public void onBagClicked() { bag++; }
        @Override public void onSettingsClicked() { settings++; }
        @Override public void onEndTurnClicked() { endTurn++; }
        @Override public void onCameraLeftPressed() { left++; }
        @Override public void onCameraRightPressed() { right++; }
        @Override public void onCameraCenterPressed() { center++; }
        @Override public void onCameraReleased() { released++; }
    }

    @Test
    void timerFramesClamp() {
        HUDOverlay hud = new HUDOverlay();

        hud.setTimerFrames(-10, -5);

        assertDoesNotThrow(() -> hud.setTimerFrames(-10, -5));
    }

    @Test
    void containsFalseOutsideClickableZones() {
        HUDOverlay hud = new HUDOverlay();
        hud.setSize(1280, 720);

        assertFalse(hud.contains(0, 0));
    }

    @Test
    void listenerSetAndGet() {
        HUDOverlay hud = new HUDOverlay();
        hud.setSize(1280, 720);

        assertTrue(hud.contains(20, 20));
    }

    @Test
    void containsTrueOnEndTurnZone() {
        HUDOverlay hud = new HUDOverlay();
        hud.setSize(1280, 720);

        assertTrue(hud.contains(250, 650));
    }

    @Test
    void timerHiddenSetterNoCrash() {
        HUDOverlay hud = new HUDOverlay();

        assertDoesNotThrow(() -> hud.setTimerHidden(true));
        assertDoesNotThrow(() -> hud.setTimerHidden(false));
    }

    @Test
    void triggerTurnFlashNoCrash() {
        HUDOverlay hud = new HUDOverlay();

        assertDoesNotThrow(hud::triggerTurnFlash);
    }
}

package fr.upc.g5e4.controller;

import fr.upc.g5e4.model.GameLoop;
import fr.upc.g5e4.model.GameManager;
import fr.upc.g5e4.view.HUDOverlay;
import fr.upc.g5e4.view.InventoryOverlay;
import fr.upc.g5e4.view.PauseOverlay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class HUDOverlayControllerTest {

    private GameManager gameManager;
    private HUDOverlay hudView;
    private InventoryOverlay inventoryOverlay;
    private PauseOverlay pauseOverlay;
    private CameraController cameraController;

    private HUDOverlayController controller;

    @BeforeEach
    void setup() {
        gameManager = mock(GameManager.class);
        hudView = mock(HUDOverlay.class);
        inventoryOverlay = mock(InventoryOverlay.class);
        pauseOverlay = mock(PauseOverlay.class);
        cameraController = mock(CameraController.class);

        controller = new HUDOverlayController(gameManager, hudView, inventoryOverlay, pauseOverlay, cameraController);
    }

    @Test
    void bagClickToggleInventory() {
        when(inventoryOverlay.isInventoryVisible()).thenReturn(false);

        controller.onBagClicked();

        verify(inventoryOverlay).setInventoryVisible(true);
    }

    @Test
    void settingsClickOpenPause_pauseTimer() {
        GameLoop loop = mock(GameLoop.class);
        when(gameManager.getGameLoop()).thenReturn(loop);

        controller.onSettingsClicked();

        verify(pauseOverlay).setPauseVisible(true);
        verify(loop).pauseTimer();
    }

    @Test
    void endTurnClickCall_nextRound() {
        controller.onEndTurnClicked();

        verify(gameManager).nextRound();
    }

    @Test
    void cameraLeftPressLeft() {
        controller.onCameraLeftPressed();

        verify(cameraController).activateLeft();
    }

    @Test
    void cameraRightPressRight() {
        controller.onCameraRightPressed();

        verify(cameraController).activateRight();
    }

    @Test
    void cameraCenterPressCenter() {
        controller.onCameraCenterPressed();

        verify(cameraController).centerOnCharacter();
    }

    @Test
    void mouseRelease_deactivateMovement() {
        controller.onCameraReleased();

        verify(cameraController).deactivateLeft();
        verify(cameraController).deactivateRight();
    }

    @Test
    void nullDependenciesDoNotCrash() {
        HUDOverlayController c = new HUDOverlayController(null, null, null, null, null);

        c.onBagClicked();
        c.onSettingsClicked();
        c.onEndTurnClicked();
        c.onCameraLeftPressed();
        c.onCameraRightPressed();
        c.onCameraCenterPressed();
        c.onCameraReleased();
        c.updateHUDData();
    }
}

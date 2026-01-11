package fr.upc.g5e4.controller;
import fr.upc.g5e4.model.GameManager;
import fr.upc.g5e4.view.HUDOverlay;
import fr.upc.g5e4.view.InventoryOverlay;
import fr.upc.g5e4.view.PauseOverlay;

/**
 * Classe controller pour HUDOverlay, gérant les interactions utilisateur avec l'interface HUD.
 */
public class HUDOverlayController implements HUDOverlay.Listener {

    private final GameManager gameManager;
    private final InventoryOverlay inventoryOverlay;
    private final PauseOverlay pauseOverlay;
    private final CameraController cameraController;

    public HUDOverlayController(GameManager gameManager,InventoryOverlay inventoryOverlay,PauseOverlay pauseOverlay,CameraController cameraController) {
        this.gameManager = gameManager;
        this.inventoryOverlay = inventoryOverlay;
        this.pauseOverlay = pauseOverlay;
        this.cameraController = cameraController;
    }

    @Override
    public void onBagClicked() {
        if (inventoryOverlay == null) return;
        inventoryOverlay.setInventoryVisible(!inventoryOverlay.isInventoryVisible());
    }

    @Override
    public void onSettingsClicked() {
        if (pauseOverlay == null) return;
        pauseOverlay.setPauseVisible(true);
        if (gameManager != null && gameManager.getGameLoop() != null) {
            gameManager.getGameLoop().pauseTimer();
        }
    }

    @Override
    public void onEndTurnClicked() {
        if (gameManager != null) gameManager.nextRound();
    }

    @Override
    public void onCameraLeftPressed() {
        if (cameraController != null) cameraController.activateLeft();
    }

    @Override
    public void onCameraRightPressed() {
        if (cameraController != null) cameraController.activateRight();
    }

    @Override
    public void onCameraCenterPressed() {
        if (cameraController != null) cameraController.centerOnCharacter();
    }

    @Override
    public void onCameraReleased() {
        if (cameraController != null) {
            cameraController.deactivateLeft();
            cameraController.deactivateRight();
        }
    }
}

package fr.upc.g5e4.controller;
import fr.upc.g5e4.model.GameManager;
import fr.upc.g5e4.model.CharacterModel;
import fr.upc.g5e4.model.InventoryModel.InventoryJson;
import fr.upc.g5e4.view.HUDOverlay;
import fr.upc.g5e4.view.InventoryOverlay;
import fr.upc.g5e4.view.PauseOverlay;

/**
 * Classe controller pour HUDOverlay, gérant les interactions utilisateur avec l'interface HUD.
 */
public class HUDOverlayController implements HUDOverlay.Listener {

    private final GameManager gameManager;
    private final HUDOverlay hudView;
    private final InventoryOverlay inventoryOverlay;
    private final PauseOverlay pauseOverlay;
    private final CameraController cameraController;

    public HUDOverlayController(GameManager gameManager, HUDOverlay hudView, InventoryOverlay inventoryOverlay, PauseOverlay pauseOverlay, CameraController cameraController) {
        this.gameManager = gameManager;
        this.hudView = hudView;
        this.inventoryOverlay = inventoryOverlay;
        this.pauseOverlay = pauseOverlay;
        this.cameraController = cameraController;
    }

    /**
     * met a jour les données affichées dans le HUD
     */
    public void updateHUDData() {
        if (hudView == null || gameManager == null) return;

        // recuperer les données du worm actif
        CharacterController activeWormController = gameManager.getActiveWormController();
        if (activeWormController != null) {
            CharacterModel worm = activeWormController.getModel();
            if (worm != null) {
                double hp = worm.getHealth();
                double shield = worm.getShield();
                hudView.updateWormData(hp, shield);
            }

            // recuperer l'arme actuelle
            String weaponName = null;
            if (activeWormController.getCurrentWeapon() != null) {
                weaponName = activeWormController.getCurrentWeapon().getName();
            }

            // recuperer l'item équipé depuis l'inventaire
            InventoryJson equippedItem = null;
            if (inventoryOverlay != null) {
                equippedItem = inventoryOverlay.getEquippedItem();
            }

            hudView.updateWeaponData(weaponName, equippedItem);
        }
    }

    @Override
    public void onBagClicked() {
        if (inventoryOverlay == null) return;
        
         boolean opening = !inventoryOverlay.isInventoryVisible();
        if (opening) {
            inventoryOverlay.openOnArmes();
            inventoryOverlay.setInventoryVisible(true);
        } else {
            inventoryOverlay.setInventoryVisible(false);
        }
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

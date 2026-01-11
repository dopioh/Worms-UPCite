package fr.upc.g5e4.controller;

import fr.upc.g5e4.model.GameLoop;
import fr.upc.g5e4.model.GameManager;
import fr.upc.g5e4.model.InventoryModel.InventoryJson;
import fr.upc.g5e4.view.InventoryOverlay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class InventoryOverlayControllerTest {

    private GameManager gameManager;
    private InventoryOverlay view;
    private SwingInputController inputController;

    private InventoryOverlayController controller;

    @BeforeEach
    void setup() {
        gameManager = mock(GameManager.class);
        view = mock(InventoryOverlay.class);
        inputController = mock(SwingInputController.class);

        controller = new InventoryOverlayController(gameManager,view,inputController,null,null,null,null);
    }

    @Test
    void fireReleasedOutside_InputController() {
        controller.onFireReleasedOutside(10, 20);

        verify(inputController).fireWeaponAtAngle(10, 20);
    }

    @Test
    void mouseMoveOutsideUpdatesTrajectory() {
        controller.onMouseMovedOutside(5, 15);

        verify(inputController).updateTrajectoryFromMouseMotion(5, 15);
    }

    @Test
    void mouseDragOutsideUpdatesTrajectory() {
        controller.onMouseDraggedOutside(8, 18);

        verify(inputController).updateTrajectoryFromMouseMotion(8, 18);
    }

    @Test
    void closeInventoryHidesAndResumeTimer() {
        GameLoop loop = mock(GameLoop.class);
        when(gameManager.getGameLoop()).thenReturn(loop);

        controller.onCloseInventory();

        verify(view).setInventoryVisible(false);
        verify(loop).resumeTimer();
    }

    @Test
    void categoryBoutiquePauseTimer() {
        GameLoop loop = mock(GameLoop.class);
        when(gameManager.getGameLoop()).thenReturn(loop);

        controller.onCategoryChanged(InventoryOverlay.Category.BOUTIQUE);

        verify(loop).pauseTimer();
    }

    @Test
    void categoryOtherResumeTimer() {
        GameLoop loop = mock(GameLoop.class);
        when(gameManager.getGameLoop()).thenReturn(loop);

        controller.onCategoryChanged(InventoryOverlay.Category.ARMES);

        verify(loop).resumeTimer();
    }

    @Test
    void equipWeaponFromInventory() {
        InventoryJson item = new InventoryJson();
        item.name = "ak47";
        item.objectType = "WEAPON";

        GameLoop loop = mock(GameLoop.class);
        when(gameManager.getGameLoop()).thenReturn(loop);

        controller.onEquipItem(item);

        verify(gameManager).setWeaponFromInventory("ak47");
        verify(view).setEquippedItem(item);
        verify(view).setInventoryVisible(false);
        verify(loop).resumeTimer();
    }

    @Test
    void useGadgetByIndexCallsGameManager() {
        InventoryJson gadget = new InventoryJson();
        gadget.name = "PotionHeal";
        gadget.objectType = "GADGET";
        gadget.maxUsages = 1;
        gadget.remainingUses = 1;

        when(view.getInventory()).thenReturn(List.of(gadget));

        controller.onUseGadgetByIndex(0);

        verify(gameManager).useGadgetFromInventory("PotionHeal");
        verify(view).notifyGadgetUsed();
    }

    @Test
    void useEquippedGadgetCallsGameManager() {
        InventoryJson gadget = new InventoryJson();
        gadget.name = "PotionSpeed";
        gadget.objectType = "GADGET";
        gadget.maxUsages = 1;
        gadget.remainingUses = 1;

        when(view.getEquippedGadget()).thenReturn(gadget);

        controller.onUseEquippedGadget();

        verify(gameManager).useGadgetFromInventory("PotionSpeed");
        verify(view).notifyGadgetUsed();
    }

    @Test
    void nullDependenciesNoCrash() {
        InventoryOverlayController c = new InventoryOverlayController(null, null, null, null, null, null, null);

        c.onFireReleasedOutside(0, 0);
        c.onMouseMovedOutside(0, 0);
        c.onMouseDraggedOutside(0, 0);
        c.onCloseInventory();
        c.onCategoryChanged(InventoryOverlay.Category.ARMES);
        c.onEquipItem(null);
        c.onUseGadgetByIndex(0);
        c.onUseEquippedGadget();
    }
}

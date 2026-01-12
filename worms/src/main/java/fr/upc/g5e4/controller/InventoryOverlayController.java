package fr.upc.g5e4.controller;

import java.util.ArrayList;
import java.util.List;

import fr.upc.g5e4.controller.minigames.WormGuessController;
import fr.upc.g5e4.controller.minigames.WormJackController;
import fr.upc.g5e4.controller.minigames.WormRaceController;
import fr.upc.g5e4.model.GameManager;
import fr.upc.g5e4.model.InventoryModel.InventoryJson;
import fr.upc.g5e4.view.InventoryOverlay;

public class InventoryOverlayController implements InventoryOverlay.Listener {

    private final GameManager gameManager;
    private final InventoryOverlay view;
    private final SwingInputController inputController;
    private final PlayerDataController playerDataController;

    private final WormRaceController wormRaceController;
    private final WormGuessController wormGuessController;
    private final WormJackController wormJackController;

    public InventoryOverlayController(GameManager gameManager,InventoryOverlay view,SwingInputController inputController,PlayerDataController playerDataController,WormRaceController wormRaceController,WormGuessController wormGuessController,WormJackController wormJackController) {
        this.gameManager = gameManager;
        this.view = view;
        this.inputController = inputController;
        this.playerDataController = playerDataController;
        this.wormRaceController = wormRaceController;
        this.wormGuessController = wormGuessController;
        this.wormJackController = wormJackController;
    }

    @Override
    public void onFireReleasedOutside(int x, int y) {
        if (inputController != null){
            inputController.updateTrajectoryFromMouse(x, y);
            inputController.fireWeaponAtAngle(x, y);
        }
    }

    @Override
    public void onMouseMovedOutside(int x, int y) {
        if (inputController != null) inputController.updateTrajectoryFromMouseMotion(x, y);
    }

    @Override
    public void onMouseDraggedOutside(int x, int y) {
        if (inputController != null) inputController.updateTrajectoryFromMouseMotion(x, y);
    }

    @Override
    public void onCloseInventory() {
        if (view != null) view.setInventoryVisible(false);
        if (gameManager != null && gameManager.getGameLoop() != null) {
            gameManager.getGameLoop().resumeTimer();
        }
    }

    @Override
    public void onCategoryChanged(InventoryOverlay.Category newCat) {
        if (gameManager == null || gameManager.getGameLoop() == null) return;

        if (newCat == InventoryOverlay.Category.BOUTIQUE) {
            gameManager.getGameLoop().pauseTimer();
        } else {
            gameManager.getGameLoop().resumeTimer();
        }
    }

    @Override
    public void onEquipItem(InventoryJson item) {
        if (item == null || gameManager == null) return;

        if ("GADGET".equalsIgnoreCase(item.objectType)) {
            gameManager.setGadgetFromInventory(item.name);
            if (inputController != null) inputController.disableTrajectory();
        } else {
            gameManager.setWeaponFromInventory(item.name);
        }
        view.setEquippedItem(item);
        view.setInventoryVisible(false);
        if (gameManager.getGameLoop() != null) gameManager.getGameLoop().resumeTimer();
    }

    @Override
    public void onBuyShopItem(int shopIndex) {
        List<InventoryOverlay.ShopItem> items = view.getShopItems();
        if (items == null || shopIndex < 0 || shopIndex >= items.size()) {
            return;
        }

        InventoryOverlay.ShopItem shopItem = items.get(shopIndex);
        if (gameManager == null || gameManager.getCurrentTeam() == null) {
            return;
        }

        boolean isMinigame = "WORMRACE".equalsIgnoreCase(shopItem.name) || "WORMGUESS".equalsIgnoreCase(shopItem.name) || "WORMJACK".equalsIgnoreCase(shopItem.name);

        boolean isSkin = shopItem.name.startsWith("Worm") && !isMinigame;

        gameManager.getCurrentTeam().removeMoney(shopItem.price);

        if (isSkin) {
            if (playerDataController != null && playerDataController.getPlayerData() != null) {
                playerDataController.getPlayerData().unlockSkin(shopItem.name);
            }
        } else if (!shopItem.name.startsWith("WORM")) {
            addItemToTeamInventory(shopItem);
        }

        if (playerDataController != null) {
            playerDataController.sauvegarderJoueur();
        }

        System.out.println("Achat réussi: " + shopItem.name + " pour " + shopItem.price + "$");

        if ("WORMRACE".equalsIgnoreCase(shopItem.name)) {
            if (wormRaceController != null) {
                wormRaceController.showGame();
            } else {
                System.err.println("ERREUR: wormRaceController est null");
            }
        }

        if ("WORMGUESS".equalsIgnoreCase(shopItem.name)) {
            if (wormGuessController != null) {
                wormGuessController.showGame();
            } else {
                System.err.println("ERREUR: wormGuessController est null");
            }
        }

        if ("WORMJACK".equalsIgnoreCase(shopItem.name)) {
            if (wormJackController != null) {
                wormJackController.showGame();
            } else {
                System.err.println("ERREUR: wormJackController est null");
            }
        }

        view.repaint();
    }

    private void addItemToTeamInventory(InventoryOverlay.ShopItem shopItem) {
        if (gameManager == null || gameManager.getCurrentTeam() == null) {
            System.err.println("Erreur: pas d'équipe active pour ajouter l'item");
            return;
        }

        List<InventoryJson> teamInventory = gameManager.getCurrentTeam().getInventory();
        if (teamInventory == null) return;

        String objectType = null;
        int quantityToAdd = 0;

        if (shopItem.imagePath.equals("potionheal")) {
            objectType = "potion";
            quantityToAdd = 1;
        } else if (shopItem.imagePath.equals("potionshield")) {
            objectType = "potion";
            quantityToAdd = 1;
        } else if (shopItem.imagePath.equals("potionspeed")) {
            objectType = "potion";
            quantityToAdd = 1;
        } else if (shopItem.imagePath.equals("potionjump")) {
            objectType = "potion";
            quantityToAdd = 1;
        } else if (shopItem.imagePath.equals("ak47")) {
            objectType = "weapon";
            quantityToAdd = 10;
        } else if (shopItem.imagePath.equals("bazooka")) {
            objectType = "weapon";
            quantityToAdd = 1;
        } else if (shopItem.imagePath.equals("grenade")) {
            objectType = "weapon";
            quantityToAdd = 1;
        }

        if (objectType == null) {
            System.out.println("Item introuvable dans l'inventaire: " + shopItem.name);
            return;
        }

        InventoryJson existingItem = null;
        for (InventoryJson item : teamInventory) {
            if (item.name.equalsIgnoreCase(shopItem.imagePath)) {
                existingItem = item;
                break;
            }
        }

        if (existingItem != null) {
            if (!existingItem.isInfinite && existingItem.remainingUses != null) {
                existingItem.remainingUses += quantityToAdd;
                System.out.println("Item ajouté à l'inventaire: " + shopItem.name + " (+" + quantityToAdd + " → total: " + existingItem.remainingUses + ")");
            }
        }
    }

    @Override
    public void onUseGadgetByIndex(int gadgetIndex) {
        if (gameManager == null) return;

        List<InventoryJson> inv = view.getInventory();
        if (inv == null) return;

        List<InventoryJson> gadgets = new ArrayList<>();
        for (InventoryJson item : inv) {
            if (item != null && "GADGET".equals(item.objectType)) {
                gadgets.add(item);
            }
        }

        if (gadgetIndex < 0 || gadgetIndex >= gadgets.size()) {
            return;
        }

        InventoryJson gadget = gadgets.get(gadgetIndex);
        if (gadget.canUseThisTurn()) {
            gameManager.useGadgetFromInventory(gadget.name);
            view.notifyGadgetUsed();
        }
    }

    @Override
    public void onUseEquippedGadget() {
        if (view == null) return;
        InventoryJson equippedItem = view.getEquippedGadget();
        if (equippedItem == null) return;

        if (!"GADGET".equals(equippedItem.objectType)) {
            return;
        }

        if (equippedItem.canUseThisTurn() && gameManager != null) {
            gameManager.useGadgetFromInventory(equippedItem.name);
            view.notifyGadgetUsed();
        }
    }
}
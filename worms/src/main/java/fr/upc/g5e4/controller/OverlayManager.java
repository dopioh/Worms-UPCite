package fr.upc.g5e4.controller;

import fr.upc.g5e4.view.HelpOverlay;
import fr.upc.g5e4.view.HUDOverlay;
import fr.upc.g5e4.view.InventoryOverlay;
import fr.upc.g5e4.view.PauseOverlay;
import fr.upc.g5e4.model.GameManager;

import javax.swing.*;

/**
 * Contrôleur central pour gérer l'affichage et la priorité des overlays.
 *   Ouvrir/fermer Pause, Help, Inventory
 *   Assurer l'ordre des couches (moveToFront)
 *   Centraliser la logique UI (MVC) au lieu de la mettre dans les vues
 * @author Hugo
 * @version 1.0
 */
public class OverlayManager implements
        PauseOverlay.Listener,
        HUDOverlay.Listener,
        HelpOverlay.Listener,
        InventoryOverlay.Listener {

    private final JLayeredPane layeredPane;
    private final PauseOverlay pauseOverlay;
    private final HelpOverlay helpOverlay;
    private final InventoryOverlay inventoryOverlay;
    private final HUDOverlay hudOverlay;

    private final GameManager gameManager;

    /**
     * Construit un OverlayManager.
     *
     * @param layeredPane pane qui contient les overlays 
     * @param pauseOverlay overlay pause
     * @param helpOverlay overlay aide
     * @param inventoryOverlay overlay inventaire
     * @param hudOverlay overlay HUD 
     * @param gameManager gestionnaire de jeu
     */
    public OverlayManager(JLayeredPane layeredPane, PauseOverlay pauseOverlay, HelpOverlay helpOverlay, InventoryOverlay inventoryOverlay, HUDOverlay hudOverlay, GameManager gameManager) {
        this.layeredPane = layeredPane;
        this.pauseOverlay = pauseOverlay;
        this.helpOverlay = helpOverlay;
        this.inventoryOverlay = inventoryOverlay;
        this.hudOverlay = hudOverlay;
        this.gameManager = gameManager;
    }

    /**
     * Installe ce manager comme listener sur les overlays.
     *
     * À appeler après avoir créé et ajouté les overlays au layeredPane
     */
    public void installListeners() {
        if (pauseOverlay != null) pauseOverlay.setListener(this);
        if (helpOverlay != null) helpOverlay.setListener(this);
        if (inventoryOverlay != null) inventoryOverlay.setListener(this);
        if (hudOverlay != null) hudOverlay.setListener(this);
    }

    /**
     * Ferme tous les overlays (pause/help/inventory).
     */
    public void closeAll() {
        if (pauseOverlay != null) pauseOverlay.setPauseVisible(false);
        if (helpOverlay != null) helpOverlay.setHelpVisible(false);
        if (inventoryOverlay != null) inventoryOverlay.setInventoryVisible(false);
        bringToFront();
    }

    /**
     * Ouvre/ferme l'overlay de pause.
     * Si la pause se ferme, l'aide se ferme aussi
     *
     * @param visible true pour afficher, false pour masquer
     */
    public void setPauseVisible(boolean visible) {
        if (pauseOverlay != null) {
            pauseOverlay.setPauseVisible(visible);
        }
        if (!visible) {
            setHelpVisible(false);
        }
        bringToFront();
    }

    /**
     * Ouvre/ferme l'overlay d'aide.
     *
     * @param visible true pour afficher, false pour masquer
     */
    public void setHelpVisible(boolean visible) {
        if (helpOverlay != null) {
            helpOverlay.setHelpVisible(visible);
        }
        bringToFront();
    }

    /**
     * Ouvre/ferme l'overlay d'inventaire.
     * Si la pause est ouverte, l'inventaire ne s'ouvre pas
     *
     * @param visible true pour afficher, false pour masquer
     */
    public void setInventoryVisible(boolean visible) {
        if (pauseOverlay != null && pauseOverlay.isPauseVisible() && visible) {
            return;
        }
        if (inventoryOverlay != null) {
            inventoryOverlay.setInventoryVisible(visible);
        }
        bringToFront();
    }

    /**
     * Bascule l'overlay de pause.
     */
    public void togglePause() {
        if (pauseOverlay == null) return;
        setPauseVisible(!pauseOverlay.isPauseVisible());
    }
    
    /**i
     * Bascule l'overlay d'inventaire.
     */
    public void toggleInventory() {
        if (inventoryOverlay == null) return;
        setInventoryVisible(!inventoryOverlay.isInventoryVisible());
    }

    /**
     * Bascule l'overlay d'aide.
     */
    public void toggleHelp() {
        if (helpOverlay == null) return;
        setHelpVisible(!helpOverlay.isHelpVisible());
    }

    /**
     * Indique si l'overlay de pause est visible.
     * @return true si visible, sinon false
     */
    public boolean isInventoryVisible() {
        return inventoryOverlay != null && inventoryOverlay.isInventoryVisible();
    }

    /**
     * Indique si le HUD a été cliqué.
     * @param x 
     * @param y 
     * @return true si le HUD a été cliqué, sinon false
     */
    public boolean isHudHit(int x, int y) {
        return hudOverlay != null && hudOverlay.contains(x, y);
    }


    /**
     * Met les overlays au premier plan dans un ordre cohérent.
     *
     * HUD  Inventory  Pause  Help
     */
    public void bringToFront() {
        if (layeredPane == null) return;

        if (hudOverlay != null) layeredPane.moveToFront(hudOverlay);
        if (inventoryOverlay != null && inventoryOverlay.isInventoryVisible()) layeredPane.moveToFront(inventoryOverlay);
        if (pauseOverlay != null && pauseOverlay.isPauseVisible()) layeredPane.moveToFront(pauseOverlay);
        if (helpOverlay != null && helpOverlay.isHelpVisible()) layeredPane.moveToFront(helpOverlay);

        layeredPane.repaint();
    }


    /**
     * Clic sur HELP depuis l'overlay de pause
     */
    @Override
    public void onHelpClicked() {
        if (helpOverlay == null) return;
        setHelpVisible(!helpOverlay.isHelpVisible());
    }

    /**
     * Clic sur Vent depuis l'overlay de pause.
     */
    @Override
    public void onVentClicked() {
        System.out.println("[OverlayManager] Vent cliqué (TODO)");
    }

    /**
     * Clic sur Save depuis l'overlay de pause MEIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
     */
    @Override
    public void onSaveClicked() {
        System.out.println("[OverlayManager] Save cliqué (TODO)");
    }

    /**
     * Clic sur le sac depuis le HUD 
     */
    @Override
    public void onBagClicked() {
        if (inventoryOverlay == null) return;
        setInventoryVisible(!inventoryOverlay.isInventoryVisible());
    }

    /**
     * Clic sur FERMER depuis HelpOverlay 
     */
    @Override
    public void onCloseClicked() {
        setHelpVisible(false);
    }
    
    /**
     * Indique si un overlay UI doit bloquer les actions de gameplay
     * si Pause/Help/Inventory est visible, alors on bloque le gameplay.
     *
     * @return true si un overlay interactif est ouvert, sinon false
     */
    public boolean isBlockingGameplayInput() {
        boolean pause = pauseOverlay != null && pauseOverlay.isPauseVisible() && pauseOverlay.isVisible();
        boolean help  = helpOverlay  != null && helpOverlay.isHelpVisible()  && helpOverlay.isVisible();
        boolean inv   = inventoryOverlay != null && inventoryOverlay.isInventoryVisible() && inventoryOverlay.isVisible();
        return pause || help || inv;
    }
    
    /**
     * Appelé quand un item est équipé dans l'inventaire.
     *
     * @param item item équipé
     */
    @Override
    public void onItemEquipped(fr.upc.g5e4.model.InventoryModel.InventoryJson item) {
        if (item == null || gameManager == null) return;
        // équipe dans le GameManager
        gameManager.setObjectFromInventory(item);

        // ferme l'inventaire après équipement
        setInventoryVisible(false);
    }
}

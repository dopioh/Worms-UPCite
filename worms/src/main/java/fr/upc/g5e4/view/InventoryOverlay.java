package fr.upc.g5e4.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import fr.upc.g5e4.model.InventoryModel.InventoryJson;
import fr.upc.g5e4.controller.SwingInputController;
import fr.upc.g5e4.model.GameManager;

public class InventoryOverlay extends JComponent {
    private List<InventoryJson> inventory;
    private boolean visible = false;
    private MouseAdapter fireListener; // pour déléguer le tir

    // icône du sac
    private BufferedImage icon;
    private Rectangle iconBounds;

    // liste des items
    private final List<Rectangle> itemBounds = new ArrayList<>();
    private int selectedIndex = -1;
    private InventoryJson equippedItem = null;

    // zone du panneau inventaire
    private Rectangle panelBounds;

    private GameManager gameManager;

    public InventoryOverlay(List<InventoryJson> inventory) {
        this.inventory = inventory;
        setOpaque(false);

        try {
            icon = javax.imageio.ImageIO.read(
                getClass().getResource("/backpack.png")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // inventaire fermé -> on laisse le tir passer normalement
                if (!visible && fireListener != null) {
                    fireListener.mousePressed(e);
                }
            }
        });
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * FireListener basé sur le SwingInputController (tir vers la souris)
     */
    public void setFireListener(SwingInputController inputController){
        this.fireListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    inputController.handleFireAt(e.getX(), e.getY());
                }
            }
        };
    }

    public void setFireListener(MouseAdapter listener){
        this.fireListener = listener;
    }

    private void handleClick(int x, int y) {
        // inventaire fermé -> clic = tir normal
        if (!visible) {
            if (fireListener != null) {
                MouseEvent fake = new MouseEvent(
                        this,
                        MouseEvent.MOUSE_PRESSED,
                        System.currentTimeMillis(),
                        0,
                        x, y,
                        1,
                        false,
                        MouseEvent.BUTTON1
                );
                fireListener.mousePressed(fake);
            }
            return;
        }

        // inventaire ouvert
        // 1) clic dans le panneau -> sélection / équipement
        if (panelBounds != null && panelBounds.contains(x, y)) {
            for (int i = 0; i < itemBounds.size(); i++) {
                Rectangle r = itemBounds.get(i);
                if (r.contains(x, y)) {
                    selectedIndex = i;
                    equippedItem = inventory.get(i);

                    if (gameManager != null && "WEAPON".equals(equippedItem.objectType)) {
                        gameManager.setWeaponFromInventory(equippedItem.name);
                    }

                    System.out.println("[Inventaire] " +
                        (gameManager != null ? gameManager.getCurrentTeamName() : "?") +
                        " équipe " + equippedItem.name);

                    repaint();
                    return;
                }
            }
            return; // clic dans le panneau mais pas sur un item -> rien
        }

        // 2) clic en dehors du panneau -> tir avec l'item équipé
        if (equippedItem == null) {
            System.out.println("[Inventaire] Aucun objet équipé, tir ignoré.");
            return;
        }

        boolean ok = equippedItem.registerUseThisTurn();
        if (!ok) {
            System.out.println("[Inventaire] Plus d'utilisation possible pour " + equippedItem.name);
            return;
        }

        int restantesTour = equippedItem.getRemainingUseThisTurn();
        if (equippedItem.remainingUses == null) {
            System.out.println("[" +
                (gameManager != null ? gameManager.getCurrentTeamName() : "?") +
                "] tire avec " + equippedItem.name +
                " → " + restantesTour + " utilisation(s) restante(s) ce tour, munitions : infinies");
        } else {
            System.out.println("[" +
                (gameManager != null ? gameManager.getCurrentTeamName() : "?") +
                "] tire avec " + equippedItem.name +
                " → " + restantesTour + " utilisation(s) ce tour, " +
                equippedItem.remainingUses + " munition(s) restante(s)");
        }

        // Vérifier si c'est un gadget ou une arme
        if ("GADGET".equals(equippedItem.objectType)) {
            // Pour les gadgets, utiliser directement
            if (gameManager != null) {
                gameManager.useGadgetFromInventory(equippedItem.name);
                System.out.println("[Inventaire] Gadget utilisé : " + equippedItem.name);
            }
        } else {
            // Pour les armes, déléguer le tir au listener (SwingInputController -> handleFireAt)
            if (fireListener != null) {
                MouseEvent fake = new MouseEvent(
                        this,
                        MouseEvent.MOUSE_PRESSED,
                        System.currentTimeMillis(),
                        0,
                        x, y,
                        1,
                        false,
                        MouseEvent.BUTTON1
                );
                fireListener.mousePressed(fake);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        int w = getWidth();
        int h = getHeight();

        Graphics2D g2 = (Graphics2D) g;

        // icône sac
        int iconSize = 80;
        int iconX = w - iconSize - 16;
        int iconY = 16;

        iconBounds = new Rectangle(iconX, iconY, iconSize, iconSize);

        if(icon != null){
            g2.drawImage(icon,iconX,iconY,iconSize,iconSize,null);
        } else {
            g2.setColor(Color.GRAY);
            g2.fillRect(iconX,iconY, iconSize, iconSize);
            g2.setColor(Color.WHITE);
            g2.drawString("INV", iconX + 10, iconY + 28);
        }

        if(!visible){
            return;
        }

        // panneau inventaire à gauche
        int panelW = 250;
        int panelH = 400;
        int panelX = 20;
        int panelY = (h - panelH) / 2;

        panelBounds = new Rectangle(panelX, panelY, panelW, panelH);

        g2.setColor(new Color(40,40,40));
        g2.fillRoundRect(panelX, panelY, panelW, panelH, 20, 20);

        g2.setColor(Color.WHITE);
        g2.drawString("INVENTAIRE", panelX + 20, panelY + 30);

        int listX = panelX + 20;
        int listY = panelY + 50;
        int itemHeight = 30;

        itemBounds.clear();

        for (int i = 0; i < inventory.size(); i++) {
            int y = listY + i * itemHeight;

            Rectangle r = new Rectangle(listX, y - 20, panelW - 40, itemHeight);
            itemBounds.add(r);

            if (i == selectedIndex) {
                g2.setColor(new Color(80,80,120));
                g2.fill(r);
                g2.setColor(Color.WHITE);
            } else {
                g2.setColor(Color.WHITE);
            }

            InventoryJson item = inventory.get(i);
            String label = item.name + " (" + item.objectType + ") ";

            g2.drawString(label, listX + 5, y);
        }

        if (equippedItem != null) {
            g2.drawString("Equipé : " + equippedItem.name,
                          panelX + 20,
                          panelY + panelH - 20);
        }
    }

    public void setInventory(List<InventoryJson> inventory){
        this.inventory = inventory;
        selectedIndex = -1;
        equippedItem = null;
        itemBounds.clear();
        repaint();
    }
    public void setInventoryVisible(boolean visible){
        this.visible = visible;
        repaint();
    }
    
    public boolean isInventoryVisible(){
        return visible;
    }
}

package fr.upc.g5e4.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import fr.upc.g5e4.model.InventoryModel.InventoryJson;

public class InventoryOverlay extends JComponent{       //S'integre dans une interface Swing (JFrame)
    private final List<InventoryJson> inventory;
    private boolean visible = false;

    //Icone image png
    private BufferedImage icon;
    private Rectangle iconBounds; 
    
    //Zone Cliquable de l'inventaire
    private final java.util.List<Rectangle> itemBounds = new ArrayList<>();
    private int selectedIndex = -1;
    private InventoryJson equippedItem = null;

    //
    private Rectangle useButtonBounds;

    public InventoryOverlay(List<InventoryJson> inventory){
        this.inventory = inventory;
        setOpaque(false);                   // transparent

        //Chargement d'icon
        try{
            icon = javax.imageio.ImageIO.read(
                getClass().getResource("/backpack.png")
            );
        } catch(Exception e){
            e.printStackTrace();
        }

        //Clic souris
        addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                handleClick(e.getX(), e.getY());
            }
        });
    }

    private void handleClick(int x, int y){             //Si clic faire :
        if(iconBounds != null && iconBounds.contains(x,y)){
            setInventoryVisible(!visible);
            return;
        }
        if(!visible){
            return;
        }

        for(int i=0;i< itemBounds.size();i++){
            Rectangle r = itemBounds.get(i);
            if(r.contains(x,y)){
                selectedIndex= i;
                equippedItem = inventory.get(i);
                repaint();
                return;
            }
        }

        // clic sur le bouton Utiliser
        if (useButtonBounds != null && useButtonBounds.contains(x, y)) {
            handleUse();
            return;
        }
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        int w = getWidth();
        int h = getHeight();

        Graphics2D g2 = (Graphics2D) g;
        
        /* Image + Inventaire */
        int iconSize = 80;
        int iconX = w - iconSize -16;
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
        
        // Si on ne clique pas 
        if(!visible){
            return;
        }

        /* FOND SOMBRE */
        g2.setColor(new Color(0,0,0,150));
        g2.fillRect(0, 0, w, h);

        /* Overlay */
        int panelW = 300;
        int panelH = 200;
        int panelX = (w - panelW) /2;
        int panelY = (h - panelH) /2;

        g2.setColor(new Color(40,40,40));
        g2.fillRoundRect(panelX, panelY, panelW, panelH, 20, 20);

        g2.setColor(Color.WHITE);
        g2.drawString("INVENTAIRE", panelX +20, panelY +30);

        /* liste Item */
        int listX = panelX +20;
        int listY = panelY + 50;
        int itemHeight = 30;
        
        itemBounds.clear();

        for(int i=0; i< inventory.size();i++){
            int y = listY + i * itemHeight;

            Rectangle r = new Rectangle(listX, y-20, panelW -40, itemHeight);
            itemBounds.add(r);

            if(i == selectedIndex){
                g2.setColor(new Color(80,80,120));
                g2.fill(r);
                g2.setColor(Color.WHITE);
            } else {
                g2.setColor(Color.WHITE);
            }
            InventoryJson item = inventory.get(i);
            String label = item.name + " (" + item.objectType + ") ";

            g2.drawString(label, listX+5, y);
        }

        /*Affiche l'item équipé*/
        if(equippedItem != null){
            g2.drawString("Equipé : "+ equippedItem.name, panelX +20, panelY + panelH-20);
        }

        /* Bouton Utiliser*/
        int btnW = 100;
        int btnH = 30;
        int btnY = panelY + panelH - btnH - 10;

        useButtonBounds = new Rectangle(panelX + panelW - btnW - 20, btnY, btnW, btnH);

        // bouton Utiliser
        g2.setColor(new Color(100, 160, 100));
        g2.fillRect(useButtonBounds.x, useButtonBounds.y, useButtonBounds.width, useButtonBounds.height);
        g2.setColor(Color.WHITE);
        g2.drawString("Utiliser", useButtonBounds.x + 20, useButtonBounds.y + 20);

    }

    public void setInventoryVisible(boolean visible){
        this.visible = visible;
        repaint();
    }
    
    public boolean isInventoryVisible(){
        return visible;
    }

    private void handleUse() {
        if(selectedIndex < 0 || selectedIndex >= inventory.size()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Aucun objet sélectionné",
                    "Inventaire",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        InventoryJson selected = inventory.get(selectedIndex);

        boolean ok = selected.registerUseThisTurn();
        if(!ok) {
            JOptionPane.showMessageDialog(
                    this,
                    "Action impossible (plus d'utilisation ce tour ou plus de munitions)",
                    "Inventaire",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        //Log dans le terminal
        int restantesTour = selected.getRemainingUseThisTurn();
        if(selected.remainingUses == null){
            System.out.println("[Inventaire] " + selected.name + " : " + restantesTour + " utilisation(s) restante(s) ce tour, munitions globales : infinite");
        } else {
            System.out.println("[Inventaire] " + selected.name + " : " + restantesTour + " utilisation(s) restante(s) ce tour, "+ selected.remainingUses + " munition(s) globale(s) restante(s)");
        }

        //Met à jour l'affichage
        equippedItem = selected;
        repaint();
    }

}

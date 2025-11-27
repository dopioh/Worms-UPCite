package fr.upc.g5e4.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import fr.upc.g5e4.model.InventoryModel.InventoryJson;

public class InventoryOverlay extends JComponent{       //S'integre dans une interface Swing (JFrame)
    private final List<InventoryJson> inventory;
    private boolean visible = false;

    //Icone image png
    private BufferedImage icon;
    private Rectangle iconBounds; 

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

        int listX = panelX +20;
        int listY = panelY + 50;
        int itemHeight = 30;

        g2.setColor(new Color(40,40,40));
        g2.fillRoundRect(panelX, panelY, panelW, panelH, 20, 20);

        g2.setColor(Color.WHITE);
        for(int i=0;i< inventory.size();i++){
            int y = listY +i * itemHeight;

            InventoryJson item = inventory.get(i);
            String label = item.name + " (" + item.objectType + ") ";

            g2.drawString(label, listX, y);
        }

        g2.drawString("INVENTAIRE", panelX +20, panelY +30);
    }

    public void setInventoryVisible(boolean visible){
        this.visible = visible;
        repaint();
    }
    
    public boolean isInventoryVisible(){
        return visible;
    }
}

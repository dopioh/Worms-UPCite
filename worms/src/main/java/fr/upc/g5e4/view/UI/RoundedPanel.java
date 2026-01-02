package fr.upc.g5e4.view.UI;

import javax.swing.*;
import java.awt.*;

public class RoundedPanel extends JPanel{
    
    private int radius;

    public RoundedPanel(int radius){
        super();
        this.radius = radius;
        this.setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        
        // pour pas avoir d'effet pixeliser sur les bords arrondis
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // setup la couleur comme defini avec setBackground(new Color(165, 242, 190));
        g2.setColor(getBackground());

        // dessin du rectangle, en prenant en compte les angles
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), this.radius, this.radius);

        super.paintComponent(g);
    }
}

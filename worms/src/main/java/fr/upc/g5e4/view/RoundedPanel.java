package fr.upc.g5e4.view;

import java.awt.*;
import javax.swing.*;

/**
 * JPanel avec des coins arrondis (utilisés pour la boutique)
 */
public class RoundedPanel extends JPanel {
    
    private int cornerRadius;
    private Color borderColor = Color.BLACK;
    
    public RoundedPanel(int cornerRadius) {
        this.cornerRadius = cornerRadius;
        setOpaque(false);
    }
    
    public void setBorderColor(Color color) {
        this.borderColor = color;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        
        g2d.dispose();
    }
}

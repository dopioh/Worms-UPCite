package fr.upc.g5e4.view;

import javax.swing.*;
import java.awt.*;

/**
 * Barre de progression verte pixelisée sur fond noir pour DA console.
 */
public class PixelProgressBar extends JComponent {
    private int progress = 0; // 0 à 100
    private String message = "Chargement...";

    public PixelProgressBar() {
        setPreferredSize(new Dimension(400, 60));
    }

    public void setProgress(int value) {
        this.progress = Math.max(0, Math.min(100, value));
        repaint();
    }

    public void setMessage(String msg) {
        this.message = msg;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // Fond noir
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Bordure verte pixelisée
        g2.setColor(new Color(0, 255, 0));
        for (int i = 0; i < getWidth(); i += 4) {
            g2.fillRect(i, 0, 2, 4);
            g2.fillRect(i, getHeight() - 4, 2, 4);
        }
        for (int j = 0; j < getHeight(); j += 4) {
            g2.fillRect(0, j, 4, 2);
            g2.fillRect(getWidth() - 4, j, 4, 2);
        }

        // Barre de progression verte pixelisée
        int barWidth = getWidth() - 32;
        int barHeight = 20;
        int x = 16, y = getHeight() / 2 - barHeight / 2;
        g2.setColor(new Color(0, 255, 0));
        int filled = (int) (barWidth * (progress / 100.0));
        for (int i = 0; i < filled; i += 8) {
            g2.fillRect(x + i, y, 6, barHeight);
        }
        // Barre vide (contour)
        g2.setColor(new Color(0, 80, 0));
        g2.drawRect(x, y, barWidth, barHeight);

        // Texte en dessous
        g2.setFont(new Font("Consolas", Font.BOLD, 18));
        g2.setColor(new Color(0, 255, 0));
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(message);
        g2.drawString(message, (getWidth() - textWidth) / 2, y + barHeight + 28);
    }
}

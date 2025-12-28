package fr.upc.g5e4.view;

import javax.swing.*;
import java.awt.*;

/**
 * Barre de chargement verte pour l'introduction
 */
public class LoadingBarPanel extends JPanel {
    private int progress = 0; // 0 à 100
    private String message = "";
    private Color barColor = new Color(40, 220, 80); // vert 

    public LoadingBarPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(600, 80));
    }

    public void setProgress(int progress) {
        this.progress = Math.max(0, Math.min(100, progress));
        repaint();
    }

    public void setMessage(String message) {
        this.message = message;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fond arrondi
        g2d.setColor(new Color(255, 230, 180));
        g2d.fillRoundRect(20, 20, 560, 40, 40, 40);
        g2d.setColor(new Color(255, 180, 60));
        g2d.setStroke(new BasicStroke(4f));
        g2d.drawRoundRect(20, 20, 560, 40, 40, 40);

        // Remplissage de la barre
        int barWidth = (int) (5.6 * progress);
        g2d.setColor(barColor);
        g2d.fillRoundRect(20, 20, barWidth, 40, 40, 40);

        // Effet de brillance
        g2d.setColor(new Color(255, 255, 255, 80));
        g2d.fillRoundRect(20, 20, barWidth, 20, 40, 20);

        // Message centré
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 22));
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(message)) / 2;
        int y = 50;
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString(message, x, y);

        g2d.dispose();
    }

    public void setBarColor(Color color) {
        this.barColor = color;
        repaint();
    }
}

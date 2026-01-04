package fr.upc.g5e4.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HelpOverlay extends JComponent {
    private Rectangle closeButtonBounds;    // bouton FERMER

    public HelpOverlay() { // Constructeur
        setOpaque(false);
        setVisible(false); 

        addMouseListener(new MouseAdapter() {       // Gestion des clics souris
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isVisible()) return;

                if (closeButtonBounds != null && closeButtonBounds.contains(e.getPoint())) {
                    setHelpVisible(false);
                }
                // aucun autre clic ne fait rien
            }
        });
    }

    public void setHelpVisible(boolean v) {
        setVisible(v);  
        repaint();
    }

    public boolean isHelpVisible() {
        return isVisible();
    }

    @Override
    protected void paintComponent(Graphics g) {    // Dessin de l'overlay d'aide
        super.paintComponent(g);
        if (!isVisible()) return;
        if (!(g instanceof Graphics2D g2)) return; 

        int w = getWidth();
        int h = getHeight();

        g2.setColor(new Color(0, 0, 0, 220));
        g2.fillRect(0, 0, w, h);

        // Dimension du panneau
        int panelW = 620;       
        int panelH = 420;
        int x = (w - panelW) / 2;
        int y = (h - panelH) / 2;

        // Couleur et style du panneau
        g2.setColor(new Color(25, 25, 25, 235));
        g2.fillRoundRect(x, y, panelW, panelH, 18, 18);

        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20f));
        g2.drawString("HOW TO PLAY / RACCOURCIS", x + 20, y + 35);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 14f));
        int ty = y + 70;

        g2.drawString("Déplacement :", x + 20, ty); ty += 22;
        g2.drawString("Q/D = gauche/droite", x + 40, ty); ty += 18;
        g2.drawString("Z = sauter", x + 40, ty); ty += 24;

        g2.drawString("Caméra :", x + 20, ty); ty += 22;
        g2.drawString("Scroll Haut = zoom +", x + 40, ty); ty += 18;
        g2.drawString("Scroll Bas = zoom -", x + 40, ty); ty += 18;
        g2.drawString("Espace = centrer caméra", x + 40, ty); ty += 24;

        g2.drawString("Combat :", x + 20, ty); ty += 22;
        g2.drawString("Click gauche = tirer", x + 40, ty); ty += 18;

        // ----- Bouton FERMER -----
        int btnW = 120;
        int btnH = 34;
        int btnX = x + panelW - btnW - 20;
        int btnY = y + panelH - btnH - 20;

        closeButtonBounds = new Rectangle(btnX, btnY, btnW, btnH);
        
        // Style du bouton FERMER
        g2.setColor(new Color(180, 60, 60));
        g2.fillRoundRect(btnX, btnY, btnW, btnH, 12, 12);

        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));
        g2.drawString("FERMER", btnX + 28, btnY + 22);

    }

    @Override
    public boolean contains(int x, int y) { // Clics uniquement si l'overlay est visible
        return isHelpVisible() && super.contains(x, y);
    }  
}
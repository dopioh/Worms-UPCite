package fr.upc.g5e4.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HelpOverlay extends JComponent {

    public HelpOverlay() {
        setOpaque(false);
        setVisible(false); // IMPORTANT : invisible Swing => ne capte plus les clics

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // clic n'importe où pour fermer
                setHelpVisible(false);
            }
        });
    }

    public void setHelpVisible(boolean v) {
        setVisible(v);     // IMPORTANT
        repaint();
    }

    public boolean isHelpVisible() {
        return isVisible();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!isVisible()) return;
        if (!(g instanceof Graphics2D g2)) return;

        int w = getWidth();
        int h = getHeight();

        g2.setColor(new Color(0, 0, 0, 220));
        g2.fillRect(0, 0, w, h);

        int panelW = 620;
        int panelH = 420;
        int x = (w - panelW) / 2;
        int y = (h - panelH) / 2;

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
        g2.drawString("T/G = zoom + / zoom -", x + 40, ty); ty += 18;
        g2.drawString("SPACE = centrer caméra", x + 40, ty); ty += 24;

        g2.drawString("Combat :", x + 20, ty); ty += 22;
        g2.drawString("UP/DOWN = viser (angle)", x + 40, ty); ty += 18;
        g2.drawString("Click gauche = tirer", x + 40, ty); ty += 18;
        g2.drawString("E = changer d'arme (temp)", x + 40, ty); ty += 24;

        g2.drawString("Tour / Jeu :", x + 20, ty); ty += 22;
        g2.drawString("A = fin de tour / nextRound", x + 40, ty); ty += 18;
        g2.drawString("I = inventaire", x + 40, ty); ty += 18;
        g2.drawString("ESC = pause/paramètres", x + 40, ty); ty += 18;
    }
}

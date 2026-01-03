package fr.upc.g5e4.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
/**
 * Overlay affichant les commandes d'aide du jeu
 *  
 * @author Hugo
 * @version 1.0
 */
public class HelpOverlay extends JComponent {
    public interface Listener {
        void onCloseClicked(); // Si close est cliqué
    }
    private Rectangle closeButtonBounds;    // bouton FERMER
    private Listener listener;
    private boolean closeHovered = false;

    public HelpOverlay() { // Constructeur
        setOpaque(false);
        setVisible(false); 

        addMouseListener(new MouseAdapter() {       // Gestion des clics souris
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isVisible()) return;

                if (closeButtonBounds != null && closeButtonBounds.contains(e.getPoint())) {
                    if (listener != null) {
                        listener.onCloseClicked();
                        return;
                    }
                }
                // aucun autre clic ne fait rien
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!isVisible()) return;

                boolean newHover = closeButtonBounds != null && closeButtonBounds.contains(e.getPoint());
                if (newHover != closeHovered) {
                    closeHovered = newHover;
                    repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                if (closeHovered) {
                    closeHovered = false;
                    repaint();
                }
            }
        });

    }


    /**
     * Affiche ou masque l'overlay d'aide.
     *
     * @param v true pour afficher l'aide, false pour la masquer
     */
    public void setHelpVisible(boolean v) {
        setVisible(v);  
        repaint();
    }

    /**
     * Indique si l'overlay d'aide est visible.
     *
     * @return true si l'overlay est affiché
     */
    public boolean isHelpVisible() {
        return isVisible();
    }

    /**
     * Définit le listener recevant les événements de l'overlay d'aide.
     *
     * @param listener contrôleur 
     */
    public void setListener(Listener listener) {
        this.listener = listener;
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
        g2.drawString("Up et DOwn = zoom + / zoom -", x + 40, ty); ty += 18;
        g2.drawString("SPACE = centrer caméra", x + 40, ty); ty += 24;

        g2.drawString("Combat :", x + 20, ty); ty += 22;
        g2.drawString("UP/DOWN = viser (angle)", x + 40, ty); ty += 18;
        g2.drawString("Click gauche = tirer", x + 40, ty); ty += 18;
        g2.drawString("E = changer d'arme (temp)", x + 40, ty); ty += 24;
        
        // ----- Bouton FERMER -----
        int btnW = 120;
        int btnH = 34;
        int btnX = x + panelW - btnW - 20;
        int btnY = y + panelH - btnH - 20;

        closeButtonBounds = new Rectangle(btnX, btnY, btnW, btnH);
        
        // Style du bouton FERMER
        g2.setColor(closeHovered ? new Color(200, 80, 80) : new Color(180, 60, 60));
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
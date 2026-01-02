package fr.upc.g5e4.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Overlay affichant le menu de pause du jeu. (Paramètres, aide, etc.)
 * 
 * L'overlay intercepte les clics souris uniquement lorsqu'il est visible
 * {@link #contains(int, int)}
 * 
 * @author Hugo
 * @version 1.0
 */
public class PauseOverlay extends JComponent {

    /**
     * Listener permettant à la vue {@link PauseOverlay} de signaler des actions
     * au contrôleur 
     */
    public interface Listener {
        void onHelpClicked(); 
        void onVentClicked();
        void onSaveClicked();
    }

    private Listener listener;
    private boolean visible = false;
    private Rectangle helpButtonBounds;

    private Rectangle ventButtonBounds; // bouton placeholder
    private Rectangle saveButtonBounds; // bouton placeholder

    public PauseOverlay() {
        setOpaque(false);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!visible) return;

                if (helpButtonBounds != null && helpButtonBounds.contains(e.getPoint())) {
                    if (listener != null) listener.onHelpClicked();
                }
                else if (ventButtonBounds != null && ventButtonBounds.contains(e.getPoint())) {
                    if (listener != null) listener.onVentClicked();
                }
                else if (saveButtonBounds != null && saveButtonBounds.contains(e.getPoint())) {
                    if (listener != null) listener.onSaveClicked();
                }
            }
        });
    }


    /**
     * Associe un listener pour gérer les clics sur les boutons de l'overlay.
     * @param listener
     */
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /**
     * Affiche ou masque l'overlay de pause.
     *
     * @param v true pour afficher la pause, false pour la masquer
     */
    public void setPauseVisible(boolean v) {
        this.visible = v;
        repaint();
    }

    /**
     * Indique si l'overlay de pause est visible.
     *
     * @return true si l'overlay est affiché
     */
    public boolean isPauseVisible() {
        return visible;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!visible) return;
        if (!(g instanceof Graphics2D g2)) return;

        int w = getWidth();
        int h = getHeight();

        // Fond sombre
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(0, 0, w, h);

        // Panneau
        int panelW = 520;
        int panelH = 360;
        int x = (w - panelW) / 2;
        int y = (h - panelH) / 2;

        g2.setColor(new Color(35, 35, 35, 230));
        g2.fillRoundRect(x, y, panelW, panelH, 18, 18);

        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20f));
        g2.drawString("PAUSE / PARAMÈTRES", x + 20, y + 35);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 14f));
        int ty = y + 70;

        g2.drawString("Audio : (placeholder)", x + 20, ty); ty += 24;
        g2.drawString("Graphismes : (placeholder)", x + 20, ty); ty += 24;
        g2.drawString("Plein écran : F11", x + 20, ty); ty += 24;
        g2.drawString("Retour au jeu : ESC", x + 20, ty); ty += 24;

        ty += 10;
        g2.drawString("Autres touches : (à remplir plus tard)", x + 20, ty); ty += 24;
        g2.drawString("- ...", x + 40, ty); ty += 20;
        g2.drawString("- ...", x + 40, ty); ty += 20;

        int btnW = 140;
        int btnH = 34;
        int btnX = x + 20;
        int btnY = y + panelH - btnH - 20;

        helpButtonBounds = new Rectangle(btnX, btnY, btnW, btnH);

        g2.setColor(new Color(70, 120, 180));
        g2.fillRoundRect(btnX, btnY, btnW, btnH, 12, 12);

        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));
        g2.drawString("HELP", btnX + 45, btnY + 22);
        
        // bouton placeholder (à droite du HELP)
        int ventX = btnX + btnW + 16;
        int ventY = btnY;

        ventButtonBounds = new Rectangle(ventX, ventY, btnW, btnH);

        g2.setColor(new Color(90, 90, 90)); // gris
        g2.fillRoundRect(ventX, ventY, btnW, btnH, 12, 12);

        g2.setColor(Color.WHITE);
        g2.drawRoundRect(ventX, ventY, btnW, btnH, 12, 12);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));
        g2.drawString("Vent", ventX + 35, ventY + 22);

        // bouton placeholder (à droite du HELP)

        int saveX = btnX + btnW*2+ 32;
        int saveY = btnY;
        saveButtonBounds = new Rectangle(saveX, saveY, btnW, btnH);

        g2.setColor(new Color(90, 90, 90)); // gris
        g2.fillRoundRect(saveX, saveY, btnW, btnH, 12, 12);

        g2.setColor(Color.WHITE);
        g2.drawRoundRect(saveX, saveY, btnW, btnH, 12, 12);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));
        g2.drawString("Save", saveX + 35, saveY + 22);
    }

    //Garanti que le clic ne soit pris en compte que si l'overlay est visible
    @Override
    public boolean contains(int x, int y) {
        return visible && super.contains(x, y);
    }
}
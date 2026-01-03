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
        void onToggleVent(boolean enabled);
        void onSaveClicked();
        void onToggleFlyMode(boolean enabled);
        void onToggleBlueprint(boolean enabled);
        void onQuitClicked();
        void onCloseClicked();
    }

    private Listener listener;
    private boolean visible = false;

    private Rectangle closeButtonBounds;

    private Rectangle toggleFlyBounds;
    private Rectangle toggleBlueprintBounds;
    private Rectangle toggleVentBounds;
    private Rectangle btnHelpBounds;
    private Rectangle btnSaveBounds;
    private Rectangle btnQuitBounds; 

    private enum Hover { NONE, TOGGLE_FLY, TOGGLE_BLUEPRINT, TOGGLE_VENT, HELP, SAVE, QUIT, CLOSE }
    private Hover hovered = Hover.NONE;

    private boolean flyModeOn = false;
    private boolean blueprintOn = false;
    private boolean ventOn = true;

    public PauseOverlay() {
        setOpaque(false);

        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!visible) return;

                Hover newHover = hitTest(e.getPoint());
                if (newHover != hovered) {
                    hovered = newHover;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (hovered != Hover.NONE) {
                    hovered = Hover.NONE;
                    repaint();
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (!visible) return;
                Point p = e.getPoint();
                if (closeButtonBounds != null && closeButtonBounds.contains(p)) {
                    if (listener != null) listener.onCloseClicked();
                    return;
                }

                if (toggleFlyBounds != null && toggleFlyBounds.contains(p)) {
                    flyModeOn = !flyModeOn;
                    if (listener != null) listener.onToggleFlyMode(flyModeOn);
                    repaint();
                    return;
                }

                if(toggleVentBounds != null && toggleVentBounds.contains(p)){
                    ventOn = !ventOn;
                    if(listener!=null) listener.onToggleVent(ventOn);
                    repaint();
                    return;
                }

                if (toggleBlueprintBounds != null && toggleBlueprintBounds.contains(p)) {
                    blueprintOn = !blueprintOn;
                    if (listener != null) listener.onToggleBlueprint(blueprintOn);
                    repaint();
                    return;
                }

                if (btnHelpBounds != null && btnHelpBounds.contains(p)) {
                    if (listener != null) listener.onHelpClicked();
                    return;
                }

                if (btnSaveBounds != null && btnSaveBounds.contains(p)) {
                    if (listener != null) listener.onSaveClicked();
                    return;
                }

                if (btnQuitBounds != null && btnQuitBounds.contains(p)) {
                    if (listener != null) listener.onQuitClicked();
                }
            }
        };
        addMouseMotionListener(mouse);
        addMouseListener(mouse);
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
        
        // fond sombre
        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRect(0, 0, w, h);

        // panneau
        int panelW = 560;
        int panelH = 420;
        int x = (w - panelW) / 2;
        int y = (h - panelH) / 2;

        int closeSize = 26;
        int closeX = x + panelW - closeSize - 12;
        int closeY = y + 10;
        

        g2.setColor(new Color(245, 245, 245, 235));
        g2.fillRoundRect(x, y, panelW, panelH, 22, 22);

        g2.setColor(new Color(0, 0, 0, 80));
        g2.drawRoundRect(x, y, panelW, panelH, 22, 22);

        closeButtonBounds = new Rectangle(closeX, closeY, closeSize, closeSize);

        g2.setColor(hovered == Hover.CLOSE ? new Color(200, 80, 80) : new Color(180, 60, 60));
        g2.fillRoundRect(closeX, closeY, closeSize, closeSize, 8, 8);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2.2f));
        g2.drawLine(closeX + 7, closeY + 7, closeX + closeSize - 7, closeY + closeSize - 7);
        g2.drawLine(closeX + closeSize - 7, closeY + 7, closeX + 7, closeY + closeSize - 7);

        g2.setStroke(new BasicStroke(1f));
        // titre
        g2.setColor(new Color(20, 20, 20));
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 22f));
        g2.drawString("Paramètres", x + 26, y + 40);

        // séparateur
        g2.setColor(new Color(0, 0, 0, 35));
        g2.drawLine(x + 20, y + 58, x + panelW - 20, y + 58);

        // zone options
        int rowX = x + 26;
        int rowW = panelW - 52;
        int rowH = 60;

        int row1Y = y + 80;
        int row2Y = y + 150;
        int row3Y = y + 220;

        drawToggleRow(g2,"Fly mode","Permet de voler",rowX, row1Y, rowW, rowH,flyModeOn,hovered == Hover.TOGGLE_FLY);
        drawToggleRow(g2,"Blueprint","Affiche le mode Blueprint",rowX, row2Y, rowW, rowH,blueprintOn,hovered == Hover.TOGGLE_BLUEPRINT);
        drawToggleRow(g2,"Vent","Active/Désactive le vent",rowX, row3Y, rowW, rowH,ventOn,hovered == Hover.TOGGLE_VENT);

        // boutons en bas
        int btnY = y + panelH - 70;
        int btnH = 42;
        int gap = 12;

        int btnW = (rowW - 2 * gap) / 3;
        int b1X = rowX;
        int b2X = rowX + btnW + gap;
        int b3X = rowX + (btnW + gap) * 2;

        btnHelpBounds = new Rectangle(b1X, btnY, btnW, btnH);
        btnSaveBounds = new Rectangle(b2X, btnY, btnW, btnH);
        btnQuitBounds = new Rectangle(b3X, btnY, btnW, btnH);

        drawButton(g2, btnHelpBounds, "Help", hovered == Hover.HELP, new Color(80, 140, 210));
        drawButton(g2, btnSaveBounds, "Save", hovered == Hover.SAVE, new Color(90, 90, 90));
        drawButton(g2, btnQuitBounds, "Quitter", hovered == Hover.QUIT, new Color(200, 70, 70));
    }

    private void drawToggleRow(Graphics2D g2,String title, String desc,int x, int y, int w, int h,boolean on,boolean hover) {

        g2.setColor(hover ? new Color(255, 255, 255) : new Color(250, 250, 250));
        g2.fillRoundRect(x, y, w, h, 18, 18);

        g2.setColor(new Color(0, 0, 0, 40));
        g2.drawRoundRect(x, y, w, h, 18, 18);

        // texte
        g2.setColor(new Color(15, 15, 15));
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 15f));
        g2.drawString(title, x + 16, y + 24);

        g2.setColor(new Color(0, 0, 0, 140));
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 12f));
        g2.drawString(desc, x + 16, y + 44);

        // switch à droite
        int swW = 76;
        int swH = 30;
        int swX = x + w - swW - 16;
        int swY = y + (h - swH) / 2;

        g2.setColor(on ? new Color(80, 170, 110) : new Color(170, 170, 170));
        g2.fillRoundRect(swX, swY, swW, swH, 18, 18);

        g2.setColor(new Color(0, 0, 0, 60));
        g2.drawRoundRect(swX, swY, swW, swH, 18, 18);

        // knob
        int knob = 26;
        int kX = on ? (swX + swW - knob - 3) : (swX + 3);
        int kY = swY + 2;

        g2.setColor(Color.WHITE);
        g2.fillOval(kX, kY, knob, knob);
        g2.setColor(new Color(0, 0, 0, 50));
        g2.drawOval(kX, kY, knob, knob);

        if ("Fly mode".equals(title)) toggleFlyBounds = new Rectangle(x, y, w, h);
        if ("Blueprint".equals(title)) toggleBlueprintBounds = new Rectangle(x, y, w, h);
        if ("Vent".equals(title)) toggleVentBounds = new Rectangle(x, y, w, h);
    }

    private void drawButton(Graphics2D g2, Rectangle r, String text, boolean hover, Color base) {
        Color fill = hover ? new Color(Math.min(255, base.getRed() + 20),Math.min(255, base.getGreen() + 20), Math.min(255, base.getBlue() + 20),230) : new Color(base.getRed(), base.getGreen(), base.getBlue(), 210);

        g2.setColor(fill);
        g2.fillRoundRect(r.x, r.y, r.width, r.height, 16, 16);

        g2.setColor(new Color(0, 0, 0, 80));
        g2.drawRoundRect(r.x, r.y, r.width, r.height, 16, 16);

        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));
        FontMetrics fm = g2.getFontMetrics();
        int tx = r.x + (r.width - fm.stringWidth(text)) / 2;
        int ty = r.y + (r.height + fm.getAscent()) / 2 - 2;
        g2.drawString(text, tx, ty);
    }

    private Hover hitTest(Point p) {
        if (closeButtonBounds != null && closeButtonBounds.contains(p)) return Hover.CLOSE;
        if (toggleFlyBounds != null && toggleFlyBounds.contains(p)) return Hover.TOGGLE_FLY;
        if (toggleBlueprintBounds != null && toggleBlueprintBounds.contains(p)) return Hover.TOGGLE_BLUEPRINT;
        if (toggleVentBounds != null && toggleVentBounds.contains(p)) return Hover.TOGGLE_VENT;

        if (btnHelpBounds != null && btnHelpBounds.contains(p)) return Hover.HELP;
        if (btnSaveBounds != null && btnSaveBounds.contains(p)) return Hover.SAVE;
        if (btnQuitBounds != null && btnQuitBounds.contains(p)) return Hover.QUIT;

        return Hover.NONE;
    }

    //Garanti que le clic ne soit pris en compte que si l'overlay est visible
    @Override
    public boolean contains(int x, int y) {
        return visible && super.contains(x, y);
    }
}
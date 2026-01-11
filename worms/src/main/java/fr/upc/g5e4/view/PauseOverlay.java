package fr.upc.g5e4.view;

import fr.upc.g5e4.model.SoundPlayer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Overlay affichant le menu de pause du jeu (Paramètres, aide, etc.).
 * L'overlay "écoute" les mouvements et clics de la souris pour gérer les interactions
 *
 * @author Hugo
 * @version 1.0
 */
public class PauseOverlay extends JComponent {

    /**
     * Listener permettant à la vue {@link PauseOverlay} de signaler des actions
     * au contrôleur.
     */
    public interface Listener {
        void onHelpClicked();
        void onToggleVent(boolean enabled);
        void onSaveClicked();
        void onFlyModePressed(); 
        void onToggleBlueprint(boolean enabled);
        void onToggleFullScreen(boolean enabled);
        void onToggleSound(boolean enabled);
        void onQuitClicked();
        void onCloseClicked();
        void onToggleTeamKill(boolean enabled);
    }

    private Listener listener;
    private boolean visible = false;

    private Rectangle closeButtonBounds;

    private Rectangle flyActionBounds;

    private Rectangle toggleBlueprintBounds;
    private Rectangle toggleVentBounds;
    private Rectangle toggleFullScreenBounds;
    private Rectangle toggleSoundBounds;
    private Rectangle toggleTeamKillBounds;

    private Rectangle btnHelpBounds;
    private Rectangle btnSaveBounds;
    private Rectangle btnQuitBounds;

    private enum Hover {NONE,ACTION_FLY, TOGGLE_BLUEPRINT, TOGGLE_VENT, TOGGLE_TEAMKILL, TOGGLE_FULLSCREEN, TOGGLE_SOUND ,HELP, SAVE, QUIT, CLOSE}

    private float flyAnim = 0f; 
    private Timer flyAnimTimer;

    private Hover hovered = Hover.NONE;

    private boolean blueprintOn = false;
    private boolean ventOn = true;
    private boolean fullScreenOn = false;
    private boolean soundOn = true;
    private boolean teamKillOn = false;
    
    private int scrollY = 0;
    private int maxScrollY = 0;
    private static final int SCROLL_STEP = 24;

    public PauseOverlay() {
        setOpaque(false);

        MouseAdapter mouse = new MouseAdapter(){
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
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (!visible) return;
                if (maxScrollY <= 0) return;

                int notches = e.getWheelRotation();
                scrollY += notches * SCROLL_STEP;
                scrollY = clamp(scrollY, 0, maxScrollY);

                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (!visible) return;

                Point p = e.getPoint();
                requestFocusInWindow();

                if (closeButtonBounds != null && closeButtonBounds.contains(p)) {
                    if (listener != null) listener.onCloseClicked();
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

                if (flyActionBounds != null && flyActionBounds.contains(p)) {
                    if (listener != null) listener.onFlyModePressed();
                    startFlyAnimation();
                    repaint();
                    return;
                }

                if (toggleBlueprintBounds != null && toggleBlueprintBounds.contains(p)) {
                    blueprintOn = !blueprintOn;
                    if (listener != null) listener.onToggleBlueprint(blueprintOn);
                    repaint();
                    return;
                }

                if (toggleVentBounds != null && toggleVentBounds.contains(p)) {
                    ventOn = !ventOn;
                    if (listener != null) listener.onToggleVent(ventOn);
                    repaint();
                    return;
                }

                if (toggleFullScreenBounds != null && toggleFullScreenBounds.contains(p)) {
                    fullScreenOn = !fullScreenOn;
                    if (listener != null) listener.onToggleFullScreen(fullScreenOn);
                    repaint();
                    return;
                }

                if (toggleSoundBounds != null && toggleSoundBounds.contains(p)) {
                    soundOn = !soundOn;
                    if (listener != null) listener.onToggleSound(soundOn);
                    repaint();
                    return;
                }

                if (toggleTeamKillBounds != null && toggleTeamKillBounds.contains(p)) {
                    teamKillOn = !teamKillOn;
                    if (listener != null) listener.onToggleTeamKill(teamKillOn);
                    repaint();
                    return;
                }
            }
        };

        addMouseMotionListener(mouse);
        addMouseListener(mouse);
        addMouseWheelListener(mouse);
    }

    /**
     * Associe un listener pour gérer les clics sur les boutons de l'overlay.
     * @param listener listener
     */
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /**
     * Affiche ou masque l'overlay de pause. 
     * @param v true pour afficher la pause, false pour la masquer
     */
    public void setPauseVisible(boolean v) {
        this.visible = v;
        if (!v) {
            hovered = Hover.NONE;
            scrollY = 0;
        }
        repaint();
    }

    /**
     * @return true si l'overlay est affiché
     */
    public boolean isPauseVisible() {
        return visible;
    }

    /**
     * Active le plein écran depuis le contrôleur de fenêtre
     */
    public void setFullScreenOn(boolean fullScreenOn) {
        this.fullScreenOn = fullScreenOn;
        repaint();
    }

    /**
     * Active le son depuis SoundPlayer
     */
    public void setSoundOn(boolean soundOn) {
        this.soundOn = soundOn;
        repaint();
    }

    /**
     * Permet de synchroniser l'état du toggle vent depuis l'extérieur.
     */
    public void setVentOn(boolean ventOn) {
        this.ventOn = ventOn;
        repaint();
    }

    /**
     * Permet de synchroniser l'état du toggle TeamKill depuis l'extérieur.
     */
    public void setTeamKillOn(boolean teamKillOn) {
        this.teamKillOn = teamKillOn;
        repaint();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!visible) return;
        if (!(g instanceof Graphics2D g2)) return;

        //Plus beau rendu
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

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

        // header/footer 
        int headerH = 70;
        int footerH = 86; 
        int contentX = x + 26;
        int contentW = panelW - 52;

        // zone scrollable 
        int contentY = y + headerH;
        int contentH = panelH - headerH - footerH;

        int closeSize = 26;
        int closeX = x + panelW - closeSize - 12;
        int closeY = y + 10;

        // panneau blanc
        g2.setColor(new Color(245, 245, 245, 235));
        g2.fillRoundRect(x, y, panelW, panelH, 22, 22);

        g2.setColor(new Color(0, 0, 0, 80));
        g2.drawRoundRect(x, y, panelW, panelH, 22, 22);

        // close
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

        //Contenu dans la zone de scroll
        int rowH = 60;
        int gapY = 10;

        int totalRows = 6;
        int virtualContentHeight = totalRows * rowH + (totalRows - 1) * gapY;

        maxScrollY = Math.max(0, virtualContentHeight - contentH);
        scrollY = clamp(scrollY, 0, maxScrollY);

        // Clip + translate uniquement sur la zone scrollable
        Shape oldClip = g2.getClip();
        g2.setClip(new Rectangle(contentX, contentY, contentW, contentH));
        g2.translate(0, -scrollY);

        int row1Y = contentY;
        int row2Y = row1Y + rowH + gapY;
        int row3Y = row2Y + rowH + gapY;
        int row4Y = row3Y + rowH + gapY;
        int row5Y = row4Y + rowH + gapY;
        int row6Y = row5Y + rowH + gapY;


        drawActionRow(g2,"Fly mode","Active/désactive le vol pour le worm actif",contentX, row1Y, contentW, rowH,hovered == Hover.ACTION_FLY,Hover.ACTION_FLY,flyAnim);
        drawToggleRow(g2, "Blueprint", "Affiche le mode Blueprint",contentX, row2Y, contentW, rowH,blueprintOn, hovered == Hover.TOGGLE_BLUEPRINT,Hover.TOGGLE_BLUEPRINT);
        drawToggleRow(g2, "Vent", "Active/Désactive le vent (reduit les FPS)",contentX, row3Y, contentW, rowH,ventOn, hovered == Hover.TOGGLE_VENT,Hover.TOGGLE_VENT);
        drawToggleRow(g2, "Plein écran", "Mode plein écran",contentX, row4Y, contentW, rowH,fullScreenOn, hovered == Hover.TOGGLE_FULLSCREEN,Hover.TOGGLE_FULLSCREEN);
        drawToggleRow(g2, "Son", "Active/Désactive les SFX",contentX, row5Y, contentW, rowH,soundOn, hovered == Hover.TOGGLE_SOUND,Hover.TOGGLE_SOUND);
        drawToggleRow(g2, "TeamKill", "Autorise les dégats entre alliés", contentX, row6Y, contentW, rowH, teamKillOn,hovered == Hover.TOGGLE_TEAMKILL, Hover.TOGGLE_TEAMKILL);

        // reset translate et clip
        g2.translate(0, scrollY);
        g2.setClip(oldClip);

        // footer 
        int btnY = y + panelH - 70;
        int btnH = 42;
        int gap = 12;

        int btnW = (contentW - 2 * gap) / 3;
        int b1X = contentX;
        int b2X = contentX + btnW + gap;
        int b3X = contentX + (btnW + gap) * 2;

        btnHelpBounds = new Rectangle(b1X, btnY, btnW, btnH);
        btnSaveBounds = new Rectangle(b2X, btnY, btnW, btnH);
        btnQuitBounds = new Rectangle(b3X, btnY, btnW, btnH);

        drawButton(g2, btnHelpBounds, "Help", hovered == Hover.HELP, new Color(80, 140, 210));
        drawButton(g2, btnSaveBounds, "Save", hovered == Hover.SAVE, new Color(90, 90, 90));
        drawButton(g2, btnQuitBounds, "Quitter", hovered == Hover.QUIT, new Color(200, 70, 70));

        // Indicateur de scroll 
        if (maxScrollY > 0) {
            drawScrollIndicator(g2, x, y, panelW, panelH, contentY, contentH);
        }
    }

    /**
     * Dessine une ligne de toggle.
     *
     * @param hoverId permet de savoir quel toggle on dessine (pour bounds + hover)
     */
    @SuppressWarnings("incomplete-switch") // car on ne gère que les cas utile
    private void drawToggleRow(Graphics2D g2, String title, String desc,int x, int y, int w, int h,boolean on, boolean hover,Hover hoverId) {

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

        // switch 
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

        Rectangle screenRect = new Rectangle(x, y - scrollY, w, h);

        switch (hoverId) {
            case TOGGLE_BLUEPRINT -> toggleBlueprintBounds = screenRect;
            case TOGGLE_VENT -> toggleVentBounds = screenRect;
            case TOGGLE_FULLSCREEN -> toggleFullScreenBounds = screenRect;
            case TOGGLE_SOUND -> toggleSoundBounds = screenRect;
            case TOGGLE_TEAMKILL -> toggleTeamKillBounds = screenRect;
        }
    }

    /**
     * Dessine un bouton rectangulaire arrondi.
     */
    private void drawButton(Graphics2D g2, Rectangle r, String text, boolean hover, Color base) {
        Color fill = hover
                ? new Color(Math.min(255, base.getRed() + 20), Math.min(255, base.getGreen() + 20), Math.min(255, base.getBlue() + 20), 230)
                : new Color(base.getRed(), base.getGreen(), base.getBlue(), 210);

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

    /**
     * Petit indicateur à droite pour montrer qu'on peut scroller
     */
    private void drawScrollIndicator(Graphics2D g2, int panelX, int panelY, int panelW, int panelH,int contentY, int contentH){
        int trackW = 6;
        int trackX = panelX + panelW - 12;
        int trackY = contentY;
        int trackH = contentH;

        g2.setColor(new Color(0, 0, 0, 30));
        g2.fillRoundRect(trackX, trackY, trackW, trackH, 8, 8);

        float ratio = (maxScrollY == 0) ? 0f : (scrollY / (float) maxScrollY);
        int thumbH = Math.max(24, (int) (trackH * (contentH / (float) (contentH + maxScrollY))));
        int thumbY = trackY + (int) ((trackH - thumbH) * ratio);

        g2.setColor(new Color(0, 0, 0, 90));
        g2.fillRoundRect(trackX, thumbY, trackW, thumbH, 8, 8);
    }

    /**
     * Détecte quel élément est survolé par la souris.
     */
    private Hover hitTest(Point p) {
        if (closeButtonBounds != null && closeButtonBounds.contains(p)) return Hover.CLOSE;

        if (btnHelpBounds != null && btnHelpBounds.contains(p)) return Hover.HELP;
        if (btnSaveBounds != null && btnSaveBounds.contains(p)) return Hover.SAVE;
        if (btnQuitBounds != null && btnQuitBounds.contains(p)) return Hover.QUIT;

        if (flyActionBounds != null && flyActionBounds.contains(p)) return Hover.ACTION_FLY;
        if (toggleBlueprintBounds != null && toggleBlueprintBounds.contains(p)) return Hover.TOGGLE_BLUEPRINT;
        if (toggleVentBounds != null && toggleVentBounds.contains(p)) return Hover.TOGGLE_VENT;
        if (toggleFullScreenBounds != null && toggleFullScreenBounds.contains(p)) return Hover.TOGGLE_FULLSCREEN;
        if (toggleSoundBounds != null && toggleSoundBounds.contains(p)) return Hover.TOGGLE_SOUND;
        if (toggleTeamKillBounds != null && toggleTeamKillBounds.contains(p)) return Hover.TOGGLE_TEAMKILL;

        return Hover.NONE;
    }

    /**
     * Garanti que le clic ne soit pris en compte que si l'overlay est visible.
     */
    @Override
    public boolean contains(int x, int y) {
        return visible && super.contains(x, y);
    }

    /* Assure qu'une action 'v' est bien entre min et max*/
    private static int clamp(int v, int min, int max) {
        if (v < min) return min;
        if (v > max) return max;
        return v;
    }

    private void drawActionRow(Graphics2D g2,String title, String desc,int x, int y, int w, int h,boolean hover, Hover hoverId,float anim){
        // fond
        g2.setColor(hover ? new Color(255, 255, 255) : new Color(250, 250, 250));
        g2.fillRoundRect(x, y, w, h, 18, 18);

        // pulse 
        if (anim > 0f) {
            int a = (int) (60 * anim); // alpha
            g2.setColor(new Color(80, 170, 110, a));
            g2.fillRoundRect(x, y, w, h, 18, 18);
        }

        g2.setColor(new Color(0, 0, 0, 40));
        g2.drawRoundRect(x, y, w, h, 18, 18);

        // texte
        g2.setColor(new Color(15, 15, 15));
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 15f));
        g2.drawString(title, x + 16, y + 24);

        g2.setColor(new Color(0, 0, 0, 140));
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 12f));
        g2.drawString(desc, x + 16, y + 44);

        // bounds en coords écran 
        Rectangle screenRect = new Rectangle(x, y - scrollY, w, h);
        if (hoverId == Hover.ACTION_FLY) {
            flyActionBounds = screenRect;
        }
    }

    /**
     * Démarre l'animation de pulse pour le bouton Fly Mode
     */
    private void startFlyAnimation() {
        flyAnim = 1f;

        if (flyAnimTimer != null && flyAnimTimer.isRunning()) {
            flyAnimTimer.stop();
        }

        flyAnimTimer = new Timer(16, e -> {
            flyAnim -= 0.07f;
            if (flyAnim <= 0f) {
                flyAnim = 0f;
                ((Timer) e.getSource()).stop();
            }
            repaint();
        });
        flyAnimTimer.start();
    }
}
package fr.upc.g5e4.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PauseOverlay extends JComponent {
    private boolean visible = false;
    private Rectangle helpButtonBounds;
    private HelpOverlay helpOverlay;

    private Rectangle ventButtonBounds; // bouton placeholder
    private Rectangle saveButtonBounds; // bouton placeholder

    public PauseOverlay() {
        setOpaque(false);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!visible) return;

                // Clic uniquement sur le bouton HELP
                if (helpButtonBounds != null && helpButtonBounds.contains(e.getPoint())) {
                    if (helpOverlay != null) {
                        boolean v = helpOverlay.isHelpVisible();
                        helpOverlay.setHelpVisible(!v);

                        // Remonter Help au-dessus 
                        Container parent = helpOverlay.getParent();
                        if (parent instanceof JLayeredPane lp) {
                            lp.moveToFront(helpOverlay);
                            lp.repaint();
                        }
                    }
                }
                if (ventButtonBounds != null && ventButtonBounds.contains(e.getPoint())) {                       // A changer par l'activation du vent MEIIII
                    System.out.println("[PauseOverlay] Bouton vent placeholder cliqué !");
                    return; 
                }
                if (saveButtonBounds != null && saveButtonBounds.contains(e.getPoint())) {                       // A changer par la sauvegarde MEIIII
                    System.out.println("[PauseOverlay] Bouton save placeholder cliqué !");
                    return; 
                }
            }
        });
    }

    public void setPauseVisible(boolean v) {
        this.visible = v;
        repaint();
    }


    public void setHelpOverlay(HelpOverlay helpOverlay) {
        this.helpOverlay = helpOverlay;
    }


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
        int footerH = 70; 
        int x = (w - panelW) / 2;
        int y = (h - panelH) / 2;

        g2.setColor(new Color(35, 35, 35, 230));
        g2.fillRoundRect(x, y, panelW, panelH, 18, 18);

        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20f));
        g2.drawString("PAUSE / PARAMÈTRES", x + 20, y + 35);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 14f));
        int ty = y + 70;
        //if (ty > y + panelH - footerH) return;                                En cas de debordement

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

    @Override
    public boolean contains(int x, int y) {
        // Quand la pause n'est pas visible, on laisse passer la souris aux overlays dessous
        return visible && super.contains(x, y);
    }
}
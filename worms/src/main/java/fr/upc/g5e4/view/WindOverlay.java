package fr.upc.g5e4.view;

import fr.upc.g5e4.model.WindModel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Overlay affichant la direction et la force du vent dans le jeu. @see WindModel.java
 *
 * On y trouve une flèche indiquant la direction du vent, une barre de force, ainsi que des particules animées
 *
 * L'animation est mise à jour via la méthode {@link #update(float)} appelée depuis la boucle principale du jeu.
 * @author Hugo
 * @version 1.0
 */
public class WindOverlay extends JComponent {
    private final WindModel wind;       // modèle du vent
    private final List<Particle> particles = new ArrayList<>(); // Particules de vent
    private final Random rng = new Random();

    // Pré-création des objets graphiques pour éviter les allocations à chaque frame + access facile aux couleurs
    private static final Color BG_COLOR = new Color(0, 0, 0, 120);
    private static final Color TEXT_COLOR = new Color(255, 255, 255, 220);
    private static final Color ARROW_COLOR_BASE = new Color(200, 230, 255);
    private static final Color BAR_BG = new Color(255, 255, 255, 60);
    private static final Color BAR_FG = new Color(120, 200, 255, 200);
    private static final BasicStroke ARROW_STROKE = new BasicStroke(3.0f);
    private final Rectangle clipRect = new Rectangle();

    public WindOverlay(WindModel wind) {
        this.wind = wind;
        setOpaque(false);

        // crée quelques particules
        for (int i = 0; i < 30; i++) {
            particles.add(spawnParticle());
        }
    }

    // Génère une particule de vent avec des propriétés aléatoires
    private Particle spawnParticle() {
        Particle p = new Particle();
        p.x = rng.nextFloat();
        p.y = rng.nextFloat();
        p.speed = 0.15f + rng.nextFloat() * 0.35f; // vitesse base
        p.size = 1.5f + rng.nextFloat() * 2.5f;
        p.alpha = 0.2f + rng.nextFloat() * 0.35f;
        return p;
    }

    /**
     * Méthode publique pour animer le vent depuis GameLoop
     * @param dt delta time en secondes
     */
    public void update(float dt) {
        tick(dt);
        repaint();
    }

    // Met à jour uniquement l'état interne de la vue (particules + phase)
    private void tick(float dt) {

        // direction normalisée (dx,dy dans {-1,0,1})
        WindModel.Dir d = wind.getDir();
        float strength = wind.getStrength();

        // vitesse réelle : discrète mais visible
        float vx = d.dx * (0.20f + 1.2f * strength);
        float vy = d.dy * (0.20f + 1.2f * strength);

        // bouge les particules en boucle
        for (Particle p : particles) {
            p.x += vx * p.speed * dt;
            p.y += vy * p.speed * dt;

            // wrap (revient de l’autre côté)
            if (p.x < -0.1f) p.x = 1.1f;
            if (p.x > 1.1f) p.x = -0.1f;
            if (p.y < -0.1f) p.y = 1.1f;
            if (p.y > 1.1f) p.y = -0.1f;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (!(g instanceof Graphics2D g2)) return;

        //Text et formes plus lisses
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // --- widget position
        int boxW = 180;
        int boxH = 60;

        // marge par rapport au bord
        int margin = 16;

        // bas droite
        int x = w - boxW - margin;
        int y = h - boxH - margin;

        // fond semi-transparent
        g2.setColor(BG_COLOR);
        g2.fillRoundRect(x, y, boxW, boxH, 16, 16);

        // titre
        g2.setColor(TEXT_COLOR);
        g2.drawString("VENT", x + 12, y + 18);

        // flèche + label direction
        WindModel.Dir dir = wind.getDir();
        float strength = wind.getStrength();

        // Centre de la zone de la flèche
        int arrowAreaX = x + 15;
        int arrowAreaY = y + 22;
        int arrowAreaW = 80;
        int arrowAreaH = 35;

        int cx = arrowAreaX + arrowAreaW / 2;
        int cy = arrowAreaY + arrowAreaH / 2;

        // direction en pixels (dx,dy)
        int dx = dir.dx;
        int dy = dir.dy;

        // normalisation simple pour diagonales/axes
        double norm = Math.sqrt(dx * dx + dy * dy);
        double ux = (norm == 0) ? 1 : dx / norm;
        double uy = (norm == 0) ? 0 : dy / norm;

        int len = 36;      // longueur de la flèche
        int headLen = 8;   // longueur de la tête

        // demi-longueur utile en retirant la tête (sinon ça dépasse)
        int half = (len / 2);

        // Point de départ et point d'arrivée : centrés autour de (cx,cy)
        int x1 = cx - (int) (ux * half);
        int y1 = cy - (int) (uy * half);
        int x2 = cx + (int) (ux * half);
        int y2 = cy + (int) (uy * half);

        g2.setStroke(ARROW_STROKE);
        g2.setColor(ARROW_COLOR_BASE);
        g2.drawLine(x1, y1, x2, y2);

        // tête uniquement à l'extrémité "avant"
        drawArrowHead(g2, x1, y1, x2, y2, headLen);

        // label direction (NE / SO / ...)
        g2.setColor(TEXT_COLOR);
        g2.drawString(dir.label, x + 120, y + 40);

        // barre de force
        int barX = x + 95;
        int barY = y + 46;
        int barW = 70;
        int barH = 8;

        g2.setColor(BAR_BG);
        g2.fillRoundRect(barX, barY, barW, barH, 8, 8);
        g2.setColor(BAR_FG);
        g2.fillRoundRect(barX, barY, (int) (barW * strength), barH, 8, 8);

        // particules animées (dans le petit widget uniquement)
        Shape oldClip = g2.getClip();
        clipRect.setBounds(x + 6, y + 22, boxW - 12, boxH - 28);
        g2.setClip(clipRect);

        for (Particle p : particles) {
            // ne dessine que dans le widget (coordonnées normalisées => widget)
            int px = x + 6 + (int) ((boxW - 12) * p.x);
            int py = y + 22 + (int) ((boxH - 28) * p.y);

            int alpha = (int) (255 * p.alpha);
            g2.setColor(new Color(255, 255, 255, alpha));
            g2.fillOval(px, py, (int) p.size, (int) p.size);
        }

        g2.setClip(oldClip);
    }

    private void drawArrowHead(Graphics2D g2, int x1, int y1, int x2, int y2, int headLen) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        double a1 = angle + Math.toRadians(150);
        double a2 = angle - Math.toRadians(150);

        int hx1 = x2 + (int) (Math.cos(a1) * headLen);
        int hy1 = y2 + (int) (Math.sin(a1) * headLen);
        int hx2 = x2 + (int) (Math.cos(a2) * headLen);
        int hy2 = y2 + (int) (Math.sin(a2) * headLen);

        g2.drawLine(x2, y2, hx1, hy1);
        g2.drawLine(x2, y2, hx2, hy2);
    }

    private static class Particle {
        float x, y;
        float speed;
        float size;
        float alpha;
    }
}
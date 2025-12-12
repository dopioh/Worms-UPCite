package fr.upc.g5e4.view;

import fr.upc.g5e4.model.WindModel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WindOverlay extends JComponent {
    private final WindModel wind;
    private final List<Particle> particles = new ArrayList<>();
    private final Random rng = new Random();

    // animation
    private float animPhase = 0f;

    public WindOverlay(WindModel wind) {
        this.wind = wind;
        setOpaque(false);

        // crée quelques particules
        for (int i = 0; i < 30; i++) {
            particles.add(spawnParticle());
        }

        // timer d’animation (discret)
        new Timer(16, e -> {
            tick(0.016f);
            repaint();
        }).start();
    }

    private Particle spawnParticle() {
        Particle p = new Particle();
        p.x = rng.nextFloat();
        p.y = rng.nextFloat();
        p.speed = 0.15f + rng.nextFloat() * 0.35f; // vitesse base
        p.size = 1.5f + rng.nextFloat() * 2.5f;
        p.alpha = 0.2f + rng.nextFloat() * 0.35f;
        return p;
    }

    private void tick(float dt) {
        animPhase += dt;

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
        super.paintComponent(g);
        if (!(g instanceof Graphics2D g2)) return;

        int w = getWidth();
        int h = getHeight();

        // --- widget position
        int boxW = 180;
        int boxH = 60;

        // marge par rapport au bord
        int margin = 16;

        // bas droite
        int x = w - boxW - margin;
        int y = getHeight() - boxH - margin;

        // fond semi-transparent
        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRoundRect(x, y, boxW, boxH, 16, 16);

        // titre
        g2.setColor(new Color(255, 255, 255, 220));
        g2.drawString("VENT", x + 12, y + 18);

        // flèche + label direction
        WindModel.Dir dir = wind.getDir();
        float strength = wind.getStrength();

        // Dessin flèche diagonale
        int cx = x + 55;
        int cy = y + 38;
        int len = (int)(25 + 35 * strength);

        // direction en pixels (dx,dy)
        int dx = dir.dx;
        int dy = dir.dy;

        // normalisation simple pour diagonales/axes
        double norm = Math.sqrt(dx * dx + dy * dy);
        double ux = (norm == 0) ? 1 : dx / norm;
        double uy = (norm == 0) ? 0 : dy / norm;

        int x2 = cx + (int)(ux * len);
        int y2 = cy + (int)(uy * len);

        // petite animation “pulse” très discrète
        float pulse = 0.85f + 0.15f * (float)Math.sin(animPhase * 4.0);
        g2.setStroke(new BasicStroke(3.0f));
        g2.setColor(new Color(200, 230, 255, (int)(200 * pulse)));
        g2.drawLine(cx, cy, x2, y2);

        // tête de flèche
        drawArrowHead(g2, cx, cy, x2, y2);

        // label direction (NE / SO / ...)
        g2.setColor(new Color(255, 255, 255, 220));
        g2.drawString(dir.label, x + 120, y + 40);

        // barre de force
        int barX = x + 95;
        int barY = y + 46;
        int barW = 70;
        int barH = 8;

        g2.setColor(new Color(255, 255, 255, 60));
        g2.fillRoundRect(barX, barY, barW, barH, 8, 8);
        g2.setColor(new Color(120, 200, 255, 200));
        g2.fillRoundRect(barX, barY, (int)(barW * strength), barH, 8, 8);

        // particules animées (dans le petit widget uniquement)
        Shape oldClip = g2.getClip();
        g2.setClip(new Rectangle(x + 6, y + 22, boxW - 12, boxH - 28));

        for (Particle p : particles) {
            // ne dessine que dans le widget (coordonnées normalisées => widget)
            int px = x + 6 + (int)((boxW - 12) * p.x);
            int py = y + 22 + (int)((boxH - 28) * p.y);

            g2.setColor(new Color(255, 255, 255, (int)(255 * p.alpha)));
            g2.fillOval(px, py, (int)p.size, (int)p.size);
        }

        g2.setClip(oldClip);
    }

    private void drawArrowHead(Graphics2D g2, int x1, int y1, int x2, int y2) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int headLen = 10;
        double a1 = angle + Math.toRadians(150);
        double a2 = angle - Math.toRadians(150);

        int hx1 = x2 + (int)(Math.cos(a1) * headLen);
        int hy1 = y2 + (int)(Math.sin(a1) * headLen);
        int hx2 = x2 + (int)(Math.cos(a2) * headLen);
        int hy2 = y2 + (int)(Math.sin(a2) * headLen);

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

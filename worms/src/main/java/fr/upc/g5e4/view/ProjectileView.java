package fr.upc.g5e4.view;

import java.awt.*;
import fr.upc.g5e4.model.Projectile;
import org.jbox2d.common.Vec2;

/**
 * Classe pour dessiner les projectiles avec trajectoire mathématique (style Graphwar)
 */
public class ProjectileView {
    private static final float PPM = 30f;
    private static final Color PROJECTILE_COLOR = new Color(200, 100, 50, 200);  // Couleur orange-brun
    private static final Color PROJECTILE_OUTLINE = new Color(100, 50, 20, 255); // Contour plus foncé
    
    /**
     * Dessine un projectile à l'écran
     * @param g2 Context graphique
     * @param projectile Le projectile à dessiner
     */
    public static void drawProjectile(Graphics2D g2, Projectile projectile) {
        if (g2 == null || projectile == null) {
            return;
        }
        
        // Ne pas dessiner les projectiles de la baseball
        if ("Baseball Bat".equals(projectile.getSourceWeapon().getName())) {
            return;
        }
        
        Vec2 worldPos = projectile.getCurrentPosition();
        
        // Convertir les coordonnées monde en coordonnées écran
        float screenX = worldPos.x * PPM;
        float screenY = (24 - worldPos.y) * PPM;  // Inversion Y
        
        int radius = 5;  // Rayon du projectile en pixels
        
        // Remplir le projectile
        g2.setColor(PROJECTILE_COLOR);
        g2.fillOval((int)(screenX - radius), (int)(screenY - radius), radius * 2, radius * 2);
        
        // Dessiner le contour
        g2.setColor(PROJECTILE_OUTLINE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval((int)(screenX - radius), (int)(screenY - radius), radius * 2, radius * 2);
    }
    
    /**
     * Dessine une liste de projectiles
     * @param g2 Context graphique
     * @param projectiles Liste des projectiles à dessiner
     */
    public static void drawProjectiles(Graphics2D g2, java.util.List<Projectile> projectiles) {
        if (g2 == null || projectiles == null) {
            return;
        }
        
        for (Projectile projectile : projectiles) {
            drawProjectile(g2, projectile);
        }
    }
}

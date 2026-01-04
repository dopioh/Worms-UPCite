package fr.upc.g5e4.view;

import fr.upc.g5e4.model.DecorationManager;
import fr.upc.g5e4.model.DecorationModel;
import fr.upc.g5e4.util.ImageCache;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Vue pour dessiner les décorations du terrain,
 * Les images sont mises en cache
 */
public class DecorationView {
    
    private final Map<DecorationModel.DecoType, BufferedImage> imageCache;
    
    public DecorationView() {
        this.imageCache = new HashMap<>();
        loadImages();
    }
    
    /**
     * Charge toutes les images de décoration
     */
    private void loadImages() {
        for (DecorationModel.DecoType type : DecorationModel.DecoType.values()) {
            BufferedImage img = ImageCache.get("/" + type.getImagePath());
            if (img != null) {
                imageCache.put(type, img);
            } else {
                System.err.println("DecorationView: image non trouvée - " + type.getImagePath());
            }
        }
    }
    
    /**
     * Dessine toutes les décorations visibles
     * @param g2d contexte graphique
     * @param manager gestionnaire de décorations
     */
    public void draw(Graphics2D g2d, DecorationManager manager) {
        if (manager == null) return;
        
        for (DecorationModel deco : manager.getVisibleDecorations()) {
            drawDecoration(g2d, deco);
        }
    }
    
    /**
     * Dessine une décoration individuelle
     */
    private void drawDecoration(Graphics2D g2d, DecorationModel deco) {
        BufferedImage img = imageCache.get(deco.getType());
        if (img == null) return;
        
        int x = deco.getRenderX();
        int y = deco.getRenderY();
        int width = deco.getType().getWidth();
        int height = deco.getType().getHeight();
        
        g2d.drawImage(img, x, y, width, height, null);
    }
}

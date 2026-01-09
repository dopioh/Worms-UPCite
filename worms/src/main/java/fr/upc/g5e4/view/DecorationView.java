package fr.upc.g5e4.view;

import fr.upc.g5e4.model.DecorationManager;
import fr.upc.g5e4.model.DecorationModel;
import fr.upc.g5e4.util.ImageCache;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Vue des décorations du terrain
 * @author Ian
 */
public class DecorationView {
    
    public void draw(Graphics2D g2d, DecorationManager manager) {
        if (manager == null) return;
        
        for (DecorationModel deco : manager.getVisibleDecorations()) {
            drawDecoration(g2d, deco);
        }
    }
    
    private void drawDecoration(Graphics2D g2d, DecorationModel deco) {
        BufferedImage img = ImageCache.get("/" + deco.getType().getImagePath());
        if (img == null) return;
        
        g2d.drawImage(img, 
            deco.getRenderX(), 
            deco.getRenderY(), 
            deco.getType().getWidth(), 
            deco.getType().getHeight(), 
            null);
    }
}

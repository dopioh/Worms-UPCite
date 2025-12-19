package fr.upc.g5e4.view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import javax.imageio.ImageIO;
import fr.upc.g5e4.model.CharacterModel;

/**
 * Vue pour l'affichage du NameTag
 */
public class NameTagView {
    private int padding;
    private boolean visible;
    
    // Couleurs pour les barres
    private Color healthBarColor = new Color(50, 217, 97); // Vert
    private Color shieldBarColor = new Color(0, 195, 243); // Bleu cyan
    
    // Variables pour l'animation (index 0=health, 1=shield)
    private double[] displayed = {100.0, 50.0};
    private double[] start = {100.0, 50.0};
    private double[] target = {100.0, 50.0};
    private long[] animStart = {0, 0};
    
    // Liste des items à afficher au-dessus des barres
    private List<ItemIcon> itemIcons;
    
    /**
     * Classe interne pour représenter un item icon
     */
    public static class ItemIcon {
        public BufferedImage image;
        public String name;
        
        public ItemIcon(BufferedImage image, String name) {
            this.image = image;
            this.name = name;
        }
    }
    
    /**
     * Constructeur de la classe NameTagView
     */
    public NameTagView(Color borderColor) {
        this.padding = 5;
        this.visible = true;
        this.itemIcons = new ArrayList<>();
    }
    
    /**
     * Ajoute un item en chargeant l'image depuis un fichier dans resources
     * @param imagePath Le chemin de l'image dans resources (ex: "/items/potion.png")
     * @param name Le nom de l'item (identifiant unique)
     */
    public void addItemFromResource(String imagePath, String name) {
        try {
            BufferedImage image = ImageIO.read(getClass().getResourceAsStream(imagePath));
            itemIcons.add(new ItemIcon(image, name));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Impossible de charger l'image: " + imagePath);
            e.printStackTrace();
        }
    }
    
    /**
     * Retire tous les items
     */
    public void clearItems() {
        itemIcons.clear();
    }
    
    /**
     * Retire un item spécifique par son nom
     * @param name Le nom de l'item à retirer
     * @return true si l'item a été retiré, false sinon
     */
    public boolean removeItem(String name) {
        return itemIcons.removeIf(item -> item.name != null && item.name.equals(name));
    }
    
    /**
     * Fonction d'easing ease-out pour une animation fluide
     * Commence rapidement puis ralentit à la fin
     * @param t Valeur entre 0 et 1
     * @return Valeur interpolée avec easing
     */
    private double easeOut(double t) {
        return 1 - Math.pow(1 - t, 3);
    }
    
    /**
     * Anime une valeur vers sa cible
     * @param index 0 pour health, 1 pour shield
     * @param newTarget Nouvelle valeur cible
     * @param currentTime Temps actuel
     * @param duration Durée de l'animation
     */
    private void animateValue(int index, double newTarget, long currentTime, long duration) {
        if (newTarget < target[index]) {
            start[index] = displayed[index];
            target[index] = newTarget;
            animStart[index] = currentTime;
        }
        
        if (displayed[index] > target[index]) {
            long elapsed = currentTime - animStart[index];
            double progress = Math.min(1.0, (double)elapsed / duration);
            displayed[index] = start[index] - (start[index] - target[index]) * easeOut(progress);
        } else {
            displayed[index] = newTarget;
            target[index] = newTarget;
        }
    }
    
    /**
     * Dessine une barre de progression
     */
    private void drawProgressBar(Graphics2D g2d, int x, int y, int width, int height, 
                                  double value, double maxValue, Color color) {
        int radius = height;
        
        // Fond sombre de la barre
        g2d.setColor(new Color(
            (int)(color.getRed() * 0.4),
            (int)(color.getGreen() * 0.4),
            (int)(color.getBlue() * 0.4), 120));
        g2d.fillRoundRect(x, y, width, height, radius, radius);
        
        // Barre de progression
        int fillWidth = (int)((value / maxValue) * width);
        if (fillWidth > 0) {
            g2d.setColor(color);
            g2d.fillRoundRect(x, y, fillWidth, height, radius, radius);
        }
    }
    
    /**
     * Dessine le nouveau nametag avec items, barres de vie/shield et nom
     * @param g2d Le contexte graphique
     * @param model Le modèle du personnage
     */
    public void drawNewStyle(Graphics2D g2d, CharacterModel model) {
        if (!visible) {
            return;
        }
        
        // Utiliser les coordonnées du Body pour suivre le worm
        org.jbox2d.common.Vec2 position = model.getBody().getPosition();
        int centerX = (int)(position.x * CharacterModel.getPPM());
        int centerY = (int)((24 - position.y) * CharacterModel.getPPM());
        
        // Dimensions
        final int ITEM_SIZE = 18;
        final int ITEM_SPACING = 3;
        final int BAR_WIDTH = 70;
        final int BAR_HEIGHT = 8;
        final int BAR_SPACING = 3;
        final int NAME_PADDING = 8;
        
        // Calculer la hauteur totale du nametag
        int itemsHeight = itemIcons.isEmpty() ? 0 : ITEM_SIZE + ITEM_SPACING * 2;
        int barsHeight = BAR_HEIGHT * 2 + BAR_SPACING + padding;
        
        // Largeur du conteneur
        int containerWidth = BAR_WIDTH;
        int containerHeight = itemsHeight + barsHeight;
        
        int boxY = centerY - 30 - containerHeight;
        
        int currentY = boxY + padding;
        
        // 1. Dessiner les items alignés en haut
        if (!itemIcons.isEmpty()) {
            int totalItemsWidth = itemIcons.size() * ITEM_SIZE + (itemIcons.size() - 1) * ITEM_SPACING;
            int itemsStartX = centerX - totalItemsWidth / 2;
            
            for (int i = 0; i < itemIcons.size(); i++) {
                ItemIcon item = itemIcons.get(i);
                int itemX = itemsStartX + i * (ITEM_SIZE + ITEM_SPACING);
                
                if (item.image != null) {
                    g2d.drawImage(item.image, itemX, currentY, ITEM_SIZE, ITEM_SIZE, null);
                }
            }
            currentY += ITEM_SIZE + ITEM_SPACING * 2;
        }
        
        // 2. Dessiner la barre de shield (bleue)
        int barX = centerX - BAR_WIDTH / 2;
        long currentTime = System.currentTimeMillis();
        final long ANIMATION_DURATION = 600;
        
        // Animer les valeurs
        animateValue(0, model.getHealth(), currentTime, ANIMATION_DURATION);
        animateValue(1, model.getShield(), currentTime, ANIMATION_DURATION);
        
        drawProgressBar(g2d, barX, currentY, BAR_WIDTH, BAR_HEIGHT, 
                       displayed[1], 50.0, shieldBarColor);
        currentY += BAR_HEIGHT + BAR_SPACING;
        
        // 3. Dessiner la barre de vie (verte)
        drawProgressBar(g2d, barX, currentY, BAR_WIDTH, BAR_HEIGHT, 
                       displayed[0], 100.0, healthBarColor);
    }
    
    
    // Getters & Setters

    /**
     * Permet de savoir si le NameTag est visible
     * @return true si visible et false sinon
     */
    public boolean isVisible() {
        return visible;
    }
    
    /**
     * Active ou désactive l'affichage du NameTag
     * @param visible true pour afficher et false pour masquer
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}

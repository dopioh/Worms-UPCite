package fr.upc.g5e4.view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import fr.upc.g5e4.model.CharacterModel;
import fr.upc.g5e4.model.MapConfig;
import fr.upc.g5e4.util.ImageCache;

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
    private double[] displayed = {100.0, 100.0};
    private double[] start = {100.0, 100.0};
    private double[] target = {100.0, 100.0};
    private long[] animStart = {0, 0};
    
    // Liste des items à afficher au-dessus des barres
    private List<ItemIcon> itemIcons;

    // couleur pour chaque equipe
    private Color teamColor;
    
    /**
     * Classe interne pour représenter un item icon
     */
    public static class ItemIcon {
        public BufferedImage image;
        public String name;
        public long creationTime; // temps de création de l'item
        public long duration; // durée en millisecondes 
        
        public ItemIcon(BufferedImage image, String name) {
            this.image = image;
            this.name = name;
            this.creationTime = System.currentTimeMillis();
            this.duration = 0; // par défaut, pas d'expiration
        }
        
        public ItemIcon(BufferedImage image, String name, long durationMs) {
            this.image = image;
            this.name = name;
            this.creationTime = System.currentTimeMillis();
            this.duration = durationMs;
        }
        
        /**
         * @return le temps restant en millisecondes, ou 0 si expiré
         */
        public long getRemainingTime() {
            if (duration == 0) return 0; // pas de timer
            long elapsed = System.currentTimeMillis() - creationTime;
            long remaining = duration - elapsed;
            return Math.max(0, remaining);
        }
        
        /**
         * @return true si l'item a expiré
         */
        public boolean isExpired() {
            if (duration == 0) return false; // pas d'expiration
            return getRemainingTime() <= 0;
        }
    }
    
    /**
     * Constructeur de la classe NameTagView
     */
    public NameTagView(Color borderColor) {
        this.padding = 5;
        this.visible = true;
        this.teamColor = borderColor;
        this.itemIcons = new ArrayList<>();
    }
    
    /**
     * Ajoute un item en chargeant l'image depuis un fichier dans resources
     * @param imagePath Le chemin de l'image dans resources (ex: "/items/potion.png")
     * @param name Le nom de l'item (identifiant unique)
     */
    public void addItemFromResource(String imagePath, String name) {
        addItemFromResource(imagePath, name, 0);
    }
    
    /**
     * ajoute un item avec un timer
     * @param imagePath Le chemin de l'image dans resources
     * @param name Le nom de l'item 
     * @param durationMs La durée en milliseconde
     */
    public void addItemFromResource(String imagePath, String name, long durationMs) {
        BufferedImage image = ImageCache.get(imagePath);
        if (image != null) {
            itemIcons.add(new ItemIcon(image, name, durationMs));
        } else {
            System.err.println("Impossible de charger l'image: " + imagePath);
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
                                  double value, double maxValue, Color color, Color borderColor) {
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
        
        // Bordure
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawRoundRect(x, y, width, height, radius, radius);
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
        final int ITEM_SIZE = 25 ;
        final int ITEM_SPACING = 0;
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
        
        int boxY = centerY - 22 - containerHeight;
        
        int currentY = boxY + padding;
        
        // 1. Dessiner les items alignés en haut
        if (!itemIcons.isEmpty()) {
            // nettoyer les items expirés
            itemIcons.removeIf(ItemIcon::isExpired);
            
            int totalItemsWidth = itemIcons.size() * ITEM_SIZE + (itemIcons.size() - 1) * ITEM_SPACING;
            int itemsStartX = centerX - totalItemsWidth / 2;
            
            for (int i = 0; i < itemIcons.size(); i++) {
                ItemIcon item = itemIcons.get(i);
                int itemX = itemsStartX + i * (ITEM_SIZE + ITEM_SPACING);
                
                if (item.image != null) {
                    g2d.drawImage(item.image, itemX, currentY, ITEM_SIZE, ITEM_SIZE, null);
                    
                    // afficher le timer si l'item a une durée 
                    if (item.duration > 0) {
                        long remaining = item.getRemainingTime();
                        double seconds = remaining / 1000.0;
                        String timerText = String.format("%.0f", seconds);
                        
                        //texte du timer
                        g2d.setColor(Color.WHITE);
                        g2d.setFont(new Font("Arial", Font.BOLD, 8));
                        FontMetrics fm = g2d.getFontMetrics();
                        int textX = itemX + 13 - fm.stringWidth(timerText) / 2;
                        int textY = currentY + 8;
                        g2d.drawString(timerText, textX, textY);
                    }
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
        
        // Couleurs de bordure
        Color shieldBorderColor = new Color(38, 119, 140, 220);
        Color healthBorderColor = new Color(42, 129, 67, 220);
        
        drawProgressBar(g2d, barX, currentY, BAR_WIDTH, BAR_HEIGHT, 
                       displayed[1], 100.0, shieldBarColor, shieldBorderColor);
        currentY += BAR_HEIGHT + BAR_SPACING;
        
        // 3. Dessiner la barre de vie (verte)
        drawProgressBar(g2d, barX, currentY, BAR_WIDTH, BAR_HEIGHT, 
                       displayed[0], 100.0, healthBarColor, healthBorderColor);


        String name = model.getName();

        if (name != null && !name.isEmpty()) {
            g2d.setFont(new Font("googleSansFont", Font.BOLD, 22));
    
            FontMetrics fm = g2d.getFontMetrics(); // on recup les mesures de la font pour calculer son positionnement
            int textWidth = fm.stringWidth(name);   // recup la largeur du texte

            org.jbox2d.dynamics.Body body = model.getBody();
            org.jbox2d. common.Vec2 pos = body.getPosition();
            int x = (int)(pos.x * fr.upc.g5e4.model.CharacterModel. getPPM());
            int y = (int)((24 - pos.y) * fr.upc.g5e4.model.CharacterModel.getPPM());
    
            // on centre le texte par rapport a la position du worm
            int textX = x - (textWidth / 2);
            int textY = y - 70;

            if ((MapConfig.getBackgroundStyle() != null) && MapConfig.getBackgroundStyle().equals(MapConfig.BackgroundStyle.NUIT)){
                g2d.setColor(Color.BLACK);
            } else {
                 g2d.setColor(Color.WHITE);
            }

            g2d.setColor(Color.BLACK);
            int outlineThickness = 2; 
    
            for (int i = -outlineThickness; i <= outlineThickness; i++) {
                for (int j = -outlineThickness; j <= outlineThickness; j++) {
                    if ( i!= 0 || j != 0) { // pour eviter de dessiner au centre
                        g2d.drawString(name, textX + i, textY + j);
                    }   
                }
            }

            // Texte principal par-dessus
            g2d.setColor(teamColor);
            g2d.drawString(name, textX, textY);
  
        }
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

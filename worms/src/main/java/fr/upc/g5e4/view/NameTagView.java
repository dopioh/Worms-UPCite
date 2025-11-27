package fr.upc.g5e4.view;

import java.awt.*;
import fr.upc.g5e4.model.CharacterModel;

/**
 * Vue pour l'affichage du NameTag
 */
public class NameTagView {
    private Color backgroundColor;
    private Color textColor;
    private Color borderColor;
    private int padding;
    private Font font;
    private boolean visible;
    
    /**
     * Constructeur de la classe NameTagView
     */
    public NameTagView(Color borderColor) {
        this.backgroundColor = new Color(0, 0, 0, 180);
        this.textColor = Color.WHITE;
        this.borderColor = borderColor;
        this.padding = 5;
        this.font = new Font("Arial", Font.BOLD, 12);
        this.visible = true;
    }
    
    /**
     * Dessine une textbox simple
     * @param g2d Le contexte graphique
     * @param model Le modèle du personnage
     * @param text Le texte à afficher
     */
    public void draw(Graphics2D g2d, CharacterModel model, String text) {
        if (!visible || text == null || text.isEmpty()) {
            return;
        }
        
        // Utiliser les coordonnées du Body pour suivre le worm
        org.jbox2d.common.Vec2 position = model.getBody().getPosition();
        int x = (int)(position.x * CharacterModel.getPPM());
        int y = (int)((20 - position.y) * CharacterModel.getPPM());
        
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        
        int boxWidth = textWidth + padding * 2;
        int boxHeight = textHeight + padding * 2;
        
        int boxX = x - boxWidth / 2;
        int boxY = y - 35 - boxHeight;
        
        g2d.setColor(backgroundColor);
        g2d.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 5, 5);
        
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 5, 5);
        
        g2d.setColor(textColor);
        int textX = boxX + padding;
        int textY = boxY + padding + fm.getAscent();
        g2d.drawString(text, textX, textY);
    }
    
    /**
     * Dessine une textbox multi-lignes
     * @param g2d Le contexte graphique
     * @param model Le modèle du personnage
     * @param lignes Les lignes de texte
     */
    public void drawMultiLine(Graphics2D g2d, CharacterModel model, String[] lignes) {
        if (!visible || lignes == null || lignes.length == 0) {
            return;
        }
        
        // Utiliser les coordonnées du Body pour suivre le worm
        org.jbox2d.common.Vec2 position = model.getBody().getPosition();
        int x = (int)(position.x * CharacterModel.getPPM());
        int y = (int)((20 - position.y) * CharacterModel.getPPM());
        
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        
        int maxWidth = 0;
        for (String ligne : lignes) {
            int width = fm.stringWidth(ligne);
            if (width > maxWidth) {
                maxWidth = width;
            }
        }
        
        int textHeight = fm.getHeight();
        int boxWidth = maxWidth + padding * 2;
        int boxHeight = (textHeight * lignes.length) + padding * 2;
        
        int boxX = x - boxWidth / 2;
        int boxY = y - 15 - boxHeight;
        
        g2d.setColor(backgroundColor);
        g2d.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 5, 5);
        
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 5, 5);
        
        g2d.setColor(textColor);
        for (int i = 0; i < lignes.length; i++) {
            int textX = boxX + padding;
            int textY = boxY + padding + fm.getAscent() + (i * textHeight);
            g2d.drawString(lignes[i], textX, textY);
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

package fr.upc.g5e4.view;

import java.awt.*;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import fr.upc.g5e4.model.CharacterModel;

/**
 * Vue pour l'affichage graphique
 */
public class CharacterView {
    private int size;
    private Color color;
    private NameTagView nameTagView;
    
    /**
     * Constructeur de la classe PersonnageView
     */
    public CharacterView() {
        this.size = 15;
        this.color = new Color(255, 150, 0); // Orange
        this.nameTagView = new NameTagView(color);
    }
    
    /**
     * Constructeur de la classe PersonnageView avec paramètres personnalisés
     * @param taille La taille du worm
     * @param couleur La couleur du worm
     */
    public CharacterView(int size, Color color) {
        this.size = size;
        this.color = color;
        this.nameTagView = new NameTagView(color);
    }
    
    /**
     * Dessine le model du worm à l'écran
     * @param g2d Le contexte graphique
     * @param model Le modèle du worm à dessiner
     */
    public void draw(Graphics2D g2d, CharacterModel model) {
        drawBody(g2d, model);
        drawStats(g2d, model);
    }
    
    /**
     * Dessine le corps du worm (carré)
     * On se base sur son body
     * @param g2d Le contexte graphique
     * @param model Le model du worm
     */
    private void drawBody(Graphics2D g2d, CharacterModel model) {
        if (model.isDead()) {
            return;
        }
        
        Body body = model.getBody();
        Vec2 m_position = body.getPosition();
    
        // Conversion mètres -> pixels
        int x = (int)(m_position.x * CharacterModel.getPPM());
        int y = (int)((24 - m_position.y) * CharacterModel.getPPM());  // Conversion et Inversion Y
    
        g2d.setColor(color);
        g2d.fillRect(x - size/2, y - size/2, size, size);
    }
    
    /**
     * Affiche les statistiques du worm au-dessus de sa tête
     * @param g2d Le contexte graphique
     * @param model Le modèle du worm
     */
    private void drawStats(Graphics2D g2d, CharacterModel model) {
        if (model.isDead()) {
            nameTagView.setVisible(false);
            return;
        }
        
        // Afficher le NameTag si le personnage est vivant
        nameTagView.setVisible(true);
        
        String[] stats = {
            model.getName(),
            "HP: " + (int)model.getHealth() + "/100",
            "Shield: " + (int)model.getShield(),
        };
        
        nameTagView.drawMultiLine(g2d, model, stats);
    }
    
    // Getters et Setters
    public int getSize() {
        return size;
    }
    
    public void setSize(int size) {
        this.size = size;
    }

    public Color getColor() {
        return color;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
    
    public NameTagView getNameTagView() {
        return nameTagView;
    }
}

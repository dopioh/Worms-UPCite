package fr.upc.g5e4.view;

import java.awt.*;
import fr.upc.g5e4.model.PersonnageModel;

/**
 * Vue pour l'affichage graphique
 */
public class PersonnageView {
    private int size;
    private Color color;
    private NameTagView nameTagView;
    
    /**
     * Constructeur de la classe PersonnageView
     */
    public PersonnageView() {
        this.size = 40;
        this.color = new Color(255, 150, 0); // Orange
        this.nameTagView = new NameTagView();
    }
    
    /**
     * Constructeur de la classe PersonnageView avec paramètres personnalisés
     * @param taille La taille du worm
     * @param couleur La couleur du worm
     */
    public PersonnageView(int size, Color color) {
        this.size = size;
        this.color = color;
        this.nameTagView = new NameTagView();
    }
    
    /**
     * Dessine le model du worm à l'écran
     * @param g2d Le contexte graphique
     * @param model Le modèle du worm à dessiner
     */
    public void draw(Graphics2D g2d, PersonnageModel model) {
        drawBody(g2d, model);
        drawStats(g2d, model);
    }
    
    /**
     * Dessine le corps du worm (carré)
     * @param g2d Le contexte graphique
     * @param model Le model du worm
     */
    private void drawBody(Graphics2D g2d, PersonnageModel model) {
        int x = (int) Math.round(model.getXcord());
        int y = (int) Math.round(model.getYcord());

        g2d.setColor(color);
        g2d.fillRect(x - size/2, y - size/2, size, size);
    }
    
    /**
     * Affiche les statistiques du worm au-dessus de sa tête
     * @param g2d Le contexte graphique
     * @param model Le modèle du worm
     */
    private void drawStats(Graphics2D g2d, PersonnageModel model) {
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

package fr.upc.g5e4.view;

import java.awt.*;
import fr.upc.g5e4.model.TeamModel;
import fr.upc.g5e4.model.CharacterModel;

/**
 * Vue pour l'affichage d'une equipe de worms
 */
public class TeamView {
    private Color teamColor;
    private CharacterView[] tabViewWorms;
    
    /**
     * Constructeur de la classe TeamView (avec un sprite par defaut (un carré de couleur))
     * @param wormNumber nombre de worms dans l'équipe
     * @param teamColor Couleur de l'équipe
     */
    public TeamView(int wormNumber, Color teamColor) {
        this.teamColor = teamColor;
        this.tabViewWorms = new CharacterView[wormNumber];
        
        // Attribut une vue pour chaque worm de l'equipe
        for (int i = 0; i < tabViewWorms.length; i++) {
            tabViewWorms[i] = new CharacterView(25, teamColor);
        }
    }
    
    /**
     * constructeur de la classe TeamView avec sprite personnalisé
     * @param wormNumber nombre de worms dans l'équipe
     * @param teamColor Couleur de l'équipe
     * @param spritePath le chemin du sprite dans /resources (ex: "/worm.png")
     */
    public TeamView(int wormNumber, Color teamColor, String spritePath) {
        this(wormNumber, teamColor, spritePath, true);
    }
    
    /**
     * constructeur de la classe TeamView avec sprite personnalisé + direction personnalisée
     * @param wormNumber nombre de worms dans l'équipe
     * @param teamColor Couleur de l'équipe
     * @param spritePath le chemin du sprite dans /resources (ex: "/worm.png")
     * @param facingRight true si les worms regardent à droite par défaut, false pour gauche
     */
    public TeamView(int wormNumber, Color teamColor, String spritePath, boolean facingRight) {
        this.teamColor = teamColor;
        this.tabViewWorms = new CharacterView[wormNumber];
        
        // Attribut une vue avec sprite personnalisé pour chaque worm de l'equipe
        for (int i = 0; i < tabViewWorms.length; i++) {
            tabViewWorms[i] = new CharacterView(25, teamColor, spritePath);
        }
    }
    
    /**
     * Dessine tous les worms de l'équipe
     * @param g2d Le contexte graphique
     * @param teamModel Le modèle de l'équipe
     */
    public void draw(Graphics2D g2d, TeamModel teamModel) {
        CharacterModel[] worms = teamModel.getTabWorms();
        
        for (int i = 0; i < worms.length; i++) {
            if (worms[i] != null && !worms[i].isDead()) {
                tabViewWorms[i].draw(g2d, worms[i]);
            }
        }
    }
    
    // Getters et Setters
    public Color getTeamColor() {
        return teamColor;
    }
    
    public void setTeamColor(Color teamColor) {
        this.teamColor = teamColor;
        // Mettre à jour la couleur de tous les worms
        for (CharacterView view : tabViewWorms) {
            if (view != null) {
                view.setColor(teamColor);
            }
        }
    }
    
    /**
     * active ou désactive l'affichage des nametags pour toute l'équipe
     * @param show true pour afficher, false pour masquer
     */
    public void setShowAllNameTags(boolean show) {
        for (CharacterView view : tabViewWorms) {
            if (view != null) {
                view.setShowNameTag(show);
            }
        }
    }
    
    public CharacterView getCharacterView(int index) {
        return tabViewWorms[index];
    }
    
    /**
     * Retourne le tableau complet des vues de personnages
     * @return Le tableau des CharacterView
     */
    public CharacterView[] getViews() {
        return tabViewWorms;
    }
}

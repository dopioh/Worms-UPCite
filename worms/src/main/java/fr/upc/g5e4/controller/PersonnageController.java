package fr.upc.g5e4.controller;

import fr.upc.g5e4.model.PersonnageModel;

/**
 * Contrôleur pour le worm
 * Gère la logique de contrôle
 */
public class PersonnageController {
    private PersonnageModel model;
    
    // Variables pour gérer les touches maintenues
    private boolean LeftKeyPressed = false;
    private boolean RightKeyPressed = false;

    /**
     * Constructeur de la classe PersonnageController
     * @param model Le modèle du worm à contrôler
     */
    public PersonnageController(PersonnageModel model) {
        this.model = model;
    }

    // Activation & Desactivation des touches
    public void activateLeft() {
        LeftKeyPressed = true;
    }
    
    public void deactivateLeft() {
        LeftKeyPressed = false;
    }
    
    public void activateRight() {
        RightKeyPressed = true;
    }
    
    public void deactivateRight() {
        RightKeyPressed = false;
    }
    
    /**
     * Met à jour la position du worm en fonction des touches enfoncées
     * @return true si le worm a bougé, false sinon
     */
    public boolean update() {
        double dx = 0;

        if (LeftKeyPressed) dx -= model.getSpeed();
        if (RightKeyPressed) dx += model.getSpeed();

        if (dx != 0) {
            model.setXcord(model.getXcord() + dx);
            return true;
        }
        
        return false;
    }

    public void moveLeft() {
        model.setXcord(model.getXcord() - model.getSpeed());
    }

    public void moveRight() {
        model.setXcord(model.getXcord() + model.getSpeed());
    }
    
    /**
     * Téléporte le personnage à une position
     * 
     * @param newX La nouvelle coordonnée x
     * @param newY La nouvelle coordonnée y
     */
    public void teleport(double newX, double newY) {
        model.setXcord(newX);
        model.setYcord(newY);
    }
    
}

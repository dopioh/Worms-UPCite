package fr.upc.g5e4.controller;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import fr.upc.g5e4.model.CharacterModel;

/**
 * Contrôleur pour le worm
 * Gère la logique de contrôle
 */
public class CharacterController {
    private CharacterModel model;
    
    // Variables pour gérer les touches maintenues
    private boolean LeftKeyPressed = false;
    private boolean RightKeyPressed = false;
    private boolean SpaceKeyPressed = false;

    private final double MOVE_FORCE;  // Vitesse de deplacement

    /**
     * Constructeur de la classe PersonnageController
     * @param model Le modèle du worm à contrôler
     */
    public CharacterController(CharacterModel model) {
        this.model = model;
        MOVE_FORCE = model.getSpeed();
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

    public void activateJump() {
        if (!SpaceKeyPressed) {
            SpaceKeyPressed = true;
            model.jump();
        }
    }

    public void deactivateJump() {
        SpaceKeyPressed = false;
    }
    
    
    /**
     * @return true si le worm a bougé, false sinon
     * Met à jour la position du worm en fonction des touches enfoncées
     * Contrairement à avant, on se base sur la position du body pour le faire bouger
     * On le syncro ensuite avec son model
     */
    public boolean update() {
        Body body = model.getBody();
        
        // Appliquer une force horizontale
        if (LeftKeyPressed) {
            body.applyForceToCenter(new Vec2((float)-MOVE_FORCE, 0));
        }
        if (RightKeyPressed) {
            body.applyForceToCenter(new Vec2((float)MOVE_FORCE, 0));
        }
        
        // Synchroniser les coordonnées du modèle avec le corps physique
        Vec2 position = body.getPosition();
        model.setXcord(position.x * 30f);  // Conversion mètres → pixels
        model.setYcord((20 - position.y) * 30f);  // Inversion Y + conversion
        
        return LeftKeyPressed ^ RightKeyPressed;
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

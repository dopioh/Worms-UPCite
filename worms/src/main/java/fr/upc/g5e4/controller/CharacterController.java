package fr.upc.g5e4.controller;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import fr.upc.g5e4.model.CharacterModel;
import fr.upc.g5e4.model.Weapon;

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
    
    // Références pour le tir
    private ItemController itemController;
    private World world;
    private CharacterModel[] allWorms;
    private Weapon currentWeapon;

    /**
     * Constructeur de la classe PersonnageController
     * @param model Le modèle du worm à contrôler
     */
    public CharacterController(CharacterModel model) {
        this.model = model;
        MOVE_FORCE = model.getSpeed();
    }

    /**
     * Définit l'ItemController pour le tir
     * @param itemController Le contrôleur d'items
     */
    public void setItemController(ItemController itemController) {
        this.itemController = itemController;
    }

    /**
     * Définit le monde physique pour les raycast
     * @param world Le monde JBox2D
     */
    public void setWorld(World world) {
        this.world = world;
    }

    /**
     * Définit le tableau de tous les worms
     * @param allWorms Tableau de tous les worms
     */
    public void setAllWorms(CharacterModel[] allWorms) {
        this.allWorms = allWorms;
    }

    /**
     * Définit l'arme actuellement équipée
     * @param weapon L'arme à équiper
     */
    public void setCurrentWeapon(Weapon weapon) {
        this.currentWeapon = weapon;
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
        model.setYcord((10 - position.y) * 30f);  // Inversion Y + conversion
        
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

    /**
     * Tire avec l'arme actuelle en direction du curseur
     * @param screenX Position X de la souris à l'écran
     * @param screenY Position Y de la souris à l'écran
     */
    public void fireWeapon(int screenX, int screenY) { 
        if (itemController == null || world == null || allWorms == null || currentWeapon == null) { 
            return;
        }
        // Déterminer la direction en fonction de la position du curseur par rapport au worm
        Body body = model.getBody();
        float wormWorldX = body.getPosition().x;
        
        // Convertir les coordonnées écran en coordonnées monde
        int direction = screenX > (int)(wormWorldX * 30f) ? 1 : -1;
        
        // Tirer avec l'arme
        itemController.useWeaponOnTarget(currentWeapon, model, direction);
    }
    
}

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

    /**
     * retourne l'arme actuellement équipée
     * @return l'arme actuelle
     */
    public Weapon getCurrentWeapon() {
        return this.currentWeapon;
    }

    /**
     * Retourne le modèle du worm
     * @return le modèle du worm
     */
    public CharacterModel getModel() {
        return this.model;
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
        //System.out.println("update called");
        Body body = model.getBody();
        
        
        if (model.isFlyMode()) { // deplacements flymod
            Vec2 velocity = new Vec2(0, 0);
            
            if (LeftKeyPressed) {
                velocity.x = (float)-MOVE_FORCE*3;
            }
            if (RightKeyPressed) {
                velocity.x = (float)MOVE_FORCE*3;
            }
            if (SpaceKeyPressed) {
                velocity.y = (float)MOVE_FORCE*3;  // monter
            }
            
            body.setLinearVelocity(velocity);
        } else {
            // Mode normal : appliquer une force horizontale
            if (LeftKeyPressed) {
                body.applyForceToCenter(new Vec2((float)-MOVE_FORCE, 0));
            }
            if (RightKeyPressed) {
                body.applyForceToCenter(new Vec2((float)MOVE_FORCE, 0));
            }
        }
        
        // Synchroniser les coordonnées du modèle avec le corps physique
        Vec2 position = body.getPosition();
        model.setXcord(position.x * 30f);  // Conversion mètres → pixels
        model.setYcord((10 - position.y) * 30f);  // Inversion Y + conversion
        
        return LeftKeyPressed ^ RightKeyPressed;
    }
    
    /**
     * Active/désactive le mode vol pour ce worm
     */
    public void toggleFlyMode() {
        model.toggleFlyMode();
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
        if (itemController == null || world == null || allWorms == null || currentWeapon == null) { //
            return;
        }
        
        //position du worm
        Body body = model.getBody();
        float wormWorldX = body.getPosition().x * 30f;  // conversion en pixels
        float wormWorldY = (10 - body.getPosition().y) * 30f;  // conversion en pixels
        //le vecteur direction depuis le worm vers le curseur
        float dx = screenX - wormWorldX;
        float dy = screenY - wormWorldY;
        
        //angle en radians
        double angle = Math.atan2(-dy, dx);  // -dy car Y est inversé en écran
        
        //direction (gauche/droite) basée sur l'angle
        int direction = dx > 0 ? 1 : -1;
        
        //tirer avec l'arme en passant l'angle
        fireWeaponWithAngle(angle, direction);
    }
    
    /**
     * Tire avec l'arme actuelle à un angle spécifique
     * @param angle L'angle de tir en radians (non utilisé en système Graphwar)
     * @param direction La direction (1 pour droite, -1 pour gauche)
     */
    public void fireWeaponWithAngle(double angle, int direction) {
        if (itemController == null || world == null || allWorms == null || currentWeapon == null) { 
            return;
        }
        
        // tirer avec l'arme 
        itemController.useWeaponOnTarget(currentWeapon, model, direction);
    }
    
    /**
     * tire avec l'arme actuelle dans la direction spécifiée 
     * @param direction la direction
     */
    public void fireWeapon(int direction) {
        if (itemController == null || world == null || allWorms == null || currentWeapon == null) { 
            return;
        }
        
        // tirer avec l'arme 
        itemController.useWeaponOnTarget(currentWeapon, model, direction);
    }
}

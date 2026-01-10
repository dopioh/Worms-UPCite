package fr.upc.g5e4.controller;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import fr.upc.g5e4.model.CharacterModel;
import fr.upc.g5e4.model.GameManager;
import fr.upc.g5e4.model.InventoryModel.InventoryJson;
import fr.upc.g5e4.model.Weapon;

/**
 * Contrôleur pour le worm
 * Gère la logique de contrôle
 * @author Nassim & Billy
 */
public class CharacterController {
    private CharacterModel model;
    
    // Variables pour gérer les touches pressées
    private boolean LeftKeyPressed = false;
    private boolean RightKeyPressed = false;
    private boolean SpaceKeyPressed = false;
    
    // Références pour le tir
    private ItemController itemController;
    private World world;
    private CharacterModel[] allWorms;
    private Weapon currentWeapon;
    private InventoryJson currentWeaponInventory;
    private GameManager gameManager;

    /**
     * Constructeur de la classe PersonnageController
     * @param model Le modèle du worm à contrôler
     */
    public CharacterController(CharacterModel model) {
        this.model = model;
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
     * définit le GameManager pour l'utilisation d'arme
     * @param gameManager Le GameManager
     */
    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * Définit l'arme actuellement équipée
     * @param weapon L'arme à équiper
     */
    public void setCurrentWeapon(Weapon weapon) {
        this.currentWeapon = weapon;
    }
    
    /**
     * définit l'objet inventoryJson de l'arme actuellement équipée
     * @param weaponInventory L'objet inventoryJson de l'arme
     */
    public void setCurrentWeaponInventory(InventoryJson weaponInventory) {
        this.currentWeaponInventory = weaponInventory;
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
    
    // Vitesse maximale horizontale (en m/s)
    private static final float MAX_HORIZONTAL_SPEED = 3.3f;
    
    /**
     * @return true si le worm a bougé, false sinon
     * Met à jour la position du worm en fonction des touches enfoncées
     * Contrairement à avant, on se base sur la position du body pour le faire bouger
     * On le syncro ensuite avec son model
     */
    public boolean update() {
        Body body = model.getBody();
        boolean isMoving = false;
        
        // calcule de la vitesse avec le buff
        float currentSpeed = (float) model.getSpeed();
        
        if (model.isFlyMode()) {
            // Mode vol : déplacement direct sans accumulation d'élan
            float velX = 0;
            float velY = 0;
            
            if (LeftKeyPressed) velX = -currentSpeed * 2;
            if (RightKeyPressed) velX = currentSpeed * 2;
            if (SpaceKeyPressed) velY = currentSpeed * 2;
            
            body.setLinearVelocity(new Vec2(velX, velY));
            isMoving = LeftKeyPressed || RightKeyPressed;
        } else {
            // Mode normal (on sait quelle face du sprite afficher)
            if (LeftKeyPressed || RightKeyPressed) {
                int direction = RightKeyPressed ? 1 : -1;
                model.setFacingRight(RightKeyPressed);
                
                float currentVelY = body.getLinearVelocity().y;
                body.setLinearVelocity(new Vec2(direction * currentSpeed, currentVelY));
                
                isMoving = true;
            } else {
                // on arrête complètement le mouvement horizontal si on n'appuie sur rien
                // pour eviter le glissement
                model.stopHorizontalMovement();
            }
        }
        
        // synchronise les coordonnées du modèle avec le corps physique
        Vec2 position = body.getPosition();
        model.setXcord(position.x * 30f); // Conversion mètres -> pixels
        model.setYcord((10 - position.y) * 30f); // Inversion Y + conversion
        
        return isMoving;
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
        
        // vérifier si une autre arme a déjà été utilisée ce tour
        if (gameManager != null) {
            String usedWeapon = gameManager.getWeaponUsedThisTurn();
            if (usedWeapon != null && currentWeaponInventory != null && !usedWeapon.equalsIgnoreCase(currentWeaponInventory.name)) {
                System.out.println("[Arme] Vous avez déjà utilisé " + usedWeapon + " ce tour! Vous ne pouvez utiliser que cette arme.");
                return;
            }
        }
        
        // vérifier si on peut tirer avec cette arme ce tour
        if (currentWeaponInventory != null && !currentWeaponInventory.canUseThisTurn()) {
            System.out.println("[Arme] Limite d'utilisation atteinte pour cette arme ce tour!");
            return;
        }
        
        // tirer avec l'arme 
        itemController.useWeaponOnTarget(currentWeapon, model, direction);
        
        // enregistrer l'utilisation de l'arme
        if (currentWeaponInventory != null) {
            currentWeaponInventory.registerUseThisTurn();
        }
        
        // marquer que l'arme a été utilisée ce tour
        if (gameManager != null && currentWeaponInventory != null) {
            gameManager.markWeaponUsed(currentWeaponInventory.name);
        }
    }
    
    /**
     * tire avec l'arme actuelle dans la direction spécifiée 
     * @param direction la direction
     */
    public void fireWeapon(int direction) {
        if (itemController == null || world == null || allWorms == null || currentWeapon == null) { 
            return;
        }
        
        // vérifier si une autre arme a déjà été utilisée ce tour
        if (gameManager != null) {
            String usedWeapon = gameManager.getWeaponUsedThisTurn();
            if (usedWeapon != null && currentWeaponInventory != null && !usedWeapon.equalsIgnoreCase(currentWeaponInventory.name)) {
                System.out.println("[Arme] Vous avez déjà utilisé " + usedWeapon + " ce tour! Vous ne pouvez utiliser que cette arme.");
                return;
            }
        }
        
        // vérifier si on peut tirer avec cette arme ce tour
        if (currentWeaponInventory != null && !currentWeaponInventory.canUseThisTurn()) {
            System.out.println("[Arme] Limite d'utilisation atteinte pour cette arme ce tour!");
            return;
        }
        
        // tirer avec l'arme 
        itemController.useWeaponOnTarget(currentWeapon, model, direction);
        
        // enregistrer l'utilisation de l'arme
        if (currentWeaponInventory != null) {
            currentWeaponInventory.registerUseThisTurn();
        }
        
        // marquer que l'arme a été utilisée ce tour
        if (gameManager != null && currentWeaponInventory != null) {
            gameManager.markWeaponUsed(currentWeaponInventory.name);
        }
    }
}

package fr.upc.g5e4;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jbox2d.dynamics.World;

import fr.upc.g5e4.controller.CharacterController;
import fr.upc.g5e4.controller.ItemController;
import fr.upc.g5e4.model.*;
import fr.upc.g5e4.model.weapons.Ak47;
import fr.upc.g5e4.view.ItemView;

public class FireWeaponWithAngleTest {
    private CharacterController characterController;
    private CharacterModel character;
    private World world;
    private PhysicsWorld physicsWorld;
    private GroundModel ground;
    private ItemController itemController;
    private CharacterModel[] allWorms;
    private Weapon currentWeapon;

    @BeforeEach
    public void setUp() {
        int sizeX = 800;
        int sizeY = 600;
        int[] groundY = {400, 420, 450, 450, 400, 300, 530, 450, 350, 300, 250, 230, 200, 230};
        
        ground = new GroundModel(groundY, sizeX, sizeY, 500);
        physicsWorld = new PhysicsWorld();
        world = physicsWorld.getWorld();
        physicsWorld.createGroundFromModel(ground);
        
        character = new CharacterModel(ground, world, 400, ground.getHeight() - 100);
        characterController = new CharacterController(character);
        
        // créer et configurer l'ItemController
        itemController = new ItemController(new ItemView());
        itemController.setWorld(world);
        
        //configurer tous les worms
        allWorms = new CharacterModel[1];
        allWorms[0] = character;
        itemController.setAllWorms(allWorms);
        
        characterController.setItemController(itemController);
        characterController.setWorld(world);
        characterController.setAllWorms(allWorms);
        
        currentWeapon = new Ak47();
        characterController.setCurrentWeapon(currentWeapon);
    }

    @Test
    public void testFireWeaponWithAngleHorizontal() {
        // angle 0 = tir horizontal
        double angle = 0;
        int direction = 1;
        
        assertDoesNotThrow(() -> characterController.fireWeaponWithAngle(angle, direction));
    }

    @Test
    public void testFireWeaponWithAngle45Degrees() {
        // Angle 45 degrés
        double angle = Math.PI / 4;
        int direction = 1;
        
        assertDoesNotThrow(() -> characterController.fireWeaponWithAngle(angle, direction));
    }

    @Test
    public void testFireWeaponWithAngle90Degrees() {
        // Angle 90 degrés = vertical
        double angle = Math.PI / 2;
        int direction = 1;
        
        assertDoesNotThrow(() -> characterController.fireWeaponWithAngle(angle, direction));
    }

    @Test
    public void testFireWeaponWithAngleNegativeDirection() {
        // Tir vers la gauche
        double angle = 0;
        int direction = -1;
        
        assertDoesNotThrow(() -> characterController.fireWeaponWithAngle(angle, direction));
    }

    @Test
    public void testFireWeaponWithAngleVariousAngles() {
        int[] angles = {0, 15, 30, 45, 60, 75, 90};
        
        for (int angleDegrees : angles) {
            double angleRadians = Math.toRadians(angleDegrees);
            assertDoesNotThrow(() -> 
                characterController.fireWeaponWithAngle(angleRadians, 1),
                "FireWeaponWithAngle should handle angle: " + angleDegrees + "°"
            );
        }
    }

    @Test
    public void testFireWeaponWithAngleBothDirections() {
        double angle = Math.PI / 4;
        
        // Tir vers la droite
        assertDoesNotThrow(() -> characterController.fireWeaponWithAngle(angle, 1));
        
        // Tir vers la gauche
        assertDoesNotThrow(() -> characterController.fireWeaponWithAngle(angle, -1));
    }

    @Test
    public void testFireWeaponWithAngleWithoutItemController() {
        CharacterController controller = new CharacterController(character);
        // Pas d'itemController configuré
        
        // Devrait ne pas lever d'exception
        assertDoesNotThrow(() -> controller.fireWeaponWithAngle(Math.PI / 4, 1));
    }

    @Test
    public void testFireWeaponWithAngleSetsAngleOnWeapon() {
        double angle = Math.PI / 3;
        
        assertDoesNotThrow(() -> characterController.fireWeaponWithAngle(angle, 1));
        
        // Vérifier que l'angle a été défini sur l'arme
        assertEquals(angle, currentWeapon.getShootAngle(), 0.0001);
    }

    @Test
    public void testFireWeaponWithAngleSmallAngle() {
        double angle = 0.1;  // ~5.7 degrés
        assertDoesNotThrow(() -> characterController.fireWeaponWithAngle(angle, 1));
    }

    @Test
    public void testFireWeaponWithAngleLargeAngle() {
        double angle = Math.PI / 2 - 0.1;  // ~84.3 degrés
        assertDoesNotThrow(() -> characterController.fireWeaponWithAngle(angle, 1));
    }
}

package fr.upc.g5e4.view;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.upc.g5e4.model.*;
import org.jbox2d.dynamics.World;

public class TrajectoryRendererTest {
    private TrajectoryRenderer trajectoryRenderer;
    private CharacterModel character;
    private World world;
    private PhysicsWorld physicsWorld;
    private GroundModel ground;
    private Weapon weapon;

    @BeforeEach
    public void setUp() {
        trajectoryRenderer = new TrajectoryRenderer();
        
        int sizeX = 800;
        int sizeY = 600;
        int[] groundY = {400, 420, 450, 450, 400, 300, 530, 450, 350, 300, 250, 230, 200, 230};
        
        ground = new GroundModel(groundY, sizeX, sizeY, 500);
        physicsWorld = new PhysicsWorld();
        world = physicsWorld.getWorld();
        physicsWorld.createGroundFromModel(ground);
        
        character = new CharacterModel(ground, world, 400, ground.getHeight() - 100);
        
        // Créer une arme simple pour les tests
        weapon = new Weapon("Test Weapon", 10, false, 10, 0, 10, 30) {
            @Override
            protected boolean onUse() {
                return true;
            }
        };
    }

    @Test
    public void testTrajectoryRendererInitialization() {
        assertNotNull(trajectoryRenderer);
    }

    @Test
    public void testSetShooter() {
        assertDoesNotThrow(() -> trajectoryRenderer.setShooter(character));
    }

    @Test
    public void testSetWeapon() {
        assertDoesNotThrow(() -> trajectoryRenderer.setWeapon(weapon));
    }

    @Test
    public void testSetAiming() {
        assertDoesNotThrow(() -> trajectoryRenderer.setAiming(true));
        assertDoesNotThrow(() -> trajectoryRenderer.setAiming(false));
    }

    @Test
    public void testDrawWithoutInitialization() {
        // Tester draw sans initialiser les paramètres
        assertDoesNotThrow(() -> trajectoryRenderer.draw(null, null));
    }

    @Test
    public void testAimingToggle() {
        trajectoryRenderer.setShooter(character);
        
        trajectoryRenderer.setAiming(true);
        assertTrue(trajectoryRenderer.isAiming());
        
        trajectoryRenderer.setAiming(false);
        assertFalse(trajectoryRenderer.isAiming());
    }

    @Test
    public void testTrajectoryRendererFullSetup() {
        trajectoryRenderer.setShooter(character);
        trajectoryRenderer.setWeapon(weapon);
        trajectoryRenderer.setAiming(true);
        
        assertDoesNotThrow(() -> trajectoryRenderer.draw(null, null));
    }
}

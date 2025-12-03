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
    }

    @Test
    public void testTrajectoryRendererInitialization() {
        assertNotNull(trajectoryRenderer);
    }

    @Test
    public void testSetShootingAngle() {
        double angle = Math.PI / 4;
        assertDoesNotThrow(() -> trajectoryRenderer.setShootingAngle(angle));
    }

    @Test
    public void testSetShootingAngleVariousAngles() {
        double[] angles = {0, Math.PI / 6, Math.PI / 4, Math.PI / 3, Math.PI / 2};
        
        for (double angle : angles) {
            assertDoesNotThrow(() -> trajectoryRenderer.setShootingAngle(angle));
        }
    }

    @Test
    public void testSetShooter() {
        assertDoesNotThrow(() -> trajectoryRenderer.setShooter(character));
    }

    @Test
    public void testSetRange() {
        assertDoesNotThrow(() -> trajectoryRenderer.setRange(30));
    }

    @Test
    public void testSetRangeVariousValues() {
        double[] ranges = {10, 20, 30, 50, 100};
        
        for (double range : ranges) {
            assertDoesNotThrow(() -> trajectoryRenderer.setRange(range));
        }
    }

    @Test
    public void testSetAiming() {
        assertDoesNotThrow(() -> trajectoryRenderer.setAiming(true));
        assertDoesNotThrow(() -> trajectoryRenderer.setAiming(false));
    }

    @Test
    public void testUpdateAngleFromMouseValidCoordinates() {
        trajectoryRenderer.setShooter(character);
        
        assertDoesNotThrow(() -> trajectoryRenderer.updateAngleFromMouse(400, 300));
    }

    @Test
    public void testUpdateAngleFromMouseVariousCoordinates() {
        trajectoryRenderer.setShooter(character);
        
        int[][] positions = {
            {0, 0},      // en haut a gauche
            {800, 0},    // en haut a droite
            {400, 300},  // Centre
            {800, 600},  // en bas a droite
            {0, 600},    // en bas a gauche
            {100, 150},  // un point arbitraire
            {600, 450}   // un autre point arbitraire
        };
        
        for (int[] pos : positions) {
            assertDoesNotThrow(() -> 
                trajectoryRenderer.updateAngleFromMouse(pos[0], pos[1]),
                "UpdateAngleFromMouse should handle coordinates: " + pos[0] + ", " + pos[1]
            );
        }
    }

    @Test
    public void testUpdateAngleFromMouseWithoutShooter() {
        //ne pas définir de shooter
        assertDoesNotThrow(() -> trajectoryRenderer.updateAngleFromMouse(400, 300));
    }

    @Test
    public void testTrajectoryRendererFullSetup() {
        trajectoryRenderer.setShooter(character);
        trajectoryRenderer.setRange(30);
        trajectoryRenderer.setShootingAngle(Math.PI / 4);
        trajectoryRenderer.setAiming(true);
        
        assertDoesNotThrow(() -> trajectoryRenderer.updateAngleFromMouse(400, 300));
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
    public void testUpdateAngleMultipleTimes() {
        trajectoryRenderer.setShooter(character);
        
        // simuler plusieurs mise à jour d'angle (comme lors du déplacement de la souris)
        for (int x = 0; x < 800; x += 100) {
            for (int y = 0; y < 600; y += 100) {
                int finalX = x;
                int finalY = y;
                assertDoesNotThrow(() -> 
                    trajectoryRenderer.updateAngleFromMouse(finalX, finalY)
                );
            }
        }
    }

    @Test
    public void testTrajectoryRendererWithDifferentRanges() {
        trajectoryRenderer.setShooter(character);
        trajectoryRenderer.setShootingAngle(Math.PI / 4);
        trajectoryRenderer.setAiming(true);
        
        double[] ranges = {5, 10, 20, 30, 50};
        
        for (double range : ranges) {
            trajectoryRenderer.setRange(range);
            assertTrue(trajectoryRenderer.getRange() >= 0, "Range should be non-negative");
        }
    }

    @Test
    public void testTrajectoryRendererWithDifferentAngles() {
        trajectoryRenderer.setShooter(character);
        trajectoryRenderer.setRange(30);
        trajectoryRenderer.setAiming(true);
        
        double[] angles = {0, Math.PI / 6, Math.PI / 4, Math.PI / 3, Math.PI / 2};
        
        for (double angle : angles) {
            trajectoryRenderer.setShootingAngle(angle);
            assertEquals(angle, trajectoryRenderer.getAngle(), 0.0001, "Angle should be stored correctly");
        }
    }
}

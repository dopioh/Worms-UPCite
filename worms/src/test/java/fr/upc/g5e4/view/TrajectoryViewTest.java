package fr.upc.g5e4.view;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.upc.g5e4.model.*;
import org.jbox2d.dynamics.World;
import java.awt.*;
import java.awt.image.BufferedImage;

public class TrajectoryViewTest {
    private TrajectoryView trajectoryView;
    private CharacterModel character;
    private World world;
    private PhysicsWorld physicsWorld;
    private GroundModel ground;
    private Weapon weapon;

    @BeforeEach
    public void setUp() {
        trajectoryView = new TrajectoryView();
        
        int sizeX = 800;
        int sizeY = 600;
        int[] groundY = {400, 420, 450, 450, 400, 300, 530, 450, 350, 300, 250, 230, 200, 230};
        
        ground = new GroundModel(groundY, sizeX, sizeY, 500);
        physicsWorld = new PhysicsWorld();
        world = physicsWorld.getWorld();
        physicsWorld.createGroundFromModel(ground);
        
        character = new CharacterModel(ground, world, 400, ground.getHeight() - 100, false);
        
        //créer une arme simple pour les tests
        weapon = new Weapon("Test Weapon", 10, false, 10, 0, 10, 30, 1.0) {
            @Override
            protected boolean onUse() {
                return true;
            }
        };
    }

    // ========== Tests d'initialisation ==========
    @Test
    public void testTrajectoryViewInitialization() {
        assertNotNull(trajectoryView, "TrajectoryView devrait être initialisé");
    }

    @Test
    public void testInitialAimingState() {
        assertFalse(trajectoryView.isAiming(), "L'état de visée devrait être faux au démarrage");
    }

    // ========== Tests setShootingAngle ==========
    @Test
    public void testSetShootingAngle() {
        assertDoesNotThrow(() -> trajectoryView.setShootingAngle(45.0));
        assertDoesNotThrow(() -> trajectoryView.setShootingAngle(90.0));
        assertDoesNotThrow(() -> trajectoryView.setShootingAngle(180.0));
    }

    @Test
    public void testSetShootingAngleWithNegativeValues() {
        // Les angles négatifs doivent être gérés correctement (modulo 360)
        assertDoesNotThrow(() -> trajectoryView.setShootingAngle(-45.0));
        assertDoesNotThrow(() -> trajectoryView.setShootingAngle(-90.0));
    }

    @Test
    public void testSetShootingAngleOver360() {
        // Les angles > 360 doivent être réduits (modulo 360)
        assertDoesNotThrow(() -> trajectoryView.setShootingAngle(450.0));
        assertDoesNotThrow(() -> trajectoryView.setShootingAngle(720.0));
    }

    @Test
    public void testSetShootingAngleZero() {
        assertDoesNotThrow(() -> trajectoryView.setShootingAngle(0.0));
    }

    // ========== Tests setShooter ==========
    @Test
    public void testSetShooter() {
        assertDoesNotThrow(() -> trajectoryView.setShooter(character));
    }

    @Test
    public void testSetShooterWithNull() {
        // Le système doit gérer les valeurs null
        assertDoesNotThrow(() -> trajectoryView.setShooter(null));
    }

    @Test
    public void testSetShooterMultipleTimes() {
        assertDoesNotThrow(() -> {
            trajectoryView.setShooter(character);
            trajectoryView.setShooter(character);
        });
    }

    // ========== Tests setWeapon ==========
    @Test
    public void testSetWeapon() {
        assertDoesNotThrow(() -> trajectoryView.setWeapon(weapon));
    }

    @Test
    public void testSetWeaponWithNull() {
        assertDoesNotThrow(() -> trajectoryView.setWeapon(null));
    }

    @Test
    public void testSetWeaponMultipleTimes() {
        assertDoesNotThrow(() -> {
            trajectoryView.setWeapon(weapon);
            trajectoryView.setWeapon(weapon);
        });
    }

    // ========== Tests setAiming ==========
    @Test
    public void testSetAimingTrue() {
        trajectoryView.setAiming(true);
        assertTrue(trajectoryView.isAiming(), "isAiming() devrait retourner true");
    }

    @Test
    public void testSetAimingFalse() {
        trajectoryView.setAiming(true);
        trajectoryView.setAiming(false);
        assertFalse(trajectoryView.isAiming(), "isAiming() devrait retourner false");
    }

    @Test
    public void testAimingToggle() {
        assertFalse(trajectoryView.isAiming());
        
        trajectoryView.setAiming(true);
        assertTrue(trajectoryView.isAiming());
        
        trajectoryView.setAiming(false);
        assertFalse(trajectoryView.isAiming());
        
        trajectoryView.setAiming(true);
        assertTrue(trajectoryView.isAiming());
    }

    // ========== Tests isAiming ==========
    @Test
    public void testIsAimingAfterSetAiming() {
        trajectoryView.setAiming(true);
        assertTrue(trajectoryView.isAiming());
        
        trajectoryView.setAiming(false);
        assertFalse(trajectoryView.isAiming());
    }

    // ========== Tests draw ==========
    @Test
    public void testDrawWithoutInitialization() {
        // Tester draw sans initialiser les paramètres (doit ne pas lancer d'exception)
        assertDoesNotThrow(() -> trajectoryView.draw(null, null));
    }

    @Test
    public void testDrawWithoutAiming() {
        trajectoryView.setShooter(character);
        trajectoryView.setWeapon(weapon);
        trajectoryView.setAiming(false);
        
        // Draw ne devrait rien faire si aiming est false
        assertDoesNotThrow(() -> trajectoryView.draw(null, null));
    }

    @Test
    public void testDrawWithGraphics() {
        BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        
        trajectoryView.setShooter(character);
        trajectoryView.setWeapon(weapon);
        trajectoryView.setAiming(true);
        
        assertDoesNotThrow(() -> trajectoryView.draw(g2, world));
        g2.dispose();
    }

    // ========== Tests d'intégration ==========
    @Test
    public void testTrajectoryViewFullSetup() {
        trajectoryView.setShooter(character);
        trajectoryView.setWeapon(weapon);
        trajectoryView.setShootingAngle(45.0);
        trajectoryView.setAiming(true);
        
        assertTrue(trajectoryView.isAiming());
        assertDoesNotThrow(() -> trajectoryView.draw(null, world));
    }

    @Test
    public void testCompleteWorkflow() {
        // Test un flux complet de configuration et d'affichage
        trajectoryView.setShooter(character);
        assertFalse(trajectoryView.isAiming());
        
        trajectoryView.setWeapon(weapon);
        trajectoryView.setShootingAngle(90.0);
        
        trajectoryView.setAiming(true);
        assertTrue(trajectoryView.isAiming());
        
        BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        
        assertDoesNotThrow(() -> trajectoryView.draw(g2, world));
        
        trajectoryView.setAiming(false);
        assertFalse(trajectoryView.isAiming());
        
        g2.dispose();
    }

    @Test
    public void testMultipleAngleChanges() {
        trajectoryView.setShooter(character);
        trajectoryView.setWeapon(weapon);
        trajectoryView.setAiming(true);
        
        int[] angles = {0, 45, 90, 135, 180, 225, 270, 315, 360};
        
        for (int angle : angles) {
            assertDoesNotThrow(() -> trajectoryView.setShootingAngle(angle));
            assertDoesNotThrow(() -> trajectoryView.draw(null, world));
        }
    }

    @Test
    public void testChangeWeaponWhileAiming() {
        trajectoryView.setShooter(character);
        trajectoryView.setAiming(true);
        
        Weapon weapon2 = new Weapon("Second Weapon", 10, false, 15, 5, 20, 40, 1.0) {
            @Override
            protected boolean onUse() {
                return true;
            }
        };
        
        trajectoryView.setWeapon(weapon);
        assertDoesNotThrow(() -> trajectoryView.draw(null, world));
        
        trajectoryView.setWeapon(weapon2);
        assertDoesNotThrow(() -> trajectoryView.draw(null, world));
    }

    //Tests d'intégration
    @Test
    void testTrajectoryWithPhysicsWorld() {
        // Test l'intégration avec le moteur physique sur plusieurs frames
        trajectoryView.setShooter(character);
        trajectoryView.setWeapon(weapon);
        trajectoryView.setAiming(true);
        
    }

    @Test
    void testTrajectoryIntegrationWithTerrainCollision() {
        // Test avec terrain variable et angles différents
        int[] variableGround = {400, 380, 350, 330, 320, 320, 330, 350, 380, 400, 400, 400, 400, 400};
        GroundModel variableGroundModel = new GroundModel(variableGround, 800, 600, 500);
        PhysicsWorld varPhysicsWorld = new PhysicsWorld();
        World varWorld = varPhysicsWorld.getWorld();
        varPhysicsWorld.createGroundFromModel(variableGroundModel);
        
        CharacterModel charOnVariableGround = new CharacterModel(variableGroundModel, varWorld, 150, 100, false);
        trajectoryView.setShooter(charOnVariableGround);
        trajectoryView.setWeapon(weapon);
        trajectoryView.setAiming(true);
        
        // Tester plusieurs angles
        for (int angle = 0; angle <= 90; angle += 15) {
            trajectoryView.setShootingAngle(angle);
            assertDoesNotThrow(() -> trajectoryView.draw(null, varWorld));
        }
    }

    //Tests de performance 
    @Test
    void testTrajectoryRenderingPerformance() {
        // Test la performance du rendu de la trajectoire
        trajectoryView.setShooter(character);
        trajectoryView.setWeapon(weapon);
        trajectoryView.setAiming(true);
        trajectoryView.setShootingAngle(45);
        
        long startTime = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            trajectoryView.draw(null, world);
        }
        long duration = System.nanoTime() - startTime;
        
        // 100 frames doivent se faire en moins de 1 seconde
        assertTrue(duration < 1_000_000_000L, "100 frames devraient prendre moins de 1s");
    }

    @Test
    void testTrajectoryCalculationPerformance() {
        // Test la performance du calcul de trajectoire
        trajectoryView.setShooter(character);
        trajectoryView.setWeapon(weapon);
        trajectoryView.setAiming(true);
        
        long startTime = System.nanoTime();
        for (int angle = 0; angle < 1000; angle++) {
            trajectoryView.setShootingAngle(angle % 360);
        }
        long duration = System.nanoTime() - startTime;
        
        // 1000 changements d'angle doivent se faire en moins de 100ms
        assertTrue(duration < 100_000_000L, "1000 changements d'angle devraient prendre moins de 100ms");
    }
}

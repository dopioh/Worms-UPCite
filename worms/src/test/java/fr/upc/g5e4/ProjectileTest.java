package fr.upc.g5e4;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import fr.upc.g5e4.model.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe Projectile (trajectoire mathématique)
 */
public class ProjectileTest {
    private Projectile projectile;
    private Weapon weapon;
    private CharacterModel shooter;
    private World world;
    private GroundModel ground;
    private PhysicsWorld physicsWorld;

    @BeforeEach
    void setUp() {
        // Initialiser le monde physique
        physicsWorld = new PhysicsWorld();
        world = physicsWorld.getWorld();
        
        int[] groundY = {400, 400, 400, 400, 400, 400, 400, 400, 400, 400, 400, 400, 400, 400};
        ground = new GroundModel(groundY, 800, 600, 500);
        physicsWorld.createGroundFromModel(ground);
        
        // Créer le tireur
        shooter = new CharacterModel(ground, world, 100, ground.getHeight() - 400, false);
        
        // Créer une arme simple
        weapon = new Weapon("Test Weapon", 10, false, 10, 0, 10, 30, 1.0) {
            @Override
            protected boolean onUse() {
                return true;
            }
        };
        
        // Créer un projectile
        Vec2 position = new Vec2(100, 20);
        projectile = new Projectile(weapon, shooter, position, 20, 10, 45);
    }

    @Test
    void testProjectileInitialization() {
        assertNotNull(projectile);
        assertEquals(45, projectile.getShootingAngle(), 0.01);
        assertEquals(20, projectile.getRange(), 0.01);
        assertEquals(shooter, projectile.getShooter());
        assertEquals(weapon, projectile.getSourceWeapon());
    }

    @Test
    void testProjectileLifetime() {
        assertTrue(projectile.update(0.5f));  // Projectile encore vivant
        assertTrue(projectile.update(2.0f));  // Projectile encore vivant
        assertTrue(projectile.update(3.0f));  // Projectile encore vivant
        assertFalse(projectile.update(5.0f)); // Projectile mort (MAX_LIFETIME = 10)
    }

    @Test
    void testGetPositionAtTime() {
        // t = 0 : position de départ
        Vec2 posAtStart = projectile.getPositionAtTime(0);
        assertNotNull(posAtStart);
        
        // t = 0.5 : position au milieu
        Vec2 posAtMid = projectile.getPositionAtTime(0.5f);
        assertNotNull(posAtMid);
        
        // t = 1 : position finale
        Vec2 posAtEnd = projectile.getPositionAtTime(1);
        assertNotNull(posAtEnd);
        
        // Vérifier que les positions sont différentes
        assertNotEquals(posAtStart.x, posAtEnd.x, 0.01);
    }

    @Test
    void testTrajectorySymmetry() {
        // Avec un angle de 45°, la trajectoire doit être symétrique en horizontal
        Vec2 pos_0_25 = projectile.getPositionAtTime(0.25f);
        Vec2 pos_0_75 = projectile.getPositionAtTime(0.75f);
        
        // Le X doit être différent (distance parcourue croissante)
        assertTrue(pos_0_25.x < pos_0_75.x);
        // Les hauteurs devraient être comparables en raison de la parabole
        assertTrue(Math.abs(pos_0_25.y - pos_0_75.y) < 10);
    }

    @Test
    void testTrajectoryAngle0() {
        // Créer un projectile tirant à 0° (horizontalement à droite)
        Vec2 position = new Vec2(0, 10);
        Projectile projectile0 = new Projectile(weapon, shooter, position, 20, 10, 0);
        
        Vec2 pos_0_5 = projectile0.getPositionAtTime(0.5f);
        
        // À 0°, le projectile doit progresser horizontalement (X > 0)
        assertTrue(pos_0_5.x > position.x);
        // Le Y doit baisser à cause de la courbe parabolique
        assertTrue(pos_0_5.y < position.y);
    }

    @Test
    void testTrajectoryAngle90() {
        // Créer un projectile tirant à 90° (verticalement vers le haut)
        Vec2 position = new Vec2(0, 0);
        Projectile projectile90 = new Projectile(weapon, shooter, position, 20, 10, 90);
        
        Vec2 pos_0_5 = projectile90.getPositionAtTime(0.5f);
        
        // À 90°, le projectile doit aller vers le haut (Y > 0)
        assertTrue(pos_0_5.y > position.y);
        // X ne doit pas beaucoup changer (cos(90°) ≈ 0)
        assertEquals(position.x, pos_0_5.x, 0.5f);
    }

    @Test
    void testTrajectoryAngle180() {
        // Créer un projectile tirant à 180° (horizontalement à gauche)
        Vec2 position = new Vec2(100, 10);
        Projectile projectile180 = new Projectile(weapon, shooter, position, 20, 10, 180);
        
        Vec2 pos_0_5 = projectile180.getPositionAtTime(0.5f);
        
        // À 180°, le projectile doit aller à gauche (X < 100)
        assertTrue(pos_0_5.x < position.x);
        // Le Y doit baisser à cause de la courbe parabolique
        assertTrue(pos_0_5.y < position.y);
    }

    @Test
    void testTrajectoryAngle270() {
        // Créer un projectile tirant à 270° (verticalement vers le bas)
        Vec2 position = new Vec2(0, 20);
        Projectile projectile270 = new Projectile(weapon, shooter, position, 20, 10, 270);
        
        Vec2 pos_0_5 = projectile270.getPositionAtTime(0.5f);
        
        // À 270°, le projectile doit aller vers le bas (Y < 20)
        assertTrue(pos_0_5.y < position.y);
    }

    @Test
    void testGetCurrentPosition() {
        // Vérifier que getCurrentPosition utilise correctement la durée de vie
        projectile.update(1.0f);
        Vec2 currentPos = projectile.getCurrentPosition();
        assertNotNull(currentPos);
        
        // Vérifier que c'est une position valide
        assertTrue(currentPos.x >= 0);
    }

    @Test
    void testPositionAtTimeOutOfBounds() {
        // t < 0 doit être clampé à 0
        Vec2 posBelowZero = projectile.getPositionAtTime(-1.0f);
        Vec2 posAtZero = projectile.getPositionAtTime(0);
        assertEquals(posAtZero.x, posBelowZero.x, 0.01);
        assertEquals(posAtZero.y, posBelowZero.y, 0.01);
        
        // t > 1 doit être clampé à 1
        Vec2 posAboveOne = projectile.getPositionAtTime(2.0f);
        Vec2 posAtOne = projectile.getPositionAtTime(1);
        assertEquals(posAtOne.x, posAboveOne.x, 0.01);
        assertEquals(posAtOne.y, posAboveOne.y, 0.01);
    }

    @Test
    void testProjectileWithDifferentAngles() {
        int[] angles = {0, 30, 45, 60, 90, 120, 180, 270, 315, 359};
        
        for (int angle : angles) {
            Vec2 position = new Vec2(0, 0);
            Projectile projectileAngled = new Projectile(weapon, shooter, position, 20, 10, angle);
            
            // Vérifier que le projectile a l'angle correct
            assertEquals(angle, projectileAngled.getShootingAngle(), 0.01);
            
            // Vérifier que la position est calculable
            Vec2 pos = projectileAngled.getPositionAtTime(0.5f);
            assertNotNull(pos);
        }
    }

    @Test
    void testProjectileParabolicCurve() {
        // Vérifier que la courbe parabolique fonctionne correctement
        Vec2 position = new Vec2(0, 10);
        Projectile projectileHorizontal = new Projectile(weapon, shooter, position, 20, 10, 0);
        
        Vec2 pos_0_0 = projectileHorizontal.getPositionAtTime(0.0f);
        Vec2 pos_0_25 = projectileHorizontal.getPositionAtTime(0.25f);
        Vec2 pos_0_5 = projectileHorizontal.getPositionAtTime(0.5f);
        Vec2 pos_0_75 = projectileHorizontal.getPositionAtTime(0.75f);
        @SuppressWarnings("unused")
        Vec2 pos_1_0 = projectileHorizontal.getPositionAtTime(1.0f);
        
        // La trajectoire doit descendre au début à cause de la courbe parabolique
        assertTrue(pos_0_0.y >= pos_0_25.y);
        assertTrue(pos_0_25.y >= pos_0_5.y);
        
        // La progression en X doit être croissante
        assertTrue(pos_0_0.x < pos_0_25.x);
        assertTrue(pos_0_25.x < pos_0_5.x);
        assertTrue(pos_0_5.x < pos_0_75.x);
    }

    @Test
    void testShooterReference() {
        assertEquals(shooter, projectile.getShooter());
        assertNotNull(projectile.getShooter());
    }

    @Test
    void testWeaponReference() {
        assertEquals(weapon, projectile.getSourceWeapon());
        assertNotNull(projectile.getSourceWeapon());
    }

    @Test
    void testRangeGetter() {
        assertEquals(20, projectile.getRange(), 0.01);
    }
}

package fr.upc.g5e4.model.weapons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import fr.upc.g5e4.model.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe Projectile (trajectoire mathématique)
 * Coordonnés avec l'implémentation réelle de la classe Projectile
 */
public class ProjectileTest {
    private ProjectileModel projectile;
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
        projectile = new ProjectileModel(weapon, shooter, position, 20, 10, 45);
    }

    // ========== Tests d'initialisation ==========
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

    // ========== Tests de position ==========
    @Test
    void testGetPositionAtTime() {
        // t = 0 : position de départ
        Vec2 posAtStart = projectile.getPositionAtTime(0);
        assertNotNull(posAtStart);
        assertEquals(100, posAtStart.x, 0.5);
        assertEquals(20, posAtStart.y, 0.5);
        
        // t = 0.5 : position au milieu
        Vec2 posAtMid = projectile.getPositionAtTime(0.5f);
        assertNotNull(posAtMid);
        
        // t = 1 : position finale
        Vec2 posAtEnd = projectile.getPositionAtTime(1);
        assertNotNull(posAtEnd);
        
        // Vérifier que les positions progressent en X
        assertTrue(posAtStart.x <= posAtMid.x);
        assertTrue(posAtMid.x <= posAtEnd.x);
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

    // ========== Tests avec différents angles ==========
    @Test
    void testTrajectoryAngle0Degrees() {
        // Créer un projectile tirant à 0° (horizontalement à droite)
        Vec2 position = new Vec2(0, 10);
        ProjectileModel projectile0 = new ProjectileModel(weapon, shooter, position, 20, 10, 0);
        
        Vec2 pos_0_0 = projectile0.getPositionAtTime(0.0f);
        Vec2 pos_0_5 = projectile0.getPositionAtTime(0.5f);
        Vec2 pos_1_0 = projectile0.getPositionAtTime(1.0f);
        
        // À 0°, cos(0) = 1, sin(0) = 0
        // X progresse linéairement, Y reste stable
        assertTrue(pos_0_0.x <= pos_0_5.x, "X doit augmenter");
        assertTrue(pos_0_5.x <= pos_1_0.x, "X doit augmenter");
        assertEquals(position.y, pos_0_0.y, 0.1, "Y devrait rester constant à 0°");
    }

    @Test
    void testTrajectoryAngle45Degrees() {
        // À 45°, le projectile progresse diagonalement
        Vec2 pos_0_0 = projectile.getPositionAtTime(0.0f);
        Vec2 pos_0_5 = projectile.getPositionAtTime(0.5f);
        Vec2 pos_1_0 = projectile.getPositionAtTime(1.0f);
        
        // X progresse constamment
        assertTrue(pos_0_0.x < pos_0_5.x);
        assertTrue(pos_0_5.x < pos_1_0.x);
        
        // Y aussi progresse (car sin(45°) > 0)
        assertTrue(pos_0_0.y < pos_0_5.y);
        assertTrue(pos_0_5.y < pos_1_0.y);
    }

    @Test
    void testTrajectoryAngle90Degrees() {
        // Créer un projectile tirant à 90° (verticalement vers le haut)
        Vec2 position = new Vec2(0, 0);
        ProjectileModel projectile90 = new ProjectileModel(weapon, shooter, position, 20, 10, 90);
        
        Vec2 pos_0_0 = projectile90.getPositionAtTime(0.0f);
        Vec2 pos_0_5 = projectile90.getPositionAtTime(0.5f);
        Vec2 pos_1_0 = projectile90.getPositionAtTime(1.0f);
        
        // À 90°, cos(90°) ≈ 0, sin(90°) = 1
        // X reste constant, Y progresse
        assertEquals(position.x, pos_0_0.x, 0.1, "X devrait être constant à 90°");
        assertEquals(position.x, pos_0_5.x, 0.1, "X devrait être constant à 90°");
        
        // Y augmente
        assertTrue(pos_0_0.y <= pos_0_5.y);
        assertTrue(pos_0_5.y <= pos_1_0.y);
    }

    @Test
    void testTrajectoryAngle180Degrees() {
        // Créer un projectile tirant à 180° (horizontalement à gauche)
        Vec2 position = new Vec2(100, 10);
        ProjectileModel projectile180 = new ProjectileModel(weapon, shooter, position, 20, 10, 180);
        
        Vec2 pos_0_0 = projectile180.getPositionAtTime(0.0f);
        Vec2 pos_0_5 = projectile180.getPositionAtTime(0.5f);
        Vec2 pos_1_0 = projectile180.getPositionAtTime(1.0f);
        
        // À 180°, cos(180°) = -1, sin(180°) = 0
        // X diminue (direction inverse), Y reste stable
        assertTrue(pos_0_0.x >= pos_0_5.x, "X doit diminuer à 180°");
        assertTrue(pos_0_5.x >= pos_1_0.x, "X doit diminuer à 180°");
        assertEquals(position.y, pos_0_0.y, 0.1, "Y devrait rester constant à 180°");
    }

    @Test
    void testTrajectoryAngle270Degrees() {
        // Créer un projectile tirant à 270° (verticalement vers le bas)
        Vec2 position = new Vec2(0, 20);
        ProjectileModel projectile270 = new ProjectileModel(weapon, shooter, position, 20, 10, 270);
        
        Vec2 pos_0_0 = projectile270.getPositionAtTime(0.0f);
        Vec2 pos_0_5 = projectile270.getPositionAtTime(0.5f);
        Vec2 pos_1_0 = projectile270.getPositionAtTime(1.0f);
        
        // À 270°, cos(270°) ≈ 0, sin(270°) = -1
        // X reste constant, Y diminue
        assertEquals(position.x, pos_0_0.x, 0.1, "X devrait être constant à 270°");
        
        // Y diminue
        assertTrue(pos_0_0.y >= pos_0_5.y, "Y doit diminuer à 270°");
        assertTrue(pos_0_5.y >= pos_1_0.y, "Y doit diminuer à 270°");
    }

    @Test
    void testProjectileWithDifferentAngles() {
        int[] angles = {0, 30, 45, 60, 90, 120, 180, 270, 315, 359};
        
        for (int angle : angles) {
            Vec2 position = new Vec2(0, 0);
            ProjectileModel projectileAngled = new ProjectileModel(weapon, shooter, position, 20, 10, angle);
            
            // Vérifier que le projectile a l'angle correct
            assertEquals(angle, projectileAngled.getShootingAngle(), 0.01);
            
            // Vérifier que la position est calculable
            Vec2 pos = projectileAngled.getPositionAtTime(0.5f);
            assertNotNull(pos);
            
            // Vérifier que la position initiale est correcte
            Vec2 posStart = projectileAngled.getPositionAtTime(0);
            assertEquals(position.x, posStart.x, 0.1);
            assertEquals(position.y, posStart.y, 0.1);
        }
    }

    @Test
    void testProjectileProgressionLinearity() {
        // Vérifier que la progression est linéaire en fonction de t
        Vec2 pos_0_0 = projectile.getPositionAtTime(0.0f);
        Vec2 pos_0_25 = projectile.getPositionAtTime(0.25f);
        Vec2 pos_0_5 = projectile.getPositionAtTime(0.5f);
        Vec2 pos_0_75 = projectile.getPositionAtTime(0.75f);
        Vec2 pos_1_0 = projectile.getPositionAtTime(1.0f);
        
        // Les positions doivent être en progression croissante
        assertTrue(pos_0_0.x <= pos_0_25.x);
        assertTrue(pos_0_25.x <= pos_0_5.x);
        assertTrue(pos_0_5.x <= pos_0_75.x);
        assertTrue(pos_0_75.x <= pos_1_0.x);
        
        assertTrue(pos_0_0.y <= pos_0_25.y);
        assertTrue(pos_0_25.y <= pos_0_5.y);
        assertTrue(pos_0_5.y <= pos_0_75.y);
        assertTrue(pos_0_75.y <= pos_1_0.y);
    }

    // ========== Tests des getters ==========
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

    @Test
    void testAngleGetter() {
        assertEquals(45, projectile.getShootingAngle(), 0.01);
    }

    // ========== Tests de stabilité ==========
    @Test
    void testMultipleUpdates() {
        assertTrue(projectile.update(1.0f));
        assertTrue(projectile.update(2.0f));
        assertTrue(projectile.update(3.0f));
        assertTrue(projectile.update(3.0f));  // Cumul = 9f, encore vivant
        assertFalse(projectile.update(2.0f)); // Cumul = 11f, mort
    }

    @Test
    void testProjectileMultiplePositionQueries() {
        // Vérifier que plusieurs appels ne modifient pas l'état
        Vec2 pos1 = projectile.getPositionAtTime(0.5f);
        Vec2 pos2 = projectile.getPositionAtTime(0.5f);
        
        assertEquals(pos1.x, pos2.x, 0.01);
        assertEquals(pos1.y, pos2.y, 0.01);
    }

    @Test
    void testStartPositionImmutable() {
        // Vérifier que la position de départ n'est pas modifiée
        Vec2 posStart1 = projectile.getPositionAtTime(0);
        
        // Faire des appels multiples
        projectile.getPositionAtTime(0.25f);
        projectile.getPositionAtTime(0.5f);
        projectile.getPositionAtTime(1.0f);
        projectile.update(1.0f);
        
        Vec2 posStart2 = projectile.getPositionAtTime(0);
        assertEquals(posStart1.x, posStart2.x, 0.01);
        assertEquals(posStart1.y, posStart2.y, 0.01);
    }

    //Tests d'intégration
    @Test
    void testMultipleProjectilesIntegration() {
        // Test plusieurs projectiles en simultané
        ProjectileModel proj1 = new ProjectileModel(weapon, shooter, new Vec2(100, 20), 20, 10, 45);
        ProjectileModel proj2 = new ProjectileModel(weapon, shooter, new Vec2(150, 25), 15, 8, 30);
        ProjectileModel proj3 = new ProjectileModel(weapon, shooter, new Vec2(200, 30), 25, 12, 60);
        
        // Simuler plusieurs frames
        for (int i = 0; i < 50; i++) {
            float deltaTime = 0.1f;
            boolean a1 = proj1.update(deltaTime);
            boolean a2 = proj2.update(deltaTime);
            boolean a3 = proj3.update(deltaTime);
            
            // Vérifier les positions
            assertNotNull(proj1.getCurrentPosition());
            assertNotNull(proj2.getCurrentPosition());
            assertNotNull(proj3.getCurrentPosition());
        }
    }

    @Test
    void testProjectileWithPhysicsWorld() {
        // Test projectile avec monde physique
        projectile.update(0.5f);
        Vec2 pos = projectile.getCurrentPosition();
        
        // Vérifier que la position est cohérente avec le monde
        assertNotNull(pos);
        assertTrue(pos.x >= 0 && pos.x <= 800, "Position X dans les limites du monde");
        
        // Simuler plusieurs steps physiques
        for (int i = 0; i < 60; i++) {
            world.step(1/60f, 8, 3);
            projectile.update(1/60f);
        }
        
        Vec2 finalPos = projectile.getCurrentPosition();
        assertNotNull(finalPos);
    }

    //Tests de performance 
    @Test
    void testProjectileCreationPerformance() {
        // Test la performance de création de projectiles
        long startTime = System.nanoTime();
        
        for (int i = 0; i < 1000; i++) {
            Vec2 pos = new Vec2(100 + i % 100, 20 + i % 50);
            ProjectileModel proj = new ProjectileModel(weapon, shooter, pos, 20, 10, i % 360);
            assertNotNull(proj);
        }
        
        long duration = System.nanoTime() - startTime;
        // 1000 créations doivent se faire en moins de 100ms
        assertTrue(duration < 100_000_000L, "1000 créations devraient prendre moins de 100ms");
    }

    @Test
    void testProjectileUpdatePerformance() {
        // Test la performance des mises à jour
        long startTime = System.nanoTime();
        
        for (int i = 0; i < 10000; i++) {
            projectile.getPositionAtTime(i / 10000f);
        }
        
        long duration = System.nanoTime() - startTime;
        // 10000 calculs de position doivent se faire en moins de 50ms
        assertTrue(duration < 50_000_000L, "10000 calculs de position devraient prendre moins de 50ms");
    }
}

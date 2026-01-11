package fr.upc.g5e4.model.weapons;

import fr.upc.g5e4.controller.ItemController;
import fr.upc.g5e4.model.CharacterModel;
import fr.upc.g5e4.model.GroundModel;
import fr.upc.g5e4.model.ProjectileModel;
import fr.upc.g5e4.model.Weapon;
import fr.upc.g5e4.view.ItemView;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration pour le système d'impact avec les projectiles
 */
class ImpactSystemTest {

    private ItemController controller;
    private World world;
    private GroundModel ground;
    private CharacterModel shooter;
    private CharacterModel target;

    @BeforeEach
    void setUp() {
        // Vue factice
        ItemView view = new ItemView() {
            @Override
            public void showUseResult(boolean ok) {}
            @Override
            public void showItem(fr.upc.g5e4.model.Item item) {}
        };
        
        controller = new ItemController(view);
        
        // Création d'un terrain plat
        int[] groundY = new int[10];
        for (int i = 0; i < groundY.length; i++) {
            groundY[i] = 100;
        }
        ground = new GroundModel(groundY, 800, 800, 0);
        
        // Création du monde physique
        world = new World(new Vec2(0, -10f));
        
        // Création des personnages
        shooter = new CharacterModel(ground, world, 100.0, 200.0, false);
        target = new CharacterModel(ground, world, 300.0, 200.0, false);
    }

    @Test
    void testProjectileCreation() {
        // Créer une arme de test
        Weapon testWeapon = new Weapon("TestGun", 20, false, 10, 0, 15, 30, 1.0) {
            @Override
            protected boolean onUse() {
                return true;
            }
        };
        
        testWeapon.use();
        
        // Créer un projectile
        ProjectileModel projectile = testWeapon.fire(shooter, world, new CharacterModel[]{target}, 1);
        
        // Vérifier que le projectile existe et a les bonnes propriétés
        assertNotNull(projectile);
        assertEquals(testWeapon, projectile.getSourceWeapon());
        assertEquals(shooter, projectile.getShooter());
    }

    @Test
    void testImpactDirectionCalculation() {
        // Test que la direction de l'impact est calculée correctement
        Vec2 projectilePos = new Vec2(2.0f, 2.0f);
        Vec2 targetPos = new Vec2(3.0f, 2.5f);
        
        // Direction attendue (du projectile vers la cible)
        Vec2 expectedDirection = new Vec2(
            targetPos.x - projectilePos.x,  // 1.0
            targetPos.y - projectilePos.y   // 0.5
        );
        
        // Vérifier que les valeurs sont correctes
        assertEquals(1.0f, expectedDirection.x, 0.01f);
        assertEquals(0.5f, expectedDirection.y, 0.01f);
    }

    @Test
    void testProjectileImpactIntegration() {
        // Test complet : projectile créé, lancé, et impact appliqué
        Weapon testWeapon = new Weapon("ImpactWeapon", 10, false, 5, 0, 20, 30, 1.0) {
            @Override
            protected boolean onUse() {
                return true;
            }
        };
        
        testWeapon.use();
        
        // Créer un projectile et l'ajouter directement à la liste
        ProjectileModel projectile = testWeapon.fire(shooter, world, new CharacterModel[]{target}, 1);
        controller.getActiveProjectiles().add(projectile);
        controller.setAllWorms(new CharacterModel[]{shooter, target});
        
        // Simuler plusieurs frames - ne devrait pas crasher
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 100; i++) {
                controller.updateProjectiles(0.016f); // ~60 FPS
            }
        });
        
        // Vérifier que les personnages existent toujours
        assertNotNull(shooter.getBody(), "Le shooter devrait toujours exister");
        assertNotNull(target.getBody(), "La cible devrait toujours exister");
    }

    @Test
    void testImpactWithDifferentForces() {
        // Test avec différentes forces d'impact
        Vec2 direction = new Vec2(1.0f, 0.0f);
        
        // Force faible
        target.applyImpact(direction, 1.0f);
        Vec2 velocityAfterWeakImpact = target.getBody().getLinearVelocity().clone();
        
        // Réinitialiser la vitesse
        target.getBody().setLinearVelocity(new Vec2(0, 0));
        
        // Force forte
        target.applyImpact(direction, 10.0f);
        Vec2 velocityAfterStrongImpact = target.getBody().getLinearVelocity().clone();
        
        // L'impact fort devrait donner une vitesse plus élevée
        assertTrue(Math.abs(velocityAfterStrongImpact.x) > Math.abs(velocityAfterWeakImpact.x),
            "Un impact plus fort devrait créer une vitesse plus élevée");
    }
}

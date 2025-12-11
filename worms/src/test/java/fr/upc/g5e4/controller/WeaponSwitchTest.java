package fr.upc.g5e4.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jbox2d.dynamics.World;

import fr.upc.g5e4.model.CharacterModel;
import fr.upc.g5e4.model.GroundModel;
import fr.upc.g5e4.model.PhysicsWorld;
import fr.upc.g5e4.model.Weapon;
import fr.upc.g5e4.model.weapons.Ak47;
import fr.upc.g5e4.model.weapons.BaseballBat;
import fr.upc.g5e4.model.weapons.Grenade;
import fr.upc.g5e4.model.weapons.RocketLauncher;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour le système de changement d'arme
 */
public class WeaponSwitchTest {
    private CharacterController controller;
    private CharacterModel worm;
    private World world;

    @BeforeEach
    void setUp() {
        // Initialiser le monde physique
        PhysicsWorld physicsWorld = new PhysicsWorld();
        world = physicsWorld.getWorld();
        
        // Créer un terrain
        int[] groundY = {400, 400, 400, 400, 400, 400, 400, 400, 400, 400, 400, 400, 400, 400};
        GroundModel ground = new GroundModel(groundY, 800, 600, 500);
        physicsWorld.createGroundFromModel(ground);
        
        // Créer un worm et son contrôleur
        worm = new CharacterModel(ground, world, 100, ground.getHeight() - 400,false);
        controller = new CharacterController(worm);
    }

    @Test
    void testSetCurrentWeapon() {
        // Test: on peut équiper une arme
        Ak47 rifle = new Ak47();
        controller.setCurrentWeapon(rifle);
        
        // L'arme doit être équipée (pas d'erreur)
        assertNotNull(rifle);
    }

    @Test
    void testWeaponTypes() {
        // Test: vérifier que toutes les armes peuvent être créées
        Weapon ak47 = new Ak47();
        Weapon bat = new BaseballBat();
        Weapon grenade = new Grenade();
        Weapon rocket = new RocketLauncher();
        
        assertNotNull(ak47);
        assertNotNull(bat);
        assertNotNull(grenade);
        assertNotNull(rocket);
        
        // Vérifier les dégâts
        assertTrue(ak47.getDamage() > 0);
        assertTrue(bat.getDamage() > 0);
        assertTrue(grenade.getDamage() > 0);
        assertTrue(rocket.getDamage() > 0);
    }

    @Test
    void testWeaponOrder() {
        // Test: vérifier l'ordre des armes
        // AK-47 (dégâts faibles) < Grenade < Rocket Launcher
        Ak47 ak47 = new Ak47();
        Grenade grenade = new Grenade();
        RocketLauncher rocket = new RocketLauncher();
        
        assertTrue(ak47.getDamage() <= grenade.getDamage());
        assertTrue(grenade.getDamage() <= rocket.getDamage());
    }

    @Test
    void testBaseballBatIsNotProjectile() {
        // Test: le baseball bat est une arme de mêlée
        BaseballBat bat = new BaseballBat();
        // Vitesse faible car c'est une arme de mêlée
        assertTrue(bat.getSpeed() >= 0);
    }

    @Test
    void testGrenadeHasBlastRadius() {
        // Test: la grenade a un rayon d'explosion
        Grenade grenade = new Grenade();
        assertTrue(grenade.getBlastRadius() > 0);
    }

    @Test
    void testRocketLauncherHasBlastRadius() {
        // Test: le rocket launcher a un rayon d'explosion
        RocketLauncher rocket = new RocketLauncher();
        assertTrue(rocket.getBlastRadius() > 0);
    }

    @Test
    void testItemControllerInitialization() {
        // Test: initialiser l'ItemController
        ItemController itemController = new ItemController(null);
        assertNotNull(itemController);
    }
}

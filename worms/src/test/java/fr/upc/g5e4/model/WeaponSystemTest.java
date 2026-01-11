package fr.upc.g5e4.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jbox2d.dynamics.World;

import fr.upc.g5e4.model.CharacterModel;
import fr.upc.g5e4.model.GroundModel;
import fr.upc.g5e4.model.PhysicsWorld;
import fr.upc.g5e4.model.weapons.Ak47;
import fr.upc.g5e4.model.weapons.Grenade;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour le système de tir et les armes
 */
public class WeaponSystemTest {
    private World world;
    private GroundModel ground;
    private CharacterModel shooter;
    private CharacterModel target;

    @BeforeEach
    void setUp() {
        PhysicsWorld physicsWorld = new PhysicsWorld();
        world = physicsWorld.getWorld();
        
        int[] groundY = {400, 400, 400, 400, 400, 400, 400, 400, 400, 400, 400, 400, 400, 400};
        ground = new GroundModel(groundY, 800, 600, 500);
        physicsWorld.createGroundFromModel(ground);
        
        shooter = new CharacterModel(ground, world, 100, ground.getHeight() - 400,false);
        target = new CharacterModel(ground, world, 300, ground.getHeight() - 400,false);
    }

    @Test
    void testDealDamageShieldAbsorption() {
        double initialShield = target.getShield();
        double initialHealth = target.getHealth();
        double damage = 10;
        
        target.dealDamage(damage, shooter);
        
        assertEquals(initialShield - damage, target.getShield(), 0.01);
        assertEquals(initialHealth, target.getHealth(), 0.01);
        assertFalse(target.isDead());
    }

    @Test
    void testDealDamageShieldBroken() {
        double initialHealth = target.getHealth();
        double shieldValue = target.getShield();
        double damage = shieldValue + 10;
        
        target.dealDamage(damage, shooter);
        
        assertEquals(0, target.getShield(), 0.01);
        assertEquals(initialHealth - 10, target.getHealth(), 0.01);
        assertFalse(target.isDead());
    }

    @Test
    void testDealDamageKill() {
        double health = target.getHealth();
        double shield = target.getShield();
        double totalDamage = health + shield + 10;
        
        target.dealDamage(totalDamage, shooter);
        
        assertTrue(target.isDead());
    }

    @Test
    void testWeaponDamage() {
        Ak47 rifle = new Ak47();
        assertEquals(20, rifle.getDamage());
    }

    @Test
    void testWeaponBlastRadius() {
        Grenade grenade = new Grenade();
        assertTrue(grenade.getBlastRadius() > 0);
    }

    @Test
    void testWeaponProperties() {
        Ak47 rifle = new Ak47();
        assertTrue(rifle.getDamage() > 0);
        assertTrue(rifle.getSpeed() > 0);
        assertEquals(0, rifle.getBlastRadius());
        
        Grenade grenade = new Grenade();
        assertTrue(grenade.getDamage() > 0);
        assertTrue(grenade.getBlastRadius() > 0);
    }

    @Test
    void testCharacterNotDeadByDefault() {
        assertFalse(target.isDead());
    }
}

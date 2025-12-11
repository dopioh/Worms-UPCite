package fr.upc.g5e4.controller;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jbox2d.dynamics.World;

import fr.upc.g5e4.model.*;

/**
 * Tests unitaires pour les modifications du système de projectile
 */
public class ProjectileSystemTest {
    private Weapon weapon;
    private CharacterModel shooter;
    private CharacterModel target;
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
        
        // Créer le tireur et la cible
        shooter = new CharacterModel(ground, world, 100, ground.getHeight() - 400, false);
        target = new CharacterModel(ground, world, 300, ground.getHeight() - 400, false);
        
        // Créer une arme
        weapon = new Weapon("Test Weapon", 10, false, 10, 0, 10, 30) {
            @Override
            protected boolean onUse() {
                return true;
            }
        };
    }

    @Test
    void testWeaponShootingAngle() {
        // Tester le stockage et récupération de l'angle
        weapon.setShootingAngle(45);
        assertEquals(45, weapon.getShootingAngle(), 0.01);
        
        weapon.setShootingAngle(90);
        assertEquals(90, weapon.getShootingAngle(), 0.01);
        
        weapon.setShootingAngle(180);
        assertEquals(180, weapon.getShootingAngle(), 0.01);
    }

    @Test
    void testWeaponAngleWrapping() {
        // L'angle doit être modulo 360
        weapon.setShootingAngle(360);
        assertEquals(0, weapon.getShootingAngle(), 0.01);
        
        weapon.setShootingAngle(375);
        assertEquals(15, weapon.getShootingAngle(), 0.01);
        
        weapon.setShootingAngle(720);
        assertEquals(0, weapon.getShootingAngle(), 0.01);
    }

    @Test
    void testFireReturnsProjectile() {
        // Tester que fire() retourne un objet Projectile
        weapon.setShootingAngle(45);
        weapon.use();
        
        Projectile projectile = weapon.fire(shooter, world, new CharacterModel[]{target}, 1);
        
        assertNotNull(projectile);
        assertEquals(45, projectile.getShootingAngle(), 0.01);
        assertEquals(shooter, projectile.getShooter());
        assertEquals(weapon, projectile.getSourceWeapon());
    }

    @Test
    void testFireWithDifferentAngles() {
        int[] angles = {0, 30, 45, 90, 180, 270};
        
        for (int angle : angles) {
            weapon.setShootingAngle(angle);
            weapon.use();
            
            Projectile projectile = weapon.fire(shooter, world, new CharacterModel[]{target}, 1);
            
            assertEquals(angle, projectile.getShootingAngle(), 0.01);
            assertEquals(shooter, projectile.getShooter());
        }
    }

    @Test
    void testProjectileRangeFromWeapon() {
        weapon.setShootingAngle(45);
        weapon.use();
        
        Projectile projectile = weapon.fire(shooter, world, new CharacterModel[]{target}, 1);
        
        // Le projectile doit avoir la portée de l'arme
        assertEquals(weapon.getRange(), projectile.getRange(), 0.01);
    }

    @Test
    void testProjectileSpeedFromWeapon() {
        // Créer une arme avec une vitesse spécifique
        Weapon fastWeapon = new Weapon("Fast Weapon", 10, false, 15, 0, 25, 40) {
            @Override
            protected boolean onUse() {
                return true;
            }
        };
        
        fastWeapon.setShootingAngle(45);
        fastWeapon.use();
        
        Projectile projectile = fastWeapon.fire(shooter, world, new CharacterModel[]{target}, 1);
        
        assertNotNull(projectile);
        // Vérifier que le projectile est créé avec les paramètres corrects
        assertEquals(45, projectile.getShootingAngle(), 0.01);
    }

    @Test
    void testWeaponUsageConsumption() {
        Weapon limitedWeapon = new Weapon("Limited Weapon", 3, false, 10, 0, 10, 30) {
            @Override
            protected boolean onUse() {
                return true;
            }
        };
        
        // Tirer 3 fois
        assertTrue(limitedWeapon.use());
        assertTrue(limitedWeapon.use());
        assertTrue(limitedWeapon.use());
        
        // Au-delà de 3 utilisations, devrait être false
        assertFalse(limitedWeapon.use());
    }

    @Test
    void testInfiniteWeapon() {
        Weapon infiniteWeapon = new Weapon("Infinite Weapon", 1, true, 10, 0, 10, 30) {
            @Override
            protected boolean onUse() {
                return true;
            }
        };
        
        // Une arme infinie peut être utilisée indéfiniment
        for (int i = 0; i < 100; i++) {
            assertTrue(infiniteWeapon.use());
        }
    }

    @Test
    void testProjectileShooterNotNull() {
        weapon.setShootingAngle(45);
        weapon.use();
        
        Projectile projectile = weapon.fire(shooter, world, new CharacterModel[]{target}, 1);
        
        assertNotNull(projectile.getShooter());
        assertEquals(shooter.getName(), projectile.getShooter().getName());
    }

    @Test
    void testProjectileCanDealDamage() {
        weapon.setShootingAngle(45);
        weapon.use();
        
        Projectile projectile = weapon.fire(shooter, world, new CharacterModel[]{target}, 1);
        
        // Le projectile doit pouvoir accéder aux dégâts de l'arme
        assertNotNull(projectile.getSourceWeapon());
        assertEquals(weapon.getDamage(), projectile.getSourceWeapon().getDamage());
    }

    @Test
    void testMultipleProjectilesFromSameWeapon() {
        weapon.setShootingAngle(30);
        weapon.use();
        
        Projectile projectile1 = weapon.fire(shooter, world, new CharacterModel[]{target}, 1);
        
        weapon.setShootingAngle(60);
        weapon.use();
        
        Projectile projectile2 = weapon.fire(shooter, world, new CharacterModel[]{target}, 1);
        
        assertNotNull(projectile1);
        assertNotNull(projectile2);
        assertEquals(30, projectile1.getShootingAngle(), 0.01);
        assertEquals(60, projectile2.getShootingAngle(), 0.01);
    }
}

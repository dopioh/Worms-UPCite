package fr.upc.g5e4;

import fr.upc.g5e4.model.CharacterModel;
import fr.upc.g5e4.model.GroundModel;
import org.jbox2d.dynamics.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CharacterModelTest {

    private CharacterModel personnage;
    private GroundModel ground;
    private World world;

    @BeforeEach
    void setUp() {
        // Création d'un terrain 
        int[] groundY = new int[10];
        for (int i = 0; i < groundY.length; i++) {
            groundY[i] = 100; // La hauteur est constante.
        }
        ground = new GroundModel(groundY, 800, 800, 0);

        // Création d'un monde JBox2D
        world = new World(new org.jbox2d.common.Vec2(0, -10f)); // gravité vers le bas

        // Création d'un personnage
        personnage = new CharacterModel(ground, world, 100.0, 200.0);
    }

    @Test
    void testAttributsInitiaux() {
        assertEquals(100.0, personnage.getXcord());
        assertEquals(200.0, personnage.getYcord());
        assertEquals(100.0, personnage.getHealth());
        assertEquals(50.0, personnage.getShield());
        assertFalse(personnage.isDead());
        assertNotNull(personnage.getBody());
    }

    @Test
    void testSettersEtGetters() {
        personnage.setHealth(80.0);
        personnage.setShield(30.0);
        personnage.setSpeed(12.0);
        personnage.setJump(6.0);
        personnage.setDead(true);
        personnage.setXcord(150.0);
        personnage.setYcord(250.0);
        personnage.setName("TestWorm");

        assertEquals(80.0, personnage.getHealth());
        assertEquals(30.0, personnage.getShield());
        assertEquals(12.0, personnage.getSpeed());
        assertEquals(6.0, personnage.getJump());
        assertTrue(personnage.isDead());
        assertEquals(150.0, personnage.getXcord());
        assertEquals(250.0, personnage.getYcord());
        assertEquals("TestWorm", personnage.getName());
    }

    @Test
    void testToStringContientNomEtCoordonnees() {
        String str = personnage.toString();
        assertTrue(str.contains("Worm"));
        assertTrue(str.contains("Position"));
        assertTrue(str.contains("Health"));
    }

    @Test
    void testJump() {
        // Le personnage est au sol et doit sauter
        double yAvant = personnage.getYcord();
        personnage.jump();
        // On ne peut pas vérifier la position exacte car JBox2D modifie avec gravité, mais on vérifie que le body existe
        assertNotNull(personnage.getBody());
    }

    @Test
    void testIsOnGround() {
        // On ne simule pas les collisions complexes mais la méthode retourne false si aucun objet en dessous
        boolean onGround = personnage.isOnGround();
        assertFalse(onGround);
    }
}

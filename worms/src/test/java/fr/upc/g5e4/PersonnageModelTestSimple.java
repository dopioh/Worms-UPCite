package fr.upc.g5e4;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import fr.upc.g5e4.model.PersonnageModel;

/**
 * Tests unitaires pour PersonnageModel (avec Junit)
 * 
 */
public class PersonnageModelTestSimple {

    // @Test constructeurs
    /*@Test
    public void testDefaultConstructor() {
        PersonnageModel personnage = new PersonnageModel(100.0, 200.0);
        
        assertEquals(100.0, personnage.getXcord());
        assertEquals(200.0, personnage.getYcord());
        assertEquals(100.0, personnage.getHealth());
        assertEquals(50.0, personnage.getShield());
        assertEquals(3.0, personnage.getSpeed());
        assertEquals(5.0, personnage.getJump());
        assertFalse(personnage.isDead());
    }

    @Test
    public void testConstructorWithParameters() {
        PersonnageModel personnage = new PersonnageModel("TestWorm", 150.0, 250.0, 80.0, 30.0, 5.5, 7.0);
        
        assertEquals(150.0, personnage.getXcord());
        assertEquals(250.0, personnage.getYcord());
        assertEquals(80.0, personnage.getHealth());
        assertEquals(30.0, personnage.getShield());
        assertEquals(5.5, personnage.getSpeed());
        assertEquals(7.0, personnage.getJump());
        assertFalse(personnage.isDead());
    }

    // @Test getters
    @Test
    public void testID() {
        PersonnageModel personnage = new PersonnageModel(0, 0);
        assertEquals(0, personnage.getID());
    }

    @Test 
    public void testName() {
        PersonnageModel personnage = new PersonnageModel(0, 0);
        assertEquals("Worm-" + personnage.getID(), personnage.getName());
    }
    @Test
    public void testHealth() {
        PersonnageModel personnage = new PersonnageModel(0, 0);
        assertEquals(100.0, personnage.getHealth());
    }

    @Test
    public void testShield() {
        PersonnageModel personnage = new PersonnageModel(0, 0);
        assertEquals(50.0, personnage.getShield());
    }

    @Test
    public void jump() {
        PersonnageModel personnage = new PersonnageModel(0, 0);
        assertEquals(5.0, personnage.getJump());
    }

    @Test
    public void testIsDead() {
        PersonnageModel personnage = new PersonnageModel(0, 0);
        assertFalse(personnage.isDead());
    }

    @Test
    public void testSpeed() {
        PersonnageModel personnage = new PersonnageModel(0, 0);
        assertEquals(3.0, personnage.getSpeed());
    }

    // @Test setters
    @Test
    public void testSetName() {
        PersonnageModel personnage = new PersonnageModel(0, 0);
        personnage.setName("TestWorm");
        assertEquals("TestWorm", personnage.getName());
    }

    @Test
    public void testSetXcord() {
        PersonnageModel personnage = new PersonnageModel(0, 0);
        personnage.setXcord(150.5);
        assertEquals(150.5, personnage.getXcord());
    }

    @Test
    public void testSetYcord() {
        PersonnageModel personnage = new PersonnageModel(0, 0);
        personnage.setYcord(250.75);
        assertEquals(250.75, personnage.getYcord());
    }

    @Test
    public void testSetHealth() {
        PersonnageModel personnage = new PersonnageModel(0, 0);
        personnage.setHealth(80.0);
        assertEquals(80.0, personnage.getHealth());
    }

    @Test
    public void testSetShield() {
        PersonnageModel personnage = new PersonnageModel(0, 0);
        personnage.setShield(30.0);
        assertEquals(30.0, personnage.getShield());
    }

    @Test
    public void testSetSpeed() {
        PersonnageModel personnage = new PersonnageModel(0, 0);
        personnage.setSpeed(5.5);
        assertEquals(5.5, personnage.getSpeed());
    }

    @Test
    public void testSetJump() {
        PersonnageModel personnage = new PersonnageModel(0, 0);
        personnage.setJump(7.0);
        assertEquals(7.0, personnage.getJump());
    }

    @Test
    public void testSetDead() {
        PersonnageModel personnage = new PersonnageModel(0, 0);
        // Au départ, le worm est vivant
        assertFalse(personnage.isDead());
        
        // On le tue
        personnage.setDead(true);
        assertTrue(personnage.isDead());
        
        // On le ressuscite
        personnage.setDead(false);
        assertFalse(personnage.isDead());
    }*/


}

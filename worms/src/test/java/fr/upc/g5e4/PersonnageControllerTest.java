package fr.upc.g5e4;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import fr.upc.g5e4.model.PersonnageModel;
import fr.upc.g5e4.controller.PersonnageController;

/**
 * Tests unitaires pour PersonnageController (avec Junit)
 * 
 */
public class PersonnageControllerTest {
    
    // @Tests activation/désactivation touches
    
    /*@Test
    public void testActivateLeft() {
        PersonnageModel model = new PersonnageModel(100, 200);
        PersonnageController controller = new PersonnageController(model);
        
        controller.activateLeft();

        double positionInitiale = model.getXcord();
        controller.update();
        assertTrue(model.getXcord() < positionInitiale, "le worm devrait se déplacer vers la gauche");
    }
    
    @Test
    public void testDeactivateLeft() {
        PersonnageModel model = new PersonnageModel(100, 200);
        PersonnageController controller = new PersonnageController(model);

        controller.activateLeft();
        controller.deactivateLeft();

        double positionInitiale = model.getXcord();
        controller.update();
        assertEquals(positionInitiale, model.getXcord(), "le worm ne devrait pas bouger");
    }
    
    @Test
    public void testActivateRight() {
        PersonnageModel model = new PersonnageModel(100, 200);
        PersonnageController controller = new PersonnageController(model);

        controller.activateRight();
        double positionInitiale = model.getXcord();
        controller.update();
        assertTrue(model.getXcord() > positionInitiale, "le worm devrait se déplacer vers la droite");
    }
    
    @Test
    public void testDeactivateRight() {
        PersonnageModel model = new PersonnageModel(100, 200);
        PersonnageController controller = new PersonnageController(model);

        controller.activateRight();
        controller.deactivateRight();

        double positionInitiale = model.getXcord();
        controller.update();
        assertEquals(positionInitiale, model.getXcord(), "le worm ne devrait pas bouger");
    }
    
    // @Tests méthode update()
    
    @Test
    public void testUpdateWithoutKeyPress() {
        PersonnageModel model = new PersonnageModel(100, 200);
        PersonnageController controller = new PersonnageController(model);
        
        boolean hasMoved = controller.update();
        assertFalse(hasMoved, "update() devrait retourner false si aucune touche n'est enfoncée");
        assertEquals(100, model.getXcord(), "la position ne devrait pas changer");
    }
    
    @Test
    public void testUpdateWithLeftKeyPress() {
        PersonnageModel model = new PersonnageModel(100, 200);
        PersonnageController controller = new PersonnageController(model);

        controller.activateLeft();
        boolean hasMoved = controller.update();

        assertTrue(hasMoved, "update() devrait retourner true");
        assertEquals(100 - model.getSpeed(), model.getXcord(), "la position X devrait diminuer de la vitesse");
    }
    
    @Test
    public void testUpdateWithRightKeyPress() {
        PersonnageModel model = new PersonnageModel(100, 200);
        PersonnageController controller = new PersonnageController(model);

        controller.activateRight();
        boolean hasMoved = controller.update();

        assertTrue(hasMoved, "update() devrait retourner true");
        assertEquals(100 + model.getSpeed(), model.getXcord(), "la position X devrait augmenter de la vitesse");
    }
    
    @Test
    public void testUpdateWithLeftAndRightKeyPress() {
        PersonnageModel model = new PersonnageModel(100, 200);
        PersonnageController controller = new PersonnageController(model);
        
        controller.activateLeft();
        controller.activateRight();
        boolean hasMoved = controller.update();

        assertFalse(hasMoved, "update() devrait retourner false car les mouvements s'annulent");
        assertEquals(100, model.getXcord(), "la position ne devrait pas changer (forces opposées)");
    }
    
    // @Test méthodes de déplacement direct
    
    @Test
    public void testDeplacerGauche() {
        PersonnageModel model = new PersonnageModel(100, 200);
        PersonnageController controller = new PersonnageController(model);

        controller.moveLeft();
        assertEquals(100 - model.getSpeed(), model.getXcord(), "la position X devrait diminuer");
    }
    
    @Test
    public void testDeplacerDroite() {
        PersonnageModel model = new PersonnageModel(100, 200);
        PersonnageController controller = new PersonnageController(model);

        controller.moveRight();
        assertEquals(100 + model.getSpeed(), model.getXcord(), "la position X devrait augmenter");
    }
    
    // @Tests de la méthode teleport()
    
    @Test
    public void testTeleport() {
        PersonnageModel model = new PersonnageModel(100, 200);
        PersonnageController controller = new PersonnageController(model);
        
        controller.teleport(500, 300);
        
        assertEquals(500, model.getXcord(), "la position X devrait être 500");
        assertEquals(300, model.getYcord(), "la position Y devrait être 300");
    }*/

}

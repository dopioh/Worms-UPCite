package fr.upc.g5e4.controller.minigames;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import fr.upc.g5e4.model.minigames.WormRaceModel;
import fr.upc.g5e4.view.minigames.WormRaceView;

/**
 * Tests unitaires pour WormRaceController
 * @author Nassim
 */
public class WormRaceControllerTest {
    
    private WormRaceController controller;
    private WormRaceModel model;
    private WormRaceView view;
    
    @BeforeEach
    void setUp() {
        model = new WormRaceModel();
        view = new WormRaceView(model);
        view.setSize(1200, 800);
        controller = new WormRaceController(model, view);
    }
    
    // Tests d'initialisation
    
    @Test
    void testControllerInitialization() {
        assertNotNull(controller, "Le controller ne doit pas être null");
    }
    
    @Test
    void testGetModel() {
        assertNotNull(controller.getModel(), "getModel ne doit pas retourner null");
        assertEquals(model, controller.getModel(), "getModel doit retourner le bon model");
    }
    
    @Test
    void testGetView() {
        assertNotNull(controller.getView(), "getView ne doit pas retourner null");
        assertEquals(view, controller.getView(), "getView doit retourner la bonne vue");
    }
    
    // Tests de showGame
    
    @Test
    void testShowGameMakesViewVisible() {
        controller.showGame();
        
        assertTrue(view.isOverlayVisible(), "La vue doit être visible après showGame");
    }
    
    // Tests de closeOverlay
    
    @Test
    void testCloseOverlayHidesView() {
        controller.showGame();
        assertTrue(view.isOverlayVisible());
        
        controller.closeOverlay();
        
        assertFalse(view.isOverlayVisible(), "La vue doit être cachée après closeOverlay");
    }
}

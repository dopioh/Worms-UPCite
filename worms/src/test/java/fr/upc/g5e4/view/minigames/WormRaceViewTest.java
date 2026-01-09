package fr.upc.g5e4.view.minigames;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.*;
import fr.upc.g5e4.model.minigames.WormRaceModel;

/**
 * Tests unitaires pour WormRaceView
 * @author Nassim
 */
public class WormRaceViewTest {
    
    private WormRaceView view;
    private WormRaceModel model;
    
    @BeforeEach
    void setUp() {
        model = new WormRaceModel();
        view = new WormRaceView(model);
        view.setSize(1200, 800);
    }
    
    // Tests d'initialisation
    
    @Test
    void testViewInitialization() {
        assertNotNull(view, "La vue ne doit pas être null");
    }
    
    @Test
    void testInitialVisibilityFalse() {
        assertFalse(view.isOverlayVisible(), "L'overlay doit être invisible au départ");
        assertFalse(view.isVisible(), "La vue doit être invisible au départ");
    }
    
    @Test
    void testViewIsNotOpaque() {
        assertFalse(view.isOpaque(), "La vue ne doit pas être opaque");
    }
    
    @Test
    void testModelViewConnection() {
        model.resetGame();
        assertDoesNotThrow(() -> model.resetGame());
    }
    
    // Tests de visibilité
    
    @Test
    void testShowOverlay() {
        view.showOverlay();
        
        assertTrue(view.isOverlayVisible(), "L'overlay doit être visible après showOverlay");
        assertTrue(view.isVisible(), "La vue doit être visible après showOverlay");
        assertTrue(view.isEnabled(), "La vue doit être enabled après showOverlay");
    }
    
    @Test
    void testHideOverlay() {
        view.showOverlay();
        view.hideOverlay();
        
        assertFalse(view.isOverlayVisible(), "L'overlay doit être invisible après hideOverlay");
        assertFalse(view.isVisible(), "La vue doit être invisible après hideOverlay");
        assertFalse(view.isEnabled(), "La vue doit être disabled après hideOverlay");
    }
    
    @Test
    void testToggleVisibility() {
        assertFalse(view.isOverlayVisible());
        
        view.showOverlay();
        assertTrue(view.isOverlayVisible());
        
        view.hideOverlay();
        assertFalse(view.isOverlayVisible());
        
        view.showOverlay();
        assertTrue(view.isOverlayVisible());
    }
    
    // Tests des paris 
    
    @Test
    void testBetButtonsArray() {
        Rectangle[] betButtons = view.getBetButtons();
        
        assertNotNull(betButtons, "Le tableau de boutons de paris ne doit pas être null");
        assertEquals(3, betButtons.length, "Il doit y avoir 3 boutons de paris (un par worm)");
    }
    
    // Tests de repaint
    
    @Test
    void testRepaintDoesNotThrow() {
        assertDoesNotThrow(() -> view.repaint());
    }
    
    @Test
    void testRepaintWhenVisible() {
        view.showOverlay();
        assertDoesNotThrow(() -> view.repaint());
    }
    
    @Test
    void testRepaintWhenHidden() {
        view.hideOverlay();
        assertDoesNotThrow(() -> view.repaint());
    }
    
    // Tests connexion avec le model
    
    @Test
    void testViewUpdatesWithModelReset() {
        view.showOverlay();
        
        assertDoesNotThrow(() -> {
            model.resetGame();
            view.repaint();
        });
    }
    
    @Test
    void testViewUpdatesWithModelStartRace() {
        view.showOverlay();
        model.resetGame();
        
        assertDoesNotThrow(() -> {
            model.startRace();
            view.repaint();
        });
    }
    
    @Test
    void testViewUpdatesWithPositionUpdates() {
        view.showOverlay();
        model.resetGame();
        model.startRace();
        
        assertDoesNotThrow(() -> {
            model.updatePositions();
            view.repaint();
        });
    }
}

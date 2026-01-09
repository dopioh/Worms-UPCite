package fr.upc.g5e4.view.minigames;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.*;
import fr.upc.g5e4.model.minigames.WormGuessModel;

/**
 * Tests unitaires pour WormGuessView
 * @author Nassim
 */
public class WormGuessViewTest {
    
    private WormGuessView view;
    private WormGuessModel model;
    
    @BeforeEach
    void setUp() {
        model = new WormGuessModel();
        view = new WormGuessView(model);
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
    void testViewNotOpaque() {
        assertFalse(view.isOpaque(), "La vue ne doit pas être opaque");
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
    
    // Tests des boutons de paris
    
    @Test
    void testBetButtonsArray() {
        Rectangle[] betButtons = view.getBetButtons();
        
        assertNotNull(betButtons, "Le tableau de boutons de paris ne doit pas être null");
        assertEquals(2, betButtons.length, "Il doit y avoir 2 boutons de paris");
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
    
    // Tests connexion au model
    
    @Test
    void testViewUpdatesWithModelReset() {
        view.showOverlay();
        
        assertDoesNotThrow(() -> {
            model.resetGame();
            view.repaint();
        });
    }
    
    @Test
    void testViewUpdatesWithModelStartGame() {
        view.showOverlay();
        model.resetGame();
        
        assertDoesNotThrow(() -> {
            model.startGame();
            view.repaint();
        });
    }
    
    @Test
    void testViewUpdatesWithModelAnimation() {
        view.showOverlay();
        model.resetGame();
        model.startGame();
        
        assertDoesNotThrow(() -> {
            model.updateAnimation();
            view.repaint();
        });
    }
}

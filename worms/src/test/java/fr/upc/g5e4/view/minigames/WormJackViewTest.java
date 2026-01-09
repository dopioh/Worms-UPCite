package fr.upc.g5e4.view.minigames;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import fr.upc.g5e4.model.minigames.WormJackModel;

/**
 * Tests unitaires pour WormJackView
 */
public class WormJackViewTest {
    
    private WormJackView view;
    private WormJackModel model;
    
    @BeforeEach
    void setUp() {
        model = new WormJackModel();
        view = new WormJackView(model);
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
    
    // Tests de connexion au model
    
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
    void testViewUpdatesWithHit() {
        view.showOverlay();
        model.resetGame();
        model.startGame();
        
        assertDoesNotThrow(() -> {
            model.hit();
            view.repaint();
        });
    }
    
    @Test
    void testViewUpdatesWithStand() {
        view.showOverlay();
        model.resetGame();
        model.startGame();
        
        assertDoesNotThrow(() -> {
            model.stand();
            view.repaint();
        });
    }
}

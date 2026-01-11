package fr.upc.g5e4.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.*;
import fr.upc.g5e4.model.TutorialModel;

/**
 * Tests unitaires pour TutorialView
 * @author Nassim
 */
public class TutorialViewTest {
    
    private TutorialView view;
    private TutorialModel model;
    
    @BeforeEach
    void setUp() {
        model = new TutorialModel();
        view = new TutorialView(model);
        view.setSize(1200, 800);
    }
    
    // Tests d'initialisation
    
    @Test
    void testViewInitialization() {
        assertNotNull(view, "La vue ne doit pas être null");
    }
    
    @Test
    void testViewIsNotOpaque() {
        assertFalse(view.isOpaque(), "La vue ne doit pas être opaque");
    }
    
    @Test
    void testViewHasNullLayout() {
        assertNull(view.getLayout(), "La vue doit avoir un layout null");
    }
    
    @Test
    void testModelViewConnection() {
        assertDoesNotThrow(() -> model.nextPage());
    }
    
    // Tests des boutons
    
    @Test
    void testContinueButtonExists() {
        JButton continueButton = view.getContinueButton();
        assertNotNull(continueButton, "Le bouton Continuer ne doit pas être null");
    }
    
    @Test
    void testBackButtonExists() {
        JButton backButton = view.getBackButton();
        assertNotNull(backButton, "Le bouton Retour ne doit pas être null");
    }
    
    @Test
    void testCloseButtonExists() {
        JButton closeButton = view.getCloseButton();
        assertNotNull(closeButton, "Le bouton Fermer ne doit pas être null");
    }
    
    @Test
    void testBackButtonDisabledOnFirstPage() {
        view.updateButtonStates();
        
        JButton backButton = view.getBackButton();
        assertFalse(backButton.isEnabled(), "Le bouton Retour doit être désactivé sur la première page");
    }
    
    @Test
    void testBackButtonEnabledOnSecondPage() {
        model.nextPage();
        view.updateButtonStates();
        
        JButton backButton = view.getBackButton();
        assertTrue(backButton.isEnabled(), "Le bouton Retour doit être activé à partir de la deuxième page");
    }
    
    @Test
    void testContinueButtonDisabledOnLastPage() {
        while (model.hasNextPage()) {
            model.nextPage();
        }
        view.updateButtonStates();
        
        JButton continueButton = view.getContinueButton();
        assertFalse(continueButton.isEnabled(), "Le bouton Continuer doit être désactivé sur la dernière page");
    }
    
    // Tests de visibilité
    
    @Test
    void testSetVisibleTrue() {
        view.setVisible(true);
        assertTrue(view.isVisible(), "La vue doit être visible après setVisible(true)");
    }
    
    @Test
    void testSetVisibleFalse() {
        view.setVisible(true);
        view.setVisible(false);
        assertFalse(view.isVisible(), "La vue doit être invisible après setVisible(false)");
    }
}

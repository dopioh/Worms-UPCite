package fr.upc.g5e4.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.*;
import fr.upc.g5e4.model.TutorialModel;
import fr.upc.g5e4.view.TutorialView;

/**
 * Tests unitaires pour TutorialController
 * @author Nassim
 */
public class TutorialControllerTest {
    
    private TutorialController controller;
    private TutorialModel model;
    private TutorialView view;
    
    @BeforeEach
    void setUp() {
        model = new TutorialModel();
        view = new TutorialView(model);
        view.setSize(1200, 800);
        controller = new TutorialController(model, view);
    }
    
    // Tests d'initialisation
    
    @Test
    void testControllerInitialization() {
        assertNotNull(controller, "Le controller ne doit pas être null");
    }
    
    @Test
    void testGetView() {
        assertNotNull(controller.getView(), "getView ne doit pas retourner null");
        assertEquals(view, controller.getView(), "getView doit retourner la bonne vue");
    }
    
    // Tests de showTutorial
    
    @Test
    void testShowTutorialMakesViewVisible() {
        controller.showTutorial();
        
        assertTrue(view.isVisible(), "La vue doit être visible après showTutorial");
    }
    
    @Test
    void testShowTutorialResetsToFirstPage() {
        model.nextPage();
        model.nextPage();
        
        controller.showTutorial();
        
        assertEquals(0, model.getCurrentPage(), "showTutorial doit ramener à la première page");
    }
    
    // Tests des boutons via le controller
    
    @Test
    void testContinueButtonAdvancesPage() {
        controller.showTutorial();
        
        JButton continueButton = view.getContinueButton();
        int initialPage = model.getCurrentPage();
        
        // simule un clic sur le bouton
        continueButton.doClick();
        
        assertEquals(initialPage + 1, model.getCurrentPage(), "Le bouton Continuer doit avancer d'une page");
    }
    
    @Test
    void testBackButtonGoesBack() {
        controller.showTutorial();
        model.nextPage();
        
        JButton backButton = view.getBackButton();
        int currentPage = model.getCurrentPage();
        
        backButton.doClick();
        
        assertEquals(currentPage - 1, model.getCurrentPage(), "Le bouton Retour doit reculer d'une page");
    }
    
    @Test
    void testCloseButtonHidesView() {
        controller.showTutorial();
        assertTrue(view.isVisible());
        
        JButton closeButton = view.getCloseButton();
        closeButton.doClick();
        
        assertFalse(view.isVisible(), "Le bouton Fermer doit cacher la vue");
    }
    
    @Test
    void testCloseButtonResetsPage() {
        controller.showTutorial();
        model.nextPage();
        model.nextPage();
        
        JButton closeButton = view.getCloseButton();
        closeButton.doClick();
        
        assertEquals(0, model.getCurrentPage(), "Fermer le tutoriel doit reset à la page 0");
    }
}

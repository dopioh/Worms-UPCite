package fr.upc.g5e4.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import fr.upc.g5e4.model.minigames.WormGuessModel;
import fr.upc.g5e4.view.minigames.WormGuessView;

/**
 * Tests d'intégration MVC pour WormGuess
 * @author Nassim
 */
public class WormGuessIntegrationTest {
    
    private WormGuessModel model;
    private WormGuessView view;
    
    @BeforeEach
    void setUp() {
        model = new WormGuessModel();
        view = new WormGuessView(model);
        view.setSize(1200, 800);
    }
    
    /**
     * scénario partie complète avec animation jusqu'à la fin
     */
    @Test
    void testScenarioFullParty() {
        model.resetGame();
        model.startGame();
        
        assertTrue(model.isGameStarted(), "Le jeu doit être démarré");
        assertTrue(model.isAnimating(), "L'animation doit être en cours");
        
        int maxFrames = 60;
        int frame = 0;
        while (model.isAnimating() && frame < maxFrames) {
            model.updateAnimation();
            frame++;
        }
        
        assertTrue(model.isGameFinished(), "Le jeu doit être terminé après l'animation");
        assertFalse(model.isAnimating(), "L'animation doit être terminée");
        assertTrue(model.getWinnerIndex() == 0 || model.getWinnerIndex() == 1, "Un gagnant valide doit être déterminé (0 ou 1)");
    }
    
    /**
     * scénario lancement de plusieurs parties successives (robustesse)
     */
    @Test
    void testScenarioMultiParty() {

        for (int partie = 0; partie < 5; partie++) {
            model.resetGame();
            model.startGame();
            
            while (model.isAnimating()) {
                model.updateAnimation();
            }
            
            assertTrue(model.isGameFinished(), "Partie " + partie + " doit être terminée");
            assertTrue(model.getWinnerIndex() == 0 || model.getWinnerIndex() == 1, "Un gagnant valide doit être déterminé (0 ou 1)");
        }
    }
}

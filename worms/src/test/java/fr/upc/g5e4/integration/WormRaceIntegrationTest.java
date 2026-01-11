package fr.upc.g5e4.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import fr.upc.g5e4.model.minigames.WormRaceModel;
import fr.upc.g5e4.view.minigames.WormRaceView;

/**
 * Tests d'intégration MVC pour WormRace
 * @author Nassim
 */
public class WormRaceIntegrationTest {
    
    private WormRaceModel model;
    private WormRaceView view;
    
    @BeforeEach
    void setUp() {
        model = new WormRaceModel();
        view = new WormRaceView(model);
        view.setSize(1200, 800);
    }
    
    /**
     * scénario de course complète
     * les worms doivent atteindre la position 1.0 pour gagner
     */
    @Test
    void testScenarioFullRun() {
        model.resetGame();
        model.startRace();
        
        assertTrue(model.isRaceStarted(), "La course doit être démarrée");
        
        // Simuler la course jusqu'à ce qu'un worm gagne
        int maxIterations = 1000;
        int iterations = 0;

        while (!model.isRaceFinished() && iterations < maxIterations) {
            model.updatePositions();
            iterations++;
        }
        
        assertTrue(model.isRaceFinished(), "La course doit être terminée, il y'a trop d'itérations d'updatePosition");
        assertTrue(model.getWinnerIndex() >= 0 && model.getWinnerIndex() < 3, "Un gagnant valide doit être déterminé (index 0, 1 ou 2)");
        
        // Le gagnant doit avoir atteint la ligne d'arrivée
        float[] positions = model.getWormPositions();
        assertTrue(positions[model.getWinnerIndex()] >= 1.0f, "Le gagnant doit avoir atteint la position 1.0");
    }
    
    /**
     * scénario lancement de plusieurs courses successives (robustesse)
     */
    @Test
    void testScenarioMultiFullRun() {
        for (int course = 0; course < 3; course++) {
            model.resetGame();
            model.startRace();
            
            while (!model.isRaceFinished()) {
                model.updatePositions();
            }
            
            assertTrue(model.isRaceFinished(), "Course " + course + " doit être terminée");
            assertTrue(model.getWinnerIndex() >= 0, "Course " + course + " doit avoir un gagnant");
        }
    }
}

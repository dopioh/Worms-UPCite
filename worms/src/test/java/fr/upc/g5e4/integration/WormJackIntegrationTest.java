package fr.upc.g5e4.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import fr.upc.g5e4.model.minigames.WormJackModel;
import fr.upc.g5e4.view.minigames.WormJackView;

/**
 * Tests d'intégration MVC pour WormJack
 * @author Nassim
 */
public class WormJackIntegrationTest {
    
    private WormJackModel model;
    private WormJackView view;
    
    @BeforeEach
    void setUp() {
        model = new WormJackModel();
        view = new WormJackView(model);
        view.setSize(1200, 800);
    }
    
    /**
     * scénario joueur fait stand immédiatement (donc il a qu'une carte et wormbot joue la game)
     */
    @Test
    void testScenarioJoueurStand() {
        model.resetGame();
        model.startGame();
        
        assertTrue(model.isGameStarted(), "Le jeu doit être démarré");
        assertTrue(model.getPlayerScore() > 0, "Le joueur doit avoir des cartes");
        
        model.stand();
        
        assertFalse(model.isPlayerTurn(), "Ce n'est plus le tour du joueur après stand");
        
        // wormbot joue (tire jusqu'à avoir >= 17)
        while (model.shouldWormbotHit()) {
            model.wormbotHit();
        }

        model.finishWormbotTurn();
        
        assertTrue(model.isGameFinished(), "Le jeu doit être terminé");
    }
    
    /**
     * scénario joueur tire 1 carte sans bust
     */
    @Test
    void testScenarioJoueurMultiHit() {
        model.resetGame();
        model.startGame();
        
        int scoreBefore = model.getPlayerScore();
        int cardsBefore = model.getPlayerCards().size();
        
        boolean busted = model.hit();
        
        if (!busted) {
            assertTrue(model.getPlayerCards().size() > cardsBefore, "Le joueur doit avoir plus de cartes après hit");
            assertTrue(model.getPlayerScore() > scoreBefore, "Le score doit avoir augmenté ou resté stable");
        }
    }
    
    /**
     * scénario bust du joueur, tirage de 10 cartes puis calcul du score
     */
    @Test
    void testScenarioDetectionJoueurBust() {
        model.resetGame();
        model.startGame();
        
        int nbTirages = 0;
        boolean busted = false;
        
        while (!busted && nbTirages < 10) {
            busted = model.hit();
            nbTirages++;
        }
        
        if (model.getPlayerScore() > 21) {
            assertTrue(model.isGameFinished(), "Le jeu doit être terminé si bust");
        }
    }
    
    /**
     * scénario lancement de plusieurs parties successives (robustesse)
     */
    @Test
    void testScenarioMultiParty() {
        for (int partie = 0; partie < 3; partie++) {
            model.resetGame();
            model.startGame();
            
            assertEquals(0, model.getCurrentBet(), "Le pari doit être reset");
            assertTrue(model.isGameStarted(), "La partie " + partie + " doit démarrer");
            
            model.stand();
            while (model.shouldWormbotHit()) {
                model.wormbotHit();
            }
            model.finishWormbotTurn();
            
            assertTrue(model.isGameFinished(), "La partie " + partie + " doit être terminée");
        }
    }
}

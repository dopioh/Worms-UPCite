package fr.upc.g5e4.model.minigames;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour WormGuessModel
 */
public class WormGuessModelTest {
    
    private WormGuessModel model;
    
    @BeforeEach
    void setUp() {
        model = new WormGuessModel();
        model.resetGame();
    }
    
    // Tests d'initialisation
    
    @Test
    void testInitialization() {
        assertNotNull(model, "Le model ne doit pas être null");
    }
    
    @Test
    void testResetGameInitialization() {
        model.resetGame();
        
        assertFalse(model.isGameStarted(), "Le jeu ne doit pas être démarré après reset");
        assertFalse(model.isGameFinished(), "Le jeu ne doit pas être terminé après reset");
        assertFalse(model.isAnimating(), "L'animation ne doit pas être en cours après reset");
        assertEquals(-1, model.getWinnerIndex(), "L'index du gagnant doit être -1 après reset");
        assertEquals(0, model.getCurrentAnimFrame(), "Le frame (animation) doit être 0 après reset");
    }
    
    @Test
    void testSelectedWormsAfterReset() {
        model.resetGame();
        assertNotNull(model.getSelectedWorms(), "Les worms sélectionnés ne doivent pas être null après reset");
        assertEquals(2, model.getSelectedWorms().length, "Il doit y avoir 2 worms sélectionnés");
    }
    
    // Tests des paris
    
    @Test
    void testBetsInitialization() {
        model.resetGame();
        int[] bets = model.getBets();
        
        assertNotNull(bets, "Le tableau de paris ne doit pas être null");
        assertEquals(0, bets[0], "Le pari sur le worm 0 doit être 0");
        assertEquals(0, bets[1], "Le pari sur le worm 1 doit être 0");
    }
    
    @Test
    void testGetTotalBetsInitialization() {
        model.resetGame();
        assertEquals(0, model.getTotalBets(), "Le total des paris doit être 0 au début");
    }
    
    // Tests pendant le jeu
    
    @Test
    void testStartGameChangesState() {
        model.resetGame();
        model.startGame();
        
        assertTrue(model.isGameStarted(), "Le jeu doit être démarré après startGame");
        assertTrue(model.isAnimating(), "L'animation doit être en cours après startGame");
    }
    
    @Test
    void testStartGameSetsWinnerIndex() {
        model.resetGame();
        model.startGame();
        
        int winnerIndex = model.getWinnerIndex();
        assertTrue(winnerIndex >= 0 && winnerIndex < 2, "L'index du gagnant doit être 0 ou 1");
    }
    
    @Test
    void testStartGameResetsAnimationFrame() {
        model.resetGame();
        model.startGame();
        
        assertEquals(0, model.getCurrentAnimFrame(), "Le frame d'animation doit être 0 après startGame");
    }
    
    // Tests de l'animation roulette

    @Test
    void testUpdateAnimationReturnsFalseWhenNotFinished() {
        model.resetGame();
        model.startGame();
        
        boolean result = model.updateAnimation();
        assertFalse(result, "updateAnimation doit retourner false quand l'animation n'est pas terminée");
    }
    
    @Test
    void testAnimationFinishesAfter60Frames() {
        model.resetGame();
        model.startGame();
        
        // simule 60 frames d'animation
        boolean finished = false;
        for (int i = 0; i < 60; i++) {
            finished = model.updateAnimation();
        }
        
        assertTrue(finished, "L'animation doit être terminée après 60 frames");
        assertTrue(model.isGameFinished(), "Le jeu doit être terminé après l'animation");
        assertFalse(model.isAnimating(), "L'animation ne doit plus être en cours");
    }
    
    // Tests quand le jeu est arreté
    
    @Test
    void testStopGameResetsState() {
        model.resetGame();
        model.startGame();
        model.stopGame();
        
        assertFalse(model.isAnimating(), "L'animation ne doit pas être en cours après stopGame");
        assertFalse(model.isGameStarted(), "Le jeu ne doit pas être démarré après stopGame");
        assertFalse(model.isGameFinished(), "Le jeu ne doit pas être terminé après stopGame");
    }
}

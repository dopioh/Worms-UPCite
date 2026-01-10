package fr.upc.g5e4.model.minigames;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour WormRaceModel
 * @author Nassim
 */
public class WormRaceModelTest {
    
    private WormRaceModel model;
    
    @BeforeEach
    void setUp() {
        model = new WormRaceModel();
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
        
        assertFalse(model.isRaceStarted(), "La course ne doit pas être démarrée après reset");
        assertFalse(model.isRaceFinished(), "La course ne doit pas être terminée après reset");
        assertEquals(-1, model.getWinnerIndex(), "L'index du gagnant doit être -1 après reset");
    }
    
    @Test
    void testSelectedWormsAfterReset() {
        model.resetGame();
        assertNotNull(model.getSelectedWorms(), "Les worms sélectionnés ne doivent pas être null après reset");
        assertEquals(3, model.getSelectedWorms().length, "Il doit y avoir 3 worms sélectionnés pour la course");
    }
    
    // Tests des paris
    
    @Test
    void testBetsInitializedToZero() {
        model.resetGame();
        int[] bets = model.getBets();
        
        assertNotNull(bets, "Le tableau de paris ne doit pas être null");
        assertEquals(3, bets.length, "Il doit y avoir 3 slots de paris (un par worm)");
        
        assertEquals(0, bets[0], "Le pari sur le worm 0 doit être 0");
        assertEquals(0, bets[1], "Le pari sur le worm 1 doit être 0");
        assertEquals(0, bets[2], "Le pari sur le worm 2 doit être 0");
    }
    
    @Test
    void testGetTotalBetsInitialization() {
        model.resetGame();
        assertEquals(0, model.getTotalBets(), "Le total des paris doit être 0 initialement");
    }
    
    //  Tests au lancement de la course 
    
    @Test
    void testStartRaceChangesState() {
        model.resetGame();
        model.startRace();
        
        assertTrue(model.isRaceStarted(), "La course doit être démarrée après startRace");
        assertFalse(model.isRaceFinished(), "La course ne doit pas être terminée juste après démarrage");
    }
    
    @Test
    void testStartRaceNoNullPositions() {
        model.resetGame();
        model.startRace();
        
        float[] positions = model.getWormPositions();
        assertNotNull(positions, "Les positions ne doivent pas être null après startRace");
    }
    
    // autres tests

    @Test
    void testStartRaceAfterReset() {
        model.resetGame();
        model.startRace();
        model.resetGame();
        model.startRace();
        
        assertTrue(model.isRaceStarted(), "La course doit pouvoir redémarrer après reset");
    }
}

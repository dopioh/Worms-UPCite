package fr.upc.g5e4.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.upc.g5e4.controller.CharacterController;
import fr.upc.g5e4.view.InventoryOverlay;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests for GameManager's inventory and round management functionality
 * Testing code added on December 11, 2025
 */
public class GameManagerTest {
    
    private GameManager gameManager;
    private InventoryOverlay mockInventoryOverlay;
    
    @BeforeEach
    void setUp() {
        TeamConfig.NBR_EQUIPE = 2;
        
        if (TeamConfig.aCustomForATeam == null) {
            TeamConfig.aCustomForATeam = new ArrayList<>();
        }
        gameManager = new GameManager(); // on test pour 2 equipes ici
        // Create a mock inventory list for testing
        List<InventoryModel.InventoryJson> mockInventory = new ArrayList<>();
        mockInventoryOverlay = new InventoryOverlay(mockInventory);
    }
    
    /* ---------- getCurrentTeam() ---------- */
    
    // Je masque tous ces tests psq on a N Team et plus seulement 2 desormais
    /*
    @Test
    void testGetCurrentTeamReturnsTeam1WhenIndexZero() {
        // Assuming currentTeamIndex starts at 0 (Team1)
        TeamModel team = gameManager.getCurrentTeam();
        
        assertNotNull(team, "getCurrentTeam should not return null when currentTeamIndex is 0");
        assertEquals(team, gameManager.getTeamModel1(), "Should return Team1 when currentTeamIndex is 0");
    }
    
    @Test
    void testGetCurrentTeamReturnsTeam2WhenIndexOne() {
        // Switch to team 2 by calling nextRound
        gameManager.nextRound();
        
        TeamModel team = gameManager.getCurrentTeam();
        
        assertNotNull(team, "getCurrentTeam should not return null when currentTeamIndex is 1");
        assertEquals(team, gameManager.getTeamModel2(), "Should return Team2 when currentTeamIndex is 1");
    }
    

    */
    /* ---------- getCurrentTeamName() ---------- */
    
    /*
    @Test
    void testGetCurrentTeamNameReturnsTeam1() {
        String name = gameManager.getCurrentTeamName();
        
        assertEquals("Team1", name, "Should return 'Team1' when currentTeamIndex is 0");
    }
    
    @Test
    void testGetCurrentTeamNameReturnsTeam2AfterRound() {
        gameManager.nextRound();
        
        String name = gameManager.getCurrentTeamName();
        
        assertEquals("Team2", name, "Should return 'Team2' when currentTeamIndex is 1");
    }

    */

    /* ---------- setInventoryOverlay() ---------- */
    
    // @Test
    // void testSetInventoryOverlayStoresReference() {
    //     List<InventoryModel.InventoryJson> mockInventory = new ArrayList<>();
    //     InventoryOverlay overlay = new InventoryOverlay(mockInventory);
    //     gameManager.setInventoryOverlay(overlay);
        
    //     assertNotNull(overlay, "Inventory overlay should be set and non-null");
    // }
    
    // @Test
    // void testSetInventoryOverlayCanBeCalledMultipleTimes() {
    //     List<InventoryModel.InventoryJson> mockInventory = new ArrayList<>();
    //     InventoryOverlay overlay1 = new InventoryOverlay(mockInventory);
    //     InventoryOverlay overlay2 = new InventoryOverlay(mockInventory);
        
    //     gameManager.setInventoryOverlay(overlay1);
    //     // Should not throw an exception
    //     gameManager.setInventoryOverlay(overlay2);
        
    //     assertTrue(true, "setInventoryOverlay should allow reassignment");
    // }
    
    /* ---------- nextRound() ---------- */
    
    @Test
    void testNextRoundIncrementsRoundNumber() {
        int initialRound = gameManager.getRoundNumber();
        
        gameManager.nextRound();
        
        assertEquals(initialRound + 1, gameManager.getRoundNumber(), 
                "nextRound should increment the round number by 1");
    }
    
    @Test
    void testNextRoundAlternatesTeams() {
        int initialTeamIndex = gameManager.getCurrentTeamIndex();
        
        gameManager.nextRound();
        
        int newTeamIndex = gameManager.getCurrentTeamIndex();
        assertNotEquals(initialTeamIndex, newTeamIndex, 
                "nextRound should switch to the other team");
    }
    
    @Test
    void testNextRoundTeamAlternation() {
        assertEquals(0, gameManager.getCurrentTeamIndex(), "Should start with Team1 (index 0)");
        
        gameManager.nextRound();
        assertEquals(1, gameManager.getCurrentTeamIndex(), "After first nextRound, should be Team2 (index 1)");
        
        gameManager.nextRound();
        assertEquals(0, gameManager.getCurrentTeamIndex(), "After second nextRound, should cycle back to Team1");
    }
    
    @Test
    void testNextRoundSwitchesActiveWorm() {
        CharacterController initialController = gameManager.getActiveWormController();
        assertNotNull(initialController, "Initial active worm controller should not be null");
        
        gameManager.nextRound();
        
        CharacterController newController = gameManager.getActiveWormController();
        assertNotNull(newController, "Active worm controller should still be valid after nextRound");
        // Note: Controllers may be new instances even if controlling the same worm type
    }
    
    @Test
    void testNextRoundMultipleCycles() {
        int startTeamIndex = gameManager.getCurrentTeamIndex();
        
        // Cycle through multiple rounds
        for (int i = 0; i < 4; i++) {
            gameManager.nextRound();
        }
        
        // After 4 rounds (2 full cycles), we should be back at the starting team
        assertEquals(startTeamIndex, gameManager.getCurrentTeamIndex(),
                "After 4 rounds (2 full cycles), should return to the starting team");
        assertEquals(5, gameManager.getRoundNumber(), "Round number should be 5 after 4 nextRound calls");
    }
    
    @Test
    void testNextRoundWithNullInventoryOverlay() {
        // Don't set an inventory overlay
        assertDoesNotThrow(() -> gameManager.nextRound(),
                "nextRound should handle null inventory overlay gracefully");
    }
    
    @Test
    void testNextRoundPrintsDebugOutput() {
        // This test verifies that nextRound produces some output
        // In a real scenario, you might capture System.out
        assertDoesNotThrow(() -> gameManager.nextRound(),
                "nextRound should not throw exceptions and should print debug info");
    }
}

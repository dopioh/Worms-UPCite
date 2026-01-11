package fr.upc.g5e4.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import fr.upc.g5e4.model.InventoryModel.InventoryJson;
import fr.upc.g5e4.view.NameTagView;

/**
 * Tests unitaires pour le système de gadgets et potions
 */
public class GadgetSystemTest {
    
    private InventoryJson potionHeal;
    private InventoryJson potionSpeed;
    private InventoryJson potionJump;
    private InventoryJson potionShield;
    private InventoryJson bumper;
    
    @BeforeEach
    public void setUp() {
        // initialiser les gadgets
        potionHeal = new InventoryJson();
        potionHeal.objectType = "GADGET";
        potionHeal.name = "PotionHeal";
        potionHeal.maxUsages = 3;
        potionHeal.isInfinite = false;
        potionHeal.remainingUses = 3;
        
        potionSpeed = new InventoryJson();
        potionSpeed.objectType = "GADGET";
        potionSpeed.name = "PotionSpeed";
        potionSpeed.maxUsages = 2;
        potionSpeed.isInfinite = false;
        potionSpeed.remainingUses = 10;
        
        potionJump = new InventoryJson();
        potionJump.objectType = "GADGET";
        potionJump.name = "PotionJump";
        potionJump.maxUsages = 2;
        potionJump.isInfinite = false;
        potionJump.remainingUses = 2;
        
        potionShield = new InventoryJson();
        potionShield.objectType = "GADGET";
        potionShield.name = "PotionShield";
        potionShield.maxUsages = 3;
        potionShield.isInfinite = false;
        potionShield.remainingUses = 3;
        
        bumper = new InventoryJson();
        bumper.objectType = "GADGET";
        bumper.name = "Bumper";
        bumper.maxUsages = 5;
        bumper.isInfinite = true;
        bumper.remainingUses = null;
    }
    
    /**
     * test: vérifier que les gadgets sont bien marqués comme GADGET
     */
    @Test
    public void testGadgetType() {
        assertEquals("GADGET", potionHeal.objectType);
        assertEquals("GADGET", potionSpeed.objectType);
        assertEquals("GADGET", bumper.objectType);
    }
    
    /**
     * test: vérifier la capacité à utiliser un gadget dans un tour
     */
    @Test
    public void testCanUseGadgetThisTurn() {
        assertTrue(potionHeal.canUseThisTurn());
        assertTrue(potionSpeed.canUseThisTurn());
        
        // enregistrer une utilisation
        assertTrue(potionHeal.registerUseThisTurn());
        assertEquals(1, potionHeal.useThisTurn);
        
        // vérifier les utilisations restantes ce tour
        assertEquals(2, potionHeal.getRemainingUseThisTurn());
    }
    
    /**
     * test: vérifier que les gadgets infinis peuvent être utilisés plusieurs fois
     */
    @Test
    public void testInfiniteGadget() {
        assertTrue(bumper.isInfinite);
        assertTrue(bumper.hasRemainingUses());
        
        // utiliser plusieurs fois
        for (int i = 0; i < 5; i++) {
            assertTrue(bumper.canUseThisTurn());
            bumper.registerUseThisTurn();
        }
        
        // vérifier que bumper a dépassé le maxUsages
        assertEquals(5, bumper.useThisTurn);
        assertFalse(bumper.canUseThisTurn());
    }
    
    /**
     * test: vérifier la limitation d'utilisation par tour
     */
    @Test
    public void testMaxUsagesPerTurn() {
        assertEquals(3, potionHeal.maxUsages);
        assertEquals(3, potionHeal.getRemainingUseThisTurn());
        
        // utiliser 3 fois
        for (int i = 0; i < 3; i++) {
            assertTrue(potionHeal.registerUseThisTurn());
        }
        
        // ne peut plus être utilisé
        assertFalse(potionHeal.canUseThisTurn());
        assertEquals(0, potionHeal.getRemainingUseThisTurn());
    }
    
    /**
     * test: vérifier le reset du tour
     */
    @Test
    public void testResetTurn() {
        potionSpeed.registerUseThisTurn();
        potionSpeed.registerUseThisTurn();
        assertEquals(2, potionSpeed.useThisTurn);
        
        // reset pour le nouveau tour
        potionSpeed.resetTurn();
        assertEquals(0, potionSpeed.useThisTurn);
        assertTrue(potionSpeed.canUseThisTurn());
        assertEquals(2, potionSpeed.getRemainingUseThisTurn());
    }
    
    /**
     * test: vérifier la consommation de gadgets non infinis
     */
    @Test
    public void testConsumableGadget() {
        assertEquals(3, potionHeal.remainingUses);
        assertFalse(potionHeal.isInfinite);
        
        potionHeal.registerUseThisTurn();
        assertEquals(2, potionHeal.remainingUses);
        
        potionHeal.registerUseThisTurn();
        assertEquals(1, potionHeal.remainingUses);
        
        potionHeal.registerUseThisTurn();
        assertEquals(0, potionHeal.remainingUses);
        
        // ne peut plus être utilisé
        assertFalse(potionHeal.canUseThisTurn());
    }
}

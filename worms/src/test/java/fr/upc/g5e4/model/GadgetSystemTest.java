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
    private InventoryJson teleporter;
    private InventoryJson drill;
    
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
        
        teleporter = new InventoryJson();
        teleporter.objectType = "GADGET";
        teleporter.name = "Teleporter";
        teleporter.maxUsages = 3;
        teleporter.isInfinite = false;
        teleporter.remainingUses = 3;
        
        drill = new InventoryJson();
        drill.objectType = "GADGET";
        drill.name = "Drill";
        drill.maxUsages = 10;
        drill.isInfinite = true;
        drill.remainingUses = null;
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
    
    /**
     * test: vérifier le teleporter (gadget limité à 3 utilisations)
     */
    @Test
    public void testTeleporter() {
        assertEquals("GADGET", teleporter.objectType);
        assertEquals("Teleporter", teleporter.name);
        assertEquals(3, teleporter.maxUsages);
        assertFalse(teleporter.isInfinite);
        assertEquals(3, teleporter.remainingUses);
        
        // vérifier qu'on peut l'utiliser
        assertTrue(teleporter.hasRemainingUses());
        assertTrue(teleporter.canUseThisTurn());
    }
    
    /**
     * test: vérifier l'utilisation du teleporter jusqu'à épuisement
     */
    @Test
    public void testTeleporterConsumption() {
        assertEquals(3, teleporter.remainingUses);
        
        // première utilisation
        assertTrue(teleporter.registerUseThisTurn());
        assertEquals(2, teleporter.remainingUses);
        assertTrue(teleporter.hasRemainingUses());
        
        // deuxième utilisation
        assertTrue(teleporter.registerUseThisTurn());
        assertEquals(1, teleporter.remainingUses);
        assertTrue(teleporter.hasRemainingUses());
        
        // troisième utilisation
        assertTrue(teleporter.registerUseThisTurn());
        assertEquals(0, teleporter.remainingUses);
        assertFalse(teleporter.hasRemainingUses());
        
        // ne peut plus être utilisé
        assertFalse(teleporter.canUseThisTurn());
    }
    
    /**
     * test: vérifier la limitation du teleporter par tour
     */
    @Test
    public void testTeleporterMaxUsagesPerTurn() {
        // 3 utilisations max par tour
        for (int i = 0; i < 3; i++) {
            assertTrue(teleporter.canUseThisTurn());
            assertTrue(teleporter.registerUseThisTurn());
        }
        
        // ne peut plus être utilisé ce tour
        assertFalse(teleporter.canUseThisTurn());
        assertEquals(0, teleporter.getRemainingUseThisTurn());
        
        // mais toutes les utilisations sont épuisées
        assertEquals(0, teleporter.remainingUses);
    }
    
    /**
     * test: vérifier le drill (gadget infini)
     */
    @Test
    public void testDrill() {
        assertEquals("GADGET", drill.objectType);
        assertEquals("Drill", drill.name);
        assertEquals(10, drill.maxUsages);
        assertTrue(drill.isInfinite);
        assertNull(drill.remainingUses);
        
        // vérifier qu'on peut l'utiliser
        assertTrue(drill.hasRemainingUses());
        assertTrue(drill.canUseThisTurn());
    }
    
    /**
     * test: vérifier que le drill est infini mais limité par tour
     */
    @Test
    public void testDrillInfiniteButLimitedPerTurn() {
        assertTrue(drill.isInfinite);
        
        // utiliser 10 fois (max par tour)
        for (int i = 0; i < 10; i++) {
            assertTrue(drill.canUseThisTurn());
            assertTrue(drill.registerUseThisTurn());
        }
        
        // ne peut plus être utilisé ce tour
        assertFalse(drill.canUseThisTurn());
        assertEquals(10, drill.useThisTurn);
        assertEquals(0, drill.getRemainingUseThisTurn());
        
        // mais reste infini (pas de remainingUses)
        assertNull(drill.remainingUses);
        assertTrue(drill.hasRemainingUses());
    }
    
    /**
     * test: vérifier le reset du drill pour un nouveau tour
     */
    @Test
    public void testDrillResetBetweenTurns() {
        // utiliser plusieurs fois
        for (int i = 0; i < 5; i++) {
            drill.registerUseThisTurn();
        }
        assertEquals(5, drill.useThisTurn);
        assertEquals(5, drill.getRemainingUseThisTurn());
        
        // reset pour le nouveau tour
        drill.resetTurn();
        assertEquals(0, drill.useThisTurn);
        assertEquals(10, drill.getRemainingUseThisTurn());
        assertTrue(drill.canUseThisTurn());
        
        // peut à nouveau être utilisé 10 fois
        for (int i = 0; i < 10; i++) {
            assertTrue(drill.registerUseThisTurn());
        }
        assertFalse(drill.canUseThisTurn());
    }
    
    /**
     * test: vérifier la comparaison entre drill (infini) et teleporter (consommable)
     */
    @Test
    public void testDrillVsTeleporter() {
        // drill est infini
        assertTrue(drill.isInfinite);
        assertNull(drill.remainingUses);
        
        // teleporter est consommable
        assertFalse(teleporter.isInfinite);
        assertEquals(3, teleporter.remainingUses);
        
        // après utilisation complète
        for (int i = 0; i < 3; i++) {
            teleporter.registerUseThisTurn();
        }
        
        // teleporter est épuisé
        assertFalse(teleporter.hasRemainingUses());
        assertEquals(0, teleporter.remainingUses);
        
        // drill reste disponible
        assertTrue(drill.hasRemainingUses());
        assertNull(drill.remainingUses);
    }
}

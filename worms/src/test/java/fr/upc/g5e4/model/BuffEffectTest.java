package fr.upc.g5e4.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import fr.upc.g5e4.model.BuffModel;
import org.jbox2d.dynamics.World;

/**
 * Tests unitaires pour le système de buffs temporaires
 */
public class BuffEffectTest {
    
    private BuffModel speedBuff;
    private BuffModel jumpBuff;
    private World world;
    
    @BeforeEach
    public void setUp() {
        //créer un monde physique pour les tests
        world = new World(new org.jbox2d.common.Vec2(0, -10));
        
        //créer des buffs
        speedBuff = new BuffModel(BuffModel.BuffType.SPEED_BOOST, 5000); // 5 secondes
        jumpBuff = new BuffModel(BuffModel.BuffType.JUMP_BOOST, 3000);   // 3 secondes
    }
    
    /**
     * test: vérifier que les buffs sont actifs au départ
     */
    @Test
    public void testBuffIsActiveAtStart() {
        assertTrue(speedBuff.isActive());
        assertTrue(jumpBuff.isActive());
    }
    
    /**
     * test: vérifier le multiplicateur de speed buff
     */
    @Test
    public void testSpeedBuffMultiplier() {
        assertEquals(BuffModel.BuffType.SPEED_BOOST, speedBuff.getType());
        assertEquals(1.5, speedBuff.getMultiplier());
    }
    
    /**
     * test: vérifier le multiplicateur de jump buff
     */
    @Test
    public void testJumpBuffMultiplier() {
        assertEquals(BuffModel.BuffType.JUMP_BOOST, jumpBuff.getType());
        assertEquals(1.5, jumpBuff.getMultiplier());
    }
    
    /**
     * test: vérifier que le temps restant décroît
     */
    @Test
    public void testRemainingTimeDecreases() throws InterruptedException {
        long remainingBefore = speedBuff.getRemainingTime();
        
        Thread.sleep(100);
        
        long remainingAfter = speedBuff.getRemainingTime();
        assertTrue(remainingAfter < remainingBefore);
        assertTrue(remainingAfter > 0);
    }
    
    /**
     * test: vérifier l'expiration d'un buff court
     */
    @Test
    public void testBuffExpiration() throws InterruptedException {
        BuffModel shortBuff = new BuffModel(BuffModel.BuffType.SPEED_BOOST, 100);
        
        assertTrue(shortBuff.isActive());
        
        Thread.sleep(150);
        
        assertFalse(shortBuff.isActive());
        assertEquals(0, shortBuff.getRemainingTime());
    }
    
    /**
     * test: vérifier que le temps restant ne devient jamais négatif
     */
    @Test
    public void testRemainingTimeNeverNegative() throws InterruptedException {
        BuffModel shortBuff = new BuffModel(BuffModel.BuffType.JUMP_BOOST, 50);
        
        Thread.sleep(200);
        
        long remaining = shortBuff.getRemainingTime();
        assertTrue(remaining >= 0);
        assertEquals(0, shortBuff.getRemainingTime());
    }
    
    /**
     * test: vérifier les types de buff
     */
    @Test
    public void testBuffTypes() {
        assertEquals("Speed Boost", BuffModel.BuffType.SPEED_BOOST.getName());
        assertEquals("Jump Boost", BuffModel.BuffType.JUMP_BOOST.getName());
    }
    
    /**
     * test: vérifier que les buffs distincts ont des timers distincts
     */
    @Test
    public void testMultipleBuffs() {
        BuffModel buff1 = new BuffModel(BuffModel.BuffType.SPEED_BOOST, 5000);
        BuffModel buff2 = new BuffModel(BuffModel.BuffType.JUMP_BOOST, 3000);
        
        assertTrue(buff1.isActive());
        assertTrue(buff2.isActive());
        
        long time1 = buff1.getRemainingTime();
        long time2 = buff2.getRemainingTime();
        
        // buff1 doit avoir plus de temps que buff2
        assertTrue(time1 > time2);
    }
    
    /**
     * test: vérifier la durée initiale
     */
    @Test
    public void testInitialDuration() {
        BuffModel buff5sec = new BuffModel(BuffModel.BuffType.SPEED_BOOST, 5000);
        BuffModel buff3sec = new BuffModel(BuffModel.BuffType.JUMP_BOOST, 3000);
        
        long remaining5 = buff5sec.getRemainingTime();
        long remaining3 = buff3sec.getRemainingTime();
        
        // le temps restant doit être proche de la durée initiale
        assertTrue(remaining5 <= 5000 && remaining5 > 4950); // marge de 50ms
        assertTrue(remaining3 <= 3000 && remaining3 > 2950); // marge de 50ms
    }
}

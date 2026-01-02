package fr.upc.g5e4.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;

/**
 * Tests unitaires pour le système de timer des étiquettes au nametag
 */
public class NameTagTimerTest {
    
    private NameTagView.ItemIcon itemWithoutTimer;
    private NameTagView.ItemIcon itemWithTimer;
    private BufferedImage testImage;
    
    @BeforeEach
    public void setUp() {
        // créer une image test simple
        testImage = new BufferedImage(18, 18, BufferedImage.TYPE_INT_RGB);
        
        //item sans timer
        itemWithoutTimer = new NameTagView.ItemIcon(testImage, "test_item");
        
        //item avec timer (5 secondes)
        itemWithTimer = new NameTagView.ItemIcon(testImage, "buff_item", 5000);
    }

    
    /**
     * test: Vérifier qu'un item avec durée a un timer
     */
    @Test
    public void testItemWithTimer() {
        assertEquals(5000, itemWithTimer.duration);
        assertTrue(itemWithTimer.getRemainingTime() > 0);
        assertFalse(itemWithTimer.isExpired());
    }
    
    /**
     * test: vérifier que le timer décroît
     */
    @Test
    public void testTimerDecreases() throws InterruptedException {
        long remainingBefore = itemWithTimer.getRemainingTime();
        
        // Attendre 100ms
        Thread.sleep(100);
        
        long remainingAfter = itemWithTimer.getRemainingTime();
        assertTrue(remainingAfter < remainingBefore);
    }
    
    /**
     * test: vérifier l'expiration d'un item
     */
    @Test
    public void testItemExpiration() throws InterruptedException {
        //créer un item avec une courte durée (100ms)
        NameTagView.ItemIcon shortTimer = new NameTagView.ItemIcon(testImage, "short_timer", 100);
        
        assertFalse(shortTimer.isExpired());
        
        //attendre que le timer expire
        Thread.sleep(150);
        
        assertTrue(shortTimer.isExpired());
        assertEquals(0, shortTimer.getRemainingTime());
    }
    
    /**
     * test: vérifier que le temps restant ne devient pas négatif
     */
    @Test
    public void testTimerNeverNegative() throws InterruptedException {
        //créer un item avec une courte durée
        NameTagView.ItemIcon shortTimer = new NameTagView.ItemIcon(testImage, "short", 50);
        
        //attendre longtemps après expiration
        Thread.sleep(200);
        
        // le temps restant doit être 0, pas négatif
        assertEquals(0, shortTimer.getRemainingTime());
        assertTrue(shortTimer.isExpired());
    }
    
    /**
     * test: vérifier les noms des items
     */
    @Test
    public void testItemNames() {
        assertEquals("test_item", itemWithoutTimer.name);
        assertEquals("buff_item", itemWithTimer.name);
    }
    
    /**
     * test: vérifier que les images sont correctement stockées
     */
    @Test
    public void testItemImages() {
        assertNotNull(itemWithoutTimer.image);
        assertNotNull(itemWithTimer.image);
        assertEquals(18, itemWithoutTimer.image.getWidth());
        assertEquals(18, itemWithoutTimer.image.getHeight());
    }
    
    /**
     * test: vérifier la gestion de timers multiples
     */
    @Test
    public void testMultipleTimers() {
        NameTagView.ItemIcon timer1 = new NameTagView.ItemIcon(testImage, "timer1", 10000);
        NameTagView.ItemIcon timer2 = new NameTagView.ItemIcon(testImage, "timer2", 5000);
        NameTagView.ItemIcon timer3 = new NameTagView.ItemIcon(testImage, "timer3", 0);
        
        assertTrue(timer1.getRemainingTime() > timer2.getRemainingTime());
        assertEquals(0, timer3.getRemainingTime());
        assertFalse(timer1.isExpired());
        assertFalse(timer2.isExpired());
        assertFalse(timer3.isExpired());
    }
}

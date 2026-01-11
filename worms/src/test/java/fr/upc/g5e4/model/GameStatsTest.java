package fr.upc.g5e4.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameStatsTest {
    private GameStats stats;
    
    @BeforeEach
    void setUp() {
        stats = new GameStats();
    }
    
    @Test
    void testConstructorInitializesAllFieldsToZero() {
        assertEquals(0, stats.getDamageDealt());
        assertEquals(0, stats.getShotsFired());
        assertEquals(0, stats.getShotsHit());
        assertEquals(0, stats.getKills());
        assertEquals(1, stats.getRoundsPlayed());  // Commence au round 1
    }
    
    @Test
    void testAddDamage() {
        stats.addDamage(50);
        assertEquals(50, stats.getDamageDealt());
        
        stats.addDamage(30);
        assertEquals(80, stats.getDamageDealt());
    }
    
    @Test
    void testIncrementShotsFired() {
        stats.incrementShotsFired();
        assertEquals(1, stats.getShotsFired());
        
        stats.incrementShotsFired();
        stats.incrementShotsFired();
        assertEquals(3, stats.getShotsFired());
    }
    
    @Test
    void testIncrementShotsHit() {
        stats.incrementShotsHit();
        assertEquals(1, stats.getShotsHit());
        
        stats.incrementShotsHit();
        assertEquals(2, stats.getShotsHit());
    }
    
    @Test
    void testIncrementKills() {
        stats.incrementKills();
        assertEquals(1, stats.getKills());
        
        stats.incrementKills();
        stats.incrementKills();
        assertEquals(3, stats.getKills());
    }
    
    @Test
    void testIncrementRounds() {
        assertEquals(1, stats.getRoundsPlayed());
        
        stats.incrementRounds();
        assertEquals(2, stats.getRoundsPlayed());
    }
    
    @Test
    void testAccuracyWithNoShots() {
        assertEquals(0, stats.getAccuracy());
    }
    
    @Test
    void testAccuracyWithShots() {
        stats.incrementShotsFired();
        stats.incrementShotsFired();
        stats.incrementShotsFired();
        stats.incrementShotsFired();
        stats.incrementShotsHit();
        stats.incrementShotsHit();
        
        // 2 hits sur 4 tirs = 50%
        assertEquals(50, stats.getAccuracy());
    }
    
    @Test
    void testAccuracyPerfect() {
        stats.incrementShotsFired();
        stats.incrementShotsHit();
        
        assertEquals(100, stats.getAccuracy());
    }
    
    @Test
    void testAccuracyNoHits() {
        stats.incrementShotsFired();
        stats.incrementShotsFired();
        
        assertEquals(0, stats.getAccuracy());
    }
    
    @Test
    void testGameDuration() throws InterruptedException {
        long startDuration = stats.getGameDuration();
        
        // Attendre un peu
        Thread.sleep(100);
        
        long endDuration = stats.getGameDuration();
        
        assertTrue(endDuration >= startDuration + 100, 
                   "La durée devrait augmenter avec le temps");
    }
    
    @Test
    void testMultipleStatsOperations() {
        stats.addDamage(100);
        stats.incrementShotsFired();
        stats.incrementShotsFired();
        stats.incrementShotsHit();
        stats.incrementKills();
        stats.incrementRounds();
        
        assertEquals(100, stats.getDamageDealt());
        assertEquals(2, stats.getShotsFired());
        assertEquals(1, stats.getShotsHit());
        assertEquals(1, stats.getKills());
        assertEquals(2, stats.getRoundsPlayed());
        assertEquals(50, stats.getAccuracy());
    }
}

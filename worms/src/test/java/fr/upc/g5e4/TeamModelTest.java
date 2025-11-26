package fr.upc.g5e4;

import static org.junit.jupiter.api.Assertions.*;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.upc.g5e4.model.CharacterModel;
import fr.upc.g5e4.model.GroundModel;
import fr.upc.g5e4.model.TeamModel;

public class TeamModelTest {
    private GroundModel groundModel;
    private World world;
    private TeamModel team;
    
    @BeforeEach
    void setUp() {
        int[] groundY = {400, 420, 450, 450};
        groundModel = new GroundModel(groundY, 800, 600, 500);
        world = new World(new Vec2(0, 9.8f));
    }
    
    @Test
    void testConstructorWithValidWormNumber() {
        team = new TeamModel(groundModel, world, "Team1", 100, 50, 5);
        
        assertNotNull(team);
        assertEquals(5, team.getWormNumberAtStart());
        assertEquals(5, team.getAliveWormNumber());
        assertNotNull(team.getTabWorms());
        assertEquals(5, team.getTabWorms().length);
    }
    
    @Test
    void testWormsInitialization() {
        team = new TeamModel(groundModel, world, "Team1", 100, 50, 3);
        
        CharacterModel[] worms = team.getTabWorms();
        
        // Vérifier que tous les worms sont initialisés
        for (int i = 0; i < 3; i++) {
            assertNotNull(worms[i], "Le worm a la place " + i + " ne devrait pas etre null");
            assertFalse(worms[i].isDead(), "Le worm devrait etre vivant");
            assertEquals(100.0, worms[i].getHealth());
            assertEquals(50.0, worms[i].getShield());
        }
    }
    
    @Test
    /**
     * Teste getWorm avec des index valides
     */
    void testGetWormWithValidIndex() {
        team = new TeamModel(groundModel, world, "Team1", 100, 50, 5);
        
        CharacterModel worm = team.getWorm(0);
        assertNotNull(worm);
        
        CharacterModel worm2 = team.getWorm(4);
        assertNotNull(worm2);
    }
    
    @Test
    /**
     * Teste getWorm avec des index invalides
     */
    void testGetWormWithInvalidIndex() {
        team = new TeamModel(groundModel, world, "Team1", 100, 50, 5);
        
        assertThrows(IndexOutOfBoundsException.class, () -> {
            team.getWorm(-1);
        });
        
        assertThrows(IndexOutOfBoundsException.class, () -> {
            team.getWorm(5);
        });
        
        assertThrows(IndexOutOfBoundsException.class, () -> {
            team.getWorm(10);
        });
    }
    
    @Test
    void testRemoveWorm() {
        team = new TeamModel(groundModel, world, "Team1", 100, 50, 5);
        
        assertNotNull(team.getWorm(2));
        team.removeWorm(2);
        assertNull(team.getWorm(2));
    }
    
    @Test
    void testRemoveWormWithInvalidIndex() {
        team = new TeamModel(groundModel, world, "Team1", 100, 50, 5);
        
        assertThrows(IndexOutOfBoundsException.class, () -> {
            team.removeWorm(-1);
        });
        
        assertThrows(IndexOutOfBoundsException.class, () -> {
            team.removeWorm(5);
        });
    }
    
    @Test
    /**
     * Teste l'ajout d'un worm a une position qui vient de se libérer
     */
    void testAddWorm() {
        team = new TeamModel(groundModel, world, "Team1", 100, 50, 5);
        
        // on libère la place en supprimant le worm à l'index 2
        team.removeWorm(2);
        assertNull(team.getWorm(2));
        
        // on ajoute le nouveau worm à l'index 2 (devenu libre)
        CharacterModel newWorm = new CharacterModel(groundModel, world, 300, 200);
        team.addWorm(newWorm, 2);
        
        assertNotNull(team.getWorm(2));
        assertEquals(newWorm, team.getWorm(2));
    }
    
    @Test
    /**
     * Teste l'ajout d'un worm à une position prise par un autre worm
     */
    void testAddWormToUsedSlot() {
        team = new TeamModel(groundModel, world, "Team1", 100, 50, 5);
        
        CharacterModel newWorm = new CharacterModel(groundModel, world, 300, 200);
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            team.addWorm(newWorm, 0);
        });
        assertTrue(exception.getMessage().contains("un worm est dejà à cette place..."));
    }
    
    @Test
    void testAddWormWithInvalidIndex() {
        team = new TeamModel(groundModel, world, "Team1", 100, 50, 5);
        CharacterModel newWorm = new CharacterModel(groundModel, world, 300, 200);
        
        assertThrows(IndexOutOfBoundsException.class, () -> {
            team.addWorm(newWorm, -1);
        });
        
        assertThrows(IndexOutOfBoundsException.class, () -> {
            team.addWorm(newWorm, 5);
        });
    }
    
    @Test
    void testGetAliveWormNumber() {
        team = new TeamModel(groundModel, world, "Team1", 100, 50, 5);
        
        assertEquals(5, team.getAliveWormNumber());
        
        // Tuer un worm
        team.getWorm(0).setHealth(0);
        team.getWorm(0).setDead(true);
        assertEquals(4, team.getAliveWormNumber());
        
        // Tuer un autre worm
        team.getWorm(2).setHealth(0);
        team.getWorm(2).setDead(true);
        assertEquals(3, team.getAliveWormNumber());
    }
}

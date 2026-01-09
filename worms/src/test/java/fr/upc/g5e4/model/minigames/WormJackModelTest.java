package fr.upc.g5e4.model.minigames;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Tests unitaires pour WormJackModel
 */
public class WormJackModelTest {
    
    private WormJackModel model;
    
    @BeforeEach
    void setUp() {
        model = new WormJackModel();
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
        assertTrue(model.isPlayerTurn(), "C'est au joueur de jouer après reset");
        assertEquals(0, model.getPlayerScore(), "Le score du joueur doit être 0 après reset");
        assertEquals(0, model.getWormbotScore(), "Le score du wormbot doit être 0 après reset");
        assertEquals(0, model.getCurrentBet(), "La mise doit être 0 après reset");
    }
    
    @Test
    void testPlayerCardsEmptyAfterReset() {
        model.resetGame();
        List<WormJackModel.Card> playerCards = model.getPlayerCards();
        
        assertNotNull(playerCards, "La liste des cartes du joueur ne doit pas être null");
        assertTrue(playerCards.isEmpty(), "La liste des cartes du joueur doit être vide après reset");
    }
    
    @Test
    void testWormbotCardsEmptyAfterReset() {
        model.resetGame();
        List<WormJackModel.Card> wormbotCards = model.getWormbotCards();
        
        assertNotNull(wormbotCards, "La liste des cartes du wormbot ne doit pas être null");
        assertTrue(wormbotCards.isEmpty(), "La liste des cartes du wormbot doit être vide après reset");
    }

    // Tests du démarrage du jeu
    
    @Test
    void testStartGameChangesState() {
        model.resetGame();
        model.startGame();
        
        assertTrue(model.isGameStarted(), "Le jeu doit être démarré après startGame");
        assertTrue(model.isPlayerTurn(), "C'est au joueur de jouer après startGame");
    }
    
    @Test
    void testStartGameDealsCards() {
        model.resetGame();
        model.startGame();
        
        List<WormJackModel.Card> playerCards = model.getPlayerCards();
        List<WormJackModel.Card> wormbotCards = model.getWormbotCards();
        
        assertFalse(playerCards.isEmpty(), "Le joueur doit avoir au moins une carte après startGame");
        assertFalse(wormbotCards.isEmpty(), "Le wormbot doit avoir au moins une carte après startGame");
    }
    
    // Tests du Hit
    
    @Test
    void testHitAddsCardToPlayer() {
        model.resetGame();
        model.startGame();
        
        int initialSize = model.getPlayerCards().size();
        model.hit();
        
        assertEquals(initialSize + 1, model.getPlayerCards().size(), "Hit doit ajouter une carte au joueur");
    }
    
    @Test
    void testHitIncreasesPlayerScore() {
        model.resetGame();
        model.startGame();
        
        int initialScore = model.getPlayerScore();
        model.hit();
        
        assertTrue(model.getPlayerScore() >= initialScore, 
            "Le score du joueur doit augmenter ou rester stable (gestion des As)");
    }
    
    @Test
    void testHitReturnsTrueOnBust() {
        model.resetGame();
        model.startGame();
        
        // tirer des cartes jusqu'au bust
        boolean busted = false;
        for (int i = 0; i < 10; i++) {
            if (model.hit()) {
                busted = true;
                break;
            }
        }
        
        if (busted) {
            assertTrue(model.isGameFinished(), "Le jeu doit être terminé après un bust");
            assertTrue(model.getPlayerScore() > 21, "Le score doit être > 21 après un bust");
        }
    }
    
    // Tests du Stand
    
    @Test
    void testStandChangesPlayerTurn() {
        model.resetGame();
        model.startGame();
        model.stand();
        
        assertFalse(model.isPlayerTurn(), "Ce n'est plus au joueur de jouer après stand");
    }
    
    // Tests du Wormbot
    
    @Test
    void testWormbotHitAddsCard() {
        model.resetGame();
        model.startGame();
        
        int initialSize = model.getWormbotCards().size();
        model.wormbotHit();
        
        assertEquals(initialSize + 1, model.getWormbotCards().size(), "wormbotHit doit ajouter une carte au wormbot");
    }
    
    @Test
    void testFinishWormbotTurnEndsGame() {
        model.resetGame();
        model.startGame();
        model.finishWormbotTurn();
        
        assertTrue(model.isGameFinished(), "Le jeu doit être terminé après finishWormbotTurn");
    }
    
    // Tests de la classe interne Card
    
    @Test
    void testCardNumericValue() {
        WormJackModel.Card card5 = new WormJackModel.Card("5", "C", null);
        assertEquals(5, card5.getNumericValue(), "Une carte 5 doit avoir une valeur de 5");
        
        WormJackModel.Card cardJ = new WormJackModel.Card("11", "C", null);
        assertEquals(10, cardJ.getNumericValue(), "Un Valet doit valoir 10");
        
        WormJackModel.Card cardQ = new WormJackModel.Card("12", "C", null);
        assertEquals(10, cardQ.getNumericValue(), "Une Dame doit valoir 10");
        
        WormJackModel.Card cardK = new WormJackModel.Card("13", "C", null);
        assertEquals(10, cardK.getNumericValue(), "Un Roi doit valoir 10");

        WormJackModel.Card cardAce = new WormJackModel.Card("1", "C", null);
        assertEquals(11, cardAce.getNumericValue(), "Un As doit valoir 11 initialement");
    }
    
    @Test
    void testCardGetters() {
        WormJackModel.Card card = new WormJackModel.Card("7", "H", null);
        
        assertEquals("7", card.getValue(), "getValue doit retourner la valeur de la carte");
        assertEquals("H", card.getColor(), "getColor doit retourner la couleur de la carte");
        assertNull(card.getImage(), "getImage doit retourner null si aucune image");
    }
    
    // autres tests
    
    @Test
    void testStartGameAfterReset() {
        model.resetGame();
        model.startGame();
        model.resetGame();
        model.startGame();
        
        assertTrue(model.isGameStarted(), "Le jeu doit pouvoir redémarrer après reset");
    }
    
    @Test
    void testScoresArePositive() {
        model.resetGame();
        model.startGame();
        
        assertTrue(model.getPlayerScore() >= 0, "Le score du joueur doit être positif");
        assertTrue(model.getWormbotScore() >= 0, "Le score du wormbot doit être positif");
    }
}

package fr.upc.g5e4.model.minigames;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import fr.upc.g5e4.util.ImageCache;
import fr.upc.g5e4.model.GameManager;
import fr.upc.g5e4.view.minigames.WormJackView;

/**
 * Model pour le mini-jeu WormJack (Blackjack)
 */
public class WormJackModel {
    
    private BufferedImage titleImage;
    
    private boolean gameStarted = false;
    private boolean gameFinished = false;
    private boolean playerTurn = true;
    
    private List<Card> deck;
    private List<Card> playerCards;
    private List<Card> wormbotCards;
    private int playerScore = 0;
    private int wormbotScore = 0;
    private int playerAceCount = 0;
    private int wormbotAceCount = 0;
    
    public static final int BET_AMOUNT = 5;
    private int currentBet = 0;
    
    private GameManager gameManager;
    private WormJackView view;
    
    public enum GameResult {
        PLAYER_BUST,
        WORMBOT_BUST,
        PLAYER_WINS,
        TIE,
        WORMBOT_WINS
    }
    
    /**
     * Classe interne pour représenter une carte
     */
    public static class Card {
        private String value;
        private String color;
        private BufferedImage image;
        
        public Card(String value, String color, BufferedImage image) {
            this.value = value;
            this.color = color;
            this.image = image;
        }
        
        public int getNumericValue() {
            int val = Integer.parseInt(value);
            if (val >= 11 && val <= 13) return 10; // J, Q, K = 10
            if (val == 1) return 11; // As = 11 ou 1 si on dépasse 21
            return val;
        }
        
        public BufferedImage getImage() { return image; }
        public String getValue() { return value; }
        public String getColor() { return color; }
    }
    
    public WormJackModel() {
        loadImages();
        playerCards = new ArrayList<>();
        wormbotCards = new ArrayList<>();
    }
    
    public void setView(WormJackView view) {
        this.view = view;
    }
    
    private void loadImages() {
        titleImage = ImageCache.get("/wormJack.png");
        if (titleImage == null) {
            System.err.println("Erreur lors du chargement des images WormJack");
        }
    }
    
    private void initDeck() {
        deck = new ArrayList<>();
        String[] colors = {"C", "D", "H", "P"};
        
        for (String color : colors) {
            for (int value = 1; value <= 13; value++) {
                try {
                    String filename = "/sprite_card/" + value + "-" + color + ".png";
                    BufferedImage img = javax.imageio.ImageIO.read(getClass().getResource(filename));
                    deck.add(new Card(String.valueOf(value), color, img));
                } catch (Exception e) {
                    System.err.println("Erreur chargement carte: " + value + "-" + color);
                }
            }
        }
        
        Collections.shuffle(deck);
    }
    
    /**
     * Reset le jeu
     * init variables de jeu
     */
    public void resetGame() {
        gameStarted = false;
        gameFinished = false;
        playerTurn = true;
        playerScore = 0;
        wormbotScore = 0;
        playerAceCount = 0;
        wormbotAceCount = 0;
        playerCards = new ArrayList<>();
        wormbotCards = new ArrayList<>();
        currentBet = 0;
        
        initDeck();
        if (view != null) {
            view.repaint();
        }
    }
    
    /**
     * Placer un pari
     * @return true si le pari a été placé false sinon
     */
    public boolean placeBet() {
        if (gameManager == null || gameManager.getCurrentTeam() == null) {
            System.err.println("Erreur: pas d'équipe active");
            return false;
        }
        
        int currentMoney = gameManager.getCurrentTeam().getMoney();
        if (currentMoney < BET_AMOUNT) {
            System.out.println("Pas assez d'argent pour parier!");
            return false;
        }
        
        currentBet += BET_AMOUNT;
        gameManager.getCurrentTeam().removeMoney(BET_AMOUNT);
        
        if (view != null) {
            view.repaint();
        }
        return true;
    }
    
    // Lancer le jeu
    public void startGame() {
        gameStarted = true;
        playerTurn = true;
        
        // Distribution des premières cartes
        hit(); // joueur tire 1e carte
        wormbotHit(); // wormbot tire 1e carte
        
        if (view != null) {
            view.repaint();
        }
    }
    
    /**
     * Le joueur tire une carte
     * @return true si le joueur a bust (> 21)
     */
    public boolean hit() {
        if (deck.isEmpty()) return false;
        
        Card newCard = deck.remove(0);
        playerCards.add(newCard);
        int cardValue = newCard.getNumericValue();
        playerScore += cardValue;
        
        if (cardValue == 11) {
            playerAceCount++;
        }
        
        // Gérer les As si on dépasse 21
        while (playerScore > 21 && playerAceCount > 0) {
            playerScore -= 10;
            playerAceCount--;
        }
        
        if (playerScore > 21) {
            gameFinished = true;
            handleGameEnd();
            if (view != null) {
                view.repaint();
            }
            return true;
        }
        
        if (view != null) {
            view.repaint();
        }
        return false;
    }
    
    // Le wormbot tire une carte 
    public void wormbotHit() {
        if (deck.isEmpty()) return;
        
        Card newCard = deck.remove(0);
        wormbotCards.add(newCard);
        int cardValue = newCard.getNumericValue();
        wormbotScore += cardValue;
        
        if (cardValue == 11) {
            wormbotAceCount++;
        }
        
        while (wormbotScore > 21 && wormbotAceCount > 0) {
            wormbotScore -= 10;
            wormbotAceCount--;
        }
        
        if (view != null) {
            view.repaint();
        }
    }
    
    // Le joueur reste (stand)
    public void stand() {
        playerTurn = false;
        if (view != null) {
            view.repaint();
        }
    }
    
    /**
     * Vérifie si le wormbot doit continuer à tirer (< 17)
     * @return true si le wormbot doit encore tirer
     */
    public boolean shouldWormbotHit() {
        return wormbotScore < 17;
    }
    
    // Termine le tour du wormbot et finit la partie
    public void finishWormbotTurn() {
        gameFinished = true;
        if (view != null) {
            view.repaint();
        }
    }
    
    // Gère les gains/pertes après la fin du jeu
    private void handleGameEnd() {
        int gains = 0;
        if (playerScore > 21) {
            System.out.println("Bust! Vous avez perdu.");
        } else if (wormbotScore > 21) {
            gains = currentBet * 2;
            System.out.println("Le wormbot a bust! Vous gagnez " + gains + "$");
        } else if (playerScore > wormbotScore) {
            gains = currentBet * 2;
            System.out.println("Vous gagnez! +" + gains + "$");
        } else if (playerScore == wormbotScore) {
            gains = currentBet;
            System.out.println("Égalité! Mise remboursée.");
        } else {
            System.out.println("Le wormbot gagne. Vous avez perdu.");
        }
        
        if (gains > 0) {
            if (gameManager != null && gameManager.getCurrentTeam() != null) {
                gameManager.getCurrentTeam().addMoney(gains);
            }
        }
        
    }
    
    // Getters
    public BufferedImage getTitleImage() { return titleImage; }
    public List<Card> getPlayerCards() { return playerCards; }
    public List<Card> getWormbotCards() { return wormbotCards; }
    public int getPlayerScore() { return playerScore; }
    public int getWormbotScore() { return wormbotScore; }
    public boolean isGameStarted() { return gameStarted; }
    public boolean isGameFinished() { return gameFinished; }
    public boolean isPlayerTurn() { return playerTurn; }
    public int getCurrentBet() { return currentBet; }
    
    public int getCurrentMoney() {
        if (gameManager != null && gameManager.getCurrentTeam() != null) {
            return gameManager.getCurrentTeam().getMoney();
        }
        return 0;
    }
    
    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }
}

package fr.upc.g5e4.model.minigames;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import fr.upc.g5e4.util.ImageCache;
import fr.upc.g5e4.model.GameManager;
import fr.upc.g5e4.view.minigames.WormGuessView;

/**
 * Model pour le mini-jeu WormGuess
 * @author Nassim
 */
public class WormGuessModel {
    
    private BufferedImage titleImage;
    private BufferedImage[] wormImages;
    private BufferedImage[] selectedWorms;
    
    private boolean gameStarted = false;
    private boolean gameFinished = false;
    private int winnerIndex = -1;
    private boolean isAnimating = false;
    private int currentAnimFrame = 0;
    
    private int[] bets = new int[2];
    public static final int BET_AMOUNT = 1;
    
    private GameManager gameManager;
    private WormGuessView view;
    
    public WormGuessModel() {
        loadImages();
    }
    
    public void setView(WormGuessView view) {
        this.view = view;
    }
    
    private void loadImages() {
        titleImage = ImageCache.get("/wormGuess.png");
        
        wormImages = new BufferedImage[6];
        wormImages[0] = ImageCache.get("/wormCostar.png");
        wormImages[1] = ImageCache.get("/wormNoel.png");
        wormImages[2] = ImageCache.get("/wormPinky.png");
        wormImages[3] = ImageCache.get("/wormPixel.png");
        wormImages[4] = ImageCache.get("/wormCuisto.png");
        wormImages[5] = ImageCache.get("/wormNerd.png");
        
        if (titleImage == null || wormImages[0] == null) {
            System.err.println("Erreur lors du chargement des images WormGuess");
        }
    }
    
    /**
     * Reset le jeu pour une nouvelle partie
     * init des images de worms aleatoires
     * init variables de jeu
     */
    public void resetGame() {
        selectedWorms = new BufferedImage[2];
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < wormImages.length; i++) {
            indices.add(i);
        }
        Collections.shuffle(indices);
        
        for (int i = 0; i < 2; i++) {
            selectedWorms[i] = wormImages[indices.get(i)];
        }
        
        gameStarted = false;
        gameFinished = false;
        winnerIndex = -1;
        isAnimating = false;
        currentAnimFrame = 0;
        bets = new int[2];
        
        if (view != null) {
            view.repaint();
        }
    }
    
    /**
     * Placer un pari sur le worm d'index donné
     * @param wormIndex index du worm
     * @return true si le pari a été placé false sinon
     */
    public boolean placeBet(int wormIndex) {
        if (gameManager == null || gameManager.getCurrentTeam() == null) {
            System.err.println("Erreur: pas d'équipe active");
            return false;
        }
        
        int currentMoney = gameManager.getCurrentTeam().getMoney();
        if (currentMoney < BET_AMOUNT) {
            System.out.println("Pas assez d'argent pour parier!");
            return false;
        }
        
        bets[wormIndex] += BET_AMOUNT;
        gameManager.getCurrentTeam().removeMoney(BET_AMOUNT);
        
        if (view != null) {
            view.repaint();
        }
        return true;
    }
    
    // Lancer le jeu
    public void startGame() {
        gameStarted = true;
        isAnimating = true;
        winnerIndex = (int) (Math.random() * 2);
        currentAnimFrame = 0;
        if (view != null) {
            view.repaint();
        }
    }
    
    /**
     * update l'animation "roulette" des worms 
     * @return true si l'animation est terminée
     */
    public boolean updateAnimation() {
        currentAnimFrame++;
        
        if (currentAnimFrame >= 60) {
            isAnimating = false;
            gameFinished = true;
            gestionWin();
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
    
    // Gérer les gains/pertes à la fin du jeu
    private void gestionWin() {
        if (bets[winnerIndex] > 0) {
            int gains = bets[winnerIndex] * 2;
            if (gameManager != null && gameManager.getCurrentTeam() != null) {
                gameManager.getCurrentTeam().addMoney(gains);
            }
            System.out.println("Gagné! +" + gains + "$");
        } else {
            System.out.println("Perdu! Le worm " + (winnerIndex + 1) + " était le bon.");
        }
    }
    
    // Arreter le jeu
    public void stopGame() {
        isAnimating = false;
        gameStarted = false;
        gameFinished = false;
        if (view != null) {
            view.repaint();
        }
    }
    
    public int getTotalBets() {
        int total = 0;
        for (int bet : bets) {
            total += bet;
        }
        return total;
    }
    
    // Getters
    public BufferedImage getTitleImage() { return titleImage; }
    public BufferedImage[] getSelectedWorms() { return selectedWorms; }
    public boolean isGameStarted() { return gameStarted; }
    public boolean isGameFinished() { return gameFinished; }
    public boolean isAnimating() { return isAnimating; }
    public int getWinnerIndex() { return winnerIndex; }
    public int[] getBets() { return bets; }
    public int getCurrentAnimFrame() { return currentAnimFrame; }
    
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

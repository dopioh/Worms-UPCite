package fr.upc.g5e4.model.minigames;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import fr.upc.g5e4.util.ImageCache;
import fr.upc.g5e4.model.GameManager;

import fr.upc.g5e4.view.minigames.WormRaceView;

/**
 * Model pour le mini-jeu WormRace
 */
public class WormRaceModel {
    
    private BufferedImage titleImage;
    private BufferedImage[] wormImages;
    private BufferedImage[] selectedWorms;
    
    private float[] wormPositions = new float[3];
    private boolean raceStarted = false;
    private boolean raceFinished = false;
    private int winnerIndex = -1;
    
    private int[] bets = new int[3];
    public static final int BET_AMOUNT = 1;
    
    private GameManager gameManager;
    private WormRaceView view;
    
    public WormRaceModel() {
        loadImages();
    }
    
    public void setView(WormRaceView view) {
        this.view = view;
    }
    
    private void loadImages() {
        titleImage = ImageCache.get("/wormRace.png");
        
        wormImages = new BufferedImage[6];
        wormImages[0] = ImageCache.get("/wormCostar.png");
        wormImages[1] = ImageCache.get("/wormNoel.png");
        wormImages[2] = ImageCache.get("/wormPinky.png");
        wormImages[3] = ImageCache.get("/wormPixel.png");
        wormImages[4] = ImageCache.get("/wormCuisto.png");
        wormImages[5] = ImageCache.get("/wormNerd.png");
        
        if (titleImage == null || wormImages[0] == null) {
            System.err.println("Erreur lors du chargement des images WormRace");
        }
    }
    
    /**
     * Reset le jeu pour une nouvelle course
     * init des images de worms aleatoires
     * init variables de jeu
     */
    public void resetGame() {
        selectedWorms = new BufferedImage[3];
        List<Integer> indices = new ArrayList<>(); // utilisation d'une liste pour utiliser shuffle sans passer par Random
        for (int i = 0; i < wormImages.length; i++) {
            indices.add(i);
        }
        Collections.shuffle(indices);
        
        for (int i = 0; i < 3; i++) {
            selectedWorms[i] = wormImages[indices.get(i)];
        }
        
        raceStarted = false;
        raceFinished = false;
        winnerIndex = -1;
        wormPositions = new float[3];
        bets = new int[3];
        
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
        // securite il y'a bien une partie en cours
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
    
    // Lancer la course
    public void startRace() {
        raceStarted = true;
        raceFinished = false;
        if (view != null) {
            view.repaint();
        }
    }
    
    /**
     * Avance les worms sur la piste (1.0f = le worm a fini la course)
     * @return true si la course est terminée
     */
    public boolean updatePositions() {
        for (int i = 0; i < wormPositions.length; i++) {
            wormPositions[i] += (float) (Math.random() * 0.02);
            
            if (wormPositions[i] >= 1.0f && !raceFinished) {
                raceFinished = true;
                winnerIndex = i;
                gestionWin();
                if (view != null) {
                    view.repaint();
                }
                return true;
            }
        }
        if (view != null) {
            view.repaint();
        }
        return false;
    }
    
    // Gére les gains/pertes à la fin de la course
    private void gestionWin() {
        if (bets[winnerIndex] > 0) {
            int gains = bets[winnerIndex] * 2;
            if (gameManager != null && gameManager.getCurrentTeam() != null) {
                gameManager.getCurrentTeam().addMoney(gains);
            }
            System.out.println("Gagné! +" + gains + "$");
        } else {
            System.out.println("Perdu! Le worm " + (winnerIndex + 1) + " a gagné.");
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
    public float[] getWormPositions() { return wormPositions; }
    public boolean isRaceStarted() { return raceStarted; }
    public boolean isRaceFinished() { return raceFinished; }
    public int getWinnerIndex() { return winnerIndex; }
    public int[] getBets() { return bets; }
    
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

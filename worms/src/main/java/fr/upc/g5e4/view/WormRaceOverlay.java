package fr.upc.g5e4.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import fr.upc.g5e4.controller.PlayerDataController;

/**
 * Overlay pour le mini-jeu WormGuess
 */

public class WormRaceOverlay extends JPanel {
    private boolean visible = false;
    private BufferedImage titleImage;
    private BufferedImage[] wormImages; // tableau de toutes les images de worms
    private BufferedImage[] selectedWorms; // tableau des 3 worms sélectionnés pour le jeu
    
    private static final int OVERLAY_WIDTH = 800;
    private static final int OVERLAY_HEIGHT = 600;
    
    private float[] wormPositions = new float[3]; // positions de chaque worm entre 0.0 à 1.0 (1.0 = fin de la course)
    private boolean raceStarted = false;
    private boolean raceFinished = false;
    private int winnerIndex = -1;
    private Timer raceTimer;
    
    private int[] bets = new int[3]; // tableau pcq on peut parier sur 3 worms
    private Rectangle[] betButtons = new Rectangle[3];
    private Rectangle startRaceButton; // c'est le bouton "LANCER LA COURSE"
    private static final int BET_AMOUNT = 3; // on parie 3$ par 3$
    
    private PlayerDataController playerDataController;
    
    public WormRaceOverlay() {
        setOpaque(false);
        setVisible(false);
        setEnabled(false);
        loadImages();
        
        playerDataController = new PlayerDataController();
        playerDataController.chargerJoueur();
    
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!visible || !isVisible()) return;
                
                int w = getWidth();
                int h = getHeight();
                int panelX = (w - OVERLAY_WIDTH) / 2;
                int panelY = (h - OVERLAY_HEIGHT) / 2;
                Rectangle overlayBounds = new Rectangle(panelX, panelY, OVERLAY_WIDTH, OVERLAY_HEIGHT);
                
                if (overlayBounds.contains(e.getX(), e.getY())) {
                    handleClick(e.getX(), e.getY(), panelX, panelY);
                    e.consume();
                } else {
                    // cliquer hors overlay = on stop tout et on ferme
                    stopRace();
                    visible = false;
                    setVisible(false);
                    setEnabled(false);
                    repaint();
                }
            }
        });
    }
    
    private void loadImages() {
        try {
            // image titre
            titleImage = javax.imageio.ImageIO.read(getClass().getResource("/wormrace.png"));
            
            // image worms
            wormImages = new BufferedImage[4];
            wormImages[0] = javax.imageio.ImageIO.read(getClass().getResource("/worm.png"));
            wormImages[1] = javax.imageio.ImageIO.read(getClass().getResource("/wormNoel.png"));
            wormImages[2] = javax.imageio.ImageIO.read(getClass().getResource("/wormPinky.png"));
            wormImages[3] = javax.imageio.ImageIO.read(getClass().getResource("/wormPixel.png"));
            
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des images WormRace: " + e.getMessage());
        }
    }
    
    public void showRace() {
        // choisir aleatoirement 3 worms
        selectedWorms = new BufferedImage[3];
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < wormImages.length; i++) {
            indices.add(i);
        }
        Collections.shuffle(indices);
        
        for (int i = 0; i < 3; i++) {
            selectedWorms[i] = wormImages[indices.get(i)];
        }
        
        // reset la course
        raceStarted = false;
        raceFinished = false;
        winnerIndex = -1;
        wormPositions = new float[3];
        bets = new int[3];
        betButtons = new Rectangle[3];
        startRaceButton = null;
        
        this.visible = true;
        setVisible(true);
        setEnabled(true);
        setOpaque(false);
        
        // on force l'overlay a etre au premie plan
        if (getParent() instanceof JLayeredPane) {
            JLayeredPane parent = (JLayeredPane) getParent();
            parent.moveToFront(this);
            parent.setLayer(this, JLayeredPane.DRAG_LAYER + 10);
        }
        
        repaint();
        if (getParent() != null) { //securité
            getParent().repaint();
        }
    }
    
    public void setRaceVisible(boolean visible) {
        this.visible = visible;
        repaint();
    }
    
    public boolean isRaceVisible() {
        return visible;
    }

    private int getTotalBets() {
        int total = 0;
        for (int bet : bets) {
            total += bet;
        }
        return total;
    }
    
    /**
     * Place un pari sur le worm d'index donné
     * @param wormIndex index du worm/la piste du worm
     */
    private void placeBet(int wormIndex) {
        int currentMoney = playerDataController.getPlayerData().getMoney();
        if (currentMoney < BET_AMOUNT) {
            System.out.println("Pas assez d'argent pour parier!");
            return;
        }
        
        bets[wormIndex] += BET_AMOUNT;
        playerDataController.getPlayerData().setMoney(currentMoney - BET_AMOUNT);
        playerDataController.sauvegarderJoueur();
        
        repaint();
    }
    
    /**
     * Démarre la course avec un timer
     */
    private void startRace() {
        raceStarted = true;
        raceFinished = false;
        
        raceTimer = new Timer(50, e -> {
            // chaque worm = une vitesse aléatoire
            for (int i = 0; i < wormPositions.length; i++) {
                wormPositions[i] += (float) (Math.random() * 0.02);
                
                if (wormPositions[i] >= 1.0f && !raceFinished) {
                    raceFinished = true;
                    winnerIndex = i;
                    stopRace();
                    gestionWin();
                }
            }
            repaint();
        });
        raceTimer.start();
    }
    
    private void stopRace() {
        if (raceTimer != null) {
            raceTimer.stop();
            raceTimer = null;
        }
    }

    /**
     * Gère les gains/pertes après la course
     */
    private void gestionWin() {
        if (bets[winnerIndex] > 0) {
            // si le joueur a misé sur le worm gagnant
            int gains = bets[winnerIndex] * 2; // Double la mise
            playerDataController.getPlayerData().setMoney(
                playerDataController.getPlayerData().getMoney() + gains
            );
            playerDataController.sauvegarderJoueur();
            System.out.println("Gagné! +" + gains + "$");
        } else {
            System.out.println("Perdu! Le worm " + (winnerIndex + 1) + " a gagné.");
        }
    }

    /**
     * Gère les clics sur les boutons de l'overlay
     * @param x position X du clic
     * @param y position Y du clic
     * @param panelX position X de l'overlay
     * @param panelY position Y de l'overlay
     */
    private void handleClick(int x, int y, int panelX, int panelY) {
        // logique bouton LANCER LA COURSE
        if (!raceStarted && startRaceButton != null && startRaceButton.contains(x, y)) {
            if (getTotalBets() > 0) {
                startRace();
            }
            return;
        }
        
        // logique boutons de pari
        if (!raceStarted) {
            for (int i = 0; i < betButtons.length; i++) {
                if (betButtons[i] != null && betButtons[i].contains(x, y)) {
                    placeBet(i);
                    return;
                }
            }
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (!visible) return;
        
        Graphics2D g2 = (Graphics2D) g;
        // antialiasing pour lisser le rendu
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int w = getWidth();
        int h = getHeight();
        
        // overlay au centre
        int panelX = (w - OVERLAY_WIDTH) / 2;
        int panelY = (h - OVERLAY_HEIGHT) / 2;
        
        g2.setColor(new Color(139, 0, 0)); // Rouge velours
        g2.fillRoundRect(panelX, panelY, OVERLAY_WIDTH, OVERLAY_HEIGHT, 20, 20);
        
        g2.setColor(new Color(255, 215, 0));
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(panelX, panelY, OVERLAY_WIDTH, OVERLAY_HEIGHT, 20, 20);
        
        // afficher l'argent
        g2.setColor(new Color(255, 215, 0));
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        String moneyText = "Argent: " + playerDataController.getPlayerData().getMoney() + "$";
        FontMetrics moneyFm = g2.getFontMetrics();
        int moneyTextW = moneyFm.stringWidth(moneyText);
        g2.drawString(moneyText, panelX + OVERLAY_WIDTH - moneyTextW - 20, panelY + 30);
        
        if (titleImage != null) {
            int titleWidth = 250;
            int titleHeight = (int) (titleImage.getHeight() * ((double) titleWidth / titleImage.getWidth()));
            int titleX = panelX + (OVERLAY_WIDTH - titleWidth) / 2;
            int titleY = panelY + 20;
            g2.drawImage(titleImage, titleX, titleY, titleWidth, titleHeight, null);
        }
        
        // pistes de course
        int trackStartX = panelX + 50;
        int trackWidth = OVERLAY_WIDTH - 100;
        int trackStartY = panelY + 160;
        int laneHeight = 70;
        
        if (selectedWorms != null) {
            for (int i = 0; i < selectedWorms.length; i++) {
                int laneY = trackStartY + i * laneHeight;
                
                // piste
                g2.setColor(new Color(100, 50, 0));
                g2.fillRect(trackStartX, laneY + 25, trackWidth, 30);

                // ligne depart
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(trackStartX, laneY + 20, trackStartX, laneY + 60);
                
                // ligne arrivée
                g2.drawLine(trackStartX + trackWidth, laneY + 20, trackStartX + trackWidth, laneY + 60);
                
                // worm de la piste
                if (selectedWorms[i] != null) {
                    int wormSize = 50;
                    int wormX = trackStartX + (int)(wormPositions[i] * trackWidth) - wormSize / 2;
                    int wormY = laneY + 15;
                    g2.drawImage(selectedWorms[i], wormX, wormY, wormSize, wormSize, null);
                }
                
                // numero des pistes
                g2.setColor(new Color(255, 215, 0));
                g2.setFont(new Font("Arial", Font.BOLD, 20));
                g2.drawString((i + 1) + ".", trackStartX - 30, laneY + 45);
            }
        }
        
        // Bouton "LANCER LA COURSE"
        int betAreaStartY = panelY + OVERLAY_HEIGHT - 220;
        
        if (!raceStarted && getTotalBets() > 0) {
            int startBtnW = 250;
            int startBtnH = 40;
            int startBtnX = panelX + (OVERLAY_WIDTH - startBtnW) / 2;
            int startBtnY = betAreaStartY;
            
            startRaceButton = new Rectangle(startBtnX, startBtnY, startBtnW, startBtnH);
            
            g2.setColor(new Color(220, 20, 60));
            g2.fillRoundRect(startBtnX, startBtnY, startBtnW, startBtnH, 10, 10);
            
            g2.setColor(new Color(255, 215, 0));
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(startBtnX, startBtnY, startBtnW, startBtnH, 10, 10);
            
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            String startText = "LANCER LA COURSE";
            FontMetrics startFm = g2.getFontMetrics();
            int startTextW = startFm.stringWidth(startText);
            g2.drawString(startText, startBtnX + (startBtnW - startTextW) / 2, startBtnY + 26);
        }
        
        // Boutons de pari
        int betAreaY = betAreaStartY + 20;
        int wormIconSize = 50;
        int betSpacing = OVERLAY_WIDTH / 3;
        
        for (int i = 0; i < 3; i++) {
            int betX = panelX + betSpacing * i + betSpacing / 2 - wormIconSize / 2;

            // img du worm
            if (selectedWorms != null && selectedWorms[i] != null) {
                g2.drawImage(selectedWorms[i], betX, betAreaY, wormIconSize, wormIconSize, null);
            }
            
            // boutons parier
            int btnW = 150;
            int btnH = 35;
            int btnX = panelX + betSpacing * i + betSpacing / 2 - btnW / 2;
            int btnY = betAreaY + wormIconSize + 10;
            
            betButtons[i] = new Rectangle(btnX, btnY, btnW, btnH);
            
            if (!raceStarted) {
                g2.setColor(new Color(76, 175, 80));
            } else {
                g2.setColor(new Color(100, 100, 100));
            }
            g2.fillRoundRect(btnX, btnY, btnW, btnH, 8, 8);
            
            g2.setColor(Color.WHITE);
            g2.drawRoundRect(btnX, btnY, btnW, btnH, 8, 8);
            
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            String btnText = bets[i] > 0 ? "PARIER " + BET_AMOUNT + "$ (" + bets[i] + "$)" : "PARIER " + BET_AMOUNT + "$";
            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(btnText);
            g2.drawString(btnText, btnX + (btnW - textW) / 2, btnY + 22);
        }
        
        // message resultat de la course
        if (raceFinished) {
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            String resultText;
            if (bets[winnerIndex] > 0) {
                resultText = "GAGNÉ! Le worm " + (winnerIndex + 1) + " gagne! +" + (bets[winnerIndex] * 2) + "$";
            } else {
                resultText = "PERDU! Le worm " + (winnerIndex + 1) + " a gagné.";
            }
            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(resultText);
            int resultY = trackStartY + (3 * 70) + 30; // Après les 3 pistes
            g2.drawString(resultText, panelX + (OVERLAY_WIDTH - textW) / 2, resultY);
        }
        
        // footer
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.ITALIC, 12));
        String closeText = "cliquez en dehors pour fermer";
        FontMetrics fm = g2.getFontMetrics();
        int textW = fm.stringWidth(closeText);
        g2.drawString(closeText, panelX + (OVERLAY_WIDTH - textW) / 2, panelY + OVERLAY_HEIGHT - 10);
    }
}

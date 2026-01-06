package fr.upc.g5e4.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import fr.upc.g5e4.controller.PlayerDataController;
import fr.upc.g5e4.util.ImageCache;

/**
 * Overlay pour le mini-jeu WormGuess
 * Pour mieux comprendre la structure lire WormRaceOverlay.java car tout est detaille la-bas
 */
public class WormGuessOverlay extends JPanel {
    private boolean visible = false;
    private BufferedImage titleImage;
    private BufferedImage[] wormImages;
    private BufferedImage[] selectedWorms;
    
    private static final int OVERLAY_WIDTH = 800;
    private static final int OVERLAY_HEIGHT = 500;
    
    private boolean gameStarted = false;
    private boolean gameFinished = false;
    private int winnerIndex = -1;
    private boolean isAnimating = false;
    private Timer animationTimer;
    private int currentAnimFrame = 0;
    
    private int[] bets = new int[2];
    private Rectangle[] betButtons = new Rectangle[2];
    private Rectangle startGameButton;
    private static final int BET_AMOUNT = 1; 
    
    private PlayerDataController playerDataController;
    private fr.upc.g5e4.model.GameManager gameManager;
    
    public WormGuessOverlay() {
        setOpaque(false);
        setVisible(false);
        setEnabled(false);
        loadImages();
        
        playerDataController = new fr.upc.g5e4.controller.PlayerDataController();
        playerDataController.chargerJoueur();
        
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
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
                    stopGame();
                    visible = false;
                    setVisible(false);
                    setEnabled(false);
                    repaint();
                }
            }
        });
        
        // repaint au redimensionnement
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                if (visible && isVisible()) {
                    repaint();
                }
            }
        });
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
    
    public void showGame() {
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
        betButtons = new Rectangle[2];
        startGameButton = null;
        
        this.visible = true;
        setVisible(true);
        setEnabled(true);
        setOpaque(false);
        
        if (getParent() instanceof JLayeredPane) {
            JLayeredPane parent = (JLayeredPane) getParent();
            parent.moveToFront(this);
            parent.setLayer(this, JLayeredPane.DRAG_LAYER + 10);
        }
        
        repaint();
        if (getParent() != null) {
            getParent().repaint();
        }
    }
    
    /**
     * Place un pari sur le worm d'index donné
     * @param wormIndex index du worm/la piste du worm
     */
    private void placeBet(int wormIndex) {
        if (gameManager == null || gameManager.getCurrentTeam() == null) {
            System.err.println("Erreur: pas d'équipe active");
            return;
        }
        
        int currentMoney = gameManager.getCurrentTeam().getMoney();
        if (currentMoney < BET_AMOUNT) {
            System.out.println("Pas assez d'argent pour parier!");
            return;
        }
        
        bets[wormIndex] += BET_AMOUNT;
        gameManager.getCurrentTeam().removeMoney(BET_AMOUNT);
        
        repaint();
    }
    
    private int getTotalBets() {
        int total = 0;
        for (int bet : bets) {
            total += bet;
        }
        return total;
    }
    
    private void startGame() {
        gameStarted = true;
        isAnimating = true;
        
        // tirage du gagnant au hasard
        winnerIndex = (int) (Math.random() * 2);
        
        // animation des worms en noir
        final int totalFrames = 60;
        currentAnimFrame = 0;
        
        animationTimer = new Timer(50, e -> {
            currentAnimFrame++;
            repaint();
            
            if (currentAnimFrame >= totalFrames) {
                stopAnimation();
                gameFinished = true;
                gestionWin();
                repaint();
            }
        });
        animationTimer.start();
    }
    
    private void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
            animationTimer = null;
        }
        isAnimating = false;
    }
    
    private void stopGame() {
        stopAnimation();
        gameStarted = false;
        gameFinished = false;
    }

    /**
     * Gère les gains/pertes après la course
     */
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

    /**
     * Gère les clics sur les boutons de l'overlay
     * @param x position X du clic
     * @param y position Y du clic
     * @param panelX position X de l'overlay
     * @param panelY position Y de l'overlay
     */
    private void handleClick(int x, int y, int panelX, int panelY) {
        if (!gameStarted && !isAnimating && startGameButton != null && startGameButton.contains(x, y)) {
            if (getTotalBets() > 0) {
                startGame();
            }
            return;
        }
        
        if (!gameStarted && !isAnimating) {
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
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int w = getWidth();
        int h = getHeight();
        
        int panelX = (w - OVERLAY_WIDTH) / 2;
        int panelY = (h - OVERLAY_HEIGHT) / 2;

        // affichage de l'overlay
        g2.setColor(new Color(139, 0, 0));
        g2.fillRoundRect(panelX, panelY, OVERLAY_WIDTH, OVERLAY_HEIGHT, 20, 20);
        
        g2.setColor(new Color(255, 215, 0));
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(panelX, panelY, OVERLAY_WIDTH, OVERLAY_HEIGHT, 20, 20);
        
        g2.setColor(new Color(255, 215, 0));
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        int money = 0;
        if (gameManager != null && gameManager.getCurrentTeam() != null) {
            money = gameManager.getCurrentTeam().getMoney();
        }
        String moneyText = "Argent: " + money + "$";
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
        
        int centerY = panelY + 210;
        int centerSize = 130;
        int centerX = panelX + (OVERLAY_WIDTH - centerSize) / 2;
        
        if (!gameStarted && !isAnimating) {
            // le gros "?" au centre
            g2.setColor(new Color(255, 215, 0));
            g2.setFont(new Font("Arial", Font.BOLD, 100));
            String placeholder = "?";
            FontMetrics qFm = g2.getFontMetrics();
            int qW = qFm.stringWidth(placeholder);
            g2.drawString(placeholder, centerX + (centerSize - qW) / 2, centerY + 95);

        } else if (isAnimating) {
            // on joue l'animation des worms en noir
            int displayIndex = currentAnimFrame % 2; // calcul pour connaitre le worm a afficher
            if (selectedWorms[displayIndex] != null) {
                BufferedImage blackWorm = createBlackVersion(selectedWorms[displayIndex]);
                g2.drawImage(blackWorm, centerX, centerY, centerSize, centerSize, null);
            }
        } else if (gameFinished) {
            // on affiche le worm gagnant
            if (selectedWorms[winnerIndex] != null) {
                g2.drawImage(selectedWorms[winnerIndex], centerX, centerY, centerSize, centerSize, null);
            }
        }
        
        // message resultat du jeu
        if (gameFinished) {
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            String resultText;
            if (bets[winnerIndex] > 0) {
                resultText = "GAGNÉ! +" + (bets[winnerIndex] * 2) + "$";
            } else {
                resultText = "PERDU!";
            }
            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(resultText);
            g2.drawString(resultText, panelX + (OVERLAY_WIDTH - textW) / 2, centerY + centerSize + 25);
        }
        
        // boutons "LANCER"
        int betAreaStartY = panelY + OVERLAY_HEIGHT - 110;
        
        if (!gameStarted && !isAnimating && getTotalBets() > 0) {
            int startBtnW = 250;
            int startBtnH = 40;
            int startBtnX = panelX + (OVERLAY_WIDTH - startBtnW) / 2;
            int startBtnY = betAreaStartY;
            
            startGameButton = new Rectangle(startBtnX, startBtnY, startBtnW, startBtnH);
            
            g2.setColor(new Color(220, 20, 60));
            g2.fillRoundRect(startBtnX, startBtnY, startBtnW, startBtnH, 10, 10);
            
            g2.setColor(new Color(255, 215, 0));
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(startBtnX, startBtnY, startBtnW, startBtnH, 10, 10);
            
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            String startText = "LANCER";
            FontMetrics startFm = g2.getFontMetrics();
            int startTextW = startFm.stringWidth(startText);
            g2.drawString(startText, startBtnX + (startBtnW - startTextW) / 2, startBtnY + 26);
        }
        
        // zone pari
        int betAreaY = betAreaStartY + 0;
        int wormIconSize = 45; // Réduit
        int betSpacing = OVERLAY_WIDTH / 2;
        
        for (int i = 0; i < 2; i++) {
            int betX = panelX + betSpacing * i + betSpacing / 2 - wormIconSize / 2;
            
            // image du worm
            if (selectedWorms != null && selectedWorms[i] != null) {
                g2.drawImage(selectedWorms[i], betX, betAreaY, wormIconSize, wormIconSize, null);
            }
            
            // boutons pour parier
            if (!gameStarted && !isAnimating) {
                int btnW = 150;
                int btnH = 35;
                int btnX = panelX + betSpacing * i + betSpacing / 2 - btnW / 2;
                int btnY = betAreaY + wormIconSize + 10;
                
                betButtons[i] = new Rectangle(btnX, btnY, btnW, btnH);
                
                // check si l'équipe a assez d'argent
                int currentMoney = 0;
                if (gameManager != null && gameManager.getCurrentTeam() != null) {
                    currentMoney = gameManager.getCurrentTeam().getMoney();
                }
                boolean canAfford = currentMoney >= BET_AMOUNT;
                
                if (canAfford) {
                    g2.setColor(new Color(76, 175, 80));
                } else {
                    g2.setColor(new Color(100, 100, 100));
                }
                g2.fillRoundRect(btnX, btnY, btnW, btnH, 8, 8);
                
                g2.setColor(Color.WHITE);
                g2.drawRoundRect(btnX, btnY, btnW, btnH, 8, 8);
                
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                String btnText;
                if (!canAfford) {
                    btnText = "INSUFFISANT";
                } else {
                    btnText = bets[i] > 0 ? "PARIER " + BET_AMOUNT + "$ (" + bets[i] + "$)" : "PARIER " + BET_AMOUNT + "$";
                }
                FontMetrics fm = g2.getFontMetrics();
                int textW = fm.stringWidth(btnText);
                g2.drawString(btnText, btnX + (btnW - textW) / 2, btnY + 22);
            } else {
                // on affiche juste le montant parié "Mise: ..$"
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                String betText = "Mise: " + bets[i] + "$";
                FontMetrics fm = g2.getFontMetrics();
                int textW = fm.stringWidth(betText);
                g2.drawString(betText, panelX + betSpacing * i + betSpacing / 2 - textW / 2, betAreaY + wormIconSize + 35);
            }
        }
        
        // footer
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.ITALIC, 12));
        String closeText = "cliquez en dehors pour fermer";
        FontMetrics fm = g2.getFontMetrics();
        int textW = fm.stringWidth(closeText);
        g2.drawString(closeText, panelX + (OVERLAY_WIDTH - textW) / 2, panelY + OVERLAY_HEIGHT - 10);
    }
    
    /**
    * Methode qui permet de rendre une image totalement noire sauf la transaprence
    * On parcourt pixel par pixel en mettant la couleur noire
    * @param original L'image a rendre noire
    * @return L'image noire
    **/
    private BufferedImage createBlackVersion(BufferedImage original) {
        BufferedImage blackImg = new BufferedImage(
            original.getWidth(),
            original.getHeight(),
            BufferedImage.TYPE_INT_ARGB
        );
        
        Graphics2D g2 = blackImg.createGraphics();
        g2.drawImage(original, 0, 0, null);
        
        // filtre noir en préservant la transparence (alpha)
        for (int y = 0; y < blackImg.getHeight(); y++) {
            for (int x = 0; x < blackImg.getWidth(); x++) {
                int rgba = blackImg.getRGB(x, y);
                int alpha = (rgba >> 24) & 0xff;
                if (alpha > 0) {
                    int black = (alpha << 24) | 0x000000;
                    blackImg.setRGB(x, y, black);
                }
            }
        }
        
        g2.dispose();
        return blackImg;
    }
    
    public void setGameManager(fr.upc.g5e4.model.GameManager gameManager) {
        this.gameManager = gameManager;
    }
}

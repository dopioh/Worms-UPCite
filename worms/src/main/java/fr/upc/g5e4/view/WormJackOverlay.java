package fr.upc.g5e4.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import fr.upc.g5e4.util.ImageCache;

/**
 * Overlay pour le mini-jeu WormJack
 * Pour mieux comprendre la structure lire WormRaceOverlay.java car tout est detaille la-bas
 */
public class WormJackOverlay extends JPanel {
    private boolean visible = false;
    private BufferedImage titleImage;
    
    private static final int OVERLAY_WIDTH = 800;
    private static final int OVERLAY_HEIGHT = 500;
    
    private boolean gameStarted = false;
    private boolean gameFinished = false;
    private boolean playerTurn = true; // false = wormbot
    
    // systeme de cartes
    private List<Card> deck; // le set de cartes
    private List<Card> playerCards;
    private List<Card> wormbotCards;
    private int playerScore = 0;
    private int wormbotScore = 0;
    private int playerAceCount = 0;
    private int wormbotAceCount = 0;
    
    private Rectangle hitButton;
    private Rectangle standButton;
    private Rectangle startButton;
    private Rectangle betButton;
    
    private static final int BET_AMOUNT = 5; // pari 5$ par 5$
    private int currentBet = 0;
    
    private fr.upc.g5e4.controller.PlayerDataController playerDataController;
    private fr.upc.g5e4.model.GameManager gameManager;
    
    // Classe interne : chaque carte est une instance de la classe Card
    private static class Card {
        String value; // de 1 a 10
        String color;  // "C", "D", "H", "P"
        BufferedImage image;
        
        /**
         * Constructeur de la carte
         * @param value valeur de la carte
         * @param color couleur de la carte
         * @param image image sprite de la carte
         */
        Card(String value, String color, BufferedImage image) {
            this.value = value;
            this.color = color;
            this.image = image;
        }
        
        /**
         * Retourne la valeur numérique de la carte d'après les règles du jeu
         * @return valeur numérique
         */
        int getNumericValue() {
            int val = Integer.parseInt(value);
            if (val >= 11 && val <= 13) return 10; // J, Q, K = 10
            if (val == 1) return 11; // As = 11 ou 1 si on depasse de 21
            return val;
        }
    }
    
    public WormJackOverlay() {
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
                    visible = false;
                    setVisible(false);
                    setEnabled(false);
                    repaint();
                }
            }
        });
        
        // Repaindre lors du redimensionnement
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
    
    public void showGame() {
        // reset le jeu avant de l'afficher
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
    
    private void placeBet() {
        if (gameManager == null || gameManager.getCurrentTeam() == null) {
            System.err.println("Erreur: pas d'équipe active");
            return;
        }
        
        int currentMoney = gameManager.getCurrentTeam().getMoney();
        if (currentMoney < BET_AMOUNT) {
            System.out.println("Pas assez d'argent pour parier!");
            return;
        }
        
        currentBet += BET_AMOUNT;
        gameManager.getCurrentTeam().removeMoney(BET_AMOUNT);
        
        repaint();
    }
    
    private void startGame() {
        gameStarted = true;
        playerTurn = true;
        
        // distrib des premieres cartes
        hit(); // joueur tire 1e carte
        wormbotHit(); // wormbot tire 1e carte
        
        repaint();
    }
    
    private void hit() {
        if (deck.isEmpty()) return;
        
        Card newCard = deck.remove(0); // on prend la carte et on l'enleve du deck
        playerCards.add(newCard);
        int cardValue = newCard.getNumericValue();
        playerScore += cardValue;
        
        // cas c'est un As
        if (cardValue == 11) {
            playerAceCount++;
        }

        // cas As si on depasse 21
        while (playerScore > 21 && playerAceCount > 0) {
            playerScore -= 10;
            playerAceCount--;
        }
        
        // Vérifier si bust
        if (playerScore > 21) {
            gameFinished = true;
            gestionGameEnd();
        }
        
        repaint();
    }
    
    private void wormbotHit() {
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
        
        repaint();
    }
    
    /**
     * logique de jeu du wormbot (arrete de tirer a 17+)
     * c'est au wormbot de jouer car l'utilisateur reste
     */
    private void stand() {
        playerTurn = false;
        
        // le wormbot tire jusqu'à 17+ avec un petit delay entre chaque tirage
        Timer wormbotTimer = new Timer(1000, null);
        wormbotTimer.addActionListener(e -> {
            if (wormbotScore < 17) {
                wormbotHit();
            } else {
                wormbotTimer.stop();
                gameFinished = true;
                gestionGameEnd();
                repaint();
            }
        });
        wormbotTimer.start();
        
        repaint();
    }
    
    /**
     * Gère les gains/pertes après la fin du jeu
     */
    private void gestionGameEnd() {
        int gains = 0;
        
        if (playerScore > 21) { // le joueur depasse 21, il burst
            System.out.println("Bust! Vous avez perdu.");

        } else if (wormbotScore > 21) {
            gains = currentBet * 2;
            System.out.println("Le wormbot a bust! Vous gagnez " + gains + "$");

        } else if (playerScore > wormbotScore) {
            gains = currentBet * 2;
            System.out.println("Vous gagnez! +" + gains + "$");

        } else if (playerScore == wormbotScore) {
            // mise remboursée
            gains = currentBet;
            System.out.println("Égalité! Mise remboursée.");

        } else {
            // wormbot gagne
            System.out.println("Le wormbot gagne. Vous avez perdu.");
        }
        
        if (gains > 0) {
            if (gameManager != null && gameManager.getCurrentTeam() != null) {
                gameManager.getCurrentTeam().addMoney(gains);
            }
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
        // Bouton PARIER
        if (!gameStarted && betButton != null && betButton.contains(x, y)) {
            placeBet();
            return;
        }
        
        // Bouton COMMENCER
        if (!gameStarted && currentBet > 0 && startButton != null && startButton.contains(x, y)) {
            startGame();
            return;
        }
        
        // Bouton HIT
        if (gameStarted && !gameFinished && playerTurn && hitButton != null && hitButton.contains(x, y)) {
            hit();
            return;
        }
        
        // Bouton STAND
        if (gameStarted && !gameFinished && playerTurn && standButton != null && standButton.contains(x, y)) {
            stand();
            return;
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
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        int money = 0;
        if (gameManager != null && gameManager.getCurrentTeam() != null) {
            money = gameManager.getCurrentTeam().getMoney();
        }
        String moneyText = "Argent: " + money + "$";
        FontMetrics moneyFm = g2.getFontMetrics();
        int moneyTextW = moneyFm.stringWidth(moneyText);
        g2.drawString(moneyText, panelX + OVERLAY_WIDTH - moneyTextW - 20, panelY + 30);
        
        if (titleImage != null) {
            int titleWidth = 200;
            int titleHeight = (int) (titleImage.getHeight() * ((double) titleWidth / titleImage.getWidth()));
            int titleX = panelX + 20;
            int titleY = panelY + 15;
            g2.drawImage(titleImage, titleX, titleY, titleWidth, titleHeight, null);
        }
        
        // Zone de jeu du wormbot
        int wormbotY = panelY + 100;
        drawPlayerArea(g2, panelX + OVERLAY_WIDTH / 2, wormbotY, "WORMBOT", wormbotCards, wormbotScore, false);
        
        // Zone de jeu du joueur
        int playerY = panelY + 280;
        drawPlayerArea(g2, panelX + OVERLAY_WIDTH / 2, playerY, "VOUS", playerCards, playerScore, true);
        
        // boutons
        int btnY = panelY + OVERLAY_HEIGHT - 60;
        
        if (!gameStarted) {
            // bouton "PARIER"
            int betBtnW = 150;
            int betBtnH = 40;
            int betBtnX = panelX + 50;
            
            betButton = new Rectangle(betBtnX, btnY, betBtnW, betBtnH);
            
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
            g2.fillRoundRect(betBtnX, btnY, betBtnW, betBtnH, 10, 10);
            g2.setColor(new Color(255, 215, 0));
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(betBtnX, btnY, betBtnW, betBtnH, 10, 10);
            
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            String betText;
            if (!canAfford) {
                betText = "INSUFFISANT";
            } else {
                betText = currentBet > 0 ? "PARIER 5$ (" + currentBet + "$)" : "PARIER 5$";
            }
            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(betText);
            g2.drawString(betText, betBtnX + (betBtnW - textW) / 2, btnY + 26);
            
            // bouton "COMMENCER"
            if (currentBet > 0) {
                int startBtnW = 200;
                int startBtnH = 40;
                int startBtnX = panelX + (OVERLAY_WIDTH - startBtnW) / 2;
                
                startButton = new Rectangle(startBtnX, btnY, startBtnW, startBtnH);
                
                g2.setColor(new Color(220, 20, 60));
                g2.fillRoundRect(startBtnX, btnY, startBtnW, startBtnH, 10, 10);
                g2.setColor(new Color(255, 215, 0));
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(startBtnX, btnY, startBtnW, startBtnH, 10, 10);
                
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 16));
                String startText = "COMMENCER";
                FontMetrics startFm = g2.getFontMetrics();
                int startTextW = startFm.stringWidth(startText);
                g2.drawString(startText, startBtnX + (startBtnW - startTextW) / 2, btnY + 26);
            }
        } else if (!gameFinished && playerTurn) {
            // bouton "TIRER"
            int hitBtnW = 150;
            int hitBtnH = 40;
            int hitBtnX = panelX + (OVERLAY_WIDTH / 2) - hitBtnW - 10;
            
            hitButton = new Rectangle(hitBtnX, btnY, hitBtnW, hitBtnH);
            
            g2.setColor(new Color(76, 175, 80));
            g2.fillRoundRect(hitBtnX, btnY, hitBtnW, hitBtnH, 10, 10);
            g2.setColor(new Color(255, 215, 0));
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(hitBtnX, btnY, hitBtnW, hitBtnH, 10, 10);
            
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            String hitText = "TIRER";
            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(hitText);
            g2.drawString(hitText, hitBtnX + (hitBtnW - textW) / 2, btnY + 26);
            
            // bouton "RESTER"
            int standBtnW = 150;
            int standBtnH = 40;
            int standBtnX = panelX + (OVERLAY_WIDTH / 2) + 10;
            
            standButton = new Rectangle(standBtnX, btnY, standBtnW, standBtnH);
            
            g2.setColor(new Color(255, 215, 0));
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(standBtnX, btnY, standBtnW, standBtnH, 10, 10);
            
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            String standText = "RESTER";
            FontMetrics standFm = g2.getFontMetrics();
            int standTextW = standFm.stringWidth(standText);
            g2.drawString(standText, standBtnX + (standBtnW - standTextW) / 2, btnY + 26);
        }
        
        // message de résultat
        if (gameFinished) {
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            String resultText;
            
            if (playerScore > 21) {
                resultText = "BUST! Vous avez perdu.";
            } else if (wormbotScore > 21) {
                resultText = "Le wormbot a BUST! Vous gagnez +" + (currentBet * 2) + "$";
            } else if (playerScore > wormbotScore) {
                resultText = "GAGNÉ! +" + (currentBet * 2) + "$";
            } else if (playerScore == wormbotScore) {
                resultText = "ÉGALITÉ! Mise remboursée +" + currentBet + "$";
            } else {
                resultText = "PERDU! Le wormbot gagne.";
            }
            
            FontMetrics resultFm = g2.getFontMetrics();
            int resultW = resultFm.stringWidth(resultText);
            g2.drawString(resultText, panelX + (OVERLAY_WIDTH - resultW) / 2, panelY + OVERLAY_HEIGHT - 35);
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
     * Dessine la zone de jeu d'un joueur (wormbot ou joueur)
     * on chauqe fois que le player pioche on redessine la zone completement
     * @param g2 le contexte graphique
     * @param centerX le centre X de la zone
     * @param y le coin Y de la zone
     * @param label le label ("WORMBOT" ou "VOUS")
     * @param cards la liste des cartes du joueur (nous ou wormbot)
     * @param score le score actuel de la personne qui joue
     * @param isPlayer true si c'est le joueur / false si c'est le wormbot
     */
    private void drawPlayerArea(Graphics2D g2, int centerX, int y, String label, List<Card> cards, int score, boolean isPlayer) {
        // nom joueur
        g2.setColor(new Color(255, 215, 0));
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString(label, centerX - 150, y + 20);
        
        // compteur carte
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 36));
        String scoreText = String.valueOf(score);
        g2.drawString(scoreText, centerX - 150, y + 80);
        
        // cartes jouées qu'on empile
        int cardW = 80;
        int cardH = 112;
        int cardEspace = 30;
        
        if (cards != null && !cards.isEmpty()) {
            int totalWidth = (cards.size() - 1) * cardEspace + cardW;
            int startX = centerX - totalWidth / 2;
            
            for (int i = 0; i < cards.size(); i++) {
                Card card = cards.get(i);
                if (card != null && card.image != null) {
                    int cardX = startX + i * cardEspace;
                    int cardY = y + 10;
                    g2.drawImage(card.image, cardX, cardY, cardW, cardH, null);
                }
            }
        }
        
        // indicateur tour
        if (gameStarted && !gameFinished) {
            if ((isPlayer && playerTurn) || (!isPlayer && !playerTurn)) {
                g2.setColor(new Color(255, 215, 0));
                g2.fillOval(centerX - 180, y + 50, 15, 15);
            }
        }
    }
    
    public void setGameManager(fr.upc.g5e4.model.GameManager gameManager) {
        this.gameManager = gameManager;
    }
}

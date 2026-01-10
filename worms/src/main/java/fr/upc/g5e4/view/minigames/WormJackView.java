package fr.upc.g5e4.view.minigames;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import fr.upc.g5e4.model.minigames.WormJackModel;

/**
 * View pour le mini-jeu WormJack
 * @author Nassim
 */
public class WormJackView extends JPanel {
    
    private WormJackModel model;
    private boolean visible = false;
    
    public static final int OVERLAY_WIDTH = 800;
    public static final int OVERLAY_HEIGHT = 500;
    
    private Rectangle hitButton;
    private Rectangle standButton;
    private Rectangle startButton;
    private Rectangle betButton;
    
    public WormJackView(WormJackModel model) {
        this.model = model;
        setOpaque(false);
        setVisible(false);
        setEnabled(false);
        model.setView(this);
    }
    
    public void showOverlay() {
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
    
    public void hideOverlay() {
        visible = false;
        setVisible(false);
        setEnabled(false);
        repaint();
    }
    
    public boolean isOverlayVisible() {
        return visible;
    }
    
    public Rectangle getHitButton() { return hitButton; }
    public Rectangle getStandButton() { return standButton; }
    public Rectangle getStartButton() { return startButton; }
    public Rectangle getBetButton() { return betButton; }
    
    public Rectangle getOverlayBounds() {
        int w = getWidth();
        int h = getHeight();
        int panelX = (w - OVERLAY_WIDTH) / 2;
        int panelY = (h - OVERLAY_HEIGHT) / 2;
        return new Rectangle(panelX, panelY, OVERLAY_WIDTH, OVERLAY_HEIGHT);
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
        
        g2.setColor(new Color(139, 0, 0));
        g2.fillRoundRect(panelX, panelY, OVERLAY_WIDTH, OVERLAY_HEIGHT, 20, 20);
        
        g2.setColor(new Color(255, 215, 0));
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(panelX, panelY, OVERLAY_WIDTH, OVERLAY_HEIGHT, 20, 20);
        
        g2.setColor(new Color(255, 215, 0));
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        String moneyText = "Argent: " + model.getCurrentMoney() + "$";
        FontMetrics moneyFm = g2.getFontMetrics();
        int moneyTextW = moneyFm.stringWidth(moneyText);
        g2.drawString(moneyText, panelX + OVERLAY_WIDTH - moneyTextW - 20, panelY + 30);
        
        BufferedImage titleImage = model.getTitleImage();
        if (titleImage != null) {
            int titleWidth = 200;
            int titleHeight = (int) (titleImage.getHeight() * ((double) titleWidth / titleImage.getWidth()));
            int titleX = panelX + 20;
            int titleY = panelY + 15;
            g2.drawImage(titleImage, titleX, titleY, titleWidth, titleHeight, null);
        }
        
        // zone de jeu de wormbot (zone des cartes)
        int wormbotY = panelY + 100;
        drawPlayerArea(g2, panelX + OVERLAY_WIDTH / 2, wormbotY, "WORMBOT", model.getWormbotCards(), model.getWormbotScore(), false);
        
        // zone de jeu du joueur
        int playerY = panelY + 280;
        drawPlayerArea(g2, panelX + OVERLAY_WIDTH / 2, playerY, "VOUS", model.getPlayerCards(), model.getPlayerScore(), true);
        
        int btnY = panelY + OVERLAY_HEIGHT - 60;
        
        if (!model.isGameStarted()) {
            int betBtnW = 150;
            int betBtnH = 40;
            int betBtnX = panelX + 50;
            // Bouton pour parier
            betButton = new Rectangle(betBtnX, btnY, betBtnW, betBtnH);
            
            int currentMoney = model.getCurrentMoney();
            boolean canAfford = currentMoney >= WormJackModel.BET_AMOUNT;
            
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
                int currentBet = model.getCurrentBet();
                betText = currentBet > 0 ? "PARIER 5$ (" + currentBet + "$)" : "PARIER 5$";
            }
            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(betText);
            g2.drawString(betText, betBtnX + (betBtnW - textW) / 2, btnY + 26);
            
            // Bouton "COMMENCER"
            if (model.getCurrentBet() > 0) {
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
            } else {
                startButton = null;
            }
            
            hitButton = null;
            standButton = null;
        } else if (!model.isGameFinished() && model.isPlayerTurn()) {
            // Bouton "TIRER"
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
            
            // Bouton "RESTER"
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
            
            betButton = null;
            startButton = null;
        } else {
            hitButton = null;
            standButton = null;
            betButton = null;
            startButton = null;
        }
        
        // message de résultat
        if (model.isGameFinished()) {
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            String resultText;
            int currentBet = model.getCurrentBet();
            int playerScore = model.getPlayerScore();
            int wormbotScore = model.getWormbotScore();
            
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
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.ITALIC, 12));
        String closeText = "cliquez en dehors pour fermer";
        FontMetrics fm = g2.getFontMetrics();
        int textW = fm.stringWidth(closeText);
        g2.drawString(closeText, panelX + (OVERLAY_WIDTH - textW) / 2, panelY + OVERLAY_HEIGHT - 10);
    }
    
    /**
     * Dessiner la zone de jeu d'un joueur
     * (dessine le nom, le score et les cartes jouées + un indicateur de tour)
     * @param g2 le Graphics2D
     * @param centerX le centre X de la zone
     * @param y la position Y de la zone
     * @param name le nom du joueur
     * @param cards les cartes jouées
     * @param score le score actuel
     * @param isPlayer true si c'est la zone du joueur, false pour wormbot
     */
    private void drawPlayerArea(Graphics2D g2, int centerX, int y, String name, List<WormJackModel.Card> cards, int score, boolean isPlayer) {
        // nom du joueur
        g2.setColor(new Color(255, 215, 0));
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString(name, centerX - 150, y + 20);
        
        // cpt de score
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 36));
        String scoreText = String.valueOf(score);
        g2.drawString(scoreText, centerX - 150, y + 80);
        
        // pile des cartes jouées
        int cardW = 80;
        int cardH = 112;
        int cardEspace = 30;
        
        if (cards != null && !cards.isEmpty()) {
            int totalWidth = (cards.size() - 1) * cardEspace + cardW;
            int startX = centerX - totalWidth / 2;
            
            for (int i = 0; i < cards.size(); i++) {
                WormJackModel.Card card = cards.get(i);
                if (card != null && card.getImage() != null) {
                    int cardX = startX + i * cardEspace;
                    int cardY = y + 10;
                    g2.drawImage(card.getImage(), cardX, cardY, cardW, cardH, null);
                }
            }
        }
        
        // idnicateur de tour (petit rond a coté du nom)
        if (model.isGameStarted() && !model.isGameFinished()) {
            if ((isPlayer && model.isPlayerTurn()) || (!isPlayer && !model.isPlayerTurn())) {
                g2.setColor(new Color(255, 215, 0));
                g2.fillOval(centerX - 180, y + 50, 15, 15);
            }
        }
    }
}

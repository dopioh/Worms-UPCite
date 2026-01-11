package fr.upc.g5e4.view.minigames;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import fr.upc.g5e4.model.minigames.WormGuessModel;

/**
 * View pour le mini-jeu WormGuess
 * @author Nassim
 */
public class WormGuessView extends JPanel {
    
    private WormGuessModel model;
    private boolean visible = false;
    
    public static final int OVERLAY_WIDTH = 800;
    public static final int OVERLAY_HEIGHT = 500;
    
    private Rectangle[] betButtons = new Rectangle[2];
    private Rectangle startGameButton;
    
    public WormGuessView(WormGuessModel model) {
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
    
    public Rectangle[] getBetButtons() {
        return betButtons;
    }
    
    public Rectangle getStartGameButton() {
        return startGameButton;
    }

    // Retourne un rectangle de dimension : le mini jeu centré
    // on en a besoin pour le controller (ex : clic en dehors de l'overlay)
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
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        String moneyText = "Argent: " + model.getCurrentMoney() + "$";
        FontMetrics moneyFm = g2.getFontMetrics();
        int moneyTextW = moneyFm.stringWidth(moneyText);
        g2.drawString(moneyText, panelX + OVERLAY_WIDTH - moneyTextW - 20, panelY + 30);
        
        BufferedImage titleImage = model.getTitleImage();
        if (titleImage != null) {
            int titleWidth = 250;
            int titleHeight = (int) (titleImage.getHeight() * ((double) titleWidth / titleImage.getWidth()));
            int titleX = panelX + (OVERLAY_WIDTH - titleWidth) / 2;
            int titleY = panelY + 20;
            g2.drawImage(titleImage, titleX, titleY, titleWidth, titleHeight, null);
        }
        
        // affichage des elements du jeu
        int centerY = panelY + 210;
        int centerSize = 130;
        int centerX = panelX + (OVERLAY_WIDTH - centerSize) / 2;
        
        BufferedImage[] selectedWorms = model.getSelectedWorms();
        
        if (!model.isGameStarted() && !model.isAnimating()) {
            g2.setColor(new Color(255, 215, 0));
            g2.setFont(new Font("Arial", Font.BOLD, 100));
            String placeholder = "?";
            FontMetrics qFm = g2.getFontMetrics();
            int qW = qFm.stringWidth(placeholder);
            g2.drawString(placeholder, centerX + (centerSize - qW) / 2, centerY + 95);
        } else if (model.isAnimating()) {
            int displayIndex = model.getCurrentAnimFrame() % 2;
            if (selectedWorms != null && selectedWorms[displayIndex] != null) {
                BufferedImage blackWorm = createBlackVersion(selectedWorms[displayIndex]);
                g2.drawImage(blackWorm, centerX, centerY, centerSize, centerSize, null);
            }
        } else if (model.isGameFinished()) {
            int winnerIndex = model.getWinnerIndex();
            if (selectedWorms != null && selectedWorms[winnerIndex] != null) {
                g2.drawImage(selectedWorms[winnerIndex], centerX, centerY, centerSize, centerSize, null);
            }
        }
        
        // Bouton "LANCER"
        int betAreaStartY = panelY + OVERLAY_HEIGHT - 110;
        
        if (!model.isGameStarted() && !model.isAnimating() && model.getTotalBets() > 0) {
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
        } else {
            startGameButton = null;
        }
        
        // zone de pari
        int betAreaY = betAreaStartY;
        int wormIconSize = 45;
        int betSpacing = OVERLAY_WIDTH / 2;
        int currentMoney = model.getCurrentMoney();
        boolean canAfford = currentMoney >= WormGuessModel.BET_AMOUNT;
        int[] bets = model.getBets();
        for (int i = 0; i < 2; i++) {
            int betX = panelX + betSpacing * i + betSpacing / 2 - wormIconSize / 2;
            // worm au dessus de son bouton de pari
            if (selectedWorms != null && selectedWorms[i] != null) {
                g2.drawImage(selectedWorms[i], betX, betAreaY, wormIconSize, wormIconSize, null);
            }
            
            // Boutons pari
            if (!model.isGameStarted() && !model.isAnimating()) {
                int btnW = 150;
                int btnH = 35;
                int btnX = panelX + betSpacing * i + betSpacing / 2 - btnW / 2;
                int btnY = betAreaY + wormIconSize + 10;
                
                betButtons[i] = new Rectangle(btnX, btnY, btnW, btnH);
                
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
                    btnText = bets[i] > 0 ? "PARIER " + WormGuessModel.BET_AMOUNT + "$ (" + bets[i] + "$)" : "PARIER " + WormGuessModel.BET_AMOUNT + "$";
                }
                FontMetrics fm = g2.getFontMetrics();
                int textW = fm.stringWidth(btnText);
                g2.drawString(btnText, btnX + (btnW - textW) / 2, btnY + 22);
            } else {
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                String betText = "Mise: " + bets[i] + "$";
                FontMetrics fm = g2.getFontMetrics();
                int textW = fm.stringWidth(betText);
                g2.drawString(betText, panelX + betSpacing * i + betSpacing / 2 - textW / 2, betAreaY + wormIconSize + 35);
            }
        }

        // message résultat du jeu
        if (model.isGameFinished()) {
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            String resultText;
            int winnerIndex = model.getWinnerIndex();
            if (bets[winnerIndex] > 0) {
                resultText = "GAGNÉ! +" + (bets[winnerIndex] * 2) + "$";
            } else {
                resultText = "PERDU!";
            }
            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(resultText);
            g2.drawString(resultText, panelX + (OVERLAY_WIDTH - textW) / 2, centerY + centerSize + 25);
        }
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.ITALIC, 12));
        String closeText = "cliquez en dehors pour fermer";
        FontMetrics fm = g2.getFontMetrics();
        int textW = fm.stringWidth(closeText);
        g2.drawString(closeText, panelX + (OVERLAY_WIDTH - textW) / 2, panelY + OVERLAY_HEIGHT - 10);
    }
    
    /**
     * Crée une version noire du worm
     */
    private BufferedImage createBlackVersion(BufferedImage original) {
        BufferedImage blackImg = new BufferedImage(
            original.getWidth(),
            original.getHeight(),
            BufferedImage.TYPE_INT_ARGB
        );
        
        Graphics2D g2 = blackImg.createGraphics();
        g2.drawImage(original, 0, 0, null);
        
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
}

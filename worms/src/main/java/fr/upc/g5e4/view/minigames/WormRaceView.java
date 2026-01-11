package fr.upc.g5e4.view.minigames;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import fr.upc.g5e4.model.minigames.WormRaceModel;

/**
 * View pour le mini-jeu WormRace
 * @author Nassim
 */
public class WormRaceView extends JPanel {
    
    private WormRaceModel model;
    private boolean visible = false;
    
    public static final int OVERLAY_WIDTH = 800;
    public static final int OVERLAY_HEIGHT = 600;
    
    private Rectangle[] betButtons = new Rectangle[3];
    private Rectangle startRaceButton;
    
    /**
     * Constructeur (au départ l'overlay est invisible)
     * @param model le model du mini-jeu
     */
    public WormRaceView(WormRaceModel model) {
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
    
    public Rectangle getStartRaceButton() {
        return startRaceButton;
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
        
        // affichage des pistes de course
        int trackStartX = panelX + 50;
        int trackWidth = OVERLAY_WIDTH - 100;
        int trackStartY = panelY + 160;
        int laneHeight = 70;
        
        BufferedImage[] selectedWorms = model.getSelectedWorms();
        float[] wormPositions = model.getWormPositions();
        
        if (selectedWorms != null) {
            for (int i = 0; i < selectedWorms.length; i++) {
                int laneY = trackStartY + i * laneHeight;
                
                // ligne de piste
                g2.setColor(new Color(100, 50, 0));
                g2.fillRect(trackStartX, laneY + 25, trackWidth, 30);
                
                // ligne depart / arrivee
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(trackStartX, laneY + 20, trackStartX, laneY + 60);
                g2.drawLine(trackStartX + trackWidth, laneY + 20, trackStartX + trackWidth, laneY + 60);
                
                if (selectedWorms[i] != null) {
                    int wormSize = 50;
                    int wormX = trackStartX + (int)(wormPositions[i] * trackWidth) - wormSize / 2;
                    int wormY = laneY + 15;
                    g2.drawImage(selectedWorms[i], wormX, wormY, wormSize, wormSize, null);
                }

                // numéro de la piste
                g2.setColor(new Color(255, 215, 0));
                g2.setFont(new Font("Arial", Font.BOLD, 20));
                g2.drawString((i + 1) + ".", trackStartX - 30, laneY + 45);
            }
        }
        
        // Bouton "LANCER LA COURSE"
        int betAreaStartY = panelY + OVERLAY_HEIGHT - 220;
        
        if (!model.isRaceStarted() && model.getTotalBets() > 0) {
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
        } else {
            startRaceButton = null;
        }
        
        // zone de pari
        int betAreaY = betAreaStartY + 20;
        int wormIconSize = 50;
        int betSpacing = OVERLAY_WIDTH / 3;
        int[] bets = model.getBets();
        int currentMoney = model.getCurrentMoney();
        boolean canAfford = currentMoney >= WormRaceModel.BET_AMOUNT;
        
        for (int i = 0; i < 3; i++) {
            // worm au dessus de son bouton de pari
            int betX = panelX + betSpacing * i + betSpacing / 2 - wormIconSize / 2;
            if (selectedWorms != null && selectedWorms[i] != null) {
                g2.drawImage(selectedWorms[i], betX, betAreaY, wormIconSize, wormIconSize, null);
            }
            
            // Boutons pari
            int btnW = 150;
            int btnH = 35;
            int btnX = panelX + betSpacing * i + betSpacing / 2 - btnW / 2;
            int btnY = betAreaY + wormIconSize + 10;
            
            betButtons[i] = new Rectangle(btnX, btnY, btnW, btnH);
            
            if (!model.isRaceStarted() && canAfford) {
                g2.setColor(new Color(76, 175, 80));
            } else {
                g2.setColor(new Color(100, 100, 100)); // gris si on ne peut pas parier
            }
            g2.fillRoundRect(btnX, btnY, btnW, btnH, 8, 8);
            
            g2.setColor(Color.WHITE);
            g2.drawRoundRect(btnX, btnY, btnW, btnH, 8, 8);
            
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            String btnText;
            if (!model.isRaceStarted() && !canAfford) {
                btnText = "INSUFFISANT";
            } else {
                btnText = bets[i] > 0 ? "PARIER " + WormRaceModel.BET_AMOUNT + "$ (" + bets[i] + "$)" : "PARIER " + WormRaceModel.BET_AMOUNT + "$";
            }
            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(btnText);
            g2.drawString(btnText, btnX + (btnW - textW) / 2, btnY + 22);
        }
        
        // message résultat de la course
        if (model.isRaceFinished()) {
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            String resultText;
            int winnerIndex = model.getWinnerIndex();
            if (bets[winnerIndex] > 0) {
                resultText = "GAGNÉ! Le worm " + (winnerIndex + 1) + " gagne! +" + (bets[winnerIndex] * 2) + "$";
            } else {
                resultText = "PERDU! Le worm " + (winnerIndex + 1) + " a gagné.";
            }
            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(resultText);
            int resultY = trackStartY + (3 * 70) + 30;
            g2.drawString(resultText, panelX + (OVERLAY_WIDTH - textW) / 2, resultY);
        }
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.ITALIC, 12));
        String closeText = "cliquez en dehors pour fermer";
        FontMetrics fm = g2.getFontMetrics();
        int textW = fm.stringWidth(closeText);
        g2.drawString(closeText, panelX + (OVERLAY_WIDTH - textW) / 2, panelY + OVERLAY_HEIGHT - 20);
    }
}

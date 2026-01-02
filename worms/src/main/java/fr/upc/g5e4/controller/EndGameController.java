package fr.upc.g5e4.controller;

import fr.upc.g5e4.GameLauncher;
import fr.upc.g5e4.model.GameManager;
import fr.upc.g5e4.model.GameStats;
import fr.upc.g5e4.model.TeamModel;
import fr.upc.g5e4.view.EndGameView;
import fr.upc.g5e4.view.TeamView;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Contrôleur pour gérer l'écran de fin de partie
 */
public class EndGameController {
    private GameLauncher gameLauncher;
    private EndGameView endGameView;
    private Font googleSansFont;
    
    public EndGameController(GameLauncher gameLauncher) {
        this.gameLauncher = gameLauncher;
        loadFonts();
    }
    
    private void loadFonts() {
        try {
            Font baseFont = Font.createFont(Font.TRUETYPE_FONT,
                getClass().getResourceAsStream("/GoogleSans-Bold.ttf"));
            googleSansFont = baseFont.deriveFont(20f);
        } catch (Exception e) {
            System.err.println("Erreur chargement police: " + e.getMessage());
            googleSansFont = new Font("Arial", Font.BOLD, 20);
        }
    }
    
    /**
     * Affiche l'écran de fin de partie avec animation
     * @param winningTeam L'équipe gagnante
     * @param winningTeamView La vue de l'équipe gagnante
     * @param winningTeamName Le nom de l'équipe gagnante
     * @param allTeamStats Statistiques de toutes les équipes
     */
    public void showEndGameScreen(TeamModel winningTeam, TeamView winningTeamView, 
                                   String winningTeamName, java.util.Map<String, GameStats> allTeamStats) {
        // Capturer l'état actuel du jeu comme image de fond
        BufferedImage backgroundSnapshot = captureGameBackground();
        
        // Créer la vue de fin de partie
        endGameView = new EndGameView(winningTeam, winningTeamView, winningTeamName, 
                                      allTeamStats, backgroundSnapshot);
        
        // Remplacer le panel actuel par la vue de fin de jeu (via CardLayout)
        SwingUtilities.invokeLater(() -> {
            // Utiliser le CardLayout existant au lieu de remplacer le contentPane
            JPanel cardPanel = gameLauncher.getCardPanel();
            cardPanel.add(endGameView, "END_GAME");
            CardLayout cl = (CardLayout) cardPanel.getLayout();
            cl.show(cardPanel, "END_GAME");
            
            // Ajouter un bouton pour retourner au menu après l'animation
            Timer delayTimer = new Timer(6000, e -> {  // 6 secondes
                addReturnToMenuButton();
                ((Timer)e.getSource()).stop();
            });
            delayTimer.setRepeats(false);
            delayTimer.start();
        });
    }
    
    /**
     * Capture l'état actuel du jeu comme image
     */
    private BufferedImage captureGameBackground() {
        try {
            GameManager gm = gameLauncher.getGameManager();
            if (gm != null && gm.getGamePanel() != null) {
                JPanel gamePanel = gm.getGamePanel();
                BufferedImage image = new BufferedImage(
                    gamePanel.getWidth(), 
                    gamePanel.getHeight(), 
                    BufferedImage.TYPE_INT_ARGB
                );
                Graphics2D g2d = image.createGraphics();
                gamePanel.paint(g2d);
                g2d.dispose();
                return image;
            }
        } catch (Exception e) {
            System.err.println("Erreur capture background: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Ajoute le bouton pour retourner au menu principal
     */
    private void addReturnToMenuButton() {
        if (endGameView != null) {
            // Calculer la position centrée du bouton en fonction de la taille de l'écran
            int baseWidth = 1280;
            int baseHeight = 720;
            float scale = Math.min((float)endGameView.getWidth() / baseWidth, (float)endGameView.getHeight() / baseHeight);
            
            // Calculer les offsets correctement
            int scaledWidth = (int)(baseWidth * scale);
            int scaledHeight = (int)(baseHeight * scale);
            int offsetX = (endGameView.getWidth() - scaledWidth) / 2;
            int offsetY = (endGameView.getHeight() - scaledHeight) / 2;
            
            // Utiliser PillButton
            fr.upc.g5e4.view.PillButton returnButton = new fr.upc.g5e4.view.PillButton("Retour au Menu");
            returnButton.setFont(googleSansFont.deriveFont((float)(20 * scale)));
            
            // Positionner le bouton avec offset et scaling - centré sous le trophée
            int buttonBaseY = 630;
            int buttonX = (int)(700 * scale) + offsetX;  // Centré sous le trophée
            int buttonY = (int)(buttonBaseY * scale) + offsetY;
            int buttonWidth = (int)(300 * scale);
            int buttonHeight = (int)(60 * scale);
            returnButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
            
            returnButton.setFocusable(true);
            returnButton.setVisible(true);
            returnButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            returnButton.addActionListener(e -> {
                // Retourner au menu
                SwingUtilities.invokeLater(() -> {
                    gameLauncher.showMenu();
                    gameLauncher.getCardPanel().remove(endGameView);
                    gameLauncher.resetGame();
                });
            });
            
            endGameView.add(returnButton);
            endGameView.setLayout(null);
            
            // Ajouter un listener pour repositionner le bouton quand la taille change (plein écran)
            endGameView.addComponentListener(new java.awt.event.ComponentAdapter() {
                @Override
                public void componentResized(java.awt.event.ComponentEvent e) {
                    // Recalculer la position du bouton
                    float scale = Math.min((float)endGameView.getWidth() / baseWidth, (float)endGameView.getHeight() / baseHeight);
                    int scaledWidth = (int)(baseWidth * scale);
                    int scaledHeight = (int)(baseHeight * scale);
                    int offsetX = (endGameView.getWidth() - scaledWidth) / 2;
                    int offsetY = (endGameView.getHeight() - scaledHeight) / 2;
                    
                    int buttonX = (int)(700 * scale) + offsetX;  // Centré sous le trophée
                    int buttonY = (int)(buttonBaseY * scale) + offsetY;
                    int buttonWidth = (int)(300 * scale);
                    int buttonHeight = (int)(60 * scale);
                    
                    returnButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
                    returnButton.setFont(googleSansFont.deriveFont((float)(20 * scale)));
                }
            });
            
            endGameView.revalidate();
            endGameView.repaint();
            returnButton.requestFocus();  // Donner le focus au bouton
        }
    }
}

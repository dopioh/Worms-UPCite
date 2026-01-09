package fr.upc.g5e4.controller.minigames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import fr.upc.g5e4.model.minigames.WormGuessModel;
import fr.upc.g5e4.view.minigames.WormGuessView;
import fr.upc.g5e4.model.GameManager;

/**
 * Controller pour le mini-jeu WormGuess
 * @author Nassim
 */
public class WormGuessController {
    
    private WormGuessModel model;
    private WormGuessView view;
    private Timer animationTimer;
    
    public WormGuessController(WormGuessModel model, WormGuessView view) {
        this.model = model;
        this.view = view;
        
        setupListeners();
    }
    
    private void setupListeners() {
        view.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!view.isOverlayVisible()) return;
                
                Rectangle overlayBounds = view.getOverlayBounds();

                // si le clic est dans la dimension (overlayBounds) de l'overlay
                // on effectue l'action de l'element cliqué
                if (overlayBounds.contains(e.getX(), e.getY())) {
                    handleClick(e.getX(), e.getY());
                    e.consume();
                } else {
                    closeOverlay();
                }
            }
        });
        
        view.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (view.isOverlayVisible()) {
                    view.repaint();
                }
            }
        });
    }
    
    // Afficher l'overlay du jeu
    public void showGame() {
        model.resetGame();
        view.showOverlay();
    }
    
    // Fermer l'overlay
    public void closeOverlay() {
        stopAnimation();
        model.stopGame();
        view.hideOverlay();
    }
    
    private void handleClick(int x, int y) {
        Rectangle startButton = view.getStartGameButton();
        if (!model.isGameStarted() && !model.isAnimating() && startButton != null && startButton.contains(x, y)) {
            if (model.getTotalBets() > 0) {
                startGame();
            }
            return;
        }
        
        if (!model.isGameStarted() && !model.isAnimating()) {
            Rectangle[] betButtons = view.getBetButtons();
            for (int i = 0; i < betButtons.length; i++) {
                if (betButtons[i] != null && betButtons[i].contains(x, y)) {
                    model.placeBet(i);
                    return;
                }
            }
        }
    }
    
    // Demarrer le jeu avec l'animation
    private void startGame() {
        model.startGame();
        
        animationTimer = new Timer(50, e -> {
            if (model.updateAnimation()) {
                stopAnimation();
            }
        });
        animationTimer.start();
    }
    
    // Arreter l'animation
    private void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
            animationTimer = null;
        }
    }
    
    // Config le GameManager pour le model
    public void setGameManager(GameManager gameManager) {
        model.setGameManager(gameManager);
    }
    
    // Retourne la vue
    public WormGuessView getView() {
        return view;
    }
    
    // Retourne le model
    public WormGuessModel getModel() {
        return model;
    }
}

package fr.upc.g5e4.controller.minigames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import fr.upc.g5e4.model.minigames.WormJackModel;
import fr.upc.g5e4.view.minigames.WormJackView;
import fr.upc.g5e4.model.GameManager;

/**
 * Controller pour le mini-jeu WormJack
 * @author Nassim
*/
public class WormJackController {
    
    private WormJackModel model;
    private WormJackView view;
    private Timer wormbotTimer;
    
    public WormJackController(WormJackModel model, WormJackView view) {
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
        stopWormbotTimer();
        view.hideOverlay();
    }
    
    private void handleClick(int x, int y) {
        Rectangle betButton = view.getBetButton();
        if (!model.isGameStarted() && betButton != null && betButton.contains(x, y)) {
            model.placeBet();
            return;
        }

        Rectangle startButton = view.getStartButton();
        if (!model.isGameStarted() && model.getCurrentBet() > 0 && startButton != null && startButton.contains(x, y)) {
            model.startGame();
            return;
        }
        
        Rectangle hitButton = view.getHitButton();
        if (model.isGameStarted() && !model.isGameFinished() && model.isPlayerTurn() && hitButton != null && hitButton.contains(x, y)) {
            model.hit();
            return;
        }
        
        Rectangle standButton = view.getStandButton();
        if (model.isGameStarted() && !model.isGameFinished() && model.isPlayerTurn() && standButton != null && standButton.contains(x, y)) {
            playerStand();
            return;
        }
    }
    
    // Le joueur reste = a wormbot de jouer
    private void playerStand() {
        model.stand();
        
        // Le wormbot tire jusqu'à 17+ avec un délai entre chaque tirage
        wormbotTimer = new Timer(1000, e -> {
            if (model.shouldWormbotHit()) {
                model.wormbotHit();
            } else {
                stopWormbotTimer();
                model.finishWormbotTurn();
            }
        });
        wormbotTimer.start();
    }
    
    // Arreter le timer du wormbot
    private void stopWormbotTimer() {
        if (wormbotTimer != null) {
            wormbotTimer.stop();
            wormbotTimer = null;
        }
    }
    
    // Config le GameManager pour le model
    public void setGameManager(GameManager gameManager) {
        model.setGameManager(gameManager);
    }
    
    // Retourne la vue
    public WormJackView getView() {
        return view;
    }
    
    // Retourne le model
    public WormJackModel getModel() {
        return model;
    }
}

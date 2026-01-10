package fr.upc.g5e4.controller.minigames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import fr.upc.g5e4.model.minigames.WormRaceModel;
import fr.upc.g5e4.view.minigames.WormRaceView;
import fr.upc.g5e4.model.GameManager;

/**
 * Controller pour le mini-jeu WormRace
 * @author Nassim
 */
public class WormRaceController {
    
    private WormRaceModel model;
    private WormRaceView view;
    private Timer raceTimer;
    
    /**
     * Constructeur
     * @param model le model du mini-jeu
     * @param view la vue du mini-jeu
     */
    public WormRaceController(WormRaceModel model, WormRaceView view) {
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

    private void startRace() {
        model.startRace();
        
        raceTimer = new Timer(50, e -> {
            if (model.updatePositions()) {
                stopRace();
            }
        });
        raceTimer.start();
    }
    
    private void stopRace() {
        if (raceTimer != null) {
            raceTimer.stop();
            raceTimer = null;
        }
    }
    
    // Afficher l'overlay du jeu
    public void showGame() {
        model.resetGame();
        view.showOverlay();
    }
    
    // Fermer l'overlay du jeu
    public void closeOverlay() {
        stopRace();
        view.hideOverlay();
    }
    
    private void handleClick(int x, int y) {
        Rectangle startButton = view.getStartRaceButton();
        if (!model.isRaceStarted() && startButton != null && startButton.contains(x, y)) {
            if (model.getTotalBets() > 0) {
                startRace();
            }
            return;
        }
        
        if (!model.isRaceStarted()) {
            Rectangle[] betButtons = view.getBetButtons();
            for (int i = 0; i < betButtons.length; i++) {
                if (betButtons[i] != null && betButtons[i].contains(x, y)) {
                    model.placeBet(i);
                    return;
                }
            }
        }
    }
    
    // Config le GameManager pour le model
    public void setGameManager(GameManager gameManager) {
        model.setGameManager(gameManager);
    }
    
    // Retourne la vue
    public WormRaceView getView() {
        return view;
    }
    
    // Retourne le model
    public WormRaceModel getModel() {
        return model;
    }
}

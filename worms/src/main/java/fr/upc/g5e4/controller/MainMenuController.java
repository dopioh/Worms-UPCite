package fr.upc.g5e4.controller;

import java.io.IOException;

import javax.swing.JOptionPane;

import fr.upc.g5e4.GameLauncher;
import fr.upc.g5e4.view.MainMenuView;

/**
 * Contrôleur pour l'écran d'accueil
 * Gère les actions des boutons du menu principal
 */
public class MainMenuController {
        
    private MainMenuView view;

    public MainMenuController(MainMenuView view) {
        this.view = view;
        addListeners();
    }

    //relie les buttons aux actions
    private void addListeners() {
        view.getBtnNewGame().addActionListener(e -> {
            try {
                startNewGame();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });
        view.getBtnResume().addActionListener(e -> resumeGame());
        view.getBtnConfig().addActionListener(e -> openConfig());
        view.getBtnQuit().addActionListener(e -> quitGame());
    }

    private void startNewGame() throws IOException {
        view.dispose(); // Ferme la fenêtre d'accueil
        new GameLauncher(true); // Lance le jeu
    }

    private void resumeGame() {
        // Pour l'instant juste un message
        JOptionPane.showMessageDialog(view, 
            "Fonctionnalité à venir", 
            "Poursuivre", 
            JOptionPane.INFORMATION_MESSAGE); //determine l'icône qui s'affiche à gauche du texte
    }

    private void openConfig() {
        // Pour l'instant juste un message
        JOptionPane.showMessageDialog(view, 
            "Fonctionnalité à venir", 
            "Réglages", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void quitGame() {
        System.exit(0);
    }

}

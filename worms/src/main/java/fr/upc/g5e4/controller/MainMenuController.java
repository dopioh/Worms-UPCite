package fr.upc.g5e4.controller;

import java.io.IOException;

import javax.swing.JOptionPane;

import fr.upc.g5e4.GameLauncher;
import fr.upc.g5e4.model.io.SaveLoad;
import fr.upc.g5e4.view.MainMenuView;
import fr.upc.g5e4.view.SettingsView;

/**
 * Contrôleur pour l'écran d'accueil
 * Gère les actions des boutons du menu principal
 */
public class MainMenuController {
        
    private MainMenuView view;
    private GameLauncher gameLauncher;

    public MainMenuController(MainMenuView view, GameLauncher gameLauncher) {
        this.view = view;
        this.gameLauncher = gameLauncher;
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
        // Utilise le GameLauncher existant pour basculer vers le jeu
        if (gameLauncher == null){
            this.gameLauncher = new GameLauncher();
        }
        gameLauncher.startGame();
    }

    /**
     * Methode pour lancer une sauvegarde
     */
    private void resumeGame(){ 
        // Pour l'instant juste un message
        /*
        JOptionPane.showMessageDialog(gameLauncher.getMainFrame(), 
            "Fonctionnalité à venir", 
            "Poursuivre", 
            JOptionPane.INFORMATION_MESSAGE); //determine l'icône qui s'affiche à gauche du texte
        */

        try {
            SaveLoad sv = new SaveLoad();
            this.gameLauncher.startFromGameSave(sv.getSaveData());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }


    }

    private void openConfig() {
        // Créer le contrôleur des réglages qui gère les listeners
        SettingsController settingsController = new SettingsController(gameLauncher);
        // Afficher les réglages via GameLauncher qui gère la navigation
        gameLauncher.showSettings(settingsController.getView());
    }

    private void quitGame() {
        System.exit(0);
    }

}

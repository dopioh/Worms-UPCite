package fr.upc.g5e4.controller;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;

import javax.swing.JOptionPane;

import fr.upc.g5e4.GameLauncher;
import fr.upc.g5e4.model.SoundPlayer;
import fr.upc.g5e4.model.TeamSettingsModel;
import fr.upc.g5e4.model.io.SaveLoad;
import fr.upc.g5e4.view.MainMenuView;
import fr.upc.g5e4.view.SettingsView;
import fr.upc.g5e4.view.TeamSettingsView;

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
        view.getBtnHowToPlay().addActionListener(e -> openTutorial());
        view.getBtnConfig().addActionListener(e -> openConfig());
        view.getBtnQuit().addActionListener(e -> quitGame());
        
        // Listener pour repositionner les composants lors du redimensionnement
        view.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                view.repositionComponents();
            }
        });
   }

    private void startNewGame() throws IOException {
        // Utilise le GameLauncher existant pour basculer vers le jeu
        gameLauncher.resetGame();   // pour reset le jeu au cas ou une save serait toujours en memoire
        gameLauncher.startGame();
    }

    /**
     * methode pour lancer une sauvegarde
     */
    private void resumeGame(){ 
        try {
            SaveLoad sv = new SaveLoad();
            boolean lancement = this.gameLauncher.startFromGameSave(sv.getSaveData());
            if (!lancement == true){
                JOptionPane.showMessageDialog(gameLauncher.getMainFrame(),"Aucune sauvegarde enregistrer, veillez d'abord lancer une game et la sauvegarder !", "AUCUNE SAVE FILE TROUVER",
            JOptionPane.INFORMATION_MESSAGE); //determine l'icône qui s'affiche à gauche du texte
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(gameLauncher.getMainFrame(), 
            "Impossible de lancer une sauvegarde", 
            "A Report A Mei", 
            JOptionPane.INFORMATION_MESSAGE); //determine l'icône qui s'affiche à gauche du texte
            System.out.println(e.getMessage());
        }


    }

    private void openConfig() {
        TeamSettingsView teamSettingsView = new TeamSettingsView(gameLauncher.getPlayerDataController()); // creer la vue 

        // créer le contrôleur des réglages qui gère les listeners
        SettingsController settingsController = new SettingsController(gameLauncher, teamSettingsView);

        // creer la partie Terminal 
        TeamSettingsModel teamSettingsModel = new TeamSettingsModel(teamSettingsView,gameLauncher.getPlayerDataController());
        TeamSettingsController teamSettingsController = teamSettingsModel.getTeamSettingsController();

        // afficher les réglages via GameLauncher qui gère la navigation
        gameLauncher.showSettings(settingsController.getView());
    }

    private void openTutorial() {
        view.getTutorialController().showTutorial();
    }

    private void quitGame() {
        SoundPlayer.stopBackgroundMusic();
        System.exit(0);
    }

}

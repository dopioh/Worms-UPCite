package fr.upc.g5e4.controller;

import fr.upc.g5e4.GameLauncher;
import fr.upc.g5e4.view.SettingsView;

/**
 * Contrôleur pour la vue des réglages
 * Gère les actions des onglets et boutons dans SettingsView
 */
public class SettingsController {
    
    private SettingsView view;
    private GameLauncher gameLauncher;
    
    public SettingsController(GameLauncher gameLauncher) {
        this.gameLauncher = gameLauncher;
        this.view = new SettingsView();
        addListeners();
    }
    
    /**
     * Ajoute les listeners aux boutons de la vue
     */
    private void addListeners() {
        // Listener pour le bouton retour
        view.getBtnBack().addActionListener(e -> handleBackButton());
        
        // Listeners pour les onglets
        view.getBtnMapTab().addActionListener(e -> view.showPanel("Map"));
        view.getBtnTeamsTab().addActionListener(e -> view.showPanel("Equipes"));
        view.getBtnOtherTab().addActionListener(e -> view.showPanel("Autre"));
        
        // Listener pour le bouton test dans l'onglet Map
        view.getTestButton().addActionListener(e -> handleTestButton());
    }
    
    /**
     * Gère le clic sur le bouton retour
     */
    private void handleBackButton() {
        gameLauncher.showMenu();
    }
    
    /**
     * Gère le clic sur le bouton test
     */
    private void handleTestButton() {
        System.out.println("Test appuyé");
    }
    
    public SettingsView getView() {
        return view;
    }
}

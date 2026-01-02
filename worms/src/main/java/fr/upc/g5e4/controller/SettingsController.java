package fr.upc.g5e4.controller;

import fr.upc.g5e4.GameLauncher;
import fr.upc.g5e4.model.MapConfig;
import fr.upc.g5e4.view.SettingsView;
import fr.upc.g5e4.view.TeamSettingsView;

/**
 * Contrôleur pour la vue des réglages
 * Gère les actions des onglets et boutons dans SettingsView
 */
public class SettingsController {
    
    private SettingsView view;
    private GameLauncher gameLauncher;
    
    public SettingsController(GameLauncher gameLauncher, TeamSettingsView teamSettingsView) {
        this.gameLauncher = gameLauncher;
        this.view = new SettingsView(teamSettingsView);
        initializeMapSettings();
        addListeners();
    }
    
    /**
     * Initialise les valeurs des dropdowns selon la configuration actuelle
     */
    private void initializeMapSettings() {
        // Initialiser la taille
        MapConfig.MapSize currentSize = MapConfig.getMapSize();
        String sizeText = switch (currentSize) {
            case SMALL -> "Petit";
            case MEDIUM -> "Moyen";
            case LARGE -> "Grand";
        };
        view.getSizeComboBox().setSelectedItem(sizeText);
        
        // Initialiser le terrain
        MapConfig.MapType currentType = MapConfig.getMapType();
        String terrainText = switch (currentType) {
            case STANDARD -> "Standard";
            case FLAT -> "Plat";
            case MOUNTAINS -> "Montagnes";
            case ISLANDS -> "Iles";
        };
        view.getTerrainComboBox().setSelectedItem(terrainText);
        
        // Mettre à jour l'aperçu avec les valeurs actuelles
        view.updateMapPreview();
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
        
        // Listeners pour les dropdowns de la map
        view.getSizeComboBox().addActionListener(e -> {
            handleSizeChange();
            view.updateMapPreview(); // Mettre à jour l'aperçu
        });
        view.getTerrainComboBox().addActionListener(e -> {
            handleTerrainChange();
            view.updateMapPreview(); // Mettre à jour l'aperçu
        });
    }
    
    /**
     * Gère le changement de taille de la map
     */
    private void handleSizeChange() {
        String selected = view.getSelectedSize();
        MapConfig.MapSize newSize = switch (selected) {
            case "Petit" -> MapConfig.MapSize.SMALL;
            case "Moyen" -> MapConfig.MapSize.MEDIUM;
            case "Grand" -> MapConfig.MapSize.LARGE;
            default -> MapConfig.MapSize.MEDIUM;
        };
        MapConfig.setMapSize(newSize);
        System.out.println("Taille de map changée: " + newSize);
    }
    
    /**
     * Gère le changement de type de terrain
     */
    private void handleTerrainChange() {
        String selected = view.getSelectedTerrain();
        MapConfig.MapType newType = switch (selected) {
            case "Standard" -> MapConfig.MapType.STANDARD;
            case "Plat" -> MapConfig.MapType.FLAT;
            case "Montagnes" -> MapConfig.MapType.MOUNTAINS;
            case "Iles" -> MapConfig.MapType.ISLANDS;
            default -> MapConfig.MapType.STANDARD;
        };
        MapConfig.setMapType(newType);
        System.out.println("Type de terrain changé: " + newType);
    }
    
    /**
     * Gère le clic sur le bouton retour
     */
    private void handleBackButton() {
        gameLauncher.showMenu();
    }
    
    public SettingsView getView() {
        return view;
    }
}

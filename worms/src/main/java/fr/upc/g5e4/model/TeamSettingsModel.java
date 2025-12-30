package fr.upc.g5e4.model;

import fr.upc.g5e4.controller.*;
import fr.upc.g5e4.view.*;

/**
 * Classe qui va gerer le controller et la view
 */
public class TeamSettingsModel {

    private TeamSettingsView view;
    private TeamSettingsController controller;

    public TeamSettingsModel(TeamSettingsView view){
        this.view = view;
        this.controller = new TeamSettingsController(this, view);
    }

    public TeamSettingsController getTeamSettingsController(){
        return this.controller;
    }
    
}

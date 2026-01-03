package fr.upc.g5e4.controller;

import fr.upc.g5e4.model.*;
import fr.upc.g5e4.view.*;

public class TeamSettingsController {

    private TeamSettingsModel model;
    private TeamSettingsView view;
    
    public TeamSettingsController(TeamSettingsModel model, TeamSettingsView view){
        this.model = model;
        this.view = view;

        listener();
    }

    public void listener(){
        view.getSaveNextGameButton().addActionListener(e -> model.saveDataNextGame());
        view.getSkinPillButton().addActionListener(e -> model.changePreviewImage());
        view.getSaveCustom().addActionListener(e -> model.saveCustom());
        view.getChangeNumberTeam().addActionListener(e -> model.changePreviewImage());
    }


}

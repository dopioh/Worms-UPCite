package fr.upc.g5e4.controller;

import fr.upc.g5e4.model.*;
import fr.upc.g5e4.view.*;

/**
 * Controller pour relier les buttons de teamSettings View a leur fonction
 * 
 * @see TeamSettingsView les bouttons
 * @see TeamSettingsModel les fonctions
 * 
 * @author Mei
 */
public class TeamSettingsController {

    private TeamSettingsModel model;
    private TeamSettingsView view;
    
    /**
     * Controller par defaut
     * @param model TeamSettingsModel associees
     * @param view TeamSettingsView associees
     */
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

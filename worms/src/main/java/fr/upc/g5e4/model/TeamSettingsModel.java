package fr.upc.g5e4.model;

import fr.upc.g5e4.controller.*;import fr.upc.g5e4.model.io.CustomTeamData;
import fr.upc.g5e4.view.*;

import java.util.ArrayList;

/**
 * Classe qui va gerer le controller et la view
 */
public class TeamSettingsModel {

    private TeamSettingsView view;
    private TeamSettingsController controller;
    private PlayerDataController playerDataController;

    private int nbrEquipe;
    private int nbrWormByTeam;
    private ArrayList<String> customTeamByTeam;


    public TeamSettingsModel(TeamSettingsView view, PlayerDataController playerDataController){
        this.playerDataController = playerDataController;
        this.view = view;
        this.controller = new TeamSettingsController(this, view);
    }

    public TeamSettingsController getTeamSettingsController(){
        return this.controller;
    }

    public void saveDataNextGame(){
        this.nbrEquipe = view.getNbrEquipe();
        this.nbrWormByTeam = view.getWormByTeam();
        this.customTeamByTeam = view.getAllCustomByTeam();

        transferTeamConfig();
        System.out.println(nbrEquipe);
        System.out.println("Bouton Appuye - Save des data entree pour la prochaine partie");
    }

    public void transferTeamConfig(){
        TeamConfig.NBR_EQUIPE = nbrEquipe;
        TeamConfig.NBR_WORM_EQUIPE = nbrWormByTeam;
        TeamConfig.aCustomForATeam = findClassByName();
    }

    public void changePreviewImage(){
        view.update(view.getFullScreen());
    }

    public void saveCustom(){
        String teamName = view.getTeamName();
        SkinModel skin = playerDataController.getPlayerData().findSkinModelByName(view.getSkinChooseSTR());
        ArrayList<String> allWormsName = view.getAllWormsName();

        // on save dans le playerController -> PlayerData

        this.playerDataController.getPlayerData().getCustomTeamManager().createCustomTeam(teamName, skin, 6, allWormsName);
        this.playerDataController.sauvegarderJoueur();

        view.reloadCustomTeamChoose();
    }

    public ArrayList<CustomTeamData> findClassByName(){
        ArrayList<CustomTeamData> res = new ArrayList<>();
        for (int i = 0 ; i < customTeamByTeam.size() ; i++){
            String nomCustomTeam = customTeamByTeam.get(i);
            ArrayList<CustomTeamData> allPlayerCustomData = playerDataController.getPlayerData().getCustomTeamManager().getListCustomTeamData();
            for (CustomTeamData cs : allPlayerCustomData){
                if (cs.getTeamName().equals(nomCustomTeam)){
                    res.add(cs);
                }
            }
        }
        return res;
    }
    
}

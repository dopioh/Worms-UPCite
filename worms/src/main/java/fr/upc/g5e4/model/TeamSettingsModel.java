package fr.upc.g5e4.model;

import fr.upc.g5e4.controller.*;
import fr.upc.g5e4.model.io.CustomTeamData;
import fr.upc.g5e4.view.*;

import java.util.ArrayList;

/**
 * Classe qui va gerer le controller et la view
 * 
 * @see TeamSettingsView partie view
 * @see PlayerDataController partie controller
 * @see TeamConfig pour save les donnees pour la prochaine partie
 * 
 * @author Mei
 */
public class TeamSettingsModel {

    private TeamSettingsView view;
    private TeamSettingsController controller;
    private PlayerDataController playerDataController;

    private int nbrEquipe;
    private int nbrWormByTeam;
    private ArrayList<String> customTeamByTeam;

    /**
     * Constructeur par defaut
     * @param view la vue des settings team 
     * @param playerDataController le controller qui gere les donnees joueur
     */
    public TeamSettingsModel(TeamSettingsView view, PlayerDataController playerDataController){
        this.playerDataController = playerDataController;
        this.view = view;
        this.controller = new TeamSettingsController(this, view);
    }

    // getter
    public TeamSettingsController getTeamSettingsController(){
        return this.controller;
    }

    /**
     * Appeler lorsque le bouton de gauche (zoneChoice) est called
     * Save les donnees entrees dans TeamConfig
     * 
     * @see TeamSettingsModel#transferTeamConfig()
     */
    public void saveDataNextGame(){
        this.nbrEquipe = view.getNbrEquipe();
        this.nbrWormByTeam = view.getWormByTeam();
        this.customTeamByTeam = view.getAllCustomByTeam();

        transferTeamConfig();
        System.out.println(nbrEquipe);
        System.out.println("Bouton Appuye - Save des data entree pour la prochaine partie");
    }

    /**
     * Methode interne pour transferer les donnees user dans TeamCongif
     */
    private void transferTeamConfig(){
        TeamConfig.NBR_EQUIPE = nbrEquipe;
        TeamConfig.NBR_WORM_EQUIPE = nbrWormByTeam;
        TeamConfig.aCustomForATeam = findClassByName();
    }

    /**
     * Pour l'update de l'image du worm
     * 
     * Ca update toute la vue graphique des TeamSettings en realites
     */
    public void changePreviewImage(){
        view.update(view.getFullScreen());
    }

    /**
     * On save dans PlayerData si on creer une nouvelle Custom Team
     */
    public void saveCustom(){
        String teamName = view.getTeamName();
        
        // verifier si nom est vide
        if (teamName == null || teamName.trim().isEmpty()) {
            showDialog("Le nom de l'équipe ne peut pas être vide!", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // verifier si le nom existe déjà
        ArrayList<CustomTeamData> existingTeams = playerDataController.getPlayerData().getCustomTeamManager().getListCustomTeamData();
        for (CustomTeamData existing : existingTeams) {
            if (existing.getTeamName().equalsIgnoreCase(teamName.trim())) {
                showDialog("Une équipe avec ce nom existe déjà!", javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        SkinModel skin = playerDataController.getPlayerData().findSkinModelByName(view.getSkinChooseSTR());
        ArrayList<String> allWormsName = view.getAllWormsName();

        // on save dans le playerController -> PlayerData
        this.playerDataController.getPlayerData().getCustomTeamManager().createCustomTeam(teamName.trim(), skin, 6, allWormsName);
        this.playerDataController.sauvegarderJoueur();

        view.reloadCustomTeamChoose();
        
        showDialog("Équipe créée!", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showDialog(String message, int type) {
        if (!java.awt.GraphicsEnvironment.isHeadless() && view != null && view.isDisplayable()) {
            javax.swing.JOptionPane.showMessageDialog(view, message, "info", type);
        }
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

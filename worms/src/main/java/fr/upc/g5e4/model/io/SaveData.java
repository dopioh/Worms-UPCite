package fr.upc.g5e4.model.io;

import fr.upc.g5e4.view.*;
import fr.upc.g5e4.controller.*;
import fr.upc.g5e4.model.*;

import java.util.ArrayList;

import com.google.gson.*;  

/*
Que mettre dans la sauvegarde ?
    - Vers actifs :
        - leur position 
        - stocker que ceux vivant 
        - leur sprite ? (si dans le futur, on peut les personnaliser)
            -> stocker leur character model

        Juste prendre les teams model, tout pourra etre recuperer depuis eux ? 
            -> On veut afficher les pv aussi etc dans sauvegarde.txt
                -> Donc on doit detailler les attributs ici

    - Map :
        - l'eau (position fixe ?)
        - le terrain (juste GroundModel ?)

    - Inventaire : 
        - l'inventaire par equipe (est ce que c'est deja correctement configurer ca ?)

    - Donnees Parties :
        - Timer 
        - Round 
        - Team Actif 
        - Ver Actif 
*/

/**
 * Classe qui va stocker, dans des variables,
 * les donnees qu'on veut mettre dans le json
 * 
 * Chaque attribut sera transforme en key dans JSON 
 * Grace a GSON
 */
public class SaveData {
    // Donnes Parties :
    private int round;
    private int currentTeamindex;
    private int activeWormIndex;

    private int roundTimeRemaining;
    // Terrain : 
    private GroundModel groundModel; // pas besoin de classe Data car aucune data useless dans GroundModel
    // Worms :
    private ArrayList<TeamData> teamDatas; // devrait print dans le json les character aussi sous forme de tab
    
    // Inventaire :

    // Infos Partie (Round, team et worm actif) :


    public SaveData(GameManager gameManager){
        System.out.println("SaveData Called");
        this.groundModel = gameManager.getGroundModel();
        teamDatas = modelToData(gameManager.getlistTeams());
        this.round = gameManager.getRoundNumber();
        this.currentTeamindex = gameManager.getCurrentTeamIndex();
        this.activeWormIndex = gameManager.getActiveWormIndex();
        this.roundTimeRemaining = gameManager.getRoundTimeSave();
    }

    public ArrayList<TeamData> modelToData(ArrayList<TeamModel> teamModels){
        ArrayList<TeamData> result = new ArrayList<>();
        for (TeamModel tm : teamModels){
            TeamData tdTemp = tm.toData();
            result.add(tdTemp);
        }
        return result;
    }

    public GroundModel getGroundModel(){
        return groundModel;
    }

    public ArrayList<TeamData> getListTeamData(){
        return teamDatas;
    }

    public int getRound(){
        return round;
    }
    
    public int getCurrentTeamIndex(){
        return currentTeamindex;
    }

    public int getActiveWormIndex(){
        return activeWormIndex;
    }

    public int getRoundTimeRemaining(){
        return roundTimeRemaining;
    }




}

package fr.upc.g5e4.model.io;

//import fr.upc.g5e4.view.*;
//import fr.upc.g5e4.controller.*;
import fr.upc.g5e4.model.*;
import fr.upc.g5e4.model.MapConfig.BackgroundStyle;

import java.util.ArrayList;
import java.util.Map;

//import com.google.gson.*;  

/**
 * Classe qui va stocker, dans des variables,
 * les donnees qu'on veut mettre dans le json
 * 
 * Chaque attribut sera transforme en key dans JSON 
 * Grace a GSON
 * 
 * @see SaveLoad pour le chargement d'une SaveData
 * 
 * @see TeamData donnees de TeamModel version minimal
 * @see CharacterData donnees de CharacterModel version minimal
 * @see WindData donnees de WindModel version minimal
 * 
 * @author Mei
 */
public class SaveData {
    // Donnes Parties :
    private int round;

    private WindData windData;

    // Pointeurs :
    private int currentTeamindex;
    private int activeWormIndex;
    private int[] teamCursor;

    private int roundTimeRemaining;

    // Terrain : 
    private GroundModel groundModel; // pas besoin de classe Data car aucune data useless dans GroundModel
    // Worms :
    private ArrayList<TeamData> teamDatas; // devrait print dans le json les character aussi sous forme de tab
    
    // Inventaire -> dans TeamInit et TeamModel

    // GameStats : 
    private Map<String, GameStats> teamStats;

    // Map Jour ou Nuit
    private static MapConfig.BackgroundStyle bg;

    // AJOUT NOUVEAU TERRAIN (mettre en dernier dans l'ordre des attributs sinon compliquer a debug sauvegardes.txt)
    private BlockGridModel blockGridModel;

    /**
     * Constructeur pour initialiser une SaveData a partir d'une game active
     * @param gameManager game active
     */
    public SaveData(GameManager gameManager){

        System.out.println("SaveData Called");
        
        this.round = gameManager.getRoundNumber();

        this.currentTeamindex = gameManager.getCurrentTeamIndex();
        this.activeWormIndex = gameManager.getActiveWormIndex();
        this.teamCursor = gameManager.getTeamCursor();

        this.roundTimeRemaining = gameManager.getRoundTimeSave();

        this.groundModel = gameManager.getGroundModel();
        this.teamDatas = modelToData(gameManager.getlistTeams());

        this.teamStats = gameManager.getAllTeamStats();

        bg = MapConfig.getBackgroundStyle();
        if (bg == null){
            bg = MapConfig.BackgroundStyle.JOUR;
        }

        this.windData = WindData.WindModelToWindData(gameManager.getWindModel());

        this.blockGridModel = gameManager.getBlockGridModel();

    }

    /**
     * Transforme une liste de TeamModel en TeamData pour pouvoir la stocker dans SaveData
     * @param teamModels liste de tout les TeamModel d'une game
     * @return la version Data
     */
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

    public int[] getTeamCursor(){
        return teamCursor;
    }

    public Map<String, GameStats> getAllTeamStats(){
        return teamStats;
    }

    public WindData getWindData() {
        return windData;
    }

    public BlockGridModel getBlockGridModel(){
        return this.blockGridModel;
    }

    public static MapConfig.BackgroundStyle getBackgroundFROMSAVE(){
        return bg;
    }

}

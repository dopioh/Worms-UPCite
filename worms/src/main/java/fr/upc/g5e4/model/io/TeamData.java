package fr.upc.g5e4.model.io;

//import java.util.ArrayList;

import org.jbox2d.dynamics.World;

import fr.upc.g5e4.model.CharacterModel;
import fr.upc.g5e4.model.GroundModel;
import fr.upc.g5e4.model.InventoryModel.InventoryJson;
import fr.upc.g5e4.model.TeamInitialisation;
import fr.upc.g5e4.model.TeamModel;


//import java.awt.Color;
import java.util.List;

/**
 * Classe pour transformer une TeamModel en TeamData car certains attributs de TeamModel sont useless
 * 
 * @see TeamModel pour la version original
 * @see SaveData pour le regroupement des Data
 * 
 * @author Mei
 */
public class TeamData {

    private final int teamID; // c'est ca le team name ?
    private int wormsNumber; // par equipe
    private String teamName;
    private int COLORTEAM;

    private CharacterData[] dataTabCharacter;

    private List<InventoryJson> inventory;

    /**
     * Constructeur minimal pour sauvegarder les donnees necessaire pour la sauvegarde d'une partie
     * @param teamNumber numero d'equipe (= ID)
     * @param wormsNumber nombre de worms dans l'equipe
     * @param tabModelCharacter tab des CharaModel des worms dans l'equipe
     */
    public TeamData(int teamNumber, int wormsNumber, CharacterModel[] tabModelCharacter, String teamName,int COLORTEAM,List<InventoryJson> inventory){
        this.teamID = teamNumber;
        this.wormsNumber = wormsNumber;
        this.dataTabCharacter = modelToData(tabModelCharacter);
        this.teamName = teamName;
        this.COLORTEAM = COLORTEAM;
        this.inventory = inventory;
        
    }

    public int getTeamID(){
        return teamID;
    }

    public int getWormsNumber(){
        return wormsNumber;
    }

    public CharacterData[] getTabCharacterData(){
        return dataTabCharacter;
    }

    public String getTeamName(){
        return teamName;
    }

    /**
     * Tranforme un TeamData en TeamInitialisation pour load une save
     * @param groundModel le terrain
     * @param world la physique de la game
     * @return  un TeamInit a integreer dans un GameManager
     */
    public TeamInitialisation dataToInit(GroundModel groundModel,World world){
        TeamInitialisation tm = new TeamInitialisation(groundModel, world, teamID, teamName, wormsNumber, dataTabCharacter, COLORTEAM,"/worm.png",inventory);
        return tm;
    }

    /**
     * Tranforme un TeamData en TeamModel pour load une save
     * Utilise la methode dataToInit pour simplifier le code 
     * @param groundModel le terrain
     * @param world la physique de la game
     * @return un TeamModel a integreer dans un GameManager
     * 
     * @see TeamData#dataToInit(GroundModel, World)
     */
    public TeamModel dataToModel(GroundModel groundModel,World world){
        TeamModel result = dataToInit(groundModel, world).getTeamModel();
        return result;
    }

    /**
     * Transforme un tab de CharacterModel en tab de CharacterData
     * Pour le save et l'integree dans TeamData
     * @param tabModelCharacter tab de CharacterModel
     * @return tab de CharacterData
     */
    private CharacterData[] modelToData(CharacterModel[] tabModelCharacter){
        CharacterData[] result = new CharacterData[tabModelCharacter.length];
        for (int i = 0 ; i < result.length ; i++){
            result[i] = tabModelCharacter[i].toData();
        }
        return result;
    }

}

package fr.upc.g5e4.model.io;

//import java.util.ArrayList;

import org.jbox2d.dynamics.World;

import fr.upc.g5e4.model.CharacterModel;
import fr.upc.g5e4.model.GroundModel;
import fr.upc.g5e4.model.TeamModel;

public class TeamData {

    private final int teamID; // c'est ca le team name ?
    private int wormsNumber; // par equipe
    private String teamName;

    private CharacterData[] dataTabCharacter;
    /**
     * 
     * @param teamNumber numero d'equipe (= ID)
     * @param wormsNumber nombre de worms dans l'equipe
     * @param tabModelCharacter tab des CharaModel des worms dans l'equipe
     */
    public TeamData(int teamNumber, int wormsNumber, CharacterModel[] tabModelCharacter, String teamName){
        this.teamID = teamNumber;
        this.wormsNumber = wormsNumber;
        this.dataTabCharacter = modelToData(tabModelCharacter);
        this.teamName = teamName;
        
        
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

    public TeamModel dataToModel(GroundModel groundModel,World world){
        TeamModel result = new TeamModel(groundModel, world, teamName, teamID, teamID, wormsNumber, false,false,false);
        return result;
    }

    private CharacterData[] modelToData(CharacterModel[] tabModelCharacter){
        CharacterData[] result = new CharacterData[tabModelCharacter.length];
        for (int i = 0 ; i < result.length ; i++){
            result[i] = tabModelCharacter[i].toData();
        }
        return result;
    }

}

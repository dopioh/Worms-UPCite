package fr.upc.g5e4.model.io;

import fr.upc.g5e4.model.CharacterModel;

public class TeamData {

    private final int teamID;
    private int wormsNumber; // par equipe
    private CharacterData[] dataTabCharacter;

    /**
     * 
     * @param teamNumber numero d'equipe (= ID)
     * @param wormsNumber nombre de worms dans l'equipe
     * @param tabModelCharacter tab des CharaModel des worms dans l'equipe
     */
    public TeamData(int teamNumber, int wormsNumber, CharacterModel[] tabModelCharacter){
        this.teamID = teamNumber;
        this.wormsNumber = wormsNumber;
        this.dataTabCharacter = modelToData(tabModelCharacter);
        
        
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

    private CharacterData[] modelToData(CharacterModel[] tabModelCharacter){
        CharacterData[] result = new CharacterData[tabModelCharacter.length];
        for (int i = 0 ; i < result.length ; i++){
            result[i] = tabModelCharacter[i].toData();
        }
        return result;
    }
}

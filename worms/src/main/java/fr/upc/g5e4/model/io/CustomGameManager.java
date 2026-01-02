package fr.upc.g5e4.model.io;

import java.util.ArrayList;

import fr.upc.g5e4.model.SkinModel;

/**
 * Classe pour gerer les teams custom d'un joueur
 * Regroupe des data qui seront mis dans player_save.txt
 * 
 * @author Mei
 */
public class CustomGameManager {

    // je mets tous en static pour le moment mais je pense que ca va passer en dynamique pour le json
    private int nbrCustomTeam = 0;

    private ArrayList<CustomTeamData> listCustomTeamData;
    
    //private ArrayList<>

    /**
     * Constructeur pour un CustomGameManager vide
     * Dans le cas d'un nouveau joueur
     */
    public CustomGameManager(){
        this.nbrCustomTeam = 0;
        this.listCustomTeamData = new ArrayList<CustomTeamData>();
    }

    public void createCustomTeam(String teamName, SkinModel skin, int nbrWorms, ArrayList<String> WormsName){
        CustomTeamData cs = new CustomTeamData(teamName, skin, nbrWorms, WormsName);
        this.listCustomTeamData.add(cs);
        nbrCustomTeam++;
    }

    // Getter 
    public int getNbrCustomTeam(){
        return nbrCustomTeam;
    }

    public ArrayList<CustomTeamData> getListCustomTeamData(){
        return this.listCustomTeamData;
    }

    //setter
    public void setArrayList(ArrayList<CustomTeamData> listCustomTeamData){
        this.listCustomTeamData = listCustomTeamData;
    }
}

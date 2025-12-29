package fr.upc.g5e4.model.io;

import java.util.ArrayList;

/**
 * Classe pour gerer les teams custom d'un joueur
 * Regroupe des data qui seront mis dans player_save.txt
 * 
 * @author Mei
 */
public class CustomTeamManager {

    // je mets tous en static pour le moment mais je pense que ca va passer en dynamique pour le json
    private static int nbrCustomTeam = 0;

    private static ArrayList<CustomTeamData> listCustomTeamData;
    
    //private ArrayList<>

    /**
     * Constructeur pour un CustomGameManager vide
     * Dans le cas d'un nouveau joueur
     */
    public CustomTeamManager(){
        this.nbrCustomTeam = 0;
        this.listCustomTeamData = new ArrayList<CustomTeamData>();
    }

    /* Ou sauvegarder un json ? ici ou dans PlayerController ? 
     !! Essayer d'abord de rendre le PlayerController fonctionnel avant de faire les teams
     -> On va stocker ces valeurs dans PlayerData et on va save grace a PlayerController 
     */
}

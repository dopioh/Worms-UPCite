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
*/

/**
 * Classe qui va stocker, dans des variables,
 * les donnees qu'on veut mettre dans le json
 * 
 * Chaque attribut sera transforme en key dans JSON 
 * Grace a GSON
 */
public class SaveData {
    // Terrain : 
    private GroundModel groundModel; // pas besoin de classe Data car aucune data useless dans GroundModel
    // Worms :
    private ArrayList<TeamData> teamDatas; // devrait print dans le json les character aussi sous forme de tab
    
    // Inventaire :


    public SaveData(GameManager gameManager){
        this.groundModel = gameManager.getGroundModel();
        teamDatas = modelToData(gameManager.getlistTeams());
    }

    public ArrayList<TeamData> modelToData(ArrayList<TeamModel> teamModels){
        ArrayList<TeamData> result = new ArrayList<>();
        for (TeamModel tm : teamModels){
            TeamData tdTemp = tm.toData();
            result.add(tdTemp);
        }
        return result;
    }




}

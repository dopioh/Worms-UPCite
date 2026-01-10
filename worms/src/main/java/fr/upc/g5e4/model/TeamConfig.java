package fr.upc.g5e4.model;

import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;

import fr.upc.g5e4.model.io.CustomTeamData;

/**
 * Classe qui copie MapConfig mais version Team
 * 
 * @see MapConfig
 * @author Mei
 */
public class TeamConfig {
    
    public static int NBR_EQUIPE = 2;

    public static int NBR_WORM_EQUIPE = 3;

    public static ArrayList<CustomTeamData> aCustomForATeam = defaultCustom();

    // dans l'idee de base le joueur pouvait choisir sa couleur mais j'ai pas le temps 
    // donc ca sera des couleurs fixe pour n equipes
    // pas besoin de rajouter dans la save 
    public static ArrayList<Color> colorByTeam; 

    public static ArrayList<CustomTeamData> defaultCustom(){
        ArrayList<CustomTeamData> defaultC = new ArrayList<>();
        for (int i = 0 ; i < 6 ; i++){ // Car max 6 equipes
            defaultC.add(new CustomTeamData());
        }
        return defaultC;
    }
}

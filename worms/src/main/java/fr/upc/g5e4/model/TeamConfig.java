package fr.upc.g5e4.model;

import java.util.ArrayList;

import fr.upc.g5e4.model.io.CustomTeamData;

public class TeamConfig {
    
    public static int NBR_EQUIPE = 2;

    public static int NBR_WORM_EQUIPE = 4;

    public static ArrayList<CustomTeamData> aCustomForATeam = defaultCustom();



    public static ArrayList<CustomTeamData> defaultCustom(){
        ArrayList<CustomTeamData> defaultC = new ArrayList<>();
        for (int i = 0 ; i < 6 ; i++){ // Car max 6 equipes
            defaultC.add(new CustomTeamData());
        }
        return defaultC;
    }
}

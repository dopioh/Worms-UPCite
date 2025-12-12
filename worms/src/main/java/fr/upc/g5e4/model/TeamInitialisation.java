package fr.upc.g5e4.model;

import fr.upc.g5e4.view.*;
//import fr.upc.g5e4.controller.*;
//import fr.upc.g5e4.model.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import org.jbox2d.dynamics.*;

/**
 * Transfert de l'initialisation des teams (et worms) du GameManager
 * Pour des questions de modularite 
 * @see GameManager
 */
public class TeamInitialisation {
    private TeamModel teamModel;

    private TeamView teamView;

    private int direction = 0;

    private Color color;

    
    private static int SPACING = 50;
    public static ArrayList<Integer> alreadyUsed;
    
    // Prendre le minimum de data necessaire en parametre
    // le reste sera initialise avec Random
    // creer un constructeur sans startX plus tard
    public TeamInitialisation(GroundModel groundModel,World world,String teamName, int startX, int teamNumber){
        boolean d = directionTeam();
        int s = this.startX(groundModel);
        RandomColor();
        teamModel = new TeamModel(groundModel, world, teamName, s, SPACING, teamNumber, false, d, false);
        teamView = new TeamView(teamNumber,color , "/worm.png", d);
    }

    public boolean directionTeam(){
        if (direction%2 == 0){  // Nombre Pair = Droite
            direction++;
            return true;
        }
        direction++;
        return false;
    }

    public int startX(GroundModel groundModel){
        if (directionTeam()){
            return groundModel.getWidth()/4;
        }
        return groundModel.getWidth() - 100;
    }

    public void RandomColor(){
        Integer nbr;
    
        do {
            Random random = new Random(5); // 0,1,2,3,4 Normalement
            nbr = random.nextInt();
            System.out.println("nbr random : " + nbr);

        } while (alreadyUsed.contains(nbr));

        switch (nbr) {
            case 0:
                this.color = Color.RED;
                break;
            case 1 : 
                this.color = Color.BLUE;
                break;
            case 2 : 
                this.color = Color.PINK;
                break;
            case 3 : 
                this.color = Color.GREEN;
                break;
            case 4 : 
                this.color = Color.YELLOW;
                break;
        }
    }
}

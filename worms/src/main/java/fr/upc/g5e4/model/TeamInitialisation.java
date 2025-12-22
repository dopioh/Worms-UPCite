package fr.upc.g5e4.model;

import fr.upc.g5e4.view.*;
//import fr.upc.g5e4.controller.*;
//import fr.upc.g5e4.model.*;

import java.awt.Color;
import java.awt.Graphics2D;
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

    private static int COLORTEAM = 0;

    private String name;

    
    private static int SPACING = 50;
    public static int[] alreadyUsed = new int[5];
    
    // Prendre le minimum de data necessaire en parametre
    // le reste sera initialise avec Random
    // creer un constructeur sans startX plus tard
    public TeamInitialisation(GroundModel groundModel,World world,String teamName, int teamNumber, int startX){
        boolean facingRight = (startX < groundModel.getWidth() / 2);
        this.name = teamName;
        colorGiving();
        teamModel = new TeamModel(groundModel, world, name, startX, SPACING, teamNumber, false, facingRight, false);
        teamView = new TeamView(teamNumber,color, "/worm.png", facingRight);
    }

    public TeamModel getTeamModel(){
        return teamModel;
    }

    public TeamView getTeamView(){
        return teamView;
    }

    public String getName(){
        return this.name;
    }
    
    /*
    public boolean directionTeam(){
        if (direction%2 == 0){  // Nombre Pair = Droite
            direction++;
            return true;
        }
        direction++;
        return false;
    }
     */

    public void colorGiving(){

        if (COLORTEAM >= 5){
            COLORTEAM = 0;
        }

        switch (COLORTEAM) {
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
        COLORTEAM++;
    }

    public void draw(Graphics2D gd){
        teamView.draw(gd, teamModel);
    }
}

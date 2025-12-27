package fr.upc.g5e4.model;

import fr.upc.g5e4.view.*;
//import fr.upc.g5e4.controller.*;
//import fr.upc.g5e4.model.*;

import java.awt.Color;
import java.awt.Graphics2D;
//import java.util.ArrayList;
//import java.util.Random;

import org.jbox2d.dynamics.*;

/**
 * Transfert de l'initialisation des teams (et worms) du GameManager
 * Pour des questions de modularite 
 * @see GameManager
 * 
 * @author Mei
 */
public class TeamInitialisation {
    private TeamModel teamModel;

    private TeamView teamView;

    private Color color;

    private static int COLORTEAM = 0;

    private String name;

    
    private static int SPACING = 30;
    public static int[] alreadyUsed = new int[5];
    
    // Prendre le minimum de data necessaire en parametre
    // le reste sera initialise avec Random
    // creer un constructeur sans startX plus tard
    /**
     * Initialise une instance qui creer un teamModel et TeamView ensemble
     * @param groundModel terrain model
     * @param world physique du jeu
     * @param teamName nom de l'equipe
     * @param teamNumber nombre de worms par equipe (doit etre le meme pour toutes les equipes)
     * @param startX position d'initialisation
     */
    public TeamInitialisation(GroundModel groundModel,World world,String teamName, int teamNumber, int startX){
        this(groundModel, world, teamName, teamNumber, startX, "/wormPixel.png");
    }
    
    /**
     * Initialise une instance qui creer un teamModel et TeamView ensemble avec un skin personnalisé
     * @param groundModel terrain model
     * @param world physique du jeu
     * @param teamName nom de l'equipe
     * @param teamNumber nombre de worms par equipe (doit etre le meme pour toutes les equipes)
     * @param startX position d'initialisation
     * @param skinPath chemin vers le sprite du worm
     */
    public TeamInitialisation(GroundModel groundModel,World world,String teamName, int teamNumber, int startX, String skinPath){
        //System.out.println("Nombre de worms pour " + teamName + "est de : " + teamNumber);
        boolean facingRight = (startX < groundModel.getWidth() / 2);
        this.name = teamName;
        colorGiving();
        teamModel = new TeamModel(groundModel, world, name, startX, SPACING, teamNumber, false, facingRight, false);
        teamView = new TeamView(teamNumber,color, skinPath, facingRight);
        SPACING += 5; // pour que si il y'a plus de 2 equipes, les worms ne se superposent pas
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

    /**
     * Methode interne pour attribuer une couleur a une equipe
     * Au bout de 5 couleurs diff, on reprends des couleurs deja attribuees 
     */
    private void colorGiving(){

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

package fr.upc.g5e4.model;

import fr.upc.g5e4.view.*;
import fr.upc.g5e4.model.InventoryModel.InventoryJson;
//import fr.upc.g5e4.model.InventoryModel.InventoryRoot;
//import fr.upc.g5e4.controller.*;
//import fr.upc.g5e4.model.io.*;
import fr.upc.g5e4.model.io.CharacterData;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
//import java.util.ArrayList;
//import java.util.Random;
import java.util.List;

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
    private int intColor;

    private String name;

    
    private static int SPACING = 25;
    public static int[] alreadyUsed = new int[5];

    private String skinPath;
    
    
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
    public TeamInitialisation(GroundModel groundModel,World world,String teamName, int teamNumber, int startX,ArrayList<String> nameWorms){
        this(groundModel, world, teamName, teamNumber, startX, "/wormPinky.png",nameWorms);
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
    public TeamInitialisation(GroundModel groundModel,World world,String teamName, int teamNumber, int startX, String skinPath,ArrayList<String> nameWorms){
        //System.out.println("Nombre de worms pour " + teamName + "est de : " + teamNumber);
        boolean facingRight = (startX < groundModel.getWidth() / 2);
        this.name = teamName;
        this.skinPath = skinPath;
        this.intColor = COLORTEAM;
        colorGiving(true);
        teamModel = new TeamModel(groundModel, world, name, startX, SPACING, teamNumber, false, facingRight, false,intColor, nameWorms,skinPath);
        teamView = new TeamView(teamNumber,color, skinPath, facingRight);
        //upSpacing(groundModel); // pour que si il y'a plus de 2 equipes, les worms ne se superposent pas
        SPACING += 3;
        //System.out.println("INT COLOR : " + intColor);
        //System.out.println("COLOR COLOR : " + color.toString());
    }

    /**
     * Constructeur pour load une save avec int COLOR et un skin
     * @param groundModel
     * @param world
     * @param TeamID
     * @param teamName
     * @param teamNumber
     * @param characterData
     * @param COLOR
     * @param skinPath
     */
    public TeamInitialisation(GroundModel groundModel,World world,int TeamID ,String teamName, int teamNumber,CharacterData[] characterData, int COLOR, String skinPath,List<InventoryJson> inventory, int money){
        this.name = teamName;
        this.skinPath = skinPath;
        this.intColor = COLOR;
        COLORTEAM = intColor;
        colorGiving(false);
        teamModel = new TeamModel(groundModel, world, teamName, teamNumber, TeamID, characterData,intColor,inventory,skinPath);
        teamView = new TeamView(teamNumber,color, skinPath, true); // facingRight a changer apres
        //System.out.println("INT COLOR SAVE : " + intColor);
        //System.out.println("COLOR COLOR SAVE : " + color.toString());
        teamModel.setMoney(money);
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
    private void colorGiving(boolean augmente){
        //System.out.println("COLORTEAM PDT COLOR GIVING : " + COLORTEAM);
        //System.out.println("COLOR INDI INT PDT COLOR GIVING : " + intColor);
        if (COLORTEAM >= 6){
            COLORTEAM = 0;
        }

        switch (COLORTEAM) {
            case 0:
                this.color = new Color(255, 74, 2);
                break;
            case 1 : 
                this.color = new Color(146, 130, 255);
                break;
            case 2 : 
                this.color = new Color(137, 247, 136);
                break;
            case 3 : 
                this.color = new Color(253, 91, 191);
                break;
            case 4 : 
                this.color = new Color(100, 227, 114);
                break;
            case 5 : 
            this.color =new Color(255, 255, 56);
                break;
        }
        if (augmente){
            COLORTEAM++;
        }
    }

    public void draw(Graphics2D gd){
        teamView.draw(gd, teamModel);
    }

}

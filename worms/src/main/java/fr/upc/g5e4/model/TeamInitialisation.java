package fr.upc.g5e4.model;

import fr.upc.g5e4.view.*;
import fr.upc.g5e4.model.InventoryModel.InventoryJson;
import fr.upc.g5e4.model.InventoryModel.InventoryRoot;
//import fr.upc.g5e4.controller.*;
import fr.upc.g5e4.model.io.*;
import fr.upc.g5e4.model.io.CharacterData;

import java.awt.Color;
import java.awt.Graphics2D;
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

    private String name;

    
    private static int SPACING = 25;
    public static int[] alreadyUsed = new int[5];

    private String skinPath;
    
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
        this.skinPath = skinPath;
        colorGiving();
        teamModel = new TeamModel(groundModel, world, name, startX, SPACING, teamNumber, false, facingRight, false,COLORTEAM, skinPath);
        teamView = new TeamView(teamNumber,color, skinPath, facingRight);
        SPACING += 3; // pour que si il y'a plus de 2 equipes, les worms ne se superposent pas
    }
    

    /**
     * Constructeur pour load une sauvegarde
     * @param groundModel
     * @param world
     * @param teamName
     * @param teamNumber
     * @param characterData tableau des data des worms
     */
    public TeamInitialisation(GroundModel groundModel,World world,int TeamID ,String teamName, int teamNumber,CharacterData[] characterData, Color color, String skinPath, List<InventoryJson> inventory){
        this.name = teamName;
        this.color = color;
        this.skinPath = skinPath;
        teamModel = new TeamModel(groundModel, world, teamName, teamNumber, TeamID, characterData,COLORTEAM, inventory,skinPath);
        teamView = new TeamView(teamNumber,color, skinPath, true); // facingRight a changer apres
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
    public TeamInitialisation(GroundModel groundModel,World world,int TeamID ,String teamName, int teamNumber,CharacterData[] characterData, int COLOR, String skinPath,List<InventoryJson> inventory){
        this.name = teamName;
        this.skinPath = skinPath;
        COLORTEAM = COLOR;
        colorGiving();
        teamModel = new TeamModel(groundModel, world, teamName, teamNumber, TeamID, characterData,COLORTEAM,inventory,skinPath);
        teamView = new TeamView(teamNumber,color, skinPath, true); // facingRight a changer apres
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

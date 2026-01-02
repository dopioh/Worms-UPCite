package fr.upc.g5e4.model.io;

import fr.upc.g5e4.model.*;

import org.jbox2d.dynamics.*;


/**
 * Recupere les donnees essentiels
 * pour recreer une partie 
 * avec un fichier sauvegarde
 * En utilisant Gson et Json
 */
public class CharacterData {
    private int ID;
    private String name;

    private double xcord;   // En pixel
    private double ycord;

    private double health;
    private double shield;

    // on met pas speed psq je pars du principe que sans gadget temp 
    // tous on la meme vitesse

    private boolean isDead;

    private boolean facingRight;

    /**
     * 
     * @param ID ID du CharaModel
     * @param name nom du CharaModel
     * @param xcord sa position X de depart
     * @param ycord sa position Y de depart
     * @param health ses points de vie
     * @param shield ses points de bouclier
     * @param isDead son statut (mort ou vivant)
     */
    public CharacterData(int ID, String name, double xcord, double ycord, double health, double shield, boolean isDead,boolean facingRight){
            this.ID = ID;
            this.name = name;
            this.xcord =xcord;
            this.ycord =ycord;
            this.health = health;
            this.shield = shield;
            this.isDead = isDead;
            this.facingRight = facingRight;
    }

    public int getID(){
        return ID;
    }

    public String getName(){
        return name;
    }

    public double getXcord(){
        return xcord;
    }

    public double getYcord(){
        return ycord;
    }

    public double getHealth(){
        return health;
    }

    public double getShield(){
        return shield;
    } 

    public boolean getIsDead(){
        return isDead;
    }

    public boolean getFacingRight(){
        return this.facingRight;
    }

    public CharacterModel dataToModel(GroundModel model, World world){
        CharacterModel cm = new CharacterModel(model, world, ID, name, xcord, ycord, health, shield, isDead,facingRight);
        return cm;
    }
}

package fr.upc.g5e4.model.io;

import fr.upc.g5e4.model.*;
//import fr.upc.g5e4.model.CharacterModel.StatusEffect;

//import java.lang.reflect.Array;
import java.util.ArrayList;

import org.jbox2d.dynamics.*;


/**
 * Classe pour sauvegarder un CharacterModel
 * En version minimal
 * Pour la sauvegarde
 * 
 * @author Mei
 */
public class CharacterData {
    private int ID;
    private String name;

    private double xcord;   // En pixel
    private double ycord;

    private double health;
    private double shield;

    private double jump;
    private double speed;

    private boolean isDead;

    private boolean facingRight;

    // buff worm
    private ArrayList<BuffModel> activeBuffs;

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
    public CharacterData(int ID, String name, double xcord, double ycord, double health, double shield, boolean isDead,boolean facingRight,double jump,double speed, ArrayList<BuffModel> activeBuffs){
            this.ID = ID;
            this.name = name;
            this.xcord =xcord;
            this.ycord =ycord;
            this.health = health;
            this.shield = shield;
            this.isDead = isDead;
            this.facingRight = facingRight;
            this.speed = speed;
            this.jump = jump;
            this.activeBuffs = activeBuffs;
    }

    /**
     * Constructeur pour les tests unitaires
     * @param ID
     * @param name
     * @param xcord
     * @param ycord
     * @param health
     * @param shield
     * @param isDead
     * @param facingRight
     * @param jump
     * @param speed
     */
    public CharacterData(int ID, String name, double xcord, double ycord, double health, double shield, boolean isDead,boolean facingRight,double jump,double speed){
            this.ID = ID;
            this.name = name;
            this.xcord =xcord;
            this.ycord =ycord;
            this.health = health;
            this.shield = shield;
            this.isDead = isDead;
            this.facingRight = facingRight;
            this.speed = speed;
            this.jump = jump;
            this.activeBuffs = null;
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
        CharacterModel cm = new CharacterModel(model, world, ID, name, xcord, ycord, health, shield, isDead,facingRight,speed,jump,activeBuffs);
        return cm;
    }
}

package fr.upc.g5e4.model;

import java.util.HashMap;

/**
 * Classe pour les Données du joueur (Model)
 */
public class PlayerData {
    
    private String name;
    private int xp;
    private int money;

    private  HashMap<String,SkinModel> listSkinPlayer;

    /**
     * Constructeur de la classe DoneesJoueur avec MapSkinModel
     * @param name nom du joueur
     * @param xp points d'expérience du joueur
     * @param money argent du joueur
     * @param listSkinPlayer list des skin du jeu (lock and unlocked)
     */
    public PlayerData(String name, int xp, int money, HashMap<String,SkinModel> listSkinPlayer ) {
        this.name = name;
        this.xp = xp;
        this.money = money;
        this.listSkinPlayer = listSkinPlayer;
    }

    /**
     * Constructeur dans MapSkinModel
     * @param name
     * @param xp
     * @param money
     */
    public PlayerData(String name, int xp, int money){
        this(name, xp, money, null);
    }

    // Getters 
    public String getName() {
        return name;
    }

    public int getXp() {
        return xp;
    }

    public int getMoney() {
        return money;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }
    
    public void setXp(int xp) {
        this.xp = xp;
    }
    
    public void setMoney(int money) {
        this.money = money;
    }

    /**
     * methode toString pour afficher les info du joueur
     * @return un string avec les info du joueur
     */
    @Override
    public String toString() {
        return "Nom " + name + ", Argent: " + money + ", XP: " + xp;
    }
}
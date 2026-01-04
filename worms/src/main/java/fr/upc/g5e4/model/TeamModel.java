package fr.upc.g5e4.model;

//import java.util.ArrayList;

import org.jbox2d.dynamics.World;

import java.util.ArrayList;
import java.util.List;
import fr.upc.g5e4.model.InventoryModel.InventoryJson;

import fr.upc.g5e4.model.io.*;
import fr.upc.g5e4.model.io.CharacterData;

//import java.awt.Color;

/**
 * Modèle pour une équipe de worms
 */
public class TeamModel {
    private int teamID; // ID de l'equipe, peut pas etre final sinon on peut pas load une partie
    private String teamName;

    private int number; // nbr de worms dans l'equipe
    private CharacterModel[] tabModelWorms;
    

    private static int TEAMSNUMBER = 0;

    private int COLORTEAM; // permet de le stocker dans sauvegarde.txt

    private List<InventoryJson> inventory;

    private boolean facingRight;

    private String skinPath;
    
    private int money;
    
    /**
     * Constructeur de la classe TeamModel avec initialisation automatique des worms
     * @param groundModel Le modèle du terrain
     * @param world Le monde physique JBox2D
     * @param teamName Le nom de l'équipe
     * @param startX La position X de départ pour le premier worm
     * @param spacing L'espacement entre chaque worm
     * @param wormNumber Le nombre de worms dans l'équipe
     * @param console Mode console (debug)
     * @param facingRight Direction initiale des worms (true = droite, false = gauche)
     * @param withCollision Active les collisions entre worms (true = avec collisions, false = sans)
     */
    public TeamModel(GroundModel groundModel, World world, String teamName, double startX, double spacing, int wormNumber, boolean console, boolean facingRight, boolean withCollision,int COLORTEAM,ArrayList<String> nameWorms,String skinPath) {
        if (wormNumber < 1) {
            throw new IllegalArgumentException("Une équipe doit avoir au moins 1 worm");
        }
        this.facingRight = facingRight;
        this.teamName = teamName;
        this.number = wormNumber;
        this.tabModelWorms = new CharacterModel[number];
        this.COLORTEAM = COLORTEAM;
        this.money = 0;

        TEAMSNUMBER++;
        this.teamID = TEAMSNUMBER;
        this.skinPath = skinPath;
        
        // Initialisation automatique des worms et position espacée
        for (int i = 0; i < number; i++) {
            double xPosition = startX + (i * spacing);
            double yPosition = groundModel.getHeight() - 600;

            String wormName;
            if (nameWorms == null){
                wormName = "bot";
            } else {
                wormName = nameWorms.get(i);
                System.out.println(wormName);
            }
            
            
            tabModelWorms[i] = new CharacterModel(
                groundModel, 
                world, 
                wormName, 
                xPosition, 
                yPosition, 
                100.0,  // health
                50.0,   // shield
                4,      // speed
                5.0,     // jump
                console,
                withCollision
            );
            
            // Définir la direction initiale
            tabModelWorms[i].setFacingRight(facingRight);
        }
    }

    /**
     * Constructeur pour le load d'une sauvegarde 
     */
    public TeamModel(GroundModel groundModel, World world, String teamName, int wormNumber, int teamID, CharacterData[] characterDatas,int COLORTEAM,List<InventoryJson> inventory , String skinPath){
        this.teamName = teamName;
        this.number = wormNumber;
        this.teamID = teamID;
        this.COLORTEAM = COLORTEAM;
        this.inventory = inventory;
        this.skinPath = skinPath;
        this.money = 0;
        
        this.tabModelWorms = new CharacterModel[number];
        for (int i = 0 ; i < tabModelWorms.length ; i++){
            tabModelWorms[i] = characterDatas[i].dataToModel(groundModel, world);
        }
    }
    
    // Getters et Setters   
    public String getTeamName(){
        return teamName;
    }

    public CharacterModel[] getTabWorms() {
        return tabModelWorms;
    }

    public CharacterModel getWorm(int index) {
        if (index < 0 || index >= number) {
            throw new IndexOutOfBoundsException("Hors tableau: " + index);
        }
        return tabModelWorms[index];
    }

    public void removeWorm(int index) {
        if (index < 0 || index >= number) {
            throw new IndexOutOfBoundsException("Hors tableau: " + index);
        }
        tabModelWorms[index] = null;
    }

    public void addWorm(CharacterModel worm, int index) {
        if (index < 0 || index >= number) {
            throw new IndexOutOfBoundsException("Hors tableau: " + index);
        }
        if (tabModelWorms[index] != null) {
            throw new IllegalArgumentException("Worm déjà placé à l'index: " + index);
        }
        tabModelWorms[index] = worm;
    }
    
    // recuperer le nombre de worm a l'initialisation de la team
    public int getWormNumberAtStart() {
        return number;
    }

    public int getAliveWormNumber() {
        int aliveCount = 0;
        for (CharacterModel worm : tabModelWorms) {
            if (worm != null && !worm.isDead()) {
                aliveCount++;
            }
        }
        return aliveCount;
    }


    // Getter Data
    public TeamData toData(){
        return new TeamData(teamID, number, tabModelWorms,teamName,COLORTEAM, inventory, money,skinPath);
    }
    

    // Setter Data
    public void loadFromData(TeamData teamData){
        this.teamID = teamData.getTeamID();
        this.number = teamData.getWormsNumber();
    
    }

    
    public void setInventory(List<InventoryJson> inventory) {
        this.inventory = inventory;
    }

    public List<InventoryJson> getInventory() {
        return inventory;
    }

    // Remet useThisTurn = 0 pour tous les items de l'équipe (nouveau tour pour cette team)
    public void resetInventoryTurn() {
        if (inventory == null) return;
        for (InventoryJson item : inventory) {
            item.resetTurn();
        }
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void addMoney(int amount) {
        this.money += amount;
    }

    public void removeMoney(int amount) {
        this.money -= amount;
    }
}

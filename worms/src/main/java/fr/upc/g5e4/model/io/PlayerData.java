package fr.upc.g5e4.model.io;


//import java.lang.reflect.Array;
import java.util.ArrayList;

import fr.upc.g5e4.model.SkinModel;

/**
 * Classe pour les Données du joueur (Model)
 */
public class PlayerData {
    
    private String name;
    private int xp;
    private int money;

    //private HashMap<String,SkinModel> listSkinPlayer;

    private SkinModel[] tabSkinPlayer;

    private CustomGameManager customTeamManager;

    // rajouter l'attribut CustomTeamManager ici

    /**
     * Constructeur de la classe DoneesJoueur avec MapSkinModel
     * @param name nom du joueur
     * @param xp points d'expérience du joueur
     * @param money argent du joueur
     * @param listSkinPlayer list des skin du jeu (lock and unlocked)
     */
    public PlayerData(String name, int xp, int money, SkinModel[] tabSkinPlayer,CustomGameManager customTeamManager) {
        this.name = name;
        this.xp = xp;
        this.money = money;
        this.tabSkinPlayer = tabSkinPlayer;
        this.customTeamManager = customTeamManager;
    }

    /**
     * Constructeur pour un PlayerData null 
     * Dans le cas ou c'est la premiere fois que le joueur lance le jeu
     */
    public PlayerData(String name){
        this.name = name;
        this.xp = 0;
        this.money = 0;
        createtanSkinFULLLOCK();
        this.customTeamManager = new CustomGameManager();
    }

    public ArrayList<String> onlyUnlocked(){
        ArrayList<String> unlocked = new ArrayList<>();
        for (int i = 0 ; i < tabSkinPlayer.length ; i++){
            if (tabSkinPlayer[i].isUnlocked()){
                unlocked.add(tabSkinPlayer[i].getName());
            }
        }
        return unlocked;
    }

    public SkinModel findSkinModelByName(String name){
        for (SkinModel sk : tabSkinPlayer){
            if (sk.getName().equals(name)){
                return sk;
            }
        }
        return tabSkinPlayer[0]; // dans le cas ou il trouve pas le skin, il renvoie celui par defaut (le premier)
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

    public SkinModel[] getTabSkinPlayer(){
        return this.tabSkinPlayer;
    }

    public CustomGameManager getCustomTeamManager(){
        return this.customTeamManager;
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

    public void setTabSkinModel(SkinModel[] tabSkinPlayer){
        this.tabSkinPlayer = tabSkinPlayer;
    }

    public void setCustomTeamManager(CustomGameManager customTeamManager) {
        this.customTeamManager = customTeamManager;
    }

    /**
     * methode toString pour afficher les info du joueur
     * @return un string avec les info du joueur
     */
    @Override
    public String toString() {
        return "Nom " + name + ", Argent: " + money + ", XP: " + xp;
    }

    /**
     * Initialise tous les skin du jeu pour un nouveau Joueur
     * Ils sont tous bloquer pour le joueur sauf le premier 
     * Car c'est le skin de base
     */
    public void createtanSkinFULLLOCK(){
        this.tabSkinPlayer = new SkinModel[6]; // 6 Skin, le premier est unlocked car c'est celui de base
        
        // Obliger d'initialiser a la main sans boucle car chaque SkinModel a des param diff pour chaque
        tabSkinPlayer[0] = new SkinModel("Worm de base","/wormPixel.png", true);
        tabSkinPlayer[1] = new SkinModel("Worm Noel Edition","/wormNoel.png", false);
        tabSkinPlayer[2] = new SkinModel("Worm Pinky Edition","/wormPinky.png", false);
        tabSkinPlayer[3] = new SkinModel("Worm Nerd Edition","/wormNerd.png", false);
        tabSkinPlayer[4] = new SkinModel("Worm Cuisto Edition","/wormCuisto.png", false);
        tabSkinPlayer[5] = new SkinModel("Worm Costar Edition","/wormCostar.png", false);
        //tabSkinPlayer[6] = new SkinModel("Base","/wormPixel.png", false); // on supp 1 skin car pas de battlepass

    }
}
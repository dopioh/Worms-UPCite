// File: fr/upc/g5e4/model/GameManager.java
package fr.upc.g5e4.model;

//import java.awt.Color;
import java.util.ArrayList;
import javax.swing.Timer;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import fr.upc.g5e4.GameLauncher;
import fr.upc.g5e4.controller.*;
//import fr.upc.g5e4.model.*;
import fr.upc.g5e4.model.weapons.Ak47;
import fr.upc.g5e4.model.weapons.BaseballBat;
import fr.upc.g5e4.model.weapons.Grenade;
import fr.upc.g5e4.model.weapons.RocketLauncher;
import fr.upc.g5e4.model.PhysicsWorld.WaterSensorListener;
import fr.upc.g5e4.model.camera.CameraModel;
import fr.upc.g5e4.model.io.SaveData;
import fr.upc.g5e4.model.io.TeamData;
//import fr.upc.g5e4.view.CharacterView;
import fr.upc.g5e4.view.GamePanel;
import fr.upc.g5e4.view.ItemView;
//import fr.upc.g5e4.view.TeamView;
import fr.upc.g5e4.view.InventoryOverlay;

import java.io.*;
import com.google.gson.*;

/**
 * Cette classe servira pour instancier tout ce dont le jeu (une partie de jeu)
 * a besoin
 */
public class GameManager {
    private final GamePanel gamePanel;
    private GroundModel model;
    private final PhysicsWorld physic;
    private final World world;
    private final Body water;
    private final WaterSensorListener wSL;
    private SwingInputController imputController;
    private CameraModel camera; // AJOUT RAPHAËL
    private CameraController cameraController; // AJOUT RAPHAËL

    // Team Initialisation = Team Model + Team View
    private ArrayList<TeamInitialisation> listTeamInital;

    private ArrayList<TeamModel> listTeamModel; // toutes les TeamModel dans une seule liste

    private int nbrTeams = 0;
    private int nbrWormsByTeam;

    private int currentTeamIndex;
    

    private final ItemController itemController;
    private final CharacterModel[] allWorms;

    private int activeWormIndex;
    private CharacterController activeWormController;

    private int[] teamCursor; // stock l'index du dernier worm jouee pour chaque equipe 

    private TeamModel currentTeam;

    private int currentWeaponIndex = 0;  // 0=AK47, 1=Baseball, 2=Grenade, 3=Rocket    

    // ajout minimal: gestion des rounds
    private int roundNumber = 1;
    //private int currentTeamIndex = 0; // 0 = Team1, 1 = Team2

    // Timer : 
    private int roundTimeRemaining = 1800;
    private int roundTimeSave = 1800;

    
    private InventoryOverlay inventoryOverlay;

    /**
     * Initialise le terrain du jeu.
     * <p>
     * La taille du terrain est pour le moment la même que celle de la fenêtre.
     * La valeur {@code groundY} correspond à la hauteur en pixels Y
     * (selon le repère Swing/AWT et non JBox2D).
     * X est calculé comme la division de {@code groundY.length} par un indice
     * {@code i}
     * pour {@code i} allant de 0 à {@code groundY.length}.
     * </p>
     *
     * @see GameLauncher
     * @see PhysicsWorld#createGroundFromModel()
     */
    public GameManager(int nbrEquipe) {
        int sizeX = 2400;  // Taille du terrain
        int sizeY = 720;
        
        // generation procédurale du terrain avec seed (le temps de la machine)
        TerrainGenerator generator = new TerrainGenerator(System.currentTimeMillis());
        int[] groundY = generator.generateTerrainWithPlatforms(400, 70, 900, 200, 30);

        /*
         * Ancienne méthode : terrain manuel
         * int[] groundY = {470,450,430,420,410,400,390,370,340,300,280,250,230,200};
         * 
         * Plusieurs autres valeurs de groundY si vous voulez tester plusieurs terrains :
         * int[] groundY = {400,420,450,450,400,300,530,450,350,300,250,230,200,230};
         * int[] groundY = {200,230,250,280,300,340,370,390,400,410,420,430,450,470};
         * int[] groundY = {470,450,430,420,410,400,390,370,340,300,280,250,230,200};
         * int[] groundY = {400,420,450,450,400,700,930,450,350,300,250,230,200,200};
         */

        this.model = new GroundModel(groundY, sizeX, sizeY, 500);

        this.physic = new PhysicsWorld();
        this.world = physic.getWorld();
        physic.createGroundFromModel(model);
        this.water = physic.createBodyWater(model);

        // initialisation caméra et contrôleur (ON PREND LES COORDONNEES DE LA FENETRE)
        this.camera = new CameraModel(1280, 720, model.getWidth(), model.getHeight(), 1);
        physic.limitPanel(model);

        // On initialise les liste de Teams
        this.listTeamModel = new ArrayList<>();
        this.listTeamInital = new ArrayList<>();

        // initialise le nbr de worm par equipe
        this.nbrWormsByTeam = 3;

        // creation des equipes selon le nombre que on veut
        createTeam(nbrEquipe);

        // initialisation des index 
        currentTeamIndex = 0;
        this.currentTeam = listTeamInital.get(currentTeamIndex).getTeamModel();
        this.teamCursor = new int[nbrEquipe];

        this.activeWormIndex = 0;
        this.activeWormController = new CharacterController(listTeamInital.get(currentTeamIndex).getTeamModel().getWorm(activeWormIndex));


        
        // Créer une liste de tous les worms pour le WaterSensorListener
        ArrayList<CharacterModel> allWorms = new ArrayList<>();
        for (int i = 0; i < listTeamInital.size(); i++) {
            for (int j = 0 ; j < listTeamInital.get(i).getTeamModel().getTabWorms().length; j++){
                allWorms.add(listTeamInital.get(i).getTeamModel().getTabWorms()[j]);
            }
        }
        
        this.wSL = physic.new WaterSensorListener(water, allWorms, new ArrayList<CharacterModel>());
        world.setContactListener(wSL);
        
        
        // Initialisation du système de tir
        this.itemController = new ItemController(new ItemView());
        this.itemController.setWorld(world);
        
        // Créer un tableau de tous les worms (N equipes)
        this.allWorms = new CharacterModel[allWorms.size()];
        for (int i = 0 ; i < this.allWorms.length ; i++){
            this.allWorms[i] = allWorms.get(i);
        }

        
        this.itemController.setAllWorms(this.allWorms);
        activeWormController.setItemController(this.itemController);
        activeWormController.setWorld(world);
        activeWormController.setAllWorms(this.allWorms);
        
        // Équiper une arme par défaut (AK-47)
        activeWormController.setCurrentWeapon(new Ak47());
        
        // Initialisation caméra avec le worm actif (fenêtre 1280x720, terrain 6000x720)
        this.cameraController = new CameraController(camera,listTeamInital.get(currentTeamIndex).getTeamModel().getWorm(activeWormIndex));
        
        // Centrer la caméra sur le worm actif au démarrage
        this.cameraController.centerOnCharacter();
        
        // Contrôleur fusionné avec le worm actif
        this.imputController = new SwingInputController(activeWormController, cameraController);

        // GamePanel avec tout ce qui est nécessaire
        this.gamePanel = new GamePanel(model, camera, cameraController, listTeamInital); 
        
        // Passer l'ItemController au GamePanel pour dessiner les projectiles
        this.gamePanel.setItemController(this.itemController);

        // Ajoute le contrôleur pour capter les touches
        this.gamePanel.add(imputController);
        imputController.setGameManager(this); // connecte inputController au GameManager
                                             //  pour que inputController puisse appeler switchToNextWorm()
        
        // Connecter le renderer de trajectoire
        imputController.setTrajectoryRenderer(gamePanel.getTrajectoryRenderer());
        gamePanel.getTrajectoryRenderer().setShooter(activeWormController.getModel());
        gamePanel.getTrajectoryRenderer().setWeapon(activeWormController.getCurrentWeapon());
        
        // pour ne pas que gamePanel et imputController se disputent le focus
        // en lancant le jeu, certaines fois rien n'était controllable mais c'est fix maintenant
        this.gamePanel.setFocusable(false);  // le panel ne prend pas le focus
        imputController.setFocusable(true);   // l'inputController prend le focus    
        

        // Timer pour update régulier de la caméra, projectiles et repaint
        new Timer(16, e -> {
            cameraController.update();
            // mettre à jour les projectiles (16ms = 0.016s par frame)
            itemController.updateProjectiles(0.016f);
            gamePanel.repaint();
        }).start();
    }

    /**
     * Tous les getters qui suivent ont été ajouter psq j'en avais besoin sur le
     * moment
     * Il en manque donc que vous pouvez rjt si besoin
     */
    public World getWorld() {
        return this.world;
    }

    public WaterSensorListener getWSL() {
        return this.wSL;
    }

    public GroundModel getGroundModel() {
        return this.model;
    }

    public GamePanel getGamePanel() {
        return this.gamePanel;
    }

    public SwingInputController getImputController() {
        return this.imputController;
    }
    
    public CameraController getCameraController() { 
        return this.cameraController;
    }
    
    public CameraModel getCamera() {
        return this.camera;
    }
    
    public int getActiveWormIndex() {
        return this.activeWormIndex;
    }
    
    public CharacterController getActiveWormController() {
        return this.activeWormController;
    }

    public int getRoundNumber() {
        return this.roundNumber;
    }

    public int getCurrentTeamIndex() {
        return this.currentTeamIndex;
    }


    public ArrayList<TeamModel> getlistTeams(){
        System.out.print(listTeamModel.toString()); 
        return listTeamModel;
    }

    public ArrayList<TeamInitialisation> getListTeamInit(){
        return listTeamInital;
    }

    public int getRoundTimeRemaining(){
        return this.roundTimeRemaining;
    }
    public int getRoundTimeSave(){
        return this.roundTimeSave;
    }

    public void setRoundTimeSave(int time){
        this.roundTimeSave = time;
    }

    public TeamModel getCurrentTeam(){
        return this.currentTeam;
    }

    public String getCurrentTeamName(){
        return listTeamInital.get(currentTeamIndex).getName();
    }


    // switch généralisé pour une équipe donnée 
    // adaptation de la methode pour N equipe avec un tableau
    public void switchToNextWorm(TeamModel team) {
        // au lieu de pointer sur activeWormIndex, pointe sur un tableau
        int cursor = teamCursor[currentTeamIndex];  // pointe sur la team actuelle
        int startIndex = cursor;
        int wormCount = team.getWormNumberAtStart();
    
        do {
            cursor = (cursor + 1) % wormCount;  // divise la team par le nbr de worms
        
            CharacterModel worm = team.getWorm(cursor);
        
            if (worm != null && !worm.isDead()) {
                teamCursor[currentTeamIndex] = cursor; //on ecrit dans le tableau que le dernier worm jouee pour cette equipe c'est celui la 
                this.activeWormIndex = cursor;
            
                activeWormController = new CharacterController(worm);
                activeWormController.setItemController(this.itemController);
                activeWormController.setWorld(world);
                activeWormController.setAllWorms(this.allWorms);
                activeWormController. setCurrentWeapon(new Ak47());

                imputController.setCharacterController(activeWormController);
                cameraController.setTargetCharacter(worm);
                cameraController.centerOnCharacter();
            
                gamePanel.getTrajectoryRenderer().setShooter(worm);
                gamePanel. getTrajectoryRenderer().setWeapon(activeWormController.getCurrentWeapon());
            
                return;
            }   
        
            if (cursor == startIndex) {
                System.out.println("Aucun worm vivant disponible dans cette équipe.. .");
                return;
            }
        } while (true);
    }

    /**
     * passe au round suivant et alterne les équipes
     */
    public void nextRound() {
    roundNumber++;
    currentTeamIndex = (currentTeamIndex + 1) % listTeamInital.size(); 
    currentTeam = listTeamInital.get(currentTeamIndex).getTeamModel();
    
    switchToNextWorm(currentTeam);
    System.out.println("Round " + roundNumber + " - Equipe active :  Team " + (currentTeamIndex + 1));
    }

    
    /**
     * Change l'arme équipée du worm actif (cycliquement)
     * AK-47 → Baseball Bat → Grenade → Rocket Launcher → AK-47...
     */
    public void switchWeapon() {
        currentWeaponIndex = (currentWeaponIndex + 1) % 4;  // Boucle entre 0, 1, 2, 3
        
        Weapon newWeapon = null;
        
        switch(currentWeaponIndex) {
            case 0:
                newWeapon = new Ak47();
                System.out.println("AK-47");
                break;
            case 1:
                newWeapon = new BaseballBat();
                System.out.println("Baseball Bat");
                break;
            case 2:
                newWeapon = new Grenade();
                System.out.println("Grenade");
                break;
            case 3:
                newWeapon = new RocketLauncher();
                System.out.println("Rocket Launcher");
                break;
        }
        
        if (newWeapon != null && activeWormController != null) {
            activeWormController.setCurrentWeapon(newWeapon);
            // mettre à jour la trajectoire avec la nouvelle arme
            gamePanel.getTrajectoryRenderer().setWeapon(newWeapon);
        }
    }

    // Initialiser un nombre N d'equipes
    public void createTeam(int number){
        int mapWidth = model.getWidth();
        int margin = 100; // Marge pour ne pas spawner au bord
        int usableWidth = mapWidth - (2 * margin); // Espace utilisable

        int index = 0;

        for (int i = 0; i < number; i++){
            this.nbrTeams++;

            // Répartir les équipes uniformément sur la map
            //int startX = margin + sectionWidth * (i + 1);

            int startX;

            if (index % 2 == 0){
                startX = margin;
            } else {
                startX = usableWidth;
            }

            index++;

            String name = "Team " + this.nbrTeams;

            TeamInitialisation temp = new TeamInitialisation(model, world, name, this.nbrWormsByTeam, startX);
    
            listTeamInital.add(temp);
            listTeamModel.add(temp.getTeamModel());
        }
    }

    // SAUVEGARDE :

    /**
     * Methode appelee lorsqu'on appuie sur le bouton save
     */
    public void save(){
        // creer une sauvegarde automatiquement
        SaveData saveData = new SaveData(this);

        try {
            File dossier = new File("sauvegardes");
            File file = new File(dossier, "sauvegarde.txt");
            FileWriter fileWriter = new FileWriter(file);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(saveData,fileWriter);
            System.out.println(file.getAbsolutePath());
            System.out.println(file.getCanonicalPath());
            System.out.println("Partie sauvegardee !");

            fileWriter.close();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * Charge la game a partir d'un fichier json (.txt lu en json)
     * @param filePath chemin du fichier json
     */
    public void loadGame(String filePath){
        try {
            Gson gson = new Gson();
            FileReader fileReader = new FileReader(filePath);

            SaveData saveData = gson.fromJson(fileReader, SaveData.class);

            //// On remplace :

            // Donnees Partie :
            this.roundNumber = saveData.getRound();
            this.currentTeamIndex = saveData.getCurrentTeamIndex();
            this.activeWormIndex = saveData.getActiveWormIndex();
            this.roundTimeRemaining = saveData.getRoundTimeRemaining();

            // Terrain :
            this.model = saveData.getGroundModel();

            // Worms : 

            // Comment attribuer les equipes ? -> Le code n'est pas modulable pour les equipes 
            // A corriger Sprint 4

            for (TeamData td : saveData.getListTeamData()){
                listTeamModel.add(td.dataToModel(model, world));
            }

            // a finir 
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * Sauvegarde la partie
     * Est called dans le controller
     */
    public void actionPerformed(){
        save();
        //System.out.println("JSON Creer");
    }

    /*Selection d'inventaire depuis l'inventaire*/
    public void setWeaponFromInventory(String itemName) {
        Weapon newWeapon = null;

        switch (itemName) {
            case "Ak47":
            case "AK47":
                newWeapon = new Ak47();
                System.out.println("[Inventaire] Arme équipée depuis inventaire : AK-47");
                break;
            case "BaseballBat":
                newWeapon = new BaseballBat();
                System.out.println("[Inventaire] Arme équipée depuis inventaire : Baseball Bat");
                break;
            case "Grenade":
                newWeapon = new Grenade();
                System.out.println("[Inventaire] Arme équipée depuis inventaire : Grenade");
                break;
            case "RocketLauncher":
                newWeapon = new RocketLauncher();
                System.out.println("[Inventaire] Arme équipée depuis inventaire : Rocket Launcher");
                break;
            default:
                System.out.println("[Inventaire] Aucun mapping d’arme pour : " + itemName);
                break;
        }

        if (newWeapon != null && activeWormController != null) {
            activeWormController.setCurrentWeapon(newWeapon);
            // Met à jour la trajectoire
            gamePanel.getTrajectoryRenderer().setWeapon(newWeapon);
        }
    }

    public java.util.List<InventoryModel.InventoryJson> getCurrentTeamInventory() {
        TeamModel team = getCurrentTeam();
        return (team != null) ? team.getInventory() : null;
    }

    public void setInventoryOverlay(InventoryOverlay overlay){
        this.inventoryOverlay = overlay;
    }

    private void resetCurrentTeamInventoryTurn() {
        TeamModel team = getCurrentTeam();
        if (team != null) {
            team.resetInventoryTurn();
        }
    }

}

// File: fr/upc/g5e4/model/GameManager.java
package fr.upc.g5e4.model;

//import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Timer;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import fr.upc.g5e4.GameLauncher;
import fr.upc.g5e4.controller.*;
//import fr.upc.g5e4.model.WindModel;
import fr.upc.g5e4.model.weapons.*;
import fr.upc.g5e4.model.InventoryModel.InventoryJson;
import fr.upc.g5e4.model.InventoryModel.InventoryRoot;
import fr.upc.g5e4.model.PhysicsWorld.WaterSensorListener;
//import fr.upc.g5e4.model.WindModel.Dir;
import fr.upc.g5e4.model.camera.CameraModel;
import fr.upc.g5e4.model.io.SaveData;
import fr.upc.g5e4.model.io.TeamData;
//import fr.upc.g5e4.view.CharacterView;
import fr.upc.g5e4.view.GamePanel;
import fr.upc.g5e4.view.ItemView;
import fr.upc.g5e4.view.TeamView;
//import fr.upc.g5e4.view.TeamView;
import fr.upc.g5e4.view.InventoryOverlay;
import fr.upc.g5e4.view.HUDOverlay;

import java.io.*;
import com.google.gson.*;

/**
 * Cette classe servira pour instancier tout ce dont le jeu (une partie de jeu)
 * a besoin
 */
public class GameManager {
    private final GamePanel gamePanel;
    private GroundModel model;  // Gardé pour compatibilité avec TeamModel
    private BlockGridModel blockGridModel;  // Nouveau système de terrain en blocs
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
    private HUDOverlay hudOverlay;
    private fr.upc.g5e4.view.WindOverlay windOverlay;

    private final WindModel windModel = new WindModel();
    private final Random windRng = new Random();
    
    // Statistiques de jeu - support pour un nombre variable d'équipes
    private Map<String, GameStats> teamStats = new HashMap<>();

    //private boolean saveDataBool;
    //private SaveData saveData;

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
        // Initialiser le système audio en arrière-plan pour éviter le lag au premier tir
        //this.saveDataBool = false;
        SoundPlayer.initialize();

        // ========== CONFIGURATION DU TERRAIN ==========
        // Pour changer le type de terrain et la taille, voir MapConfig.java
        int sizeX = MapConfig.getMapWidth();
        int sizeY = MapConfig.getMapHeight();
        // Eau uniquement pour le mode ISLANDS
        int waterLevel = (MapConfig.getMapType() == MapConfig.MapType.ISLANDS) ? 480 : sizeY + 100;
        // =============================================
        
        // generation procédurale du terrain avec seed (le temps de la machine)
        TerrainGenerator generator = new TerrainGenerator(System.currentTimeMillis());
        int[] groundY = generator.generateTerrainByType(
            200,          // nombre de points pour la génération
            250,          // hauteur minimale (depuis le haut)
            650           // hauteur maximale
        );
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

        // création du BlockGridModel (système de terrain en blocs)
        this.blockGridModel = new BlockGridModel(sizeX, sizeY, waterLevel);
        blockGridModel.generateWithGroundY(groundY);
        
        // on garde le GroundModel pour compatibilité avec TeamModel
        this.model = new GroundModel(groundY, sizeX, sizeY, waterLevel);

        this.physic = new PhysicsWorld();
        this.world = physic.getWorld();
        
        // nouveau sol
        physic.createGroundFromBlockGrid(blockGridModel);
        physic.limitPanel(blockGridModel);
        
        // création de l'eau
        this.water = physic.createBodyWater(model);

        // initialisation caméra et contrôleur (ON PREND LES COORDONNEES DE LA FENETRE)
        this.camera = new CameraModel(1280, 720, model.getWidth(), model.getHeight(), 1);
        physic.limitPanel(model);

        // On initialise les liste de Teams
        this.listTeamModel = new ArrayList<>();
        this.listTeamInital = new ArrayList<>();

        // initialise le nbr de worm par equipe
        this.nbrWormsByTeam = 3;

        // Récupérer le skin sélectionné dans les paramètres
        String selectedSkin = SettingsModel.getCurrentSkin();

        // creation des equipes selon le nombre que on veut
        createTeam(nbrEquipe, selectedSkin);

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
        this.itemController.setBlockGridModel(this.blockGridModel);// pour les dégats sur le terrain
        this.itemController.setWindModel(this.windModel); // pour le vent
        this.itemController.setGameManager(this);  // Pour le tracking des stats
        
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
        activeWormController.setCurrentWeapon(null);
        // Initialisation caméra avec le worm actif (fenêtre 1280x720, terrain 6000x720)
        this.cameraController = new CameraController(camera,listTeamInital.get(currentTeamIndex).getTeamModel().getWorm(activeWormIndex));
        // Centrer la caméra sur le worm actif au démarrage
        this.cameraController.centerOnCharacter();
        // Contrôleur fusionné avec le worm actif
        this.imputController = new SwingInputController(activeWormController, cameraController);
        // GamePanel avec tout ce qui est nécessaire
        this.gamePanel = new GamePanel(model, camera, cameraController, listTeamInital); 
        // Passer le BlockGridModel au GamePanel pour le rendu en blocs
        this.gamePanel.setBlockGridModel(this.blockGridModel);
        // Passer l'ItemController au GamePanel pour dessiner les projectiles
        this.gamePanel.setItemController(this.itemController);
        // Ajoute le contrôleur pour capter les touches
        //this.gamePanel.add(imputController);
        imputController.setGameManager(this); // connecte inputController au GameManager
        // Connecter le renderer de trajectoire
        imputController.setTrajectoryRenderer(gamePanel.getTrajectoryView());
        gamePanel.getTrajectoryView().setShooter(activeWormController.getModel());
        gamePanel.getTrajectoryView().setWeapon(activeWormController.getCurrentWeapon());
        // pour ne pas que gamePanel et imputController se disputent le focus
        this.gamePanel.setFocusable(false);  // le panel ne prend pas le focus
        imputController.setFocusable(true);   // l'inputController prend le focus    

        initialiseInventory();
        
        // La mise à jour de la caméra et des projectiles est maintenant gérée dans GameLoop
    }

    /**
     * Constructeur pour lancer a partir d'un fichier json sauvegarde
     * On part du principe que elle sera tjr appele si saveData est vrai 
     * Et jamais appele si saveData est faux
     * 
     * Initialise les donnees de la partie depuis saveData au lieu d'en creer
     * 
     * @param saveData donnees minimal qui viennent d'un sauvegarde.txt
     * 
     * @see GameManager#GameManager(int nbrEquipe) pour plus de precision sur comment fonctionne le constructeur
     */
    public GameManager(SaveData saveData){
        //this.saveDataBool = true;
        //this.saveData = saveData;

        // On part du principe que la taille du terrain ne changera jamais 
        int sizeX = 1280;  // Taille du terrain
        int sizeY = 720;
        int waterLevel = 800;

        System.out.println("Ca marche ?"); // oui, ca casse a la ligne de en dessous

        int[] groundY = saveData.getGroundModel().getGroundY();
        this.blockGridModel = new BlockGridModel(sizeX, sizeY, waterLevel);
        blockGridModel.generateWithGroundY(groundY);

        this.model = saveData.getGroundModel();

        this.physic = new PhysicsWorld();
        this.world = physic.getWorld();

        physic.createGroundFromBlockGrid(blockGridModel);
        physic.limitPanel(blockGridModel);

        this.water = physic.createBodyWater(model);

        SoundPlayer.initialize();

        this.roundTimeRemaining = saveData.getRoundTimeRemaining();

        this.camera = new CameraModel(1280, 720, model.getWidth(), model.getHeight(), 1);
        physic.limitPanel(model);

        // Au lieu de creer des nouvelles equipes, on prend celle de la save
        this.listTeamInital = new ArrayList<>();
        this.listTeamModel = new ArrayList<>();

            for (TeamData td : saveData.getListTeamData()){
                TeamInitialisation tm = td.dataToInit(model, world);
                this.listTeamInital.add(tm);
                listTeamModel.add(tm.getTeamModel());

                // c'est pas grave de le reecrire plusieurs fois, toutes les equipes auront le meme nbr de worms
                this.nbrWormsByTeam = td.getWormsNumber();
            }
        this.currentTeamIndex = saveData.getCurrentTeamIndex();
        this.currentTeam = listTeamInital.get(currentTeamIndex).getTeamModel();

        // Pour avoir la pos de chaque worm qui a jouee pour la derniere fois dans chaque equipe
        this.teamCursor = saveData.getTeamCursor();

        this.activeWormIndex = saveData.getActiveWormIndex();
        this.activeWormController =  new CharacterController(listTeamInital.get(currentTeamIndex).getTeamModel().getWorm(activeWormIndex));

        // GameStats : 
        this.teamStats = saveData.getAllTeamStats();
        
        // pour le WaterSensorListener
        ArrayList<CharacterModel> allWorms = new ArrayList<>();
        for (int i = 0; i < listTeamInital.size(); i++) {
            for (int j = 0 ; j < listTeamInital.get(i).getTeamModel().getTabWorms().length; j++){
                allWorms.add(listTeamInital.get(i).getTeamModel().getTabWorms()[j]);
            }
        }

        this.wSL = physic.new WaterSensorListener(water, allWorms, new ArrayList<CharacterModel>());
        world.setContactListener(wSL);

        // initialisation des rounds
        this.roundNumber = saveData.getRound();

        // Initialisation du système de tir
        this.itemController = new ItemController(new ItemView());
        this.itemController.setWorld(world);
        this.itemController.setGameManager(this);  // Pour le tracking des stats
        
        // Créer un tableau de tous les worms (N equipes)
        this.allWorms = new CharacterModel[allWorms.size()];
        for (int i = 0 ; i < this.allWorms.length ; i++){
            this.allWorms[i] = allWorms.get(i);
        }

        
        this.itemController.setAllWorms(this.allWorms);
        activeWormController.setItemController(this.itemController);
        activeWormController.setWorld(world);
        activeWormController.setAllWorms(this.allWorms);
        
        activeWormController.setCurrentWeapon(null);
        
        // Initialisation caméra avec le worm actif (fenêtre 1280x720, terrain 6000x720)
        this.cameraController = new CameraController(camera,listTeamInital.get(currentTeamIndex).getTeamModel().getWorm(activeWormIndex));
        
        // Centrer la caméra sur le worm actif au démarrage
        this.cameraController.centerOnCharacter();
        
        // Contrôleur fusionné avec le worm actif
        this.imputController = new SwingInputController(activeWormController, cameraController);

        // GamePanel avec tout ce qui est nécessaire
        this.gamePanel = new GamePanel(model, camera, cameraController, listTeamInital); 
        
        // Passer le BlockGridModel au GamePanel pour le rendu en blocs
        this.gamePanel.setBlockGridModel(this.blockGridModel);
        
        // Passer l'ItemController au GamePanel pour dessiner les projectiles
        this.gamePanel.setItemController(this.itemController);

        // Ajoute le contrôleur pour capter les touches
        //this.gamePanel.add(imputController);
        imputController.setGameManager(this); // connecte inputController au GameManager
                                             //  pour que inputController puisse appeler switchToNextWorm()
        
        // Connecter le renderer de trajectoire
        imputController.setTrajectoryRenderer(gamePanel.getTrajectoryView());
        gamePanel.getTrajectoryView().setShooter(activeWormController.getModel());
        gamePanel.getTrajectoryView().setWeapon(activeWormController.getCurrentWeapon());
        
        // pour ne pas que gamePanel et imputController se disputent le focus
        // en lancant le jeu, certaines fois rien n'était controllable mais c'est fix maintenant
        this.gamePanel.setFocusable(false);  // le panel ne prend pas le focus
        imputController.setFocusable(true);   // l'inputController prend le focus    

        // Timer pour update régulier de la caméra, projectiles et repaint
        new Timer(16, e -> {
            cameraController.update();
            // mettre à jour les projectiles (16ms = 0.016s par frame)
            itemController.updateProjectiles(0.016f);
            
            // recréer le terrain physique si la grille a changé (blocs détruits)
            if (blockGridModel.isDirty()) {
                physic.recreateTerrainFromBlockGrid(blockGridModel);
                blockGridModel.clearDirty();
            }
            
            gamePanel.repaint();
        }).start();

        //initialiseInventory();

        if (saveData.getWindData() != null) {
            saveData.getWindData().applyToWindModel(this.windModel);
        }

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
    
    public BlockGridModel getBlockGridModel() {
        return this.blockGridModel;
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
    
    public ItemController getItemController() {
        return this.itemController;
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

    public WindModel getWindModel(){
        return this.windModel;
    }

    public void setHudOverlay(HUDOverlay hud) {
        this.hudOverlay = hud;
    }

    public HUDOverlay getHudOverlay() {
        return hudOverlay;
    }
    
    public Map<String, GameStats> getAllTeamStats() {
        return teamStats;
    }

    public GameStats getTeamStats(String teamName) {
        return teamStats.get(teamName);
    }

    public GameStats getCurrentTeamStats() {
        String currentTeamName = getCurrentTeamName();
        return teamStats.get(currentTeamName);
    }

    public int[] getTeamCursor(){
        return this.teamCursor;
    }

    /**
    * Vérifie si une équipe a gagné (tous les worms des autres équipes sont morts)
    * @return Le nom de l'équipe gagnante ou null si le jeu continue
    */
    public String checkGameOver() {
        int teamsAlive = 0;
        String winningTeam = null;
        
        for (TeamInitialisation team : listTeamInital) {
            int aliveWorms = team.getTeamModel().getAliveWormNumber();
            if (aliveWorms > 0) {
                teamsAlive++;
                winningTeam = team.getName();
            }
        }
        
        // Si une seule équipe reste en vie, elle gagne
        if (teamsAlive == 1) {
            return winningTeam;
        }
        
        return null;  // Le jeu continue
    }

    /**
    * switch le worm actif vers le suivant dans l'équipe
    * saute les worms morts
    * Version généralisée pour N équipes
    */
    public void switchToNextWorm() {
        TeamModel currentTeamModel = listTeamInital.get(currentTeamIndex).getTeamModel();
        int startIndex = teamCursor[currentTeamIndex];
        
        do {
            teamCursor[currentTeamIndex]++;
            if (teamCursor[currentTeamIndex] >= currentTeamModel.getWormNumberAtStart()) {
                teamCursor[currentTeamIndex] = 0; // Retour au début
            }
            
            CharacterModel worm = currentTeamModel.getWorm(teamCursor[currentTeamIndex]);
            
            if (worm != null && !worm.isDead()) {
                // si le nouveau worm existe et n'est pas mort alors c'est le nouveau actif
                // => on attache le worm a la camera et a son controller
                activeWormController = new CharacterController(worm);
                
                // Initialiser le système de tir pour le nouveau worm
                activeWormController.setItemController(this.itemController);
                activeWormController.setWorld(world);
                activeWormController.setAllWorms(this.allWorms);
                activeWormController.setCurrentWeapon(null);
                
                imputController.setCharacterController(activeWormController);
                cameraController.setTargetCharacter(worm);
                cameraController.centerOnCharacter();
                
                // mettre à jour la trajectoire avec le nouveau worm et son arme
                gamePanel.getTrajectoryView().setShooter(worm);
                gamePanel.getTrajectoryView().setWeapon(activeWormController.getCurrentWeapon());
                
                return;
            }
            
            // si on est revenu au worm de départ alors il n'y a plus de worm vivant
            if (teamCursor[currentTeamIndex] == startIndex) {
                System.out.println("Aucun worm vivant disponible dans cette équipe");
                return;
            }
        } while (true);
    }

    /**
     * Ajoute une nouvelle équipe au jeu (pour supporter N équipes)
     * IMPORTANT: À appeler AVANT le démarrage du jeu
     *
     * @param team L'équipe à ajouter
     * @param view La vue correspondante de l'équipe
     */
    public void addTeam(TeamModel team,TeamView view) {
        if (team != null && view != null) {
            // Extraction des paramètres nécessaires
            String teamName = team.getTeamName();
            int teamNumber = team.getWormNumberAtStart();
            // Position de départ : on prend la position X du premier worm
            int startX = 100; // Valeur par défaut
            try {
                if (team.getTabWorms() != null && team.getTabWorms().length > 0) {
                    startX = (int) team.getTabWorms()[0].getXcord();
                }
            } catch (Exception e) {
                // fallback sur la valeur par défaut
            }
            String skinPath = "/wormPixel.png";
            // Si la TeamView possède un sprite personnalisé, on peut tenter de le récupérer
            // (à adapter si TeamView expose ce champ)
            // Construction de TeamInitialisation
            TeamInitialisation teamInit = new TeamInitialisation(
                this.model, // GroundModel
                this.world, // World
                teamName,
                teamNumber,
                startX,
                skinPath
            );
            listTeamInital.add(teamInit);
            listTeamModel.add(team);
            System.out.println("Équipe ajoutée: " + team.getTeamName() + " (Total: " + listTeamModel.size() + " équipes)");
        }
    }

    /**
     * Retourne le nombre total d'équipes
     */
    public int getTeamCount() {
        return listTeamModel.size();
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
                activeWormController.setCurrentWeapon(null);

                imputController.setCharacterController(activeWormController);
                cameraController.setTargetCharacter(worm);
                cameraController.centerOnCharacter();
                
                // mettre à jour la trajectoire avec le nouveau worm et son arme
                gamePanel.getTrajectoryView().setShooter(worm);
                gamePanel.getTrajectoryView().setWeapon(activeWormController.getCurrentWeapon());
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
    // Ajout d'une référence à GameLoop pour synchroniser le timer
    private GameLoop gameLoop;

    public void setGameLoop(GameLoop loop) {
        this.gameLoop = loop;
    }

    public void nextRound() {
        roundNumber++;
        currentTeamIndex = (currentTeamIndex + 1) % listTeamInital.size(); 
        currentTeam = listTeamInital.get(currentTeamIndex).getTeamModel();
        if (currentTeam != null) {
            currentTeam.resetInventoryTurn();
        }
        // Réinitialise le timer à 30 secondes
        roundTimeRemaining = roundTimeSave; // ou 1800
        if (gameLoop != null) {
            gameLoop.resetRoundTimer();
        }
        //switch worm dans cette équipe
        switchToNextWorm(currentTeam);
        System.out.println("Round " + roundNumber + " - Equipe active :  Team " + (currentTeamIndex + 1));
        //mettre à jour l'inventaire affiché dans l'overlay
        if (inventoryOverlay != null) {
            inventoryOverlay.setInventory(getCurrentTeamInventory());
        }

        
        // --- Vent : change à chaque tour (fixe pendant le tour) ---
        WindModel.Dir[] dirs = WindModel.Dir.values();
        windModel.setDir(dirs[windRng.nextInt(dirs.length)]);

        // force entre 0.15 et 1.0 (évite vent invisible)
        float strength = 0.15f + windRng.nextFloat() * 0.85f;
        windModel.setStrength(strength,false);

        System.out.println("[VENT] " + windModel.getDir().label + " | force=" + String.format("%.2f", windModel.getStrength()));

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
            gamePanel.getTrajectoryView().setWeapon(newWeapon);
        }
    }

    // Initialiser un nombre N d'equipes
    public void createTeam(int number){
        createTeam(number, "/wormPixel.png"); // Utilise le skin par défaut
    }
    
    // Initialiser un nombre N d'equipes avec un skin spécifique
    public void createTeam(int number, String skinPath){
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

            TeamInitialisation temp = new TeamInitialisation(model, world, name, this.nbrWormsByTeam, startX, skinPath);
    
            listTeamInital.add(temp);
            listTeamModel.add(temp.getTeamModel());
            
            // Initialiser les statistiques pour cette équipe
            teamStats.put(name, new GameStats());
        }
    }

    // SAUVEGARDE :

    /**
     * Methode appelee lorsqu'on appuie sur le bouton save
     */
    public void save(){
        // creer une sauvegarde automatiquement
        SaveData saveData = new SaveData(this);
        System.out.println("coucou"); 

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
            System.out.println(e.getMessage()); 
            //"Failed making field 'java.awt.Color#value' accessible; either increase its visibility or write a custom TypeAdapter for its declaring type."
        }
    }

    /**
     * Sauvegarde la partie
     * Est called dans le controller
     * Pourquoi ne pas juste appeler save() dans le controller ?
     */
    public void actionPerformed(){
        save();
        System.out.println("JSON Creer");
    }

    /*Selection d'inventaire depuis l'inventaire*/
    public void setWeaponFromInventory(String itemName) {
        Weapon newWeapon = null;

        // Empeche la casse 
        String normalizedName = itemName.toLowerCase();

        switch (normalizedName) {
            case "ak47":
                newWeapon = new Ak47();
                System.out.println("[Inventaire] Arme équipée depuis inventaire : AK-47");
                break;
            case "baseballbat":
                newWeapon = new BaseballBat();
                System.out.println("[Inventaire] Arme équipée depuis inventaire : Baseball Bat");
                break;
            case "grenade":
                newWeapon = new Grenade();
                System.out.println("[Inventaire] Arme équipée depuis inventaire : Grenade");
                break;
            case "rocketlauncher":
                newWeapon = new RocketLauncher();
                System.out.println("[Inventaire] Arme équipée depuis inventaire : Rocket Launcher");
                break;
            default:
                System.out.println("[Inventaire] Arme non reconnue: " + itemName);
                break;
        }

        if (newWeapon != null && activeWormController != null) {
            activeWormController.setCurrentWeapon(newWeapon);
            // Met à jour la trajectoire
            gamePanel.getTrajectoryView().setWeapon(newWeapon);
        }
    }

    public java.util.List<InventoryModel.InventoryJson> getCurrentTeamInventory() {
        TeamModel team = getCurrentTeam();
        return (team != null) ? team.getInventory() : null;
    }

    public void setInventoryOverlay(InventoryOverlay overlay){
        this.inventoryOverlay = overlay;
    }
    
    public void setWindOverlay(fr.upc.g5e4.view.WindOverlay overlay) {
        this.windOverlay = overlay;
    }
    
    public fr.upc.g5e4.view.WindOverlay getWindOverlay() {
        return this.windOverlay;
    }

    private void resetCurrentTeamInventoryTurn() {
        TeamModel team = getCurrentTeam();
        if (team != null) {
            team.resetInventoryTurn();
        }
    }

    // On passe la partie inventaire overlay ici (precedemment dans Gamelauncher)
    public void initialiseInventory(){

            try {
                InventoryRoot root = InventoryModel.load("/inventory.json");
                int index = 0;

                for (TeamInitialisation tm : listTeamInital){
                    java.util.List<InventoryJson> teamInventory = copyInventory(root.inventory);
                    listTeamInital.get(index).getTeamModel().setInventory(teamInventory);
                    index++;
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        
    }

    private java.util.List<InventoryModel.InventoryJson> copyInventory(java.util.List<InventoryModel.InventoryJson> src) {
                java.util.List<InventoryModel.InventoryJson> res = new java.util.ArrayList<>();
                for (InventoryModel.InventoryJson it : src) {
                    InventoryModel.InventoryJson c = new InventoryModel.InventoryJson();
                    c.objectType = it.objectType;
                    c.name = it.name;
                    c.maxUsages = it.maxUsages;
                    c.isInfinite = it.isInfinite;
                    c.remainingUses = it.remainingUses;
                    // useThisTurn reste à 0
                    res.add(c);
                }
                return res;
            }
    /**
     * Construit une ArrayList de tous les vers de toutes les équipes
     * Utile pour le WaterSensorListener et autres systèmes globaux
     */
    private ArrayList<CharacterModel> buildAllWormsList() {
        ArrayList<CharacterModel> allWorms = new ArrayList<>();
        for (TeamModel team : listTeamModel) {
            for (int i = 0; i < team.getWormNumberAtStart(); i++) {
                CharacterModel worm = team.getWorm(i);
                if (worm != null) {
                    allWorms.add(worm);
                }
            }
        }
        return allWorms;
    }

    /**
     * Construit un tableau de tous les vers de toutes les équipes
     * Utile pour l'ItemController et autres systèmes qui ont besoin d'un tableau
     */
    private CharacterModel[] buildAllWormsArray() {
        ArrayList<CharacterModel> wormsList = buildAllWormsList();
        return wormsList.toArray(new CharacterModel[0]);
    }

}


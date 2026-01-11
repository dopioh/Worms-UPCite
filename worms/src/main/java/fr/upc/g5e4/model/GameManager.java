package fr.upc.g5e4.model;

//import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;
//import javax.swing.Timer;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import fr.upc.g5e4.GameLauncher;
import fr.upc.g5e4.controller.*;
//import fr.upc.g5e4.model.WindModel;
import fr.upc.g5e4.model.weapons.*;
import fr.upc.g5e4.model.InventoryModel.InventoryJson;
import fr.upc.g5e4.model.InventoryModel.InventoryRoot;
import fr.upc.g5e4.model.PhysicsWorld.WaterSensorListener;
import fr.upc.g5e4.model.io.CustomTeamData;
//import fr.upc.g5e4.model.WindModel.Dir;
import fr.upc.g5e4.model.camera.CameraModel;
import fr.upc.g5e4.model.io.SaveData;
import fr.upc.g5e4.model.io.SaveLoad;
import fr.upc.g5e4.model.io.TeamData;
//import fr.upc.g5e4.view.CharacterView;
import fr.upc.g5e4.view.GamePanel;
import fr.upc.g5e4.view.ItemView;
import fr.upc.g5e4.view.TeamView;
import fr.upc.g5e4.view.CharacterView;
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
    private DecorationManager decorationManager;  // Gestion des décorations
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
    
    private String weaponUsedThisTurn = null;  // Nom de l'arme déjà utilisée ce tour 
    private int gadgetsUsedThisTurn = 0;  // Compte le nombre de gadgets utilisés

    // ajout minimal: gestion des rounds
    private int roundNumber = 1;
    //private int currentTeamIndex = 0; // 0 = Team1, 1 = Team2

    // Timer : 
    private int roundTimeRemaining = 1800;
    private int roundTimeSave = 1800;

    
    private InventoryOverlay inventoryOverlay;
    private HUDOverlay hudOverlay;
    private fr.upc.g5e4.view.WindOverlay windOverlay;
    // --- Vent toggle ---
    private boolean windEnabled = true;

    // Vent "réel" du tour (mémorisé même si windEnabled=false)
    private WindModel.Dir turnWindDir = WindModel.Dir.E;
    private float turnWindStrength = 0.5f;

    private final WindModel windModel = new WindModel();
    private final Random windRng = new Random();
    
    // Statistiques de jeu - support pour un nombre variable d'équipes
    private Map<Integer, GameStats> teamStats = new HashMap<>();

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
    public GameManager() {
        // Initialiser le système audio en arrière-plan pour éviter le lag au premier tir
        //this.saveDataBool = false;
        SoundPlayer.initialize();

        // ========== CONFIGURATION DU TERRAIN ==========
        // Pour changer le type de terrain et la taille, voir MapConfig.java
        int sizeX = MapConfig.getMapWidth();
        int sizeY = MapConfig.getMapHeight();
        // Eau uniquement pour le mode ISLANDS
        int waterLevel = (MapConfig.getMapType() == MapConfig.MapType.ISLANDS) ? 480 : sizeY - 100;
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
        
        // création des décorations sur le terrain
        this.decorationManager = new DecorationManager(blockGridModel);
        decorationManager.generateDecorations(6);  // environ 6 décorations aléatoires
        
        // on garde le GroundModel pour compatibilité avec TeamModel
        this.model = new GroundModel(groundY, sizeX, sizeY, waterLevel);

        this.physic = new PhysicsWorld();
        this.world = physic.getWorld();
        
        // nouveau sol
        physic.recreateGroundFromBlockGrid(blockGridModel);
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
        this.nbrWormsByTeam = TeamConfig.NBR_WORM_EQUIPE;

        // Récupérer le skin sélectionné dans les paramètres
        //String selectedSkin = SkinSettings.getCurrentSkin();

        // creation des equipes selon le nombre que on veut
        createTeam(TeamConfig.NBR_EQUIPE, TeamConfig.aCustomForATeam);

        // initialisation des index 
        currentTeamIndex = 0;
        this.currentTeam = listTeamInital.get(currentTeamIndex).getTeamModel();
        this.teamCursor = new int[TeamConfig.NBR_EQUIPE];
        this.activeWormIndex = 0;
        this.activeWormController = new CharacterController(listTeamInital.get(currentTeamIndex).getTeamModel().getWorm(activeWormIndex));
        activeWormController.setGameManager(this);
        
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
        this.itemController.setPhysicsWorld(this.physic); // pour reconstruire le terrain
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
        Weapon defaultWeapon = null;
        activeWormController.setCurrentWeapon(defaultWeapon);
        // Mettre à jour l'arme dans la vue
        updateWeaponInView(activeWormController.getModel(), defaultWeapon);
        // Initialisation caméra avec le worm actif (fenêtre 1280x720, terrain 6000x720)
        this.cameraController = new CameraController(camera,listTeamInital.get(currentTeamIndex).getTeamModel().getWorm(activeWormIndex));
        // Centrer la caméra sur le worm actif au démarrage
        this.cameraController.centerOnCharacter();
        // Contrôleur fusionné avec le worm actif
        this.imputController = new SwingInputController(activeWormController, cameraController);
        // GamePanel avec tout ce qui est nécessaire
        this.gamePanel = new GamePanel(model, camera, cameraController, listTeamInital, this); 
        // Passer le BlockGridModel au GamePanel pour le rendu en blocs
        this.gamePanel.setBlockGridModel(this.blockGridModel);
        // Passer le DecorationManager au GamePanel pour le rendu des décos
        this.gamePanel.setDecorationManager(this.decorationManager);
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
        BlockGridModel b = saveData.getBlockGridModel();

        // On part du principe que la taille du terrain ne changera jamais 
        int sizeX = b.getPixelWidth();  // Taille du terrain
        int sizeY = b.getPixelHeight();
        int waterLevel = b.getWaterLevel(); // a recup de saveData aussi..

        int[] groundY = saveData.getGroundModel().getGroundY();
        this.blockGridModel = b;
        
        // création des décorations pour les parties chargées
        this.decorationManager = new DecorationManager(blockGridModel);
        decorationManager.generateDecorations(6);

        this.model = saveData.getGroundModel();

        this.physic = new PhysicsWorld();
        this.world = physic.getWorld();

        physic.recreateGroundFromBlockGrid(blockGridModel);
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
        activeWormController.setGameManager(this);

        // GameStats : 
        this.teamStats = saveData.getAllTeamStats();

        // Background : 
        MapConfig.setBackgroundStyle(SaveData.getBackgroundFROMSAVE());
        
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
        this.itemController.setPhysicsWorld(this.physic); // pour reconstruire le terrain
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
        Weapon defaultWeapon = null;
        activeWormController.setCurrentWeapon(defaultWeapon);
        // Mettre à jour l'arme dans la vue
        updateWeaponInView(activeWormController.getModel(), defaultWeapon);

        // Normalement les index pour cv et cm sont les memes
        for (TeamInitialisation tm : listTeamInital) {
                TeamModel td = tm.getTeamModel();
                TeamView tv = tm.getTeamView();
    
            // on parcourt les worms de CETTE equipe specifique
            for (int i = 0; i < td.getTabWorms().length; i++) {
                CharacterView cv = tv.getCharacterView(i);
    
                CharacterModel currentWorm = td.getTabWorms()[i]; 

                currentWorm.restoreVisuals(cv);
            }
        }
        

        // Initialisation caméra avec le worm actif (fenêtre 1280x720, terrain 6000x720)
        this.cameraController = new CameraController(camera,listTeamInital.get(currentTeamIndex).getTeamModel().getWorm(activeWormIndex));
        
        // Centrer la caméra sur le worm actif au démarrage
        this.cameraController.centerOnCharacter();
        
        // Contrôleur fusionné avec le worm actif
        this.imputController = new SwingInputController(activeWormController, cameraController);

        // GamePanel avec tout ce qui est nécessaire
        this.gamePanel = new GamePanel(model, camera, cameraController, listTeamInital, this); 
        
        // Passer le BlockGridModel au GamePanel pour le rendu en blocs
        this.gamePanel.setBlockGridModel(this.blockGridModel);
        
        // Passer le DecorationManager au GamePanel pour le rendu des décos
        this.gamePanel.setDecorationManager(this.decorationManager);
        
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

        // suppression de l'ancien timer useless ici (projectiles, camera et terrain)

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
    
    /** @return le gestionnaire de décorations */
    public DecorationManager getDecorationManager() {
        return this.decorationManager;
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
    
    public Map<Integer, GameStats> getAllTeamStats() {
        return teamStats;
    }

    public GameStats getTeamStats(int teamId) {
        return teamStats.get(teamId);
    }

    public GameStats getCurrentTeamStats() {
        int currentTeamId = currentTeam.getTeamID();
        return teamStats.get(currentTeamId);
    }

    public int[] getTeamCursor(){
        return this.teamCursor;
    }

    /**
     * @return true si le vent est actif
     */
    public boolean isWindEnabled() {
        return windEnabled;
    }

    /**
     * Active ou désactive le vent.
     * Si désactivé, le vent est forcé à une valeur 0 et ne change plus à chaque tour.
     *
     * @param enabled true ou false
     */
    public void toggleVent(boolean enabled) {
        this.windEnabled = enabled;

        if (enabled) {
            // On restaure exactement le vent du tour courant (pas de nouveau tirage)
            windModel.setDir(turnWindDir);
            windModel.setStrength(turnWindStrength, false);
        } else {
            // On coupe l'effet du vent sans perdre la valeur du tour
            windModel.setStrength(0f, false);
        }

        // Côté vue : cacher/montrer l'overlay
        if (windOverlay != null) {
            windOverlay.setVisible(enabled);
            windOverlay.repaint();
        }
    }

    /**
     * Garde la force actuelle du vent
     * @return force du vent 
     */
    public float getEffectiveWindStrength() {
        return windEnabled ? windModel.getStrength() : 0f;
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
                Weapon defaultWeapon = null;
                activeWormController.setCurrentWeapon(defaultWeapon);
                
                imputController.setCharacterController(activeWormController);
                cameraController.setTargetCharacter(worm);
                cameraController.centerOnCharacter();
                
                // mettre à jour la trajectoire avec le nouveau worm et son arme
                gamePanel.getTrajectoryView().setShooter(worm);
                gamePanel.getTrajectoryView().setWeapon(defaultWeapon);
                
                // mettre à jour l'arme dans la vue
                updateWeaponInView(worm, defaultWeapon);
                
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
                activeWormController.setGameManager(this);
                activeWormController.setItemController(this.itemController);
                activeWormController.setWorld(world);
                activeWormController.setAllWorms(this.allWorms);
                Weapon defaultWeapon = null;
                activeWormController.setCurrentWeapon(defaultWeapon);

                imputController.setCharacterController(activeWormController);
                cameraController.setTargetCharacter(worm);
                cameraController.centerOnCharacter();
                
                // mettre à jour la trajectoire avec le nouveau worm et son arme
                gamePanel.getTrajectoryView().setShooter(worm);
                gamePanel.getTrajectoryView().setWeapon(defaultWeapon);
                
                // mettre à jour l'arme dans la vue
                updateWeaponInView(worm, defaultWeapon);
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
    
    public GameLoop getGameLoop() {
        return this.gameLoop;
    }

    public void nextRound() {
        // add 5$ à l'équipe actuelle si le worm actif est vivant
        if (activeWormController != null) {
            CharacterModel activeWorm = activeWormController.getModel();
            if (activeWorm != null && !activeWorm.isDead()) {
                TeamModel teamBeforeSwitch = listTeamInital.get(currentTeamIndex).getTeamModel();
                teamBeforeSwitch.addMoney(5);
                System.out.println("[MONEY] +5$ pour Team " + (currentTeamIndex + 1) + " (total: " + teamBeforeSwitch.getMoney() + "$)");
            }
        }
        
        // incrémenter le compteur de rounds pour l'équipe qui vient de jouer
        GameStats currentStats = getCurrentTeamStats();
        if (currentStats != null) {
            currentStats.incrementRounds();
        }
        
        roundNumber++;
        currentTeamIndex = (currentTeamIndex + 1) % listTeamInital.size(); 
        currentTeam = listTeamInital.get(currentTeamIndex).getTeamModel();
        if (currentTeam != null) {
            currentTeam.resetInventoryTurn();
        }
        // réinitialise le flag d'utilisation d'arme pour le nouveau tour
        resetWeaponUsedFlag();
        // réinitialise le compteur de gadgets utilisés
        gadgetsUsedThisTurn = 0;
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
            inventoryOverlay.clearEquipped();
        }
        
        if (hudOverlay != null) {
            hudOverlay.repaint();
            hudOverlay.triggerTurnFlash();
        }

        
        //Vent qui change à chaque tour 
        WindModel.Dir[] dirs = WindModel.Dir.values();
        turnWindDir = dirs[windRng.nextInt(dirs.length)];

        // force entre 0.15 et 1.0 pour éviter le vent nul
        turnWindStrength = 0.15f + windRng.nextFloat() * 0.85f;

        // Le vent appliquera la valeur calculée "turnwindstrengh" ou 0 
        if (windEnabled) {
            windModel.setDir(turnWindDir);
            windModel.setStrength(turnWindStrength, false);
        } else {
            windModel.setStrength(0f, false);
        }
    }
    
    // Initialiser un nombre N d'equipes avec un skin spécifique
    public void createTeam(int number, ArrayList<CustomTeamData> customTeam){
        int mapWidth = model.getWidth();
        int margin = 50; // Marge pour ne pas spawner au bord
        int usableWidth; // Espace utilisable
        if (number > 3 && TeamConfig.NBR_WORM_EQUIPE > 3){
            usableWidth = mapWidth - (4 * margin);
            System.out.println("Usable Width : " + usableWidth);
        } else {
            usableWidth = mapWidth - (2 * margin);
        }

        int index = 0;

        for (int i = 0; i < number; i++){

            String skinPath;
            CustomTeamData ctd;
            if (customTeam != null && i < customTeam.size()){
                ctd = customTeam.get(i);
                skinPath = ctd.getSkin().getFilePath();
            } else {
                ctd = new CustomTeamData();
                skinPath = "/wormPixel.png";
            }

            ArrayList<String> nameWorms = ctd.getWormsName();
            

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

            String name = ctd.getTeamName();
            //si le nom est default donner un nom unique basé sur l'index
            if (name.equalsIgnoreCase("Default")) {
                name = "Team " + (i + 1);
            }

            TeamInitialisation temp = new TeamInitialisation(model, world, name, this.nbrWormsByTeam, startX, skinPath,nameWorms);
    
            listTeamInital.add(temp);
            listTeamModel.add(temp.getTeamModel());
            
            // Initialiser les statistiques pour cette équipe
            teamStats.put(temp.getTeamModel().getTeamID(), new GameStats());
        }
    }

    // SAUVEGARDE :

    /**
     * Methode appelee lorsqu'on appuie sur le bouton save
     */
    public void save(){
        // creer une sauvegarde automatiquement
        SaveData saveData = new SaveData(this);
        //System.out.println("coucou"); 

        try {
            File file = SaveLoad.findSaveFile();
            FileWriter fileWriter = new FileWriter(file);
            Gson gson = new GsonBuilder().setPrettyPrinting().create(); // rajouter serializeNulls()
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
        // Permet de changer d'arme même si une arme a été utilisée, mais avertir si c'est une arme différente
        if (weaponUsedThisTurn != null && !weaponUsedThisTurn.equalsIgnoreCase(itemName)) {
            System.out.println("[Inventaire] Une autre arme a déjà été utilisée ce tour. Vous pouvez changer mais pas tirer!");
        }
        
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
            case "bazooka":
                newWeapon = new RocketLauncher();
                System.out.println("[Inventaire] Arme équipée depuis inventaire : Rocket Launcher");
                break;
            default:
                System.out.println("[Inventaire] Arme non reconnue: " + itemName);
                break;
        }

        if (newWeapon != null && activeWormController != null) {
            // Déséquiper le gadget (on ne peut pas avoir 2 choses dans les mains)
            clearGadgetSprite();
            
            // trouver l'arme dans l'inventaire 
            InventoryJson weaponInventory = findWeaponInInventory(itemName);
            
            activeWormController.setCurrentWeapon(newWeapon);
            activeWormController.setCurrentWeaponInventory(weaponInventory);
            // Met à jour la trajectoire
            gamePanel.getTrajectoryView().setWeapon(newWeapon);
            // met à jour l'arme dans la vue
            updateWeaponInView(activeWormController.getModel(), newWeapon);
        }
    }
    
    /**
     * équipe un gadget depuis l'inventaire et affiche son sprite
     * @param gadgetName Le nom du gadget à équiper
     */
    public void setGadgetFromInventory(String gadgetName) {
        if (activeWormController == null || activeWormController.getModel() == null) {
            return;
        }

        System.out.println("[Inventaire] Gadget équipé depuis inventaire : " + gadgetName);
        
        // déséquiper l'arme (on ne peut pas avoir 2 choses dans les mains)
        clearWeaponSprite();
        
        // récupérer la vue du worm pour afficher le sprite du gadget
        fr.upc.g5e4.view.CharacterView charView = null;
        int wormIndex = getActiveWormIndex();
        int teamIndex = getCurrentTeamIndex();
        if (wormIndex >= 0 && teamIndex >= 0 && teamIndex < getListTeamInit().size()) {
            charView = getListTeamInit().get(teamIndex).getTeamView().getCharacterView(wormIndex);
        }
        
        // afficher le sprite du gadget
        if (charView != null) {
            charView.setCurrentGadgetSprite(gadgetName);
        }
    }
    
    /**
     * Utilise un gadget depuis l'inventaire (potions, bumper, etc)
     * @param itemName Le nom du gadget à utiliser
     */
    public void useGadgetFromInventory(String itemName) {
        if (activeWormController == null || activeWormController.getModel() == null) {
            return;
        }
        
        // vérifier si on peut encore utiliser un gadget ce tour
        if (gadgetsUsedThisTurn >= 2) {
            System.out.println("[Gadget] Limite atteinte! Vous avez déjà utilisé 2 gadgets ce tour.");
            return;
        }
        
        CharacterModel currentWorm = activeWormController.getModel();
        
        // récupérer la vue du worm pour ajouter l'étiquette au nametag et afficher le sprite du gadget
        fr.upc.g5e4.view.CharacterView charView = null;
        int wormIndex = getActiveWormIndex();
        int teamIndex = getCurrentTeamIndex();
        if (wormIndex >= 0 && teamIndex >= 0 && teamIndex < getListTeamInit().size()) {
            charView = getListTeamInit().get(teamIndex).getTeamView().getCharacterView(wormIndex);
        }   
        
        // afficher le sprite du gadget
        if (charView != null) {
            charView.setCurrentGadgetSprite(itemName);
        }
        
        // trouver le gadget dans l'inventaire pour enregistrer son utilisation
        InventoryJson gadgetFromInventory = findGadgetInInventory(itemName);
        
        switch (itemName) {
            case "PotionHeal":
                currentWorm.setHealth(Math.min(100, currentWorm.getHealth() + 50));
                currentWorm.healEffect();
                // Pas d'étiquette pour le heal
                System.out.println("[Gadget] Potion Heal : +50 HP");
                SoundPlayer.play("/sfx/potionheal-sound.wav");
                gadgetsUsedThisTurn++;
                // enregistrer l'utilisation du gadget
                if (gadgetFromInventory != null && gadgetFromInventory.canUseThisTurn()) {
                    gadgetFromInventory.registerUseThisTurn();
                }
                break;
            case "PotionShield":
                currentWorm.setShield(Math.min(100, currentWorm.getShield() + 25));
                currentWorm.shieldEffect();
                // Pas d'étiquette pour le shield
                System.out.println("[Gadget] Potion Shield : +25 Shield");
                SoundPlayer.play("/sfx/potionshield-sound.wav");
                gadgetsUsedThisTurn++;
                // enregistrer l'utilisation du gadget
                if (gadgetFromInventory != null && gadgetFromInventory.canUseThisTurn()) {
                    gadgetFromInventory.registerUseThisTurn();
                }
                break;
            case "PotionSpeed":
                currentWorm.applySpeedBuff(20000);
                // ajouter l'étiquette via la vue du worm avec timer (20 secondes)
                if (charView != null) {
                    charView.getNameTagView().addItemFromResource("/buffs/speed_buff.png", "speed_buff_icon", 20000);
                }
                System.out.println("[Gadget] Potion Speed : +50% vitesse");
                SoundPlayer.play("/sfx/potionspeed-sound.wav");
                gadgetsUsedThisTurn++;
                // enregistrer l'utilisation du gadget
                if (gadgetFromInventory != null && gadgetFromInventory.canUseThisTurn()) {
                    gadgetFromInventory.registerUseThisTurn();
                }
                break;
            case "PotionJump":
                currentWorm.applyJumpBuff(20000);
                // ajouter l'étiquette via la vue du worm avec timer (20 secondes)
                if (charView != null) {
                    charView.getNameTagView().addItemFromResource("/buffs/jump_buff.png", "jump_buff_icon", 20000);
                }
                System.out.println("[Gadget] Potion Jump : +50% saut");
                SoundPlayer.play("/sfx/potionjump-sound.wav");
                gadgetsUsedThisTurn++;
                // enregistrer l'utilisation du gadget
                if (gadgetFromInventory != null && gadgetFromInventory.canUseThisTurn()) {
                    gadgetFromInventory.registerUseThisTurn();
                }
                break;
            case "Bumper":
                System.out.println("[Gadget] Bumper utilisé");
                // SoundPlayer.play("/sfx/bumper-sound.wav");
                gadgetsUsedThisTurn++;
                // enregistrer l'utilisation du gadget
                if (gadgetFromInventory != null && gadgetFromInventory.canUseThisTurn()) {
                    gadgetFromInventory.registerUseThisTurn();
                }
                break;
            default:
                System.out.println("[Gadget] Gadget inconnu : " + itemName);
                break;
        }
    }

    /**
     * Methode pour recuperer les buff actif apres le load d'une save
     * @param itemName
     * @param tempsRestant
     */
    public void useGadgetDuringSave(BuffEffect.BuffType itemName,long tempsRestantL, CharacterModel cm){
        int tempsRestant = (int) tempsRestantL;

        CharacterModel currentWorm = cm;
        
        // récupérer la vue du worm pour ajouter l'étiquette au nametag et afficher le sprite du gadget
        fr.upc.g5e4.view.CharacterView charView = null;
        int wormIndex = getActiveWormIndex();
        int teamIndex = getCurrentTeamIndex();
        if (wormIndex >= 0 && teamIndex >= 0 && teamIndex < getListTeamInit().size()) {
            charView = getListTeamInit().get(teamIndex).getTeamView().getCharacterView(wormIndex);
        }   
        
        // afficher le sprite du gadget
        //if (charView != null) {
        //    charView.setCurrentGadgetSprite(itemName);
        //}

        switch (itemName) {
            case BuffEffect.BuffType.SPEED_BOOST:
                currentWorm.applySpeedBuff(tempsRestant);
                // ajouter l'étiquette via la vue du worm avec timer (20 secondes)
                if (charView != null) {
                    charView.getNameTagView().addItemFromResource("/buffs/speed_buff.png", "speed_buff_icon", tempsRestant);
                }
                break;
            case BuffEffect.BuffType.JUMP_BOOST:
                currentWorm.applyJumpBuff(tempsRestant);
                // ajouter l'étiquette via la vue du worm avec timer (20 secondes)
                if (charView != null) {
                    charView.getNameTagView().addItemFromResource("/buffs/jump_buff.png", "jump_buff_icon", tempsRestant);
                }
                break;
            default:
                System.out.println("[Gadget] Gadget inconnu : " + itemName);
                break;
        }
    }

    public java.util.List<InventoryModel.InventoryJson> getCurrentTeamInventory() {
        TeamModel team = getCurrentTeam();
        return (team != null) ? team.getInventory() : null;
    }
    
    /**
     * trouve un gadget dans l'inventaire de l'équipe actuelle par son nom
     * @param itemName Le nom du gadget
     * @return L'objet inventoryJson du gadget ou null si non trouvé
     */
    private InventoryJson findGadgetInInventory(String itemName) {
        java.util.List<InventoryJson> inventory = getCurrentTeamInventory();
        if (inventory == null) return null;
        
        for (InventoryJson item : inventory) {
            if (itemName.equals(item.name) && "GADGET".equals(item.objectType)) {
                return item;
            }
        }
        return null;
    }
    
    /**
     * trouve une arme dans l'inventaire de l'équipe actuelle par son nom
     * @param itemName Le nom de l'arme
     * @return L'objet inventoryJson de l'arme ou null si non trouvé
     */
    private InventoryJson findWeaponInInventory(String itemName) {
        java.util.List<InventoryJson> inventory = getCurrentTeamInventory();
        if (inventory == null) return null;
        
        for (InventoryJson item : inventory) {
            if (itemName.equalsIgnoreCase(item.name) && "WEAPON".equals(item.objectType)) {
                return item;
            }
        }
        return null;
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
     * Met à jour l'arme affichee dans la vue pour un worm en parametre
     * @param worm le modèle du worm
     * @param weapon l'arme à afficher (peut être null)
     */
    private void updateWeaponInView(CharacterModel worm, Weapon weapon) {
        if (worm == null || listTeamInital == null) {
            return;
        }
        
        // chercher le worm a travers les equipes en jeu
        for (TeamInitialisation teamInit : listTeamInital) {
            TeamModel teamModel = teamInit.getTeamModel();
            CharacterModel[] worms = teamModel.getTabWorms();
            
            // chercher l'index du worm dans l'equipe qu'on est entrain de parcourir
            for (int i = 0; i < worms.length; i++) {
                if (worms[i] == worm) {
                    TeamView teamView = teamInit.getTeamView();
                    if (teamView != null) {
                        CharacterView characterView = teamView.getCharacterView(i);
                        if (characterView != null) {
                            characterView.setCurrentWeaponSprite(weapon);
                        }
                    }
                    return;
                }
            }
        }
    }

    /**
     * désactive le sprite du gadget pour tous les worms de l'équipe active
     */
    private void clearGadgetSprite() {
        if (activeWormController == null || activeWormController.getModel() == null) {
            return;
        }

        CharacterModel worm = activeWormController.getModel();
        for (TeamInitialisation teamInit : listTeamInital) {
            TeamModel teamModel = teamInit.getTeamModel();
            CharacterModel[] worms = teamModel.getTabWorms();

            for (int i = 0; i < worms.length; i++) {
                if (worms[i] == worm) {
                    TeamView teamView = teamInit.getTeamView();
                    if (teamView != null) {
                        CharacterView characterView = teamView.getCharacterView(i);
                        if (characterView != null) {
                            characterView.setCurrentGadgetSprite(null);
                        }
                    }
                    return;
                }
            }
        }
    }

    /**
     * desactive le sprite de l'arme pour tous les worms de l'équipe active
     */
    private void clearWeaponSprite() {
        if (activeWormController == null || activeWormController.getModel() == null) {
            return;
        }

        CharacterModel worm = activeWormController.getModel();
        for (TeamInitialisation teamInit : listTeamInital) {
            TeamModel teamModel = teamInit.getTeamModel();
            CharacterModel[] worms = teamModel.getTabWorms();

            for (int i = 0; i < worms.length; i++) {
                if (worms[i] == worm) {
                    TeamView teamView = teamInit.getTeamView();
                    if (teamView != null) {
                        CharacterView characterView = teamView.getCharacterView(i);
                        if (characterView != null) {
                            characterView.setCurrentWeaponSprite(null);
                        }
                    }
                    return;
                }
            }
        }
    }

    /**
     * Construit un tableau de tous les vers de toutes les équipes
     * Utile pour l'ItemController et autres systèmes qui ont besoin d'un tableau
     */
    private CharacterModel[] buildAllWormsArray() {
        ArrayList<CharacterModel> wormsList = buildAllWormsList();
        return wormsList.toArray(new CharacterModel[0]);
    }
    
    /**
     * Marque qu'une arme a été utilisée ce tour
     * @param weaponName Le nom de l'arme utilisée
     */
    public void markWeaponUsed(String weaponName) {
        if (weaponUsedThisTurn == null) {
            weaponUsedThisTurn = weaponName;
            System.out.println("[Arme] " + weaponName + " utilisée ce tour!");
        }
    }
    
    /**
     * retourne le nom de l'arme utilisée ce tour 
     */
    public String getWeaponUsedThisTurn() {
        return weaponUsedThisTurn;
    }
    
    /**
     * Réinitialise le flag d'utilisation d'arme (appelé au début du tour)
     */
    public void resetWeaponUsedFlag() {
        weaponUsedThisTurn = null;
    }
    
    /**
     * Retourne le nombre de gadgets restants ce tour
     */
    public int getRemainingGadgetsThisTurn() {
        return 2 - gadgetsUsedThisTurn;
    }

}


// File: fr/upc/g5e4/model/GameManager.java
package fr.upc.g5e4.model;

import java.awt.Color;
import java.util.ArrayList;
import javax.swing.Timer;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import fr.upc.g5e4.GameLauncher;
import fr.upc.g5e4.controller.*;
import fr.upc.g5e4.model.*;
import fr.upc.g5e4.model.weapons.Ak47;
import fr.upc.g5e4.model.weapons.BaseballBat;
import fr.upc.g5e4.model.weapons.Grenade;
import fr.upc.g5e4.model.weapons.RocketLauncher;
import fr.upc.g5e4.model.PhysicsWorld.WaterSensorListener;
import fr.upc.g5e4.model.camera.CameraModel;
import fr.upc.g5e4.view.CharacterView;
import fr.upc.g5e4.view.GamePanel;
import fr.upc.g5e4.view.ItemView;
import fr.upc.g5e4.view.TeamView;

/**
 * Cette classe servira pour instancier tout ce dont le jeu (une partie de jeu)
 * a besoin
 */
public class GameManager {
    private final GamePanel gamePanel;
    private final GroundModel model;
    private final PhysicsWorld physic;
    private final World world;
    private final Body water;
    private final WaterSensorListener wSL;
    private SwingInputController imputController;
    private CameraModel camera; // AJOUT RAPHAËL
    private CameraController cameraController; // AJOUT RAPHAËL
    private final TeamModel teamModel1;
    private final TeamView teamView1;
    private final TeamModel teamModel2;  
    private final TeamView teamView2;
    private final ItemController itemController;
    private final CharacterModel[] allWorms;
    private int activeWormIndex;
    private CharacterController activeWormController;
    private int currentWeaponIndex = 0;  // 0=AK47, 1=Baseball, 2=Grenade, 3=Rocket    

    // ajout minimal: gestion des rounds
    private int roundNumber = 1;
    private int currentTeamIndex = 0; // 0 = Team1, 1 = Team2

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
    public GameManager(int sizex, int sizey) {
        int sizeX = sizex;
        int sizeY = sizey;
        int[] groundY = {400,420,450,450,400,700,930,450,350,300,250,230,200,200};
        /*
         * Plusieurs autres valeurs de groundY si vous voulez tester plusieurs terrains
         * :
         * int[] groundY = {400,420,450,450,400,300,530,450,350,300,250,230,200,230};
         * int[] groundY = {200,230,250,280,300,340,370,390,400,410,420,430,450,470};
         * int[] groundY = {470,450,430,420,410,400,390,370,340,300,280,250,230,200};
         * int[] groundY = {400,420,450,450,400,700,930,450,350,300,250,230,200,200};
         * 
         * J'ai toujours pris la meme taille de tableau pour faire les tests
         * Mais vous pouvez mettre autant de valeurs que vous voulez
         * Ca rendra d'ailleurs le Ground plus lisse normalement
         */

        this.model = new GroundModel(groundY, sizeX, sizeY, 500);

        this.physic = new PhysicsWorld();
        this.world = physic.getWorld();
        physic.createGroundFromModel(model);
        this.water = physic.createBodyWater(model);

        // Initialisation caméra et contrôleur
        this.camera = new CameraModel(sizeX, sizeY, model.getWidth(), model.getHeight(), 1);
        physic.limitPanel(model);

        // création premiere equipe
        int wormNumber = 3; 
        this.teamModel1 = new TeamModel(model, world, "Team1", 100, 98, wormNumber,false);
        this.teamView1 = new TeamView(wormNumber, Color.BLUE);

        // création seconde equipe
        this.teamModel2 = new TeamModel(model, world, "Team2", 400, 98, wormNumber,false);  
        this.teamView2 = new TeamView(wormNumber, Color.RED);
        
        // Créer une liste de tous les worms pour le WaterSensorListener
        ArrayList<CharacterModel> allWorms = new ArrayList<>();
        for (int i = 0; i < wormNumber; i++) {
            allWorms.add(teamModel1.getWorm(i));
            allWorms.add(teamModel2.getWorm(i));
        }
        
        this.wSL = physic.new WaterSensorListener(water, allWorms, new ArrayList<CharacterModel>());
        world.setContactListener(wSL);
        
        // ON JOUE LES BLEUS PAR DEFAUT
        // Le premier worm du tableau est le worm qu'on controle au debut
        this.activeWormIndex = 0;
        this.activeWormController = new CharacterController(teamModel1.getWorm(activeWormIndex));
        
        // Initialisation du système de tir
        this.itemController = new ItemController(new ItemView());
        this.itemController.setWorld(world);
        
        // Créer un tableau de tous les worms (équipe 1 + équipe 2)
        this.allWorms = new CharacterModel[teamModel1.getTabWorms().length + teamModel2.getTabWorms().length];
        System.arraycopy(teamModel1.getTabWorms(), 0, this.allWorms, 0, teamModel1.getTabWorms().length);
        System.arraycopy(teamModel2.getTabWorms(), 0, this.allWorms, teamModel1.getTabWorms().length, teamModel2.getTabWorms().length);
        
        this.itemController.setAllWorms(this.allWorms);
        activeWormController.setItemController(this.itemController);
        activeWormController.setWorld(world);
        activeWormController.setAllWorms(this.allWorms);
        
        // Équiper une arme par défaut (AK-47)
        activeWormController.setCurrentWeapon(new Ak47());
        
        // Initialisation caméra avec le worm actif
        this.camera = new CameraModel(sizeX, sizeY, model.getWidth(), model.getHeight(), 1);
        this.cameraController = new CameraController(camera, teamModel1.getWorm(activeWormIndex));
        // Contrôleur fusionné avec le worm actif
        this.imputController = new SwingInputController(activeWormController, cameraController);

        // GamePanel avec tout ce qui est nécessaire
        this.gamePanel = new GamePanel(model, camera, cameraController, teamModel1, teamView1, teamModel2, teamView2); 
        
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
        
        imputController.requestFocusInWindow();
        this.gamePanel.setFocusable(true);
        this.gamePanel.requestFocusInWindow();

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
    
    public TeamModel getTeamModel1() {
        return this.teamModel1;
    }

    public TeamModel getTeamModel2() {
        return this.teamModel2;
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

    /**
     * switch le worm actif vers le suivant dans l'équipe
     * saute les worms morts
     * TOUT CE PASSE SUR TEAM 1 POUR LE MOMENT (a rendre dynamique avec l'ajout des rounds)
     */
    public void switchToNextWorm() {
        int startIndex = activeWormIndex;
        do {
            activeWormIndex++;
            if (activeWormIndex >= teamModel1.getWormNumberAtStart()) {
                activeWormIndex = 0; // Retour au début
            }
            CharacterModel worm = teamModel1.getWorm(activeWormIndex);
            
            if (worm != null && !worm.isDead()) {
                // si le nouveau worm existe et n'est pas mort alors c'est le nouveau actif
                // => on attache le worm a la camera et a son controller
                activeWormController = new CharacterController(worm);
                
                // Initialiser le système de tir pour le nouveau worm
                activeWormController.setItemController(this.itemController);
                activeWormController.setWorld(world);
                activeWormController.setAllWorms(this.allWorms);
                activeWormController.setCurrentWeapon(new Ak47());
                
                imputController.setCharacterController(activeWormController);
                cameraController.setTargetCharacter(worm);
                cameraController.centerOnCharacter();
                
                // mettre à jour la trajectoire avec le nouveau worm et son arme
                gamePanel.getTrajectoryRenderer().setShooter(worm);
                gamePanel.getTrajectoryRenderer().setWeapon(activeWormController.getCurrentWeapon());
                
                // Réappliquer les listeners de souris pour le nouveau worm
                //gamePanel.addMouseListeners(activeWormController);
                return;
            }
            
            // si on est revenu au worm de départ alors il n'y a plus de worm vivant donc on ne peut plus controller
            if (activeWormIndex == startIndex) {
                System.out.println("Aucun worm vivant disponible...");
                return;
            }
        } while (true);
    }
    
    // switch généralisé pour une équipe donnée
    public void switchToNextWorm(TeamModel team) {
        int startIndex = activeWormIndex;
        do {
            activeWormIndex++;
            if (activeWormIndex >= team.getWormNumberAtStart()) {
                activeWormIndex = 0; // Retour au début dans l'équipe passée en paramètre
            }
            CharacterModel worm = team.getWorm(activeWormIndex);

            if (worm != null && !worm.isDead()) {
                activeWormController = new CharacterController(worm);
                activeWormController.setItemController(this.itemController);
                activeWormController.setWorld(world);
                activeWormController.setAllWorms(this.allWorms);
                activeWormController.setCurrentWeapon(new Ak47());

                imputController.setCharacterController(activeWormController);
                cameraController.setTargetCharacter(worm);
                cameraController.centerOnCharacter();
                
                // mettre à jour la trajectoire avec le nouveau worm et son arme
                gamePanel.getTrajectoryRenderer().setShooter(worm);
                gamePanel.getTrajectoryRenderer().setWeapon(activeWormController.getCurrentWeapon());
                
                return;
            }

            if (activeWormIndex == startIndex) {
                System.out.println("Aucun worm vivant disponible...");
                return;
            }
        } while (true);
    }

    // passe au round suivant et alterne les équipes
    public void nextRound() {
        roundNumber++;
        currentTeamIndex = (currentTeamIndex + 1) % 2;
        TeamModel currentTeam = (currentTeamIndex == 0) ? teamModel1 : teamModel2;
        switchToNextWorm(currentTeam);
        System.out.println("Round " + roundNumber + " - Equipe active : " + (currentTeamIndex == 0 ? "Team1" : "Team2"));
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
}

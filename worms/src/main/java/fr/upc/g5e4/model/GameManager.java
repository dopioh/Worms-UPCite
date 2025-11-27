package fr.upc.g5e4.model;

import java.util.ArrayList;
import javax.swing.Timer;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import fr.upc.g5e4.GameLauncher;
import fr.upc.g5e4.controller.*;
import fr.upc.g5e4.model.PhysicsWorld.WaterSensorListener;
import fr.upc.g5e4.model.camera.CameraModel;
import fr.upc.g5e4.view.CharacterView;
import fr.upc.g5e4.view.GamePanel;

/**
 * Cette classe servira pour instancier tout ce dont le jeu (une partie de jeu)
 * a besoin
 */
public class GameManager {
    private final GamePanel gamePanel;
    private final GroundModel model;

    private final CharacterModel personnageModel;
    private final CharacterView personnageView;
    private final CharacterController personnageController;
    private final PhysicsWorld physic;
    private final World world;
    private final Body water;
    private final WaterSensorListener wSL;
    private final SwingInputController imputController;
    private final CameraModel camera; // AJOUT RAPHAËL
    private final CameraController cameraController; // AJOUT RAPHAËL

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
        int sizeX = 800;
        int sizeY = 600;
        int[] groundY = { 400, 420, 450, 500, 550, 540, 530, 450, 350, 300, 250, 230, 200, 230 };
        /*
         * Plusieurs autres valeurs de groundY si vous voulez tester plusieurs terrains
         * :
         * int[] groundY = {400,420,450,450,400,300,530,450,350,300,250,230,200,230};
         * int[] groundY = {200,230,250,280,300,340,370,390,400,410,420,430,450,470};
         * int[] groundY = {470,450,430,420,410,400,390,370,340,300,280,250,230,200};
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

        this.personnageModel = new CharacterModel(model, world, 700, model.getHeight() - 500); // Si le x est à 800
                                                                                                // pile i ça marche pas
        // System.out.println("\nGame Manager xcord : " +
        // this.personnageModel.getXcord());
        this.personnageView = new CharacterView();
        this.personnageController = new CharacterController(personnageModel);

        // Initialisation caméra et contrôleur
        this.camera = new CameraModel(800, 600, model.getWidth(), model.getHeight(), 1);
        this.cameraController = new CameraController(camera, personnageModel);

        // Contrôleur fusionné personnage + caméra
        this.imputController = new SwingInputController(personnageController, cameraController); 

        physic.limitPanel(model);

        this.wSL = physic.new WaterSensorListener(water, personnageModel, new ArrayList<CharacterModel>());
        world.setContactListener(wSL);

        // GamePanel avec tout ce qui est nécessaire
        this.gamePanel = new GamePanel(model, personnageModel, personnageView, camera, cameraController); 

        // Ajoute le contrôleur pour capter les touches
        this.gamePanel.add(imputController);
        imputController.requestFocusInWindow();
        this.gamePanel.setFocusable(true);
        this.gamePanel.requestFocusInWindow();

        // Timer pour update régulier de la caméra et repaint
        new Timer(16, e -> {
            cameraController.update();
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

    public CharacterController getPersonnageController() {
        return this.personnageController;
    }

    public CharacterModel getPersonnageModel(){
        return this.personnageModel;
    }
    
    public CameraController getCameraController() { 
        return this.cameraController;
    }
}

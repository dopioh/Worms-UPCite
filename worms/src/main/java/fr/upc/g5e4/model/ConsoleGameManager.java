package fr.upc.g5e4.model;

import java.util.ArrayList;
import java.awt.Color;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import fr.upc.g5e4.controller.*;
import fr.upc.g5e4.view.*;
//import fr.upc.g5e4.model.*;
import fr.upc.g5e4.model.PhysicsWorld.WaterSensorListener;

//import com.googlecode.lanterna.screen.Screen;
//import com.googlecode.lanterna.terminal.Terminal;

/**
 * Gere la logique d'une partie dans le terminal 
 * Ils manquent des fonctionnalites pour le moment 
 * Seront ajouter au SPRINT 3
 * @see ConsoleGameLoop class qui est appeler la bas
 * @see GameManager pour voir la version avec Swing (Interface graphique)
 */
public class ConsoleGameManager {
    // private final GamePanel gamePanel; Swing donc on peut pas le mettre ici, il faut reecrire les mathodes
    private final GroundModel model;

    private final CharacterModel personnageModel;
    private final CharacterView personnageView;
    private final CharacterController personnageController;
    private final PhysicsWorld physic;
    private final World world;
    private final Body water;
    private final WaterSensorListener wSL;
    private final ConsoleInputController consoleInputController;
    private final ConsolePanel consolePanel;
    private final ConsoleView consoleView;

    private final TeamModel teamModel1;
    private final TeamModel teamModel2;  

    private int activeWormIndex;
    private CharacterController activeWormController;  
    
    private final CharacterModel[] allWormsCharacter;
    private final ArrayList<CharacterModel> allWorms;

    public static final int pixelX = 1280;
    public static final int pixelY = 720;

    // Conversion de Pixel en Case :
    //public static final int echelleConsole = 10; // on divise toute valeur en pixel par 10

    /**
     * Initialise le jeu de la meme maniere que GameManager
     * Mais en l'adaptant pour Lanterna
     * Les XCase et YCase seront supprimer d'ici au SPRINT 3 
     * @param consoleView recupere le terminal graphique
     */
    public ConsoleGameManager(ConsoleView consoleView){   // equivalent au game manager

        this.consoleView = consoleView;
        consoleView.clearRefresh();

        // il faut pas appliquer la conversion ici 
        int[] groundY = { 400, 420, 450, 500, 550, 540, 530, 450, 350, 300, 250, 230, 200, 230};
        this.model = new GroundModel(groundY, pixelX, pixelY, pixelX - pixelX/3);   // car de haut en bas (ancienne valeur de water = 500)

        this.physic = new PhysicsWorld();
        this.world = physic.getWorld();
        physic.createGroundFromModel(model);
        this.water = physic.createBodyWater(model);

        this.personnageModel = new CharacterModel(model, world, pixelX, pixelY/3);
        this.personnageView = new CharacterView();
        this.personnageController = new CharacterController(personnageModel);

        physic.limitPanel(model);

        // création premiere equipe
        int wormNumber = 3; 
        this.teamModel1 = new TeamModel(model, world, "Team1", 100, 98, wormNumber);

        // création seconde equipe
        this.teamModel2 = new TeamModel(model, world, "Team2", 400, 158, wormNumber);  

        this.allWorms = new ArrayList<>();
        allWorms.add(personnageModel);  // Worm de test jaune
        for (int i = 0; i < wormNumber; i++) {
            allWorms.add(teamModel1.getWorm(i));
            allWorms.add(teamModel2.getWorm(i));
        }

        this.activeWormIndex = 0;
        this.activeWormController = new CharacterController(teamModel1.getWorm(activeWormIndex));

        this.allWormsCharacter = new CharacterModel[teamModel1.getTabWorms().length + teamModel2.getTabWorms().length];
        System.arraycopy(teamModel1.getTabWorms(), 0, this.allWormsCharacter, 0, teamModel1.getTabWorms().length);
        System.arraycopy(teamModel2.getTabWorms(), 0, this.allWormsCharacter, teamModel1.getTabWorms().length, teamModel2.getTabWorms().length);
        
        activeWormController.setWorld(world);
        activeWormController.setAllWorms(this.allWormsCharacter);

        this.wSL = physic.new WaterSensorListener(water, allWorms, new ArrayList<CharacterModel>());
        world.setContactListener(wSL);

        this.consolePanel = new ConsolePanel(model);
        this.consoleInputController = new ConsoleInputController(activeWormController);

        

        consoleView.refresh();
    }

    /**
     * Est appeler dans le ConsoleGameLoop
     */
    public void draw(){
        this.consolePanel.drawGround(consoleView.getTextGraphics(),ConsoleView.getXCase(), ConsoleView.getYCase());
        this.consolePanel.drawAllCharacter(consoleView.getTextGraphics(),allWorms);
    }

    public ConsolePanel getConsolePanel(){
        return this.consolePanel;
    }

    public CharacterController getCharacterController(){
        return this.personnageController;
    }

    public ConsoleInputController getConsoleInputController(){
        return this.consoleInputController;
    }

    public ConsoleView getConsoleView(){
        return this.consoleView;
    }

    public World getWorld(){
        return this.world;
    }

    public CharacterController getActiveWormController(){
        return this.activeWormController;
    }
}

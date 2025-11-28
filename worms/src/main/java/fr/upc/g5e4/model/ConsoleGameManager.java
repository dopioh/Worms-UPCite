package fr.upc.g5e4.model;

import java.util.ArrayList;

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

    // Conversion de Pixel en Case :
    public static final int echelleConsole = 10; // on divise toute valeur en pixel par 10

    /**
     * Initialise le jeu de la meme maniere que GameManager
     * Mais en l'adaptant pour Lanterna
     * Les XCase et YCase seront supprimer d'ici au SPRINT 3 
     * @param consoleView recupere le terminal graphique
     */
    public ConsoleGameManager(ConsoleView consoleView){   // equivalent au game manager

        this.consoleView = consoleView;
        consoleView.clearRefresh();
        int XCase = consoleView.getXCase();
        int YCase = consoleView.getYCase();

        // il faut pas appliquer la conversion ici 
        int[] groundY = { 400/YCase, 420/YCase, 450/YCase, 500/YCase, 550/YCase, 540/YCase, 530/YCase, 450/YCase, 350/YCase, 300/YCase, 250/YCase, 230/YCase, 200/YCase, 230/YCase};
        this.model = new GroundModel(groundY, XCase, YCase, YCase - YCase/3);   // car de haut en bas (ancienne valeur de water = 500)

        this.physic = new PhysicsWorld();
        this.world = physic.getWorld();
        physic.createGroundFromModel(model);
        this.water = physic.createBodyWater(model);

        this.personnageModel = new CharacterModel(model, world, XCase/2, YCase/3);
        this.personnageView = new CharacterView();
        this.personnageController = new CharacterController(personnageModel);

        physic.limitPanel(model);
        this.wSL = physic.new WaterSensorListener(water, personnageModel, new ArrayList<CharacterModel>());
        world.setContactListener(wSL);

        this.consolePanel = new ConsolePanel(model, personnageModel, XCase, YCase);
        this.consoleInputController = new ConsoleInputController(personnageController);

        

        consoleView.refresh();
    }

    public void draw(){
        this.consolePanel.drawGround(consoleView.getTextGraphics());
        //this.consolePanel.drawCharacter(consoleView.getTextGraphics());
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
}

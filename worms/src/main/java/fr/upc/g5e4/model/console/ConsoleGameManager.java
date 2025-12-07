package fr.upc.g5e4.model.console;

import java.util.ArrayList;


import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import fr.upc.g5e4.controller.*;
import fr.upc.g5e4.controller.console.ConsoleInputController;
import fr.upc.g5e4.view.*;
import fr.upc.g5e4.view.console.ConsoleGamePanel;
import fr.upc.g5e4.view.console.ConsoleView;
import fr.upc.g5e4.model.CharacterModel;
import fr.upc.g5e4.model.GameManager;
import fr.upc.g5e4.model.GroundModel;
import fr.upc.g5e4.model.PhysicsWorld;
import fr.upc.g5e4.model.TeamModel;
//import fr.upc.g5e4.model.*;
import fr.upc.g5e4.model.PhysicsWorld.WaterSensorListener;
import fr.upc.g5e4.model.weapons.Ak47;

//import com.googlecode.lanterna.screen.Screen;
//import com.googlecode.lanterna.terminal.Terminal;

/**
 * Gere la logique d'une partie dans le terminal
 * Ils manquent des fonctionnalites pour le moment
 * Seront ajouter au SPRINT 3
 * 
 * @see ConsoleGameLoop class qui est appeler la bas
 * @see GameManager pour voir la version avec Swing (Interface graphique)
 */
public class ConsoleGameManager {
    // private final GamePanel gamePanel; Swing donc on peut pas le mettre ici, il
    // faut reecrire les mathodes
    private final GroundModel model;

    private final CharacterModel personnageModel;
    private final CharacterView personnageView;
    private final CharacterController personnageController;
    private final PhysicsWorld physic;
    private final World world;
    private final Body water;
    private final WaterSensorListener wSL;
    private final ConsoleInputController consoleInputController;
    private final ConsoleGamePanel consolePanel;
    private final ConsoleView consoleView;

    private final TeamModel teamModel1;
    private final TeamModel teamModel2;

    private int activeWormIndex;
    private CharacterController activeWormController;

    private final CharacterModel[] allWormsCharacter;
    private final ArrayList<CharacterModel> allWorms;

    private int roundNumber = 1;
    private int currentTeamIndex = 0; // 0 = Team1, 1 = Team2

    public static final int pixelX = 1280;
    public static final int pixelY = 720;

    // Conversion de Pixel en Case :
    // public static final int echelleConsole = 10; // on divise toute valeur en
    // pixel par 10

    /**
     * Initialise le jeu de la meme maniere que GameManager
     * Mais en l'adaptant pour Lanterna
     * Les XCase et YCase seront supprimer d'ici au SPRINT 3
     * 
     * @param consoleView recupere le terminal graphique
     */
    public ConsoleGameManager(ConsoleView consoleView) { // equivalent au game manager

        this.consoleView = consoleView;
        consoleView.clearRefresh();

        // il faut pas appliquer la conversion ici
        int[] groundY = { 400, 420, 450, 500, 550, 540, 530, 450, 350, 300, 250, 230, 200, 230 };
        this.model = new GroundModel(groundY, pixelX, pixelY, pixelX - pixelX / 3); // car de haut en bas (ancienne
                                                                                    // valeur de water = 500)

        this.physic = new PhysicsWorld();
        this.world = physic.getWorld();
        physic.createGroundFromModel(model);
        this.water = physic.createBodyWater(model);

        this.personnageModel = new CharacterModel(model, world, pixelX, pixelY / 3, true);
        this.personnageView = new CharacterView();
        this.personnageController = new CharacterController(personnageModel);

        physic.limitPanel(model);

        // création premiere equipe
        int wormNumber = 3;
        this.teamModel1 = new TeamModel(model, world, "Team1", 100, 98, wormNumber, true);

        // création seconde equipe
        this.teamModel2 = new TeamModel(model, world, "Team2", 400, 158, wormNumber, true);

        this.allWorms = new ArrayList<>();
        allWorms.add(personnageModel); // Worm de test jaune
        for (int i = 0; i < wormNumber; i++) {
            allWorms.add(teamModel1.getWorm(i));
            allWorms.add(teamModel2.getWorm(i));
        }

        this.activeWormIndex = 0;
        this.activeWormController = new CharacterController(teamModel1.getWorm(activeWormIndex));

        this.allWormsCharacter = new CharacterModel[teamModel1.getTabWorms().length + teamModel2.getTabWorms().length];
        System.arraycopy(teamModel1.getTabWorms(), 0, this.allWormsCharacter, 0, teamModel1.getTabWorms().length);
        System.arraycopy(teamModel2.getTabWorms(), 0, this.allWormsCharacter, teamModel1.getTabWorms().length,
                teamModel2.getTabWorms().length);

        activeWormController.setWorld(world);
        activeWormController.setAllWorms(this.allWormsCharacter);

        this.wSL = physic.new WaterSensorListener(water, allWorms, new ArrayList<CharacterModel>());
        world.setContactListener(wSL);

        this.consolePanel = new ConsoleGamePanel(model);
        this.consoleInputController = new ConsoleInputController(activeWormController, this);

        consoleView.refresh();
    }

    /**
     * Est appeler dans le ConsoleGameLoop
     */
    public void draw() {
        this.consolePanel.drawGround(consoleView.getTextGraphics(), ConsoleView.getXCase(), ConsoleView.getYCase());
        this.consolePanel.drawAllCharacter(consoleView.getTextGraphics(), teamModel1, teamModel2);
    }

    public ConsoleGamePanel getConsolePanel() {
        return this.consolePanel;
    }

    public CharacterController getCharacterController() {
        return this.personnageController;
    }

    public ConsoleInputController getConsoleInputController() {
        return this.consoleInputController;
    }

    public ConsoleView getConsoleView() {
        return this.consoleView;
    }

    public World getWorld() {
        return this.world;
    }

    public CharacterController getActiveWormController() {
        return this.activeWormController;
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
                // activeWormController.setItemController(this.itemController);
                activeWormController.setWorld(world);
                activeWormController.setAllWorms(this.allWormsCharacter);
                // activeWormController.setCurrentWeapon(new Ak47());

                consoleInputController.setCharacterController(activeWormController);
                // cameraController.setTargetCharacter(worm);
                // cameraController.centerOnCharacter();
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

    public void statsPartie() {
        String team;
        CharacterModel cmActuel;
        if (currentTeamIndex == 0) {
            team = "Team1";
            cmActuel = teamModel1.getWorm(activeWormIndex);
        } else {
            team = "Team2";
            cmActuel = teamModel2.getWorm(activeWormIndex);
        }
        // String team = currentTeamIndex == 0 ? "Team1" : "Team2";
        consoleView.printStatsInGame(roundNumber, team, activeWormIndex, cmActuel); // les worms n'ont pas de noms dans la version terminal

    }

}

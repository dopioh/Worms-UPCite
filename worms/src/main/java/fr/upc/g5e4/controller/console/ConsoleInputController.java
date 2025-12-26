package fr.upc.g5e4.controller.console;

import java.io.IOException;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
//import com.googlecode.lanterna.terminal.Terminal;

import fr.upc.g5e4.controller.CharacterController;
import fr.upc.g5e4.controller.SwingInputController;
import fr.upc.g5e4.model.console.ConsoleGameManager;
import fr.upc.g5e4.model.console.ConsoleLogics;

/**
 * Gere les input clavier
 * Version lanterna de InputController qui etait avec Swing
 * 
 * @see SwingInputController pour voir la version original
 * 
 * @author Mei
 */
public class ConsoleInputController {

    private CharacterController personnageController;
    private ConsoleGameManager consoleGameManager;

    private long lastKeyPressTime = 0;
    private static final long INPUT_TIMEOUT = 100; // 100ms de tolérance

    private boolean EnterKey; // on peut initialiser a false aussi 

    public ConsoleInputController(CharacterController personnageController, ConsoleGameManager consoleGameManager) {
        this.personnageController = personnageController;
        this.consoleGameManager = consoleGameManager;
        this.EnterKey = false;
    }

    public boolean getEnterKey() {
        return this.EnterKey;
    }

    public void setEnterPressed(boolean b) {
        this.EnterKey = b;
    }

    public void setCharacterController(CharacterController controller) {
        this.personnageController = controller;
    }

    /**
     * Key controle pour la partie jeu, apres le menu
     * Permet de faire bouger les worms
     */
    public void addControlsGame(Screen screen) {
        try {
            // System.out.println("DEBUG : addControlsGame de ConsoleInputController.java");
            KeyStroke key = screen.pollInput();

            if (key != null) {
                keyPressedPerso(key);
                lastKeyPressTime = System.currentTimeMillis(); // On met à jour l'heure
            }

            // systeme de ms ecoulee pour relacher la key
            // car lanterna n'arrive pas suivre sinon et certaine touche ne fonctionne pas a certaines frames
            if (System.currentTimeMillis() - lastKeyPressTime > INPUT_TIMEOUT) {
                this.personnageController.deactivateJump();
                this.personnageController.deactivateRight();
                this.personnageController.deactivateLeft();
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Methode interne de addControleGame
     * @see addControlsGame
     * 
     * Recupere le type de la touche appuyee
     * Pour l'envoyer dans une autre fonction
     * @param key touche appuyee
     */
    private void keyPressedPerso(KeyStroke key) { // Only Game
        if (key == null) {
            return;
        }
        switch (key.getKeyType()) {
            case Character:
                charPressed(key);
                break;
            default:
                break;
        }
    }

    /**
     * Methode interne de addControleGame
     * @see addControlsGame
     * 
     * Recupere une touche dont on sait que c'est une lettre (Character)
     * Pour determiner quelle lettre c'est et active l'action associee
     * @param key touche appuyee
     */
    private void charPressed(KeyStroke key) {
        switch (key.getCharacter()) {
            case 'z':
                this.personnageController.activateJump();
                //System.out.println("Z pressed");

                break;
            case 'd':
                this.personnageController.activateRight();
                System.out.println("D pressed");

                break;
            case 'q':
                personnageController.activateLeft();
                //System.out.println("Q pressed");

                break;
            case 'a':
                consoleGameManager.nextRound();
                break;
            case 'e':
                //System.out.println("Tir - Model ID: " + personnageController.getModel().getID() + " - facingRight: " + personnageController.getModel().isFacingRight());
                int direction;
                if (personnageController.getModel().isFacingRight()){
                    direction = 1;
                } else {
                    direction = -1;
                    //System.out.println("[Direction Gauche Called]");
                }
                personnageController.fireWeapon(direction);
                System.out.println("TIREEEE");
                break;
        }
    }

    /**
     * Recupere chaque char pour former une ligne de caracteres plus tard
     * 
     * @param screen
     * @return chaque lettre entrer pour la concatener en String et voir si ca fait "launch game"
     * @see ConsoleLogics
     */
    public Character addLetters(Screen screen) {
        try {
            KeyStroke key = screen.readInput();

            if (key.getKeyType().equals(KeyType.Character)) {
                return key.getCharacter();

            } else if (key.getKeyType().equals(KeyType.Enter)) {
                setEnterPressed(true);
                return null;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return null;
    }

}

package fr.upc.g5e4.controller;

import java.io.IOException;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
//import com.googlecode.lanterna.terminal.Terminal;

import fr.upc.g5e4.model.ConsoleGameManager;
import fr.upc.g5e4.model.ConsoleLogics;

/**
 * Gere les input clavier 
 * Version lanterna de InputController qui etait avec Swing
 * @see SwingInputController pour voir la version original
 */
public class ConsoleInputController{
        private CharacterController personnageController;
        private ConsoleGameManager consoleGameManager;

        private boolean EnterKey;

        public ConsoleInputController(CharacterController personnageController,ConsoleGameManager consoleGameManager){
            this.personnageController = personnageController;
            this.consoleGameManager = consoleGameManager;
            this.EnterKey = false;
        }

        public boolean getEnterKey(){
            return this.EnterKey;
        }

        public void setEnterPressed(boolean b){
            this.EnterKey = b;
        }

        /**
         * Key controle pour la partie jeu, apres le menu
         */
        public void addControlsGame(Screen screen){
            try {
                this.personnageController.deactivateJump();
                this.personnageController.deactivateRight();
                this.personnageController.deactivateLeft();
                //System.out.println("DEBUG : addControlsGame de ConsoleInputController.java");
                KeyStroke key = screen.pollInput();
                keyPressedPerso(key);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        private void keyPressedPerso(KeyStroke key){  // Only Game
            if (key == null){
                return;
            }
            switch (key.getKeyType()) {
                case Character: charPressed(key);
                    break;            
                default: 
                    break;
            }
        }

        private void charPressed(KeyStroke key){
            switch (key.getCharacter()) {
                case 'z': 
                    this.personnageController.activateJump();
                    //System.out.println("Z pressed");
                   
                    break;
                case 'd' : 
                    this.personnageController.activateRight();
                    //System.out.println("D pressed");
                    
                    break;
                case 'q' : 
                    personnageController.activateLeft();
                    
                    break;
                case 'a':
                        consoleGameManager.nextRound();
                        break;
                default:
                    break;
            }
        }


        /**
         * Recupere chaque char pour former une ligne de caracteres plus tard
         * @param screen
         * @return chaque lettre entrer pour la concatener en String et voir si ca fait "launch game"
         * @see ConsoleLogics
         */
        public Character addLetters(Screen screen){   
            try {
                KeyStroke key = screen.readInput();
                if (key.getKeyType().equals(KeyType.Character)){
                    return key.getCharacter();
                } else if (key.getKeyType().equals(KeyType.Enter)){
                    setEnterPressed(true);
                    return null;
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return null;
            }
            return null;
        }
        // on doit ajouter des methodes pour enlever les addControls avec les key relier,
        // pour quand on switch entre menu et jeu

        public void setCharacterController(CharacterController controller) {
        this.personnageController = controller;
    }
    
}

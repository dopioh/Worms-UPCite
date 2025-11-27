package fr.upc.g5e4.controller;

import java.io.IOException;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
//import com.googlecode.lanterna.terminal.Terminal;

public class ConsoleInputController{
        private CharacterController personnageController;

        private boolean EnterKey;

        public ConsoleInputController(CharacterController personnageController){
            this.personnageController = personnageController;
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
                default:    // on le met ou pas
                    break;
            }
        }

        private void charPressed(KeyStroke key){
            switch (key.getCharacter()) {
                case 'z': 
                    this.personnageController.activateJump();
                    this.personnageController.deactivateJump();
                    break;
                case 'd' : 
                    this.personnageController.activateRight();
                    this.personnageController.deactivateRight();
                    break;
                case 'q' : 
                    personnageController.activateLeft();
                    personnageController.deactivateLeft();
                    break;
                default:    // il se passe rien, dcp est ce qu'il faut vrmt le mettre ?
                    break;
            }
        }


        public Character addLetters(Screen screen){   // il faudrait faire en sorte ici que le joueur puisse ecrire "lunch" et dcp lancer une game -> se fait pas ici ? Model ?
            try {
                KeyStroke key = screen.readInput(); // en continuiter ou une seule key a la fois ?
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

        /*private String writeUntillEnter(KeyStroke key){
            if (!(key.getKeyType().equals(KeyType.Character))){
                return "";  // si je retuirn un char je peux pas return rien ici
            }
            return key.getCharacter().toString();
        }*/

        // on doit ajouter des methodes pour enlever les addControls avec les key relier,
        // pour quand on switch entre menu et jeu
    
}

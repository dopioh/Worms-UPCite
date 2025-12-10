package fr.upc.g5e4.model.console;

//import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

//import com.googlecode.lanterna.screen.Screen;

import fr.upc.g5e4.controller.*;
import fr.upc.g5e4.controller.console.ConsoleInputController;
import fr.upc.g5e4.view.console.ConsoleView;

import com.googlecode.lanterna.screen.Screen;

/**
 * Gere la logique du terminal
 * Permet de dire au manager quand il peut lancer le jeu
 * @see ConsoleGameManager
 * 
 * @author Mei
 */
public class ConsoleLogics {
    private ConsoleInputController consoleInput;
    private CharacterController characterController;
    private ConsoleView consoleView;
    private Screen screen;

    public ConsoleLogics(CharacterController characterController){
        this.characterController = characterController;
        this.consoleView = new ConsoleView();
        this.screen = consoleView.getScreen();
        this.consoleInput = new ConsoleInputController(characterController, null);
    }

    public ConsoleView getConsoleView(){
        return consoleView;
    }

    /**
     * Recupere la ligne entre avec {@code word}
     * @return vrai si l'utilisateur a ecrit "launch game"
     * @see ConsoleLauncher
     */
    /**
     * Recupere la ligne entre avec {@code word}
     * @return vrai si l'utilisateur a ecrit "launch game"
     * @see ConsoleLauncher
     */
    public boolean launchGame() {

        String input = word(screen); // stock l'entree de l'utilisateur

        while (!input.equals("launch game")) {
            
            consoleView.clearRefresh();
            consoleView.menu();
            
            // pour que lorsque on re ecrit
            // on reparte de en bas a gauche
            ConsoleView.positionLettre = 0;  
            consoleView.refresh();

            input = word(screen);
        }

        return true;
    }

    /**
     * Methode interne de launchGame() qui stock un mot jusqu'a qu'on appuie sur entree
     * @param screen pour afficher sur l'ecran les entree clavier
     * @return la ligne de mot entree pour l'envoyer dans {@code launchGame}
     * 
     * @see ConsoleView#printLetterMenu pour voir la methode qui affiche les lettres
     */
    private String word(Screen screen){
        try {
            String word = "";   
            while (consoleInput.getEnterKey() == false){
                Character lettre = consoleInput.addLetters(screen);
                if (lettre != null){
                    word += lettre;
                consoleView.printLetterMenu(lettre);
                }
            }
            consoleInput.setEnterPressed(false);
            return word;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return "";
    }
}

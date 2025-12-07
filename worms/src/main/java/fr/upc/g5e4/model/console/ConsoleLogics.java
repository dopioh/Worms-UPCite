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
    public boolean launchGame(){
        if (word(screen).equals("launch game")){    
            return true;
        } else {
            /**
             * A fix :
             * on doit appuyer deux fois sur entree pour refresh quand c'est pas launch game 
             * si je creer une variable String word = word(screen); 
             * ca actualise pas si le mot est faux -> boucle infini
             */
            while (!(word(screen).equals("launch game"))){ 
            consoleView.clearRefresh();
            consoleView.menu();
            this.consoleInput = new ConsoleInputController(characterController, null);
            ConsoleView.positionLettre = 0;
            consoleView.refresh();
            }
            return true;
        }
    }

    /**
     * 
     * @param screen pour afficher sur l'ecran les entree clavier
     * @return la ligne de mot entree pour l'envoyer dans {@code launchGame}
     */
    private String word(Screen screen){
        try {
            // pour le moment on mets un string classique, on mettra un StringBuffer plus tard
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

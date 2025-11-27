package fr.upc.g5e4.model;

//import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

//import com.googlecode.lanterna.screen.Screen;

import fr.upc.g5e4.controller.*;
import fr.upc.g5e4.view.ConsoleView;

import com.googlecode.lanterna.screen.Screen;

public class ConsoleLogics {
    private ConsoleInputController consoleInput;
    private CharacterController characterController;
    private ConsoleView consoleView;
    private Screen screen;
    //private CharacterModel characterModel;

    public ConsoleLogics(CharacterController characterController){
        this.characterController = characterController;
        this.consoleView = new ConsoleView();
        this.screen = consoleView.getScreen();
        this.consoleInput = new ConsoleInputController(characterController);
    }

    public ConsoleView getConsoleView(){
        return consoleView;
    }

    public boolean launchGame(){
        if (word(screen).equals("launch game")){    
            return true;
        } else {
            /**
             * on doit appuyer deux fois sur entree pour refresh quand c'est pas launch game
             * si je creer une variable String word = word(screen); 
             * ca actualise pas si le mot est faux -> boucle infini
             */
            while (!(word(screen).equals("launch game"))){ 
            consoleView.clearRefresh();
            consoleView.menu();
            this.consoleInput = new ConsoleInputController(characterController);
            ConsoleView.positionLettre = 0;
            consoleView.refresh();
            }
            return true;
        }
    }

    private String word(Screen screen){
        try {
        String word = "";   // pour le moment on mets un string classique, on mettra un StringBuffer plus tard
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

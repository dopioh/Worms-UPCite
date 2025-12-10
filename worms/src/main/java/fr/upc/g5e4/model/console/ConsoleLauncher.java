package fr.upc.g5e4.model.console;

import fr.upc.g5e4.view.console.ConsoleView;

public class ConsoleLauncher {
    private ConsoleGameManager consoleGameManager;
    private ConsoleView consoleView;
    private ConsoleLogics consoleLogics;
    private ConsoleGameLoop consoleGameLoop;

    /**
     * Initialise le jeu console 
     * Gere la transition entre le menu et la partie
     * 
     * @author Mei
     */
    public ConsoleLauncher(){
        // null car le parametre ici sert a faire bouger le perso, or dans le menu il n'y a pas de game et donc pas de perso
        this.consoleLogics = new ConsoleLogics(null);
        this.consoleView = consoleLogics.getConsoleView();
        consoleView.menu();
        if (consoleLogics.launchGame()){
            this.consoleGameManager = new ConsoleGameManager(this.consoleView);
            this.consoleGameLoop = new ConsoleGameLoop(consoleGameManager);
            this.consoleGameLoop.start();
        }
    }
}

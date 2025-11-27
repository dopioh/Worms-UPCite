package fr.upc.g5e4.model;

import java.io.IOException;

import fr.upc.g5e4.view.ConsoleView;

public class ConsoleLauncher {
    private ConsoleGameManager consoleGameManager;
    private ConsoleView consoleView;
    private ConsoleLogics consoleLogics;
    private ConsoleGameLoop consoleGameLoop;


    public ConsoleLauncher(){
        // null car le parametre ici sert a faire bouger le perso, or dans le menu il n'y a pas de game et donc pas de perso
        this.consoleLogics = new ConsoleLogics(null);
        this.consoleView = consoleLogics.getConsoleView();
        consoleView.menu();
        if (consoleLogics.launchGame()){
        this.consoleGameManager = new ConsoleGameManager(this.consoleView); // ici ca ouvre une nouvelle fenetre alors que on veut juste effacer tout
        this.consoleGameLoop = new ConsoleGameLoop(consoleGameManager);
        this.consoleGameLoop.start();
        }
    }


    public static void main(String[] args) {
        new ConsoleLauncher();
    }
}

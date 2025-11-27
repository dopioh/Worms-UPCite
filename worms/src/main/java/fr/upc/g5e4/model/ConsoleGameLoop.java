package fr.upc.g5e4.model;

import fr.upc.g5e4.GameLoop;
import fr.upc.g5e4.controller.CharacterController;
import fr.upc.g5e4.view.ConsolePanel;

/**
 * Adaptation de la classe GameLoop pour la vue terminal avec Lanterna
 * Fonctionne avec des thread et pas un Timer deja defini dans la librairie
 * @see GameLoop
 */
public class ConsoleGameLoop{
    private ConsoleGameManager consoleGameManager;
    //private ConsolePanel consolePanel;

    private Thread gameThread;
    private boolean running;

    private static final float FIXHZ = 1f/60f;  //60 images par secondes

    public ConsoleGameLoop(ConsoleGameManager consoleGameManager){
        this.consoleGameManager = consoleGameManager;
        //this.consolePanel = consoleGameManager.getConsolePanel();
        this.running = false;
        //System.out.println("Constructeur ConsoleGameLoop");
    }

    public void gameLoop(){
        while (running) {
            long startTime = System. currentTimeMillis();
            //System.out.println("Run");
            consoleGameManager.getConsoleInputController().addControlsGame(consoleGameManager.getConsoleView().getScreen());

            consoleGameManager.getCharacterController().update();
            consoleGameManager.getWorld().step(FIXHZ, 8, 3);
            consoleGameManager.getConsoleView().getScreen().clear();
            consoleGameManager.draw();
            consoleGameManager.getConsoleView().refresh();

            long elapsedTime = System.currentTimeMillis() - startTime;
            long sleepTime = (long)(FIXHZ*1000) - elapsedTime;
            
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);  // pause pour atteindre 60 FPS
                } catch (InterruptedException e) {
                    running = false;  // si interrompu, on arrête
                }
            }
        }
    }

    public void start(){
        System.out.println("start");
        if (running){
            //System.out.println("deja run");
            return;
        }
        //System.out.println("start");
        this.running = true;
        this.gameThread = new Thread(new Runnable() {
            @Override
            public void run(){
                //System.out.println("start");
                gameLoop();
            }
        });
        gameThread.start();
    }

    public void stop(){
        this.running = false;
    }
}

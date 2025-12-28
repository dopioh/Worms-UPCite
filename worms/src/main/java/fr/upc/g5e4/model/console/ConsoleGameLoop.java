package fr.upc.g5e4.model.console;

import fr.upc.g5e4.model.GameLoop;

/**
 * Adaptation de la classe GameLoop pour la vue terminal avec Lanterna
 * Fonctionne avec des thread et pas un Timer deja defini dans la librairie
 * @see GameLoop pour la version avec Swing/AWT
 */
public class ConsoleGameLoop{
    private ConsoleGameManager consoleGameManager;

    private Thread gameThread;
    private boolean running;

    private static final float FIXHZ = 1f/60f;  //60 images par secondes

    public ConsoleGameLoop(ConsoleGameManager consoleGameManager){
        this.consoleGameManager = consoleGameManager;
        this.running = false;
    }

    /**
     * Boucle du jeu (60 fps)
     * {@see sleepTime} gere la framerate
     * Sans ca, les fps vont bien au dessus de 60
     *  - Gachis de perf
     *  - le {@code screen} s'intille psq lanterna arrive pas a suivre
     */
    public void gameLoop(){
        while (running) {
            long startTime = System.currentTimeMillis();
            //System.out.println("Run");
            consoleGameManager.getConsoleInputController().addControlsGame(consoleGameManager.getConsoleView().getScreen());
            if (consoleGameManager.getActiveWormController() != null) {
                //System.out.println("ActiveWormController n'est pas nul");
                consoleGameManager.getActiveWormController().update();
            }

            consoleGameManager.getCharacterController().update();

            // Met a jour les projectiles (16ms = 0.016s)
            consoleGameManager.getItemController().updateProjectiles(0.016f);

            consoleGameManager.getWorld().step(FIXHZ, 8, 3);
            consoleGameManager.getConsoleView().getScreen().clear();

            // Graphismes -> on pourrait mettre ca dans GameManager aussi ? 
            consoleGameManager.draw();
            consoleGameManager.statsPartie();

            consoleGameManager.getConsoleView().refresh();

            long elapsedTime = System.currentTimeMillis() - startTime;  // duree de temps en ms entre le debut de la loop et ici

            // FIXHZ (en sec) * 1000 pour passer en ms
            // on a la meme unite de mesure que elapsedTime
            long sleepTime = (long)(FIXHZ*1000) - elapsedTime; // le temps que le Thread devra sleep pour rester a 60 fps
            
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);  // pause pour atteindre 60 FPS
                } catch (InterruptedException e) {
                    running = false;  // si interrompu, on arrête
                }
            }
        }
    }

    /**
     * Utilse un thread pour faire tourner la loop en continue 
     * On peut pas utiliser de Listener deja dans la lirairie comme Swing
     */
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

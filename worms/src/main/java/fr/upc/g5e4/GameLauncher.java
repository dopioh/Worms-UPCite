package fr.upc.g5e4;

import javax.swing.*;

import fr.upc.g5e4.model.GamePanel;


/** Class qui lance le jeu, elle set la taille de l'ecran (grace a son heritage de JFrame) */
public class GameLauncher{
    // classe pour lancer le jeu -> elle va instancier le GamePanel
    private final GameManager gameManager;
    private final GamePanel gamePanel;
    private final GameLoop gameLoop;

    private boolean screen;

    public GameLauncher(boolean screen){
        this.screen = screen;
        if (screen){
            int sizeX = 800;
            int sizeY = 600;
            JFrame jeu = new JFrame();

            jeu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jeu.setTitle("Prototype");
            jeu.setSize(sizeX,sizeY);         

            this.gameManager = new GameManager();
            this.gamePanel = gameManager.getGamePanel();
            jeu.add(gamePanel);
            jeu.pack();
            jeu.setLocationRelativeTo(null);
            jeu.setResizable(false);

            jeu.setVisible(true);

            // le clavier focus sur l'imput controller de this.gameManager
            this.gameManager.getImputController().requestFocusInWindow();

            this.gameLoop = new GameLoop(gameManager);
        }
        else{
            this.gameManager = new GameManager();
            this.gamePanel = gameManager.getGamePanel();
            this.gameLoop = new GameLoop(gameManager);
        }
    }

    public GameManager getGameManager(){
        return this.gameManager;
    }

    public static void main(String[] args){
        new GameLauncher(true);
        
    }
}

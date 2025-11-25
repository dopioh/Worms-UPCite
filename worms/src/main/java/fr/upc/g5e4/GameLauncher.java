package fr.upc.g5e4;

import java.io.IOException;

import javax.swing.*;
import fr.upc.g5e4.model.GamePanel;
import fr.upc.g5e4.model.InventoryModel.InventoryRoot;
import fr.upc.g5e4.view.InventoryUI;
import fr.upc.g5e4.model.*; //J'ai ajouté pour tester (Hugo)

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
            this.gameLoop.timerStart();
        }
        else{
            this.gameManager = new GameManager();
            this.gamePanel = gameManager.getGamePanel();
            this.gameLoop = new GameLoop(gameManager);
            this.gameLoop.timerStart();
        }
    }

    public GameManager getGameManager(){
        return this.gameManager;
    }

    public GameLoop getGameLoop(){
        return this.gameLoop;
    }

    void testInventaire() throws IOException{        //il faut rajouter throws IOException pour le main aussi pour utiliser
        InventoryRoot root = InventoryModel.load("worms/src/main/resources/inventory.json");
        InventoryUI ui = new InventoryUI(null, root.inventory);

        //Tue le programme après fermeture de la fenetre
        ui.addWindowListener(new java.awt.event.WindowAdapter(){
            @Override
            public void windowClosed(java.awt.event.WindowEvent e){
                System.exit(0);
            }
        });

        ui.setVisible(true);

        /* Plus tard 
         * for(InventoryJson item : inventoryRoot.inventory){
         *     item.resetTurn();
         * }
         */
    }

    public static void main(String[] args){
        new GameLauncher(true);
        
    }
}

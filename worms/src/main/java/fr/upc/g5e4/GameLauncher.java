package fr.upc.g5e4;

import java.io.IOException;

import javax.swing.*;

import fr.upc.g5e4.model.InventoryModel.InventoryRoot;
import fr.upc.g5e4.view.InventoryUI;
import fr.upc.g5e4.view.MainMenuView;
import fr.upc.g5e4.controller.MainMenuController;
import fr.upc.g5e4.model.*; //J'ai ajouté pour tester (Hugo)


/** Classe qui lance le jeu, elle set la taille de l'ecran (grace a son heritage de JFrame) */
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
            JFrame game = new JFrame();

            game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            game.setTitle("Prototype");
            game.setSize(sizeX,sizeY);         

            this.gameManager = new GameManager();
            this.gamePanel = gameManager.getGamePanel();
            game.add(gamePanel);
            game.pack();
            game.setLocationRelativeTo(null);
            game.setResizable(false);

            game.setVisible(true);

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

    
    /**  Affiche l'écran d'accueil au démarrage  */
    private static void showAccueil() {

        SwingUtilities.invokeLater(() -> { //Mets cette tâche dans la file d'attente pour que l'interface la traite dès que possible

            MainMenuView view = new MainMenuView(); 
            new MainMenuController(view);
            view.setVisible(true);

        });
    }

    public static void main(String[] args){
        //// Lance l'écran d'accueil au lieu du jeu directement
        showAccueil();

        //// Pour lancer le jeu directement (pour les testes)
        //new GameLauncher(true);
    }
}

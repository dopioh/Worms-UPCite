git package fr.upc.g5e4;
import java.io.IOException;

import javax.swing.*;
import java.awt.event.ActionEvent;

import fr.upc.g5e4.model.InventoryModel.InventoryRoot;
import fr.upc.g5e4.view.*;
import fr.upc.g5e4.controller.*;
import fr.upc.g5e4.model.*; 


/** Classe qui lance le jeu, elle set la taille de l'ecran (grace a son heritage de JFrame) */
public class GameLauncher{
    // classe pour lancer le jeu -> elle va instancier le GamePanel
    private final GameManager gameManager;
    private final GamePanel gamePanel;
    private final GameLoop gameLoop;

    private InventoryOverlay inventoryOverlay; 

    private boolean screen;

    public GameLauncher(boolean screen) throws IOException{
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

            /* ------------------------ Partie Overlay Inventaire --------------------------------- */
            //Charge l'inventaire 
            InventoryRoot root = InventoryModel.load("worms/src/main/resources/inventory.json");
            //Créer l'overlay
            this.inventoryOverlay = new InventoryOverlay((root.inventory));
            //Créer un layeredPane
            JLayeredPane layered = new JLayeredPane();
            layered.setPreferredSize(new java.awt.Dimension(sizeX,sizeY));
            //placer dedans
            gamePanel.setBounds(0,0,sizeX,sizeY);
            inventoryOverlay.setBounds(0,0,sizeX,sizeY);

            layered.add(gamePanel, JLayeredPane.DEFAULT_LAYER);
            layered.add(inventoryOverlay, JLayeredPane.PALETTE_LAYER);
            game.setContentPane(layered);
            /* Fin de partie */

            //Touches raccourcies
            setupKeyBindings(layered, game);
            
            //game.add(gamePanel);
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

    private void setupKeyBindings(JComponent root, JFrame game) {
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        // Touche ESC -> quitter le jeu
        im.put(KeyStroke.getKeyStroke("ESCAPE"), "exitGame");
        am.put("exitGame", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.dispose();
                System.exit(0);
            }
        });

        // Touche I -> ouvrir / fermer l'inventaire
        im.put(KeyStroke.getKeyStroke('i'), "toggleInventory");
        am.put("toggleInventory", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (inventoryOverlay != null) {
                    boolean visible = inventoryOverlay.isInventoryVisible();
                    inventoryOverlay.setInventoryVisible(!visible);
                }
            }
        });
    }

    
    public GameManager getGameManager(){
        return this.gameManager;
    }

    public GameLoop getGameLoop(){
        return this.gameLoop;
    }
    
    /*  */
    /**  Affiche l'écran d'accueil au démarrage  */
    private static void showAccueil() {

        SwingUtilities.invokeLater(() -> { //Mets cette tâche dans la file d'attente pour que l'interface la traite dès que possible

            MainMenuView view = new MainMenuView(); 
            new MainMenuController(view);
            view.setVisible(true);

        });
    }

    public static void main(String[] args) throws IOException{
        
        //// Lance l'écran d'accueil au lieu du jeu directement
        showAccueil();

        //// Pour lancer le jeu directement (pour les testes)
        //new GameLauncher(true);
        //new ConsoleLauncher();
    }
}

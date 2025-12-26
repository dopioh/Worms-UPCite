package fr.upc.g5e4;
import java.io.IOException;

import javax.swing.*;
import java.awt.event.ActionEvent;

import fr.upc.g5e4.model.InventoryModel.*;
import fr.upc.g5e4.model.console.ConsoleLauncher;
import fr.upc.g5e4.view.*;
import fr.upc.g5e4.controller.*;
import fr.upc.g5e4.model.*; 


/** Classe qui lance le jeu, elle set la taille de l'ecran (grace a son heritage de JFrame) */
public class GameLauncher{
    // classe pour lancer le jeu -> elle va instancier le GamePanel
    private final GameManager gameManager;
    private final GamePanel gamePanel;
    private final GameLoop gameLoop;

    private WindOverlay windOverlay;
    private InventoryOverlay inventoryOverlay; 
    private PauseOverlay pauseOverlay;
    private HelpOverlay helpOverlay;
    private HUDOverlay hudOverlay;


    private boolean screen;

    public GameLauncher(boolean screen) throws IOException {
        this(screen, false);
    }
    
    public GameLauncher(boolean screen, boolean startFullScreen) throws IOException{
        this.screen = screen;
        int sizeX = 1280;
        int sizeY = 720;

        if (screen){
            JFrame game = new JFrame();

            game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            game.setTitle("Prototype");
            game.setSize(sizeX,sizeY);         

            this.gameManager = new GameManager();
            this.gamePanel = gameManager.getGamePanel();
             
            //VENT
            this.windOverlay = new WindOverlay(gameManager.getWindModel());
            this.windOverlay.setBounds(0, 0, sizeX, sizeY);

            /* ------------------------ Partie Overlay Inventaire --------------------------------- */
            //Charge l'inventaire 
            InventoryRoot root = InventoryModel.load("/inventory.json");
            java.util.List<InventoryJson> team1Inventory = copyInventory(root.inventory);
            java.util.List<InventoryJson> team2Inventory = copyInventory(root.inventory);
            // Attacher les inventaires aux teams
            gameManager.getTeamModel1().setInventory(team1Inventory);
            gameManager.getTeamModel2().setInventory(team2Inventory);
            //Créer l'overlay
            this.inventoryOverlay = new InventoryOverlay(gameManager.getTeamModel1().getInventory());  
            //Créer un layeredPane
            JLayeredPane layered = new JLayeredPane();
            layered.setPreferredSize(new java.awt.Dimension(sizeX,sizeY));
            layered.setBackground(java.awt.Color.BLACK);
            layered.setOpaque(true);
            //placer dedans
            gamePanel.setBounds(0,0,sizeX,sizeY);
            inventoryOverlay.setBounds(0,0,sizeX,sizeY);
            //HUD
            this.hudOverlay = new HUDOverlay(gameManager);
            this.hudOverlay.setBounds(0, 0, sizeX, sizeY);
            this.hudOverlay.setInventoryOverlay(inventoryOverlay);
            //Menu parametres 
            this.pauseOverlay = new PauseOverlay();
            this.pauseOverlay.setBounds(0, 0, sizeX, sizeY);
            //How to play
            this.helpOverlay = new HelpOverlay();
            this.helpOverlay.setBounds(0, 0, sizeX, sizeY);

            this.pauseOverlay.setHelpOverlay(this.helpOverlay);

            layered.add(gamePanel, JLayeredPane.DEFAULT_LAYER);
            layered.add(this.windOverlay, JLayeredPane.PALETTE_LAYER);
            
            layered.add(this.hudOverlay, JLayeredPane.DRAG_LAYER);
            layered.add(inventoryOverlay, JLayeredPane.PALETTE_LAYER);

            layered.add(pauseOverlay, JLayeredPane.MODAL_LAYER);
            layered.add(helpOverlay, JLayeredPane.DRAG_LAYER);

            game.setContentPane(layered);
            
            inventoryOverlay.setGameManager(gameManager);    
            inventoryOverlay.setFireListener(gameManager.getImputController());
            
            gameManager.setInventoryOverlay(inventoryOverlay);
            gameManager.setHudOverlay(this.hudOverlay); 

            SwingInputController input = gameManager.getImputController();
            input.setGameFrame(game);
            input.setInventoryOverlay(inventoryOverlay);
            input.setGameManager(gameManager);
            input.setPauseOverlay(pauseOverlay);
            input.setHelpOverlay(helpOverlay);

            game.getContentPane().setBackground(java.awt.Color.BLACK);
            
            //Touches raccourcies
            setupKeyBindings(layered, game, startFullScreen);
            
            //game.add(gamePanel);
            game.pack();
            game.setLocationRelativeTo(null);
            game.setResizable(false);

            game.setVisible(true);
            
            // Démarrer en plein écran si demandé
            if (startFullScreen) {
                java.awt.GraphicsDevice device = java.awt.GraphicsEnvironment
                    .getLocalGraphicsEnvironment().getDefaultScreenDevice();
                game.dispose();
                game.setUndecorated(true);
                game.setVisible(true);
                device.setFullScreenWindow(game);
                
                // Mettre à jour les dimensions
                java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
                layered.setPreferredSize(screenSize);
                gamePanel.setBounds(0, 0, screenSize.width, screenSize.height);
                gamePanel.setPreferredSize(screenSize);
                inventoryOverlay.setBounds(0, 0, screenSize.width, screenSize.height);
                windOverlay.setBounds(0, 0, screenSize.width, screenSize.height);
                pauseOverlay.setBounds(0, 0, screenSize.width, screenSize.height);
                helpOverlay.setBounds(0, 0, screenSize.width, screenSize.height);
                gameManager.getCamera().setViewSize(screenSize.width, screenSize.height);
            }

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

    private java.util.List<InventoryModel.InventoryJson> copyInventory(java.util.List<InventoryModel.InventoryJson> src) {
                java.util.List<InventoryModel.InventoryJson> res = new java.util.ArrayList<>();
                for (InventoryModel.InventoryJson it : src) {
                    InventoryModel.InventoryJson c = new InventoryModel.InventoryJson();
                    c.objectType = it.objectType;
                    c.name = it.name;
                    c.maxUsages = it.maxUsages;
                    c.isInfinite = it.isInfinite;
                    c.remainingUses = it.remainingUses;
                    // useThisTurn reste à 0
                    res.add(c);
                }
                return res;
    }

    private void setupKeyBindings(JComponent root, JFrame game, boolean initialFullScreen) {
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();
        // Touche F11 -> basculer en plein écran
        im.put(KeyStroke.getKeyStroke("F11"), "toggleFullScreen");
        am.put("toggleFullScreen", new AbstractAction() {
            private boolean isFullScreen = initialFullScreen;
            private final int defaultSizeX = 1280;
            private final int defaultSizeY = 720;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                java.awt.GraphicsDevice device = java.awt.GraphicsEnvironment
                    .getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice();
                
                if (isFullScreen) {
                    // Sortir du plein écran
                    device.setFullScreenWindow(null);
                    game.dispose();
                    game.setUndecorated(false);
                    game.setVisible(true);
                    isFullScreen = false;
                    
                    // Remettre les dimensions par défaut
                    updateGameSize(root, defaultSizeX, defaultSizeY);
                } else {
                    // Entrer en plein écran
                    game.dispose();
                    game.setUndecorated(true);
                    game.setVisible(true);
                    device.setFullScreenWindow(game);
                    isFullScreen = true;
                    
                    // Récupérer les dimensions de l'écran et mettre à jour le jeu
                    java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
                    updateGameSize(root, screenSize.width, screenSize.height);
                }
                
                // Redonner le focus au contrôleur d'input
                gameManager.getImputController().requestFocusInWindow();
            }
            
            private void updateGameSize(JComponent layered, int newWidth, int newHeight) {
                // Mettre à jour les bounds du layered pane
                layered.setPreferredSize(new java.awt.Dimension(newWidth, newHeight));
                
                // Mettre à jour les bounds du game panel
                gamePanel.setBounds(0, 0, newWidth, newHeight);
                gamePanel.setPreferredSize(new java.awt.Dimension(newWidth, newHeight));
                
                // Mettre à jour les bounds de l'overlay inventaire
                if (inventoryOverlay != null) {
                    inventoryOverlay.setBounds(0, 0, newWidth, newHeight);
                }

                // Mets à jour les bounds du overlay vent
                if (windOverlay != null) {
                    windOverlay.setBounds(0, 0, newWidth, newHeight);
                }

                // Mets à jour les bounds du overlay pause
                if (pauseOverlay != null) {
                    pauseOverlay.setBounds(0, 0, newWidth, newHeight);
                }

                // Mets à jour les bounds du overlay aide
                if (helpOverlay != null) {
                    helpOverlay.setBounds(0, 0, newWidth, newHeight);
                }

                // Mets à jour les bounds du overlay HUD
                if (hudOverlay != null) {
                    hudOverlay.setBounds(0, 0, newWidth, newHeight);
                }
                
                // Mettre à jour la taille de vue de la caméra
                gameManager.getCamera().setViewSize(newWidth, newHeight);
                
                // Revalider et repeindre
                layered.revalidate();
                layered.repaint();
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
        //new ConsoleLauncher(); //la console est trop bien
    }
}

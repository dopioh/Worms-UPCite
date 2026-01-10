package fr.upc.g5e4;
import java.io.IOException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

//import java.util.ArrayList;

//import fr.upc.g5e4.model.InventoryModel.*;
import fr.upc.g5e4.model.io.SaveData;
import fr.upc.g5e4.model.io.SaveLoad;
//import fr.upc.g5e4.model.io.SaveLoad;
import fr.upc.g5e4.model.console.ConsoleLauncher;
import fr.upc.g5e4.view.*;
import fr.upc.g5e4.controller.*;
import fr.upc.g5e4.controller.minigames.*;
import fr.upc.g5e4.model.*;
import fr.upc.g5e4.model.minigames.*;
import fr.upc.g5e4.view.minigames.*;
import fr.upc.g5e4.util.ImageCache;


/** Classe qui lance le jeu, elle set la taille de l'ecran (grace a son heritage de JFrame) */
public class GameLauncher{
    // classe pour lancer le jeu -> elle va instancier le GamePanel
    private GameManager gameManager;
    private GamePanel gamePanel;
    private GameLoop gameLoop;

    private WindOverlay windOverlay;
    private InventoryOverlay inventoryOverlay;
    private WormRaceController wormRaceController;
    private WormGuessController wormGuessController;
    private WormJackController wormJackController;
    private PauseOverlay pauseOverlay;
    private HelpOverlay helpOverlay;
    private HUDOverlay hudOverlay;


    private boolean screen;

    // Composants pour la fenêtre unique
    private JFrame mainFrame;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JLayeredPane gameLayeredPane;
    private MainMenuView mainMenuView;
    private boolean isFullScreen = false;
    
    private static final int DEFAULT_WIDTH = 1280;
    private static final int DEFAULT_HEIGHT = 720;
    
    public static final String MENU_CARD = "menu";
    public static final String CINEMATIC_CARD = "cinematic";
    public static final String GAME_CARD = "game";

    private SaveData saveData; // si il y'a un fichier de sauvegarde

    private PlayerDataController playerDataController;

    //private boolean firstTimeLaunch = true ; -> on va juste checker si il y'a une sauvegarde deja presente de PlayerData

    /**
     * Constructeur normal : affiche le menu principal
     */
    public GameLauncher() throws IOException {
        this(true, false); // Avec écran, affiche le menu
    }
    
    /**
     * Constructeur pour les tests sans interface graphique
     * @param screen true = avec interface graphique, false = mode headless pour tests
     */
    public GameLauncher(boolean screen) throws IOException {
        this(screen, true); // skipMenu = true si screen, sinon pas de menu en headless
    }
    
    /**
     * Constructeur complet
     * @param screen true = avec interface graphique, false = mode headless pour tests
     * @param skipMenu true = lance le jeu directement, false = affiche le menu (ignoré si screen=false)
     */
    public GameLauncher(boolean screen, boolean skipMenu) throws IOException {
        attributionPlayerDataController();

        this.screen = screen;
        
        if (!screen) {
            // Mode headless pour les tests (pas d'interface graphique)
            this.gameManager = new GameManager();
            this.gamePanel = gameManager.getGamePanel();
            this.gameLoop = new GameLoop(gameManager);
            this.gameLoop.timerStart();
            return;
        }
        
        preloadCommonImages();
        
        // Créer la fenêtre principale unique
        mainFrame = new JFrame("Worms");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        
        // Créer le CardLayout pour basculer entre menu et jeu
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        
        // Créer le menu
        mainMenuView = new MainMenuView();
        new MainMenuController(mainMenuView, this);
        cardPanel.add(mainMenuView, MENU_CARD);
        
        // Créer le panneau de jeu (layered pane)
        gameLayeredPane = new JLayeredPane();
        gameLayeredPane.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        gameLayeredPane.setBackground(Color.BLACK);
        gameLayeredPane.setOpaque(true);
        cardPanel.add(gameLayeredPane, GAME_CARD);
        
        mainFrame.setContentPane(cardPanel);
    
        
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);
        mainFrame.setVisible(true);
        
        // Afficher le menu ou lancer le jeu directement
        if (skipMenu) {
            startGame();
        } else {
            showMenu();
        }
    }
    
    /**
     * Affiche le menu principal
     */
    public void showMenu() {
        // Arrêter le jeu si en cours
        if (gameLoop != null) {
            gameLoop.timerStop();
        }
        cardLayout.show(cardPanel, MENU_CARD);
        mainMenuView.requestFocusInWindow();
    }
    
    /**
     * Affiche settings
     */
    public void showSettings(SettingsView settingsView) {
        cardPanel.add(settingsView, "SETTINGS");
        cardLayout.show(cardPanel, "SETTINGS");
    }
    
    /**
     * Lance le jeu avec une cinématique d'introduction
     */
    public void startGame() throws IOException {

        if (gameManager == null) {
                initializeGame(false);
            } 
            // Lancer la cinématique d'introduction
            startIntroCinematic();
        }
    

    /**
     * Lance la cinématique d'introduction
     */
    private void startIntroCinematic() {
        // Créer la vue de cinématique
        IntroCinematicView cinematicView = new IntroCinematicView(
            gameManager.getlistTeams(),
            this::onCinematicEnd
        );

        // Ajouter la cinématique au card panel
        cardPanel.add(cinematicView, CINEMATIC_CARD);

        // Afficher la cinématique
        cardLayout.show(cardPanel, CINEMATIC_CARD);
    }

    /**
     * Callback appelée à la fin de la cinématique
     */
    private void onCinematicEnd() {
        // Basculer vers le jeu
        cardLayout.show(cardPanel, GAME_CARD);

        // Démarrer la game loop
        if (gameLoop != null) {
            gameLoop.timerStart();
        }

        // Focus sur l'input controller
        if (gameManager != null && gameManager.getImputController() != null) {
            gameManager.getImputController().requestFocusInWindow();
        }
    }

    /**
     * Pour lancer SUR LA MEME FENETRE resumeGame
     * @param saveData une saveData load depuis sauvegarde.txt
     * 
     * @see SaveLoad
     * @see SaveData
     */
    public void startFromGameSave(SaveData saveData) throws IOException{
        resetGame(); // au cas ou 
        if (saveData == null){
            initializeGame(false);
        } else {
            this.saveData = saveData;
            initializeGame(true);
        }

        cardLayout.show(cardPanel, GAME_CARD); // afficher le jeu 

        if (gameLoop != null) { // demarrer la gameLoop
            gameLoop.timerStart();
        }

        if (gameManager != null && gameManager.getImputController() != null) {
            gameManager.getImputController().requestFocusInWindow(); // focus l'inputController
        }

    }
    
    /**
     * Initialise tous les composants du jeu
     */
    private void initializeGame(boolean save) throws IOException {
        int width = cardPanel.getWidth();
        int height = cardPanel.getHeight();
        
        // Mettre à jour la taille du layered pane
        gameLayeredPane.setPreferredSize(new Dimension(width, height));
        if (save){
            this.gameManager = new GameManager(saveData);
        } else {
            this.gameManager = new GameManager();  // 3 équipes pour tester le scroll
        }

        this.gamePanel = gameManager.getGamePanel();
        SwingInputController input = gameManager.getImputController();
        input.setBounds(0, 0, width, height);
        input.setOpaque(false);

        
        // Inventaire Initialisation -> GameManager

        this.inventoryOverlay = new InventoryOverlay(gameManager.getListTeamInit().get(0).getTeamModel().getInventory());
        this.inventoryOverlay.setBounds(0, 0, width, height);
        // WormRace MVC
        WormRaceModel wormRaceModel = new WormRaceModel();
        WormRaceView wormRaceView = new WormRaceView(wormRaceModel);
        this.wormRaceController = new WormRaceController(wormRaceModel, wormRaceView);
        wormRaceView.setBounds(0, 0, width, height);
        this.wormRaceController.setGameManager(gameManager);
        this.inventoryOverlay.setWormRaceController(this.wormRaceController);
        // WormGuess MVC
        WormGuessModel wormGuessModel = new WormGuessModel();
        WormGuessView wormGuessView = new WormGuessView(wormGuessModel);
        this.wormGuessController = new WormGuessController(wormGuessModel, wormGuessView);
        wormGuessView.setBounds(0, 0, width, height);
        this.wormGuessController.setGameManager(gameManager);
        this.inventoryOverlay.setWormGuessController(this.wormGuessController);
        // WormJack MVC
        WormJackModel wormJackModel = new WormJackModel();
        WormJackView wormJackView = new WormJackView(wormJackModel);
        this.wormJackController = new WormJackController(wormJackModel, wormJackView);
        wormJackView.setBounds(0, 0, width, height);
        this.wormJackController.setGameManager(gameManager);
        this.inventoryOverlay.setWormJackController(this.wormJackController);
        // VENT
        windOverlay = new WindOverlay(gameManager.getWindModel());
        windOverlay.setBounds(0, 0, width, height);
        gameManager.setWindOverlay(windOverlay);
        //HUD
        this.hudOverlay = new HUDOverlay(gameManager);
        this.hudOverlay.setBounds(0, 0, width, height);
        this.hudOverlay.setInventoryOverlay(inventoryOverlay);
        //Menu parametres 
        this.pauseOverlay = new PauseOverlay();
        this.pauseOverlay.setBounds(0, 0, width, height);
        this.pauseOverlay.setSoundOn(!SoundPlayer.isMuted());
        this.pauseOverlay.setFullScreenOn(isFullScreen);
        hudOverlay.setPauseOverlay(pauseOverlay);
        //How to play
        this.helpOverlay = new HelpOverlay();
        this.helpOverlay.setBounds(0, 0, width, height);
        
        // Placer les composants dans le layered pane
        gamePanel.setBounds(0, 0, width, height);
        inventoryOverlay.setBounds(0, 0, width, height);

        
        gameLayeredPane.add(gamePanel, JLayeredPane.DEFAULT_LAYER);
        gameLayeredPane.add(input, JLayeredPane.DEFAULT_LAYER);
        gameLayeredPane.add(windOverlay, JLayeredPane.PALETTE_LAYER);   
        gameLayeredPane.add(hudOverlay, JLayeredPane.PALETTE_LAYER);
        gameLayeredPane.add(inventoryOverlay, JLayeredPane.MODAL_LAYER);  
        gameLayeredPane.add(pauseOverlay, JLayeredPane.POPUP_LAYER);
        gameLayeredPane.add(helpOverlay, JLayeredPane.DRAG_LAYER);
        gameLayeredPane.add(wormRaceController.getView());
        gameLayeredPane.add(wormGuessController.getView());
        gameLayeredPane.add(wormJackController.getView());
        
        // redim WormRace / WormGuess / WormJack lors du resize de la fenêtre
        gameLayeredPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int w = gameLayeredPane.getWidth();
                int h = gameLayeredPane.getHeight();
                wormRaceController.getView().setBounds(0, 0, w, h);
                wormGuessController.getView().setBounds(0, 0, w, h);
                wormJackController.getView().setBounds(0, 0, w, h);
            }
        });
        
        inventoryOverlay.setGameManager(gameManager);    
        inventoryOverlay.setFireListener(gameManager.getImputController());
        gameManager.setInventoryOverlay(inventoryOverlay);
        gameManager.setHudOverlay(this.hudOverlay);
        input.setGameFrame(mainFrame);
        input.setInventoryOverlay(inventoryOverlay);
        input.setGameManager(gameManager);
        input.setPauseOverlay(pauseOverlay);
        input.setFullScreenToggleAction(this::toggleFullScreen);
        input.setBackToMenuAction(this::showMenu);
        input.setHelpOverlay(helpOverlay);  
        
        // Mettre à jour la caméra avec les dimensions actuelles (important si en plein écran)
        if (gameManager.getCamera() != null) {
            gameManager.getCamera().setViewSize(width, height);
        }
        
        // Créer et démarrer la game loop
        this.gameLoop = new GameLoop(gameManager);
        gameManager.setGameLoop(gameLoop);
        
        // Timer pour vérifier la fin de partie
        Timer checkGameOverTimer = new Timer(1000, e -> {
            if (gameLoop != null && gameLoop.isGameOver()) {
                String winner = gameLoop.getWinner();
                if (winner != null) {
                    ((Timer)e.getSource()).stop();
                    showEndGameScreen(winner);
                }
            }
        });
        checkGameOverTimer.start();
    }
    
    /**
     * Pour declencher le fullscreen dans les settings
     */
    public void requestToggleFullScreen() {
        toggleFullScreen(); 
    }

    private void toggleFullScreen() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        
        // Obtenir la taille de l'écran sans le notch(mac), menu bar/dock
        Rectangle screenBounds = gc.getBounds();
        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
        
        int usableX = screenBounds.x + screenInsets.left;
        int usableY = screenBounds.y + screenInsets.top;
        int usableWidth = screenBounds.width - screenInsets.left - screenInsets.right;
        int usableHeight = screenBounds.height - screenInsets.top - screenInsets.bottom;
        
        if (isFullScreen) {
            // Sortir du plein écran
            mainFrame.dispose();
            mainFrame.setUndecorated(false);
            mainFrame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setVisible(true);
            isFullScreen = false;
        } else {
            // Entrer en plein écran (simulé, respecte le notch et le dock)
            mainFrame.dispose();
            mainFrame.setUndecorated(true);
            mainFrame.setBounds(usableX, usableY, usableWidth, usableHeight);
            mainFrame.setVisible(true);
            isFullScreen = true;
        }
        
        mainFrame.validate();

        Dimension d = cardPanel.getSize();
        int w = (d.width > 0) ? d.width : (isFullScreen ? usableWidth : DEFAULT_WIDTH);
        int h = (d.height > 0) ? d.height : (isFullScreen ? usableHeight : DEFAULT_HEIGHT);

        updateAllSizes(w, h);
        
        // Redonner le focus
        if (gameManager != null && gameManager.getImputController() != null) {
            gameManager.getImputController().requestFocusInWindow();
        }
 
        if (pauseOverlay != null) {
            pauseOverlay.setFullScreenOn(isFullScreen);
        }
    }
    
    private void updateAllSizes(int width, int height) {
        // Mettre à jour le menu
        mainMenuView.updateSizes(isFullScreen);
        
        // Mettre à jour le jeu si initialisé
        if (gameLayeredPane != null) {
            gameLayeredPane.setBounds(0, 0, width, height);
            gameLayeredPane.setSize(width, height);
            gameLayeredPane.setPreferredSize(new Dimension(width, height));
        }
        
        if (gamePanel != null) {
            gamePanel.setBounds(0, 0, width, height);
            gamePanel.setPreferredSize(new Dimension(width, height));
        }
        
        if (inventoryOverlay != null) {
            inventoryOverlay.setBounds(0, 0, width, height);
        }
        
        if (windOverlay != null) {
            windOverlay.setBounds(0, 0, width, height);
        }
        // Mets à jour les bounds du overlay pause
        if (pauseOverlay != null) {
            pauseOverlay.setBounds(0, 0, width, height);
        }

        // Mets à jour les bounds du overlay aide
        if (helpOverlay != null) {
            helpOverlay.setBounds(0, 0, width,height);
        }

        // Mets à jour les bounds du overlay HUD
        if (hudOverlay != null) {
            hudOverlay.setBounds(0, 0, width, height);
        }

        if (gameManager != null && gameManager.getImputController() != null) {
            gameManager.getImputController().setBounds(0, 0, width, height);
        }

        if (gameManager != null && gameManager.getCamera() != null) {
            gameManager.getCamera().setViewSize(width, height);
            // Notifier le CameraController pour éviter le glissement du au followPlayer
            if (gameManager.getCameraController() != null) {
                gameManager.getCameraController().onViewSizeChanged();
            }
        }

        if (gameLayeredPane != null) {
            gameLayeredPane.revalidate();
            gameLayeredPane.repaint();
        }
        cardPanel.revalidate();
        cardPanel.repaint();
    }
    
    public boolean isFullScreen() {
        return isFullScreen;
    }
    
    public JFrame getMainFrame() {
        return mainFrame;
    }
    
    public JPanel getCardPanel() {
        return cardPanel;
    }

    
    public GameManager getGameManager(){
        return this.gameManager;
    }

    public GameLoop getGameLoop(){
        return this.gameLoop;
    }
    
    /**
     * Affiche l'écran de fin de partie avec animation
     */
    private void showEndGameScreen(String winnerName) {
        SwingUtilities.invokeLater(() -> {
            // Trouver l'équipe gagnante dans la liste des équipes
            TeamModel winningTeam = null;
            TeamView winningTeamView = null;
            
            for (TeamInitialisation teamInit : gameManager.getListTeamInit()) {
                if (teamInit.getName().equals(winnerName)) {
                    winningTeam = teamInit.getTeamModel();
                    winningTeamView = teamInit.getTeamView();
                    break;
                }
            }
            
            if (winningTeam == null || winningTeamView == null) {
                System.err.println("Erreur: Équipe gagnante non trouvée: " + winnerName);
                return;
            }
            
            EndGameController endGameController = new EndGameController(this);
            endGameController.showEndGameScreen(
                winningTeam, 
                winningTeamView, 
                winnerName,
                gameManager.getAllTeamStats(),
                gameManager.getListTeamInit()
            );
        });
    }

    public void resetGame() {
        if (gameLoop != null) {
            gameLoop.timerStop();
        }
        this.gameManager = null;
        this.gamePanel = null;
        this.gameLoop = null;
        this.inventoryOverlay = null;
        this.windOverlay = null;
        this.hudOverlay = null;
        this.pauseOverlay = null;
        if (gameLayeredPane != null) {
            gameLayeredPane.removeAll();
            gameLayeredPane.repaint();
        }
    }

    /**
     * Methode pour charger une sauvegarde de Player
     * Ou en creer une si il n'en existe pas
     */
    public void attributionPlayerDataController(){

        boolean playerSave = PlayerDataController.isThereSavePlayer(); // tjr l'appeler avant this.playerDataController = new PlayerDataController()
        this.playerDataController = new PlayerDataController();

        if (playerSave){
            System.out.println("Il y'a une save");
            this.playerDataController.chargerJoueur();
        } else {
            System.out.println("Il y'a pas de save");
            this.playerDataController.nouveauJoueur();
            this.playerDataController.sauvegarderJoueur();
        }
    }

    /**
     * preload les images
     */
    private void preloadCommonImages() {
        System.out.println("[GameLauncher] Préchargement des images...");
        
        // sprites des worms
        ImageCache.preload(
            "/wormCostar.png", "/wormNoel.png", "/wormPinky.png",
            "/wormPixel.png", "/wormCuisto.png", "/wormNerd.png"
        );
        
        // armes POUR LA BOUTIQUE (IL FAUDRAIT QUE LES IMAGES DE LA BOUTIQUE SOIENT LES SPRITES DES ITEMS POUR OPTI)
        ImageCache.preload(
            "/ak.png", "/grenade.png", "/bazooka.png", "/baseballbat.png"
        );
        
        // gadgets
        ImageCache.preload(
            "/gadgets/potion_speed.png", "/gadgets/potion_jump.png",
            "/gadgets/potion_heal.png", "/gadgets/potion_shield.png"
        );
        
        // sprites d'armes
        ImageCache.preload(
            "/sprite_items/ak47-sprite.png", "/sprite_items/grenade-sprite.png",
            "/sprite_items/rocketlauncher-sprite.png", "/sprite_items/baseballbat-sprite.png"
        );
        
        // sprites de gadgets
        ImageCache.preload(
            "/gadgets/potionspeed-sprite.png", "/gadgets/potionjump-sprite.png",
            "/gadgets/potionheal-sprite.png", "/gadgets/potionshield-sprite.png"
        );
        
        // backgrounds et UI
        ImageCache.preload(
            "/background.png", "/Reglage_BG.png",
            "/backpack.png", "/parametre.png", "/title.png", "/BG_Jour.png"
        );
        
        // gambling
        ImageCache.preload(
            "/wormRace.png", "/wormGuess.png", "/wormJack.png"
        );
        
        // fin de jeu
        ImageCache.preload(
            "/Trophee.png", "/Sparkle.png"
        );
        
        System.out.println("[GameLauncher] Préchargement terminé !");
    }

    public PlayerDataController getPlayerDataController(){
        return this.playerDataController;
    } 
    public PauseOverlay getPauseOverlay() {
        return pauseOverlay;
    }

    public static void main(String[] args) throws IOException{

        // Lance le jeu avec le menu principal
        new GameLauncher();

        // Pour lancer le jeu directement (sans menu) :
        // new GameLauncher(true, true);

        // Mode console :
        //new ConsoleLauncher();
    }
}

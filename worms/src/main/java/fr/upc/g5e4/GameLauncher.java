package fr.upc.g5e4;
import java.io.IOException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

//import java.util.ArrayList;

//import fr.upc.g5e4.model.InventoryModel.*;
import fr.upc.g5e4.model.io.SaveData;
//import fr.upc.g5e4.model.io.SaveLoad;
import fr.upc.g5e4.model.console.ConsoleLauncher;
import fr.upc.g5e4.view.*;
import fr.upc.g5e4.controller.*;
import fr.upc.g5e4.model.*; 


/** Classe qui lance le jeu, elle set la taille de l'ecran (grace a son heritage de JFrame) */
public class GameLauncher{
    // classe pour lancer le jeu -> elle va instancier le GamePanel
    private GameManager gameManager;
    private GamePanel gamePanel;
    private GameLoop gameLoop;

    private InventoryOverlay inventoryOverlay;
    private WindOverlay windOverlay;

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
        
        // Setup des touches globales
        setupGlobalKeyBindings();
        
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
        this.saveData = saveData;

        resetGame(); // au cas ou 

        initializeGame(true);

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
        
        // VENT
        windOverlay = new WindOverlay(gameManager.getWindModel());
        windOverlay.setBounds(0, 0, width, height);
        gameManager.setWindOverlay(windOverlay);

        // Inventaire Initialisation -> GameManager

        //Créer l'overlay
        this.inventoryOverlay = new InventoryOverlay(gameManager.getListTeamInit().get(0).getTeamModel().getInventory());  

        
        // Placer les composants dans le layered pane
        gamePanel.setBounds(0, 0, width, height);
        inventoryOverlay.setBounds(0, 0, width, height);

        gameLayeredPane.add(gamePanel, JLayeredPane.DEFAULT_LAYER);
        gameLayeredPane.add(windOverlay, JLayeredPane.PALETTE_LAYER);
        gameLayeredPane.add(inventoryOverlay, JLayeredPane.PALETTE_LAYER);
        
        inventoryOverlay.setGameManager(gameManager);    
        inventoryOverlay.setFireListener(gameManager.getImputController());
        gameManager.setInventoryOverlay(inventoryOverlay);

        SwingInputController input = gameManager.getImputController();
        input.setGameFrame(mainFrame);
        input.setInventoryOverlay(inventoryOverlay);
        input.setGameManager(gameManager);
        
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

    private void setupGlobalKeyBindings() {
        JRootPane rootPane = mainFrame.getRootPane();
        InputMap im = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = rootPane.getActionMap();
        
        // F -> basculer en plein écran
        im.put(KeyStroke.getKeyStroke("F"), "toggleFullScreen");
        am.put("toggleFullScreen", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleFullScreen();
            }
        });
        
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
            updateAllSizes(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        } else {
            // Entrer en plein écran (simulé, respecte le notch et le dock)
            mainFrame.dispose();
            mainFrame.setUndecorated(true);
            mainFrame.setBounds(usableX, usableY, usableWidth, usableHeight);
            mainFrame.setVisible(true);
            isFullScreen = true;
            
            updateAllSizes(usableWidth, usableHeight);
        }
        
        // Redonner le focus
        if (gameManager != null && gameManager.getImputController() != null) {
            gameManager.getImputController().requestFocusInWindow();
        }
    }
    
    private void updateAllSizes(int width, int height) {
        // Mettre à jour le menu
        mainMenuView.updateSizes(isFullScreen);
        
        // Mettre à jour le jeu si initialisé
        if (gameLayeredPane != null) {
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
        
        if (gameManager != null && gameManager.getCamera() != null) {
            gameManager.getCamera().setViewSize(width, height);
            // Notifier le CameraController pour éviter le glissement du au followPlayer
            if (gameManager.getCameraController() != null) {
                gameManager.getCameraController().onViewSizeChanged();
            }
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
                gameManager.getAllTeamStats()
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

    public PlayerDataController getPlayerDataController(){
        return this.playerDataController;
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

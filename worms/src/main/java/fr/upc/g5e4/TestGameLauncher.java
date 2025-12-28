package fr.upc.g5e4;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import fr.upc.g5e4.controller.*;
import fr.upc.g5e4.model.*;
import fr.upc.g5e4.model.camera.CameraModel;
import fr.upc.g5e4.view.*;

/**
 * Lanceur de test pour le terrain en blocs
 * Contrôles:
 * - Clic gauche = détruire bloc (rayon 10 blocs)
 * - Clic droit = placer bloc (si bloc en dessous)
 */
public class TestGameLauncher {
    
    // Rayon d'interaction avec les blocs (en nombre de blocs)
    private static final int BLOCK_INTERACTION_RADIUS = 10;
    
    private BlockGridModel blockGrid;
    private PhysicsWorld physicsWorld;
    private World world;
    private Body groundBody;
    private CharacterModel worm;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new TestGameLauncher();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    public TestGameLauncher() {
        // Dimensions du terrain et de la fenêtre
        // IMPORTANT: La hauteur doit être 720 pixels pour correspondre au body des worms SINON on a un décalage entre le terrain et le body des worms
        // (car CharacterView utilise 24 mètres en dur)
        int terrainWidth = 1000;   // largeur du terrain en pixels
        int terrainHeight = 720;   // hauteur du terrain en pixels (720 = 24 mètres à 30 PPM)
        int windowWidth = 1280;    // largeur de la fenêtre
        int windowHeight = 720;    // hauteur de la fenêtre
        int waterLevel = 680;      // niveau de l'eau
        
        System.out.println("[TEST] Création du terrain en blocs avec Perlin...");
        
        // on crée la grille de blocs
        this.blockGrid = new BlockGridModel(terrainWidth, terrainHeight, waterLevel);
        
        // on génère le terrain avec Perlin Noise
        TerrainGenerator generator = new TerrainGenerator(System.currentTimeMillis());
        int[] groundY = generator.generateTerrainWithPlatforms(
            200,          // nombre de points pour la génération
            250,          // hauteur minimale (depuis le haut)
            650,          // hauteur maximale
            280,          // hauteur des plateformes aux bords
            15            // largeur des plateformes en %
        );
        
        blockGrid.generateWithGroundY(groundY);
        
        System.out.println("[TEST] Grille créée: " + blockGrid.getGridWidth() + "x" + blockGrid.getGridHeight() + " blocs");
        System.out.println("[TEST] Taille bloc: " + blockGrid.getBlockSize() + " pixels");
        
        // création monde physique
        this.physicsWorld = new PhysicsWorld();
        this.world = physicsWorld.getWorld();
        
        // création du sol (grace aux valeurs de blockGrid)
        this.groundBody = physicsWorld.createGroundFromBlockGrid(blockGrid);
        physicsWorld.limitPanel(blockGrid);
        
        System.out.println("[TEST] Physique initialisée");
        
        // on a besoin de créer un GroundModel factice pour le moment car il faut rendre compatible TeamModel
        // (TeamModel a besoin d'un GroundModel pour positionner les worms)
        GroundModel dummyGroundModel = new GroundModel(groundY, terrainWidth, terrainHeight, waterLevel);
        
        // init equipe de worms
        int wormCount = 1;
        int spawnX = 2;
        TeamModel team = new TeamModel(dummyGroundModel, world, "Test Team", spawnX, 100, wormCount, false, true, true,2);
        TeamView teamView = new TeamView(wormCount, Color.ORANGE, "/worm222.png", true);
        this.worm = team.getWorm(0);
        
        // init caméra avec zoom x2
        CameraModel camera = new CameraModel(windowWidth, windowHeight, terrainWidth, terrainHeight, 2.0);
        CameraController cameraController = new CameraController(camera, team.getWorm(0));
        cameraController.centerOnCharacter();
        
        // init contrôleur worm
        CharacterController wormController = new CharacterController(team.getWorm(0));
        SwingInputController inputController = new SwingInputController(wormController, cameraController);
        
        // init vue de la grille de blocs
        BlockGridView blockGridView = new BlockGridView();
        
        // le gamepanel est ici (version plus legère que GamePanel)
        JPanel gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                // appliquer le zoom de la caméra
                g2d.scale(camera.getZoomFactor(), camera.getZoomFactor());
                
                // appliquer le déplacement de la caméra
                g2d.translate(-camera.getX(), -camera.getY());
                
                // ciel
                g2d.setColor(new Color(135, 206, 235));
                g2d.fillRect(0, 0, terrainWidth, terrainHeight);
                
                // dessiner l'eau en arrière-plan
                blockGridView.drawWater(g2d, blockGrid);
                
                // dessiner les blocs
                blockGridView.drawAll(g2d, blockGrid);
                
                // dessiner l'équipe de worms
                teamView.draw(g2d, team);
                
                g2d.dispose();
            }
        };
        
        gamePanel.setPreferredSize(new Dimension(windowWidth, windowHeight));
        gamePanel.setLayout(null);
        gamePanel.add(inputController);
        gamePanel.setFocusable(true);
        
        // Listener pour placer/détruire des blocs
        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleBlockInteraction(e, camera, inputController);
            }
        });
        
        // Créer la fenêtre
        JFrame frame = new JFrame("Worms - Terrain Blocs");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        
        // 13. S'assurer que le focus est sur le contrôleur
        SwingUtilities.invokeLater(() -> {
            inputController.requestFocusInWindow();
            inputController.setFocusable(true);
        });
        
        // 14. Démarrer la boucle de jeu
        Timer gameTimer = new Timer(16, e -> {
            world.step(1f/60f, 8, 3);
            wormController.update();
            cameraController.update();
            gamePanel.repaint();
        });
        gameTimer.start();
        
        System.out.println("[TEST] Jeu lancé !");
        System.out.println("[TEST] Contrôles worm: Q/D = gauche/droite, Z = sauter");
        System.out.println("[TEST] Contrôles caméra: Flèches = déplacer, T/G = zoom, ESPACE = centrer");
        System.out.println("[TEST] Contrôles blocs: Clic gauche = détruire, Clic droit = placer");
    }
    
    /**
     * Gère l'interaction avec les blocs (clic souris)
     */
    private void handleBlockInteraction(MouseEvent e, CameraModel camera, SwingInputController inputController) {
        // Convertir les coordonnées écran en coordonnées monde
        double worldX = e.getX() / camera.getZoomFactor() + camera.getX();
        double worldY = e.getY() / camera.getZoomFactor() + camera.getY();
        
        // Convertir en coordonnées de grille
        int gridX = blockGrid.pixelToGridX((int) worldX);
        int gridY = blockGrid.pixelToGridY((int) worldY);
        
        // Vérifier si le bloc est dans le rayon d'interaction du worm
        if (!isInRange(gridX, gridY)) {
            System.out.println("[TEST] Bloc trop loin du worm !");
            inputController.requestFocusInWindow();
            return;
        }
        
        boolean modified = false;
        
        if (e.getButton() == MouseEvent.BUTTON1) {
            // Clic gauche = détruire le bloc
            if (blockGrid.destroyBlock(gridX, gridY)) {
                System.out.println("[TEST] Bloc détruit en (" + gridX + ", " + gridY + ")");
                modified = true;
            }
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            // Clic droit = placer un bloc (seulement s'il y a un support en dessous)
            if (canPlaceBlock(gridX, gridY)) {
                blockGrid.setBlock(gridX, gridY, BlockType.DIRT);
                System.out.println("[TEST] Bloc placé en (" + gridX + ", " + gridY + ")");
                modified = true;
            } else {
                System.out.println("[TEST] Impossible de placer: pas de support ou bloc existant");
            }
        }
        
        // Reconstruire la physique si modifié
        if (modified) {
            rebuildGroundPhysics();
        }
        
        // Redonner le focus au contrôleur
        inputController.requestFocusInWindow();
    }
    
    /**
     * Vérifie si un bloc est dans le rayon d'interaction du worm
     */
    private boolean isInRange(int gridX, int gridY) {
        // Position du worm en pixels
        float wormPixelX = worm.getBody().getPosition().x * CharacterModel.getPPM();
        float wormPixelY = (24 - worm.getBody().getPosition().y) * CharacterModel.getPPM();
        
        // Position du worm en grille
        int wormGridX = blockGrid.pixelToGridX((int) wormPixelX);
        int wormGridY = blockGrid.pixelToGridY((int) wormPixelY);
        
        // Calculer la distance en blocs
        int distX = Math.abs(gridX - wormGridX);
        int distY = Math.abs(gridY - wormGridY);
        
        // Vérifier si dans le rayon
        return distX <= BLOCK_INTERACTION_RADIUS && distY <= BLOCK_INTERACTION_RADIUS;
    }
    
    /**
     * Vérifie si on peut placer un bloc à cette position
     * (doit être vide ET avoir un bloc solide en dessous)
     */
    private boolean canPlaceBlock(int gridX, int gridY) {
        // Vérifier que la position est vide
        if (blockGrid.getBlock(gridX, gridY) != BlockType.AIR) {
            return false;
        }
        
        // Vérifier qu'il y a un bloc solide en dessous OU sur un côté (pour construire sur les murs)
        boolean hasSupport = blockGrid.getBlock(gridX, gridY + 1).isSolid() ||  // En dessous
                             blockGrid.getBlock(gridX - 1, gridY).isSolid() ||  // À gauche
                             blockGrid.getBlock(gridX + 1, gridY).isSolid();    // À droite
        
        return hasSupport;
    }
    
    /**
     * Reconstruit la physique du sol après modification des blocs
     */
    private void rebuildGroundPhysics() {
        // Détruire l'ancien body du sol
        if (groundBody != null) {
            world.destroyBody(groundBody);
        }
        
        // Recréer le body du sol à partir de la grille modifiée
        groundBody = physicsWorld.createGroundFromBlockGrid(blockGrid);
        
        System.out.println("[TEST] Physique reconstruite");
    }
}


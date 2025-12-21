package fr.upc.g5e4;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.*;
import org.jbox2d.dynamics.World;

import fr.upc.g5e4.controller.*;
import fr.upc.g5e4.model.*;
import fr.upc.g5e4.model.camera.CameraModel;
import fr.upc.g5e4.view.*;

/**
 * Lanceur de test pour le terrain en blocs style Terraria
 * Affiche un terrain généré avec des blocs 16x16
 */
public class TestGameLauncher {
    
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
        BlockGridModel blockGrid = new BlockGridModel(terrainWidth, terrainHeight, waterLevel);
        
        // on génère le terrain avec Perlin Noise
        TerrainGenerator generator = new TerrainGenerator(System.currentTimeMillis());
        int[] groundY = generator.generateTerrainWithPlatforms(
            200,          // nombre de points pour la génération
            250,          // hauteur minimale (depuis le haut)
            650,          // hauteur maximale
            280,          // hauteur des plateformes aux bords
            15            // largeur des plateformes en %
        );
        
        // on remplit la grille à partir des hauteurs générées par Perlin (avec la méthode generateWithGroundY de BlockGridModel)
        blockGrid.generateWithGroundY(groundY);
        
        System.out.println("[TEST] Grille créée: " + blockGrid.getGridWidth() + "x" + blockGrid.getGridHeight() + " blocs");
        System.out.println("[TEST] Taille bloc: " + blockGrid.getBlockSize() + " pixels");
        
        // création monde physique
        PhysicsWorld physic = new PhysicsWorld();
        World world = physic.getWorld();
        
        // création du sol (grace aux valeurs de blockGrid)
        physic.createGroundFromBlockGrid(blockGrid);
        physic.limitPanel(blockGrid);
        
        System.out.println("[TEST] Physique initialisée");
        
        // on a besoin de créer un GroundModel factice pour le moment car il faut rendre compatible TeamModel
        // (TeamModel a besoin d'un GroundModel pour positionner les worms)
        GroundModel dummyGroundModel = new GroundModel(groundY, terrainWidth, terrainHeight, waterLevel);
        
        // init equipe de worms
        int wormCount = 1;
        int spawnX = 2;
        TeamModel team = new TeamModel(dummyGroundModel, world, "Test Team", spawnX, 100, wormCount, false, true, true);
        TeamView teamView = new TeamView(wormCount, Color.ORANGE, "/worm.png", true);
        
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
        
        // 12. Créer la fenêtre
        JFrame frame = new JFrame("Worms - Terrain Blocs Terraria (Test)");
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
        System.out.println("[TEST] Contrôles: Q/D = gauche/droite, Z = sauter");
        System.out.println("[TEST] Caméra: Flèches gauche/droite, T/G = zoom, ESPACE = centrer");
    }
}


package fr.upc.g5e4;

import java.awt.Color;
import javax.swing.*;
import org.jbox2d.dynamics.World;

import fr.upc.g5e4.controller.*;
import fr.upc.g5e4.model.*;
import fr.upc.g5e4.model.camera.CameraModel;
import fr.upc.g5e4.view.*;

/**
 * Lanceur de jeu avec terrain généré par Perlin
 */
public class PerlinGameLauncher {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new PerlinGameLauncher();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    public PerlinGameLauncher() {
        int sizeX = 3380;
        int sizeY = 720;
        
        // Générer le terrain avec Perlin
        TerrainGenerator generator = new TerrainGenerator(System.currentTimeMillis());
        int[] groundY = generator.generateTerrainWithFlatEdges(14, 200, 600);
        
        // Créer le modèle de terrain
        GroundModel groundModel = new GroundModel(groundY, sizeX, sizeY, 500);
        
        // Initialiser la physique
        PhysicsWorld physic = new PhysicsWorld();
        World world = physic.getWorld();
        physic.createGroundFromModel(groundModel);
        physic.limitPanel(groundModel);
        
        // Créer une équipe de worms
        int wormNumber = 1;
        TeamModel team = new TeamModel(groundModel, world, "Team Perlin", 300, 300, wormNumber);
        TeamView teamView = new TeamView(wormNumber, Color.ORANGE);
        
        // Initialiser la caméra
        CameraModel camera = new CameraModel(sizeX, sizeY, groundModel.getWidth(), groundModel.getHeight(), 1);
        CameraController cameraController = new CameraController(camera, team.getWorm(0));
        
        // Créer le contrôleur pour le premier worm
        CharacterController wormController = new CharacterController(team.getWorm(0));
        SwingInputController inputController = new SwingInputController(wormController, cameraController);
        
        // Créer le panel de jeu
        GamePanel gamePanel = new GamePanel(groundModel, camera, cameraController, 
                                           team, teamView, null, null);
        
        // Ajouter le contrôleur
        gamePanel.add(inputController);
        gamePanel.setFocusable(true);
        
        // Créer la fenêtre
        JFrame frame = new JFrame("Worms - Terrain Perlin");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        // S'assurer que le focus est sur le contrôleur
        SwingUtilities.invokeLater(() -> {
            inputController.requestFocusInWindow();
            inputController.setFocusable(true);
        });
        
        // Démarrer la boucle de jeu
        Timer gameTimer = new Timer(16, e -> {
            world.step(1f/60f, 8, 3);
            wormController.update();
        });
        gameTimer.start();
        
        System.out.println("Jeu avec terrain Perlin lancé !");
        System.out.println("Contrôles : Q/D = gauche/droite, Z = sauter, Flèches = caméra");
    }
}

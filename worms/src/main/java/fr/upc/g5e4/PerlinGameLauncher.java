package fr.upc.g5e4;

import java.awt.Color;
import javax.swing.*;
import org.jbox2d.dynamics.World;

import fr.upc.g5e4.controller.*;
import fr.upc.g5e4.model.*;
import fr.upc.g5e4.model.camera.CameraModel;
import fr.upc.g5e4.view.*;

/**
 * Lanceur de test des terrains générés par Perlin
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
        int sizeX = 6000;  // taille du terrain
        int sizeY = 720;
        
        // seed = temps actuelle de la machine donc jamais le meme terrain
        TerrainGenerator generator = new TerrainGenerator(System.currentTimeMillis());
        System.out.println("seed actuelle : " + System.currentTimeMillis());
        int[] groundY = generator.generateTerrain(1000, 70, 800); //minHeight a 400 decalait le ground de sa hitbox wtff
        
        GroundModel groundModel = new GroundModel(groundY, sizeX, sizeY, 700);
        
        PhysicsWorld physic = new PhysicsWorld();
        World world = physic.getWorld();
        physic.createGroundFromModel(groundModel);
        physic.limitPanel(groundModel);
        
        int wormNumber = 1;
        TeamModel team1 = new TeamModel(groundModel, world, "Team Perlin", 300, 300, wormNumber, false, true, true);
        TeamView teamView1 = new TeamView(wormNumber, Color.ORANGE);
        
        // créer une deuxième équipe vide pour éviter les NullPointerException
        TeamModel team2 = new TeamModel(groundModel, world, "Team 2", 5000, 300, 1, false, false, true);
        TeamView teamView2 = new TeamView(1, Color.BLUE);
        
        // init camera (utiliser la taille de la fenêtre, pas du terrain)
        CameraModel camera = new CameraModel(1280, 720, groundModel.getWidth(), groundModel.getHeight(), 1);
        CameraController cameraController = new CameraController(camera, team1.getWorm(0));
        cameraController.centerOnCharacter();
        
        CharacterController wormController = new CharacterController(team1.getWorm(0));
        SwingInputController inputController = new SwingInputController(wormController, cameraController);
        
        GamePanel gamePanel = new GamePanel(groundModel, camera, cameraController, 
                                           team1, teamView1, team2, teamView2);
        gamePanel.setPreferredSize(new java.awt.Dimension(1280, 720));
        // ajout du controller
        gamePanel.add(inputController);
        gamePanel.setFocusable(true);
        
        // la fenêtre
        JFrame frame = new JFrame("Worms - Terrain Perlin");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        // s'assurer que le focus est sur le contrôleur
        SwingUtilities.invokeLater(() -> {
            inputController.requestFocusInWindow();
            inputController.setFocusable(true);
        });
        
        // démarrer la boucle de jeu
        Timer gameTimer = new Timer(16, e -> {
            world.step(1f/60f, 8, 3);
            wormController.update();
            cameraController.update();
            gamePanel.repaint();
        });
        gameTimer.start();
        
        System.out.println("Jeu avec terrain Perlin lancé !");
        System.out.println("Contrôles : Q/D = gauche/droite, Z = sauter, Flèches = caméra");
    }
}

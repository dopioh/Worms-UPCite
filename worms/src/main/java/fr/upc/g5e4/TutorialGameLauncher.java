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
public class TutorialGameLauncher {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new TutorialGameLauncher();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    public TutorialGameLauncher() {
        int sizeX = 1280;  // Taille du terrain
        int sizeY = 720;
        
        // Terrain plat pour le tutoriel (au lieu de Perlin)
        // On crée autant de points que la largeur pour éviter les trous
        int[] groundY = new int[sizeX];
        for (int i = 0; i < sizeX; i++) {
            groundY[i] = 400; // Hauteur fixe = terrain complètement plat
        }
        System.out.println("[TUTORIAL] Terrain plat créé: " + sizeX + " points");
        
        // Créer le modèle de terrain
        GroundModel groundModel = new GroundModel(groundY, sizeX, sizeY, 700);
        
        // Initialiser la physique
        PhysicsWorld physic = new PhysicsWorld();
        World world = physic.getWorld();
        physic.createGroundFromModel(groundModel);
        physic.limitPanel(groundModel);
        
        // Créer une équipe de worms
        TeamModel team = new TeamModel(groundModel, world, "Tuto", 300, 300, 1);
        TeamView teamView = new TeamView(1, Color.GREEN);
        teamView.setShowAllNameTags(true);

        TeamModel teamTest = new TeamModel(groundModel, world, "Tuto", 500, 0, 5);
        TeamView teamViewTest = new TeamView(5, Color.WHITE);
        
        // Désactiver les nametags pour l'équipe de test aussi
        teamViewTest.setShowAllNameTags(false);

        
        // Initialiser la caméra (utiliser la taille de la fenêtre, pas du terrain)
        CameraModel camera = new CameraModel(1280, 720, groundModel.getWidth(), groundModel.getHeight(), 1.5);
        CameraController cameraController = new CameraController(camera, team.getWorm(0));
        
        // Centrer immédiatement la caméra sur le worm
        cameraController.centerOnCharacter();
        
        // Créer le contrôleur pour le premier worm
        CharacterController wormController = new CharacterController(team.getWorm(0));
        SwingInputController inputController = new SwingInputController(wormController, cameraController);
        
        // Créer le panel de jeu avec style blueprint
        GamePanel gamePanel = new GamePanel(groundModel, camera, cameraController, 
                                           team, teamView, teamTest, teamViewTest);
        
        // Activer le mode blueprint
        gamePanel.setBlueprintMode(true);
        
        // Ajouter le contrôleur
        gamePanel.add(inputController);
        gamePanel.setFocusable(true);
        
        // Créer la fenêtre
        JFrame frame = new JFrame("Worms - Terrain Tutoriel");
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

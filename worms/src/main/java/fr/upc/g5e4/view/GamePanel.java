package fr.upc.g5e4.view;

import fr.upc.g5e4.controller.CameraController;
import fr.upc.g5e4.model.*;
import fr.upc.g5e4.model.camera.CameraModel;
import fr.upc.g5e4.view.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

/**
 * Classe qui sert pour tous les graphismes du monde
 * Gère le rendu avec caméra et zoom
 */
public class GamePanel extends JPanel {
    private final GroundModel model;
    private final CharacterModel personnageModel;
    private final CharacterView personnageView;
    private final CameraModel camera;
    private final CameraController cameraController;
    private final TeamModel teamModel1;
    private final TeamView teamView1;
    private final TeamModel teamModel2;
    private final TeamView teamView2;

    private static final float PPM = 30f;

    /**
     * {@code setPreferredSize()} est essentiel sinon rien ne s'affiche
     */
    public GamePanel(GroundModel model, CharacterModel personnageModel, CharacterView personnageView,
            CameraModel camera, CameraController cameraController, 
            TeamModel teamModel1, TeamView teamView1, TeamModel teamModel2, TeamView teamView2) {
        this.model = model;
        this.personnageModel = personnageModel;
        this.personnageView = personnageView;
        this.camera = camera;
        this.cameraController = cameraController;
        this.teamModel1 = teamModel1;
        this.teamView1 = teamView1;
        this.teamModel2 = teamModel2;
        this.teamView2 = teamView2;

        setPreferredSize(new Dimension(model.getWidth(), model.getHeight()));
        setLayout(null);

        // Timer pour update régulier de la caméra et repaint
        new javax.swing.Timer(16, e -> {
            cameraController.update();
            repaint();
        }).start();
    }

    public static float getPPM() {
        return PPM;
    }

    /**
     * L'ordre d'ajout pour les éléments graphiques impacte la superposition des
     * dessins
     * Exemple :
     * Ciel doit etre placé avant Ground psq il doit pas cacher Ground
     * Il apparait donc derriere et Ground est au premier plan par rapport a Ciel
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        // Permet le zoom
        g2.scale(camera.getZoomFactor(), camera.getZoomFactor());

        // Permet le deplacement de la caméra
        g2.translate(-camera.getX(), -camera.getY());

        // Ciel
        g2.setColor(new Color(135, 206, 235));
        g2.fillRect(0, 0, model.getWidth(), model.getHeight());

        // Ground
        g2.setColor(new Color(34, 139, 34));
        Path2D path = new Path2D.Double();
        path.moveTo(0, model.getHeight());
        double step = (double) model.getWidth() / (model.getGroundY().length - 1);
        for (int i = 0; i < model.getGroundY().length; i++) {
            path.lineTo(i * step, model.getGroundY()[i]);
        }
        path.lineTo(model.getWidth(), model.getHeight());
        path.closePath();
        g2.fill(path);

        // Personnage
        personnageView.draw(g2, personnageModel);
        
        // Team de worms
        teamView1.draw(g2, teamModel1);
        teamView2.draw(g2, teamModel2);

        // Eau
        g2.setColor(new Color(75, 147, 240));
        g2.fillRect(0, model.getWaterLevel(), model.getWidth(), model.getHeight() - model.getWaterLevel());

        // permet libérer les ressources graphiques
        g2.dispose();
    }
}

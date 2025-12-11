// File: fr/upc/g5e4/view/GamePanel.java
package fr.upc.g5e4.view;

import fr.upc.g5e4.controller.CameraController;
import fr.upc.g5e4.model.*;
import fr.upc.g5e4.model.camera.CameraModel;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Classe qui sert pour tous les graphismes du monde
 * Gère le rendu avec caméra et zoom
 */
public class GamePanel extends JPanel {
    private final GroundModel model;
    private final CameraModel camera;
    private final TeamModel teamModel1;
    private final TeamView teamView1;
    private final TeamModel teamModel2;
    private final TeamView teamView2;

    private static final float PPM = 30f;
    
    private BufferedImage groundTexture;
    private boolean blueprintMode = true;

    // ajout: label pour afficher le temps restant (vue)
    private final JLabel roundTimerLabel = new JLabel("Temps restant : 30s");

    /**
     * {@code setPreferredSize()} est essentiel sinon rien ne s'affiche
     */
    public GamePanel(GroundModel model,
            CameraModel camera, CameraController cameraController, 
            TeamModel teamModel1, TeamView teamView1, TeamModel teamModel2, TeamView teamView2) {
        this.model = model;
        this.camera = camera;
        this.teamModel1 = teamModel1;
        this.teamView1 = teamView1;
        this.teamModel2 = teamModel2;
        this.teamView2 = teamView2;

        setPreferredSize(new Dimension(model.getWidth(), model.getHeight()));
        setLayout(null);
        
        // Charger la texture du ground
        try {
            groundTexture = ImageIO.read(getClass().getResourceAsStream("/ground_texture.png"));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("impossible de charger la texture du ground : " + e.getMessage());
            groundTexture = null;
        }

        // ajout: configuration du label chrono
        roundTimerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        roundTimerLabel.setForeground(Color.WHITE);
        roundTimerLabel.setBounds(20, 10, 220, 30);
        roundTimerLabel.setOpaque(false);
        this.add(roundTimerLabel);

        // Timer pour update régulier de la caméra et repaint
        new javax.swing.Timer(16, e -> {
            cameraController.update();
            repaint();
        }).start();
    }

    public static float getPPM() {
        return PPM;
    }

    // ajout: API publique pour mettre à jour l'affichage du chrono
    public void setRoundTimer(int seconds) {
        SwingUtilities.invokeLater(() -> roundTimerLabel.setText("Temps restant : " + seconds + "s"));
    }
    
    // Activer le mode blueprint (style bleu foncé)
    public void setBlueprintMode(boolean enabled) {
        this.blueprintMode = enabled;
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

        // Ciel (ou fond blueprint)
        if (blueprintMode) {
            // Fond bleu foncé
            g2.setColor(new Color(20, 40, 80));
            g2.fillRect(0, 0, model.getWidth(), model.getHeight());
            
            // Quadrillage blueprint
            g2.setColor(new Color(60, 100, 150, 180)); // Bleu plus clair semi-transparent
            g2.setStroke(new BasicStroke(1));
            
            // Lignes verticales (tous les 50 pixels)
            for (int x = 0; x < model.getWidth(); x += 50) {
                g2.drawLine(x, 0, x, model.getHeight());
            }
            
            // Lignes horizontales (tous les 50 pixels)
            for (int y = 0; y < model.getHeight(); y += 50) {
                g2.drawLine(0, y, model.getWidth(), y);
            }
            
            // Lignes plus épaisses tous les 200 pixels
            g2.setColor(new Color(100, 140, 200, 220));
            g2.setStroke(new BasicStroke(2));
            for (int x = 0; x < model.getWidth(); x += 200) {
                g2.drawLine(x, 0, x, model.getHeight());
            }
            for (int y = 0; y < model.getHeight(); y += 200) {
                g2.drawLine(0, y, model.getWidth(), y);
            }
            
            // Rétablir le stroke normal
            g2.setStroke(new BasicStroke(1));
        } else {
            g2.setColor(new Color(135, 206, 235)); // Ciel normal
            g2.fillRect(0, 0, model.getWidth(), model.getHeight());
        }

        // Eau (en arrière-plan, pas en mode blueprint)
        if (!blueprintMode) {
            g2.setColor(new Color(75, 147, 240));
            g2.fillRect(0, model.getWaterLevel(), model.getWidth(), model.getHeight() - model.getWaterLevel());
        }

        // Ground (au premier plan)
        Path2D path = new Path2D.Double();
        path.moveTo(0, model.getHeight());
        double step = (double) model.getWidth() / (model.getGroundY().length - 1);
        for (int i = 0; i < model.getGroundY().length; i++) {
            path.lineTo(i * step, model.getGroundY()[i]);
        }
        path.lineTo(model.getWidth(), model.getHeight());
        path.closePath();
        
        // Appliquer la texture ou la couleur
        if (groundTexture != null) {
            g2.setPaint(new TexturePaint(groundTexture, 
                new Rectangle(0, 0, groundTexture.getWidth(), groundTexture.getHeight())));
        } else {
            g2.setColor(new Color(34, 139, 34));
        }
        g2.fill(path);

        // Personnage
        //personnageView.draw(g2, personnageModel);
        
        // Team de worms
        if (teamView1 != null && teamModel1 != null) {
            teamView1.draw(g2, teamModel1);
        }
        if (teamView2 != null && teamModel2 != null) {
            teamView2.draw(g2, teamModel2);
        }

        // permet libérer les ressources graphiques
        g2.dispose();
    }
}

// File: fr/upc/g5e4/view/GamePanel.java
package fr.upc.g5e4.view;

import fr.upc.g5e4.controller.CameraController;
import fr.upc.g5e4.controller.ItemController;
import fr.upc.g5e4.model.*;
import fr.upc.g5e4.model.camera.CameraModel;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Classe qui sert pour tous les graphismes du monde
 * Gère le rendu avec caméra et zoom
 */
public class GamePanel extends JPanel {
    private final GroundModel model;  // Gardé pour compatibilité
    private BlockGridModel blockGridModel;  // système de terrain en blocs
    private final CameraModel camera;
    private final CameraController cameraController; // Ajouté pour le timer
    private ArrayList<TeamInitialisation> listTeamInitialisation;
    private boolean blueprintMode = false;
    private final TrajectoryRenderer trajectoryRenderer;
    private ItemController itemController;
    private JButton saveButton;
    private static final float PPM = 30f;
    private BufferedImage groundTexture;
    // Vue pour dessiner les blocs
    private BlockGridView blockGridView;
    // ajout: label pour afficher le temps restant (vue)
    private final JLabel roundTimerLabel = new JLabel("Temps restant : 30s");
    
    // Pré-création des objets graphiques pour éviter les allocations à chaque frame + access facile aux couleurs
    private static final Color SKY_COLOR = new Color(135, 206, 235);
    private static final Color BLUEPRINT_BG = new Color(20, 40, 80);
    private static final Color BLUEPRINT_GRID = new Color(60, 100, 150, 180);
    private static final Color BLUEPRINT_THICK = new Color(100, 140, 200, 220);
    private static final Color WATER_COLOR = new Color(75, 147, 240);
    private static final Color GROUND_COLOR = new Color(34, 139, 34);
    private static final BasicStroke GRID_STROKE_THIN = new BasicStroke(1);
    private static final BasicStroke GRID_STROKE_THICK = new BasicStroke(2);

    /**
     * Constructeur pour N équipes
     */
    public GamePanel(GroundModel model, CameraModel camera, CameraController cameraController, ArrayList<TeamInitialisation> listTeamInitialisation) {
        this.model = model;
        this.camera = camera;
        this.cameraController = cameraController;
        this.listTeamInitialisation = listTeamInitialisation;
        this.trajectoryRenderer = new TrajectoryRenderer();
        this.blockGridView = new BlockGridView();
        initializePanel();
    }

    private void initializePanel() {
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

        /*
        // Bouton de sauvegarde
        this.saveButton = new JButton("Sauvegarde");
        this.saveButton.setBounds(40, 70, 70, 30);
        saveButton.setFocusable(false);
        this.add(saveButton);
        */

        // Timer pour update régulier de la caméra (repaint géré par GameLoop)
        new javax.swing.Timer(16, e -> {
            cameraController.update();
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
     * Retourne le renderer de trajectoire pour que d'autres composants puissent l'utiliser
     */
    public TrajectoryRenderer getTrajectoryRenderer() {
        return trajectoryRenderer;
    }
    
    /**
     * Définit l'ItemController pour dessiner les projectiles
     * @param itemController Le contrôleur d'items
     */
    public void setItemController(ItemController itemController) {
        this.itemController = itemController;
    }
    
    /**
     * Définit le BlockGridModel pour le rendu du terrain en blocs
     * @param blockGridModel Le modèle de grille de blocs
     */
    public void setBlockGridModel(BlockGridModel blockGridModel) {
        this.blockGridModel = blockGridModel;
    }

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
            g2.setColor(BLUEPRINT_BG);
            g2.fillRect(0, 0, model.getWidth(), model.getHeight());
            // Quadrillage blueprint
            g2.setColor(BLUEPRINT_GRID);
            g2.setStroke(GRID_STROKE_THIN);
            
            // Lignes verticales (tous les 50 pixels)
            for (int x = 0; x < model.getWidth(); x += 50) {
                g2.drawLine(x, 0, x, model.getHeight());
            }
            // Lignes horizontales (tous les 50 pixels)
            for (int y = 0; y < model.getHeight(); y += 50) {
                g2.drawLine(0, y, model.getWidth(), y);
            }
            // Lignes plus épaisses tous les 200 pixels
            g2.setColor(BLUEPRINT_THICK);
            g2.setStroke(GRID_STROKE_THICK);
            for (int x = 0; x < model.getWidth(); x += 200) {
                g2.drawLine(x, 0, x, model.getHeight());
            }
            for (int y = 0; y < model.getHeight(); y += 200) {
                g2.drawLine(0, y, model.getWidth(), y);
            }
            // Rétablir le stroke normal
            g2.setStroke(GRID_STROKE_THIN);
        } else {
            g2.setColor(SKY_COLOR);
            g2.fillRect(0, 0, model.getWidth(), model.getHeight());
        }

        // Eau en arrière-plan quand pas en mode blueprint
        if (!blueprintMode) {
            if (blockGridModel != null) {
                // Utiliser BlockGridView pour dessiner l'eau
                blockGridView.drawWater(g2, blockGridModel);
            } else {
                // Ancien système : eau simple
                g2.setColor(WATER_COLOR);
                g2.fillRect(0, model.getWaterLevel(), model.getWidth(), model.getHeight() - model.getWaterLevel());
            }
        }

        // Ground (au premier plan)
        if (blockGridModel != null) {
            // Nouveau système : terrain en blocs
            blockGridView.drawAll(g2, blockGridModel);
        } else {
            // Ancien système : terrain avec Path2D (ligne continue)
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
                g2.setColor(GROUND_COLOR);
            }
            g2.fill(path);
        }

        // Équipes de worms
        for (TeamInitialisation t : listTeamInitialisation){
            t.draw(g2);
        }

        // Trajectoire de tir (Angry Birds style)
        trajectoryRenderer.draw(g2, null);
        // Projectiles en vol
        if (itemController != null) {
            ProjectileView.drawProjectiles(g2, itemController.getActiveProjectiles());
        }
        // permet libérer les ressources graphiques
        g2.dispose();
    }
}

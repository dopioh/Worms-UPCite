// File: fr/upc/g5e4/controller/SwingInputController.java
package fr.upc.g5e4.controller;
//import java.awt.*;
import java.awt.event.*;
import javax.swing.JPanel;

import org.jbox2d.common.Vec2;

import fr.upc.g5e4.model.GameManager;
import fr.upc.g5e4.model.CharacterModel;
import fr.upc.g5e4.view.InventoryOverlay;
import fr.upc.g5e4.view.TrajectoryRenderer;
import javax.swing.JFrame;

/** 
 * Classe pour les Entrées Clavier Souris qui reprend celle de Nassim
 * Pour le moment je reprends l'ancien model et elle herite de JPanel
 * Mais le mieux serait de la recréer de 0 et pas qu'elle herite de JPanel 
 * 
 * @author Nassim et Raphaël pour plus d'info sur son fonctionnement
 */
public class SwingInputController extends JPanel{
    private CharacterController personnageController;
    private CameraController cameraController;
    private GameManager gameManager; 
    private TrajectoryRenderer trajectoryRenderer;
    private boolean isAiming = false;  // affichage de la trajectoire
    private double shootingAngle = 0;  // angle de visée en degrés

    private InventoryOverlay inventoryOverlay; 
    private JFrame gameFrame;

    /**
     * Constructeur pour gérer uniquement le personnage
     * @param personnageController le controller du personnage
     */
    public SwingInputController(CharacterController personnageController){
        this.personnageController = personnageController;
        addControls();
    }

    /**
     * Constructeur pour gérer personnage + caméra
     * @param personnageController le controller du personnage
     * @param cameraController le controller de la caméra
     */
    public SwingInputController(CharacterController personnageController, CameraController cameraController){ 
        this.personnageController = personnageController;
        this.cameraController = cameraController;

        setFocusable(true); 
        requestFocusInWindow();

        addControls();
    }

    //Setters 
    /**
    * Définit le rendu de la trajectoire
    */
    public void setTrajectoryRenderer(TrajectoryRenderer renderer) {
        this.trajectoryRenderer = renderer;
    }
    
    /**
     * Définit le GameManager pour permettre le changement de worm
     * @param gameManager le gestionnaire de jeu
     */
    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }
    
    /**
     * Change le contrôleur de personnage actif
     * @param controller le nouveau contrôleur de personnage
     */
    public void setCharacterController(CharacterController controller) {
        this.personnageController = controller;
    }
    
    /**
     * Retourne le contrôleur de personnage actif
     * @return le contrôleur de personnage
     */
    public CharacterController getCharacterController() {
        return this.personnageController;
    }

    /**
     * Définit l'overlay d'inventaire pour ce contrôleur.
     * 
     * @param overlay l'InventoryOverlay à associer à ce contrôleur
     */
    public void setInventoryOverlay(InventoryOverlay overlay){
        this.inventoryOverlay = overlay;
    }

    /**
     * Sets the game frame for this controller, allowing it to manage window operations.
     * @param frame the JFrame instance representing the game window
     */
    public void setGameFrame(JFrame frame){
        this.gameFrame = frame;
    }

    //@Override
    public void addControls() {    
        //System.out.println("addControls() called");
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                //System.out.println("key pressed");
                switch(e.getKeyCode()) {
                    // Personnage
                    case KeyEvent.VK_Q:
                        personnageController.activateLeft();
                        break;
                    case KeyEvent.VK_D:
                        personnageController.activateRight();
                        break;
                    case KeyEvent.VK_Z:
                        personnageController.activateJump();
                        break;

                    // contrôle du viseur - UP/DOWN pour rotation circulaire (0-360°)
                    case KeyEvent.VK_UP:
                        // augmenter l'angle (viser plus haut)
                        shootingAngle += 10;  // +10 degrés
                        if (shootingAngle >= 360) {
                            shootingAngle -= 360;  // revenir à 0 si dépassement
                        }
                        isAiming = true;
                        updateTrajectory();
                        break;
                    case KeyEvent.VK_DOWN:
                        // diminuer l'angle (viser plus bas)
                        shootingAngle -= 10;  // -10 degrés
                        if (shootingAngle < 0) {
                            shootingAngle += 360;  // revenir à 360 si négatif
                        }
                        isAiming = true;
                        updateTrajectory();
                        break;

                    // Caméra déplacements
                    case KeyEvent.VK_LEFT:
                        if(cameraController != null) cameraController.activateLeft();
                        break;
                    case KeyEvent.VK_RIGHT:
                        if(cameraController != null) cameraController.activateRight();
                        break;
                    
                    // Caméra zoom
                    case KeyEvent.VK_T:
                        if(cameraController != null) cameraController.zoomIn();
                        break;
                    case KeyEvent.VK_G:
                        if(cameraController != null) cameraController.zoomOut();
                        break;
                    case KeyEvent.VK_SPACE:
                        if(cameraController != null) cameraController.centerOnCharacter();
                        break;
                    
                    // modif: A déclenche nextRound
                    case KeyEvent.VK_A:
                        if(gameManager != null) gameManager.nextRound();
                        break;

                    // Changement d'arme (touche E)
                    case KeyEvent.VK_E:
                        if(gameManager != null) gameManager.switchWeapon();
                        System.out.println("switch");
                        break;

                    // Sauvegarde de la partie actuel
                    case KeyEvent.VK_J:
                        gameManager.actionPerformed();
                        System.out.println("Save Buttun Pressed");
                        break;
                    
                    // Toggle mode vol (touche P)
                    case KeyEvent.VK_P:
                        personnageController.toggleFlyMode();
                        System.out.println("SUPERMAAAAAAAAAAAAN (todo : ajouter cape)");
                        break;
                    // Touche Echap pour quitter le jeu !!!!!!!!!!!!!!!!!!!!!!!!! A modifier pour le menu plus tard
                    case KeyEvent.VK_ESCAPE:
                        if(gameFrame != null){
                            gameFrame.dispose();
                        }
                        System.exit(0);
                        break;
                    case KeyEvent.VK_I:
                        if (inventoryOverlay != null) {
                        boolean visible = inventoryOverlay.isInventoryVisible();
                        inventoryOverlay.setInventoryVisible(!visible);
                    }
                    break;
                    
                    // Touche H pour ajouter un item de test au nametag
                    case KeyEvent.VK_H:
                        if (gameManager != null && personnageController != null) {
                            // Récupérer le worm actif et son index
                            CharacterModel activeWorm = personnageController.getModel();
                            int wormIndex = gameManager.getActiveWormIndex();
                            int teamIndex = gameManager.getCurrentTeamIndex();
                            
                            // Récupérer le CharacterView correspondant
                            fr.upc.g5e4.view.CharacterView charView = null;
                            charView = gameManager.getListTeamInit().get(teamIndex).getTeamView().getCharacterView(wormIndex);
                            
                            if (charView != null) {
                                charView.getNameTagView().addItemFromResource("/worm.png", "test_item");
                                System.out.println("Item ajouté au nametag");
                            }
                        }
                        break;
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                //System.out.println("key released");
                switch(e.getKeyCode()) {
                    // Personnage
                    case KeyEvent.VK_Q:
                        personnageController.deactivateLeft();
                        break;
                    case KeyEvent.VK_D:
                        personnageController.deactivateRight();
                        break;
                    case KeyEvent.VK_Z:
                        personnageController.deactivateJump();
                        break;

                    // Caméra relâchement touches
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_DOWN:
                        break;
                    case KeyEvent.VK_LEFT:
                        if(cameraController != null) cameraController.deactivateLeft();
                        break;
                    case KeyEvent.VK_RIGHT:
                        if(cameraController != null) cameraController.deactivateRight();
                        break;
                }
            }
        });
        
        // Tir au clic gauche -> vers la souris
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();  // Récupérer le focus au clic
                if (e.getButton() == MouseEvent.BUTTON1) {
                    handleFireAt(e.getX(), e.getY());
                }
            }
        });
    }
    
    /**
     * Tirer vers la position écran (x, y)
     */
    public void handleFireAt(int x, int y) {
        if (personnageController == null || personnageController.getModel() == null) {
            return;
        }

        CharacterModel model = personnageController.getModel();
        float ppm = CharacterModel.getPPM();
        float worldHeightMeters = 24f; // 720 / 30 = 24 (hauteur du monde en mètres)

        // Position du worm en coordonnées monde
        Vec2 pos = model.getBody().getPosition();

        // Position écran approximative
        float wormScreenX = pos.x * ppm;
        float wormScreenY = (worldHeightMeters - pos.y) * ppm;  // inversion Y

        double dx = x - wormScreenX;
        double dy = wormScreenY - y; // inversion Y

        shootingAngle = Math.toDegrees(Math.atan2(dy, dx));
        if (shootingAngle < 0) {
            shootingAngle += 360;
        }

        isAiming = true;
        updateTrajectory();

        // désactiver la visée juste après le tir
        if (trajectoryRenderer != null) {
            trajectoryRenderer.setAiming(false);
        }
        isAiming = false;

        if (personnageController.getCurrentWeapon() != null) {
            personnageController.getCurrentWeapon().setShootingAngle(shootingAngle);
        }

        personnageController.fireWeapon(1);  // direction ignorée, l'angle est dans l'arme
    }
    
    /**
     * met à jour l'affichage de la trajectoire avec l'angle courant
     */
    private void updateTrajectory() {
        if (trajectoryRenderer != null && personnageController != null) {
            trajectoryRenderer.setShootingAngle(shootingAngle);
            trajectoryRenderer.setAiming(isAiming);
            trajectoryRenderer.setShooter(personnageController.getModel());
            
            // Passer aussi l'angle au weapon pour que la trajectoire affichée = trajectoire du tir
            if (personnageController.getCurrentWeapon() != null) {
                personnageController.getCurrentWeapon().setShootingAngle(shootingAngle);
            }
            
            System.out.println("Angle: " + String.format("%.0f°", shootingAngle));
        }
    }
    
   
}

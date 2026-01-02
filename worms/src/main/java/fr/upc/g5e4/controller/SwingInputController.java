// File: fr/upc/g5e4/controller/SwingInputController.java
package fr.upc.g5e4.controller;
//import java.awt.*;
import java.awt.event.*;
import javax.swing.JPanel;

import org.jbox2d.common.Vec2;

import fr.upc.g5e4.model.GameManager;
import fr.upc.g5e4.model.CharacterModel;
import fr.upc.g5e4.view.*;

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
    private TrajectoryView trajectoryRenderer;
    private boolean isAiming = false;  // affichage de la trajectoire
    private double shootingAngle = 0;  // angle de visée en degrés

    private OverlayManager overlayManager;


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
    public void setTrajectoryRenderer(TrajectoryView renderer) {
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
     * Définit le gestionnaire d'overlays
     *
     * @param overlayManager gestionnaire des overlays
     */
    public void setOverlayManager(OverlayManager overlayManager) {
        this.overlayManager = overlayManager;
    }

    //@Override
    public void addControls() {    
        //System.out.println("addControls() called");
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                //Ici on gère les overlays avant tout le reste
                if (overlayManager != null) {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        overlayManager.togglePause();
                        return;
                    }
                    if (e.getKeyCode() == KeyEvent.VK_I) {
                        overlayManager.toggleInventory();
                        return;
                    }
                }
                //Overlays bloquent les entrées de gameplay
                if (overlayManager != null && overlayManager.isBlockingGameplayInput()) {
                    return;
                }
                
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
                                charView.getNameTagView().addItemFromResource("/wormPinky.png", "test_item");
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

                if (overlayManager != null) {
                    // si overlay bloque le gameplay → pas de tir
                    if (overlayManager.isBlockingGameplayInput()) return;

                    // si clic sur HUD → pas de tir
                    if (overlayManager.isHudHit(e.getX(), e.getY())) return;

                    // si inventaire ouvert → pas de tir (l'inventaire gère ses clics)
                    if (overlayManager.isInventoryVisible()) return;
                }

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

        //info de la caméra
        double zoomFactor = 1.0;
        double cameraX = 0.0;
        double cameraY = 0.0;
        
        if (cameraController != null) {
            zoomFactor = cameraController.getCamera().getZoomFactor();
            cameraX = cameraController.getCamera().getX();
            cameraY = cameraController.getCamera().getY();
        }
        
        // obtenir les coordonnées monde
        //convertir les pixel écran (x, y) en coordonnées monde:

        //Compenser le translate(-camX, -camY) en pixels
        double xAfterCamera = x + cameraX;
        double yAfterCamera = y + cameraY;
        
        //inverser le zoom
        double xAfterZoom = xAfterCamera / zoomFactor;
        double yAfterZoom = yAfterCamera / zoomFactor;
        
        //convertir pixels en mètres
        float clickWorldX = (float)(xAfterZoom / ppm);
        float clickWorldY = (float)(yAfterZoom / ppm);
        
        // Inverser l'axe Y
        clickWorldY = (float)(worldHeightMeters - clickWorldY);

        // calculer l'angle entre le worm et le clic
        double dx = clickWorldX - pos.x;
        double dy = clickWorldY - pos.y;

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

        // Calculer la direction basée sur l'angle
        int direction = (dx > 0) ? 1 : -1;
        // Convertir l'angle en radians pour fireWeaponWithAngle
        double angleInRadians = Math.toRadians(shootingAngle);
        personnageController.fireWeaponWithAngle(angleInRadians, direction);
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

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
    private boolean mouseButtonPressed = false;  // regarde si la souris est appuyé
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
     * Getter d'inventoryOverlay
     * @return
     */
    private InventoryOverlay getInventoryOverlay() {
        return (overlayManager != null) ? overlayManager.getInventoryOverlay() : null;
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
                    case KeyEvent.VK_UP:
                        if(cameraController != null) cameraController.zoomIn();
                        break;
                    case KeyEvent.VK_DOWN:
                        if(cameraController != null) cameraController.zoomOut();
                        break;

                    case KeyEvent.VK_SPACE:
                        if(cameraController != null) cameraController.centerOnCharacter();
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
                    mouseButtonPressed = true;
                    updateTrajectoryFromMouse(e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (overlayManager != null) {
                    if (overlayManager.isBlockingGameplayInput()) return;
                    if (overlayManager.isHudHit(e.getX(), e.getY())) return;
                    if (overlayManager.isInventoryVisible()) return;
                }

                if (e.getButton() == MouseEvent.BUTTON1 && mouseButtonPressed) {
                    mouseButtonPressed = false;
                    fireWeaponAtAngle(e.getX(), e.getY());
                }
            }
        });

        // Mise à jour continue de la trajectoire quand la souris bouge 
        addMouseMotionListener(new java.awt.event.MouseMotionListener() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (mouseButtonPressed) {
                    updateTrajectoryFromMouse(e.getX(), e.getY());
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (mouseButtonPressed) {
                    updateTrajectoryFromMouse(e.getX(), e.getY());
                }
            }
        });
    }
    
    /**
     * désactive l'affichage de la trajectoire
     */
    public void disableTrajectory() {
        isAiming = false;
        mouseButtonPressed = false;
        if (trajectoryRenderer != null) {
            trajectoryRenderer.setAiming(false);
        }
    }
    
    /**
     * met à jour la trajectoire basée sur la position écran
     * version simplifiée sans vérification de focus
     * n'active que si une arme est équipée
     */
    public void updateTrajectoryFromMouseMotion(int x, int y) {
        // vérifier qu'une arme est équipée et pas un gadget
        if (personnageController == null || personnageController.getCurrentWeapon() == null) {
            return;
        }
        
        InventoryOverlay inv = getInventoryOverlay();
        if (inv != null && inv.getEquippedGadget() != null) {
            return;
        }

        updateTrajectoryFromMouse(x, y);
    }
    
    /**
     * MAJ la trajectoire basée sur la position écran
     * affiche la trajectoire sans tirer
     */
    public void updateTrajectoryFromMouse(int x, int y) {
        if (personnageController == null || personnageController.getModel() == null) {
            return;
        }

        // ne pas afficher la trajectoire si un gadget est équipé
        InventoryOverlay inv = getInventoryOverlay();
        if (inv != null && inv.getEquippedGadget() != null) {
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

        // inverser le zoom d'abord 
        double xAfterZoom = x / zoomFactor;
        double yAfterZoom = y / zoomFactor;
        
        //compenser le décalage de la caméra
        double clickWorldX_temp = xAfterZoom + cameraX;
        double clickWorldY_temp = yAfterZoom + cameraY;
        
        //convertir pixels en mètres
        float clickWorldX = (float)(clickWorldX_temp / ppm);
        float clickWorldY = (float)(clickWorldY_temp / ppm);
        
        // Inverser l'axe Y
        clickWorldY = (float)(worldHeightMeters - clickWorldY);

        // calculer l'angle entre le worm et le clic
        double dx = clickWorldX - pos.x;
        double dy = clickWorldY - pos.y;

        shootingAngle = Math.toDegrees(Math.atan2(dy, dx));
        if (shootingAngle < 0) {
            shootingAngle += 360;
        }

        // mettre à jour l'arme avec le nouvel angle
        if (personnageController.getCurrentWeapon() != null) {
            personnageController.getCurrentWeapon().setShootingAngle(shootingAngle);
        }

        // afficher la trajectoire en temps réel
        isAiming = true;
        updateTrajectory();
    }

    /**
     * effectue le tir avec l'angle actuel
     */
    public void fireWeaponAtAngle(int x, int y) {
        if (personnageController == null || personnageController.getModel() == null) {
            return;
        }

        // vérifier si un gadget est équipé au lieu d'une arme
        InventoryOverlay inv = getInventoryOverlay();
        if (inv != null && inv.getEquippedGadget() != null) {
            inv.useEquippedGadget();
            return;
        }


        CharacterModel model = personnageController.getModel();
        
        // position du worm en coordonnées monde
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
        
        float ppm = CharacterModel.getPPM();
        float worldHeightMeters = 24f;

        // obtenir les coordonnées monde
        // inverser le zoom d'abord (diviser par le zoom)
        double xAfterZoom = x / zoomFactor;
        double yAfterZoom = y / zoomFactor;
        
        //compenser le décalage de la caméra
        double clickWorldX_temp = xAfterZoom + cameraX;
        double clickWorldY_temp = yAfterZoom + cameraY;
        
        //convertir pixels en mètres
        float clickWorldX = (float)(clickWorldX_temp / ppm);
        float clickWorldY = (float)(clickWorldY_temp / ppm);
        
        // Inverser l'axe Y
        clickWorldY = (float)(worldHeightMeters - clickWorldY);

        // calculer la direction basée sur l'angle
        double dx = clickWorldX - pos.x;
        int direction = (dx > 0) ? 1 : -1;

        // désactiver l'affichage de la trajectoire
        isAiming = false;
        if (trajectoryRenderer != null) {
            trajectoryRenderer.setAiming(false);
        }

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
            
            //System.out.println("Angle: " + String.format("%.0f°", shootingAngle));
        }
    }
    
   
}

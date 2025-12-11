// File: fr/upc/g5e4/controller/SwingInputController.java
package fr.upc.g5e4.controller;
//import java.awt.*;
import java.awt.event.*;

import javax.swing.JPanel;

import fr.upc.g5e4.model.GameManager;

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

                    // Caméra déplacements
                    case KeyEvent.VK_UP:
                        if(cameraController != null) cameraController.activateUp();
                        break;
                    case KeyEvent.VK_DOWN:
                        if(cameraController != null) cameraController.activateDown();
                        break;
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

                    // Ouverture de l'inventaire
                    case KeyEvent.VK_P:
                        gameManager.actionPerformed();
                        System.out.println("Save Buttun Pressed");
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
                        if(cameraController != null) cameraController.deactivateUp();
                        break;
                    case KeyEvent.VK_DOWN:
                        if(cameraController != null) cameraController.deactivateDown();
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
        
        // Listener de souris pour le tir au click gauche
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    personnageController.fireWeapon(e.getX(), e.getY());
                }
            }
        });
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
}

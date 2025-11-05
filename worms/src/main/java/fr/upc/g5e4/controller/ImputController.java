package fr.upc.g5e4.controller;
//import java.awt.*;
import java.awt.event.*;

import javax.swing.JPanel;

/** 
 * Classe pour les Entrées Clavier Souris qui reprend celle de Nassim
 * Pour le moment je reprends l'ancien model et elle herite de JPanel
 * Mais le mieux serait de la recréer de 0 et pas qu'elle herite de JPanel 
 * 
 * @author Nassim pour plus d'info sur son fonctionnement
 */
public class ImputController extends JPanel{
    private PersonnageController personnageController;
    
    public ImputController(PersonnageController personnageController){
        this.personnageController = personnageController;
        addControls();
    }

    private void addControls() {    
        //System.out.println("addControls() called");
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                //System.out.println("key pressed");
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_Q:
                        personnageController.activateLeft();
                        break;
                        
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        personnageController.activateRight();
                        break;
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                //System.out.println("key released");
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_Q:
                        personnageController.deactivateLeft();
                        break;
                        
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        personnageController.deactivateRight();
                        break;
                }
            }
        });
    }
}

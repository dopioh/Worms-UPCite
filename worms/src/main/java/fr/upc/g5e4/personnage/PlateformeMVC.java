package fr.upc.g5e4.personnage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import fr.upc.g5e4.personnage.model.PersonnageModel;
import fr.upc.g5e4.personnage.view.PersonnageView;
import fr.upc.g5e4.personnage.controller.PersonnageController;

/**
 * Platforme principale du jeu
 * Gère l'affichage et l'intégration des composants MVC
 */
public class PlateformeMVC extends JPanel {

    private PersonnageModel personnageModel;

    private PersonnageView personnageView;
    
    private PersonnageController personnageController;
    
    private int hauteurSol;
    
    /**
     * Constructeur par défaut
     */
    public PlateformeMVC() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(new Color(30, 60, 110));
        
        hauteurSol = 500;
        
        // Initialisation MVC
        personnageModel = new PersonnageModel(400, hauteurSol - 20);
        personnageView = new PersonnageView();
        personnageController = new PersonnageController(personnageModel);

        // Initialisation de l'interface
        setFocusable(true);
        requestFocusInWindow();
        
        // Ajouter les contrôles
        ajouterControles();
        
        // Timer pour mettre à jour le jeu
        Timer timer = new Timer(16, e -> gameUpdate());
        timer.start();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                             RenderingHints.VALUE_ANTIALIAS_ON);
        
        drawGrid(g2d);
        drawGround(g2d);
        
        // Dessiner le personnage via sa VIEW
        if (personnageModel != null) {
            personnageView.draw(g2d, personnageModel);
        }
    }
    
    /**
     * Dessine la grille blueprint
     */
    private void drawGrid(Graphics2D g2d) {
        int width = getWidth();
        int height = getHeight();

        g2d.setColor(new Color(70, 120, 180, 100));

        for (int x = 0; x < width; x += 20) {
            if (x % 100 == 0) {
                g2d.setStroke(new BasicStroke(1.5f));
            } else {
                g2d.setStroke(new BasicStroke(0.5f));
            }
            g2d.drawLine(x, 0, x, height);
        }

        for (int y = 0; y < height; y += 20) {
            if (y % 100 == 0) {
                g2d.setStroke(new BasicStroke(1.5f));
            } else {
                g2d.setStroke(new BasicStroke(0.5f));
            }
            g2d.drawLine(0, y, width, y);
        }
    }
    
    /**
     * Dessine le sol
     */
    private void drawGround(Graphics2D g2d) {
        int width = getWidth();
        int height = getHeight();

        g2d.setColor(new Color(50, 100, 150, 150));
        g2d.fillRect(0, height - 100, width, 100);

        g2d.setColor(new Color(100, 180, 255));
        g2d.setStroke(new BasicStroke(3.0f));
        g2d.drawLine(0, height - 100, width, height - 100);
    }
    
    /**
     * Ajoute les events clavier
     */
    private void ajouterControles() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
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
    
    /**
     * Met à jour le jeu
     */
    private void gameUpdate() {
        if (personnageController.update()) {
            repaint();
        }
    }
    
    /**
     * Point d'entrée du programme
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Terrain Blueprint - Worms (MVC)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new PlateformeMVC());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

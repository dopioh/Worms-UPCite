package fr.upc.g5e4;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import fr.upc.g5e4.model.PersonnageModel;
import fr.upc.g5e4.view.PersonnageView;
import fr.upc.g5e4.controller.PersonnageController;

/**
 * Platforme principale du jeu
 * Gère l'affichage et l'intégration des composants MVC
 */
public class PlateformeMVC extends JPanel {

    private PersonnageModel personnageModel;

    private PersonnageView personnageView;
    
    private PersonnageController personnageController;

    private int groundHeight;

    /**
     * Constructeur par défaut
     */
    public PlateformeMVC() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(new Color(30, 60, 110));
        
        groundHeight = 500;
        
        // Initialisation MVC
        personnageModel = new PersonnageModel(400, groundHeight - 20);
        personnageView = new PersonnageView();
        personnageController = new PersonnageController(personnageModel);

        setFocusable(true);
        requestFocusInWindow();
        
        addControls();
        
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
    
    private void drawGround(Graphics2D g2d) {
        int width = getWidth();
        int height = getHeight();

        g2d.setColor(new Color(50, 100, 150, 150));
        g2d.fillRect(0, height - 100, width, 100);

        g2d.setColor(new Color(100, 180, 255));
        g2d.setStroke(new BasicStroke(3.0f));
        g2d.drawLine(0, height - 100, width, height - 100);
    }

    private void addControls() {
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
     * met a jour l'affichage de la fenetre
     */
    private void gameUpdate() {
        if (personnageController.update()) {
            repaint();
        }
    }

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

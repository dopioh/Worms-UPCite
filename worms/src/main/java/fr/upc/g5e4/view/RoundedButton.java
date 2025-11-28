package fr.upc.g5e4.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;

/**
 * JBouton personnalisé avec des coins arrondis et un effet de survol
 */
public class RoundedButton extends JButton {
    
    private int radius;
    private Color hoverColor;
    private boolean isHovering;

    public RoundedButton(String text, int radius) {
        super(text);
        this.radius = radius;
        this.isHovering = false;
        
        setupButton();
        addHoverEffect();
    }

    //Nettoyage de boutton par défaut pour le personnaiser
    private void setupButton() {
        setFocusPainted(false); // Désactive l'indicateur de focus
        setContentAreaFilled(false); // Désactive le rendu standard de l'arrière-plan
        setOpaque(false); // gérer correctement la transparence des coins arrondis
        setBorderPainted(false); // Désactive l'affichage de la bordure
        setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    }

    private void addHoverEffect() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovering = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovering = false;
                repaint();
            }
        });
    }
    
    //on definit la couleur dans view
    public void setHoverColor(Color color) {
        this.hoverColor = color;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create(); // On convertit en Graphics2D (plus puissant)
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                        RenderingHints.VALUE_ANTIALIAS_ON); //lisser les bords arrondis

        int width = getWidth();
        int height = getHeight();

        // Choix de la couleur selon l'état
        Color bgColor = getBackground();
        if (getModel().isPressed()) {

            // peut etre utile (l'utilisateur est en train de cliquer)
            
        } else if (isHovering && hoverColor != null) {
            bgColor = hoverColor;
        }

        // Dessine le fond arrondi
        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, width, height, radius, radius);

        // Effet de brillance en haut
        g2.setColor(new Color(255, 255, 255, 30));
        g2.fillRoundRect(0, 0, width, height / 2, radius, radius);

        g2.dispose();

        // Dessine le texte
        super.paintComponent(g);
    }
}
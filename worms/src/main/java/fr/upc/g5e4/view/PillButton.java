package fr.upc.g5e4.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;


/**
 * COMMENT UTILISER:
 * - PillButton btn = new PillButton("Texte");
 * - Ajoutez un ActionListener comme un JButton normal
 * 
 * EXEMPLE:
 * PillButton btn = new PillButton("Cliquer moi");
 * btn.addActionListener(e -> System.out.println("Bouton cliqué!"));
 * panel.add(btn);
 * btn.setFont(new Font("Arial", Font.BOLD, 20)); // Change la taille de la police
 * 
 * PLEINE ECRAN:
 * - btn.setPreferredSize(new Dimension(largeur, hauteur)); // Ajuste la taille du bouton
 * - btn.setFont(btn.getFont().deriveFont((float)nouvelleTaille)); // Ajuste la taille de la police
 * Exemple: float scale = fullScreen ? 1.3f : 1.0f;
 *          int buttonWidth = (int)(baseWidth * scale);
 *          int buttonHeight = (int)(baseHeight * scale);
 *          int fontSize = (int)(baseFontSize * scale);
 * 
 * POUR CHANGER LES COULEURS:
 * btn.setBackground(nouvelleColor) pour la couleur normale
 * btn.setHoverColor(nouvelleColor) pour la couleur au survol
 * btn.setForeground(nouvelleColor) pour la couleur du texte
 * 
 * COULEURS PAR DÉFAUT :
 * - ACTIVE: new Color(66, 139, 82)
 * - INACTIVE: new Color(41, 59, 45)
 * - HOVER: new Color(42, 99, 55)
 * - TEXT: Color.WHITE
 */


/**
 * Bouton en forme de pilule pour les onglets reutilisables
 */
public class PillButton extends JButton {
    
    private Color hoverColor;
    private boolean isHovering;
    private Font googleSansFont;

    public PillButton(String text) {
        super(text);
        this.isHovering = false;
        
        // Couleurs par défaut (SettingsView)
        setBackground(new Color(41, 59, 45)); // TAB_INACTIVE
        setHoverColor(new Color(42, 99, 55)); // TAB_HOVER
        setForeground(Color.WHITE); // TEXT_COLOR
        
        loadFont();
        setupButton();
        addHoverEffect();
    }

    private void loadFont() {
        try {
            Font baseFont = Font.createFont(Font.TRUETYPE_FONT,
                getClass().getResourceAsStream("/GoogleSans-Bold.ttf"));
            googleSansFont = baseFont.deriveFont(18f); // Taille par défaut
            super.setFont(googleSansFont);
        } catch (Exception e) {
            System.err.println("Erreur chargement police Google Sans: " + e.getMessage());
            googleSansFont = new Font("Arial", Font.BOLD, 18);
            super.setFont(googleSansFont);
        }
    }

    private void setupButton() {
        setFocusPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setBorderPainted(false);
        setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
    }
    
    @Override
    public void setFont(Font font) {
        if (googleSansFont != null && font != null) {
            super.setFont(googleSansFont.deriveFont(font.getSize2D()));
        } else {
            super.setFont(font);
        }
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
    
    public void setHoverColor(Color color) {
        this.hoverColor = color;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                        RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Choix de la couleur selon l'état
        Color bgColor = getBackground();
        if (isHovering && hoverColor != null) {
            bgColor = hoverColor;
        }

        // Dessine le fond en forme de pilule (rayon = hauteur/2 pour les côtés complètement arrondis)
        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, width, height, height, height);

        g2.dispose();

        // Dessine le texte
        super.paintComponent(g);
    }
}

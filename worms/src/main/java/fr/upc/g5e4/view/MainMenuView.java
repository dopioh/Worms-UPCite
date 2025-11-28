package fr.upc.g5e4.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;


/**
 * Vue de l'écran d'accueil du jeu Worms
 * Affiche le menu principal avec les boutons d'action
 */
public class MainMenuView extends JFrame {
    
    // couleurs
    private static final Color BUTTON_BASE = new Color(131, 172, 140);
    private static final Color BUTTON_HOVER = new Color(92, 121, 98);

    private JButton btnNewGame;
    private JButton btnResume;
    private JButton btnConfig;
    private JButton btnQuit;

    public MainMenuView() {
        super("Worms - Home");
        initComponents();
        setupLayout();
    }

    private void initComponents() {
        // Création du panneau de fond
        JLabel background = createBackground();
        setContentPane(background);

        // Titre
        addTitle(background);

        // Création des boutons
        btnNewGame = createMenuButton("Nouvelle Partie");
        btnResume = createMenuButton("Poursuivre une partie");
        btnConfig = createMenuButton("Reglages");
        btnQuit = createMenuButton("Quitter");

        // Ajout des boutons au panneau
        background.add(btnNewGame);
        background.add(Box.createVerticalStrut(10));
        background.add(btnResume);
        background.add(Box.createVerticalStrut(10));
        background.add(btnConfig);
        background.add(Box.createVerticalStrut(10));
        background.add(btnQuit);
    }

    private void setupLayout() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private JLabel createBackground() {
        ImageIcon bgIcon = loadImage("/background.png");
        JLabel bg = (bgIcon != null) ? new JLabel(bgIcon) : new JLabel();
        bg.setLayout(new BoxLayout(bg, BoxLayout.Y_AXIS));
        bg.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        return bg;
    }

    private void addTitle(JLabel parent) {
        ImageIcon titleIcon = loadImage("/title.png");
        
        if (titleIcon != null) {
            // Redimensionne l'image à 300px de largeur
            java.awt.Image scaledImage = titleIcon.getImage().getScaledInstance(
                300, -1, java.awt.Image.SCALE_SMOOTH);
            JLabel title = new JLabel(new ImageIcon(scaledImage));
            title.setAlignmentX(Component.CENTER_ALIGNMENT);
            parent.add(title);
        } else {
            // Texte si l'image n'existe pas
            JLabel title = new JLabel("Worms");
            title.setFont(new Font("Arial", Font.BOLD, 50));
            title.setAlignmentX(Component.CENTER_ALIGNMENT);
            title.setForeground(new Color(40, 49, 32));
            parent.add(title);
        }
        
        parent.add(Box.createVerticalStrut(15));
    }

    //methode template pour creer des bouttons
    private JButton createMenuButton(String text) {
        RoundedButton button = new RoundedButton(text, 18);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(BUTTON_BASE);
        button.setHoverColor(BUTTON_HOVER);
        button.setMaximumSize(new Dimension(220, 36));
        return button;
    }

    private ImageIcon loadImage(String path) {
        java.net.URL url = getClass().getResource(path);
        return (url != null) ? new ImageIcon(url) : null;
    }

    // Getters pour les boutons (le controller en aura besoin)
    public JButton getBtnNewGame() {
        return btnNewGame;
    }

    public JButton getBtnResume() {
        return btnResume;
    }

    public JButton getBtnConfig() {
        return btnConfig;
    }

    public JButton getBtnQuit() {
        return btnQuit;
    }
}

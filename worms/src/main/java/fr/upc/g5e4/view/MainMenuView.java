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
    private JLabel titleLabel;
    private boolean isFullScreen = false;

    public MainMenuView() {
        super("Worms - Home");
        initComponents();
        setupLayout();
        setupKeyBindings();
    }

    private void setupKeyBindings() {
        setupFullScreenToggle();
        setupExitOnEscape();
    }
    
    private void setupFullScreenToggle() {
        // F11 pour basculer en plein écran
        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(javax.swing.KeyStroke.getKeyStroke("F11"), "toggleFullScreen");
        getRootPane().getActionMap().put("toggleFullScreen", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                toggleFullScreen();
            }
        });
    }

    private void setupExitOnEscape() {
        // ESC pour quitter
        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(javax.swing.KeyStroke.getKeyStroke("ESCAPE"), "exitGame");
        getRootPane().getActionMap().put("exitGame", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                System.exit(0);
            }
        });
    }
    
    private void toggleFullScreen() {
        java.awt.GraphicsDevice device = java.awt.GraphicsEnvironment
            .getLocalGraphicsEnvironment().getDefaultScreenDevice();
        
        if (isFullScreen) {
            device.setFullScreenWindow(null);
            dispose();
            setUndecorated(false);
            setVisible(true);
            isFullScreen = false;
            updateSizes(false);
        } else {
            dispose();
            setUndecorated(true);
            setVisible(true);
            device.setFullScreenWindow(this);
            isFullScreen = true;
            updateSizes(true);
        }
    }
    
    private void updateSizes(boolean fullScreen) {
        // Tailles pour plein écran vs fenêtré
        int titleWidth = fullScreen ? 550 : 400;
        int buttonWidth = fullScreen ? 380 : 280;
        int buttonHeight = fullScreen ? 60 : 45;
        int fontSize = fullScreen ? 24 : 18;
        
        // Mettre à jour le titre
        ImageIcon titleIcon = loadImage("/title.png");
        if (titleIcon != null && titleLabel != null) {
            java.awt.Image scaledImage = titleIcon.getImage().getScaledInstance(
                titleWidth, -1, java.awt.Image.SCALE_SMOOTH);
            titleLabel.setIcon(new ImageIcon(scaledImage));
        } else if (titleLabel != null) {
            titleLabel.setFont(new Font("Arial", Font.BOLD, fullScreen ? 90 : 70));
        }
        
        // Mettre à jour les boutons
        JButton[] buttons = {btnNewGame, btnResume, btnConfig, btnQuit};
        for (JButton btn : buttons) {
            btn.setFont(new Font("Arial", Font.BOLD, fontSize));
            btn.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        }
    }
    
    public boolean isFullScreen() {
        return isFullScreen;
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
        setSize(1280, 720);
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
            // Redimensionne l'image à 400px de largeur
            java.awt.Image scaledImage = titleIcon.getImage().getScaledInstance(
                400, -1, java.awt.Image.SCALE_SMOOTH);
            titleLabel = new JLabel(new ImageIcon(scaledImage));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            parent.add(titleLabel);
        } else {
            // Texte si l'image n'existe pas
            titleLabel = new JLabel("Worms");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 70));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titleLabel.setForeground(new Color(40, 49, 32));
            parent.add(titleLabel);
        }
        
        parent.add(Box.createVerticalStrut(15));
    }

    //methode template pour creer des bouttons
    private JButton createMenuButton(String text) {
        RoundedButton button = new RoundedButton(text, 24);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(BUTTON_BASE);
        button.setHoverColor(BUTTON_HOVER);
        button.setMaximumSize(new Dimension(280, 45));
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

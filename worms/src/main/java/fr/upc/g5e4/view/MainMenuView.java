package fr.upc.g5e4.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * Vue de l'écran d'accueil du jeu Worms
 * Affiche le menu principal avec les boutons d'action
 */
public class MainMenuView extends JPanel {
    
    // couleurs de avant pour les bouttons
    // private static final Color BUTTON_BASE = new Color(131, 172, 140);
    // private static final Color BUTTON_HOVER = new Color(92, 121, 98);

    private JButton btnNewGame;
    private JButton btnResume;
    private JButton btnConfig;
    private JButton btnQuit;
    private JLabel titleLabel;
    private ShopView shopView;
    private JPanel centerPanel;
    private JLabel background;

    public MainMenuView() {
        initComponents();
        setupLayout();
        setupResizeListener();
    }
    
    public void updateSizes(boolean fullScreen) {
        // Tailles pour plein écran vs fenêtré
        int titleWidth = fullScreen ? 450 : 400; // si on agrandi le title il est coupé
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

    private void initComponents() {
        // Création du panneau de fond
        background = createBackground();
        setLayout(new java.awt.BorderLayout());
        add(background, java.awt.BorderLayout.CENTER);

        // Boutique à gauche en position absolue
        shopView = new ShopView();
        background.add(shopView);
        
        // Panel au centre avec titre et boutons
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        
        // Titre
        addTitle(centerPanel);

        // Création des boutons
        btnNewGame = createMenuButton("Nouvelle Partie");
        btnResume = createMenuButton("Poursuivre une partie");
        btnConfig = createMenuButton("Reglages");
        btnQuit = createMenuButton("Quitter");

        // Ajout des boutons au panneau central
        centerPanel.add(btnNewGame);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(btnResume);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(btnConfig);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(btnQuit);
        
        background.add(centerPanel);
    }

    private void setupLayout() {
        setPreferredSize(new Dimension(1280, 720));
    }
    
    private void setupResizeListener() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                repositionComponents();
            }
        });
    }
    
    private void repositionComponents() {
        int width = getWidth();
        int height = getHeight();
        
        // la boutique à gauche
        shopView.setBounds(20, 100, 350, Math.min(520, height - 200));
        
        // le panel au centre
        int centerPanelWidth = 400;
        int centerPanelX = (width - centerPanelWidth) / 2;
        centerPanel.setBounds(centerPanelX, 20, centerPanelWidth, height - 40);
    }

    private JLabel createBackground() {
        ImageIcon bgIcon = loadImage("/background.png");
        JLabel bg = (bgIcon != null) ? new JLabel(bgIcon) : new JLabel();
        bg.setLayout(null); // Layout null pour positionnement absolu
        return bg;
    }

    private void addTitle(JPanel parent) {
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
        PillButton button = new PillButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
    
    public ShopView getShopView() {
        return shopView;
    }
}

package fr.upc.g5e4.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.upc.g5e4.util.ImageCache;

import fr.upc.g5e4.view.UI.*;

import fr.upc.g5e4.model.TutorialModel;
import fr.upc.g5e4.view.TutorialView;
import fr.upc.g5e4.controller.TutorialController;



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
    private JButton btnHowToPlay;
    private JButton btnConfig;
    private JButton btnQuit;
    private JLabel titleLabel;
    private TutorialController tutorialController;
    private JPanel centerPanel;
    private JPanel titlePanel;
    private BufferedImage backgroundImage;

    public MainMenuView() {
        loadBackgroundImage();
        initComponents();
        setupLayout();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            Graphics2D g2d = (Graphics2D) g;

            // rendu de haute qualité
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            // Calcul du ratio pour zoomer (cover) plutôt que stretch
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            int imgWidth = backgroundImage.getWidth();
            int imgHeight = backgroundImage.getHeight();
            
            double panelRatio = (double) panelWidth / panelHeight;
            double imgRatio = (double) imgWidth / imgHeight;
            
            int drawWidth, drawHeight, drawX, drawY;
            
            if (panelRatio > imgRatio) {
                // Panel plus large que l'image : remplir en largeur
                drawWidth = panelWidth;
                drawHeight = (int) (panelWidth / imgRatio);
                drawX = 0;
                drawY = (panelHeight - drawHeight) / 2;
            } else {
                // Panel plus haut que l'image : remplir en hauteur
                drawHeight = panelHeight;
                drawWidth = (int) (panelHeight * imgRatio);
                drawX = (panelWidth - drawWidth) / 2;
                drawY = 0;
            }
            
            g2d.drawImage(backgroundImage, drawX, drawY, drawWidth, drawHeight, this);
        }
    }
    
    private void loadBackgroundImage() {
        try {
            java.net.URL url = getClass().getResource("/background.png");
            if (url != null) {
            backgroundImage = ImageCache.get("/background.png");
            if (backgroundImage == null) {
                System.err.println("Impossible de charger le fond du menu principal");
            }
        }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image de fond: " + e.getMessage());
        }
    }
    
    /**
     * Calcule le facteur de scaling basé sur les dimensions réelles de l'écran
     */
    private float calculateScale() {
        // Dimensions de base (fenêtré)
        float baseWidth = 1280f;
        float baseHeight = 720f;
        
        // Dimensions actuelles
        float currentWidth = getWidth();
        float currentHeight = getHeight();
        
        // Calculer le scale basé sur le rapport entre la taille actuelle et la taille de base
        float scaleX = currentWidth / baseWidth;
        float scaleY = currentHeight / baseHeight;
        
        return Math.min(scaleX, scaleY);
    }
    
    public void updateSizes(boolean fullScreen) {
        // Facteur de scaling dynamique basé sur la taille réelle de l'écran
        float scale = fullScreen ? calculateScale() : 1.0f;
        
        // Tailles de base
        int baseTitleWidth = 400;
        int baseButtonWidth = 280;
        int baseButtonHeight = 45;
        int baseFontSize = 18;
        
        // Application du scaling
        int titleWidth = (int)(baseTitleWidth * scale);
        int buttonWidth = (int)(baseButtonWidth * scale);
        int buttonHeight = (int)(baseButtonHeight * scale);
        int fontSize = (int)(baseFontSize * scale);
        
        // Mettre à jour le titre
        ImageIcon titleIcon = loadImage("/title.png");
        if (titleIcon != null && titleLabel != null) {
            java.awt.Image scaledImage = titleIcon.getImage().getScaledInstance(
                titleWidth, -1, java.awt.Image.SCALE_SMOOTH);
            titleLabel.setIcon(new ImageIcon(scaledImage));
        } else if (titleLabel != null) {
            int titleFontSize = (int)(70 * scale);
            titleLabel.setFont(new Font("Arial", Font.BOLD, titleFontSize));
        }
        
        // Mettre à jour les boutons
        JButton[] buttons = {btnNewGame, btnResume, btnHowToPlay, btnConfig, btnQuit};
        for (JButton btn : buttons) {
            btn.setFont(new Font("Arial", Font.BOLD, fontSize));
            btn.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        }
    }

    private void initComponents() {
        setLayout(null);
        setOpaque(true);
        
        // Panel séparé pour le titre
        titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        addTitle(titlePanel);
        add(titlePanel);
        
        // Panel au centre avec les boutons
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        btnNewGame = createMenuButton("Nouvelle Partie");
        btnResume = createMenuButton("Poursuivre une partie");
        btnHowToPlay = createMenuButton("Comment jouer");
        btnConfig = createMenuButton("Reglages");
        btnQuit = createMenuButton("Quitter");

        centerPanel.add(btnNewGame);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(btnResume);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(btnHowToPlay);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(btnConfig);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(btnQuit);
        
        add(centerPanel);
        
        // setup de l'overlay tutoriel
        TutorialModel tutorialModel = new TutorialModel();
        TutorialView tutorialView = new TutorialView(tutorialModel);
        // init du controller pour getTutorialController utilisé par MainMenuController
        tutorialController = new TutorialController(tutorialModel, tutorialView);
        tutorialView.setVisible(false);
        add(tutorialView);
        
        // dessine par dessus les composants
        setComponentZOrder(tutorialView, 0);
    }

    private void setupLayout() {
        setPreferredSize(new Dimension(1280, 720));
    }
    
    /**
     * Repositionne les composants en fonction de la taille actuelle de la fenêtre
     */
    public void repositionComponents() {
        int width = getWidth();
        int height = getHeight();
        
        // Calculer les positions proportionnellement à la hauteur
        int titleY = (int)(height * 0.08); // 8% du haut
        int titleHeight = (int)(height * 0.2); // 20% de la hauteur pour le titre
        
        // Positionner le titre en haut avec une largeur flexible
        int titlePanelWidth = Math.min(width - 40, 800); // Max 800px ou largeur écran - marges
        int titlePanelX = (width - titlePanelWidth) / 2;
        titlePanel.setBounds(titlePanelX, titleY, titlePanelWidth, titleHeight);
        
        // Positionner le panel des boutons en dessous du titre avec un espace proportionnel
        int centerPanelWidth = 400;
        int centerPanelX = (width - centerPanelWidth) / 2;
        int centerPanelY = titleY + titleHeight + (int)(height * 0.03); // 3% d'espace après le titre
        int centerPanelHeight = height - centerPanelY - (int)(height * 0.05); // 5% de marge en bas
        centerPanel.setBounds(centerPanelX, centerPanelY, centerPanelWidth, centerPanelHeight);
        
        // position de l'overlay tuto au centre de la fenetre
        if (tutorialController != null) {
            tutorialController.getView().setBounds(0, 0, width, height);
        }
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
    
    public JButton getBtnHowToPlay() {
        return btnHowToPlay;
    }
    
    public TutorialController getTutorialController() {
        return tutorialController;
    }
}

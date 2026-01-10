package fr.upc.g5e4.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

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
 * @author Ian
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
    private BufferedImage backgroundImage;

    public MainMenuView() {
        loadBackgroundImage();
        initComponents();
        setupLayout();
        setupResizeListener();
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
            backgroundImage = ImageCache.get("/background.png");
            if (backgroundImage == null) {
                System.err.println("Impossible de charger le fond du menu principal");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image de fond: " + e.getMessage());
        }
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
            Image scaledImage = titleIcon.getImage().getScaledInstance(
                titleWidth, -1, Image.SCALE_SMOOTH);
            titleLabel.setIcon(new ImageIcon(scaledImage));
        } else if (titleLabel != null) {
            titleLabel.setFont(new Font("Arial", Font.BOLD, fullScreen ? 90 : 70));
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
        
        // panel des boutons du menu
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        
        addTitle(centerPanel);

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
                
        // le panel au centre
        int centerPanelWidth = 400;
        int centerPanelX = (width - centerPanelWidth) / 2;
        centerPanel.setBounds(centerPanelX, 20, centerPanelWidth, height - 40);
        
        // position de l'overlay tuto au centre de la fenetre
        if (tutorialController != null) {
            tutorialController.getView().setBounds(0, 0, width, height);
        }
    }

    private void addTitle(JPanel parent) {
        ImageIcon titleIcon = loadImage("/title.png");
        
        if (titleIcon != null) {
            // Redimensionne l'image à 400px de largeur
            Image scaledImage = titleIcon.getImage().getScaledInstance(
                400, -1, Image.SCALE_SMOOTH);
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
        BufferedImage img = ImageCache.get(path);
        return (img != null) ? new ImageIcon(img) : null;
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

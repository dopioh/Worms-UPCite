package fr.upc.g5e4.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * STRUCTURE:
 * - SettingsView (panel principal)
 *   - topPanel: Boutons Retour, Map, Equipes, Autre
 *      - contentPanel: Affiche un des 3 sous-panels
 *          - mapPanel (createMapPanel)
 *          - teamsPanel (createTeamsPanel)
 *          - otherPanel (createOtherPanel)
 * 
 * COMMENT AJOUTER DU CONTENU:
 * - Aller dans createMapPanel() / createTeamsPanel() / createOtherPanel()
 * - Ajouter vos composants au panel
 * 
 * PLEINE ÉCRAN:
 * - Ajoutez vos trucs dans updateSizes() si besoin
 */


/**
 * Vue pour le panneau des réglages du jeu
 * Affiche les onglets Map, Equipes, Autre avec un fond personnalisé
 */
public class SettingsView extends JPanel {
    
    private static final Color TAB_ACTIVE = new Color(66, 139, 82);
    private static final Color TAB_INACTIVE = new Color(41, 59, 45);
    private static final Color TAB_HOVER = new Color(42, 99, 55);
    private static final Color TEXT_COLOR = Color.WHITE;
    
    private JButton btnMapTab;
    private JButton btnTeamsTab;
    private JButton btnOtherTab;
    private JButton btnBack;
    
    private JPanel contentPanel;
    private JPanel mapPanel;
    private JPanel teamsPanel;
    private JPanel otherPanel;
    
    private Image backgroundImage;
    private String currentTab = "Map";
    
    // Variables pour le scaling
    private boolean isFullScreen = false;

    public SettingsView() {
        loadBackgroundImage();
        initComponents();
        setupLayout();
        showPanel("Map");
        
        // Ajouter un listener pour détecter les changements de taille
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                // Considérer comme plein écran si largeur > 1280
                boolean newFullScreen = getWidth() > 1280;
                if (newFullScreen != isFullScreen) {
                    isFullScreen = newFullScreen;
                    updateSizes(isFullScreen);
                }
            }
        });
    }
    
    /**
     * Met à jour les tailles des composants selon le mode plein écran
     */
    public void updateSizes(boolean fullScreen) {
        // Facteur de scaling
        float scale = fullScreen ? 1.3f : 1.0f;
        
        // Tailles de base (mêmes pour tous les boutons)
        int baseWidth = 180;
        int baseHeight = 50;
        int baseFontSize = 18;
        
        // Application du scaling
        int buttonWidth = (int)(baseWidth * scale);
        int buttonHeight = (int)(baseHeight * scale);
        int fontSize = (int)(baseFontSize * scale);
        
        // Mise à jour de tous les boutons
        if (btnMapTab != null) {
            btnMapTab.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
            btnMapTab.setFont(btnMapTab.getFont().deriveFont((float)fontSize));
        }
        if (btnTeamsTab != null) {
            btnTeamsTab.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
            btnTeamsTab.setFont(btnTeamsTab.getFont().deriveFont((float)fontSize));
        }
        if (btnOtherTab != null) {
            btnOtherTab.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
            btnOtherTab.setFont(btnOtherTab.getFont().deriveFont((float)fontSize));
        }
        if (btnBack != null) {
            btnBack.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
            btnBack.setFont(btnBack.getFont().deriveFont((float)fontSize));
        }
        
        // Forcer le rafraîchissement
        revalidate();
        repaint();
    }

    private void loadBackgroundImage() {
        try {
            java.net.URL url = getClass().getResource("/Reglage_BG.png");
            if (url != null) {
                backgroundImage = new ImageIcon(url).getImage();
            }
        } catch (Exception e) {
            System.err.println("Impossible de charger Reglage_BG.png: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            // Dessine l'image de fond en l'étirant pour remplir le panel
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Couleur de fond de secours
            g.setColor(new Color(55, 75, 75));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setOpaque(false);

        // Panel en haut avec bouton retour à gauche et onglets à droite
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Panel pour le contenu qui change selon l'onglet
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));
        add(contentPanel, BorderLayout.CENTER);

        // Création des panels pour chaque onglet
        mapPanel = createMapPanel();
        teamsPanel = createTeamsPanel();
        otherPanel = createOtherPanel();
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 0, 40));

        // Bouton retour à gauche
        btnBack = createButton("← Retour");
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(btnBack);
        panel.add(leftPanel, BorderLayout.WEST);

        // Onglets à droite avec espacement
        JPanel tabsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        tabsPanel.setOpaque(false);

        btnMapTab = createButton("Map");
        btnTeamsTab = createButton("Equipes");
        btnOtherTab = createButton("Autre");

        // Ajout des actions pour changer d'onglet
        btnMapTab.addActionListener(e -> showPanel("Map"));
        btnTeamsTab.addActionListener(e -> showPanel("Equipes"));
        btnOtherTab.addActionListener(e -> showPanel("Autre"));

        tabsPanel.add(btnMapTab);
        tabsPanel.add(btnTeamsTab);
        tabsPanel.add(btnOtherTab);
        
        panel.add(tabsPanel, BorderLayout.EAST);

        return panel;
    }

    private JButton createButton(String text) {
        PillButton button = new PillButton(text);
        button.setPreferredSize(new Dimension(180, 50));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setForeground(TEXT_COLOR);
        button.setBackground(TAB_INACTIVE);
        button.setHoverColor(TAB_HOVER);
        button.setFocusPainted(false);
        return button;
    }

    // ========== ONGLET MAP ==========
    private JPanel createMapPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        
        // Bouton test
        JButton testButton = createButton("Test");
        testButton.addActionListener(e -> System.out.println("Test appuié"));
        panel.add(testButton);
        
        return panel;
    }

    // ========== ONGLET EQUIPES ==========
    private JPanel createTeamsPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        
        // AJOUTEZ VOS COMPOSANTS ICI
        
        return panel;
    }

    // ========== ONGLET AUTRE ==========
    private JPanel createOtherPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        
        // AJOUTEZ VOS COMPOSANTS ICI
        
        return panel;
    }

    /**
     * Affiche le panel correspondant à l'onglet sélectionné
     */
    private void showPanel(String tabName) {
        currentTab = tabName;
        
        // Mise à jour des couleurs et hover des onglets
        updateTabButton(btnMapTab, tabName.equals("Map"));
        updateTabButton(btnTeamsTab, tabName.equals("Equipes"));
        updateTabButton(btnOtherTab, tabName.equals("Autre"));

        // Changement du contenu
        contentPanel.removeAll();
        switch (tabName) {
            case "Map":
                contentPanel.add(mapPanel, BorderLayout.CENTER);
                break;
            case "Equipes":
                contentPanel.add(teamsPanel, BorderLayout.CENTER);
                break;
            case "Autre":
                contentPanel.add(otherPanel, BorderLayout.CENTER);
                break;
        }
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Met à jour l'apparence d'un bouton onglet selon s'il est sélectionné ou non
     */
    private void updateTabButton(JButton button, boolean isSelected) {
        if (button instanceof PillButton) {
            PillButton pillButton = (PillButton) button;
            button.setBackground(isSelected ? TAB_ACTIVE : TAB_INACTIVE);
            // Pas de hover pour le bouton sélectionné
            pillButton.setHoverColor(isSelected ? TAB_ACTIVE : TAB_HOVER);
        }
    }

    private void setupLayout() {
        setPreferredSize(new Dimension(1280, 720));
    }

    // Getters
    public JButton getBtnMapTab() {
        return btnMapTab;
    }

    public JButton getBtnTeamsTab() {
        return btnTeamsTab;
    }

    public JButton getBtnOtherTab() {
        return btnOtherTab;
    }

    public JButton getBtnBack() {
        return btnBack;
    }

    public String getCurrentTab() {
        return currentTab;
    }
}

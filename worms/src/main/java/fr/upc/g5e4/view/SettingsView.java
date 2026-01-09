package fr.upc.g5e4.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.upc.g5e4.controller.MainMenuController;
import fr.upc.g5e4.model.SoundPlayer;
import fr.upc.g5e4.view.UI.*;
import fr.upc.g5e4.GameLauncher;

import fr.upc.g5e4.util.ImageCache;


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
    
    // Composants du panel Map
    private PillComboBox<String> sizeComboBox;
    private PillComboBox<String> terrainComboBox;
    private PillComboBox<String> styleComboBox;
    private JLabel mapPreviewLabel;
    private JPanel mapPreviewPanel;
    
    private Image backgroundImage;
    private String currentTab = "Map";

    private TeamSettingsView externalTeamsView; // pour recup la view de gestion des equipes 
    
    // Variables pour le scaling
    private boolean isFullScreen = false;

    public SettingsView(TeamSettingsView externalTeamsView) {
        this.externalTeamsView = externalTeamsView;
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

        if (externalTeamsView != null) {
            externalTeamsView.update(fullScreen);
        }
        
        // Mise à jour des combobox de la map
        int comboWidth = (int)(220 * scale);
        int comboHeight = (int)(50 * scale);
        
        if (sizeComboBox != null) {
            sizeComboBox.setPreferredSize(new Dimension(comboWidth, comboHeight));
            sizeComboBox.setMaximumSize(new Dimension(comboWidth, comboHeight));
            sizeComboBox.setFont(sizeComboBox.getFont().deriveFont((float)fontSize));
        }
        if (terrainComboBox != null) {
            terrainComboBox.setPreferredSize(new Dimension(comboWidth, comboHeight));
            terrainComboBox.setMaximumSize(new Dimension(comboWidth, comboHeight));
            terrainComboBox.setFont(terrainComboBox.getFont().deriveFont((float)fontSize));
        }
        if (styleComboBox != null) {
            styleComboBox.setPreferredSize(new Dimension(comboWidth, comboHeight));
            styleComboBox.setMaximumSize(new Dimension(comboWidth, comboHeight));
            styleComboBox.setFont(styleComboBox.getFont().deriveFont((float)fontSize));
        }
        
        // Mise à jour du panneau de prévisualisation
        if (mapPreviewPanel != null) {
            int previewWidth = (int)(500 * scale);
            int previewHeight = (int)(350 * scale);
            mapPreviewPanel.setPreferredSize(new Dimension(previewWidth, previewHeight));
            updateMapPreview(); // Recharger l'image avec la bonne taille
        }
        
        // Forcer le rafraîchissement
        revalidate();
        repaint();
    }

    private void loadBackgroundImage() {
        try {
            java.awt.image.BufferedImage img = ImageCache.get("/Reglage_BG.png");
            if (img != null) {
                backgroundImage = img;
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
        this.externalTeamsView.setFullScreen(isFullScreen);
        teamsPanel = this.externalTeamsView;
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
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 80, 100));
        panel.setOpaque(false);
        
        // Panel gauche pour la prévisualisation de la map
        mapPreviewPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                
                // Fond arrondi
                g2.setColor(new Color(41, 59, 45, 180));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 70, 70);
                g2.dispose();
            }
        };
        mapPreviewPanel.setOpaque(false);
        mapPreviewPanel.setPreferredSize(new Dimension(500, 350));
        mapPreviewPanel.setLayout(new BorderLayout());
        mapPreviewPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        mapPreviewLabel = new JLabel();
        mapPreviewLabel.setHorizontalAlignment(JLabel.CENTER);
        mapPreviewLabel.setVerticalAlignment(JLabel.CENTER);
        mapPreviewPanel.add(mapPreviewLabel, BorderLayout.CENTER);
        
        // Panel droite pour les options
        JPanel optionsPanel = new JPanel();
        optionsPanel.setOpaque(false);
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        
        // Options de taille
        String[] sizes = {"Petit", "Moyen", "Grand"};
        sizeComboBox = new PillComboBox<>(sizes);
        sizeComboBox.setSelectedItem("Moyen");
        sizeComboBox.setPreferredSize(new Dimension(220, 50));
        sizeComboBox.setMaximumSize(new Dimension(220, 50));
        
        // Options de terrain
        String[] terrains = {"Standard", "Plat", "Montagnes", "Iles"};
        terrainComboBox = new PillComboBox<>(terrains);
        terrainComboBox.setSelectedItem("Standard");
        terrainComboBox.setPreferredSize(new Dimension(220, 50));
        terrainComboBox.setMaximumSize(new Dimension(220, 50));
        
        // Options de style de fond
        String[] styles = {"Jour", "Nuit"};
        styleComboBox = new PillComboBox<>(styles);
        styleComboBox.setSelectedItem("Jour");
        styleComboBox.setPreferredSize(new Dimension(220, 50));
        styleComboBox.setMaximumSize(new Dimension(220, 50));
        
        // Ajout des labels et combos avec espacement
        optionsPanel.add(createOptionGroup("Taille", sizeComboBox));
        optionsPanel.add(Box.createVerticalStrut(20));
        optionsPanel.add(createOptionGroup("Terrain", terrainComboBox));
        optionsPanel.add(Box.createVerticalStrut(20));
        optionsPanel.add(createOptionGroup("Style", styleComboBox));
        
        panel.add(mapPreviewPanel);
        panel.add(optionsPanel);
        
        // Charger l'image initiale
        updateMapPreview();
        
        return panel;
    }
    
    /**
     * Crée un groupe label + combobox pour les options de la map
     */
    private JPanel createOptionGroup(String labelText, PillComboBox<?> comboBox) {
        JPanel group = new JPanel();
        group.setOpaque(false);
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel label = new JLabel(labelText);
        label.setForeground(TEXT_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        try {
            Font baseFont = Font.createFont(Font.TRUETYPE_FONT,
                getClass().getResourceAsStream("/GoogleSans-Bold.ttf"));
            label.setFont(baseFont.deriveFont(16f));
        } catch (Exception e) {
            label.setFont(new Font("Arial", Font.BOLD, 16));
        }
        
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        group.add(label);
        group.add(Box.createVerticalStrut(8));
        group.add(comboBox);
        
        return group;
    }
    
    /**
     * Met à jour l'image de prévisualisation de la map selon les options sélectionnées
     */
    public void updateMapPreview() {
        String size = (String) sizeComboBox.getSelectedItem();
        String terrain = (String) terrainComboBox.getSelectedItem();
        
        // Convertir les noms en anglais pour le nom de fichier
        String sizeEng = switch (size) {
            case "Petit" -> "Small";
            case "Moyen" -> "Medium";
            case "Grand" -> "Large";
            default -> "Medium";
        };
        
        String terrainEng = switch (terrain) {
            case "Standard" -> "standard";
            case "Plat" -> "flat";
            case "Montagnes" -> "mountains";
            case "Iles" -> "islands";
            default -> "standard";
        };
        
        // Construire le chemin de l'image
        String imagePath = "/Map_shots/" + sizeEng + "_" + terrainEng + ".png";
        
        try {
            java.awt.image.BufferedImage originalImg = ImageCache.get(imagePath);
            if (originalImg != null) {
                // Récupérer les dimensions de l'image originale
                int originalWidth = originalImg.getWidth();
                int originalHeight = originalImg.getHeight();
                
                // Crop le haut de l'image (enlever la barre macOS)
                int cropTop = 55;
                int croppedHeight = originalHeight - cropTop;
                
                // Créer une image croppée
                java.awt.image.BufferedImage croppedImg = new java.awt.image.BufferedImage(
                    originalWidth, croppedHeight, java.awt.image.BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = croppedImg.createGraphics();
                g2d.drawImage(originalImg, 0, 0, originalWidth, croppedHeight,
                              0, cropTop, originalWidth, originalHeight, null);
                g2d.dispose();
                
                // Redimensionner l'image croppée pour qu'elle tienne dans le panel
                int imgWidth = mapPreviewPanel.getPreferredSize().width - 30;
                int imgHeight = mapPreviewPanel.getPreferredSize().height - 30;
                Image scaledImg = croppedImg.getScaledInstance(imgWidth, imgHeight, Image.SCALE_SMOOTH);
                
                // Appliquer des coins arrondis à l'image
                java.awt.image.BufferedImage finalImg = new java.awt.image.BufferedImage(
                    imgWidth, imgHeight, java.awt.image.BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = finalImg.createGraphics();
                
                // Fix 1 Antialiasing pour des bords lisses
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fix 2 coins lisses (enfin)
                int cornerRadius = 50;
                java.awt.geom.RoundRectangle2D roundedRect = new java.awt.geom.RoundRectangle2D.Float(0, 0, imgWidth, imgHeight, cornerRadius, cornerRadius);
                // Dessiner le masque arrondi puis l'image par-dessus
                g2.setComposite(java.awt.AlphaComposite.Src);
                g2.fill(roundedRect);
                g2.setComposite(java.awt.AlphaComposite.SrcAtop);
                g2.drawImage(scaledImg, 0, 0, imgWidth, imgHeight, null);
                g2.dispose();
                
                mapPreviewLabel.setIcon(new ImageIcon(finalImg));
                mapPreviewLabel.setText(null);
            } else {
                mapPreviewLabel.setIcon(null);
                mapPreviewLabel.setText("Aperçu non disponible");
                mapPreviewLabel.setForeground(TEXT_COLOR);
            }
        } catch (Exception e) {
            mapPreviewLabel.setIcon(null);
            mapPreviewLabel.setText("Erreur de chargement");
            mapPreviewLabel.setForeground(TEXT_COLOR);
        }
    }

    // ========== ONGLET EQUIPES ==========
    /*
    private JPanel createTeamsPanel() {
        //this.teamsPanel.setLayout(new FlowLayout());
        this.teamsPanel.add(this.externalTeamsView);

        this.teamsPanel.revalidate();
        this.teamsPanel.repaint();

    }
    */

    public void setTeamSettingsView(TeamSettingsView teamSettingsView){
        this.externalTeamsView = teamSettingsView;
    }

    // ========== ONGLET AUTRE ==========

    private PillButton btnSound;
    private PillButton btnResetSave;
    private PillButton btnFullscreen;

    private JPanel createOtherPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        
        boolean soundEnabled = !SoundPlayer.isMuted();
        btnSound = (PillButton) createButton(soundEnabled ? "Son : ON" : "Son : OFF");
        btnResetSave = (PillButton) createButton("Reset Sauvegarde");
        btnFullscreen = (PillButton) createButton("Fullscreen : OFF");


        btnSound.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnResetSave.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnFullscreen.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalGlue());
        panel.add(btnSound);
        panel.add(Box.createVerticalStrut(25));
        panel.add(btnResetSave);
        panel.add(Box.createVerticalStrut(25));
        panel.add(btnFullscreen);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    public void setSoundButtonState(boolean enabled) {
        if (btnSound != null) btnSound.setText(enabled ? "Son : ON" : "Son : OFF");
    }

    public void setFullscreenButtonState(boolean enabled) {
        if (btnFullscreen != null) btnFullscreen.setText(enabled ? "Fullscreen : ON" : "Fullscreen : OFF");
    }

    public JButton getBtnSound(){return btnSound;}
    public JButton getBtnResetSave(){return btnResetSave;}
    public JButton getBtnFullscreen(){return btnFullscreen;}

    /**
     * Affiche le panel correspondant à l'onglet sélectionné
     */
    public void showPanel(String tabName) {
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
    
    // Getters pour les options de map
    public PillComboBox<String> getSizeComboBox() {
        return sizeComboBox;
    }
    
    public PillComboBox<String> getTerrainComboBox() {
        return terrainComboBox;
    }
    
    public PillComboBox<String> getStyleComboBox() {
        return styleComboBox;
    }
    
    public String getSelectedSize() {
        return (String) sizeComboBox.getSelectedItem();
    }
    
    public String getSelectedTerrain() {
        return (String) terrainComboBox.getSelectedItem();
    }
    // ------------------------------------------
    
}

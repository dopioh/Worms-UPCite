package fr.upc.g5e4.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Overlay de tutoriel avec pagination
 */
public class TutorialOverlay extends JPanel {
    private static final Color OVERLAY_BG = new Color(0, 0, 0, 200);
    private static final Color PANEL_BG = new Color(40, 40, 50);
    private static final Color TEXT_COLOR = new Color(230, 230, 230);
    private static final Color BUTTON_BG = new Color(66, 139, 82);
    private static final Color BUTTON_HOVER = new Color(100, 150, 200);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 28);
    private static final Font TEXT_FONT = new Font("Arial", Font.PLAIN, 16);
    
    private int currentPage = 0;
    private final String[][] tutorialPages;
    
    private JButton continueButton;
    private JButton backButton;
    private JButton closeButton;
    
    private Runnable onClose;
    
    /**
     * Constructeur
     */
    public TutorialOverlay() {
        setOpaque(false);
        setLayout(null);
        
        // bloque les clics sous l'overlay (pour ne pas cliquer sur les boutons du menu)
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                e.consume();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                e.consume();
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                e.consume();
            }
        });
        
        // les pages du tutoriel
        tutorialPages = new String[][] {
            {
                "COMMENT JOUER ?",
                "Worm est un jeu de stratégie au tour par tour. Chaque joueur contrôle une équipe de",
                "petits vers de terre. L'objectif est d'éliminer tous les vers adverses en utilisant ses",
                "propres vers et une multitude d'armes et d'objets spéciaux !",
                "",
                "Le jeu se déroule en tour de 30 secondes dans lesquels un joueur contrôle un worm.",
                "Durant un tour, le worm peut choisir une arme pour tirer ainsi que 2 objets spéciaux.",
                "",
                "Les touches clavier :",
                "Q et D : pour déplacer le worm à gauche et à droite",
                "Z : pour sauter",
            },
            {
                "INFORMATIONS IMPORTANTES",
                "• Attention terrain destructible ! Plus la case de terrain est claire plus elle est fragile",
                "• Un worm ne peut jouer qu'une seule arme et deux objets maximum par tour",
                "• Le worm peut cumuler deux mêmes objets, les effets seront doublés mais la durée",
                "ne sera pas augmentée",
                "• Les munitions ne sont pas illimitées et chaque arme a un nombre de munition limité à",
                "chaque tour (EX : Bazooka a 2 munitions par equipe mais seulement 1 munition par tour)",
                "• Tomber dans l'eau trop profondement entraîne la mort immédiate du worm",
                "• Chaque equipe en vie gagne 5$ à la fin de son tour",
                "• Tomber dans l'eau trop profondement entraîne la mort immédiate du worm",
                
            },
            {
                "Arsenal et objets spéciaux",
                "• Bazooka : Arme de base, inflige de gros dégâts aux worms et au terrain",
                "• Grenade : Explose, inflige des dégâts et a une courte portée",
                "• Ak47 : Fait plusieurs petits tirs rapides",
                "• Bat de baseball : Arme de mêlée, inflige des dégâts modérés au corps à corps",
                "",
                "",
                "• Potion PV : Restaure des points de vie sur la durée",
                "• Potion Bouclier : Réduit les dégâts subis pendant une durée limitée",
                "• Potion Vitesse : Augmente la vitesse de déplacement pendant une durée limitée",
                "• Potion Saut : Augmente la hauteur de saut pendant une durée limitée",
            },
            {
                "CREDITS",
                "Ce jeu a été développé par le groupe G5E4 dont les membres sont : ",
                "• MILITON Mei",
                "• MAZRI Nassim",
                "• WU Billy",
                "• DESROQUES Ian",
                "• SHAN Hugo",
                "• ROSE Raphael",
            }
        };
        
        createButtons();
        updateButtonStates();
        
        // ecouteur de redimensionnement pour repositionner les boutons
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                repositionComponents();
            }
        });
    }
    
    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }
    
    private void createButtons() {
        // bouton Continuer
        continueButton = createPageButton("Continuer");
        continueButton.addActionListener(e -> {
            if (currentPage < tutorialPages.length - 1) {
                currentPage++;
                updateButtonStates();
                repaint();
            }
        });
        add(continueButton);
        
        // bouton Retour
        backButton = createPageButton("Retour");
        backButton.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                updateButtonStates();
                repaint();
            }
        });
        add(backButton);
        
        // bouton Fermer
        closeButton = createPageButton("Fermer");
        closeButton.addActionListener(e -> closeTutorial());
        add(closeButton);
    }
    
    private JButton createPageButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(BUTTON_BG);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private void updateButtonStates() {
        backButton.setEnabled(currentPage > 0);
        continueButton.setEnabled(currentPage < tutorialPages.length - 1);
        
        // changement couleur pour bouton desac
        backButton.setBackground(backButton.isEnabled() ? BUTTON_BG : new Color(50, 50, 60));
        continueButton.setBackground(continueButton.isEnabled() ? BUTTON_BG : new Color(50, 50, 60));
    }
    
    /**
     * scale les composants en fonction de la taille de l'écran grace aux getWidth/getHeight
     */
    private void repositionComponents() {
        int width = getWidth();
        int height = getHeight();
        
        int panelWidth = Math.min(700, width - 100);
        int panelHeight = Math.min(500, height - 100);
        int panelX = (width - panelWidth) / 2;
        int panelY = (height - panelHeight) / 2;
        
        int buttonWidth = 120;
        int buttonHeight = 40;
        int buttonMargin = 20;
        
        // Position des boutons RELATIFS au panneau central
        // En bas à droite du panneau
        int rightX = panelX + panelWidth - buttonWidth - buttonMargin;
        int bottomY = panelY + panelHeight - buttonHeight - buttonMargin;
        
        continueButton.setBounds(rightX, bottomY, buttonWidth, buttonHeight);
        backButton.setBounds(rightX - buttonWidth - 10, bottomY, buttonWidth, buttonHeight);
        
        // Bouton fermer en bas à gauche du panneau
        closeButton.setBounds(panelX + buttonMargin, bottomY, buttonWidth, buttonHeight);
    }
    
    private void closeTutorial() {
        // reset de la page au cas ou on revidendrait dans le tutoriel
        currentPage = 0;
        updateButtonStates();
        if (onClose != null) {
            onClose.run();
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Antialiasing pour un rendu lisse
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        // assombrir l'arrière plan
        g2d.setColor(OVERLAY_BG);
        g2d.fillRect(0, 0, width, height);
        
        // rectangle central pour le contenu
        int panelWidth = Math.min(700, width - 100);
        int panelHeight = Math.min(500, height - 100);
        int panelX = (width - panelWidth) / 2;
        int panelY = (height - panelHeight) / 2;
        
        // rectangle bords arrondis
        g2d.setColor(PANEL_BG);
        g2d.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);
        
        // border arrondi
        g2d.setColor(new Color(66, 139, 82));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);

        drawCurrentPage(g2d, panelX, panelY, panelWidth, panelHeight);
        
        // l'index de pagination
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        String pageIndicator = "Page " + (currentPage + 1) + " / " + tutorialPages.length;
        FontMetrics fm = g2d.getFontMetrics();
        int indicatorWidth = fm.stringWidth(pageIndicator);
        g2d.drawString(pageIndicator, panelX + panelWidth - indicatorWidth - 20, panelY + panelHeight - 80);
        
        g2d.dispose();
    }
    
    /**
     * dessiner le contenu de la page actuelle
     */
    private void drawCurrentPage(Graphics2D g2d, int x, int y, int width, int height) {
        String[] pageContent = tutorialPages[currentPage];
        
        // title
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(TITLE_FONT);
        FontMetrics titleFm = g2d.getFontMetrics();
        int titleWidth = titleFm.stringWidth(pageContent[0]);
        g2d.drawString(pageContent[0], x + (width - titleWidth) / 2, y + 60);
        
        // ligne separation
        g2d.setColor(new Color(66, 139, 82));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(x + 40, y + 80, x + width - 40, y + 80);
        
        // contenu du la page
        g2d.setFont(TEXT_FONT);
        g2d.setColor(TEXT_COLOR);
        int lineY = y + 120;
        int lineHeight = 30;
        
        for (int i = 1; i < pageContent.length; i++) {
            g2d.drawString(pageContent[i], x + 50, lineY);
            lineY += lineHeight;
        }
    }
}

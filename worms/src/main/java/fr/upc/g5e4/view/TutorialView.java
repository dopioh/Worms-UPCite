package fr.upc.g5e4.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import fr.upc.g5e4.model.TutorialModel;

/**
 * View pour le tutoriel
 */
public class TutorialView extends JPanel {
    
    private static final Color OVERLAY_BG = new Color(0, 0, 0, 200);
    private static final Color PANEL_BG = new Color(40, 40, 50);
    private static final Color TEXT_COLOR = new Color(230, 230, 230);
    private static final Color BUTTON_BG = new Color(66, 139, 82);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 28);
    private static final Font TEXT_FONT = new Font("Arial", Font.PLAIN, 16);
    
    private TutorialModel model;
    
    private JButton continueButton;
    private JButton backButton;
    private JButton closeButton;
    
    public TutorialView(TutorialModel model) {
        this.model = model;
        model.setView(this);
        
        setOpaque(false);
        setLayout(null);
        
        // bloque les clics sous l'overlay
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
        
        createButtons();
    }
    
    private void createButtons() {
        continueButton = createPageButton("Continuer");
        add(continueButton);
        
        backButton = createPageButton("Retour");
        add(backButton);

        closeButton = createPageButton("Fermer");
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
    
    // update l'état des boutons (activé/désactivé)
    public void updateButtonStates() {
        backButton.setEnabled(model.hasPreviousPage());
        continueButton.setEnabled(model.hasNextPage());
        
        // changement de couleur en fonction de l'état
        backButton.setBackground(backButton.isEnabled() ? BUTTON_BG : new Color(50, 50, 60));
        continueButton.setBackground(continueButton.isEnabled() ? BUTTON_BG : new Color(50, 50, 60));
    }
    
    // config positionnement des boutons
    // on peut le faire directement dans paintComponent mais c'est plus propre comme ça
    private void buttonPosition() {
        int width = getWidth();
        int height = getHeight();
        
        int panelWidth = Math.min(700, width - 100);
        int panelHeight = Math.min(500, height - 100);
        int panelX = (width - panelWidth) / 2;
        int panelY = (height - panelHeight) / 2;
        
        int buttonWidth = 120;
        int buttonHeight = 40;
        int buttonMargin = 20;
        
        int rightX = panelX + panelWidth - buttonWidth - buttonMargin;
        int bottomY = panelY + panelHeight - buttonHeight - buttonMargin;
        
        // redim des boutons de controle de la pagination et de fermeture
        continueButton.setBounds(rightX, bottomY, buttonWidth, buttonHeight);
        backButton.setBounds(rightX - buttonWidth - 10, bottomY, buttonWidth, buttonHeight);
        closeButton.setBounds(panelX + buttonMargin, bottomY, buttonWidth, buttonHeight);
    }
    
    /**
     * dessiner le contenu de la page actuelle
     */
    private void drawCurrentPage(Graphics2D g2d, int x, int y, int width, int height) {
        String[] pageContent = model.getCurrentPageContent();
        
        // titre page
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(TITLE_FONT);
        FontMetrics titleFm = g2d.getFontMetrics();
        int titleWidth = titleFm.stringWidth(pageContent[0]);
        g2d.drawString(pageContent[0], x + (width - titleWidth) / 2, y + 60);
        
        // separation
        g2d.setColor(new Color(66, 139, 82));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(x + 40, y + 80, x + width - 40, y + 80);
        
        // contenu page
        g2d.setFont(TEXT_FONT);
        g2d.setColor(TEXT_COLOR);
        int lineY = y + 120;
        int lineHeight = 30;
        
        for (int i = 1; i < pageContent.length; i++) {
            g2d.drawString(pageContent[i], x + 50, lineY);
            lineY += lineHeight;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        
        int width = getWidth();
        int height = getHeight();
        
        g2d.setColor(OVERLAY_BG);
        g2d.fillRect(0, 0, width, height);
        
        int panelWidth = Math.min(700, width - 100);
        int panelHeight = Math.min(500, height - 100);
        int panelX = (width - panelWidth) / 2;
        int panelY = (height - panelHeight) / 2;
        
        buttonPosition();
        updateButtonStates();
        
        g2d.setColor(PANEL_BG);
        g2d.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);
        
        g2d.setColor(new Color(66, 139, 82));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);

        drawCurrentPage(g2d, panelX, panelY, panelWidth, panelHeight);
        
        // index de pagination
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        String pageIndicator = "Page " + (model.getCurrentPage() + 1) + " / " + model.getTotalPages();
        FontMetrics fm = g2d.getFontMetrics();
        int indicatorWidth = fm.stringWidth(pageIndicator);
        g2d.drawString(pageIndicator, panelX + panelWidth - indicatorWidth - 20, panelY + panelHeight - 80);
        
        g2d.dispose();
    }
    
    public JButton getContinueButton() {
        return continueButton;
    }
    
    public JButton getBackButton() {
        return backButton;
    }
    
    public JButton getCloseButton() {
        return closeButton;
    }
}

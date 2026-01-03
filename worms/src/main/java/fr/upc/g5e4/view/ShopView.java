package fr.upc.g5e4.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import fr.upc.g5e4.view.UI.*;

/**
 * Vue de la boutique du jeu (version compacte pour le menu)
 * Affiche les items disponibles à l'achat avec leurs descriptions
 * bien repositionné la boutique en fulscreen (todo)
 */
public class ShopView extends JPanel {
    
    private static final Color SHOP_BG = new Color(0, 0, 0, 170);
    private static final Color ITEM_BG = new Color(255, 255, 255);
    private static final Color ITEM_SELECTED_BG = new Color(200, 230, 201);
    private static final Color BORDER_COLOR = new Color(255, 0, 0, 0);
    private static final Color BUTTON_BUY = new Color(76, 175, 80);
    private static final Color BUTTON_BUY_HOVER = new Color(56, 142, 60);
    
    private JPanel itemsGridPanel;
    private JPanel detailPanel;
    private JButton buyButton;
    private RoundedPanel selectedCard = null;
    
    public ShopView() {
        initComponents();
        setupLayout();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        setBackground(SHOP_BG);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setOpaque(false);
        
        // Titre de la boutique
        JLabel titleLabel = new JLabel("BOUTIQUE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(255, 208, 0));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // Panel central pour les items
        itemsGridPanel = new JPanel();
        itemsGridPanel.setLayout(new GridLayout(0, 2, 5, 5)); // 2 colonnes
        itemsGridPanel.setOpaque(false);
        
        JScrollPane scrollPane = new JScrollPane(itemsGridPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel de détails en bas
        detailPanel = createLargeBuyButton();
        add(detailPanel, BorderLayout.SOUTH);
        
        // Ajouter des items d'exemple
        addExampleItems();
    }
    
    private JPanel createLargeBuyButton() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 80));
        
        buyButton = new RoundedButton("ACHETER", 15);
        buyButton.setFont(new Font("Arial", Font.BOLD, 18));
        buyButton.setBackground(BUTTON_BUY);
        buyButton.setForeground(Color.WHITE);
        ((RoundedButton)buyButton).setHoverColor(BUTTON_BUY_HOVER);
        buyButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buyButton.setEnabled(false);
        buyButton.addActionListener(e -> deselectItem());
        panel.add(buyButton, BorderLayout.CENTER);

        return panel;
    }
    
    public void addItemCard(String itemName, String iconPath, String description, int price) {
        RoundedPanel card = new RoundedPanel(10);
        card.setLayout(new BorderLayout(3, 3));
        card.setBackground(ITEM_BG);
        card.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // on charge l'icone de l'item
        JLabel iconLabel = new JLabel();
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);
        iconLabel.setPreferredSize(new Dimension(50, 50));
        iconLabel.setOpaque(false);
        
        ImageIcon icon = loadImage(iconPath);
        if (icon != null) {
            Image scaledImage = icon.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            iconLabel.setText("?");
            iconLabel.setFont(new Font("Arial", Font.BOLD, 25));
        }
        
        card.add(iconLabel, BorderLayout.CENTER);
        
        // affichage nom & prix
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(itemName, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 19));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel itemPriceLabel = new JLabel(price + " $", SwingConstants.CENTER);
        itemPriceLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        itemPriceLabel.setForeground(new Color(184, 134, 11));
        itemPriceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        infoPanel.add(nameLabel);
        infoPanel.add(itemPriceLabel);
        card.add(infoPanel, BorderLayout.SOUTH);
        
        // event click sur la carte d'un item
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                selectItem(card, itemName, iconPath, description, price);
            }
        });
        
        itemsGridPanel.add(card);
    }
    
    /**
     * cliquer sur un item = background de carte qui change + bouton acheter cliquable
     */
    private void selectItem(RoundedPanel card, String name, String iconPath, String description, int price) {
        // désélectionner l'ancien item
        if (selectedCard != null) {
            selectedCard.setBackground(ITEM_BG);
            selectedCard.repaint();
        }
        // sélectionner le nouvel item
        selectedCard = card;
        card.setBackground(ITEM_SELECTED_BG);
        card.repaint();
        // Activer le bouton d'achat
        buyButton.setEnabled(true);

    }

    /**
     * cliquer sur un item deja selectionne = désélectionner l'item + bouton acheter désactivé
     */
    private void deselectItem() {
        if (selectedCard != null) {
            selectedCard.setBackground(ITEM_BG);
            selectedCard.repaint();
            selectedCard = null;
        }
        buyButton.setEnabled(false);
    }
    
    private ImageIcon loadImage(String path) {
        java.net.URL url = getClass().getResource(path);
        return (url != null) ? new ImageIcon(url) : null;
    }
    
    private void setupLayout() {
        setPreferredSize(new Dimension(350, 500));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(SHOP_BG);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        
        g2d.setColor(BORDER_COLOR);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
        
        g2d.dispose();
    }
    
    /**
     * Ajoute des items d'exemple pour la démonstration
     */
    private void addExampleItems() {
        addItemCard("Potion PV", "/gadgets/potion_heal.png", 
            "une potion qui restaure une grande quantité de points de vie.",
            50);
        
        addItemCard("Potion SHIELD", "/gadgets/potion_shield.png",
            "une potion qui restaure une grande quantité de points de bouclier.",
            70);
        
        addItemCard("Grenade", "/gadgets/grenade.png",
            "un petit objet qui fait un grand boom.",
            150);
        
        addItemCard("Bumper", "/gadgets/bumper.png",
            "objet qui fait rebondir les worms.",
            100);
        
        addItemCard("GIGAWORMM", "/GIGAWORMM.png",
            "skin",
            250);

        addItemCard("GIGAWORMM", "/worm.png",
            "skin",
            500);
    }
    
    // Getter
    public JButton getBuyButton() {
        return buyButton;
    }
}

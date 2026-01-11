package fr.upc.g5e4.view.UI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;


/**
 * COMMENT UTILISER:
 * - PillComboBox<String> combo = new PillComboBox<>(new String[]{"Option1", "Option2"});
 * - combo.addActionListener(e -> System.out.println("Sélection: " + combo.getSelectedItem()));
 * 
 * EXEMPLE:
 * String[] options = {"Petit", "Moyen", "Grand"};
 * PillComboBox<String> sizeCombo = new PillComboBox<>(options);
 * sizeCombo.setPreferredSize(new Dimension(200, 50));
 * panel.add(sizeCombo);
 */
public class PillComboBox<E> extends JComboBox<E> {
    
    private static final Color BG_COLOR = new Color(66, 139, 82);
    private static final Color TEXT_COLOR = Color.WHITE;
    
    private Font googleSansFont;

    public PillComboBox(E[] items) {
        super(items);
        loadFont();
        setupComboBox();
    }

    private void loadFont() {
        try {
            Font baseFont = Font.createFont(Font.TRUETYPE_FONT,
                getClass().getResourceAsStream("/GoogleSans-Bold.ttf"));
            googleSansFont = baseFont.deriveFont(18f);
            setFont(googleSansFont);
        } catch (Exception e) {
            System.err.println("Erreur chargement police Google Sans: " + e.getMessage());
            googleSansFont = new Font("Arial", Font.BOLD, 18);
            setFont(googleSansFont);
        }
    }

    private void setupComboBox() {
        setOpaque(false);
        setBackground(BG_COLOR);
        setForeground(TEXT_COLOR);
        setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFocusable(false);
        
        // comboBox custom pour le look pilule
        setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton() {
                    @Override
                    public void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                          RenderingHints.VALUE_ANTIALIAS_ON);
                        // Dessine la flèche
                        g2.setColor(TEXT_COLOR);
                        int w = getWidth();
                        int h = getHeight();
                        int[] xPoints = {w/2 - 6, w/2 + 6, w/2};
                        int[] yPoints = {h/2 - 3, h/2 - 3, h/2 + 4};
                        g2.fillPolygon(xPoints, yPoints, 3);
                        
                        g2.dispose();
                    }
                };
                button.setOpaque(false);
                button.setContentAreaFilled(false);
                button.setBorderPainted(false);
                button.setFocusPainted(false);
                button.setPreferredSize(new Dimension(30, 30));
                return button;
            }
            
            @Override
            protected ComboPopup createPopup() {
                BasicComboPopup popup = new BasicComboPopup(comboBox) {
                    @Override
                    protected void configurePopup() {
                        super.configurePopup();
                        setBorder(BorderFactory.createLineBorder(BG_COLOR, 2));
                        setBackground(new Color(41, 59, 45));
                    }
                };
                return popup;
            }
        });
        
        // Custom renderer pour styliser les items dans la liste déroulante
        // Sans cela, les items auraient l'apparence standard de Swing
        setRenderer(new ListCellRenderer<E>() {
            private final DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
            
            @Override
            public Component getListCellRendererComponent(JList<? extends E> list, E value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                
                // Récupérer le label de base créé par le renderer par defaut
                JLabel label = (JLabel) defaultRenderer.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
                
                // Appliquer style personnalisé
                label.setOpaque(true);
                // Utiliser le font du combobox lui-même pour s'adapter au scaling
                label.setFont(getFont());  
                label.setForeground(TEXT_COLOR);
                label.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));  // Espacement interne
                
                // Couleur de fond selon si l'item est sélectionné/survolé ou non
                if (isSelected) {
                    label.setBackground(BG_COLOR);  // Vert pour l'item sélectionné
                } else {
                    label.setBackground(new Color(41, 59, 45));  // Gris-vert foncé pour les autres
                }
                
                return label;
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                          RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Dessine le fond en forme de pilule
        g2.setColor(BG_COLOR);
        g2.fillRoundRect(0, 0, width, height, height, height);

        g2.dispose();

        // Dessine le contenu
        super.paintComponent(g);
    }

    @Override
    public void paintBorder(Graphics g) {
        // Pas de bordure
    }
}

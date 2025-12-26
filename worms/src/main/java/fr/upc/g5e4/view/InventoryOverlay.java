package fr.upc.g5e4.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import fr.upc.g5e4.model.InventoryModel.InventoryJson;
import fr.upc.g5e4.controller.SwingInputController;
import fr.upc.g5e4.model.GameManager;

public class InventoryOverlay extends JComponent {
    private enum Category {
        ARMES, GADGET, CRAFT
    }

    private Category currentCategory = Category.ARMES;

    private List<InventoryJson> inventory;
    private boolean visible = false;
    private MouseAdapter fireListener; 
    private List<InventoryJson> lastFiltered = new ArrayList<>();


    // icône du sac
    private BufferedImage icon;
    private Rectangle iconBounds;

    // liste des items
    private final List<Rectangle> itemBounds = new ArrayList<>();
    private int selectedIndex = -1;
    private InventoryJson equippedItem = null;

    // zone du panneau inventaire
    private Rectangle panelBounds;

    private GameManager gameManager;

    // --- Grille INVENTAIRE ---
    private static final int CELL_SIZE = 96;   // taille des cases
    private static final int CELL_GAP  = 16;   // espace entre cases
    private static final int GRID_COLS = 3;    // plus de colonnes

    private Rectangle armesTab;
    private Rectangle gadgetTab;
    private Rectangle craftTab;

    
    //Icon Armes/Gadget
    private final java.util.Map<String, BufferedImage> itemImages = new java.util.HashMap<>();
    private void loadItemImages() {
        loadImage("AK47", "/ak.png");
        loadImage("Grenade", "/grenade.png");
        loadImage("Bazooka", "/bazooka.png");
        loadImage("BaseballBat", "/baseballbat.png");
        //loadImage("Bumper", "/"); Pas encore fait
    }

    private void loadImage(String name, String path) {
        try {
            itemImages.put(
                name.toLowerCase(),
                javax.imageio.ImageIO.read(getClass().getResource(path))
            );
        } catch (Exception e) {
            System.err.println("Image manquante : " + path);
        }
    }

    // --- HOVER ---
    private Category hoveredTab = null;   // onglet survolé
    private int hoveredIndex = -1;        // case survolée (index dans lastFiltered)

    // --- DESCRIPTIONS ---
    private final java.util.Map<String, String> itemDescriptions = new java.util.HashMap<>();

    private void loadDescriptions() {
        itemDescriptions.put("ak47", "rafales rapides, bonne portée");
        itemDescriptions.put("grenade", "Boom! Gros dégâts de zone");
        itemDescriptions.put("bazooka", "Gros dégâts");
        itemDescriptions.put("baseballbat", "Cogne sauvagement tes ennemis");
        itemDescriptions.put("bumper", "Rebondis");
    }


    public InventoryOverlay(List<InventoryJson> inventory) {
        this.inventory = inventory;
        setOpaque(false);
        loadItemImages(); //Pour les icones des armes/gadgets
        try {
            icon = javax.imageio.ImageIO.read(
                getClass().getResource("/backpack.png")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        loadDescriptions();
        ToolTipManager.sharedInstance().registerComponent(this); // optionnel (tooltips Swing)

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });

        // HOVER (survol)
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!visible) return;

                computeLayout();
                int x = e.getX();
                int y = e.getY();

                Category newHoveredTab = null;
                int newHoveredIndex = -1;

                // Tabs
                if (armesTab != null && armesTab.contains(x, y)) newHoveredTab = Category.ARMES;
                else if (gadgetTab != null && gadgetTab.contains(x, y)) newHoveredTab = Category.GADGET;
                else if (craftTab != null && craftTab.contains(x, y)) newHoveredTab = Category.CRAFT;

                // Cases
                if (newHoveredTab == null) {
                    for (int i = 0; i < itemBounds.size(); i++) {
                        if (itemBounds.get(i).contains(x, y)) {
                            newHoveredIndex = i;
                            break;
                        }
                    }
                }

                boolean changed = (newHoveredTab != hoveredTab) || (newHoveredIndex != hoveredIndex);
                hoveredTab = newHoveredTab;
                hoveredIndex = newHoveredIndex;

                if (changed) repaint();
            }
        });


    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * FireListener basé sur le SwingInputController (tir vers la souris)
     */
    public void setFireListener(SwingInputController inputController){
        this.fireListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    inputController.handleFireAt(e.getX(), e.getY());
                }
            }
        };
    }

    public void setFireListener(MouseAdapter listener){
        this.fireListener = listener;
    }

    
    private void handleClick(int x, int y) {
        // inventaire fermé -> clic = tir normal
        if (!visible) {
            if (fireListener != null) {
                fireListener.mousePressed(new MouseEvent(
                    this, MouseEvent.MOUSE_PRESSED,
                    System.currentTimeMillis(), 0,
                    x, y, 1, false, MouseEvent.BUTTON1
                ));
            }
            return;
        }
        
        computeLayout();

        // === CLIC SUR CATEGORIES ===
        if (armesTab != null && armesTab.contains(x, y)) {
            currentCategory = Category.ARMES;
            selectedIndex = -1;
            computeLayout();   // regen lastFiltered + itemBounds
            repaint();
            return;
        }

        if (gadgetTab != null && gadgetTab.contains(x, y)) {
            currentCategory = Category.GADGET;
            selectedIndex = -1;
            computeLayout();
            repaint();
            return;
        }

        if (craftTab != null && craftTab.contains(x, y)) {
            currentCategory = Category.CRAFT;
            selectedIndex = -1;
            computeLayout();
            repaint();
            return;
        }


        // inventaire ouvert
        // clic dans le panneau -> sélection / équipement
        if (panelBounds != null && panelBounds.contains(x, y)) {
            for (int i = 0; i < itemBounds.size(); i++) {
                Rectangle r = itemBounds.get(i);
                if (r.contains(x, y)) {
                    selectedIndex = i;
                    equippedItem = lastFiltered.get(i);

                    if (gameManager != null) {
                        gameManager.setWeaponFromInventory(equippedItem.name);
                    }

                    System.out.println("[Inventaire] " +
                        (gameManager != null ? gameManager.getCurrentTeamName() : "?") +
                        " équipe " + equippedItem.name);

                    repaint();
                    return;
                }
            }
            return; // clic dans le panneau mais pas sur un item -> rien
        }

        // clic en dehors du panneau -> tir avec l'item équipé
        if (equippedItem == null) {
            System.out.println("[Inventaire] Aucun objet équipé, tir ignoré.");
            return;
        }

        boolean ok = equippedItem.registerUseThisTurn();
        if (!ok) {
            System.out.println("[Inventaire] Plus d'utilisation possible pour " + equippedItem.name);
            return;
        }

        int restantesTour = equippedItem.getRemainingUseThisTurn();
        if (equippedItem.remainingUses == null) {
            System.out.println("[" +
                (gameManager != null ? gameManager.getCurrentTeamName() : "?") +
                "] tire avec " + equippedItem.name +
                " → " + restantesTour + " utilisation(s) restante(s) ce tour, munitions : infinies");
        } else {
            System.out.println("[" +
                (gameManager != null ? gameManager.getCurrentTeamName() : "?") +
                "] tire avec " + equippedItem.name +
                " → " + restantesTour + " utilisation(s) ce tour, " +
                equippedItem.remainingUses + " munition(s) restante(s)");
        }

        // déléguer le tir au listener (SwingInputController -> handleFireAt)
        if (fireListener != null) {
            MouseEvent fake = new MouseEvent(
                    this,
                    MouseEvent.MOUSE_PRESSED,
                    System.currentTimeMillis(),
                    0,
                    x, y,
                    1,
                    false,
                    MouseEvent.BUTTON1
            );
            fireListener.mousePressed(fake);
        }
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        int w = getWidth();
        int h = getHeight();

        Graphics2D g2 = (Graphics2D) g;

        // icône sac
        int iconSize = 80;
        int iconX = w - iconSize - 16;
        int iconY = 16;

        iconBounds = new Rectangle(iconX, iconY, iconSize, iconSize);

        if(icon != null){
            g2.drawImage(icon,iconX,iconY,iconSize,iconSize,null);
        } else {
            g2.setColor(Color.GRAY);
            g2.fillRect(iconX,iconY, iconSize, iconSize);
            g2.setColor(Color.WHITE);
            g2.drawString("INV", iconX + 10, iconY + 28);
        }

        if(!visible){
            return;
        }
        computeLayout();


        // panneau inventaire à gauche
        int panelW = 360;
        int panelH = 520;
        int panelX = 20;
        int panelY = (h - panelH) / 2;

        panelBounds = new Rectangle(panelX, panelY, panelW, panelH);

        g2.setColor(new Color(40,40,40));
        g2.fillRoundRect(panelX, panelY, panelW, panelH, 20, 20);

        g2.setColor(Color.WHITE);
        g2.drawString("INVENTAIRE", panelX + 20, panelY + 30);

        int tabY = panelY + 35;
        int tabW = 90;
        int tabH = 24;

        armesTab  = new Rectangle(panelX + 20, tabY, tabW, tabH);
        gadgetTab = new Rectangle(panelX + 120, tabY, tabW, tabH);
        craftTab  = new Rectangle(panelX + 220, tabY, tabW, tabH);

        drawTab(g2, armesTab,  "ARMES",  currentCategory == Category.ARMES,  hoveredTab == Category.ARMES);
        drawTab(g2, gadgetTab, "GADGET", currentCategory == Category.GADGET, hoveredTab == Category.GADGET);
        drawTab(g2, craftTab,  "CRAFT",  currentCategory == Category.CRAFT,  hoveredTab == Category.CRAFT);

        itemBounds.clear();

        int startX = panelX + 20;
        int startY = panelY + 80;

        int col = 0;
        int row = 0;

        for (int i = 0; i < lastFiltered.size(); i++) {
            InventoryJson item = lastFiltered.get(i);

            int x = startX + col * (CELL_SIZE + CELL_GAP);
            int y = startY + row * (CELL_SIZE + CELL_GAP);

            Rectangle r = new Rectangle(x, y, CELL_SIZE, CELL_SIZE);
            itemBounds.add(r);

            // fond sélectionné
            if (i == selectedIndex) {
                g2.setColor(new Color(80, 80, 120));
            } else {
                g2.setColor(new Color(60, 60, 60));
            }
            g2.fillRoundRect(x, y, CELL_SIZE, CELL_SIZE, 12, 12);

            // bordure
            g2.setColor(Color.WHITE);
            g2.drawRoundRect(x, y, CELL_SIZE, CELL_SIZE, 12, 12);

            // placeholder image
            int imgSize = CELL_SIZE - 20;
            int imgX = x + (CELL_SIZE - imgSize) / 2;
            int imgY = y + 10;

            BufferedImage img = itemImages.get(item.name.toLowerCase());
            if (img != null) {
                g2.drawImage(img, imgX, imgY, imgSize, imgSize, null);
            }

            // nom arme
            g2.setFont(g2.getFont().deriveFont(10f));
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(item.name);

            g2.drawString(
                item.name,
                x + (CELL_SIZE - textW) / 2,
                y + CELL_SIZE - 6
            );

            col++;
            if (col >= GRID_COLS) {
                col = 0;
                row++;
            }
        }

        // --- Description (hover > équipé) ---
        InventoryJson infoItem = null;
        if (hoveredIndex >= 0 && hoveredIndex < lastFiltered.size()) {
            infoItem = lastFiltered.get(hoveredIndex);
        } else if (equippedItem != null) {
            infoItem = equippedItem;
        }

        if (infoItem != null) {
            String key = infoItem.name.toLowerCase();
            String desc = itemDescriptions.getOrDefault(key, ""); 

            int boxX = panelX + 20;
            int boxW = panelW - 40;
            int boxH = 60;
            int boxY = panelY + panelH - boxH - 15;

            g2.setColor(new Color(0,0,0,120));
            g2.fillRoundRect(boxX, boxY, boxW, boxH, 12, 12);

            g2.setColor(Color.WHITE);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 12f));
            g2.drawString(infoItem.name, boxX + 10, boxY + 20);

            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 11f));
            g2.drawString(desc, boxX + 10, boxY + 40);
        }

        if (equippedItem != null) {
            g2.drawString("Equipé : " + equippedItem.name,
                          panelX + 20,
                          panelY + panelH - 20);
        }
    }

    public void setInventory(List<InventoryJson> inventory){
        this.inventory = inventory;
        selectedIndex = -1;
        equippedItem = null;
        itemBounds.clear();
        repaint();
    }
    
    public void setInventoryVisible(boolean visible){
        this.visible = visible;

        if (visible) {
            computeLayout();     // IMPORTANT : tabs + grid prêts avant le 1er clic
        } else {
            // optionnel : reset sélection quand on ferme
            selectedIndex = -1;
            // equippedItem = null; // si tu veux aussi oublier l'arme équipée
        }
        repaint();
    }

    
    public boolean isInventoryVisible(){
        return visible;
    }

    private boolean itemMatchesCategory(InventoryJson item) {
        return switch (currentCategory) {
            case ARMES  -> item.objectType.equalsIgnoreCase("weapon");
            case GADGET -> item.objectType.equalsIgnoreCase("gadget");
            case CRAFT  -> item.objectType.equalsIgnoreCase("craft");
        };
    }

    private List<InventoryJson> getFilteredInventory() {
        List<InventoryJson> res = new ArrayList<>();
        for (InventoryJson item : inventory) {
            if (itemMatchesCategory(item)) {
                res.add(item);
            }
        }
        return res;
    }

    private void drawTab(Graphics2D g2, Rectangle r, String label, boolean active, boolean hovered) {
        Color base = new Color(70,70,70);
        Color on   = new Color(90,90,140);
        Color hov  = new Color(95,95,95);

        if (active) g2.setColor(on);
        else if (hovered) g2.setColor(hov);
        else g2.setColor(base);

        g2.fillRoundRect(r.x, r.y, r.width, r.height, 10, 10);

        g2.setColor(Color.WHITE);
        g2.drawRoundRect(r.x, r.y, r.width, r.height, 10, 10);

        FontMetrics fm = g2.getFontMetrics();
        int tx = r.x + (r.width - fm.stringWidth(label)) / 2;
        int ty = r.y + r.height - 7;
        g2.drawString(label, tx, ty);
    }



    private void computeLayout() {
    int w = getWidth();
    int h = getHeight();

    // panneau inventaire à gauche
    int panelW = 360;
    int panelH = 520;
    int panelX = 20;
    int panelY = (h - panelH) / 2;

    panelBounds = new Rectangle(panelX, panelY, panelW, panelH);

    int tabY = panelY + 35;
    int tabW = 90;
    int tabH = 24;

    armesTab  = new Rectangle(panelX + 20,  tabY, tabW, tabH);
    gadgetTab = new Rectangle(panelX + 120, tabY, tabW, tabH);
    craftTab  = new Rectangle(panelX + 220, tabY, tabW, tabH);

    // grille
    int startX = panelX + 20;
    int startY = panelY + 80;

    itemBounds.clear();
    lastFiltered = getFilteredInventory();

    int col = 0;
    int row = 0;

    for (int i = 0; i < lastFiltered.size(); i++) {
        int x = startX + col * (CELL_SIZE + CELL_GAP);
        int y = startY + row * (CELL_SIZE + CELL_GAP);

        itemBounds.add(new Rectangle(x, y, CELL_SIZE, CELL_SIZE));

        col++;
        if (col >= GRID_COLS) {
            col = 0;
            row++;
        }
    }
}

}

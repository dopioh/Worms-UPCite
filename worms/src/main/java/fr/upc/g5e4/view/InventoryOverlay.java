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

    private String equippedItemKey = null; 
    private Category currentCategory = Category.ARMES;

    private List<InventoryJson> inventory;
    private boolean visible = false;
    private MouseAdapter fireListener; 
    private List<InventoryJson> lastFiltered = new ArrayList<>();

    // liste des items
    private final List<Rectangle> itemBounds = new ArrayList<>();
    private int selectedIndex = -1;
    private InventoryJson equippedItem = null;

    // zone du panneau inventaire
    private Rectangle panelBounds;

    private GameManager gameManager;
    private Runnable onGadgetUse;

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
        loadImage("PotionSpeed", "/gadgets/potion_speed.png");
        loadImage("PotionJump", "/gadgets/potion_jump.png");
        loadImage("PotionHeal", "/gadgets/potion_heal.png");
        loadImage("PotionShield", "/gadgets/potion_shield.png");
        loadImage("Bumper", "/gadgets/bumper.png");
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
        itemDescriptions.put("potionspeed", "Augmente la vitesse +50% pour 20s");
        itemDescriptions.put("potionjump", "Augmente le saut +50% pour 20s");
        itemDescriptions.put("potionheal", "Restaure 50 HP");
        itemDescriptions.put("potionshield", "Ajoute 25 de shield");
        itemDescriptions.put("bumper", "Rebondis");
    }


    public InventoryOverlay(List<InventoryJson> inventory) {
        this.inventory = inventory;
        setOpaque(false);
        loadItemImages(); //Pour les icones des armes/gadgets
        
        loadDescriptions();
        ToolTipManager.sharedInstance().registerComponent(this); // optionnel (tooltips Swing)

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // Quand inventaire fermé, passer l'événement au controller
                if (!visible && inputControllerRef != null && e.getButton() == MouseEvent.BUTTON1) {
                    inputControllerRef.fireWeaponAtAngle(e.getX(), e.getY());
                }
            }
        });

        // HOVER + Deleguer les événements au controller quand inventaire fermé
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!visible) {
                    // Passer l'événement au controller de tir
                    if (inputControllerRef != null) {
                        inputControllerRef.updateTrajectoryFromMouseMotion(e.getX(), e.getY());
                    }
                    return;
                }

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

            @Override
            public void mouseDragged(MouseEvent e) {
                // Quand inventaire fermé, passer l'événement au controller
                if (!visible && inputControllerRef != null) {
                    inputControllerRef.updateTrajectoryFromMouseMotion(e.getX(), e.getY());
                }
            }
        });


    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * définit un rappel quand un gadget est utilisé
     */
    public void setOnGadgetUse(Runnable callback) {
        this.onGadgetUse = callback;
    }

    private SwingInputController inputControllerRef;

    /**
     * FireListener basé sur le SwingInputController (tir vers la souris)
     */
    public void setFireListener(SwingInputController inputController){      // Listener par défaut
        this.inputControllerRef = inputController;
        this.fireListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    inputController.updateTrajectoryFromMouse(e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    inputController.fireWeaponAtAngle(e.getX(), e.getY());
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
            computeLayout();  
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
                    equipItem(lastFiltered.get(i));
                    repaint();
                    return;
                }
            }
            return; // clic dans le panneau mais pas sur un item -> rien
        }
        
        // clic en dehors du panneau -> bloqué quand l'inventaire est ouvert
        return;
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        int w = getWidth();
        int h = getHeight();

        Graphics2D g2 = (Graphics2D) g;

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
    }

    public void setInventory(List<InventoryJson> inventory){
        this.inventory = inventory;
        itemBounds.clear();
        computeLayout();
        resyncEquippedFromInventory();
        repaint();
    }
    
    public void setInventoryVisible(boolean visible){
        this.visible = visible;

        if (visible) {
            computeLayout();
            resyncEquippedFromInventory();    
        } else {
            selectedIndex = -1;
            // Re-synchroniser l'arme équipée quand on ferme l'inventaire
            if (equippedItem != null) {
                equipItem(equippedItem);
            }
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

    public InventoryJson getEquippedItem() {
        return equippedItem;
    }

    private void equipItem(InventoryJson item) {
        equippedItemKey = item.name.toLowerCase();
        equippedItem = item;
        if (gameManager != null) {
            // appeler la bonne méthode selon le type d'objet
            if ("GADGET".equalsIgnoreCase(item.objectType)) {
                gameManager.setGadgetFromInventory(item.name);
                // Désactiver la trajectoire quand un gadget est équipé
                if (inputControllerRef != null) {
                    inputControllerRef.disableTrajectory();
                }
            } else {
                // WEAPON par défaut
                gameManager.setWeaponFromInventory(item.name);
            }
        }
    }

    private void resyncEquippedFromInventory() {
        if (equippedItemKey == null || inventory == null) return;

        // retrouver l'objet actuel dans la nouvelle liste inventory
        equippedItem = null;
        for (InventoryJson it : inventory) {
            if (it != null && it.name != null && it.name.toLowerCase().equals(equippedItemKey)) {
                equippedItem = it;
                break;
            }
        }

        // si on est sur l'onglet actuel, mettre à jour selectedIndex aussi
        selectedIndex = -1;
        for (int i = 0; i < lastFiltered.size(); i++) {
            InventoryJson it = lastFiltered.get(i);
            if (it != null && it.name != null && it.name.toLowerCase().equals(equippedItemKey)) {
                selectedIndex = i;
                break;
            }
        }
    }

    /**
     * utilise un gadget de l'inventaire quand celui-ci est fermé
     * @param gadgetIndex l'index du gadget dans la liste des gadgets (0-4)
     */
    public void useGadgetByIndex(int gadgetIndex) {
        if (visible) return; // ne pas pouvoir utiliser l'inventaire si c'est ouvert
        
        // filtrer les gadgets seulement
        List<InventoryJson> gadgets = new ArrayList<>();
        for (InventoryJson item : inventory) {
            if (item != null && "GADGET".equals(item.objectType)) {
                gadgets.add(item);
            }
        }
        
        if (gadgetIndex < 0 || gadgetIndex >= gadgets.size()) {
            return; // index invalide
        }
        
        InventoryJson gadget = gadgets.get(gadgetIndex);
        if (gadget.canUseThisTurn()) {
            if (gameManager != null) {
                gameManager.useGadgetFromInventory(gadget.name);
            }
            if (onGadgetUse != null) {
                onGadgetUse.run();
            }
        }
    }

    /**
     * utilise le gadget actuellement équipé
     * appelé quand l'inventaire est fermé et on clique
     */
    public void useEquippedGadget() {
        if (visible || equippedItem == null) return; // ne pas utiliser si l'inventaire est ouvert ou pas de gadget équipé
        
        // vérifier que c'est un gadget
        if (!"GADGET".equals(equippedItem.objectType)) {
            return; // ce n'est pas un gadget
        }
        
        if (equippedItem.canUseThisTurn()) {
            if (gameManager != null) {
                gameManager.useGadgetFromInventory(equippedItem.name);
            }
            if (onGadgetUse != null) {
                onGadgetUse.run();
            }
        }
    }

    /**
     * @return le gadget actuellement équipé
     */
    public InventoryJson getEquippedGadget() {
        if (equippedItem != null && "GADGET".equals(equippedItem.objectType)) {
            return equippedItem;
        }
        return null;
    }

}

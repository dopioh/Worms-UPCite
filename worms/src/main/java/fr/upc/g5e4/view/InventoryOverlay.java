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
        ARMES, GADGET, BOUTIQUE
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

    // --- Grille INVENTAIRE ---
    private static final int CELL_SIZE = 96;   // taille des cases
    private static final int CELL_GAP  = 16;   // espace entre cases
    private static final int GRID_COLS = 3;    // plus de colonnes

    private Rectangle armesTab;
    private Rectangle gadgetTab;
    private Rectangle boutiqueTab;
    
    // Boutique
    private fr.upc.g5e4.controller.PlayerDataController playerDataController;
    private int selectedShopIndex = -1;
    private final List<Rectangle> buyButtons = new ArrayList<>();
    private WormRaceOverlay wormRaceOverlay;
    private WormGuessOverlay wormGuessOverlay;
    private WormJackOverlay wormJackOverlay;
    
    // Items de la boutique
    private static class ShopItem {
        String name;
        String imagePath;
        int price;
        
        ShopItem(String name, String imagePath, int price) {
            this.name = name;
            this.imagePath = imagePath;
            this.price = price;
        }
    }
    
    private final List<ShopItem> shopItems = new ArrayList<>();

    
    //Icon Armes/Gadget/Boutique
    private final java.util.Map<String, BufferedImage> itemImages = new java.util.HashMap<>();
    private void loadItemImages() {
        loadImage("AK47", "/ak.png");
        loadImage("Grenade", "/grenade.png");
        loadImage("Bazooka", "/bazooka.png");
        loadImage("BaseballBat", "/baseballbat.png");
        //loadImage("Bumper", "/"); Pas encore fait
        
        // Boutique
        loadImage("luckyblock", "/luckyblock.png");
        loadImage("stickygamble", "/stick-ticket.png");
        loadImage("wormjack", "/wormjack.png");
        loadImage("wormrace", "/wormrace.png");
        loadImage("wormguess", "/wormguess.png");
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
        
        loadDescriptions();
        ToolTipManager.sharedInstance().registerComponent(this); // optionnel (tooltips Swing)
        
        // init la boutique
        playerDataController = new fr.upc.g5e4.controller.PlayerDataController();
        playerDataController.chargerJoueur();
        
        shopItems.add(new ShopItem("LuckyBlock", "luckyblock", 50));
        shopItems.add(new ShopItem("WORMRACE", "wormrace", 0));
        shopItems.add(new ShopItem("WORMJACK", "wormjack", 0));
        shopItems.add(new ShopItem("WORMGUESS", "wormguess", 0));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });

        // HOVER 
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
                else if (boutiqueTab != null && boutiqueTab.contains(x, y)) newHoveredTab = Category.BOUTIQUE;

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
    public void setFireListener(SwingInputController inputController){      // Listener par défaut
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
            // Reprendre le timer si on quitte la boutique
            if (gameManager != null && gameManager.getGameLoop() != null) {
                gameManager.getGameLoop().resumeTimer();
            }
            computeLayout();  
            repaint();
            return;
        }

        if (gadgetTab != null && gadgetTab.contains(x, y)) {
            currentCategory = Category.GADGET;
            selectedIndex = -1;
            // resume le timer quand on quitte la boutique
            if (gameManager != null && gameManager.getGameLoop() != null) {
                gameManager.getGameLoop().resumeTimer();
            }
            computeLayout();
            repaint();
            return;
        }

        if (boutiqueTab != null && boutiqueTab.contains(x, y)) {
            currentCategory = Category.BOUTIQUE;
            selectedIndex = -1;
            selectedShopIndex = -1;
            // pause timer dans la boutique
            if (gameManager != null && gameManager.getGameLoop() != null) {
                gameManager.getGameLoop().pauseTimer();
            }
            computeLayout();
            repaint();
            return;
        }


        // inventaire ouvert
        // clic dans le panneau -> sélection / équipement
        if (panelBounds != null && panelBounds.contains(x, y)) {
            if (currentCategory == Category.BOUTIQUE) {
                for (int i = 0; i < buyButtons.size(); i++) {
                    if (buyButtons.get(i).contains(x, y)) {
                        selectedShopIndex = i;
                        handlePurchase();
                        return;
                    }
                }
            }
            
            for (int i = 0; i < itemBounds.size(); i++) {
                Rectangle r = itemBounds.get(i);
                if (r.contains(x, y)) {
                    if (currentCategory == Category.BOUTIQUE) {
                        selectedShopIndex = i;
                    } else {
                        selectedIndex = i;
                        equipItem(lastFiltered.get(i));
                    }
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
        
        // affiche l'argent du joueur dans la boutique
        if (currentCategory == Category.BOUTIQUE) {
            int money = playerDataController.getPlayerData().getMoney();
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));
            g2.setColor(new Color(255, 215, 0));
            String moneyText = "Argent: " + money + " $";
            FontMetrics fm = g2.getFontMetrics();
            int moneyW = fm.stringWidth(moneyText);
            g2.drawString(moneyText, panelX + panelW - moneyW - 20, panelY + 30);
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 12f));
            g2.setColor(Color.WHITE);
        }

        int tabY = panelY + 35;
        int tabW = 90;
        int tabH = 24;

        armesTab  = new Rectangle(panelX + 20, tabY, tabW, tabH);
        gadgetTab = new Rectangle(panelX + 120, tabY, tabW, tabH);
        boutiqueTab  = new Rectangle(panelX + 220, tabY, tabW, tabH);

        drawTab(g2, armesTab,  "ARMES",  currentCategory == Category.ARMES,  hoveredTab == Category.ARMES);
        drawTab(g2, gadgetTab, "GADGET", currentCategory == Category.GADGET, hoveredTab == Category.GADGET);
        drawTab(g2, boutiqueTab,  "BOUTIQUE",  currentCategory == Category.BOUTIQUE,  hoveredTab == Category.BOUTIQUE);

        itemBounds.clear();
        buyButtons.clear();

        int startX = panelX + 20;
        int startY = panelY + 80;

        int col = 0;
        int row = 0;
        
        // si on est dans la boutique on affiche les items de la boutique
        if (currentCategory == Category.BOUTIQUE) {
            int btnHeight = 24;
            int btnMargin = 8;
            int totalCellHeight = CELL_SIZE + btnHeight + btnMargin + CELL_GAP;
            
            for (int i = 0; i < shopItems.size(); i++) {
                ShopItem shopItem = shopItems.get(i);

                int x = startX + col * (CELL_SIZE + CELL_GAP);
                int y = startY + row * totalCellHeight;

                Rectangle r = new Rectangle(x, y, CELL_SIZE, CELL_SIZE);
                itemBounds.add(r);

                // fond sélectionné
                if (i == selectedShopIndex) {
                    g2.setColor(new Color(80, 120, 80));
                } else {
                    g2.setColor(new Color(60, 60, 60));
                }
                g2.fillRoundRect(x, y, CELL_SIZE, CELL_SIZE, 12, 12);

                // bordure
                g2.setColor(Color.WHITE);
                g2.drawRoundRect(x, y, CELL_SIZE, CELL_SIZE, 12, 12);

                // image
                int imgSize = CELL_SIZE - 20;
                int imgX = x + (CELL_SIZE - imgSize) / 2;
                int imgY = y + 10;

                BufferedImage img = itemImages.get(shopItem.imagePath.toLowerCase());
                if (img != null) {
                    g2.drawImage(img, imgX, imgY, imgSize, imgSize, null);
                }

                // nom & prix
                g2.setFont(g2.getFont().deriveFont(10f));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                int textW = fm.stringWidth(shopItem.name);

                g2.drawString(shopItem.name, x + (CELL_SIZE - textW) / 2, y + CELL_SIZE - 20);
                
                g2.setColor(new Color(255, 215, 0));
                String priceText = shopItem.price + " $";
                int priceW = fm.stringWidth(priceText);
                g2.drawString(priceText, x + (CELL_SIZE - priceW) / 2, y + CELL_SIZE - 6);
                
                // Bouton acheter sous l'item
                int btnW = CELL_SIZE;
                int btnH = 24;
                int btnX = x;
                int btnY = y + CELL_SIZE + 8;
                
                Rectangle buyBtn = new Rectangle(btnX, btnY, btnW, btnH);
                buyButtons.add(buyBtn);
                
                g2.setColor(new Color(76, 175, 80));
                g2.fillRoundRect(btnX, btnY, btnW, btnH, 8, 8);
                
                g2.setColor(Color.WHITE);
                g2.drawRoundRect(btnX, btnY, btnW, btnH, 8, 8);
                
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 11f));
                String btnText = "ACHETER";
                FontMetrics btnFm = g2.getFontMetrics();
                int btnTextW = btnFm.stringWidth(btnText);
                g2.drawString(btnText, btnX + (btnW - btnTextW) / 2, btnY + 16);

                col++;
                if (col >= GRID_COLS) {
                    col = 0;
                    row++;
                }
            }
        } else {
            // Affichage normal pour armes et gadgets
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
        }

        // --- Description (hover > équipé) - UNIQUEMENT pour armes et gadgets
        if (currentCategory != Category.BOUTIQUE) {
            InventoryJson infoItem = null;
            
            if (hoveredIndex >= 0 && hoveredIndex < lastFiltered.size()) {
                infoItem = lastFiltered.get(hoveredIndex);
            } else if (equippedItem != null) {
                infoItem = equippedItem;
            }

            if (infoItem != null) {
                String itemName = infoItem.name;
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
                g2.drawString(itemName, boxX + 10, boxY + 20);

                g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 11f));
                g2.drawString(desc, boxX + 10, boxY + 40);
            }
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
            // pause timer dans la boutique
            if (currentCategory == Category.BOUTIQUE && gameManager != null && gameManager.getGameLoop() != null) {
                gameManager.getGameLoop().pauseTimer();
            }
        } else {
            selectedIndex = -1;
            // Re-synchroniser l'arme équipée quand on ferme l'inventaire
            if (equippedItem != null) {
                equipItem(equippedItem);
            }
            // resume le timer quand l'inventaire est fermé
            if (gameManager != null && gameManager.getGameLoop() != null) {
                gameManager.getGameLoop().resumeTimer();
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
            case BOUTIQUE  -> item.objectType.equalsIgnoreCase("craft");
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
        boutiqueTab  = new Rectangle(panelX + 220, tabY, tabW, tabH);

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
            gameManager.setWeaponFromInventory(item.name);
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
    
    private void handlePurchase() {
        if (selectedShopIndex < 0 || selectedShopIndex >= shopItems.size()) {
            return;
        }
        
        ShopItem shopItem = shopItems.get(selectedShopIndex);
        int currentMoney = playerDataController.getPlayerData().getMoney();
        
        if (currentMoney < shopItem.price) {
            System.out.println("Pas assez d'argent pour acheter " + shopItem.name);
            return;
        }
        
        // Déduire l'argent
        playerDataController.getPlayerData().setMoney(currentMoney - shopItem.price);
        playerDataController.sauvegarderJoueur();
        
        System.out.println("Achat réussi: " + shopItem.name + " pour " + shopItem.price + "$");
        
        if ("WORMRACE".equalsIgnoreCase(shopItem.name)) {
            System.out.println("Déclenchement de WORMRACE");
            if (wormRaceOverlay != null) {
                wormRaceOverlay.showRace();
            } else {
                System.err.println("ERREUR: wormRaceOverlay est null!");
            }
        }
        
        if ("WORMGUESS".equalsIgnoreCase(shopItem.name)) {
            System.out.println("Déclenchement de WORMGUESS");
            if (wormGuessOverlay != null) {
                wormGuessOverlay.showGame();
            } else {
                System.err.println("ERREUR: wormGuessOverlay est null!");
            }
        }
        
        if ("WORMJACK".equalsIgnoreCase(shopItem.name)) {
            System.out.println("Déclenchement de WORMJACK");
            if (wormJackOverlay != null) {
                wormJackOverlay.showGame();
            } else {
                System.err.println("ERREUR: wormJackOverlay est null!");
            }
        }
        
        repaint();
    }
    
    public void setWormRaceOverlay(WormRaceOverlay overlay) {
        this.wormRaceOverlay = overlay;
    }
    
    public void setWormGuessOverlay(WormGuessOverlay overlay) {
        this.wormGuessOverlay = overlay;
    }
    
    public void setWormJackOverlay(WormJackOverlay overlay) {
        this.wormJackOverlay = overlay;
    }

}

package fr.upc.g5e4.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import fr.upc.g5e4.model.InventoryModel;
import fr.upc.g5e4.model.InventoryModel.InventoryJson;
import fr.upc.g5e4.controller.SwingInputController;
import fr.upc.g5e4.model.GameManager;
import fr.upc.g5e4.controller.PlayerDataController;

/**
 * Overlay Swing affichant l'inventaire du joueur
 *
 *   Affichage des objets filtrés par catégories (armes, gadgets, craft)
 *   Sélection et équipement d'un objet
 *   Survol (hover) pour afficher une description
 * 
 * @author Hugo
 * @version 1.0
 */
public class InventoryOverlay extends JComponent {
    private enum Category {
        ARMES, GADGET, BOUTIQUE
    }

    private String equippedItemKey = null; 
    private Category currentCategory = Category.ARMES;
    private boolean ReleaseEat = false;


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

    private Rectangle closeButtonBounds;
    
    // --- Grille INVENTAIRE ---
    private static final int CELL_SIZE = 96;   // taille des cases
    private static final int CELL_GAP  = 16;   // espace entre cases
    private static final int GRID_COLS = 3;    // 3 colonnes pour ARMES/GADGET (5 pour BOUTIQUE)

    private Rectangle armesTab;
    private Rectangle gadgetTab;
    private Rectangle boutiqueTab;
        
    // Boutique
    private PlayerDataController playerDataController;
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
        
        // Boutique
        loadImage("luckyblock", "/luckyblock.png");
        loadImage("wormcostar", "/wormcostar.png");
        loadImage("wormnerd", "/wormnerd.png");
        loadImage("wormcuisto", "/wormcuisto.png");
        loadImage("wormnoel", "/wormnoel.png");
        loadImage("wormjack", "/wormjack.png");
        loadImage("wormrace", "/wormrace.png");
        loadImage("wormguess", "/wormguess.png");

        // gadget & boutique
        loadImage("PotionSpeed", "/gadgets/potion_speed.png");
        loadImage("PotionJump", "/gadgets/potion_jump.png");
        loadImage("PotionHeal", "/gadgets/potion_heal.png");
        loadImage("PotionShield", "/gadgets/potion_shield.png");
        //loadImage("Bumper", "/gadgets/bumper.png");
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
        setVisible(false);
        setOpaque(false);
        loadItemImages(); //Pour les icones des armes/gadgets
        
        loadDescriptions();
        ToolTipManager.sharedInstance().registerComponent(this); // optionnel (tooltips Swing)
        
        // init la boutique
        playerDataController = new fr.upc.g5e4.controller.PlayerDataController();
        playerDataController.chargerJoueur();
        
        shopItems.add(new ShopItem("Worm Nerd Edition", "wormnerd", 20));
        shopItems.add(new ShopItem("Worm Cuisto Edition", "wormcuisto", 20));
        shopItems.add(new ShopItem("Worm Costard Edition", "wormcostar", 20));
        shopItems.add(new ShopItem("Worm Noel Edition", "wormnoel", 20));
        shopItems.add(new ShopItem("WORMRACE", "wormrace", 0));
        shopItems.add(new ShopItem("WORMJACK", "wormjack", 0));
        shopItems.add(new ShopItem("WORMGUESS", "wormguess", 0));
        shopItems.add(new ShopItem("PotionHeal", "potionheal", 20));
        shopItems.add(new ShopItem("PotionShield", "potionshield", 30));
        shopItems.add(new ShopItem("PotionSpeed", "potionspeed", 40));
        shopItems.add(new ShopItem("PotionJump", "potionjump", 40));
        shopItems.add(new ShopItem("Balles AK47 x10", "ak47", 20));
        shopItems.add(new ShopItem("Rocket Bazooka x1", "bazooka", 200));
        shopItems.add(new ShopItem("Grenade x1", "grenade", 50));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (ReleaseEat) {
                    ReleaseEat = false;
                    return;
                }
                // Quand inventaire fermé, passer l'événement au controller
                if (!visible && inputControllerRef != null && e.getButton() == MouseEvent.BUTTON1) {
                    inputControllerRef.fireWeaponAtAngle(e.getX(), e.getY());
                }
            }
        });

        // HOVER
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!visible) {
                    // passer l'événement au controller de tir
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

            @Override
            public void mouseDragged(MouseEvent e) {
                // quand inventaire fermé, passer l'événement au controller
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

        // Clic sur le bouton fermer
        if (closeButtonBounds != null && closeButtonBounds.contains(x, y)) {
            // évite le tir “fantôme” au mouseReleased
            ReleaseEat = true;
            setInventoryVisible(false);
            return;
        }

        
        // Clic sur catégorie
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


        // clic dans le panneau 
        if (panelBounds != null && panelBounds.contains(x, y)) {
            // gestion du clic "ACHETER" dans la boutique
            if (currentCategory == Category.BOUTIQUE) {
                for (int i = 0; i < buyButtons.size(); i++) {
                    if (buyButtons.get(i).contains(x, y)) {
                        selectedShopIndex = i;
                        handlePurchase();
                        return;
                    }
                }
            }

            // gestion du clic dans l'inventaire
            for (int i = 0; i < itemBounds.size(); i++) {
                Rectangle r = itemBounds.get(i);
                if (r.contains(x, y)) {
                    if (currentCategory != Category.BOUTIQUE) {
                        selectedIndex = i;
                        equipItem(lastFiltered.get(i));
                        ReleaseEat = true;
                        setInventoryVisible(false);
                    }
                    repaint();
                    return;
                }
            }
            return;
        }
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


        // panneau inventaire à gauche (plus large si boutique)
        int panelW = currentCategory == Category.BOUTIQUE ? 600 : 360;
        int panelH = currentCategory == Category.BOUTIQUE ? 640 : 520;
        int panelX = 20;
        int panelY = (h - panelH) / 2;

        panelBounds = new Rectangle(panelX, panelY, panelW, panelH);

        g2.setColor(new Color(40,40,40));
        g2.fillRoundRect(panelX, panelY, panelW, panelH, 20, 20);

        g2.setColor(Color.WHITE);
        g2.drawString("INVENTAIRE", panelX + 20, panelY + 30);
        
        // ----- Bouton FERMER (X rouge) -----
        int closeSize = 26;
        int closeX = panelX + panelW - closeSize - 12;
        int closeY = panelY + 10;

        closeButtonBounds = new Rectangle(closeX, closeY, closeSize, closeSize);

        g2.setColor(new Color(180, 60, 60));
        g2.fillRoundRect(closeX, closeY, closeSize, closeSize, 8, 8);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2.2f));
        g2.drawLine(closeX + 7, closeY + 7, closeX + closeSize - 7, closeY + closeSize - 7);
        g2.drawLine(closeX + closeSize - 7, closeY + 7, closeX + 7, closeY + closeSize - 7);
        g2.setStroke(new BasicStroke(1f));


        // affiche l'argent de l'équipe active dans la boutique
        if (currentCategory == Category.BOUTIQUE) {
            int money = 0;
            if (gameManager != null && gameManager.getCurrentTeam() != null) {
                money = gameManager.getCurrentTeam().getMoney();
            }
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));
            g2.setColor(new Color(255, 215, 0));
            String moneyText = "Argent: " + money + " $";
            FontMetrics fm = g2.getFontMetrics();
            int moneyW = fm.stringWidth(moneyText);
            g2.drawString(moneyText, panelX + panelW - moneyW - 20 -closeSize, panelY + 30);
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
        
        // Nombre de colonnes selon la catégorie
        int gridCols = currentCategory == Category.BOUTIQUE ? 5 : GRID_COLS;
        
        // si on est dans la boutique on affiche les items de la boutique
        if (currentCategory == Category.BOUTIQUE) {
            int btnHeight = 28;
            int btnMargin = 12;
            int nameSpacing = 30;
            int totalCellHeight = CELL_SIZE + nameSpacing + btnHeight + btnMargin + CELL_GAP;
            
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

                g2.drawString(shopItem.name, x + (CELL_SIZE - textW) / 2, y + CELL_SIZE + 15);
                
                g2.setColor(new Color(255, 215, 0));
                String priceText = shopItem.price + " $";
                int priceW = fm.stringWidth(priceText);
                g2.drawString(priceText, x + (CELL_SIZE - priceW) / 2, y + CELL_SIZE + 28);
                
                // Bouton acheter sous l'item
                int btnW = CELL_SIZE;
                int btnH = 28;
                int btnX = x;
                int btnY = y + CELL_SIZE + 35;
                
                Rectangle buyBtn = new Rectangle(btnX, btnY, btnW, btnH);
                buyButtons.add(buyBtn);
                
                // Vérifier si c'est un skin déjà débloqué
                // isSkin a un filtre pour prendre uniquement les skins c'est tres moche mais ça marche
                boolean isSkin = shopItem.name.startsWith("Worm") && 
                                 !shopItem.name.equals("WORMRACE") && 
                                 !shopItem.name.equals("WORMJACK") && 
                                 !shopItem.name.equals("WORMGUESS");
                boolean isUnlocked = isSkin && playerDataController.getPlayerData().isSkinUnlocked(shopItem.name);
                
                // check si l'équipe a assez d'argent
                int currentMoney = 0;
                if (gameManager != null && gameManager.getCurrentTeam() != null) {
                    currentMoney = gameManager.getCurrentTeam().getMoney();
                }
                boolean canAfford = currentMoney >= shopItem.price;
                
                if (isUnlocked) {
                    // bouton grisé pour skin débloqué
                    g2.setColor(new Color(100, 100, 100));
                    g2.fillRoundRect(btnX, btnY, btnW, btnH, 8, 8);
                    g2.setColor(new Color(150, 150, 150));
                    g2.drawRoundRect(btnX, btnY, btnW, btnH, 8, 8);
                    
                    g2.setColor(new Color(180, 180, 180));
                    g2.setFont(g2.getFont().deriveFont(Font.BOLD, 11f));
                    String btnText = "DÉBLOQUÉ";
                    FontMetrics btnFm = g2.getFontMetrics();
                    int btnTextW = btnFm.stringWidth(btnText);
                    g2.drawString(btnText, btnX + (btnW - btnTextW) / 2, btnY + 16);
                } else if (!canAfford) {
                    // bouton grisé pour argent insuffisant
                    g2.setColor(new Color(100, 100, 100));
                    g2.fillRoundRect(btnX, btnY, btnW, btnH, 8, 8);
                    g2.setColor(new Color(150, 150, 150));
                    g2.drawRoundRect(btnX, btnY, btnW, btnH, 8, 8);
                    
                    g2.setColor(new Color(180, 180, 180));
                    g2.setFont(g2.getFont().deriveFont(Font.BOLD, 10f));
                    String btnText = "INSUFFISANT";
                    FontMetrics btnFm = g2.getFontMetrics();
                    int btnTextW = btnFm.stringWidth(btnText);
                    g2.drawString(btnText, btnX + (btnW - btnTextW) / 2, btnY + 16);
                } else {
                    // bouton vert pour achat possible
                    g2.setColor(new Color(76, 175, 80));
                    g2.fillRoundRect(btnX, btnY, btnW, btnH, 8, 8);
                    
                    g2.setColor(Color.WHITE);
                    g2.drawRoundRect(btnX, btnY, btnW, btnH, 8, 8);
                    
                    g2.setFont(g2.getFont().deriveFont(Font.BOLD, 11f));
                    String btnText = "ACHETER";
                    FontMetrics btnFm = g2.getFontMetrics();
                    int btnTextW = btnFm.stringWidth(btnText);
                    g2.drawString(btnText, btnX + (btnW - btnTextW) / 2, btnY + 16);
                }

                // passage a la case suivante
                col++;
                if (col >= gridCols) {
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
            if (col >= gridCols) {
                col = 0;
                row++;
            }
        }
        }

        // Description
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

                //Fond de la description
                g2.setColor(new Color(0,0,0,120));
                g2.fillRoundRect(boxX, boxY, boxW, boxH, 12, 12);
                
                int leftPad = 10;
                int rightPad = 10;
                
                // Nom
                g2.setColor(Color.WHITE);
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 12f));
                g2.drawString(itemName, boxX + 10, boxY + 20);
                
                // Desccription
                g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 11f));
                g2.drawString(desc, boxX + 10, boxY + 40);

                // Zone du Tour et Stock
                g2.setColor(new Color(255, 220, 80)); // jaune
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 13.5f));

                String stockText = (infoItem.isInfinite || infoItem.remainingUses == null) ? "∞" : String.valueOf(infoItem.remainingUses);
                String tourText = infoItem.isInfinite? "∞" : (Math.max(0, infoItem.getRemainingUseThisTurn()) + "/" + infoItem.getMaxUsages());
                
                String line1 = "Tour: " + tourText;
                String line2 = "Stock: " + stockText;

                FontMetrics fm = g2.getFontMetrics();
                int rightX = boxX + boxW - rightPad;

                // Dessine le texte a droite
                g2.drawString(line1, rightX - fm.stringWidth(line1), boxY + 22);
                g2.drawString(line2, rightX - fm.stringWidth(line2), boxY + 44);
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
        setVisible(visible); 
        if (visible) {
            computeLayout();
            resyncEquippedFromInventory();
            // pause timer dans la boutique
            if (currentCategory == Category.BOUTIQUE && gameManager != null && gameManager.getGameLoop() != null) {
                gameManager.getGameLoop().pauseTimer();
            }
        } else {
            selectedIndex = -1;
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

    /**
     * Déséquipe l'arme en cours
     */
    public void clearEquipped() {
        equippedItemKey = null;
        equippedItem = null;
        selectedIndex = -1;
        repaint();
    }

    private void computeLayout() {
        int w = getWidth();
        int h = getHeight();

        // panneau inventaire à gauche (adapté selon la catégorie)
        int panelW = currentCategory == Category.BOUTIQUE ? 600 : 360;
        int panelH = currentCategory == Category.BOUTIQUE ? 640 : 520;
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

    /**
     * Renvoie l'arme équipée dans l'inventaire
     * @return l'arme équipée 
     */
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
                // désactiver la trajectoire quand un gadget est équipé
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
    
    private void handlePurchase() {
        if (selectedShopIndex < 0 || selectedShopIndex >= shopItems.size()) {
            return;
        }
        
        ShopItem shopItem = shopItems.get(selectedShopIndex);
        
        // check si c'est un skin et s'il est déjà débloqué
        // on pourrait optimiser sachant qu'on fait deja le calcule dans paintComponent
        boolean isSkin = shopItem.name.startsWith("Worm") && 
                         !shopItem.name.equals("WORMRACE") && 
                         !shopItem.name.equals("WORMJACK") && 
                         !shopItem.name.equals("WORMGUESS");
        
        if (isSkin && playerDataController.getPlayerData().isSkinUnlocked(shopItem.name)) {
            System.out.println("Skin déjà débloqué: " + shopItem.name);
            return;
        }
        
        // on prend l'argent de l'équipe active
        if (gameManager == null || gameManager.getCurrentTeam() == null) {
            System.err.println("Erreur: pas d'équipe active");
            return;
        }
        
        int currentMoney = gameManager.getCurrentTeam().getMoney();
        
        if (currentMoney < shopItem.price) {
            System.out.println("Pas assez d'argent pour acheter " + shopItem.name);
            return;
        }
        
        // Déduire l'argent de l'équipe
        gameManager.getCurrentTeam().removeMoney(shopItem.price);
        
        // on unlock le skin si c'est un skin
        if (isSkin) {
            playerDataController.getPlayerData().unlockSkin(shopItem.name);
            System.out.println("Skin débloqué: " + shopItem.name);
        } else if (!shopItem.name.startsWith("WORM")) { //filtre pour ne ajouter pas WORMRACE, WORMJACK, WORMGUESS dans l'inventaire
            // Ajout des items (potions/balles) à l'inventaire de l'équipe
            addItemToTeamInventory(shopItem);
        }
        
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
    
    /**
     * Ajoute l'item acheté à l'inventaire de l'équipe active
     * @param shopItem l'item acheté
     */
    private void addItemToTeamInventory(ShopItem shopItem) {
        if (gameManager == null || gameManager.getCurrentTeam() == null) {
            System.err.println("Erreur: pas d'équipe active pour ajouter l'item");
            return;
        }
        
        List<InventoryJson> teamInventory = gameManager.getCurrentTeam().getInventory();
        String objectType = null;
        int quantityToAdd = 0;
        
        // pour chaque item de la boutique on definit ses données pour ajouter a l'inventaire
        // redondant, on pourrait inclure ces données directement dans ShopItem
        if (shopItem.imagePath.equals("potionheal")) {
            objectType = "potion";
            quantityToAdd = 1;
        } else if (shopItem.imagePath.equals("potionshield")) {
            objectType = "potion";
            quantityToAdd = 1;
        } else if (shopItem.imagePath.equals("potionspeed")) {
            objectType = "potion";
            quantityToAdd = 1;
        } else if (shopItem.imagePath.equals("potionjump")) {
            objectType = "potion";
            quantityToAdd = 1;
        } else if (shopItem.imagePath.equals("ak47")) {
            objectType = "weapon";
            quantityToAdd = 10;
        } else if (shopItem.imagePath.equals("bazooka")) {
            objectType = "weapon";
            quantityToAdd = 1;
        } else if (shopItem.imagePath.equals("grenade")) {
            objectType = "weapon";
            quantityToAdd = 1;
        }
        
        // securité si l'item n'est pas reconnu
        if (objectType == null) {
            System.out.println("Item introuvable dans l'inventaire: " + shopItem.name);
            return;
        }
        
        // recherche si l'item existe déjà dans l'inventaire
        InventoryJson existingItem = null;
        for (InventoryJson item : teamInventory) {
            if (item.name.equalsIgnoreCase(shopItem.imagePath)) {
                existingItem = item;
                break;
            }
        }
        
        if (existingItem != null) {
            // on augmente le remainingUses de l'item
            if (!existingItem.isInfinite && existingItem.remainingUses != null) {
                existingItem.remainingUses += quantityToAdd;
                System.out.println("Item ajouté à l'inventaire: " + shopItem.name + " (+" + quantityToAdd + " → total: " + existingItem.remainingUses + ")");
            }
        }
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

    @Override
    public boolean contains(int x, int y) {
        return visible && super.contains(x, y);
    }

}

package fr.upc.g5e4.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import fr.upc.g5e4.model.GameManager;
import fr.upc.g5e4.model.InventoryModel.InventoryJson;
import fr.upc.g5e4.model.CharacterModel;
/**
 * OVerlayHUD affichant les informations de l'interface utilisateur pendant le jeu.
 * 
 * Affiche les barres de vie et bouclier du personnage actif, l'arme équipée avec munitions
 * ,un bouton pour ouvrir l'inventaire et un bouton pour finir le tour 
 * 
 * @author Hugo
 * @version 1.0
 */
public class HUDOverlay extends JComponent {
    /**
     * Listener permettant à l'overlay d'inventaire de signaler
     * des actions utilisateur à un contrôleur externe.
     */
    public interface Listener {
        void onBagClicked();
        void onEndTurnClicked();
        void onPauseClicked();
    }

    private Listener listener;

    private final GameManager gameManager;
    private InventoryOverlay inventoryOverlay;
    private Rectangle endTurnBounds;
    private boolean endTurnHovered = false;
    private BufferedImage settingsIcon;
    private Rectangle gearBounds;
    private boolean gearHovered = false;

    
    // --- Weapon HUD ---
    private final java.util.Map<String, BufferedImage> weaponImages = new java.util.HashMap<>();

    private void loadWeaponImages() {
        loadWeaponImage("ak47", "/ak.png");
        loadWeaponImage("grenade", "/grenade.png");
        loadWeaponImage("rocketlauncher", "/rocketlauncher.png");
        loadWeaponImage("baseballbat", "/baseballbat.png");
    }


    private void loadWeaponImage(String name, String path) {
        try {
            weaponImages.put(name.toLowerCase(), javax.imageio.ImageIO.read(getClass().getResource(path)));
        } catch (Exception e) {
            System.err.println("[HUD] image arme introuvable: " + path);
        }
    }
    
    // --- Backpack HUD ---

    private BufferedImage backpackIcon;

    private void loadBackpack() {
        try {
            backpackIcon = javax.imageio.ImageIO.read(getClass().getResource("/backpack.png"));
        } catch (Exception e) {
            System.err.println("[HUD] backpack.png introuvable");
        }
    }


    // Timer  
    // Pour des animations a 60FPS
    private int timerRemainingFrames = 0;
    private int timerTotalFrames = 1;

    // Bouton sac
    private Rectangle bagBounds;

    public HUDOverlay(GameManager gm) {
        this.gameManager = gm;
        setOpaque(false);

        loadBackpack();
        loadSettingsIcon();
        loadWeaponImages();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                boolean changed = false;
                if (endTurnHovered) { endTurnHovered = false; changed = true; }
                if (gearHovered) { gearHovered = false; changed = true; }
                if (changed) repaint();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                computeLayout();
                boolean newHover = endTurnBounds != null && endTurnBounds.contains(e.getPoint());
                if (newHover != endTurnHovered) {
                    endTurnHovered = newHover;
                    repaint();
                }
                
                boolean newGearHover = gearBounds != null && gearBounds.contains(e.getPoint());
                if (newGearHover != gearHovered) {
                    gearHovered = newGearHover;
                    repaint();
                }
            }
        });
    }

    /**
     * Associe l'overlay d'inventaire au HUD afin de récupérer l'objet équipé
     * et permettre l'ouverture/fermeture via le bouton sac.
     *
     * @param inv overlay d'inventaire
     */
    public void setInventoryOverlay(InventoryOverlay inv) {
        this.inventoryOverlay = inv;
    }

    /**
    * Définit le listener recevant les événements du HUD.
    *
    * @param listener contrôleur (ou gestionnaire) qui traitera les clics sur le HUD
    */
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    // /**
    // * Met à jour le timer affiché dans le HUD.
    // *
    // * @param remainingSeconds temps restant (en secondes)
    // * @param totalSeconds durée totale du tour (en secondes)
    // */
    // public void setTimer(int remainingSeconds, int totalSeconds) {
    //     this.timerRemaining = Math.max(0, remainingSeconds);
    //     this.timerTotal = Math.max(1, totalSeconds);
    //     repaint();
    // }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!(g instanceof Graphics2D g2)) return;

        int w = getWidth();
        int h = getHeight();

        // Bouton pour les parametres
        int gearSize = 44;
        int gearX = 18;
        int gearY = 18;
        gearBounds = new Rectangle(gearX, gearY, gearSize, gearSize);

        g2.setColor(gearHovered ? new Color(255,255,255,200) : new Color(255,255,255,140));
        g2.fillRoundRect(gearX, gearY, gearSize, gearSize, 14, 14);

        g2.setColor(new Color(0,0,0,90));
        g2.drawRoundRect(gearX, gearY, gearSize, gearSize, 14, 14);

        if (settingsIcon != null) {
            g2.drawImage(settingsIcon, gearX + 8, gearY + 8, gearSize - 16, gearSize - 16, null);
        }

        // ---------- Dimensions HUD ----------
        int hudW = 520;
        int hudH = 95;
        int hudX = (w - hudW) / 2;
        int hudY = h - hudH - 35;

        // ---------- Timer bar (au-dessus HUD) ----------
        int timerW = 520;
        int timerH = 14;
        int timerX = (w - timerW) / 2;
        int timerY = hudY - timerH - 10;

        float t = timerRemainingFrames / (float) timerTotalFrames;
        t = Math.max(0f, Math.min(1f, t));

        g2.setColor(new Color(0,0,0,120));
        g2.fillRoundRect(timerX, timerY, timerW, timerH, 12, 12);

        g2.setColor(new Color(255, 120, 140, 220));
        g2.fillRoundRect(timerX, timerY, (int)(timerW * t), timerH, 12, 12);

        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 12f));
        int secondsLeft = (int)Math.ceil(timerRemainingFrames / 60.0);
        g2.drawString(secondsLeft + "s", timerX + timerW + 10, timerY + 12);


        // ---------- Fond HUD ----------
        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRoundRect(hudX, hudY, hudW, hudH, 18, 18);

        // ---------- Lire PV / Shield ----------
        double hp = 0;
        double shield = 0;
        double hpMax = 100;
        double shieldMax = 100;

        CharacterModel worm = null;
        if (gameManager != null && gameManager.getActiveWormController() != null) {
            worm = gameManager.getActiveWormController().getModel();
        }

        if (worm != null) {
            hp = worm.getHealth();
            shield = worm.getShield();     
        }

        // ---------- Barres PV / Shield ----------
        int barX = hudX + 25;
        int barW = 260;
        int barH = 12;

        int hpY = hudY + 20;
        int shY = hudY + 45;

        drawBar(g2, barX, hpY, barW, barH, shield, shieldMax, "Bouclier", new Color(120, 170, 255, 220)); //Bleu
        drawBar(g2, barX, shY, barW, barH, hp, hpMax, "PV", new Color(120, 200, 120, 220)); //Vert

        // ---------- Arme équipée + munitions ----------
        int weaponBoxW = 150;
        int weaponBoxH = 60;
        int weaponBoxX = hudX + hudW - weaponBoxW - 20;   // dans le HUD, à droite
        int weaponBoxY = hudY + (hudH - weaponBoxH) / 2;

        g2.setColor(new Color(245,245,245,230));
        g2.fillRoundRect(weaponBoxX, weaponBoxY, weaponBoxW, weaponBoxH, 14, 14);
        g2.setColor(new Color(0,0,0,120));
        g2.drawRoundRect(weaponBoxX, weaponBoxY, weaponBoxW, weaponBoxH, 14, 14);

        // récupérer l'arme courante
        String weaponName = null;
        Integer ammo = null; // null = infini

        InventoryJson eq = null;
        if (gameManager != null 
            && gameManager.getActiveWormController() != null) {
            eq = gameManager.getActiveWormController().getEquippedInventoryItem();
        }

        // priorité à l'inventaire
        if (eq != null) {
            weaponName = eq.name;                 
            ammo = eq.remainingUses;              
        }
        else if (gameManager != null && gameManager.getActiveWormController() != null
                && gameManager.getActiveWormController().getCurrentWeapon() != null) {

            weaponName = gameManager.getActiveWormController().getCurrentWeapon().getName();
        }

        // icône + nom + munitions
        if (weaponName != null) {
            String key = normalizeKey(weaponName);  // clé pour les images
            BufferedImage wi = weaponImages.get(key); 

            if (wi != null) {
                g2.drawImage(wi, weaponBoxX + 8, weaponBoxY + 8, 44, 44, null);
            }

            g2.setColor(Color.BLACK);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 12f));
            g2.drawString(weaponName, weaponBoxX + 58, weaponBoxY + 22);

            // munitions depuis InventoryJson
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));
            g2.setColor(new Color(220,60,60));

            String ammoText;
            if (eq == null) {
                ammoText = "--";
            } else if (eq.isInfinite) {
                ammoText = "∞";
            } else {
                String tour = Math.max(0, eq.getRemainingUseThisTurn()) + "/" + eq.getMaxUsages();
                String stock = (eq.remainingUses == null) ? "0" : String.valueOf(eq.remainingUses);
                ammoText = tour + " | " + stock;
            }

            g2.drawString(ammoText, weaponBoxX + 58, weaponBoxY + 45);

        } else {
            g2.setColor(Color.DARK_GRAY);
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 12f));
            g2.drawString("Aucune arme", weaponBoxX + 20, weaponBoxY + 35);
        }



        // ---------- Bouton sac à droite ----------
        int bagSize = 70;
        int bagX = hudX + hudW + 20;      // à droite du HUD
        int bagY = hudY + (hudH - bagSize) / 2;

        bagBounds = new Rectangle(bagX, bagY, bagSize, bagSize);

        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRoundRect(bagX, bagY, bagSize, bagSize, 18, 18);
        g2.setColor(new Color(0,0,0,120));
        g2.drawRoundRect(bagX, bagY, bagSize, bagSize, 18, 18);

        if (backpackIcon != null) {
            g2.drawImage(backpackIcon, bagX + 10, bagY + 10, bagSize - 20, bagSize - 20, null);
        } 
        
        // ---------- Bouton FIN DE TOUR ----------
        int btnW = 140;
        int btnH = 44;
        int btnX = hudX - btnW - 20; 
        int btnY = hudY + (hudH - btnH) / 2;

        endTurnBounds = new Rectangle(btnX, btnY, btnW, btnH);

        g2.setColor(endTurnHovered ? new Color(255, 160, 90, 220) : new Color(255, 140, 70, 200));
        g2.fillRoundRect(btnX, btnY, btnW, btnH, 16, 16);

        g2.setColor(new Color(0,0,0,140));
        g2.drawRoundRect(btnX, btnY, btnW, btnH, 16, 16);

        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));
        g2.drawString("Fin de tour", btnX + 18, btnY + 28);
    }

    // Dessine une barre 
    private void drawBar(Graphics2D g2, int x, int y, int w, int h, double value, double max, String label, Color fillColor) {
        float t = (max <= 0)? 0f : (float)(value / max);
        t = Math.max(0f, Math.min(1f, t));
        g2.setColor(new Color(0,0,0,140));
        g2.fillRoundRect(x, y, w, h, 12, 12);

        g2.setColor(fillColor);
        g2.fillRoundRect(x, y, (int)(w * t), h, 12, 12);

        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 12f));
        g2.drawString(label + " : " + (int)value, x + 8, y + 11);
    }

    private void handleMousePressed(MouseEvent e) { // Clic sur le sac à dos
        computeLayout();
        if (bagBounds != null && bagBounds.contains(e.getPoint())) {
            if (listener != null) {
                listener.onBagClicked();
                return;
            }
        }
        
        if (endTurnBounds != null && endTurnBounds.contains(e.getPoint())) {
            if (listener != null) listener.onEndTurnClicked();
            return;
        }

        if (gearBounds != null && gearBounds.contains(e.getPoint())) {
            if (listener != null) listener.onPauseClicked();
            return;
        }
    }

    private void computeLayout() { // Calcule les positions des éléments HUD
        int w = getWidth();
        int h = getHeight();

        int hudW = 520;
        int hudH = 95;
        int hudX = (w - hudW) / 2;
        int hudY = h - hudH - 35;

        int bagSize = 70;
        int bagX = hudX + hudW + 20;
        int bagY = hudY + (hudH - bagSize) / 2;

        bagBounds = new Rectangle(bagX, bagY, bagSize, bagSize);

        int btnW = 140;
        int btnH = 44;
        int btnX = hudX - btnW - 20;
        int btnY = hudY + (hudH - btnH) / 2;

        endTurnBounds = new Rectangle(btnX, btnY, btnW, btnH);

        int gearSize = 44;
        int gearX = 18;
        int gearY = 18;

        gearBounds = new Rectangle(gearX, gearY, gearSize, gearSize);
    }
    
    private String normalizeKey(String s) { //pour les images ne pas avoir de problème avec les noms d'armes
        if (s == null) return null;
        return s.toLowerCase().replaceAll("[^a-z0-9]", "");
    }   

    /**
     * Définit le timer en nombre de frames (60 FPS).
     * @param remainingFrames 
     * @param totalFrames 
     */
    public void setTimerFrames(int remainingFrames, int totalFrames) {
        this.timerRemainingFrames = Math.max(0, remainingFrames);
        this.timerTotalFrames = Math.max(1, totalFrames);
        repaint();
    }

    private void loadSettingsIcon() {
        try {
            settingsIcon = javax.imageio.ImageIO.read(getClass().getResource("/parametre.png"));
        } catch (Exception e) {
            System.err.println("[HUD] parametre.png introuvable");
        }
    }
 
    @Override
    public boolean contains(int x, int y) { // Clics uniquement si sur le sac à dos
        computeLayout();  //met à jour les positions des éléments
        return (bagBounds != null && bagBounds.contains(x, y)) || (endTurnBounds != null && endTurnBounds.contains(x, y)) || (gearBounds != null && gearBounds.contains(x, y));
    }
}

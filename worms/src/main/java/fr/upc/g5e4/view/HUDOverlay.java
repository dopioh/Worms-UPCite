package fr.upc.g5e4.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import fr.upc.g5e4.model.GameManager;
import fr.upc.g5e4.model.InventoryModel.InventoryJson;
import fr.upc.g5e4.model.CharacterModel;

public class HUDOverlay extends JComponent {

    private final GameManager gameManager;
    private InventoryOverlay inventoryOverlay;
    
    // --- Weapon HUD ---
    private BufferedImage weaponIcon;
    private final java.util.Map<String, BufferedImage> weaponImages = new java.util.HashMap<>();

    private void loadWeaponImages() {
        loadWeaponImage("ak47", "/ak.png");
        loadWeaponImage("grenade", "/grenade.png");
        loadWeaponImage("bazooka", "/bazooka.png");
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


    // Timer    ----------------------------------------------------------------------------------REMPLACER PLUS TARD PAR LE TIMER DU ROUND
    private int timerRemaining = 30;
    private int timerTotal = 30;

    // Bouton sac
    private Rectangle bagBounds;

    public HUDOverlay(GameManager gm) {
        this.gameManager = gm;
        setOpaque(false);

        loadBackpack();
        loadWeaponImages();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }
        });
    }

    public void setInventoryOverlay(InventoryOverlay inv) {
        this.inventoryOverlay = inv;
    }

    // appelé par GameLoop chaque frame mais ne marche pas pour le moment
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

        float t = timerRemaining / (float) timerTotal;
        t = Math.max(0f, Math.min(1f, t));

        g2.setColor(new Color(0,0,0,120));
        g2.fillRoundRect(timerX, timerY, timerW, timerH, 12, 12);

        g2.setColor(new Color(255, 120, 140, 220));
        g2.fillRoundRect(timerX, timerY, (int)(timerW * t), timerH, 12, 12);

        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 12f));
        g2.drawString(timerRemaining + "s", timerX + timerW + 10, timerY + 12);

        // ---------- Fond HUD ----------
        g2.setColor(new Color(255,255,255,235));
        g2.fillRoundRect(hudX, hudY, hudW, hudH, 18, 18);
        g2.setColor(new Color(0,0,0,120));
        g2.drawRoundRect(hudX, hudY, hudW, hudH, 18, 18);

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
            try {
                hp = worm.getHealth();
                shield = worm.getShield();
            } catch (Exception ignored) {
                // worm null ou autre erreur
            }
        }

        // ---------- Barres PV / Shield ----------
        int barX = hudX + 25;
        int barW = 260;
        int barH = 12;

        int hpY = hudY + 20;
        int shY = hudY + 45;

        drawBar(g2, barX, hpY, barW, barH, hp, hpMax, "PV");
        drawBar(g2, barX, shY, barW, barH, shield, shieldMax, "Bouclier");

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
        Integer ammo = null; // null = inconnu / infini

        InventoryJson eq = (inventoryOverlay != null) ? inventoryOverlay.getEquippedItem() : null;

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
            String key = weaponName.toLowerCase();  // clé pour les images
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
                ammoText = "--";              // rien équipé via inventaire
            } else if (eq.remainingUses == null || eq.isInfinite) {
                ammoText = "∞";               
            } else {
                ammoText = String.valueOf(eq.remainingUses); 
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

        g2.setColor(new Color(255,255,255,235));
        g2.fillRoundRect(bagX, bagY, bagSize, bagSize, 18, 18);
        g2.setColor(new Color(0,0,0,120));
        g2.drawRoundRect(bagX, bagY, bagSize, bagSize, 18, 18);

        if (backpackIcon != null) {
            g2.drawImage(backpackIcon, bagX + 10, bagY + 10, bagSize - 20, bagSize - 20, null);
        } 
    }

    private void drawBar(Graphics2D g2, int x, int y, int w, int h, double value, double max, String label) { // Dessine une barre (PV, bouclier, etc)
        float t = (max <= 0) ? 0f : (float)(value / max);
        t = Math.max(0f, Math.min(1f, t));

        g2.setColor(new Color(0,0,0,140));
        g2.fillRoundRect(x, y, w, h, 12, 12);

        g2.setColor(new Color(120, 200, 120, 220));
        g2.fillRoundRect(x, y, (int)(w * t), h, 12, 12);

        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 12f));
        g2.drawString(label + " : " + (int)value, x + w + 15, y + 13);
    }

    private void handleMousePressed(MouseEvent e) { // Clic sur le sac à dos
        computeLayout();
        if (bagBounds != null && bagBounds.contains(e.getPoint())) {
            if (inventoryOverlay != null) {
                inventoryOverlay.setInventoryVisible(!inventoryOverlay.isInventoryVisible());
                repaint();
            }
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
    }

    @Override
    public boolean contains(int x, int y) { // Clics uniquement si sur le sac à dos
        computeLayout();  //met à jour les positions des éléments
        return bagBounds != null && bagBounds.contains(x, y);
    }
}

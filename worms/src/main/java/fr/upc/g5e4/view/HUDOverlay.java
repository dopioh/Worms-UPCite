package fr.upc.g5e4.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import fr.upc.g5e4.model.GameManager;
import fr.upc.g5e4.model.InventoryModel.InventoryJson;
import fr.upc.g5e4.model.CharacterModel;
import fr.upc.g5e4.util.*;

public class HUDOverlay extends JComponent {

    private PauseOverlay pauseOverlay;
    private final GameManager gameManager;
    private InventoryOverlay inventoryOverlay;

    // --- Weapon HUD ---
    private final java.util.Map<String, BufferedImage> weaponImages = new java.util.HashMap<>();

    private void loadImages() {
        loadImage("ak47", "/ak.png");
        loadImage("grenade", "/grenade.png");
        loadImage("bazooka", "/bazooka.png");
        loadImage("baseballbat", "/baseballbat.png");
        loadImage("PotionSpeed", "/gadgets/potion_speed.png");
        loadImage("PotionJump", "/gadgets/potion_jump.png");
        loadImage("PotionHeal", "/gadgets/potion_heal.png");
        loadImage("PotionShield", "/gadgets/potion_shield.png");
    }

    private void loadImage(String name, String path) {
        java.awt.image.BufferedImage img = fr.upc.g5e4.util.ImageCache.get(path);
        if (img != null) {
            weaponImages.put(name.toLowerCase(), img);
        } else {
            System.err.println("[HUD] image arme introuvable: " + path);
        }
    }
    
    // --- Gadget HUD ---
    private final java.util.Map<String, BufferedImage> gadgetImages = new java.util.HashMap<>();

    private void loadGadgetImages() {
        loadGadgetImage("potion_heal", "/gadgets/potion_heal-sprite.png");
        loadGadgetImage("teleporter", "/gadgets/teleporter-sprite.png");
        loadGadgetImage("drill", "/gadgets/drill-sprite.png");
    }

    private void loadGadgetImage(String name, String path) {
        try {
            gadgetImages.put(name.toLowerCase(), javax.imageio.ImageIO.read(getClass().getResource(path)));
        } catch (Exception e) {
            System.err.println("[HUD] image gadget introuvable: " + path);
        }
    }
    
    // --- Backpack HUD ---

    //Animation de changement de tour 
    private float turnFlashAlpha = 0f;   // 0 et 1 pour la luminosité
    private long turnFlashStart = 0L;
    private static final long TURN_FLASH_DURATION = 300; //en ms 

    /**
     * Active l'animation changement de tour
     */
    public void triggerTurnFlash() {
        turnFlashAlpha = 1f;
        turnFlashStart = System.currentTimeMillis();
        repaint();
    }

    //ParametresHUD
    private Rectangle gearBounds;
    private BufferedImage settingsIcon;

    //Backpack HUD
    private BufferedImage backpackIcon;

    private void loadBackpack() {
        backpackIcon = ImageCache.get("/backpack.png");
        if (backpackIcon == null) {
            System.err.println("[HUD] backpack.png introuvable");
        }
    }
    private void loadSettingsIcon() {
        settingsIcon = ImageCache.get("/parametre.png");
        if (settingsIcon == null) {
            System.err.println("[HUD] parametre.png introuvable");
        }
    }

    //Timer
    private int timerRemainingFrames = 0;
    private int timerTotalFrames = 1;

    /**
     * Appelé depuis GameLoop à chaque tick (60 FPS) pour une barre fluide.
     */
    public void setTimerFrames(int remainingFrames, int totalFrames) {
        this.timerRemainingFrames = Math.max(0, remainingFrames);
        this.timerTotalFrames = Math.max(1, totalFrames);
        repaint();
    }

    // Bouton sac
    private Rectangle bagBounds;
    private Rectangle endTurnBounds;

    // Layout constants
    private static final int HUD_W = 520;
    private static final int HUD_H = 95;

    public HUDOverlay(GameManager gm) {
        this.gameManager = gm;
        setOpaque(false);

        loadBackpack();
        loadGadgetImages();
        loadSettingsIcon();
        loadImages();

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }
        });
    
    }

    public void setInventoryOverlay(InventoryOverlay inv) {
        this.inventoryOverlay = inv;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!(g instanceof Graphics2D g2)) return;

        // rendu plus propre
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Bouton pour les parametres
        int gearSize = 44;
        int gearX = 18;
        int gearY = 18;
        gearBounds = new Rectangle(gearX, gearY, gearSize, gearSize);

        g2.setColor(new Color(255,255,255,180));
        g2.fillRoundRect(gearX, gearY, gearSize, gearSize, 14, 14);

        g2.setColor(new Color(0,0,0,90));
        g2.drawRoundRect(gearX, gearY, gearSize, gearSize, 14, 14);

        if (settingsIcon != null) {
            g2.drawImage(settingsIcon, gearX + 8, gearY + 8, gearSize - 16, gearSize - 16, null);
        }

        //Dimensions HUD
        int hudX = (w - HUD_W) / 2;
        int hudY = h - HUD_H - 35;

        //Timer
        int timerW = HUD_W;
        int timerH = 14;
        int timerX = hudX;
        int timerY = hudY - timerH - 10;

        float t = timerRemainingFrames / (float) timerTotalFrames;
        t = Math.max(0f, Math.min(1f, t));

        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillRoundRect(timerX, timerY, timerW, timerH, 12, 12);

        g2.setColor(new Color(255, 120, 140, 220));
        g2.fillRoundRect(timerX, timerY, (int) (timerW * t), timerH, 12, 12);

        // label en secondes
        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 12f));
        int secondsLeft = (int) Math.ceil(timerRemainingFrames / 60.0);
        g2.drawString(secondsLeft + "s", timerX + timerW + 10, timerY + 12);

        // ---------- Message arme verrouillée ----------
        InventoryJson equippedItem = (inventoryOverlay != null) ? inventoryOverlay.getEquippedItem() : null;
        if (equippedItem != null && equippedItem.useThisTurn > 0 && "ARMES".equalsIgnoreCase(equippedItem.objectType)) {
            g2.setColor(new Color(255, 50, 50));  // Red warning color
            g2.setFont(g2.getFont().deriveFont(java.awt.Font.BOLD, 18f));
            String warningText = "Arme verrouillée!";
            java.awt.FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(warningText);
            g2.drawString(warningText, (w - textW) / 2, hudY - 20);
        }

        // ---------- Fond HUD ----------
        g2.setColor(new Color(255,255,255,235));
        g2.fillRoundRect(hudX, hudY, HUD_W, HUD_H, 18, 18);
        g2.setColor(new Color(0,0,0,120));
        g2.drawRoundRect(hudX, hudY, HUD_W, HUD_H, 18, 18); 

        g2.setColor(new Color(0, 0, 0, 70));
        g2.fillRoundRect(hudX + 3, hudY + 4, HUD_W, HUD_H, 18, 18);

        g2.setColor(new Color(20, 20, 20, 150));
        g2.fillRoundRect(hudX, hudY, HUD_W, HUD_H, 18, 18);

        g2.setColor(new Color(255, 255, 255, 55));
        g2.drawRoundRect(hudX, hudY, HUD_W, HUD_H, 18, 18);

        //PV et Shield
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
            } catch (Exception ignored) {}
        }

        // Barre de PV et Shield
        int barX = hudX + 25;
        int barW = 260;
        int barH = 12;

        int hpY = hudY + 22;
        int shY = hudY + 48;

        drawBar(g2, barX, shY, barW, barH, hp, hpMax, "PV", new Color(120, 200, 120, 220));
        drawBar(g2, barX, hpY, barW, barH, shield, shieldMax, "Bouclier", new Color(120, 170, 255, 220));

        //Armes affichée dans l'HUD
        int weaponBoxW = 170;
        int weaponBoxH = 60;
        int weaponBoxX = hudX + HUD_W - weaponBoxW - 18;
        int weaponBoxY = hudY + (HUD_H - weaponBoxH) / 2;

        g2.setColor(new Color(255, 255, 255, 220));
        g2.fillRoundRect(weaponBoxX, weaponBoxY, weaponBoxW, weaponBoxH, 14, 14);
        g2.setColor(new Color(0, 0, 0, 90));
        g2.drawRoundRect(weaponBoxX, weaponBoxY, weaponBoxW, weaponBoxH, 14, 14);

        String weaponName = null;
        InventoryJson eq = (inventoryOverlay != null) ? inventoryOverlay.getEquippedItem() : null;

        if (eq != null) {
            weaponName = eq.name;
        } else if (gameManager != null && gameManager.getActiveWormController() != null
                && gameManager.getActiveWormController().getCurrentWeapon() != null) {
            weaponName = gameManager.getActiveWormController().getCurrentWeapon().getName();
        }

        if (weaponName != null) {
            String key = weaponName.toLowerCase();
            BufferedImage wi = weaponImages.get(key);

            if (wi != null) {
                g2.drawImage(wi, weaponBoxX + 10, weaponBoxY + 8, 44, 44, null);
            }

            g2.setColor(Color.BLACK);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 12f));
            g2.drawString(weaponName, weaponBoxX + 60, weaponBoxY + 22);

            // ammo text style "nouveau code"
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 13f));
            g2.setColor(new Color(210, 50, 50));

            String ammoText;
            if (eq == null) {
                ammoText = "--";
            } else if (eq.isInfinite || eq.remainingUses == null) {
                ammoText = "∞";
            } else {
                ammoText = String.valueOf(eq.getRemainingUseThisTurn() +"/"+ eq.maxUsages+ " Max:"+eq.remainingUses);
            }
            g2.drawString(ammoText, weaponBoxX + 60, weaponBoxY + 45);

        } else {
            g2.setColor(Color.DARK_GRAY);
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 12f));
            g2.drawString("Aucune arme", weaponBoxX + 22, weaponBoxY + 35);
        }

        // Sac à dos a droite
        int bagSize = 70;
        int bagX = hudX + HUD_W + 20;
        int bagY = hudY + (HUD_H - bagSize) / 2;

        bagBounds = new Rectangle(bagX, bagY, bagSize, bagSize);

        g2.setColor(new Color(255, 255, 255, 170));
        g2.fillRoundRect(bagX, bagY, bagSize, bagSize, 18, 18);
        g2.setColor(new Color(0, 0, 0, 120));
        g2.drawRoundRect(bagX, bagY, bagSize, bagSize, 18, 18);

        if (backpackIcon != null) {
            g2.drawImage(backpackIcon, bagX + 10, bagY + 10, bagSize - 20, bagSize - 20, null);
        }

        int btnW = 140;
        int btnH = 44;
        int btnX = hudX - btnW - 20; 
        int btnY = hudY + (HUD_H - btnH) / 2;

        endTurnBounds = new Rectangle(btnX, btnY, btnW, btnH);

        g2.setColor(new Color(255, 160, 90, 220)); // Pour le hover c'est pas mal  new Color(255, 140, 70, 200));
        g2.fillRoundRect(btnX, btnY, btnW, btnH, 16, 16);

        g2.setColor(new Color(0,0,0,140));
        g2.drawRoundRect(btnX, btnY, btnW, btnH, 16, 16);

        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));
        g2.drawString("Fin de tour", btnX + 18, btnY + 28);

        if (turnFlashAlpha > 0f) {
            long elapsed = System.currentTimeMillis() - turnFlashStart;
            float x = elapsed / (float) TURN_FLASH_DURATION;

            turnFlashAlpha = Math.max(0f, 1f - x);

            Graphics2D g2fx = (Graphics2D) g.create();
            g2fx.setComposite(
                AlphaComposite.getInstance(AlphaComposite.SRC_OVER, turnFlashAlpha * 0.35f)
            );
            g2fx.setColor(Color.WHITE);
            g2fx.fillRect(0, 0, getWidth(), getHeight());
            g2fx.dispose();

            repaint();
        }
    }

    private void drawBar(Graphics2D g2, int x, int y, int w, int h,
                         double value, double max, String label, Color fillColor) {
        float t = (max <= 0) ? 0f : (float) (value / max);
        t = Math.max(0f, Math.min(1f, t));

        // track
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(x, y, w, h, 12, 12);

        // fill
        g2.setColor(fillColor);
        g2.fillRoundRect(x, y, (int) (w * t), h, 12, 12);

        // text inside bar 
        g2.setColor(new Color(255, 255, 255, 220));
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 12f));
        g2.drawString(label + " : " + (int) value, x + 8, y + 11);
    }

    private void handleMousePressed(MouseEvent e) {
        computeLayout();
        if (bagBounds != null && bagBounds.contains(e.getPoint())) {
            if (inventoryOverlay != null) {
                inventoryOverlay.setInventoryVisible(!inventoryOverlay.isInventoryVisible());
                repaint();
                return;
            }
        }
        if (gearBounds != null && gearBounds.contains(e.getPoint())) {
            if (pauseOverlay != null) {
                pauseOverlay.setPauseVisible(true);
                if (gameManager != null && gameManager.getGameLoop() != null) {
                    gameManager.getGameLoop().pauseTimer();
                }
                repaint();
                return;
            }
        }
        if (endTurnBounds != null && endTurnBounds.contains(e.getPoint())) {
                if(gameManager != null) gameManager.nextRound();
                repaint();
                return;
            }
        }

    private void computeLayout() {
        int w = getWidth();
        int h = getHeight();

        int hudX = (w - HUD_W) / 2;
        int hudY = h - HUD_H - 35;

        int bagSize = 70;
        int bagX = hudX + HUD_W + 20;
        int bagY = hudY + (HUD_H - bagSize) / 2;

        bagBounds = new Rectangle(bagX, bagY, bagSize, bagSize);

        int gearSize = 44;
        int gearX = 18;
        int gearY = 18;

        gearBounds = new Rectangle(gearX, gearY, gearSize, gearSize);

        int btnW = 140;
        int btnH = 44;
        int btnX = hudX - btnW - 20;
        int btnY = hudY + (HUD_H - btnH) / 2;

        endTurnBounds = new Rectangle(btnX, btnY, btnW, btnH);
    }


    public void setPauseOverlay(PauseOverlay p) {
        this.pauseOverlay = p;
    }

    @Override
    public boolean contains(int x, int y) {
        computeLayout();
        return (bagBounds != null && bagBounds.contains(x, y)) || (gearBounds != null && gearBounds.contains(x, y)) || (endTurnBounds != null && endTurnBounds.contains(x, y));
    }
}


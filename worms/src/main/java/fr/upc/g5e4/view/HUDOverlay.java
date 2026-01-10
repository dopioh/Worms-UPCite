package fr.upc.g5e4.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import fr.upc.g5e4.model.GameManager;
import fr.upc.g5e4.model.InventoryModel.InventoryJson;
import fr.upc.g5e4.model.CharacterModel;
import fr.upc.g5e4.controller.CameraController;
import fr.upc.g5e4.util.ImageCache;

/**
 * Overlay affichant les informations de jeu (HUD)
 * (Timer, PV, Bouclier, Arme équipée, Bouton Fin de tour, Bouton Sac à dos, Bouton Paramètres, Flèches de caméra)
 * 
 * Ce fichier est une **view interactive** qui relaie des actions vers`GameManager`, `PauseOverlay`, `InventoryOverlay` et `CameraController`.
 * @author Hugo
 */
public class HUDOverlay extends JComponent {

    private PauseOverlay pauseOverlay;
    private final GameManager gameManager;
    private InventoryOverlay inventoryOverlay;
    private CameraController cameraController;

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


    //Variables HUD clic et/ou Image
    //Parametres HUD
    private Rectangle settingsBounds;
    private BufferedImage settingsIcon;

    //Backpack HUD 
    private BufferedImage backpackIcon;
    private Rectangle bagBounds;

    //Zone cliquable de Fin de tour
    private Rectangle endTurnBounds;

    //Camera Arrows HUD
    private BufferedImage fRightIcon;
    private BufferedImage fLeftIcon;
    private BufferedImage centerCamIcon;

    private Rectangle centerCamBounds;
    private Rectangle leftArrowBounds;
    private Rectangle rightArrowBounds;
    
    // Chargement de tous les icones ----------------------------------------------------------
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

    private void loadCameraArrowsIcon() {
        fRightIcon = ImageCache.get("/fleche-droite.png");
        fLeftIcon = ImageCache.get("/fleche-gauche.png");
        if (fLeftIcon == null) {
            System.err.println("[HUD] fleche-gauche.png introuvable");
        }
        if (fRightIcon == null) {
            System.err.println("[HUD] fleche-droite.png introuvable");
        }
    }

    private void loadCenterCamIcon() {
        centerCamIcon = ImageCache.get("/centercam.png");
        if (centerCamIcon == null) {
            System.err.println("[HUD] centercam.png introuvable");
        }
    }

    //Timer -----------------------------------------------------------
    private int timerRemainingFrames = 0;
    private int timerTotalFrames = 1;

    /**
     * Appelé depuis GameLoop à chaque tick 
     * pour mettre a jour le timer affiché dans le HUD
     */
    public void setTimerFrames(int remainingFrames, int totalFrames) {
        this.timerRemainingFrames = Math.max(0, remainingFrames);
        this.timerTotalFrames = Math.max(1, totalFrames);
        repaint();
    }

    // Layout constants
    private static final int HUD_W = 520;
    private static final int HUD_H = 95;

    /** Constructeur initialisant l'overlay HUD sur le gameManager gm*/
    public HUDOverlay(GameManager gm) {
        this.gameManager = gm;
        setOpaque(false);

        loadBackpack();
        loadSettingsIcon();
        loadImages();
        loadCameraArrowsIcon();
        loadCenterCamIcon();

        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Hover newHover = hitTest(e.getPoint());
                if (newHover != hovered) {
                    hovered = newHover;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (hovered != Hover.NONE) {
                    hovered = Hover.NONE;
                    repaint();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (cameraController != null) {
                    cameraController.deactivateLeft();
                    cameraController.deactivateRight();
                }
            }
        };

        addMouseMotionListener(mouse);
        addMouseListener(mouse);
    }

    /**
     * Définit l'overlay d'inventaire 
     * @param inv
     */
    public void setInventoryOverlay(InventoryOverlay inv) {
        this.inventoryOverlay = inv;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!(g instanceof Graphics2D g2)) return;

        // rendu plus propre (lissage des textes et formes)
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Bouton pour les parametres
        int settingsSize = 44;
        int settingsX = 18;
        int settingsY = 18;
        settingsBounds = new Rectangle(settingsX, settingsY, settingsSize, settingsSize);
        boolean hoversettings = (hovered == Hover.settings);

        g2.setColor(hoversettings ? new Color(255,255,255,210) : new Color(255,255,255,180));
        g2.fillRoundRect(settingsX, settingsY, settingsSize, settingsSize, 14, 14);

        g2.setColor(hoversettings ? new Color(0,0,0,130) : new Color(0,0,0,90));
        g2.drawRoundRect(settingsX, settingsY, settingsSize, settingsSize, 14, 14);

        if (settingsIcon != null) {
            g2.drawImage(settingsIcon, settingsX + 8, settingsY + 8, settingsSize - 16, settingsSize - 16, null);
        }

        //Dimensions HUD
        int hudX = (w - HUD_W) / 2;
        int hudY = h - HUD_H - 35;

        //Timer
        int timerW = HUD_W;
        int timerH = 14;
        int timerX = hudX;
        int timerY = hudY - timerH - 10;

        if (!timerHidden) {
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
        }

        // Taille des fleches
        int arrowSize = 34;
        int arrowPad  = 10;
        int arrowY    = timerY - arrowSize - 8;

        int centerX = timerX + (timerW / 2);

        // Positions des éléments
        int leftX = centerX - arrowSize * 2 - arrowPad;
        int centerXBtn = centerX - arrowSize / 2;
        int rightX = centerX + arrowSize + arrowPad;

        leftArrowBounds   = new Rectangle(leftX, arrowY, arrowSize, arrowSize);
        centerCamBounds   = new Rectangle(centerXBtn, arrowY, arrowSize, arrowSize);
        rightArrowBounds  = new Rectangle(rightX, arrowY, arrowSize, arrowSize);
        boolean hL = hovered == Hover.CAM_LEFT;
        boolean hC = hovered == Hover.CAM_CENTER;
        boolean hR = hovered == Hover.CAM_RIGHT;

        g2.setColor(hL ? new Color(255,255,255,220) : new Color(255,255,255,180));
        g2.fillRoundRect(leftX, arrowY, arrowSize, arrowSize, 12, 12);

        g2.setColor(hC ? new Color(255,255,255,220) : new Color(255,255,255,180));
        g2.fillRoundRect(centerXBtn, arrowY, arrowSize, arrowSize, 12, 12);

        g2.setColor(hR ? new Color(255,255,255,220) : new Color(255,255,255,180));
        g2.fillRoundRect(rightX, arrowY, arrowSize, arrowSize, 12, 12);

        // images
        if (fLeftIcon != null) {
            g2.drawImage(fLeftIcon, leftX + 6, arrowY + 6, arrowSize - 12, arrowSize - 12, null);
        }
        if (centerCamIcon != null) {
            g2.drawImage(centerCamIcon, centerXBtn + 6, arrowY + 6, arrowSize - 12, arrowSize - 12, null);
        }
        if (fRightIcon != null) {
            g2.drawImage(fRightIcon, rightX + 6, arrowY + 6, arrowSize - 12, arrowSize - 12, null);
        }

        // Fond HUD
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
        boolean hoverBag = (hovered == Hover.BAG);

        g2.setColor(hoverBag ? new Color(255, 255, 255, 210) : new Color(255, 255, 255, 170));
        g2.fillRoundRect(bagX, bagY, bagSize, bagSize, 18, 18);

        g2.setColor(hoverBag ? new Color(0, 0, 0, 160) : new Color(0, 0, 0, 120));
        g2.drawRoundRect(bagX, bagY, bagSize, bagSize, 18, 18);

        if (backpackIcon != null) {
            g2.drawImage(backpackIcon, bagX + 10, bagY + 10, bagSize - 20, bagSize - 20, null);
        }

        //Fin de tour 
        int btnW = 140;
        int btnH = 44;
        int btnX = hudX - btnW - 20; 
        int btnY = hudY + (HUD_H - btnH) / 2;

        endTurnBounds = new Rectangle(btnX, btnY, btnW, btnH);
        boolean hoverEnd = (hovered == Hover.END_TURN);

        g2.setColor(hoverEnd ? new Color(255, 175, 110, 240) : new Color(255, 140, 70, 200));
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

        // Barre de fond
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(x, y, w, h, 12, 12);

        // Remplissage de la barre
        g2.setColor(fillColor);
        g2.fillRoundRect(x, y, (int) (w * t), h, 12, 12);

        // Texte 
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
        if (settingsBounds != null && settingsBounds.contains(e.getPoint())) {
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
        
        if (leftArrowBounds != null && leftArrowBounds.contains(e.getPoint())) {
            if (cameraController != null) cameraController.activateLeft();
            repaint();
            return;
        }

        if (rightArrowBounds != null && rightArrowBounds.contains(e.getPoint())) {
            if (cameraController != null) cameraController.activateRight();
            repaint();
            return;
        }

        if (centerCamBounds != null && centerCamBounds.contains(e.getPoint())) {
            if (cameraController != null) {
                cameraController.centerOnCharacter();
            }
            repaint();
            return;
        }
    }
    
    /*Calcule toutes les zones cliquables */
    private void computeLayout() {
        int w = getWidth();
        int h = getHeight();

        int hudX = (w - HUD_W) / 2;
        int hudY = h - HUD_H - 35;

        int bagSize = 70;
        int bagX = hudX + HUD_W + 20;
        int bagY = hudY + (HUD_H - bagSize) / 2;

        int settingsSize = 44;
        int settingsX = 18;
        int settingsY = 18;

        int btnW = 140; // Bouton Fin de tour
        int btnH = 44;
        int btnX = hudX - btnW - 20;
        int btnY = hudY + (HUD_H - btnH) / 2;

        int timerW = HUD_W;
        int timerH = 14;
        int timerX = hudX;
        int timerY = hudY - timerH - 10;

        int arrowSize = 34;
        int arrowPad  = 10;
        int arrowY    = timerY - arrowSize - 8;

        int centerX = timerX + (timerW / 2);
        int leftX = centerX - arrowSize * 2 - arrowPad;
        int centerXBtn = centerX - arrowSize / 2;
        int rightX = centerX + arrowSize + arrowPad;
        
        bagBounds = new Rectangle(bagX, bagY, bagSize, bagSize);
        settingsBounds = new Rectangle(settingsX, settingsY, settingsSize, settingsSize);
        endTurnBounds = new Rectangle(btnX, btnY, btnW, btnH);
        leftArrowBounds  = new Rectangle(leftX, arrowY, arrowSize, arrowSize);
        centerCamBounds  = new Rectangle(centerXBtn, arrowY, arrowSize, arrowSize);
        rightArrowBounds = new Rectangle(rightX, arrowY, arrowSize, arrowSize);
    }

    /**
     * Définit l'overlay de pause
     * @param p overlay de pause
     */
    public void setPauseOverlay(PauseOverlay p) {
        this.pauseOverlay = p;
    }

    /**
     * Définit le contrôleur de caméra
     * @param cam contrôleur de caméra
     */
    public void setCameraController(CameraController cam){ 
        this.cameraController = cam;
    }

    /**
     * Empêche les clics lorsque le bouton n'est pas affiché
     * @param x,y coordonnées du clic
     * @return true si le clic est dans l'overlay visible, false sinon
     */
    @Override
    public boolean contains(int x, int y) {
        computeLayout();
        return (bagBounds != null && bagBounds.contains(x, y)) || (settingsBounds != null && settingsBounds.contains(x, y)) || (endTurnBounds != null && endTurnBounds.contains(x, y)) 
        || (leftArrowBounds != null && leftArrowBounds.contains(x, y)) || (rightArrowBounds != null && rightArrowBounds.contains(x, y)) || (centerCamBounds != null && centerCamBounds.contains(x, y));
    }


    //HOVER pour chaque éléments
    private enum Hover {NONE,settings,BAG,END_TURN,CAM_LEFT,CAM_CENTER,CAM_RIGHT}

    private Hover hovered = Hover.NONE;

    //Détermine quel élément est survolé par la souris
    private Hover hitTest(Point p) {
        computeLayout();
        if (settingsBounds != null && settingsBounds.contains(p)) return Hover.settings;
        if (bagBounds != null && bagBounds.contains(p)) return Hover.BAG;
        if (endTurnBounds != null && endTurnBounds.contains(p)) return Hover.END_TURN;
        if (leftArrowBounds != null && leftArrowBounds.contains(p)) return Hover.CAM_LEFT;
        if (centerCamBounds != null && centerCamBounds.contains(p)) return Hover.CAM_CENTER;
        if (rightArrowBounds != null && rightArrowBounds.contains(p)) return Hover.CAM_RIGHT;
        return Hover.NONE;
    }

    private boolean timerHidden = false;

    /**
     * Cache ou affiche le timer
     * @param hidden
     */
    public void setTimerHidden(boolean hidden) {
        this.timerHidden = hidden;
        repaint();
    }

}   


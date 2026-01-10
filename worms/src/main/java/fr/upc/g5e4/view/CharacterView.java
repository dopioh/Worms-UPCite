package fr.upc.g5e4.view;

import java.awt.*;
import java.awt.image.BufferedImage;

import org.jbox2d.common.Vec2;
import fr.upc.g5e4.util.ImageCache;
import org.jbox2d.dynamics.Body;

import fr.upc.g5e4.model.CharacterModel;
import fr.upc.g5e4.model.Weapon;

import fr.upc.g5e4.model.weapons.Grenade;
import fr.upc.g5e4.model.weapons.RocketLauncher;

/**
 * Vue pour l'affichage graphique
 * @author Nassim & Billy & Ian
 */
public class CharacterView {
    private int size;
    private Color color;
    private NameTagView nameTagView;
    private boolean showNameTag = true;
    private BufferedImage spriteImage; 
    private boolean useSprite; // utile pour savoir si on dessine un carré ou le sprite
    private Weapon currentWeapon; // arme actuellement équipée
    private BufferedImage weaponSprite; // sprite de l'arme
    private String currentGadget; // gadget actuellement équipé 
    private BufferedImage gadgetSprite; // sprite du gadget
    
    // Animation de marche
    private double walkTime = 0;
    
    /**
     * Constructeur de la classe PersonnageView avec couleur, taille et sprite par defaut
     */
    public CharacterView() {
        this.size = 15;
        this.color = new Color(255, 150, 0); // Orange
        this.nameTagView = new NameTagView(color);
    }
    
    /**
     * Constructeur de la classe PersonnageView avec taille et couleur personnaliséé
     * @param size La taille du worm
     * @param color La couleur du worm
     */
    public CharacterView(int size, Color color) {
        this.size = size;
        this.color = color;
        this.nameTagView = new NameTagView(color);
    }
    
    /**
     * Constructeur de la classe PersonnageView avec sprite custom
     * @param size La taille du worm
     * @param color La couleur du worm
     * @param spritePath le chemin du sprite dans /resources (ex: "/worm_blue.png")
     */
    public CharacterView(int size, Color color, String spritePath) {
        this.size = size;
        this.color = color;
        this.nameTagView = new NameTagView(color);
        loadSprite(spritePath);
    }
    
    /**
     * fonction qui charge le sprite du worm
     * @param path le chemin du sprite dans resources
     */
    private void loadSprite(String path) {
        spriteImage = ImageCache.get(path);
        if (spriteImage != null) {
            useSprite = true;
        } else {
            System.err.println("Impossible de charger le sprite: " + path);
            System.err.println("(utilisation d'un carré aux couleurs de l'equipe)");
            useSprite = false;
        }
    }
    
    /**
     * Dessine le model du worm à l'écran
     * @param g2d Le contexte graphique
     * @param model Le modèle du worm à dessiner
     */
    public void draw(Graphics2D g2d, CharacterModel model) {
        drawBody(g2d, model);
        drawStats(g2d, model);
    }
    
    /**
     * Dessine le corps du worm (sprite ou carré si pb de sprite)
     * On se base sur son body
     * @param g2d Le contexte graphique
     * @param model Le model du worm
     */
    private void drawBody(Graphics2D g2d, CharacterModel model) {
        if (model.isDead()) return;
        
        Body body = model.getBody();
        Vec2 pos = body.getPosition();
        Vec2 vel = body.getLinearVelocity();
    
        int x = (int)(pos.x * CharacterModel.getPPM());
        int y = (int)((24 - pos.y) * CharacterModel.getPPM());
    
        // Animation walk
        boolean isWalking = Math.abs(vel.x) > 0.5f && model.isOnGround();
        if (isWalking) walkTime += 0.25; else walkTime = 0;
        
        double yOffset = isWalking ? Math.sin(walkTime) * 2 : 0;       //mouvement vertical avec courbe sinus repetant entre -1 et 1
        double angle = isWalking ? Math.sin(walkTime * 0.5) * 5 : 0;
    
        Color drawColor = getColorWithStatusEffect(model);
        Graphics2D g2dCopy = (Graphics2D) g2d.create();
        
        if (useSprite && spriteImage != null) {
            // dessiner le sprite
            int spriteWidth = size+10;
            int spriteHeight = size+10;
            
            //clone le sprite et le colorie après un effet
            BufferedImage sprite = spriteImage;
            if (model.getCurrentStatus() != CharacterModel.StatusEffect.NONE) {
                sprite = colorizeSprite(spriteImage, drawColor);
            }
            
            g2dCopy.translate(x, y - spriteHeight/2 + yOffset);
            g2dCopy.rotate(Math.toRadians(model.isFacingRight() ? angle : -angle), 0, spriteHeight/2); //rotation (penche) vers la direction, point de rotation au centre de worm
            if (!model.isFacingRight()) g2dCopy.scale(-1, 1);
            g2dCopy.drawImage(sprite, -spriteWidth/2, 0, spriteWidth, spriteHeight, null);
        } else { // worm en carré
            g2dCopy.setColor(drawColor);
            g2dCopy.translate(x, y + yOffset);
            g2dCopy.rotate(Math.toRadians(angle), 0, 0);
            g2dCopy.fillRect(-size/2, -size/2, size, size);
        }
        
        g2dCopy.dispose();
        // on dessine l'arme si le worm en est equipé
        drawWeapon(g2d, model, x, (int)(y + yOffset));
        // on dessine le gadget si le worm en est equipe
        drawGadget(g2d, model, x, (int)(y + yOffset));
    }
    
    /**
     * Dessine le sprite de l'arme équipée sur le worm
     * @param g2d le contexte graphique
     * @param model le modèle du worm
     * @param x Position X du worm en pixels
     * @param y Position Y du worm en pixels
     */
    private void drawWeapon(Graphics2D g2d, CharacterModel model, int x, int y) {
        if (currentWeapon == null || weaponSprite == null) {
            return;
        }
        
        // init taille de l'arme
        int weaponWidth = size;
        int weaponHeight = size;
        int weaponX, weaponY = 0;

        // exeption grenade / rocketlauncher
        if (currentWeapon instanceof Grenade) {
            // reduire taille de la grenade
            weaponWidth = (int)(size * 0.9);
            weaponHeight = (int)(size * 0.9);
            weaponY += 5;
        } else if (currentWeapon instanceof RocketLauncher) {
            // allonger largeur du lance-roquettes
            weaponWidth = (int)(size * 1.8);
            weaponHeight = size;
        }
        
        // definir position de l'arme (a gauche ou a droite selon la direction)
        if (model.isFacingRight()) {
            weaponX = (x-15) + size/2;
            weaponY += (y-5) - size/4;
        } else {
            weaponX = (x+15) - size/2 - weaponWidth;
            weaponY += (y-5) - size/4;
        }
        
        // on cree un nouveau contexte graphique pour dessiner l'arme
        // sans copie du context graphique on inverserait tout (je crois) les dessin du jeu 
        // avec la copie on inverse juste le sprite de l'arme
        // on libere ensuite la copie pour une meilleure gestion des ressources
        Graphics2D g2dCopy = (Graphics2D) g2d.create();
        
        // si le worm "regarde" a gauche on reverse juste le sprite
        if (!model.isFacingRight()) {
            g2dCopy.translate(weaponX + weaponWidth, weaponY);
            g2dCopy.scale(-1, 1);
            g2dCopy.drawImage(weaponSprite, 0, 0, weaponWidth, weaponHeight, null);
        } else {
            g2dCopy.drawImage(weaponSprite, weaponX, weaponY, weaponWidth, weaponHeight, null);
        }
        
        g2dCopy.dispose();
    }
    
    /**
     * dessine le sprite du gadget équipé sur le worm (similaire à drawWeapon)
     * @param g2d le contexte graphique
     * @param model le modèle du worm
     * @param x Position X du worm en pixels
     * @param y Position Y du worm en pixels
     */
    private void drawGadget(Graphics2D g2d, CharacterModel model, int x, int y) {
        if (currentGadget == null || gadgetSprite == null) {
            return;
        }
        
        // init taille du gadget (plus petit que l'arme)
        int gadgetWidth = (int)(size * 0.7);
        int gadgetHeight = (int)(size * 0.7);
        int gadgetX, gadgetY = 0;
        
        // positionner le gadget (à gauche ou à droite selon la direction)
        // placer le gadget sous le worm
        if (model.isFacingRight()) {
            gadgetX = (x-15) + size/2;
            gadgetY += (y-5) + size/2;
        } else {
            gadgetX = (x+15) - size/2 - gadgetWidth;
            gadgetY += (y-5) + size/2;
        }
        
        // créer un nouveau contexte graphique pour dessiner le gadget
        Graphics2D g2dCopy = (Graphics2D) g2d.create();
        
        // si le worm "regarde" à gauche on reverse le sprite
        if (!model.isFacingRight()) {
            g2dCopy.translate(gadgetX + gadgetWidth, gadgetY);
            g2dCopy.scale(-1, 1);
            g2dCopy.drawImage(gadgetSprite, 0, 0, gadgetWidth, gadgetHeight, null);
        } else {
            g2dCopy.drawImage(gadgetSprite, gadgetX, gadgetY, gadgetWidth, gadgetHeight, null);
        }
        
        g2dCopy.dispose();
    }
    
    /**
     * Charge le sprite de l'arme equipee dans la variable weaponSprite
     * @param weapon l'objet arme equipee
     */
    private void loadWeaponSprite(Weapon weapon) {
        if (weapon == null) { // si on a pas d'arme equipee on ne charge rien
            weaponSprite = null;
            return;
        }
        
        // on recup le nom de la classe de l'arme pour créer le chemin du sprite
        String className = weapon.getClass().getSimpleName();
        String spritePath = getWeaponSpritePath(className);
        
        weaponSprite = ImageCache.get(spritePath);
        if (weaponSprite == null) {
            System.err.println("Impossible de charger le sprite d'arme: " + spritePath);
        }
    }
    
    /**
     * Retourne le chemin du sprite pour une arme donnée (ex: "ak47" -> "/sprite_items/ak47-sprite.png")
     * @param className le nom de la classe de l'arme
     * @return le chemin du sprite dans resources/sprite_items/
     */
    private String getWeaponSpritePath(String className) {
        String fileName = className.toLowerCase();
        return "/sprite_items/" + fileName + "-sprite.png";
    }
    
    /**
     * charge le sprite du gadget équipé dans la variable gadgetSprite
     * @param gadgetName le nom du gadget 
     */
    private void loadGadgetSprite(String gadgetName) {
        if (gadgetName == null) { // si on a pas de gadget equipé on ne charge rien
            gadgetSprite = null;
            return;
        }
        
        // on crée le chemin du sprite basé sur le nom du gadget
        String spritePath = getGadgetSpritePath(gadgetName);
        
        gadgetSprite = ImageCache.get(spritePath);
        if (gadgetSprite == null) {
            System.err.println("Impossible de charger le sprite de gadget: " + spritePath);
        }
    }
    
    /**
     * retourne le chemin du sprite pour un gadget donné 
     * @param gadgetName le nom du gadget
     * @return le chemin du sprite dans resources/gadgets/
     */
    private String getGadgetSpritePath(String gadgetName) {
        String fileName = gadgetName.toLowerCase();
        return "/gadgets/" + fileName + "-sprite.png";
    }
    
    /**
     * Affiche les statistiques du worm au-dessus de sa tête
     * @param g2d Le contexte graphique
     * @param model Le modèle du worm
     */
    private void drawStats(Graphics2D g2d, CharacterModel model) {
        if (!showNameTag) {
            nameTagView.setVisible(false);
            return;
        }
        
        if (model.isDead()) {
            nameTagView.setVisible(false);
            return;
        }
        
        // Afficher le NameTag si le personnage est vivant
        nameTagView.setVisible(true);
        
        // Utiliser le nouveau style de nametag
        nameTagView.drawNewStyle(g2d, model);
    }
    
    // Getters et Setters
    public int getSize() {
        return size;
    }
    
    public void setSize(int size) {
        this.size = size;
    }

    public Color getColor() {
        return color;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
    
    public NameTagView getNameTagView() {
        return nameTagView;
    }
    
    public boolean isShowNameTag() {
        return showNameTag;
    }
    
    public void setShowNameTag(boolean showNameTag) {
        this.showNameTag = showNameTag;
    }
    
    /**
     * Retourne l'image du sprite pour l'animation de fin
     * @return L'image du sprite ou null si pas chargé
     */
    public BufferedImage getSpriteImage() {
        return spriteImage;
    }
    
    /**
     * Vérifie si le sprite est chargé et utilisé
     * @return true si le sprite est utilisé
     */
    public boolean isUsingSprite() {
        return useSprite && spriteImage != null;
    }
    
    /**
     * envoie la couleur du worm selon son statut d'effet
     * @param model modèle du worm
     * @return la couleur à utiliser pour le dessin
     */
    private Color getColorWithStatusEffect(CharacterModel model) {
        CharacterModel.StatusEffect status = model.getCurrentStatus();
        float progress = model.getStatusEffect();
        
        Color effectColor;
        switch (status) {
            case DAMAGE:
                // rouge pour les dégâts
                effectColor = new Color(255, 0, 0, (int)(255 * progress));
                break;
            case HEAL:
                // vert pour la guérison
                effectColor = new Color(0, 255, 0, (int)(255 * progress));
                break;
            case SHIELD:
                // cyan pour le shield
                effectColor = new Color(0, 255, 255, (int)(255 * progress));
                break;
            case BUFF:
                // bleu pour les buffs génériques
                effectColor = new Color(0, 100, 255, (int)(255 * progress));
                break;
            case SPEED_BUFF:
                // rose pour le buff speed
                effectColor = new Color(255, 105, 180, (int)(255 * progress));
                break;
            case JUMP_BUFF:
                // jaune pour le buff jump
                effectColor = new Color(255, 255, 0, (int)(255 * progress));
                break;
            default:
                // couleur normale sans effet
                return color;
        }
        
        // mélange la couleur d'effet avec la couleur d'équipe
        int r = (int) (color.getRed() * (1 - progress) + effectColor.getRed() * progress);
        int g = (int) (color.getGreen() * (1 - progress) + effectColor.getGreen() * progress);
        int b = (int) (color.getBlue() * (1 - progress) + effectColor.getBlue() * progress);
        
        return new Color(r, g, b);
    }

    /**
        * Définit l'arme actuellement equipee et charge son sprite dans la variable weaponSprite
     * @param weapon l'arme a equipee (peut etre null)
     */
    public void setCurrentWeaponSprite(Weapon weapon) {
        this.currentWeapon = weapon;
        loadWeaponSprite(weapon);
    }
    
    /**
     * Retourne l'arme actuellement equipee
     * @return l'arme equipee ou null
     */
    public Weapon getCurrentWeaponSprite() {
        return currentWeapon;
    }
    
    /**
     * colorie un sprite en le peignant avec la couleur spécifiée
     * @param originalSprite le sprite original
     * @param tintColor la couleur à appliquer
     * @return une copie du sprite avec la teinte appliquée
     */
    private BufferedImage colorizeSprite(BufferedImage originalSprite, Color tintColor) {
        BufferedImage colorized = new BufferedImage(
            originalSprite.getWidth(),
            originalSprite.getHeight(),
            BufferedImage.TYPE_INT_ARGB
        );
        
        // pourcentage de la couleur
        float tintStrength = 0.7f;  // 70% 
        
        for (int y = 0; y < originalSprite.getHeight(); y++) {
            for (int x = 0; x < originalSprite.getWidth(); x++) {
                int originalRGB = originalSprite.getRGB(x, y);
                
                // extraire les couleurs ARGB originaux
                int alpha = (originalRGB >> 24) & 0xFF;
                int origR = (originalRGB >> 16) & 0xFF;
                int origG = (originalRGB >> 8) & 0xFF;
                int origB = originalRGB & 0xFF;
                
                // appliquer la teinte 
                int newR = (int) (origR * (1 - tintStrength) + tintColor.getRed() * tintStrength);
                int newG = (int) (origG * (1 - tintStrength) + tintColor.getGreen() * tintStrength);
                int newB = (int) (origB * (1 - tintStrength) + tintColor.getBlue() * tintStrength);
                
                // borné les valeurs à [0, 255]
                newR = Math.min(255, Math.max(0, newR));
                newG = Math.min(255, Math.max(0, newG));
                newB = Math.min(255, Math.max(0, newB));
                
                // créer le nouvel ARGB
                int newRGB = (alpha << 24) | (newR << 16) | (newG << 8) | newB;
                colorized.setRGB(x, y, newRGB);
            }
        }
        
        return colorized;

    }
    
    /**
     * définit le gadget actuellement équipé et charge son sprite dans la variable gadgetSprite
     * @param gadgetName le nom du gadget à équiper 
     */
    public void setCurrentGadgetSprite(String gadgetName) {
        this.currentGadget = gadgetName;
        loadGadgetSprite(gadgetName);
    }
    
    /**
     * getter pour le gadget actuellement équipé
     * @return le nom du gadget équipé ou null
     */
    public String getCurrentGadgetSprite() {
        return currentGadget;
    }
}
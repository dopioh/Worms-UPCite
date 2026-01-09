package fr.upc.g5e4.model;

import fr.upc.g5e4.model.InventoryModel.InventoryJson;

import org.jbox2d.collision.shapes.CircleShape;
//import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import fr.upc.g5e4.model.io.CharacterData;
import fr.upc.g5e4.view.CharacterView;

import java.util.ArrayList;
//import java.util.List;

/**
 * Modèle du Personnage
 * Initialise ses stats et position
 */
public class CharacterModel{
    private static int wormsCounter = 0;

    private final GroundModel model;
    private final World world;

    private int ID;
    private String name;

    private double xcord;   // En pixel
    private double ycord;


    private double health;
    private double shield;
    private double speed;
    private double jump;
    
    private boolean isDead;
    private boolean facingRight;  // true si le worm regarde vers la droite
    private boolean withCollision = false;  // collisions entre worms activées ou non
    
    private long lastJumpTime = 0;  // Temps du dernier saut pour le cooldown
    private static final double JUMP_COOLDOWN = 0.5;  // Cooldown du saut en secondes
    
    private boolean flyMode = false;  // Mode vol activé ou non

    private static float PPM = 30;

    private final float m_HeightPerso = 0.6f;   // tous les worms font la meme taille en metre
    private final float m_WidthPerso = 0.6f;

    // Catégories de collision (chaque bit = une catégorie différente)
    public static final int CATEGORY_TERRAIN = 0x0001;  // Bit 1 : terrain/sol
    public static final int CATEGORY_WORM = 0x0002;     // Bit 2 : worms

    private final Body persoBody;

    //Armes équipées
    private InventoryJson equippedItem;

    // cela permet de voir le statut actuel du perso
    public enum StatusEffect { NONE, DAMAGE, HEAL, SHIELD, BUFF, SPEED_BUFF, JUMP_BUFF }
    private StatusEffect currentStatus = StatusEffect.NONE;
    private long statusStartTime = 0;
    private static final long STATUS_DURATION = 1000; // en milliseconde 
    
    // buffs temporaires - support multiple buffs simultanés
    private ArrayList<BuffModel> activeBuffs = new ArrayList<>();
    
    // Référence optionnelle à la vue du worm pour afficher les icônes de buff
    private fr.upc.g5e4.view.CharacterView characterView = null;

    //private static final int sizePerso = 40;

    /**
     * Constructeur de la classe PersonnageModel
     * 
     * @param xcord La coordonnée x du worm
     * @param ycord La coordonnée y du worm
     */
    public CharacterModel(GroundModel model, World world, double xcord, double ycord, boolean console) {
        this.model = model;
        this.world = world;
        this.ID = wormsCounter++;
        this.name = "Worm-" + this.ID;
        this.xcord = xcord;
        //System.out.println("this.xcord : " + this.xcord);
        //System.out.println("xcord : " + xcord);
        this.ycord = ycord;
        this.health = 100.0;
        this.shield = 50.0;
        this.speed = 4;  // vitesse de base
        this.jump = 5.0;
        this.isDead = false;
        this.facingRight = true;
        
        this.persoBody = createBody(console);
    }

    /** 
     * Constructeur de la classe PersonnageModel avec paramètres personnalisés
     * 
     * @param name Le nom du worm
     * @param xcord La coordonnée x du worm
     * @param ycord La coordonnée y du worm
     * @param health La santé du worm
     * @param shield Le bouclier du worm
     * @param speed La vitesse du worm
     * @param jump Le saut du worm
     * @param console Mode console
     * @param withCollision Active les collisions entre worms
     */
    public CharacterModel(GroundModel model, World world,String name, double xcord, double ycord, double health, double shield, double speed, double jump, boolean console, boolean withCollision) {
        this.model = model;
        this.world = world;
        this.ID = wormsCounter++;
        this.name = name + "-" + this.ID;
        this.xcord = xcord;
        this.ycord = ycord;
        this.health = health;
        this.shield = shield;
        this.speed = speed;
        this.jump = jump;
        this.isDead = false;
        this.facingRight = true;
        this.withCollision = withCollision;

        this.persoBody = createBody(console);
    }

    /**
     * Constructeur pour le chargement d'une sauvegarde (parametres adapte)
     * @param model GroundModel (terrain donnes)
     * @param world Physique du jeu
     * @param ID ID unique du Worm
     * @param name nom du worm
     * @param xcord position x actuelle
     * @param ycord position y actuelle
     * @param health vie actuelle 
     * @param shield shield actuelle 
     * @param isDead true si le worm est mort
     * @param facingRight true si il regarde a droite
     */
    public CharacterModel(GroundModel model, World world,int ID, String name, double xcord, double ycord, double health, double shield, boolean isDead, boolean facingRight, double speed, double jump, ArrayList<BuffModel> activeEffects){
        this.model = model;
        this.world = world;
        this.ID = ID;
        this.name = name;
        this.xcord = xcord;
        this.ycord = ycord;
        this.health = health;
        this.shield = shield;
        this.speed = speed;
        this.jump = jump;
        this.withCollision = false;
        this.isDead = isDead;
        this.facingRight = facingRight;
        this.activeBuffs = activeEffects;

        if (isDead){
            this.persoBody = null;
        } else {
            this.persoBody = createBody(false);
        }
    }

    /**
     * methode toString de la classe PersonnageModel
     * 
     * @return Un String avec affichage des attributs du worm
     */
    @Override
    public String toString() {
        return "[" + name + "]" + " \n* Position: (" + xcord + ", " + ycord + 
               ") \n* Health: " + health + " \n* Shield: " + shield + 
               " \n* Speed: " + speed + " \n* Jump: " + jump + 
               " \n* Is Dead: " + isDead + "\n";
    }

    // Getters
    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public double getXcord() {
        //System.out.println(xcord);
        return xcord;
    }

    public double getYcord() {
        return ycord;
    }

    public double getHealth() {
        return health;
    }

    public double getShield() {
        return shield;
    }

    public double getSpeed() {  // pq les methodes pour appliquer des buff sont dans des getter ????/
        // nettoie les buffs expirés
        cleanExpiredBuffs();
        
        // Appliquer tous les buffs de speed actifs
        double finalSpeed = speed;
        for (BuffModel buff : activeBuffs) {
            if (buff.isActive() && buff.getType() == BuffModel.BuffType.SPEED_BOOST) {
                finalSpeed *= buff.getMultiplier();
            }
        }
        return finalSpeed;
    }

    public double getJump() {
        // nettoie les buffs expirés
        cleanExpiredBuffs();
        
        // Appliquer tous les buffs de jump actifs
        double finalJump = jump;
        for (BuffModel buff : activeBuffs) {
            if (buff.isActive() && buff.getType() == BuffModel.BuffType.JUMP_BOOST) {
                finalJump *= buff.getMultiplier();
            }
        }
        return finalJump;
    }

    /**
     * nettoie les buffs expirés et supprime leurs étiquettes du nametag
     */
    private void cleanExpiredBuffs() {
        activeBuffs.removeIf(buff -> {
            if (!buff.isActive()) {
                // supprime l'étiquette du buff qui expire
                if (characterView != null) {
                    if (buff.getType() == BuffModel.BuffType.SPEED_BOOST) {
                        characterView.getNameTagView().removeItem("speed_buff_icon");
                    } else if (buff.getType() == BuffModel.BuffType.JUMP_BOOST) {
                        characterView.getNameTagView().removeItem("jump_buff_icon");
                    }
                }
                return true;
            }
            return false;
        });
    }

    public boolean isDead() {
        return isDead;
    }
    
    public boolean isFacingRight() {
        return facingRight;
    }

    public InventoryJson getEquippedItem(){
        return equippedItem;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setXcord(double xcord) {
        this.xcord = xcord;
    }

    public void setYcord(double ycord) {
        this.ycord = ycord;
    }

    public void setHealth(double health) {
        // vérifie la guérison (augmentation de vie)
        if (health > this.health) {
            healEffect();
        }
        this.health = health;
    }

    public void setShield(double shield) {
        // vérifie l'ajout d'un shield
        if (shield > this.shield) {
            shieldEffect();
        }
        this.shield = shield;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setJump(double jump) {
        this.jump = jump;
    }

    public void setDead(boolean isDead) {
        this.isDead = isDead;
    }
    
    public boolean isFlyMode() {
        return flyMode;
    }
    
    public void setFlyMode(boolean flyMode) {
        this.flyMode = flyMode;
        // Quand on active le mode vol, désactiver la gravité
        if (flyMode) {
            persoBody.setGravityScale(0);
            persoBody.setLinearVelocity(new Vec2(0, 0));
        } else {
            // Réactiver la gravité quand on désactive le mode vol
            persoBody.setGravityScale(1);
        }
    }
    
    public void toggleFlyMode() {
        setFlyMode(!flyMode);
    }
    
    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }

    public boolean getFacingRight(){
        return facingRight;
    }

        public Body getBody(){
        return persoBody;
    }

    public float getMHeightPerso(){
        return this.m_HeightPerso;
    }

    public float getMWidthPerso(){
        return this.m_WidthPerso;
    }

    public static float getPPM(){
        return PPM;
    }

    
    public World getWorld() {
        return this.world;
    }
    
    /**
     * Arrête complètement le mouvement horizontal du worm quand on n'appuie plus sur une touche
     * (ça evite que le worm accumule de l'élan)
     */
    public void stopHorizontalMovement() {
        Vec2 vel = persoBody.getLinearVelocity();
        // on garde toute la vélocité verticale (saut et chute)
        persoBody.setLinearVelocity(new Vec2(0, vel.y));
    }

    public void setEquippedItem(InventoryJson item){
        this.equippedItem = item;
    }
    
    /**
     * setter pour la vue du worm (utilisé pour afficher les icônes de buff au NameTag)
     */
    public void setCharacterView(fr.upc.g5e4.view.CharacterView charView) {
        this.characterView = charView;
    }

    /**
     * Vérifie si le personnage touche le sol
     * @return true si le personnage touche le sol, false s'il est en l'air
     */
    public boolean isOnGround() {
        float distanceDetection = 0; // Une distance en plus pour être sûr de detecter le sol
        Vec2 positionActuelle = persoBody.getPosition();

        // Position du point en dessous du personnage afin de voir si on est au sol
        Vec2 positionSol = new Vec2(
            positionActuelle.x,                                    // même position x
            positionActuelle.y - (m_HeightPerso + distanceDetection )  // position y, une peu en dessous du personnage
        );

        final boolean[] toucheLeSol = {false};  // Variable pour stocker si on a trouvé le sol

        //rayon invisible vers le bas et on regarde s'il touche quelque chose
        world.raycast((fixture, point, normal, fraction) -> {
            toucheLeSol[0] = true;  // Si le rayon touche quelque chose, c'est qu'on est au sol
            return 0;               // 0 pour arrêter la recherche dès qu'on trouve quelque chose
        }, positionActuelle, positionSol);
        
        return toucheLeSol[0];
    }

    /**
     * Fait sauter le personnage si il est au sol
     */
    public void jump() {
        long currentTime = System.currentTimeMillis(); 
        double timeSinceLastJump = (currentTime - lastJumpTime) / 1000.0; //Chronometre le dernier saut
        
        if (isOnGround() && timeSinceLastJump >= JUMP_COOLDOWN) {
            float jumpForce = (float)(getJump());
            Vec2 impulse = new Vec2(0, jumpForce);
            Vec2 point = persoBody.getWorldCenter();
            persoBody.applyLinearImpulse(impulse, point);
            lastJumpTime = currentTime;  // Met à jour le timestamp du dernier saut
        }
    }

    /**
     * Applique un impact/recul au personnage lorsqu'il est touché
     * @param impactDirection Direction de l'impact (vecteur normalisé)
     * @param impactForce Force de l'impact
     */
    public void applyImpact(Vec2 impactDirection, float impactForce) {
        if (persoBody == null || isDead) {
            return;
        }
        
        // Normaliser la direction si elle n'est pas déjà normalisée
        float length = impactDirection.length();
        if (length > 0.001f) {
            Vec2 normalizedDirection = new Vec2(
                impactDirection.x / length,
                impactDirection.y / length
            );
            
            // Appliquer l'impulsion au centre du personnage
            Vec2 impulse = new Vec2(
                normalizedDirection.x * impactForce,
                normalizedDirection.y * impactForce
            );
            Vec2 point = persoBody.getWorldCenter();
            persoBody.applyLinearImpulse(impulse, point);
        }
    }

    /**
     * Inflige des dégâts au worm
     * Les dégâts sont d'abord sur le shield, puis au PV
     * @param damageAmount Le montant des dégâts
     * @param attacker Le worm qui a infligé les dégâts
     */
    public void dealDamage(double damageAmount, CharacterModel attacker) {
        // Déclencher l'effet visuel de dégâts
        damageEffect();
        
        double actualDamage = damageAmount - this.shield; //Dégats apres le shield
        if (actualDamage > 0) {
            this.health -= actualDamage;
            this.shield = 0;
            if (this.health <= 0) {
                this.isDead = true;
                SoundPlayer.playDepopSound();
                // on détruit le body physique
                if (this.persoBody != null && this.persoBody.getWorld() != null) {
                    this.world.destroyBody(this.persoBody);
                }
            }
        } else {
            this.shield -= damageAmount;
        }
    }

    /**
     * Methode pour creer son Body qui servira ensuite pour creer son image graphique
     * @param console pour donner des valeurs differentes si on lance la version terminal
     */
    public Body createBody(boolean console){

        float xWorld = (float) this.xcord / PPM;
        //System.out.println("xcord : " + xcord);
        //System.out.println("xWorld : " + xWorld);
        float yWorld = (float) (model.getHeight() - ycord) / PPM;

        BodyDef bdWorm = new BodyDef();
        bdWorm.type = BodyType.DYNAMIC;
        bdWorm.position.set(xWorld, yWorld);

        FixtureDef fixtureWorm = new FixtureDef();

        //PolygonShape square = new PolygonShape();
        //square.setAsBox(m_WidthPerso/2, m_HeightPerso/2);
        
        CircleShape square = new CircleShape();
        square.setRadius(m_WidthPerso/2);

        //square.m_p.set(0, -0.1f);

        if (console){
            //System.out.println("Console Version");
            fixtureWorm.restitution = 0.1f;
            fixtureWorm.density = 2f;  
            fixtureWorm.friction = 0.1f;
        } else {
            //System.out.println("Swing Version");
            fixtureWorm.restitution = 0.01f;
            fixtureWorm.density = 5f;   // quand on baisse ca ca foire le test controller character
            fixtureWorm.friction = 10f; // 18 a la base
        }
        fixtureWorm.shape = square;

        // Filtre de collision
        fixtureWorm.filter.categoryBits = CATEGORY_WORM; // Je suis un worm
        if (withCollision) {
            // collisions avec terrain ET autres worms
            fixtureWorm.filter.maskBits = CATEGORY_TERRAIN | CATEGORY_WORM;
        } else {
            // collisions uniquement avec le terrain (pas entre worms)
            fixtureWorm.filter.maskBits = CATEGORY_TERRAIN;
        }

        Body bWorm = world.createBody(bdWorm);
        bWorm.createFixture(fixtureWorm);

        return bWorm;
    }

    /**
     * Transforme un CharacterModel en CharacterData
     * Pour sauvegarder les donnees d'un CharacterModel
     * 
     * Agit comme un Getter
     * 
     * @return un CharacterData stockable dans sauvegarde.txt grace a Gson
     */
    public CharacterData toData() {
        double currentX = this.xcord;
        double currentY = this.ycord;
    
        if (persoBody != null) {

            // Obliger de re convertir puisque xcord et ycord ne sont pas actualiser apres que les CharacterM soient initialises
            // -> il faut recup la position du Body en metres qui lui est tjr actualisee
            // et ensuite le convertir en pixel
            currentX = persoBody.getPosition().x * PPM; 
            currentY = model.getHeight() - (persoBody.getPosition().y * PPM);
        }
        ArrayList<BuffModel> savedBuffs = new ArrayList<>();
        if (this.activeBuffs != null) {
            for (BuffModel buff : this.activeBuffs) {
                if (buff.isActive()) {
                    // sauvegarde le buff avec le temps restant en pause
                    savedBuffs.add(new BuffModel(buff.getType(), buff.getRemainingTime()));
                }
            }
        }

        System.out.println("[SAVE] " + name + " -> x=" + currentX + ", y=" + currentY);
        System.out.println("[SAVE] " + name + " -> xcord=" + xcord + ", ycord=" + ycord);

        return new CharacterData(ID, name, currentX, currentY, health, shield, isDead, this.facingRight, jump, speed, savedBuffs);
    }
    
    // ==================== Partie qui permet de voir visuellement les status du perso ====================
    
    /**
     * effet visuel de dégâts (rouge)
     */
    public void damageEffect() {
        this.currentStatus = StatusEffect.DAMAGE;
        this.statusStartTime = System.currentTimeMillis();
    }
    
    /**
     * effet visuel de guérison (vert)
     */
    public void healEffect() {
        this.currentStatus = StatusEffect.HEAL;
        this.statusStartTime = System.currentTimeMillis();
    }
    
    /**
     * effet visuel du shield (cyan)
     */
    public void shieldEffect() {
        this.currentStatus = StatusEffect.SHIELD;
        this.statusStartTime = System.currentTimeMillis();
    }
    
    /**
     * effet visuel du buff (bleu)
     */
    public void buffEffect() {
        this.currentStatus = StatusEffect.BUFF;
        this.statusStartTime = System.currentTimeMillis();
    }
    
    /**
     * effet visuel du buff speed (rose)
     */
    public void speedBuffEffect() {
        this.currentStatus = StatusEffect.SPEED_BUFF;
        this.statusStartTime = System.currentTimeMillis();
    }
    
    /**
     * effet visuel du buff jump (jaune)
     */
    public void jumpBuffEffect() {
        this.currentStatus = StatusEffect.JUMP_BUFF;
        this.statusStartTime = System.currentTimeMillis();
    }
    
    /**
     * @return effet actuel (avec vérif de la durée)
     */
    public StatusEffect getCurrentStatus() {
        long elapsed = System.currentTimeMillis() - statusStartTime;
        if (elapsed > STATUS_DURATION) {
            currentStatus = StatusEffect.NONE;
        }
        return currentStatus;
    }
    
    /**
     * @return progression de l'animation (0 à 1)
     */
    public float getStatusEffect() {
        long elapsed = System.currentTimeMillis() - statusStartTime;
        if (elapsed > STATUS_DURATION) {
            return 0f; 
        }
        return 1f;
    }
    
    // ==================== buffs temporaires ====================
    
    // -> je (mei) supp la fonction applyBuff qui servait a rien 

    
    /**
     * applique un buff speed(vitesse +50%) 
     */
    public void applySpeedBuff(int tempsRestant) {
        activeBuffs.add(new BuffModel(BuffModel.BuffType.SPEED_BOOST, tempsRestant));
        speedBuffEffect();  // déclenche l'animation visuelle rose
        System.out.println("[BUFF] " + name + " reçoit Speed Boost pour 20s");
        
        // affichage l'icône de buff speed au NameTag
        if (characterView != null) {
            characterView.getNameTagView().addItemFromResource("/buffs/speed_buff.png", "speed_buff_icon", tempsRestant);
        }
    }
    
    /**
     * applique un buff jump (saut +50%) pendant 20 secondes
     */
    public void applyJumpBuff(int tempsRestant) {
        activeBuffs.add(new BuffModel(BuffModel.BuffType.JUMP_BOOST, tempsRestant));
        jumpBuffEffect();  // déclenche l'animation visuelle jaune
        System.out.println("[BUFF] " + name + " reçoit Jump Boost pour 20s");
        
        // affichage l'icône de buff jump au NameTag
        if (characterView != null) {
            characterView.getNameTagView().addItemFromResource("/buffs/jump_buff.png", "jump_buff_icon", tempsRestant);
        }
    }
    
    /**
     * @return le buff actif actuellement (ou null)
     */
    public BuffModel getActiveBuff() {
        // nettoie les buffs expirés
        cleanExpiredBuffs();
        
        // Retourner le premier buff speed ou jump s'il existe
        if (!activeBuffs.isEmpty()) {
            return activeBuffs.get(0);
        }
        return null;
    }
    
    /**
     * @return le multiplicateur du buff actif (1.0 si pas de buff)
     */
    public double getBuffMultiplier() {
        // nettoie les buffs expirés
        cleanExpiredBuffs();
        
        // Retourner le multiplicateur du premier buff s'il existe
        if (!activeBuffs.isEmpty()) {
            return activeBuffs.get(0).getMultiplier();
        }
        return 1.0;
    }

    public ArrayList<BuffModel> getAllActiveEffects(){
        return this.activeBuffs;
    }

    /**
     * Restaure les effets visuels ET physiques des buffs après un chargement.
     */
    public void restoreVisuals(CharacterView cv) {
        for (BuffModel buff : activeBuffs) {
            System.out.println("[RestoreVisuals] Called pour la cv : " + cv);
            buff.refreshTimeStart();
            long remaining = buff.getRemainingTime();
            if (buff.getType() == BuffModel.BuffType.SPEED_BOOST) {
                System.out.println("[RestoreVisuals] BuffType SPEED : Called pour la cv : " + cv);
                speedBuffEffect(); 
                if (cv != null) {
                    System.out.println(remaining);
                    cv.getNameTagView().addItemFromResource("/buffs/speed_buff.png", "speed_buff_icon", (int) remaining);
                }
            } else if (buff.getType() == BuffModel.BuffType.JUMP_BOOST) {
                System.out.println("[RestoreVisuals] BuffType JUMP : Called pour la cv : " + cv);
                jumpBuffEffect(); 
                if (cv != null) {
                    cv.getNameTagView().addItemFromResource("/buffs/jump_buff.png", "jump_buff_icon", (int) remaining);
                }
            }
        }
    }

}



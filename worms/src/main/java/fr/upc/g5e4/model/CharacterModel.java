package fr.upc.g5e4.model;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

/**
 * Modèle du Personnage
 * Initialise ses stats et position
 */
public class CharacterModel extends Entity{
    private static int wormsCounter = 0;

    private int ID;
    private String name;

    private double xcord;   // En pixel
    private double ycord;


    private double health;
    private double shield;
    private double speed;
    private double jump;
    
    private boolean isDead;
    
    private long lastJumpTime = 0;  // Temps du dernier saut pour le cooldown
    private static final double JUMP_COOLDOWN = 1;  // Cooldown du saut en secondes

    private static float PPM = 30;

    private final float m_HeightPerso = 0.6f;   // tous les worms font la meme taille en metre
    private final float m_WidthPerso = 0.6f;

    private final Body persoBody;

    //private static final int sizePerso = 40;

    /**
     * Constructeur de la classe PersonnageModel
     * 
     * @param xcord La coordonnée x du worm
     * @param ycord La coordonnée y du worm
     */
    public CharacterModel(GroundModel model, World world, double xcord, double ycord) {
        super(model,world);
        this.ID = wormsCounter++;
        this.name = "Worm-" + this.ID;
        this.xcord = xcord;
        //System.out.println("this.xcord : " + this.xcord);
        //System.out.println("xcord : " + xcord);
        this.ycord = ycord;
        this.health = 100.0;
        this.shield = 50.0;
        this.speed = 5.0;  // defini à 3 à la base
        this.jump = 5.0;
        this.isDead = false;
        
        this.persoBody = createBody();
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
     */
    public CharacterModel(GroundModel model, World world,String name, double xcord, double ycord, double health, double shield, double speed, double jump) {
        super(model,world);
        this.ID = wormsCounter++;
        this.name = name + "-" + this.ID;
        this.xcord = xcord;
        this.ycord = ycord;
        this.health = health;
        this.shield = shield;
        this.speed = speed;
        this.jump = jump;
        this.isDead = false;

        this.persoBody = createBody();
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

    public double getSpeed() {
        return speed;
    }

    public double getJump() {
        return jump;
    }

    public boolean isDead() {
        return isDead;
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
        this.health = health;
    }

    public void setShield(double shield) {
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
            float jumpForce = (float)(jump);
            Vec2 impulse = new Vec2(0, jumpForce);
            Vec2 point = persoBody.getWorldCenter();
            persoBody.applyLinearImpulse(impulse, point);
            lastJumpTime = currentTime;  // Met à jour le timestamp du dernier saut
        }
    }

    /**
     * Inflige des dégâts au worm
     * Les dégâts sont d'abord sur le shield, puis au PV
     * @param damageAmount Le montant des dégâts
     * @param attacker Le worm qui a infligé les dégâts
     */
    public void dealDamage(double damageAmount, CharacterModel attacker) {
        double actualDamage = damageAmount - this.shield; //Dégats apres le shield
        if (actualDamage > 0) {
            this.health -= actualDamage;
            this.shield = 0;
            if (this.health <= 0) {
                this.isDead = true;
            }
        } else {
            this.shield -= damageAmount;
        }
    }

    /**
     * Methode pour creer son Body qui servira ensuite pour creer son image graphique
     */
    @Override
    public Body createBody(){

        float xWorld = (float) this.xcord / PPM;
        //System.out.println("xcord : " + xcord);
        //System.out.println("xWorld : " + xWorld);
        float yWorld = (float) (model.getHeight() - ycord) / PPM;

        BodyDef bdWorm = new BodyDef();
        bdWorm.type = BodyType.DYNAMIC;
        bdWorm.position.set(xWorld, yWorld);

        FixtureDef fixtureWorm = new FixtureDef();

        PolygonShape square = new PolygonShape();
        square.setAsBox(m_WidthPerso/2, m_HeightPerso/2);   

        fixtureWorm.shape = square;
        fixtureWorm.restitution = 0.01f;
        fixtureWorm.density = 5.0f;
        fixtureWorm.friction = 18f;

        Body bWorm = world.createBody(bdWorm);
        bWorm.createFixture(fixtureWorm);

        return bWorm;
    }

    
}

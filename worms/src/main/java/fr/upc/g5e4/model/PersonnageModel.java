package fr.upc.g5e4.model;

/**
 * Modèle du Personnage
 */
public class PersonnageModel {
    private static int wormsCounter = 0;

    private int ID;
    private String name;

    private double xcord;
    private double ycord;

    private double health;
    private double shield;
    private double speed;
    private double jump;
    
    private boolean isDead;

    /**
     * Constructeur de la classe PersonnageModel
     * 
     * @param xcord La coordonnée x du worm
     * @param ycord La coordonnée y du worm
     */
    public PersonnageModel(double xcord, double ycord) {
        this.ID = wormsCounter++;
        this.name = "Worm-" + this.ID;
        this.xcord = xcord;
        this.ycord = ycord;
        this.health = 100.0;
        this.shield = 50.0;
        this.speed = 3.0;
        this.jump = 5.0;
        this.isDead = false;
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
    public PersonnageModel(String name, double xcord, double ycord, double health, double shield, double speed, double jump) {
        this.ID = wormsCounter++;
        this.name = name + "-" + this.ID;
        this.xcord = xcord;
        this.ycord = ycord;
        this.health = health;
        this.shield = shield;
        this.speed = speed;
        this.jump = jump;
        this.isDead = false;
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
}

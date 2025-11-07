package fr.upc.g5e4.model;

/**
 * Classe pour les Données du joueur (Model)
 */
public class DoneesJoueur {
    
    private String name;
    private int xp;
    private int money;

    /**
     * Constructeur de la classe DoneesJoueur
     * @param name nom du joueur
     * @param xp points d'expérience du joueur
     * @param money argent du joueur
     */
    public DoneesJoueur(String name, int xp, int money) {
        this.name = name;
        this.xp = xp;
        this.money = money;
    }

    // Getters 
    public String getName() {
        return name;
    }

    public int getXp() {
        return xp;
    }

    public int getMoney() {
        return money;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }
    
    public void setXp(int xp) {
        this.xp = xp;
    }
    
    public void setMoney(int money) {
        this.money = money;
    }

    /**
     * methode toString pour afficher les info du joueur
     * @return un string avec les info du joueur
     */
    @Override
    public String toString() {
        return "Nom " + name + ", Argent: " + money + ", XP: " + xp;
    }
}
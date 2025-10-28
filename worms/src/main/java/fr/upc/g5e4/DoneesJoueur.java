package fr.upc.g5e4;

public class DoneesJoueur {
    
    private String name;
    private int xp;
    private int money;
    
    //Constructeur
    public DoneesJoueur(String name, int xp, int money){
        this.name = name;
        this.xp = xp;
        this.money = money;
    }

    //Getters 

    public String getName(){
        return name;
    }

    public int getXp(){
        return xp;
    }

    public int getMoney(){
        return money;
    }

    //Setters

    public void setName(){
        this.name = name;
    }
    
    public void setXp(){
        this.xp = xp;
    }
    
    public void setMoney(){
        this.money = money;
    }
}

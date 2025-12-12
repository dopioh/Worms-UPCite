package fr.upc.g5e4.model.weapons;

import fr.upc.g5e4.model.Weapon;

public class Ak47 extends Weapon{

    public Ak47() {
        super("AK-47", 30, false, 20, 0, 12, 100);
    }

    @Override
    protected boolean onUse() {
        // la vraie fonctionnalité sera ajoutée ici
        System.out.println("[AK-47] tir !");
        return true;
    }
    
    @Override
    public double getCurveAmplitude(){
        return 0.05;
    }
}

package fr.upc.g5e4.model.weapons;

import fr.upc.g5e4.model.Weapon;

public class Ak47 extends Weapon{

    /**
     * constructeur de la batte de baseball
     * @name ak47
     * @ammo 30
     * @isExplosive false
     * @damage 20
     * @blastRadius 0
     * @speed 12
     * @range 100
     */
    public Ak47() {
        super("AK-47", 30, false, 20, 0, 12, 100);
    }

    @Override
    protected boolean onUse() {
        // la vraie fonctionnalité sera ajoutée ici
        System.out.println("[AK-47] tir !");
        return true;
    }

}

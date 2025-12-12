package fr.upc.g5e4.model.weapons;

import fr.upc.g5e4.model.Weapon;

public class BaseballBat extends Weapon {

    /**
     * constructeur de la batte de baseball
     * @name baseball bat
     * @ammo 8
     * @isExplosive false
     * @damage 30
     * @blastRadius 0
     * @speed 100
     * @range 0.5
     */
    public BaseballBat() {
        super("Baseball Bat", 8, false, 30, 0, 100, 1);
    }

    @Override
    protected boolean onUse() {
        // la vraie fonctionnalité sera ajoutée ici
        System.out.println("[BaseballBat] Coup porté !");
        return true;
    }
}

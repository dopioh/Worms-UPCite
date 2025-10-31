package fr.upc.g5e4.item.model.weapons;

import fr.upc.g5e4.item.model.Weapon;

public class BaseballBat extends Weapon {

    protected BaseballBat() {
        super("Baseball Bat", 8, false, 30, 0, 1);
    }

    @Override
    protected boolean onUse() {
        // la vraie fonctionnalité sera ajoutée ici
        System.out.println("[BaseballBat] Coup porté !");
        return true;
    }
}

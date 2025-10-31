package fr.upc.g5e4.item.model.weapons;

import fr.upc.g5e4.item.model.Weapon;

public class RocketLauncher extends Weapon {

    protected RocketLauncher() {
        super("Rocket Launcher", 2, false, 100, 4, 8);
    }

    @Override
    protected boolean onUse() {
        // la vraie fonctionnalité sera ajoutée ici
        System.out.println("[RocketLauncher] Roquette tirée !");
        return true;
    }
}

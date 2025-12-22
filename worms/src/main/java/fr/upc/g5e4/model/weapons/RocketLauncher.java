package fr.upc.g5e4.model.weapons;

import fr.upc.g5e4.model.Weapon;

public class RocketLauncher extends Weapon {

    public RocketLauncher() {
        super(
            "Rocket Launcher",
            2,
            false,
            100,
            4,
            8,
            25,
            8.0
        );
    }

    @Override
    protected boolean onUse() {
        // la vraie fonctionnalité sera ajoutée ici
        System.out.println("[RocketLauncher] Roquette tirée !");
        return true;
    }

    @Override
    public double getCurveAmplitude(){
        return 0.3;
    }
}

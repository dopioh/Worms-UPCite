package fr.upc.g5e4.model.weapons;

import fr.upc.g5e4.model.Weapon;
import fr.upc.g5e4.model.trajectories.HeavyTrajectory;

public class RocketLauncher extends Weapon {

    /**
     * constructeur du lance-roquettes
     * @name rocket launcher
     * @ammo 2
     * @isExplosive false
     * @damage 100
     * @blastRadius 4
     * @speed 8
     * @range 25
     * @knockbackMultiplier 8.0
     */
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

        // Lance-roquettes : projectile lourd avec beaucoup de chute
        this.setTrajectoryCalculator(new HeavyTrajectory());
        // dégats au terrain : 9 PV par bloc avec blast radius de 7 blocs
        this.setTerrainDamage(9);
        this.setTerrainBlastRadius(7);
    }

    @Override
    protected boolean onUse() {
        // la vraie fonctionnalité sera ajoutée ici
        System.out.println("[RocketLauncher] Roquette tirée !");
        return true;
    }

}

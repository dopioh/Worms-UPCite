package fr.upc.g5e4.model.weapons;

import fr.upc.g5e4.model.Weapon;
import fr.upc.g5e4.model.trajectories.StandardTrajectory;

public class Grenade extends Weapon{

    /**
     * constructeur de la grenade
     * @name grenade
     * @ammo 3
     * @isExplosive false
     * @damage 50
     * @blastRadius 3
     * @speed 5
     * @range 15
     */
    
    public Grenade() {
        super("Grenade", 3, false, 50, 3, 5, 15);
        // Grenade : trajectoire parabolique standard
        this.setTrajectoryCalculator(new StandardTrajectory());
    }

    @Override
    protected boolean onUse() {
        // la vraie fonctionnalité sera ajoutée ici
        System.out.println("[Grenade] Lancé !");
        return true;
    }

}

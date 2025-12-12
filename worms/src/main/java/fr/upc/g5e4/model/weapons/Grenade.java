package fr.upc.g5e4.model.weapons;

import fr.upc.g5e4.model.Weapon;

public class Grenade extends Weapon{

    public Grenade() {
        super("Grenade", 3, false, 50, 3, 5, 15);
    }

    @Override
    protected boolean onUse() {
        // la vraie fonctionnalité sera ajoutée ici
        System.out.println("[Grenade] Lancé !");
        return true;
    }

}

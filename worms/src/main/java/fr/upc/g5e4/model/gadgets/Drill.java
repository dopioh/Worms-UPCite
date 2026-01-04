package fr.upc.g5e4.model.gadgets;

import fr.upc.g5e4.model.Gadget;

/**
 * Gadget Drill : casse un bloc au clic gauche
 * Pose un bloc au clic droit
 */
public class Drill extends Gadget {

    public Drill() {
        super(
            "Drill",
            Integer.MAX_VALUE,  // Infini
            true    // Infini
        );
    }

    @Override
    protected boolean onUse() {
        System.out.println(" Gadget équipé (clic gauche pour casser, clic droit pour poser) ");
        return true;
    }
    
}

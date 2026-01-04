package fr.upc.g5e4.model.gadgets;

import fr.upc.g5e4.model.Gadget;

/**
 * Gadget Drill : casse des blocs 2x2 au clic gauche
 * Pose un bloc au clic droit
 */
public class Drill extends Gadget {

    public Drill() {
        super(
            "Drill",
            Integer.MAX_VALUE,  // Infini
            true                // Infini
        );
    }

    @Override
    protected boolean onUse() {
        System.out.println("[Drill] Gadget équipé - Clic gauche pour casser des blocs 2x2, clic droit pour en poser");
        return true;
    }
    
}

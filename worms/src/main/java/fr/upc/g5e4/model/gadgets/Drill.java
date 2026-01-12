package fr.upc.g5e4.model.gadgets;

import fr.upc.g5e4.model.Gadget;

/**
 * Gadget Drill : 
 * Casse un bloc au clic gauche
 * Pose un bloc au clic droit
 * @author Billy
 */
public class Drill extends Gadget {

    public Drill() {
        super(
            "Drill",
            10,  // 10 utilisations par tour
            true    // Infini pour toute la partie
        );
    }

    @Override
    protected boolean onUse() {
        System.out.println(" Gadget équipé (clic gauche pour casser, clic droit pour poser) ");
        return true;
    }
    
}

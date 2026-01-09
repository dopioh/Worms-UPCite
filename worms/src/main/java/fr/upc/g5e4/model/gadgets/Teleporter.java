package fr.upc.g5e4.model.gadgets;

import fr.upc.g5e4.model.Gadget;

/**
 * Gadget Teleporter : se téléporte au clic (si aucun bloc à la position)
 * @author Billy
 */
public class Teleporter extends Gadget {

    public Teleporter() {
        super(
            "Teleporter",
            3,      // 3 utilisations max
            false   // Pas infini
        );
    }

    @Override
    protected boolean onUse() {
        System.out.println(" Gadget Teleporter équipé (clic gauche pour se téléporter) ");
        return true;
    }
}

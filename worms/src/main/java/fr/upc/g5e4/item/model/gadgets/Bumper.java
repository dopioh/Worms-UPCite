package fr.upc.g5e4.item.model.gadgets;

import fr.upc.g5e4.item.model.Gadget;

public class Bumper extends Gadget {

    public Bumper() {
        super("Bumper", Integer.MAX_VALUE, true);}

    @Override
    protected boolean onUse() {
        // la vraie fonctionnalité sera ajoutée ici
        System.out.println("[Bumper] rebond !");
        return true;
    }
    
}

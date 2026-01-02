package fr.upc.g5e4.model.gadgets;

import fr.upc.g5e4.model.Gadget;

/**
 * Potion de speed : boost la vitesse de 50% pendant 20 secondes
 */
public class PotionSpeed extends Gadget {

    public PotionSpeed() {
        super(
            "Potion Speed",
            2,      // 2 utilisations par partie
            false   // pas infini
        );
    }

    @Override
    protected boolean onUse() {
        System.out.println("[Potion Speed] +50% vitesse pendant 5 sec");
        return true;
    }
    
}

package fr.upc.g5e4.model.gadgets;

import fr.upc.g5e4.model.Gadget;

/**
 * Potion de guérison : restaure 50 HP au worm
 */
public class PotionHeal extends Gadget {

    public PotionHeal() {
        super(
            "Potion Heal",
            3,      // 3 utilisations par partie
            false   // pas infini
        );
    }

    @Override
    protected boolean onUse() {
        System.out.println("[Potion Heal] +50 HP");
        return true;
    }
    
}

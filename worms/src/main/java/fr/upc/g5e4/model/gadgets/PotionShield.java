package fr.upc.g5e4.model.gadgets;

import fr.upc.g5e4.model.Gadget;

/**
 * Potion de shield : restaure 25 points de shield au worm
 * @author Billy
 */
public class PotionShield extends Gadget {

    public PotionShield() {
        super(
            "Potion Shield",
            3,      // 3 utilisations par partie
            false   // pas infini
        );
    }

    @Override
    protected boolean onUse() {
        System.out.println("[Potion Shield] +25 Shield");
        return true;
    }
    
}

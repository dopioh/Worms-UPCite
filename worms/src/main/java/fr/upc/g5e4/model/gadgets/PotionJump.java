package fr.upc.g5e4.model.gadgets;

import fr.upc.g5e4.model.Gadget;

/**
 * Potion de jump : boost le saut de 50% pendant 20 secondes
 * @author Billy
 */
public class PotionJump extends Gadget {

    public PotionJump() {
        super(
            "Potion Jump",
            2,      // 2 utilisations par partie
            false   // pas infini
        );
    }

    @Override
    protected boolean onUse() {
        System.out.println("[Potion Jump] +50% saut pendant 5 sec");
        return true;
    }
    
}

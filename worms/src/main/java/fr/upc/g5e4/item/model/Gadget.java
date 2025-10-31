package fr.upc.g5e4.item.model;

public abstract class Gadget extends Item {

    protected Gadget(String name, int maxUsages, boolean isInfinite) {
        super(name, ItemType.GADGET, maxUsages, isInfinite);
    }
    
}

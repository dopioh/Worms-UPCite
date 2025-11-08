package fr.upc.g5e4.inventory.model;

public record Item(String id, String name, Type type, int damage) {
    public enum Type{WEAPON, TOOL, CONSUMABLE}; 

    @Override  // Montre le nom de l'item
    public String toString() {
        return name;
    }

}
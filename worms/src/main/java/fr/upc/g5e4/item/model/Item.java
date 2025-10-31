package fr.upc.g5e4.item.model;

/**
 * Base commune à tous les objets du jeu.
 * Gère le nom, le type et le nombre d'utilisations.
 */

public abstract class Item {

    private final String name;
    private final ItemType objectType;

    private final int maxUsages;     // total autorisé si pas infini
    private final boolean isInfinite;
    private int remainingUses;       // décrémente à chaque use()

    protected Item(String name, ItemType objectType, int maxUsages, boolean isInfinite) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name obligatoire");
        if (objectType == null) throw new IllegalArgumentException("objectType obligatoire");
        if (!isInfinite && maxUsages <= 0) throw new IllegalArgumentException("maxUsages > 0");
        this.name = name;
        this.objectType = objectType;
        this.maxUsages = Math.max(0, maxUsages); // met maxUsages a 0 si valeur negatif
        this.isInfinite = isInfinite;
        this.remainingUses = isInfinite ? Integer.MAX_VALUE : this.maxUsages;
    }

    /** Appelé par le controller, renvoie true si l'action a eu lieu */
    public final boolean use() {
        if (!isInfinite && remainingUses <= 0) return false;
        boolean ok = onUse();
        if (ok && !isInfinite) remainingUses--;
        return ok;
    }

    /** Comportement spécifique (tirer, poser…) à définir dans la sous classe */
    protected abstract boolean onUse();

    // Helpers
    public boolean isUsable() { return isInfinite || remainingUses > 0; }
    public void refill() { if (!isInfinite) remainingUses = maxUsages; }

    // Getters
    public String getName() { return name; }
    public ItemType getObjectType() { return objectType; }
    public int getMaxUsages() { return maxUsages; }
    public boolean isInfinite() { return isInfinite; }
    public int getRemainingUses() { return isInfinite ? Integer.MAX_VALUE : remainingUses; }
}

package fr.upc.g5e4.model;

/**
 * Classe pour un buff temporaire qui affecte les stats du worm (ajouté d'autre si idée vient)
 * @author Billy
 */
public class BuffModel {
    public enum BuffType {
        SPEED_BOOST("Speed Boost", 1.5),      // 50% de boost de vitesse (1.5x)
        JUMP_BOOST("Jump Boost", 1.5);        // 50% de boost de saut (1.5x)
        
        private final String name;
        private final double multiplier;
        
        BuffType(String name, double multiplier) {
            this.name = name;
            this.multiplier = multiplier;
        }
        
        public String getName() {
            return name;
        }
        
        public double getMultiplier() {
            return multiplier;
        }
    }
    
    private BuffType type;
    private long startTime;
    private long duration; // en milliseconde
    
    /**
     * crée un nouveau buff
     * @param type Le type de buff
     * @param duration La durée en milliseconde
     */
    public BuffModel(BuffType type, long duration) {
        this.type = type;
        this.duration = duration;
        this.startTime = System.currentTimeMillis();
    }
    
    /**
     * vérifie si le buff est toujours actif
     */
    public boolean isActive() {
        long elapsed = System.currentTimeMillis() - startTime;
        return elapsed < duration;
    }
    
    /**
     * @return le multiplicateur du buff 
     */
    public double getMultiplier() {
        return type.getMultiplier();
    }
    
    public BuffType getType() {
        return type;
    }
    
    /**
     * @return le temps restant en milliseconde
     */
    public long getRemainingTime() {
        long elapsed = System.currentTimeMillis() - startTime;
        long remaining = duration - elapsed;
        return Math.max(0, remaining);
    }

    public void refreshTimeStart(){
        this.startTime = System.currentTimeMillis();
    }
}

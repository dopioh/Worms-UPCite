package fr.upc.g5e4.model;

/**
 * Tracker les statistiques de jeu importantes
 */
public class GameStats {
    private int damageDealt;        // Dégâts totaux infligés
    private int shotsFired;         // Tirs effectués
    private int shotsHit;           // Tirs qui ont touché
    private int kills;              // Nombre de kills
    private int roundsPlayed;       // Nombre de rounds joués
    private long startTime;         // Temps de début de partie
    
    public GameStats() {
        this.damageDealt = 0;
        this.shotsFired = 0;
        this.shotsHit = 0;
        this.kills = 0;
        this.roundsPlayed = 0;
        this.startTime = System.currentTimeMillis();
    }
    
    // Getters
    public int getDamageDealt() { return damageDealt; }
    public int getShotsFired() { return shotsFired; }
    public int getShotsHit() { return shotsHit; }
    public int getKills() { return kills; }
    public int getRoundsPlayed() { return roundsPlayed; }
    
    public int getAccuracy() {
        if (shotsFired == 0) return 0;
        return (int)((shotsHit * 100.0) / shotsFired);
    }
    
    public long getGameDuration() {
        return System.currentTimeMillis() - startTime;
    }
    
    // Méthodes pour incrémenter les stats
    public void addDamage(int damage) {
        this.damageDealt += damage;
    }
    
    public void incrementShotsFired() {
        this.shotsFired++;
    }
    
    public void incrementShotsHit() {
        this.shotsHit++;
    }
    
    public void incrementKills() {
        this.kills++;
    }
    
    public void incrementRounds() {
        this.roundsPlayed++;
    }
}

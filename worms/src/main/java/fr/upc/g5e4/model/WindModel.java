/*
package fr.upc.g5e4.model;

public class WindModel {
    public enum Dir {
        N(0, -1, "N"),
        NE(1, -1, "NE"),
        E(1, 0, "E"),
        SE(1, 1, "SE"),
        S(0, 1, "S"),
        SO(-1, 1, "SO"),
        O(-1, 0, "O"),
        NO(-1, -1, "NO");

        public final int dx;
        public final int dy;
        public final String label;
        Dir(int dx, int dy, String label) {
            this.dx = dx;
            this.dy = dy;
            this.label = label;
        }
    }

    private Dir dir = Dir.E;
    private float strength = 0.5f; // 0..1

    public Dir getDir() { return dir; }
    public float getStrength() { return strength; }

    public void setDir(Dir dir) { this.dir = dir; }
    public void setStrength(float strength) {
        this.strength = Math.max(0f, Math.min(1f, strength));
    }
}
*/
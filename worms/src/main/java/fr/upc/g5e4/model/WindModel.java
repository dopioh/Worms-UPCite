package fr.upc.g5e4.model;
/**
 * Représente le vent dans le jeu.
 * Le vent possède une direction cardinale, ainsi qu'une force.
 * 
 * 0 représente un le calme
 * 1 représente un grand vent
 *
 * @author Hugo
 * @version 1.0
 */

public class WindModel {
    /*Direction cardinales du vent
    * Chaque direction possède un vecteur (dx,dy) et un label (ex : "N" Nord) 
    */
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

        /**
         * Constructeur de la direction
         *
         * @param dx déplacement horizontal
         * @param dy déplacement vertical
         * @param label nom de la direction
         */
        Dir(int dx, int dy, String label) {
            this.dx = dx;
            this.dy = dy;
            this.label = label;
        }
    }

    private Dir dir = Dir.E;      // Direction par défaut
    private float strength = 0.5f; // Valeur entre 0 (calme) et 1 (grand vent)

    /**Direction du vent
     * @return direction
     */
    public Dir getDir(){
        return dir;
    }

    /**Force du vent
     * 
     * @return force
     */
    public float getStrength(){ 
        return strength;
    }

    /**Mise en place d'une direction pour le vent
     * 
     * @param dir nouvelle direction du vent
     * 
     */
    public void setDir(Dir dir){
        this.dir = dir;
    }

    /**Modifie la force du vent
     * 
     * @param strength nouvelle force
     * @param save si la valeure doit être enregistrée
     */
    public void setStrength(float strength, boolean save) {
        if (!save){
            this.strength = Math.max(0f, Math.min(1f, strength)); //Pour rester entre 0 et 1
        } else {
            this.strength = strength;
        }
    }
}

package fr.upc.g5e4.model;

import java.awt.Color;

/**
 * Types de blocs pour le terrain
 * Chaque type a une couleur, peut être solide ou non donc on utilise enum  et des points de vie
 * @author Nassim & Billy 
*/
public enum BlockType {
    AIR(false, new Color(0, 0, 0, 0), 0),                    // couleur transparente (vide/air) - 0 PV
    DIRT(true, new Color(139, 90, 43), 5),                   // couleur marron (terre) - 5 PV
    GRASS(true, new Color(34, 139, 34), 5),                  // couleur verte (herbe) - 5 PV
    STONE(true, new Color(128, 128, 128), 10),               // couleur grise (pierre) - 10 PV
    WATER(false, new Color(75, 147, 240, 150), 0);           // couleur bleue semi-transparente (eau) - 0 PV

    private final boolean solid;
    private final Color color;
    private final int maxHealth;  // points de vie du bloc

    BlockType(boolean solid, Color color, int maxHealth) {
        this.solid = solid;
        this.color = color;
        this.maxHealth = maxHealth;
    }

    /**
     * Indique si le bloc est solide (pour les collisions)
     * @return true si le bloc bloque le mouvement
     */
    public boolean isSolid() {
        return solid;
    }

    /**
     * Retourne la couleur du bloc
     * @return la couleur du bloc
     */
    public Color getColor() {
        return color;
    }
    
    /**
     * Getter des points de vie du bloc
     * @return nombre de PV du bloc
     */
    public int getMaxHealth() {
        return maxHealth;
    }
}


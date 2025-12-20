package fr.upc.g5e4.model;

import java.awt.Color;

/**
 * Types de blocs pour le terrain
 * Chaque type a une couleur et peut être solide ou non donc on utilise enum
 */
public enum BlockType {
    AIR(false, new Color(0, 0, 0, 0)),           // couleur transparente (vide/air)
    DIRT(true, new Color(139, 90, 43)),          // couleur marron (terre)
    GRASS(true, new Color(34, 139, 34)),         // couleur verte (herbe)
    STONE(true, new Color(128, 128, 128)),       // couleur grise (pierre)
    WATER(false, new Color(75, 147, 240, 150));  // couleur bleue semi-transparente (eau)

    private final boolean solid;
    private final Color color;

    BlockType(boolean solid, Color color) {
        this.solid = solid;
        this.color = color;
    }

    /**
     * Indique si le bloc est solide (pour les collisions)
     * @return true si le bloc bloque le mouvement
     */
    public boolean isSolid() {
        return solid;
    }

    /**
     * On prend la couleur par défaut du bloc si y'a pas de texture
     * @return la couleur du bloc
     */
    public Color getColor() {
        return color;
    }
}


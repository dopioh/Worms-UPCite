package fr.upc.g5e4.model;

/**
 * Configuration centrale pour la génération de terrain
 * Permet de changer facilement le type de terrain et la taille de la map
 */
public class MapConfig {
    
    // ========== CONFIGURATION MANUELLE ==========
    // Changez ces valeurs pour modifier le terrain
    
    /**
     * Type de map à générer:
     * - STANDARD: Terrain avec collines et plateformes de spawn (mode original)
     * - MOUNTAINS: Hautes montagnes avec beaucoup de relief
     * - ISLANDS: Terrain avec l'eau entre les îles
     * - FLAT: Terrain complètement plat
     */
    private static MapType mapType = MapType.STANDARD;
    
    /**
     * Taille de la map:
     * - SMALL: 800x720 pixels
     * - MEDIUM: 1280x720 pixels (16:9)
     * - LARGE: 1700x720 pixels
     */
    private static MapSize mapSize = MapSize.MEDIUM;
    
    /**
     * Style de fond:
     * - JOUR: Fond de jour (BG_Jour.png)
     * - NUIT: Fond de nuit (BG.png)
     */
    private static BackgroundStyle backgroundStyle = BackgroundStyle.JOUR;
    
    // =============================================
    
    /**
     * Enum pour les types de map
     */
    public enum MapType {
        STANDARD,   // Terrain avec plateformes aux bords (mode original)
        MOUNTAINS,  // Montagnes hautes et escarpées
        ISLANDS,    // Plusieurs îles séparées
        FLAT        // Terrain complètement plat
    }
    
    /**
     * Enum pour les tailles de map
     */
    public enum MapSize {
        SMALL(800, 720),
        MEDIUM(1280, 720),
        LARGE(1700, 720);
        
        private final int width;
        private final int height;
        
        MapSize(int width, int height) {
            this.width = width;
            this.height = height;
        }
        
        public int getWidth() {
            return width;
        }
        
        public int getHeight() {
            return height;
        }
    }
    
    /**
     * Enum pour les styles de fond
     */
    public enum BackgroundStyle {
        JOUR("/BG_Jour.png"),
        NUIT("/BG.png");
        
        private final String imagePath;
        
        BackgroundStyle(String imagePath) {
            this.imagePath = imagePath;
        }
        
        public String getImagePath() {
            return imagePath;
        }
    }
    
    /**
     * Retourne la largeur de la map configurée
     */
    public static int getMapWidth() {
        return mapSize.getWidth();
    }
    
    /**
     * Retourne la hauteur de la map configurée
     */
    public static int getMapHeight() {
        return mapSize.getHeight();
    }
    
    /**
     * Retourne le type de map configuré
     */
    public static MapType getMapType() {
        return mapType;
    }
    
    /**
     * Retourne la taille de la map configurée
     */
    public static MapSize getMapSize() {
        return mapSize;
    }
    
    /**
     * Modifie le type de map
     */
    public static void setMapType(MapType type) {
        mapType = type;
    }
    
    /**
     * Modifie la taille de la map
     */
    public static void setMapSize(MapSize size) {
        mapSize = size;
    }
    
    /**
     * Retourne le style de fond configuré
     */
    public static BackgroundStyle getBackgroundStyle() {
        return backgroundStyle;
    }
    
    /**
     * Modifie le style de fond
     */
    public static void setBackgroundStyle(BackgroundStyle style) {
        backgroundStyle = style;
    }
}

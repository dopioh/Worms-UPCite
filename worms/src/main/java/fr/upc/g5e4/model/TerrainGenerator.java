package fr.upc.g5e4.model;

/**
 * Générateur de terrain procédural utilisant le bruit de Perlin
 * Supporte plusieurs types de terrains: standard, montagnes, îles, plat
 */
public class TerrainGenerator {
    private final PerlinNoise perlin;
    
    public TerrainGenerator(long seed) {
        this.perlin = new PerlinNoise(seed);
    }
    
    /**
     * Génère un terrain selon le type configuré dans MapConfig
     * @param points Nombre de points à générer
     * @param minHeight Hauteur minimale
     * @param maxHeight Hauteur maximale
     * @return Tableau des hauteurs Y selon le type de map
     */
    public int[] generateTerrainByType(int points, int minHeight, int maxHeight) {
        MapConfig.MapType type = MapConfig.getMapType();
        
        switch (type) {
            case MOUNTAINS:
                return generateMountains(points, minHeight, maxHeight);
            case ISLANDS:
                return generateIslands(points, minHeight, maxHeight);
            case FLAT:
                return generateFlat(points, minHeight, maxHeight);
            case STANDARD:
            default:
                return generateTerrainWithPlatforms(points, minHeight, maxHeight, 280, 15);
        }
    }
    
    /**
     * Génère un tableau de hauteurs pour le terrain en utilisant perlin
     * on utilise les octaves pour avoir un terrain varié
     * @param points Nombre de points à générer
     * @param minHeight Hauteur minimale
     * @param maxHeight Hauteur maximale
     * @return Tableau des hauteurs Y
     */
    public int[] generateTerrain(int points, int minHeight, int maxHeight) {
        int[] groundY = new int[points];
        
        // paramètres du bruit de Perlin
        int octaves = 4;
        double persistence = 0.02;
        double scale = 0.02; // (plus c'est petit plus c'est vallonné)
        
        int heightRange = maxHeight - minHeight;
        int midHeight = minHeight + heightRange / 2;
        
        for (int i = 0; i < points; i++) {
            // on commence par calculer le bruit de Perlin pour cette position
            double noise = perlin.octaveNoise(i * scale, octaves, persistence);
            
            // on converti la valeur (-1 à 1) en hauteur de terrain
            int y = midHeight + (int)(noise * (heightRange / 2));
            
            // on borne entre le minimum et le maximum Y de la map (securité)
            groundY[i] = Math.max(minHeight, Math.min(maxHeight, y));
        }
        
        return groundY;
    }
    
    /**
     * Génère un terrain de montagnes avec beaucoup de relief
     * @param points Nombre de points à générer
     * @param minHeight Hauteur minimale
     * @param maxHeight Hauteur maximale
     * @return Tableau des hauteurs Y avec hautes montagnes
     */
    public int[] generateMountains(int points, int minHeight, int maxHeight) {
        int[] groundY = new int[points];
        
        // Ajuster le scale en fonction de la largeur de la map pour garder la même apparence
        // Utiliser une racine carrée pour un ajustement plus doux et éviter les terrains trop plats
        int mapWidth = MapConfig.getMapWidth();
        double scaleAdjustment = Math.sqrt(mapWidth / 1700.0); // Référence: 1700px (LARGE)
        
        // paramètres pour montagnes avec relief modéré
        int octaves = 4;  // Un peu de lissage
        double persistence = 0.55; // Variations modérées
        double scale = 0.015 * scaleAdjustment; // Échelle ajustée selon la taille
        
        int heightRange = maxHeight - minHeight;
        int midHeight = minHeight + heightRange / 3;
        
        for (int i = 0; i < points; i++) {
            // Bruit principal
            double noise = perlin.octaveNoise(i * scale, octaves, persistence);
            
            // Seconde couche pour des pics variés mais pas trop
            double detailNoise = perlin.octaveNoise(i * scale * 3.5, 2, 0.35);
            
            // Combiner pour créer des pics variés
            double combined = noise + detailNoise * 0.35;
            
            // Fonction non-linéaire modérée
            combined = Math.pow(Math.abs(combined), 0.7) * Math.signum(combined);
            
            // Augmentation de l'amplitude pour des montagnes beaucoup plus hautes
            int y = midHeight + (int)(combined * heightRange * 1.3);
            groundY[i] = Math.max(minHeight, Math.min(maxHeight, y));
        }
        
        return groundY;
    }
    
    /**
     * Génère un archipel avec plusieurs îles séparées
     * @param points Nombre de points à générer
     * @param minHeight Hauteur minimale (utilisé comme niveau d'eau)
     * @param maxHeight Hauteur maximale
     * @return Tableau des hauteurs Y avec îles séparées
     */
    public int[] generateIslands(int points, int minHeight, int maxHeight) {
        int[] groundY = new int[points];
        
        // Le waterLevel est à 480 dans GameManager (mode ISLANDS seulement)
        int waterSurface = 480;
        
        // Paramètres pour un terrain plus wavy
        int octaves = 4;
        double persistence = 0.1; // Un peu plus de détails
        double scale = 0.025; // Plus de vagues
        
        int heightRange = maxHeight - minHeight;
        int midHeight = minHeight + heightRange / 2;
        
        for (int i = 0; i < points; i++) {
            // Calculer le bruit de Perlin
            double noise = perlin.octaveNoise(i * scale, octaves, persistence);
            
            // Facteur de bordure pour forcer la terre sur les côtés (éviter spawn kill)
            // 0 au centre, 1 aux extrémités
            double normalizedX = (2.0 * i / points) - 1.0; // -1 à 1
            double edgeFactor = normalizedX * normalizedX; // 0 à 1 (1 aux bords)
            
            // Biais: on remonte le terrain sur les bords (-150) et on le creuse au centre (+50)
            // force l'eau vers le milieu et la terre sur les côtés
            double bias = edgeFactor * -150 + (1 - edgeFactor) * 50;
            
            // Convertir en hauteur de terrain avec le biais
            int y = midHeight + (int)(noise * (heightRange / 1.8)) + (int)bias;
            
            // Si le terrain est sous le niveau d'eau, le faire descendre plus
            if (y > waterSurface) {
                // Zone sous l'eau, accentuer la profondeur
                int depthBelowWater = y - waterSurface;
                y = waterSurface + (int)(depthBelowWater * 1.2); 
            }
            
            // Borner entre min et max
            groundY[i] = Math.max(minHeight, Math.min(maxHeight, y));
        }
        
        return groundY;
    }
    
    /**
     * Génère un terrain complètement plat
     * @param points Nombre de points à générer
     * @param minHeight Hauteur minimale
     * @param maxHeight Hauteur maximale
     * @return Tableau des hauteurs Y plat
     */
    public int[] generateFlat(int points, int minHeight, int maxHeight) {
        int[] groundY = new int[points];
        
        // Terrain plat au milieu de la hauteur
        int flatHeight = minHeight + (maxHeight - minHeight) / 3;
        
        for (int i = 0; i < points; i++) {
            groundY[i] = flatHeight;
        }
        
        return groundY;
    }
    
    /**
     * Génère un tableau de hauteurs de terrain mais les bords sont applatis
     * (pour éviter que les worms tombent directement dans l'eau)
     * @param points Nombre de points à générer
     * @param minHeight Hauteur minimale
     * @param maxHeight Hauteur maximale
     * @param platformHeight Hauteur des bases de spawn (doit être > minHeight)
     * @param platformWidth Largeur des bases en pourcentage (ex: 15 = 15% de chaque côté)
     * @return Tableau des hauteurs Y avec zones plus plates et surélevées aux bords de la map
     */
    public int[] generateTerrainWithPlatforms(int points, int minHeight, int maxHeight, int platformHeight, int platformWidth) {
        int[] groundY = generateTerrain(points, minHeight, maxHeight);
        
        // on calcule le nombre de points pour les zones aplaties
        int edgePoints = (points * platformWidth) / 100;
        
        // on utilise une courbe pour une transition plus naturelle
        for (int i = 0; i < edgePoints; i++) {
            // utiliser une courbe quadratique pour un aplatissement plus doux
            double normalizedPos = (double)i / edgePoints;
            double factor = normalizedPos * normalizedPos; // courbe quadratique pour transition douce
            
            // interpolation entre platformHeight et la hauteur générée
            int targetHeight = (int)(platformHeight + (groundY[i] - platformHeight) * factor);
            groundY[i] = targetHeight;
            
            // on fait la même chose pour le bord droit
            int rightIndex = points - 1 - i;
            targetHeight = (int)(platformHeight + (groundY[rightIndex] - platformHeight) * factor);
            groundY[rightIndex] = targetHeight;
        }
        
        return groundY;
    }
}

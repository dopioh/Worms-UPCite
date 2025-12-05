package fr.upc.g5e4.model;

/**
 * Générateur de terrain procédural utilisant le bruit de Perlin
 */
public class TerrainGenerator {
    private final PerlinNoise perlin;
    
    public TerrainGenerator(long seed) {
        this.perlin = new PerlinNoise(seed);
    }
    
    /**
     * Génère un tableau de hauteurs pour le terrain
     * @param points Nombre de points à générer
     * @param minHeight Hauteur minimale
     * @param maxHeight Hauteur maximale
     * @return Tableau des hauteurs Y
     */
    public int[] generateTerrain(int points, int minHeight, int maxHeight) {
        int[] groundY = new int[points];
        
        // Paramètres du bruit de Perlin
        int octaves = 4;
        double persistence = 0.5;
        double scale = 0.02; // Plus c'est petit, plus c'est vallonné
        
        int heightRange = maxHeight - minHeight;
        int midHeight = minHeight + heightRange / 2;
        
        for (int i = 0; i < points; i++) {
            // Calculer le bruit de Perlin pour cette position
            double noise = perlin.octaveNoise(i * scale, octaves, persistence);
            
            // Convertir la valeur (-1 à 1) en hauteur de terrain
            int y = midHeight + (int)(noise * (heightRange / 2));
            
            // S'assurer que la valeur reste dans les limites
            groundY[i] = Math.max(minHeight, Math.min(maxHeight, y));
        }
        
        return groundY;
    }
    
    /**
     * Génère un terrain avec des bords plus plats
     */
    public int[] generateTerrainWithFlatEdges(int points, int minHeight, int maxHeight) {
        int[] groundY = generateTerrain(points, minHeight, maxHeight);
        
        // Aplatir les bords (10% de chaque côté)
        int edgePoints = points / 10;
        
        for (int i = 0; i < edgePoints; i++) {
            double factor = (double)i / edgePoints;
            groundY[i] = (int)(minHeight + (groundY[i] - minHeight) * factor);
            groundY[points - 1 - i] = (int)(minHeight + (groundY[points - 1 - i] - minHeight) * factor);
        }
        
        return groundY;
    }
}

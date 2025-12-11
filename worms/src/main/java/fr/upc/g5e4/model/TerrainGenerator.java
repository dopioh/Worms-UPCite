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
        double persistence = 0.02;
        double scale = 0.02; // Plus c'est petit, plus c'est vallonné
        
        int heightRange = maxHeight - minHeight;
        int midHeight = minHeight + heightRange / 2;
        
        for (int i = 0; i < points; i++) {
            // Calculer le bruit de Perlin pour cette position
            double noise = perlin.octaveNoise(i * scale, octaves, persistence);
            
            // Convertir la valeur (-1 à 1) en hauteur de terrain
            int y = midHeight + (int)(noise * (heightRange / 2));
            
            // on borne entre le minimum et le maximum Y de la map (securité)
            groundY[i] = Math.max(minHeight, Math.min(maxHeight, y));
        }
        
        return groundY;
    }
    
    /**
     * Génère un tableau de hauteurs de terrain mais les bords sont applatis
     * pour éviter que les worms tombent directement dans l'eau
     * @param points Nombre de points à générer
     * @param minHeight Hauteur minimale
     * @param maxHeight Hauteur maximale
     * @param platformHeight Hauteur des bases de swpan (doit être > minHeight)
     * @param platformWidth Largeur des bases en pourcentage (ex: 15 = 15% de chaque côté)
     * @return Tableau des hauteurs Y avec zones plus plates et surélevées aux bords de la map
     */
    public int[] generateTerrainWithPlatforms(int points, int minHeight, int maxHeight, int platformHeight, int platformWidth) {
        int[] groundY = generateTerrain(points, minHeight, maxHeight);
        
        // Calculer le nombre de points pour les zones aplaties
        int edgePoints = (points * platformWidth) / 100;
        
        // Aplatir progressivement les bords vers la hauteur de plateforme
        // On utilise une courbe pour une transition plus naturelle
        for (int i = 0; i < edgePoints; i++) {
            // Facteur de transition: commence à 0, monte progressivement vers 1
            // Utilise une courbe quadratique pour un aplatissement plus doux
            double normalizedPos = (double)i / edgePoints;
            double factor = normalizedPos * normalizedPos; // Courbe quadratique pour transition douce
            
            // Interpolation entre platformHeight et la hauteur générée
            int targetHeight = (int)(platformHeight + (groundY[i] - platformHeight) * factor);
            groundY[i] = targetHeight;
            
            // Même chose pour le bord droit
            int rightIndex = points - 1 - i;
            targetHeight = (int)(platformHeight + (groundY[rightIndex] - platformHeight) * factor);
            groundY[rightIndex] = targetHeight;
        }
        
        return groundY;
    }
}

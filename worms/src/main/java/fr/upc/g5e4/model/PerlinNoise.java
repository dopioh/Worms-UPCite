package fr.upc.g5e4.model;

import de.articdive.jnoise.generators.noisegen.perlin.PerlinNoiseGenerator;
import de.articdive.jnoise.pipeline.JNoise;

/**
 * Générateur de bruit de Perlin pour créer un terrain procédural en 1D
 * Utilise la librairie JNoise pour une implémentation rapide
 * (une génération est propre a sa seed, si on réutilise la seed on aura toujours le même terrain)
 */
public class PerlinNoise {
    private final JNoise noiseGenerator;
    
    public PerlinNoise(long seed) {
        this.noiseGenerator = JNoise.newBuilder()
            .perlin(PerlinNoiseGenerator.newBuilder().setSeed((int)seed).build())
            .build();
    }
    
    /**
     * Calcule le bruit de Perlin pour une coordonnée x donnée
     * @param x Position
     * @return Valeur entre -1 et 1
     */
    public double noise(double x) {
        return noiseGenerator.evaluateNoise(x, 0);
    }
    
    /**
     * Bruit octave : combine plusieurs fréquences pour avoir un terrain irrégulier
     * (sans les octaves le terrain sera trop plat)
     */
    public double octaveNoise(double x, int octaves, double persistence) {
        double total = 0;     // accumulation du bruit
        double frequency = 1; // fréquence actuelle
        double amplitude = 1; // poid de l'octave
        double maxValue = 0;  // somme des amplitudes pour normaliser (à la fin)
        
        for (int i = 0; i < octaves; i++) {
            total += noise(x * frequency) * amplitude;
            maxValue += amplitude;
            amplitude *= persistence;
            frequency *= 2;
        }
        
        return total / maxValue;
    }
}


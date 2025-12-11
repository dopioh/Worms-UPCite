package fr.upc.g5e4.model;

import de.articdive.jnoise.generators.noisegen.perlin.PerlinNoiseGenerator;
import de.articdive.jnoise.pipeline.JNoise;

/**
 * Générateur de bruit de Perlin pour créer un terrain procédural en 1D
 * Utilise la librairie JNoise pour une implémentation optimisée
 * Une génération est propre a sa seed, si on réutilise la seed on aura toujours le même terrain
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
     * Sans les octaves le terrain sera trop plat
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

/* ==================== ANCIENNE IMPLEMENTATION MANUELLE ====================
 * cette implémentation a été remplacée par la librairie JNoise
 * 
package fr.upc.g5e4.model;

import java.util.Random;

public class PerlinNoise {
    private final int[] permutation;
    
    public PerlinNoise(long seed) {
        Random random = new Random(seed);
        this.permutation = new int[512];
        int[] p = new int[256];
        // init du tableau
        for (int i = 0; i < 256; i++) {
            p[i] = i;
        }
        
        // shuffle du tableau (méthode -> Fisher-Yates)
        for (int i = 255; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = p[i];
            p[i] = p[j];
            p[j] = temp;
        }
        
        // on rempli permutation en concatenant deux fois le tableau p dans permutation 
        // ex : permutation[0..255] = p[0..255] et permutation[256..511] = p[0..255]
        // pourquoi ? car on peut est sur qu'on ira jamais hors du tableau (ArrayIndexOutOfBoundsException)
        // c'est une convention du créateur Ken Perlin, et ça permet de ne pas utiliser % qui est plus demandant en ressource
        for (int i = 0; i < 256; i++) {
            permutation[i] = permutation[i + 256] = p[i];
        }
    }
    
    public double noise(double x) {
        int X = (int) Math.floor(x) & 255; // "& 255" assure que X restera dans l'intervale 0-255 (equivalent d'un modulo)
        x -= Math.floor(x); // on garde la partie fractionnaire de x, comprise dans : [0, 1[
        
        double u = fade(x); // adoucis la courbe de terrain, pour ne pas avoir des gros pics et des cassures
        
        int a = permutation[X];
        int b = permutation[X + 1];
        
        return lerp(u, grad(a, x), grad(b, x - 1));
    }
    
    public double octaveNoise(double x, int octaves, double persistence) {
        double total = 0;     // accumulation du bruit
        double frequency = 1; // frequence actuelle
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
    
    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }
    
    private double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }
    
    private double grad(int hash, double x) {
        return (hash & 1) == 0 ? x : -x;
    }
}
* ========================================================================== */

package fr.upc.g5e4.model;

import java.util.Random;

/**
 * Générateur de bruit de Perlin pour créer des terrains procéduraux
 */
public class PerlinNoise {
    private final int[] permutation;
    
    public PerlinNoise(long seed) {
        Random random = new Random(seed);
        permutation = new int[512];
        int[] p = new int[256];
        
        for (int i = 0; i < 256; i++) {
            p[i] = i;
        }
        
        // Mélanger le tableau
        for (int i = 255; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = p[i];
            p[i] = p[j];
            p[j] = temp;
        }
        
        // Doubler pour éviter les débordements
        for (int i = 0; i < 256; i++) {
            permutation[i] = permutation[i + 256] = p[i];
        }
    }
    
    /**
     * Calcule le bruit de Perlin pour une coordonnée x donnée
     * @param x Position
     * @return Valeur entre -1 et 1
     */
    public double noise(double x) {
        int X = (int) Math.floor(x) & 255;
        x -= Math.floor(x);
        
        double u = fade(x);
        
        int a = permutation[X];
        int b = permutation[X + 1];
        
        return lerp(u, grad(a, x), grad(b, x - 1));
    }
    
    /**
     * Bruit octave : combine plusieurs fréquences
     */
    public double octaveNoise(double x, int octaves, double persistence) {
        double total = 0;
        double frequency = 1;
        double amplitude = 1;
        double maxValue = 0;
        
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

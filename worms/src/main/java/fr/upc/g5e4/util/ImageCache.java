package fr.upc.g5e4.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Cache pour toutes les images du jeu dans un dico
 * permet de NE PAS charger plusieurs fois la même image depuis le disque CAR chaque image est chargée qu'une seule fois
 * @author Nassim
 */
public class ImageCache {
    
    // création du dico cache statique partagé dans tout le code
    private static final Map<String, BufferedImage> cache = new HashMap<>();
    
    /**
     * get une image en donnant son nom ou l'ajoute donc le dico cache si elle n'existe pas encore
     * @param path Chemin de l'image dans /resources
     * @return L'image chargée ou null en cas d'erreur
     */
    public static BufferedImage get(String path) {
        if (path == null) {
            return null;
        }
        
        // si l'image est déjà en cache on return
        if (cache.containsKey(path)) {
            return cache.get(path);
        }
        
        try {
            BufferedImage image = ImageIO.read(ImageCache.class.getResourceAsStream(path));
            if (image != null) {
                cache.put(path, image);
                System.out.println("[ImageCache] Chargée: " + path + " (" + cache.size() + " images en cache)");
            }
            return image;
        } catch (Exception e) {
            System.err.println("[ImageCache] Erreur chargement: " + path + " - " + e.getMessage());
            return null;
        }
    }
    
    /**
     * preload une liste d'images en une seule fois (pour quand on demarre le jeu)
     * @param paths Liste des chemins d'images à précharger
     */
    public static void preload(String... paths) {
        for (String path : paths) {
            get(path);
        }
    }
    
    /**
     * libere le cache
     */
    public static void clear() {
        cache.clear();
        System.out.println("[ImageCache] Cache vidé");
    }
    
    /**
     * retourne le nombre d'images en cache
     */
    public static int size() {
        return cache.size();
    }
}

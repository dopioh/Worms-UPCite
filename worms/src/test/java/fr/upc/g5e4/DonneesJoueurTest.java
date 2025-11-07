package fr.upc.g5e4;

import com.google.gson.Gson;

import fr.upc.g5e4.FileUtile;
import fr.upc.g5e4.model.DonneesJoueur;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;

/**
 * Classe de test pour DoneesJoueur
 */
public class DonneesJoueurTest {
    
    private static final File SAVE_FILE = new File("target/test-classes/joueur.json");
    private final Gson gson = new Gson();

    @Test
    public void testSerializationToJson() {
        // Création d'un joueur test
        DonneesJoueur joueur = new DonneesJoueur("MEi", 100, -10);
        
        // Conversion en JSON
        String json = gson.toJson(joueur);
        
        // Sauvegarde dans un fichier
        FileUtile.save(SAVE_FILE, json);
        
        // Lecture du fichier
        String loadedJson = FileUtile.load(SAVE_FILE);
        
        // Conversion du JSON en objet
        DonneesJoueur loadedJoueur = gson.fromJson(loadedJson, DonneesJoueur.class);
        
        // Vérification des données
        assertEquals("MEi", loadedJoueur.getName());
        assertEquals(100, loadedJoueur.getXp());
        assertEquals(-10, loadedJoueur.getMoney());
        
        // Nettoyage
        SAVE_FILE.delete();
    }

    @Test
    public void testDeserializationFromJson() {
        // Création d'un JSON test
        String json = "{\"name\":\"MEi\",\"xp\":100,\"money\":-10}";
        
        // Sauvegarde du JSON dans un fichier
        FileUtile.save(SAVE_FILE, json);
        
        // Lecture et désérialisation
        String loadedJson = FileUtile.load(SAVE_FILE);
        DonneesJoueur joueur = gson.fromJson(loadedJson, DonneesJoueur.class);
        
        // Vérification des données
        assertEquals("MEi", joueur.getName());
        assertEquals(100, joueur.getXp());
        assertEquals(-10, joueur.getMoney());
        
        // Nettoyage
        SAVE_FILE.delete();
    }
}
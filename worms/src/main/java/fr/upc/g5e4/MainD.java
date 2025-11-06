package fr.upc.g5e4;

import com.google.gson.Gson;
import java.io.File;

/** 
 * Classe test pour voir si la sérialisation et désérialisation des Données du joueur fcontionne
 */
public class MainD {
    private static final File SAVE_FILE = new File("target/sauvegarde.json"); //Fichier de sauvegarde
    private static final Gson gson = new Gson(); //Instance de Gson pour la sérialisation et désérialisation

    public static void main(String[] args) {
        DoneesJoueur joueur = new DoneesJoueur("MEi", 100, -10);

        // Sauvegarde en JSON
        String jsonData = gson.toJson(joueur);
        FileUtile.save(SAVE_FILE, jsonData);
        System.out.println("Données sauvegardées en JSON");

        // Chargement depuis JSON
        String loadedJson = FileUtile.load(SAVE_FILE);
        if (loadedJson != null) {
            joueur = gson.fromJson(loadedJson, DoneesJoueur.class);
            System.out.println("Données chargées : " + joueur);
        }
    }
}

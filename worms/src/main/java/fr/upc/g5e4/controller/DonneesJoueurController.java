package fr.upc.g5e4.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.upc.g5e4.model.DoneesJoueur;
import fr.upc.g5e4.FileUtile;
import java.io.File;

/**
 * Contrôleur pour la gestion des données du joueur (Controller)
 */
public class DoneesJoueurController {
    private final Gson gson;
    private final File saveFile;

    /**
     * Constructeur du contrôleur
     * @param saveFilePath chemin du fichier de sauvegarde
     */
    public DoneesJoueurController(String saveFilePath) {
        this.gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .disableHtmlEscaping()
            .create();
        this.saveFile = new File(saveFilePath);
    }

    /**
     * Sauvegarde les données du joueur
     * @param joueur données à sauvegarder
     * @return true si la sauvegarde a réussi
     * @return false en cas d'erreur
     */
    public boolean sauvegarderJoueur(DoneesJoueur joueur) {
        try {
            String jsonData = gson.toJson(joueur);
            FileUtile.save(saveFile, jsonData);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Charge les données du joueur
     * @return les données chargées ou null en cas d'erreur
     */
    public DoneesJoueur chargerJoueur() {
        try {
            String jsonData = FileUtile.load(saveFile);
            if (jsonData != null && !jsonData.isEmpty()) {
                return gson.fromJson(jsonData, DoneesJoueur.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
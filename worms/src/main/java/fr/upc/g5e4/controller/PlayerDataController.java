package fr.upc.g5e4.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

//import fr.upc.g5e4.FileUtile;
import fr.upc.g5e4.model.io.PlayerData;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Contrôleur pour la gestion des données du joueur (Controller)
 */
public class PlayerDataController {
    private final Gson gson;
    private final File saveFile; // contient une PlayerData convertit en Json
    private PlayerData playerData;

    /**
     * Constructeur du contrôleur
     * 
     * On initialise le fichier de sauvegarde, si il y'en a pas, il le creer
     * Sinon il le retrouve 
     * @param saveFilePath chemin du fichier de sauvegarde
     */
    public PlayerDataController() {
        File dossier = new File("sauvegardes");
        if (!dossier.exists()){
            boolean created = dossier.mkdirs(); 
            if (created) {
                System.out.println("Dossier 'sauvegardes' créé.");
            }
        }
        this.saveFile = new File(dossier, "player_save.txt");
        this.gson = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();
        this.playerData = null;
    }

    // Getter
    public PlayerData getPlayerData(){
        return this.playerData;
    }

    /**
     * Sauvegarde les données du joueur
     * @param joueur données à sauvegarder
     * @return true si la sauvegarde a réussi
     * @return false en cas d'erreur
     */
    public boolean sauvegarderJoueur() {
        try {
            FileWriter fileWriter = new FileWriter(this.saveFile);
            this.gson.toJson(playerData, fileWriter);
            System.out.println(saveFile.getAbsolutePath());
            System.out.println(saveFile.getCanonicalPath());
            System.out.println("Joueur sauvegardee !");
            fileWriter.close();
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
    public void chargerJoueur() {
        try {
            File file = new File("sauvegardes/player_save.txt");
            FileReader fileReader = new FileReader(file);
            this.playerData = gson.fromJson(fileReader, PlayerData.class);

        } catch (Exception e) {
            System.out.println("[LoadPlayer] il y'a eu une erreur imprevu lors de l'appel de la fonction 'chargerJoueur()'..");
            System.out.print("[LoadPlayer] Un fichier de sauvegarde vierge va etre creer..Renitialisation des donnees joueurs..");
            nouveauJoueur();
            sauvegarderJoueur();
           // e.printStackTrace();
        }
    }

    public void nouveauJoueur(){
        this.playerData = new PlayerData("Joueur");
    }

    /**
     * Methode a appeler avant la creation de PlayerDataController
     * @return vrai si il existe une save, faux sinon 
     */
    public static boolean isThereSavePlayer(){
        System.out.println("isThereSavePlayer() called");
        File file = new File("./sauvegardes/player_save.txt");
        if (file.exists()){
            System.out.println("Une save existe");

            return true;
        }
        //System.out.print("isThereSavePlayer() called");
        System.out.println("Aucune save existe");

        return false;
        
    }


}
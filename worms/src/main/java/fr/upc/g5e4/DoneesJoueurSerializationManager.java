package fr.upc.g5e4;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Classe pour la sérialisation et désérialisation des Données du joueur
 */

public class DoneesJoueurSerializationManager {
    private Gson gson;

    /**
     * Constructeur de la classe DoneesJoueurSerializationManager
     */
    public DoneesJoueurSerializationManager(){ 
        this.gson = createGsonInstance();
    }

/**
 * Crée une instance de Gson 
 * @return une instance de Gson 
 */
    private Gson createGsonInstance(){
        return new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();
    }


    /**
     * Sérialise un objet DoneesJoueur en JSON
     * @param joueur l'objet DoneesJoueur à sérialiser
     * @return une chaîne JSON représentant l'objet DoneesJoueur
     */
    
    public String serialize(DoneesJoueur joueur){ 
        return this.gson.toJson(joueur);
    }

    /**
     * Désérialise une chaîne JSON en un objet DoneesJoueur
     * @param json la chaîne JSON à désérialiser
     * @return l'objet DoneesJoueur désérialisé
     */

    public DoneesJoueur deseralize(String json){ //Désérialise une chaîne JSON en un objet DoneesJoueur
        return this.gson.fromJson(json, DoneesJoueur.class);
    }
}

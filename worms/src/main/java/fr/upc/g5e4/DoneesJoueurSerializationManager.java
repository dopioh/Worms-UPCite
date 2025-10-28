package fr.upc.g5e4;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DoneesJoueurSerializationManager {
    private Gson gson;


    public DoneesJoueurSerializationManager(){ //Constructeur
        this.gson = createGsonInstance();
    }

    private Gson createGsonInstance(){ //Crée une instance de Gson avec des paramètres spécifiques
        return new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();
    }



    public String serialize(DoneesJoueur joueur){ //Sérialise un objet DoneesJoueur en JSON
        return this.gson.toJson(joueur);
    }

    public DoneesJoueur deseralize(String json){ //Désérialise une chaîne JSON en un objet DoneesJoueur
        return this.gson.fromJson(json, DoneesJoueur.class);
    }
}

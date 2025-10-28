package fr.upc.g5e4;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DoneesJoueurSerializationManager {
    private Gson gson;


    public DoneesJoueurSerializationManager(){
        this.gson = createGsonInstance();
    }

    private Gson createGsonInstance(){
        return new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();
    }



    public String serialize(DoneesJoueur joueur){
        return this.gson.toJson(joueur);
    }

    public DoneesJoueur deseralize(String json){
        return this.gson.fromJson(json, DoneesJoueur.class);
    }
}

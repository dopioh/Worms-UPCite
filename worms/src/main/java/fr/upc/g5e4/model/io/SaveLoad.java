package fr.upc.g5e4.model.io;

import java.io.File;
import java.io.FileReader;

import com.google.gson.Gson;

//import java.io.File;

/**
 * Classe pour charger une save de sauvegarde.txt
 * En utilisant Gson
 * 
 * @see SaveData
 * 
 * @author Mei
 */
public class SaveLoad {
    private SaveData sv;
    
    public SaveLoad(){
        loadGame();
    }

    /**
     * Trouve le fichier sauvegarde.txt et extrait ses valeurs dans un SaveData grace a Gson
     */
    public void loadGame(){
        try {

            File file = new File("./sauvegardes/sauvegarde.txt"); // on suppose que c'est ce chemin
            if (!file.exists()|| file == null){
                System.out.println("Aucun fichier trouve..");
                return;
            }

            System.out.println(file.getAbsolutePath());
            System.out.println(file.getCanonicalPath());

            Gson gson = new Gson();
            FileReader fileReader = new FileReader(file);
            this.sv = gson.fromJson(fileReader, SaveData.class); // on recupere les valeurs que on reecrit dans un SaveData grace a Gson
            if (sv ==null){
                System.out.println("La SaveData cree a partir de "+ file.getPath() +" est null");
            }
            fileReader.close();


        } catch (Exception e) {
            System.out.println("Le chargement d'un GameLauncher pour une partie sauvegardee a rencontre une erreur : ");
            System.out.println(e.getMessage());
            //System.out.println(e.getStackTrace());
        }
    }

    public SaveData getSaveData(){
        return this.sv;
    }
}

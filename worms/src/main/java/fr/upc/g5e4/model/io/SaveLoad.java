package fr.upc.g5e4.model.io;

import java.io.File;
import java.io.FileReader;

import com.google.gson.Gson;

import fr.upc.g5e4.model.GameManager;

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

            File file = findSaveFile();

            // j'ai pris 10 en valeurs au cas ou mais on aurait tres bien pu mettre = 1
            if(file.length() < 10){
                new GameManager(); // je mets 2 ici mais ca sera modif quand on aura les custom team
            } else {
                System.out.println(file.getAbsolutePath());
                System.out.println(file.getCanonicalPath());

                Gson gson = new Gson();
                FileReader fileReader = new FileReader(file);
                this.sv = gson.fromJson(fileReader, SaveData.class); // on recupere les valeurs que on reecrit dans un SaveData grace a Gson
                if (sv ==null){
                    System.out.println("La SaveData cree a partir de "+ file.getPath() +" est null");
                }
            fileReader.close();

            }

        } catch (Exception e) {
            System.out.println("Le chargement d'un GameLauncher pour une partie sauvegardee a rencontre une erreur : ");
            System.out.println(e.getMessage());
            //System.out.println(e.getStackTrace());
        }
    }

    public SaveData getSaveData(){
        return this.sv;
    }

    public static File findSaveFile(){

        File[] tabFilePossible = new File[]{
            new File("./sauvegardes/sauvegarde.txt"),
            new File("./../sauvegardes/sauvegarde.txt"),
            new File("./../../sauvegardes/sauvegarde.txt"),
        };
        for (int i  = 0 ; i < tabFilePossible.length ;i++){
            if ( tabFilePossible[i].exists()){
                return tabFilePossible[i];
            }
        }

        try {
            // cherche un fichier avec ce chemin (si on est ici, alors le chemin devrait pas exister)
            File file = new File("./sauvegardes/sauvegarde.txt");

            // on teste si le dossier parent existe ou pas (!= pour eviter une nullPointerExceptiuon)
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs(); 
            }

            // on cree le fichier si il existe pas
            if (!file.exists()){
                file.createNewFile();
            }

            return file;

        } catch (Exception e) {
            System.out.println("[Erreur] Impossible de creer un fichier sauvegarde");
            System.out.println(e.getStackTrace());
            return null;
        }
        
    }
}

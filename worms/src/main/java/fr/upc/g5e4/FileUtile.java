package fr.upc.g5e4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Classe utilitaire pour la gestion des fichiers
 * @author Billy
 */

public class FileUtile {

    /**
     * méthode qui crée le fichier s'il n'existe pas
     * @param file le fichier à créer
     * @throws IOException en cas d'erreur lors de la création du fichier
     */
    public static void createFile(File file) throws IOException { 
        if (!file.exists()) { 
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
    }

    /**
     * méthode qui sauvegarde du texte dans un fichier
     * @param file le fichier où sauvegarder le texte
     * @param text le texte à sauvegarder
     */
    public static void save(File file, String text){ 
        final FileWriter fw;
        try {
            createFile(file);
            fw = new FileWriter(file);
            fw.write(text);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * méthode qui charge du texte depuis un fichier
     * @param file le fichier à lire
     * @return le texte charger depuis le fichier
     */
    public static String load(File file){
        if (file.exists()){
            try {
                final BufferedReader reader = new BufferedReader(new FileReader(file));
                final StringBuilder text = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null){
                    text.append(line);
                }
                reader.close();
                return text.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return "";
    }
}
package fr.upc.g5e4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtile {

    public static void createFile(File file) throws IOException { // Crée le fichier s'il n'existe pas
        if (!file.exists()) { 
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
    }

    public static void save(File file, String text){ //Permet de sauvegarder du texte dans le fichier
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

    public static String load(File file){ //Permet de charger le texte dans le fichier
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
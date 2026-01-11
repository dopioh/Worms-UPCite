package fr.upc.g5e4.model;

import fr.upc.g5e4.model.io.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe simple de test de SaveLoad
 */
public class SaveLoadTest {

    private File testSaveFile;

    @BeforeEach
    public void setUp() throws IOException {
        // creer le dossier save si il existe pas
        File saveDir = new File("./sauvegardes");
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        testSaveFile = new File("./sauvegardes/sauvegarde.txt");
    }

    @AfterEach
    public void suppFile() {
        // supp le fichier apres chaque test
        if (testSaveFile != null && testSaveFile.exists()) {
            testSaveFile.delete();
        }
    }

    @Test
    public void createsFileIfNotExists() {
        if (testSaveFile.exists()) {
            testSaveFile.delete();
        }

        File result = SaveLoad.findSaveFile();

        // on verifie
        assertNotNull(result);
        assertTrue(result.exists());
    }

    @Test
    public void ReturnsExistingFile() throws IOException {
        // on en creer un
        testSaveFile.createNewFile();
        assertTrue(testSaveFile.exists());

        File result = SaveLoad.findSaveFile();

        // on verifie
        assertNotNull(result);
        assertTrue(result.exists());
    }

    @Test
    public void ReturnsSaveData() throws IOException {

        // on creer un faux json save minimal
        String validJson = """
            {
                "round":  5,
                "currentTeamindex": 1,
                "activeWormIndex": 0,
                "teamCursor":  [1, 0],
                "roundTimeRemaining":  1500,
                "teamDatas": []
            }
            """;

        try (FileWriter writer = new FileWriter(testSaveFile)) {
            writer.write(validJson);
        }

        SaveLoad saveLoad = new SaveLoad();

        // on verifie
        assertNotNull(saveLoad.getSaveData());
        assertEquals(5, saveLoad.getSaveData().getRound());
        assertEquals(1, saveLoad.getSaveData().getCurrentTeamIndex());
        assertEquals(0, saveLoad.getSaveData().getActiveWormIndex());
        assertEquals(1500, saveLoad.getSaveData().getRoundTimeRemaining());
    }

    @Test
    public void DeletesExistingFile() throws IOException {

        testSaveFile.createNewFile();
        assertTrue(testSaveFile. exists());


        boolean result = SaveLoad.suppSaveFile();

        assertTrue(result);
        assertFalse(testSaveFile.exists()); // le file doit etre supp
    }

    @Test
    public void CreatesParentDirectories() {

        File saveDir = new File("./sauvegardes");
        if (saveDir.exists()) {
            // supp le fichier si il existe
            File[] files = saveDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }

        File result = SaveLoad.findSaveFile();

        assertNotNull(result);
        assertNotNull(result.getParentFile());
        assertTrue(result.getParentFile().exists());
    }
}
package fr.upc.g5e4.model;
import org.junit.jupiter.api.Test;

import fr.upc.g5e4.controller.*;
import fr.upc.g5e4.model.*;
import fr.upc.g5e4.view.*;

import static org.junit.jupiter.api.Assertions.*;

public class GroundModelTest {

    private static int[] groundY = {400,420,450,500,470,450,430,400,350,300};
    private int width = 800;
    private int height = 600;
    private int waterLevel = 500;
    GroundModel test = new GroundModel(groundY, width, height, waterLevel);

    @Test
    public void testGetGroundHeightAtStart() {
        // x = 0 → doit renvoyer le premier point
        assertEquals(groundY[0], test.getGroundHeightAt(0));
    }

    @Test
    public void testGetGroundHeightAtEnd() {
        // x = width → doit renvoyer le dernier point
        assertEquals(groundY[groundY.length - 1], test.getGroundHeightAt(width));
    }

    @Test
    public void testGetGroundHeightAtMiddle() {
        // x au milieu → doit renvoyer une valeur entre deux points
        double x = width / 2.0;
        double heightAtX = test.getGroundHeightAt(x);

        // vérification basique : hauteur dans les limites
        double min = 200; // plus petit point dans groundY
        double max = 480; // plus grand point
        assertTrue(heightAtX >= min && heightAtX <= max, "La hauteur à x=" + x + " doit être entre " + min + " et " + max);
    }

    @Test
    public void testWaterLevel() {
        assertEquals(waterLevel, test.getWaterLevel());
    }

    @Test
    public void testWidth() {
        assertEquals(width, test.getWidth());
    }

    @Test
    public void testHeight() {
        assertEquals(height, test.getHeight());
    }
    @Test
    public void testGroundYArrayIntegrity() {
        // Vérifie que le tableau groundY n’a pas été modifié
        assertArrayEquals(groundY, test.getGroundY());
    }
    
}

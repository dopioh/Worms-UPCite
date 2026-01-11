package fr.upc.g5e4.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Classe de test pour @see WindModel */
class WindModelTest {
    @Test
    void DefaultDirection() {       
        WindModel wind = new WindModel();

        assertEquals(WindModel.Dir.E, wind.getDir());
    }

    @Test
    void DefaultStrength() {
        WindModel wind = new WindModel();

        assertEquals(0.5f, wind.getStrength());
    }

    @Test
    void changeDirection() {
        WindModel wind = new WindModel();

        wind.setDir(WindModel.Dir.N);

        assertEquals(WindModel.Dir.N, wind.getDir());
    }

    @Test
    void clampStrengthNotSavedTooHigh() {   // Le vent ne pas pas dépasser 1 
        WindModel wind = new WindModel();

        wind.setStrength(2.0f, false);

        assertEquals(1.0f, wind.getStrength());
    }

    @Test
    void clampStrengthNotSavedTooLow() { // pas négatif
        WindModel wind = new WindModel();

        wind.setStrength(-1.0f, false);

        assertEquals(0.0f, wind.getStrength());
    }

    @Test
    void notClampStrengthSaved() { // Garde la valeur en cas de sauvegarde
        WindModel wind = new WindModel();

        wind.setStrength(1.0f, true);

        assertEquals(1.0f, wind.getStrength());
    }
}

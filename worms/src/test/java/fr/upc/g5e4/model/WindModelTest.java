package fr.upc.g5e4.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WindModelTest {

    private static final float EPS = 1e-6f; // Epsilon pour les comparaisons de flottants (Imprécision possibles pour les float)

    @Test //Direction par défaut
    void getDir_Default() {
        WindModel wind = new WindModel();
        assertEquals(WindModel.Dir.E, wind.getDir());
    }

    @Test //Force par défaut
    void getStrength_Default() {
        WindModel wind = new WindModel();
        assertEquals(0.5f, wind.getStrength(), EPS);
    }

    @Test //Changer la direction du vent    
    void setDir_changesDirection() {
        WindModel wind = new WindModel();
        wind.setDir(WindModel.Dir.NE);
        assertEquals(WindModel.Dir.NE, wind.getDir());
    }

    @Test //Accepter une direction "null"
    void setDir_allowsNull() {
        WindModel wind = new WindModel();
        wind.setDir(null);
        assertNull(wind.getDir());
    }

    @Test
    void setStrength_whenSaveFalse_clampsAboveOneToOne() {
        WindModel wind = new WindModel();
        wind.setStrength(1.7f, false);
        assertEquals(1.0f, wind.getStrength(), EPS);
    }

    @Test
    void setStrength_whenSaveFalse_clampsBelowZeroToZero() {
        WindModel wind = new WindModel();
        wind.setStrength(-0.2f, false);
        assertEquals(0.0f, wind.getStrength(), EPS);
    }

    @Test
    void setStrength_whenSaveFalse_keepsValueInsideRange() {
        WindModel wind = new WindModel();
        wind.setStrength(0.8f, false);
        assertEquals(0.8f, wind.getStrength(), EPS);
    }

    @Test
    void setStrength_whenSaveFalse_acceptsExactBounds() {
        WindModel wind = new WindModel();
        wind.setStrength(0.0f, false);
        assertEquals(0.0f, wind.getStrength(), EPS);

        wind.setStrength(1.0f, false);
        assertEquals(1.0f, wind.getStrength(), EPS);
    }

    @Test
    void setStrength_whenSaveTrue_doesNotClamp() {
        WindModel wind = new WindModel();
        wind.setStrength(2.5f, true);
        assertEquals(2.5f, wind.getStrength(), EPS);

        wind.setStrength(-3.0f, true);
        assertEquals(-3.0f, wind.getStrength(), EPS);
    }

    @Test
    void dirEnum_VectorsAndLabels() {
        assertEquals(0, WindModel.Dir.N.dx);
        assertEquals(-1, WindModel.Dir.N.dy);
        assertEquals("N", WindModel.Dir.N.label);

        assertEquals(1, WindModel.Dir.SE.dx);
        assertEquals(1, WindModel.Dir.SE.dy);
        assertEquals("SE", WindModel.Dir.SE.label);

        assertEquals(-1, WindModel.Dir.O.dx);
        assertEquals(0, WindModel.Dir.O.dy);
        assertEquals("O", WindModel.Dir.O.label);
    }
}

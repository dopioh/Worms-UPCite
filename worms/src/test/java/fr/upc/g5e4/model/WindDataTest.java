package fr.upc. g5e4.model;

import fr. upc.g5e4.model.io.*;


import fr. upc.g5e4.model.WindModel.Dir;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit. jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter. MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test simple de WindData 
 * @see WindData
 */
@ExtendWith(MockitoExtension.class)
public class WindDataTest {

    @Mock
    private WindModel mockWindModel;

    private WindData windData;

    @BeforeEach
    public void setUp() {
        windData = new WindData(1, -1, "NE", 0.75f);
    }

    @Test
    public void testConstructorWithAllDirections() {
        // teste avec toutes les directions possibles
        WindData north = new WindData(0, -1, "N", 0.5f);
        assertEquals("N", north.getLabel());
        assertEquals(0, north.getDx());
        assertEquals(-1, north.getDy());

        WindData south = new WindData(0, 1, "S", 0.5f);
        assertEquals("S", south.getLabel());
        assertEquals(0, south.getDx());
        assertEquals(1, south.getDy());

        WindData east = new WindData(1, 0, "E", 0.5f);
        assertEquals("E", east.getLabel());
        assertEquals(1, east.getDx());
        assertEquals(0, east.getDy());

        WindData west = new WindData(-1, 0, "O", 0.5f);
        assertEquals("O", west.getLabel());
        assertEquals(-1, west.getDx());
        assertEquals(0, west.getDy());
    }

    @Test
    public void testWindModelToWindDataWithAllDirections() {
        // test pour chaque direction de l'enum
        for (Dir dir : Dir.values()) {

            // on mock les valeurs du WindModel
            reset(mockWindModel);
            when(mockWindModel.getDir()).thenReturn(dir);
            when(mockWindModel. getStrength()).thenReturn(0.5f);

            WindData result = WindData.WindModelToWindData(mockWindModel);

            // faut refaire les tests si l'un casse psq les suivants sont pas executer
            assertEquals(dir. dx, result.getDx());
            assertEquals(dir.dy, result.getDy());
            assertEquals(dir.label, result.getLabel());
        }
    }

    @Test
    public void testApplyToWindModelWithAllDirections() {
        WindModel realWindModel = new WindModel();

        // teste chaque direction
        String[][] directions = {
            {"0", "-1", "N"},
            {"1", "-1", "NE"},
            {"1", "0", "E"},
            {"1", "1", "SE"},
            {"0", "1", "S"},
            {"-1", "1", "SO"},
            {"-1", "0", "O"},
            {"-1", "-1", "NO"}
        };

        for (String[] dirInfo : directions) {
            int dx = Integer.parseInt(dirInfo[0]);
            int dy = Integer.parseInt(dirInfo[1]);
            String label = dirInfo[2];

            WindData data = new WindData(dx, dy, label, 0.5f);
            data.applyToWindModel(realWindModel);

            assertEquals(label, realWindModel.getDir().label,
                "Direction should be " + label);
        }
    }


    @Test
    void testConversionModelTODataTOModel() {
        // creer un WindModel, le converti en WindData, puis l'applique a un nouveau WindModel
        WindModel original = new WindModel();
        original.setDir(Dir.SO);
        original.setStrength(0.65f, false);

        // converti en WindData
        WindData data = WindData. WindModelToWindData(original);

        // applique a un nouveau WindModel
        WindModel restored = new WindModel();
        data.applyToWindModel(restored);

        // verifier que les valeurs sont identiques
        assertEquals(original.getDir(), restored.getDir());
        assertEquals(original.getStrength(), restored.getStrength());
    }
}

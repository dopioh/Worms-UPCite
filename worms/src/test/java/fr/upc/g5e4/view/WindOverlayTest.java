package fr.upc.g5e4.view;

import fr.upc.g5e4.model.WindModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WindOverlayTest {

    @Test
    void updateNoCrash() {
        WindModel wind = mock(WindModel.class);
        when(wind.getDir()).thenReturn(WindModel.Dir.E);
        when(wind.getStrength()).thenReturn(0.5f);

        WindOverlay overlay = new WindOverlay(wind);

        assertDoesNotThrow((org.junit.jupiter.api.function.Executable) overlay::update);
    }

    @Test
    void paintNoCrash() {
        WindModel wind = mock(WindModel.class);
        when(wind.getDir()).thenReturn(WindModel.Dir.NE);
        when(wind.getStrength()).thenReturn(0.8f);

        WindOverlay overlay = new WindOverlay(wind);
        overlay.setSize(300, 200);

        assertDoesNotThrow(() -> {
            java.awt.image.BufferedImage img =
                    new java.awt.image.BufferedImage(300, 200, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            java.awt.Graphics2D g2 = img.createGraphics();
            overlay.paint(g2);
            g2.dispose();
        });
    }
}

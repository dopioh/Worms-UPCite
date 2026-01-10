package fr.upc.g5e4;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.awt.Color;
import fr.upc.g5e4.view.CharacterView;
import fr.upc.g5e4.view.NameTagView;

/**
 * Tests unitaires pour PersonnageView (avec Junit)
 * @author Nassim
 */
public class CharacterViewTest {
    
    // @Test Constructeur
    
    @Test
    public void testDefaultConstructor() {
        CharacterView v = new CharacterView();
    
        assertEquals(15, v.getSize());
        assertEquals(new Color(255, 150, 0), v.getColor());
        assertNotNull(v.getNameTagView());
    }
    
    @Test
    public void testConstructorWithParameters() {
        Color customColor = new Color(0, 255, 0); // Vert
        CharacterView v = new CharacterView(50, customColor);

        assertEquals(50, v.getSize());
        assertEquals(customColor, v.getColor());
        assertNotNull(v.getNameTagView());
    }
    
    // @Test Getters
    
    @Test
    public void testGetSize() {
        CharacterView view = new CharacterView();
        assertEquals(15, view.getSize());
    }
    
    @Test
    public void testGetColor() {
        CharacterView view = new CharacterView();
        assertEquals(new Color(255, 150, 0), view.getColor());
    }
    
    @Test
    public void testGetNameTagView() {
        CharacterView view = new CharacterView();
        assertNotNull(view.getNameTagView());
        assertTrue(view.getNameTagView() instanceof NameTagView);
    }
    
    // @Test Setters
    
    @Test
    public void testSetSize() {
        CharacterView view = new CharacterView();
        view.setSize(60);
        assertEquals(60, view.getSize());
    }
    
    @Test
    public void testSetColor() {
        CharacterView view = new CharacterView();
        Color nouvelleCouleur = new Color(255, 0, 0);
        view.setColor(nouvelleCouleur);
        assertEquals(nouvelleCouleur, view.getColor());
    }
    
}

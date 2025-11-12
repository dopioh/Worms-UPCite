package fr.upc.g5e4;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.awt.Color;
import fr.upc.g5e4.view.PersonnageView;
import fr.upc.g5e4.view.NameTagView;

/**
 * Tests unitaires pour PersonnageView (avec Junit)
 * 
 */
public class PersonnageViewTest {
    
    // @Test Constructeur
    
    @Test
    public void testDefaultConstructor() {
        PersonnageView v = new PersonnageView();
    
        assertEquals(25, v.getSize());
        assertEquals(new Color(255, 150, 0), v.getColor());
        assertNotNull(v.getNameTagView());
    }
    
    @Test
    public void testConstructorWithParameters() {
        Color customColor = new Color(0, 255, 0); // Vert
        PersonnageView v = new PersonnageView(50, customColor);

        assertEquals(50, v.getSize());
        assertEquals(customColor, v.getColor());
        assertNotNull(v.getNameTagView());
    }
    
    // @Test Getters
    
    @Test
    public void testGetSize() {
        PersonnageView view = new PersonnageView();
        assertEquals(25, view.getSize());
    }
    
    @Test
    public void testGetColor() {
        PersonnageView view = new PersonnageView();
        assertEquals(new Color(255, 150, 0), view.getColor());
    }
    
    @Test
    public void testGetNameTagView() {
        PersonnageView view = new PersonnageView();
        assertNotNull(view.getNameTagView());
        assertTrue(view.getNameTagView() instanceof NameTagView);
    }
    
    // @Test Setters
    
    @Test
    public void testSetSize() {
        PersonnageView view = new PersonnageView();
        view.setSize(60);
        assertEquals(60, view.getSize());
    }
    
    @Test
    public void testSetColor() {
        PersonnageView view = new PersonnageView();
        Color nouvelleCouleur = new Color(255, 0, 0);
        view.setColor(nouvelleCouleur);
        assertEquals(nouvelleCouleur, view.getColor());
    }
    
}

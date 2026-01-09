package fr.upc.g5e4.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour TutorialModel
 */
public class TutorialModelTest {
    
    private TutorialModel model;
    
    @BeforeEach
    void setUp() {
        model = new TutorialModel();
    }
    
    // Tests d'initialisation
    
    @Test
    void testModelInitialization() {
        assertNotNull(model, "Le model ne doit pas être null");
    }
    
    @Test
    void testInitialPage() {
        assertEquals(0, model.getCurrentPage(), "La page initiale doit être 0");
    }
    
    @Test
    void testGetCurrentPageContentNotNull() {
        assertNotNull(model.getCurrentPageContent(), "Le contenu de la page ne doit pas être null");
    }
    
    @Test
    void testGetCurrentPageContentNotEmpty() {
        String[] content = model.getCurrentPageContent();
        assertTrue(content.length > 0, "Le contenu de la page ne doit pas être vide");
    }
    
    // Tests de navigation
    
    @Test
    void testNextPageIncreasesCurrentPage() {
        int initialPage = model.getCurrentPage();
        model.nextPage();
        
        assertEquals(initialPage + 1, model.getCurrentPage(), "nextPage doit incrémenter la page courante");
    }
    
    @Test
    void testNextPageReturnsTrueWhenSuccess() {
        boolean result = model.nextPage();
        assertTrue(result, "nextPage doit retourner true si le changement a réussi");
    }
    
    @Test
    void testNextPageReturnsFalseAtLastPage() {
        // aller à la dernière page
        while (model.hasNextPage()) {
            model.nextPage();
        }
        
        boolean result = model.nextPage();
        assertFalse(result, "nextPage doit retourner false à la dernière page");
    }
    
    @Test
    void testCannotGoNextTheLastPage() {
        // aller à la dernière page
        while (model.hasNextPage()) {
            model.nextPage();
        }
        
        int lastPage = model.getCurrentPage();
        model.nextPage();
        
        assertEquals(lastPage, model.getCurrentPage(), "La page devrait rester la même");
    }
    
    @Test
    void testPreviousPageDecreasesCurrentPage() {
        model.nextPage();
        int currentPage = model.getCurrentPage();
        model.previousPage();
        
        assertEquals(currentPage - 1, model.getCurrentPage(), "previousPage doit décrémenter la page courante");
    }
    
    @Test
    void testPreviousPageReturnsTrueWhenSuccess() {
        model.nextPage();
        boolean result = model.previousPage();
        assertTrue(result, "previousPage doit retourner true si le changement a réussi");
    }
    
    @Test
    void testPreviousPageReturnsFalseAtFirstPage() {
        boolean result = model.previousPage();
        assertFalse(result, "previousPage doit retourner false à la première page");
    }
    
    @Test
    void testCannotGoPreviousTheFirstPage() {
        int firstPage = model.getCurrentPage();
        model.previousPage();
        
        assertEquals(firstPage, model.getCurrentPage(), "La page doit rester la même");
    }
    
    // Tests de hasNextPage et hasPreviousPage
    
    @Test
    void testHasNextPageOnFirstPage() {
        if (model.getTotalPages() > 1) {
            assertTrue(model.hasNextPage(), "hasNextPage doit retourner true s'il y a plus d'une page");
        }
    }
    
    @Test
    void testHasNextPageOnLastPage() {
        while (model.hasNextPage()) {
            model.nextPage();
        }
        
        assertFalse(model.hasNextPage(), "hasNextPage doit retourner false à la dernière page");
    }
    
    @Test
    void testHasPreviousPageOnFirstPage() {
        assertFalse(model.hasPreviousPage(), "hasPreviousPage doit retourner false à la première page");
    }
    
    @Test
    void testHasPreviousPageOnSecondPage() {
        model.nextPage();
            assertTrue(model.hasPreviousPage(), "hasPreviousPage doit retourner true à partir de la deuxième page");
    }
    
    // Tests de reset
    
    @Test
    void testResetGoesToFirstPage() {
        model.nextPage();
        model.nextPage();
        
        model.reset();
        
        assertEquals(0, model.getCurrentPage(), "reset doit ramener à la page 0");
    }
    
    @Test
    void testResetFromFirstPage() {
        model.reset();
        assertEquals(0, model.getCurrentPage(), "reset depuis la première page doit rester à 0");
    }
    
    @Test
    void testResetFromLastPage() {
        while (model.hasNextPage()) {
            model.nextPage();
        }
        
        model.reset();
        
        assertEquals(0, model.getCurrentPage(), "reset depuis la dernière page doit ramener à 0");
    }
}

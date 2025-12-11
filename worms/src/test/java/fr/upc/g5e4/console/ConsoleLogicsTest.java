package fr.upc.g5e4.console;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.googlecode.lanterna.screen.Screen;

import fr.upc.g5e4.controller.CharacterController;
import fr.upc.g5e4.controller.console.ConsoleInputController;
import fr.upc.g5e4.model.console.ConsoleLogics;
import fr.upc.g5e4.view.console.ConsoleView;

/**
 * Tests unitaires pour ConsoleLogics
 * @see ConsoleLogics
 */
@ExtendWith(MockitoExtension.class)
public class ConsoleLogicsTest {

    @Mock
    private CharacterController characterController;

    @Mock
    private ConsoleInputController consoleInputController;

    @Mock
    private ConsoleView consoleView;

    @Mock
    private Screen screen;

    private ConsoleLogics consoleLogics;

    @BeforeEach
    public void setUp() throws Exception {

        consoleLogics = new ConsoleLogics(characterController);

        // on inject des mock pour remplacer les vrais objets qui sont instancier a l'interieur de consoleLogics
        // voir la methode en dessous
        injectMock(consoleLogics, "consoleInput", consoleInputController);
        injectMock(consoleLogics, "consoleView", consoleView);
        injectMock(consoleLogics, "screen", screen);
    }

    @Test
    void testLaunchGameSuccess() throws Exception {

        // on simule le bon mot pour lancer le jeu
        when(consoleInputController.addLetters(screen)).thenReturn(
            'l', 'a', 'u', 'n', 'c', 'h', ' ', 'g', 'a', 'm', 'e'
        );

        // gere la boucle while des char entrees, doit renvoyer vrai que quand la touche entree est pressed
        when(consoleInputController.getEnterKey()).thenReturn(
            false, false, false, false, false, false, false, false, false, false, false, // 11 fois false (pendant la frappe)
            true // La 12ème fois, c'est true (sortie de boucle)
        );


        // grace aux appels de methodes avant, c'est comme si l'utilisateur avait ecris "lunch game"
        // on teste ici si la game peut se lancer (return true) apres avoir ecrit le bon mot
        boolean result = consoleLogics.launchGame();
 
        assertTrue(result, "launchGame doit retourner true si on écrit 'launch game'");
        
        // verifie que la lettre est print sur le terminal
        verify(consoleView).printLetterMenu('l');

        // Vérifions que le setter a été appelé pour reset l'entrée à la fin
        verify(consoleInputController).setEnterPressed(false);
    }

    /**
     * Methode pour remplacer le vrai objet par son mock
     * {@code Field} objet qui represente ube variable dans une classe 
     * Permet de la modif en contournant son status private pdt qu'il s'execute
     * 
     * @param target l'objet ou on va modifier ses variables
     * @param fieldName le nom du champ a modifier
     * @param mock on va remplacer le vrai objet instancier par son mock creer avant
     */
    private void injectMock(Object target, String fieldName, Object mock) throws Exception {

        // trouve la variable a modifier 
        Field field = target.getClass().getDeclaredField(fieldName);

        // on fait sauter le private (juste pour cette execution)
        field.setAccessible(true);

        // on remplace par ce qu'on veut 
        field.set(target, mock);
    }
}

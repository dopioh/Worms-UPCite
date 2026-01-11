package fr.upc.g5e4.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// import org.junit.jupiter.api.BeforeEach;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import fr.upc.g5e4.controller.CharacterController;
import fr.upc.g5e4.controller.console.ConsoleInputController;
import fr.upc.g5e4.model.console.ConsoleGameManager;

/**
 * Tests unitaires pour ConsoleInputController
 * @see ConsoleInputController
 */
@ExtendWith(MockitoExtension.class)     // active mockito
public class ConsoleInputControllerTest {

    // creation des mocks
    @Mock
    private CharacterController characterController;
    
    @Mock
    private ConsoleGameManager consoleGameManager;
    
    @Mock
    private Screen screen; 

    // on injecte les mock dans la classe a tester (ici ConsolleInputController)
    @InjectMocks
    private ConsoleInputController consoleInputController;

    @Test
    void testSetEnterPressed() {
        consoleInputController.setEnterPressed(true);
        assertTrue(consoleInputController.getEnterKey());
    }

    @Test
    void testAddControlsJumpZ() throws IOException {
        
        // simule un appuie sur z comme si on avait vrmt appuye sur la touche
        KeyStroke keyZ = new KeyStroke('z', false, false); // touche z pressee
        when(screen.pollInput()).thenReturn(keyZ);  // quand screen.pollInput est called, renvoie keyZ

        // appel la methode -> garce a keyZ, il retourne ca des que la methode appelle pollInput
        // active le jump dcp
        consoleInputController.addControlsGame(screen);

        // verifie que la methode a ete appeler dans CharacterController
        verify(characterController).activateJump();

    }

    @Test
    void testAddControlsRightD() throws IOException {

        KeyStroke keyD = new KeyStroke('d', false, false);
        when(screen.pollInput()).thenReturn(keyD);

        consoleInputController.addControlsGame(screen);

        verify(characterController).activateRight();

        // verifie qu'on a pas sauter par erreur (utile ? pas vraiment mais je le mets au cas ou)
        verify(characterController, never()).activateJump();
    }

    @Test
    void testAddControlsLeftQ() throws IOException {

        KeyStroke keyQ = new KeyStroke('q', false, false);
        when(screen.pollInput()).thenReturn(keyQ);


        consoleInputController.addControlsGame(screen);

        verify(characterController).activateLeft();
        verify(characterController, never()).activateJump();
    }

    @Test
    void testAddControlsNextRoundA() throws IOException {

        KeyStroke keyA = new KeyStroke('a', false, false);
        when(screen.pollInput()).thenReturn(keyA);

        consoleInputController.addControlsGame(screen);

        verify(consoleGameManager).nextRound();

        // verifie qu'on a pas declencher de mouvement 
        verify(characterController, never()).activateJump();
        verify(characterController, never()).activateRight();
        verify(characterController, never()).activateLeft();

    }

    @Test
    void testAddLettersReturnsChar() throws IOException {

        KeyStroke keyH = new KeyStroke('H', false, false);
        when(screen.readInput()).thenReturn(keyH);

        Character result = consoleInputController.addLetters(screen);

        assertEquals('H', result);

        // verifie que le else if n'a pas ete executee si on a 'H'
        assertFalse(consoleInputController.getEnterKey());
    }

    @Test
    void testAddLettersEnterPressed() throws IOException {

        KeyStroke keyEnter = new KeyStroke(KeyType.Enter);
        when(screen.readInput()).thenReturn(keyEnter);


        Character result = consoleInputController.addLetters(screen);

        // doit return null
        assertNull(result); 
        // doit appeler la methode setEnterKey(true) 
        // et donc verifie que getEnterKey soit true aussi
        assertTrue(consoleInputController.getEnterKey()); 
    }
}


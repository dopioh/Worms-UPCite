package fr.upc.g5e4.model;

import fr. upc.g5e4.model.io.*;
import org.jbox2d.dynamics.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api. extension.ExtendWith;
import org. mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api. Assertions.*;


/**
 * Test Mockito simple de CharacterData
 * @see CharacterData
 */
@ExtendWith(MockitoExtension.class)
public class CharacterDataTest {

    @Mock
    private GroundModel mockGroundModel;

    @Mock
    private World mockWorld;

    private CharacterData characterData;

    @BeforeEach
    void setUp() {
        characterData = new CharacterData(1,"Worm-1",150,300,100, 50,false,true);
    }


    // test que un Worm dead EST BIEN dead 
    @Test
    void testCharacterDataWithDeadWorm() {
        CharacterData deadWorm = new CharacterData(
            2, "Worm-2", 200, 400, 0, 0, true, false);

        assertEquals(2, deadWorm.getID()); // ca vaut vrmt le coup de le mettre..?
        assertTrue(deadWorm. getIsDead());
        assertFalse(deadWorm.getFacingRight());
        assertEquals(0.0, deadWorm.getHealth());
    }

    @Test
    void testCharacterDataWithZeroShield() {
        CharacterData noShieldWorm = new CharacterData(
            3, "Worm-3", 100, 200, 75, 0, false, true);

        assertEquals(0, noShieldWorm.getShield());
        assertEquals(75, noShieldWorm.getHealth());
    }

    // on verifie pour toutes les donnees d'un coup
    @Test
    void testCharacterDataPreservesAllFields() {
        CharacterData data = new CharacterData(
            42, "TestWorm", 123.456, 789.012, 85, 25, false, true);

        assertEquals(42, data. getID(),"L'ID ne correspond pas, les autres assert n'ont pas pu etre testes, veuillez corriger ce test et le relancer.");
        assertEquals("TestWorm", data.getName(),"Le nom ne correspond pas, les autres assert n'ont pas pu etre testes, veuillez corriger ce test et le relancer.");
        assertEquals(123.456, data.getXcord(),"Xcord ne correspond pas, les autres assert n'ont pas pu etre testes, veuillez corriger ce test et le relancer.");
        assertEquals(789.012, data.getYcord(), "Ycord  ne correspond pas, les autres assert n'ont pas pu etre testes, veuillez corriger ce test et le relancer.");
        assertEquals(85, data. getHealth(), "La barre de vie ne correspond pas, les autres assert n'ont pas pu etre testes, veuillez corriger ce test et le relancer.");
        assertEquals(25, data.getShield(), "La barre de shield ne correspond pas, les autres assert n'ont pas pu etre testes, veuillez corriger ce test et le relancer.");
        assertFalse(data.getIsDead(),"Le Worm a comme valeur mort alors que il est vivant, les autres assert n'ont pas pu etre testes, veuillez corriger ce test et le relancer.");
        assertTrue(data.getFacingRight(), "Mauvais FacingRight");
    }
}
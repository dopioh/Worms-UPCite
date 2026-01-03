package fr.upc. g5e4.model;

import fr. upc.g5e4.model.io.*;
import fr.upc.g5e4.model.InventoryModel.InventoryJson;
import org.jbox2d.dynamics.World;
import org.junit.jupiter.api. BeforeEach;
import org.junit.jupiter.api. Test;
import org.junit.jupiter. api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api. Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test Mockito simple de TeamData
 * @see TeamData
 */
@ExtendWith(MockitoExtension.class)
public class TeamDataTest {

    @Mock
    private GroundModel mockGroundModel;

    @Mock
    private World mockWorld;

    @Mock
    private CharacterModel mockCharacterModel1;

    @Mock
    private CharacterModel mockCharacterModel2;

    @Mock
    private CharacterModel mockCharacterModel3;

    private TeamData teamData;
    private CharacterModel[] characterModels;
    private List<InventoryJson> inventory;

    @BeforeEach
    void setUp() {
        // configurer les mocks pour toData()
        CharacterData charData1 = new CharacterData(0, "Worm-0", 100, 200, 100, 50, false, true);
        CharacterData charData2 = new CharacterData(1, "Worm-1", 150, 200, 100, 50, false, true);
        CharacterData charData3 = new CharacterData(2, "Worm-2", 200, 200, 100, 50, false, true);

        when(mockCharacterModel1.toData()).thenReturn(charData1);
        when(mockCharacterModel2.toData()).thenReturn(charData2);
        when(mockCharacterModel3.toData()).thenReturn(charData3);

        characterModels = new CharacterModel[]{
            mockCharacterModel1, mockCharacterModel2, mockCharacterModel3
        };

        // creer un inventaire de test sans mock
        inventory = new ArrayList<>();
        InventoryJson item = new InventoryJson();
        item.name = "Ak47";
        item. objectType = "WEAPON";
        item.maxUsages = 1;
        item. isInfinite = false;
        item.remainingUses = 30;
        inventory.add(item);

        int money = 0;

        teamData = new TeamData(1, 3, characterModels, "Team 1", 0, inventory, money);
    }

    // test getter tab CharacterData
    @Test
    void testGetTabCharacterData() {
        CharacterData[] charDatas = teamData.getTabCharacterData();
        
        assertNotNull(charDatas, "charDatas ne doit pas etre nul puisque on l'a initialise avant le lancement de ce Test");
        assertEquals(3, charDatas.length);
        assertEquals("Worm-0", charDatas[0].getName());
        assertEquals("Worm-1", charDatas[1].getName());
        assertEquals("Worm-2", charDatas[2]. getName());
    }

    //tests de la conversion Model -> Data

    @Test
    void testConstructorCallsToDataOnAllCharacterModels() {
        // verifie que toData() a ete called sur chaque CharacterModel
        verify(mockCharacterModel1).toData();
        verify(mockCharacterModel2).toData();
        verify(mockCharacterModel3).toData();
    }

    @Test
    void testCharacterDataPreservesPositions() {
        CharacterData[] charDatas = teamData.getTabCharacterData();

        assertEquals(100, charDatas[0].getXcord());
        assertEquals(150, charDatas[1].getXcord());
        assertEquals(200, charDatas[2].getXcord());
    }

    @Test
    void testTeamDataWithSingleWorm() {
        CharacterModel[] singleWorm = new CharacterModel[]{mockCharacterModel1};
        
        // reset le mock pour ce test
        reset(mockCharacterModel1);

        when(mockCharacterModel1.toData()).thenReturn(
            new CharacterData(0, "Solo-Worm", 100.0, 200.0, 100.0, 50.0, false, true)
        );

        int money = 0;

        TeamData singleTeam = new TeamData(2, 1, singleWorm, "Solo Team", 1, null, money);

        assertEquals(1, singleTeam.getWormsNumber());
        assertEquals(1, singleTeam.getTabCharacterData().length);
    }

    @Test
    void testTeamDataWithNullInventory() {
        int money = 0;
        TeamData teamWithNullInventory = new TeamData(
            3, 3, characterModels, "No Inventory Team", 2, null, money
        );

        assertEquals("No Inventory Team", teamWithNullInventory.getTeamName());
        assertEquals(3, teamWithNullInventory.getWormsNumber());
    }

    @Test
    void testTeamDataWithDifferentColorTeam() {
        reset(mockCharacterModel1);
        when(mockCharacterModel1.toData()).thenReturn(
            new CharacterData(0, "Worm", 100, 200, 100, 50, false, true)
        );

        CharacterModel[] worms = new CharacterModel[]{mockCharacterModel1};

        // test differentes couleurs (0 = RED, 1 = BLUE et 2 = PINKKKKKKKKK)
        int money = 0;
        TeamData redTeam = new TeamData(1, 1, worms, "Red Team", 0, null, money);
        TeamData blueTeam = new TeamData(2, 1, worms, "Blue Team", 1, null, money);
        TeamData pinkTeam = new TeamData(3, 1, worms, "Pink Team", 2, null, money);

        assertNotNull(redTeam);
        assertNotNull(blueTeam);
        assertNotNull(pinkTeam);
    }
}
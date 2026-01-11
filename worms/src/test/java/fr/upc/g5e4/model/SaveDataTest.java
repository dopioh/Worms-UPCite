package fr.upc.g5e4.model;

import fr.upc.g5e4.model.io.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Classe simple qui teste SaveData
 */
@ExtendWith(MockitoExtension.class)
public class SaveDataTest {

    @Mock
    private GameManager mockGameManager;

    @Mock
    private GroundModel mockGroundModel;

    @Mock
    private BlockGridModel mockBlockGridModel;

    @Mock
    private WindModel mockWindModel;

    @Mock
    private TeamModel mockTeamModel1;

    @Mock
    private TeamModel mockTeamModel2;

    private ArrayList<TeamModel> teamModels;
    private Map<Integer, GameStats> teamStats;

    @BeforeEach
    public void setUp() {
        teamModels = new ArrayList<>();
        teamModels.add(mockTeamModel1);
        teamModels. add(mockTeamModel2);

        teamStats = new HashMap<>();
        teamStats.put(0, new GameStats());
        teamStats.put(1, new GameStats());

        // background par defaut
        MapConfig.setBackgroundStyle(MapConfig.BackgroundStyle.JOUR);
    }

    /**
     * Test que le constructeur enregistre bien les donnees
     */
    @Test
    public void testSaveDataConstructeur() { // est ce vraiment utile comme test finalement ??

        // on dit ce que renvoie chaque fonction mock mock
        when(mockGameManager.getRoundNumber()).thenReturn(5);
        when(mockGameManager.getCurrentTeamIndex()).thenReturn(1);
        when(mockGameManager.getActiveWormIndex()).thenReturn(2);
        when(mockGameManager.getTeamCursor()).thenReturn(new int[]{1, 2});

        when(mockGameManager.getRoundTimeSave()).thenReturn(1500);
        when(mockGameManager.getGroundModel()).thenReturn(mockGroundModel);
        when(mockGameManager.getlistTeams()).thenReturn(teamModels);
        when(mockGameManager.getAllTeamStats()).thenReturn(teamStats);
        when(mockGameManager.getWindModel()).thenReturn(mockWindModel);
        when(mockGameManager.getBlockGridModel()).thenReturn(mockBlockGridModel);

        // mock mock toData()
        TeamData mockTeamData1 = mock(TeamData.class);
        TeamData mockTeamData2 = mock(TeamData.class);
        when(mockTeamModel1.toData()).thenReturn(mockTeamData1);
        when(mockTeamModel2.toData()).thenReturn(mockTeamData2);

        // mock mock WindModel
        when(mockWindModel.getDir()).thenReturn(WindModel.Dir.E);
        when(mockWindModel.getStrength()).thenReturn(0.5f);

        SaveData saveData = new SaveData(mockGameManager);

        // on verifie que saveData a bien recup les memes valeurs que les mock mock des guetter du faux gameManager
        assertEquals(5, saveData.getRound());
        assertEquals(1, saveData.getCurrentTeamIndex());
        assertEquals(2, saveData. getActiveWormIndex());
        assertArrayEquals(new int[]{1, 2}, saveData.getTeamCursor());
        assertEquals(1500, saveData.getRoundTimeRemaining());
        assertEquals(mockGroundModel, saveData.getGroundModel());
        assertEquals(mockBlockGridModel, saveData.getBlockGridModel());
        assertEquals(2, saveData.getListTeamData().size());
        assertEquals(teamStats, saveData.getAllTeamStats());
        assertNotNull(saveData.getWindData());
    }

    /**
     * on passe de TeamModel a TeamData
     */
    @Test
    public void testModelToData_ConvertsAllTeams() {
        
        // on re mock mock
        when(mockGameManager.getRoundNumber()).thenReturn(1);
        when(mockGameManager.getCurrentTeamIndex()).thenReturn(0);
        when(mockGameManager.getActiveWormIndex()).thenReturn(0);
        when(mockGameManager. getTeamCursor()).thenReturn(new int[]{0, 0});
        when(mockGameManager.getRoundTimeSave()).thenReturn(1800);
        when(mockGameManager.getGroundModel()).thenReturn(mockGroundModel);
        when(mockGameManager.getlistTeams()).thenReturn(teamModels);
        when(mockGameManager.getAllTeamStats()).thenReturn(teamStats);
        when(mockGameManager.getWindModel()).thenReturn(mockWindModel);
        when(mockGameManager.getBlockGridModel()).thenReturn(mockBlockGridModel);

        // on re fait des mock mock de TeamData  -> pq pas tout mettre dans setUp enft ..?
        TeamData mockTeamData1 = mock(TeamData.class);
        TeamData mockTeamData2 = mock(TeamData.class);

        // mock mock des fonctions toData avec les mock
        when(mockTeamModel1.toData()).thenReturn(mockTeamData1);
        when(mockTeamModel2.toData()).thenReturn(mockTeamData2);

        // mock mock du vent
        when(mockWindModel.getDir()).thenReturn(WindModel.Dir.E);
        when(mockWindModel. getStrength()).thenReturn(0.5f);

        SaveData saveData = new SaveData(mockGameManager);
        ArrayList<TeamData> result = saveData.getListTeamData();

        // on verifie 
        assertEquals(2, result.size());
        verify(mockTeamModel1, times(1)).toData();  // times() verifie que la methode a ete called une fois
        verify(mockTeamModel2, times(1)).toData();
    }
}
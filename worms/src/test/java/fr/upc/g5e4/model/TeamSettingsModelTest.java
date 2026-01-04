package fr.upc.g5e4.model;

import fr.upc.g5e4.controller.PlayerDataController;
import fr.upc.g5e4.model.io.*;
import fr.upc.g5e4.view.TeamSettingsView;

import fr.upc.g5e4.view.UI.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Class Test simple (avec mockito) de TeamSettingsModel
 */
@ExtendWith(MockitoExtension.class)
class TeamSettingsModelTest {

    @Mock
    private TeamSettingsView view;

    @Mock
    private PlayerDataController playerDataController;

    @Mock private PlayerData playerData;
    @Mock private CustomGameManager customTeamManager;

    private TeamSettingsModel model;

    @BeforeEach
    void setUp() {

        PillButton mockButton = mock(PillButton.class);  // on mock un bouton qui est renvoyer comme dans les getter de TeamSettingsView
        when(view.getSaveNextGameButton()).thenReturn(mockButton);
        when(view.getSaveCustom()).thenReturn(mockButton);


        PillComboBox mockCombo = mock(PillComboBox.class);
        when(view.getSkinPillButton()).thenReturn(mockCombo);
        when(view.getChangeNumberTeam()).thenReturn(mockCombo);


        model = new TeamSettingsModel(view, playerDataController); // on creer le model a partir des mocksmocks
    }

    @Test
    void testSaveDataNextGame() {

        when(view.getNbrEquipe()).thenReturn(4);
        when(view.getWormByTeam()).thenReturn(2);

        when(view.getAllCustomByTeam()).thenReturn(new ArrayList<>()); // c'est vide ici, pas besoin de tester

        model.saveDataNextGame();

        assertEquals(4, TeamConfig.NBR_EQUIPE);
        assertEquals(2, TeamConfig.NBR_WORM_EQUIPE);
    }

    @Test
    void testSaveCustom() {

        String teamName = "WORMILITONNN";
        String skinName = "Worm Noel Edition";
        ArrayList<String> wormsNames = new ArrayList<>();
        wormsNames.add("Worm1");
        wormsNames.add("Worm2");

        SkinModel fakeSkin = new SkinModel("path/to/skin", "/wormNoel.png", true);

        when(view.getTeamName()).thenReturn(teamName);
        when(view.getSkinChooseSTR()).thenReturn(skinName);
        when(view.getAllWormsName()).thenReturn(wormsNames);

        when(playerDataController.getPlayerData()).thenReturn(playerData);
        when(playerData.findSkinModelByName(skinName)).thenReturn(fakeSkin);
        when(playerData.getCustomTeamManager()).thenReturn(customTeamManager);

        model.saveCustom();
        
        verify(customTeamManager).createCustomTeam(eq(teamName), eq(fakeSkin), eq(6), eq(wormsNames));
        verify(playerDataController).sauvegarderJoueur();
        verify(view).reloadCustomTeamChoose();
    }
}

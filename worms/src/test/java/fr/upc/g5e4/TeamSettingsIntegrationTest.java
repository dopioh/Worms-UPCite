package fr.upc.g5e4;

import fr.upc.g5e4.model.io.PlayerData;
import fr.upc.g5e4.controller.PlayerDataController;
import fr.upc.g5e4.controller.TeamSettingsController;
import fr.upc.g5e4.model.SkinModel;
import fr.upc.g5e4.model.TeamSettingsModel;
import fr.upc.g5e4.model.io.CustomGameManager;
import fr.upc.g5e4.view.TeamSettingsView;

import org.junit.jupiter.api. BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Test d'integration entre TeamSettingsController, TeamSettingsModel et TeamSettingsView
 * Avec Mockito
 */
@ExtendWith(MockitoExtension.class)
public class TeamSettingsIntegrationTest {

    @Mock
    private PlayerDataController mockPlayerDataController;

    @Mock
    private PlayerData mockPlayerData;

    @Mock
    private CustomGameManager mockCustomTeamManager;

    @Mock
    private SkinModel mockSkinModel;

    @Mock
    private TeamSettingsModel mockModel;

    private TeamSettingsView realView;
    private TeamSettingsController controller;

    @BeforeEach
    public void setUp() {
        // configure les mock mock pour la view
        when(mockPlayerDataController.getPlayerData()).thenReturn(mockPlayerData);
        when(mockPlayerData.onlyUnlocked()).thenReturn(new ArrayList<>(Arrays.asList("Worm de base", "Worm Noel Edition")));
        when(mockPlayerData.findSkinModelByName(anyString())).thenReturn(mockSkinModel);
        when(mockSkinModel.getFilePath()).thenReturn("/wormPixel.png");
        when(mockPlayerData.getCustomTeamManager()).thenReturn(mockCustomTeamManager);
        when(mockCustomTeamManager.getListCustomTeamData()).thenReturn(new ArrayList<>());

        // creer une vraie vue avec des mocks
        try {
            SwingUtilities.invokeAndWait(() -> {
                realView = new TeamSettingsView(mockPlayerDataController);
                controller = new TeamSettingsController(mockModel, realView);
            });
        } catch (Exception e) {
            realView = new TeamSettingsView(mockPlayerDataController);
            controller = new TeamSettingsController(mockModel, realView);
        }
    }

    @Test
    public void saveNextGameButtonClickTriggersModel() {
        // simule un click
        realView.getSaveNextGameButton().doClick();

        verify(mockModel, times(1)).saveDataNextGame();
    }

    @Test
    public void saveCustomButtonClickTriggersModel() {

        realView.getSaveCustom().doClick();

        verify(mockModel, times(1)).saveCustom();
    }

    @Test
    public void skinPillButtonChangeTriggersModel() {

        realView.getSkinPillButton().setSelectedIndex(1);

        verify(mockModel, atLeastOnce()).changePreviewImage();
    }

    @Test
    public void changeNumberTeamChangeTriggersModel() {

        realView.getChangeNumberTeam().setSelectedIndex(1);

        verify(mockModel, atLeastOnce()).changePreviewImage();
    }

    @Test
    public void viewAndControllerWorkTogether() {
        // verifie que tout est connecter
        assertNotNull(realView);
        assertNotNull(controller);

        // les boutons de la vue doivent avoir des listeners
        assertTrue(realView.getSaveNextGameButton().getActionListeners().length > 0);
        assertTrue(realView. getSaveCustom().getActionListeners().length > 0);
    }

    @Test
    public void multipleClicksAllTriggerModel() {
        realView.getSaveNextGameButton().doClick();
        realView.getSaveNextGameButton().doClick();
        realView.getSaveCustom().doClick();

        verify(mockModel, times(2)).saveDataNextGame();
        verify(mockModel, times(1)).saveCustom();
    }
}
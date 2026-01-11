package fr.upc.g5e4.view;

import fr.upc.g5e4.controller.PlayerDataController;
import fr.upc.g5e4.model.io.PlayerData;
import fr.upc.g5e4.model.SkinModel;
import fr.upc.g5e4.model.io.CustomGameManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api. Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeamSettingsViewTest {

    @Mock
    private PlayerDataController mockPlayerDataController;

    @Mock
    private PlayerData mockPlayerData;

    @Mock
    private CustomGameManager mockCustomGameManager;

    @Mock
    private SkinModel mockSkinModel;

    private TeamSettingsView view;

    @BeforeEach
    public void setUp() {

        when(mockPlayerDataController.getPlayerData()).thenReturn(mockPlayerData);

        ArrayList<String> unlockedSkins = new ArrayList<>(Arrays.asList("Worm de base", "Worm Noel Edition"));
        when(mockPlayerData.onlyUnlocked()).thenReturn(unlockedSkins);

        when(mockPlayerData.findSkinModelByName(anyString())).thenReturn(mockSkinModel);
        when(mockSkinModel.getFilePath()).thenReturn("/wormPixel.png");

        when(mockPlayerData.getCustomTeamManager()).thenReturn(mockCustomGameManager);
        when(mockCustomGameManager.getListCustomTeamData()).thenReturn(new ArrayList<>());

        // cree la vue 
        try {
            SwingUtilities.invokeAndWait(() -> { view = new TeamSettingsView(mockPlayerDataController); });
        } catch (Exception e) {
            view = new TeamSettingsView(mockPlayerDataController);
        }
    }

    @Test
    public void ReturnsDefaultValueTEAMS() {
        // par defaut la premiere valeur select doit etre 2
        int nbrEquipe = view.getNbrEquipe();
        assertEquals(2, nbrEquipe);
    }

    @Test
    public void ReturnsDefaultValueWORMS() {
        // par defaut la premiere valeur select doit etre 1
        int wormByTeam = view.getWormByTeam();
        assertEquals(1, wormByTeam);
    }

    @Test
    public void ReturnsFirstSkin() {
        String selectedSkin = view.getSkinChooseSTR();
        assertEquals("Worm de base", selectedSkin); // valide le skin valide au passage
    }

    @Test
    public void ReturnsDefaultNamesALL() {
        ArrayList<String> wormsNames = view.getAllWormsName();

        // doit return 6 noms car 6 worms creer par defaut pour chaque equipe
        assertEquals(6, wormsNames.size());

        // tous "bot" par defaut
        for (String name : wormsNames) {
            assertEquals("bot", name);
        }
    }

    @Test
    public void testSetFullScreen_UpdatesState() {
        view.setFullScreen(true);
        assertTrue(view.getFullScreen());

        view.setFullScreen(false);
        assertFalse(view.getFullScreen());
    }

    // verifie que les button renvoie pas nul

    @Test
    public void testGetSaveNextGameButtonReturnsNonNull() {
        JButton button = view.getSaveNextGameButton();
        assertNotNull(button);
        assertInstanceOf(JButton.class, button);
    }

    @Test
    void testGetSaveCustomReturnsNonNull() {
        JButton button = view.getSaveCustom();
        assertNotNull(button);
        assertInstanceOf(JButton.class, button);
    }



    @Test
    public void testUpdateDoesNotThrowException() {
        assertDoesNotThrow(() -> { view.update(false);} );

        assertDoesNotThrow(() -> { view.update(true);} );
    }

    @Test
    public void testReloadCustomTeamChoose_DoesNotThrowException() {
        assertDoesNotThrow(() -> { view.reloadCustomTeamChoose();} );
    }

    @Test
    public void testUpdateSkinWithInvalidSkin() {
        // skin qui existe pas
        String invalidSkinName = "MEIMEI";
        int size = 50;

        assertDoesNotThrow(() -> { view.updateSkin(invalidSkinName, size);});
    }
}
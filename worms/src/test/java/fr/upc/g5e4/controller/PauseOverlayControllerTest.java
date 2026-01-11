package fr.upc.g5e4.controller;

import fr.upc.g5e4.model.GameLoop;
import fr.upc.g5e4.model.GameManager;
import fr.upc.g5e4.model.SoundPlayer;
import fr.upc.g5e4.view.HelpOverlay;
import fr.upc.g5e4.view.PauseOverlay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/** 
 * Tests unitaires pour le contrôleur de l'overlay de pause @see PauseOverlayController.java
 */
class PauseOverlayControllerTest {

    private GameManager gameManager;
    private PauseOverlay pauseOverlay;
    private HelpOverlay helpOverlay;

    //Compteurs pour vérifier l'exécution 
    private int fullScreenRuns; 
    private int backToMenuRuns;

    private PauseOverlayController controller;

    @BeforeEach
    void setup() {
        //Mock pour vérifier les appls
        gameManager = mock(GameManager.class);
        pauseOverlay = mock(PauseOverlay.class);
        helpOverlay = mock(HelpOverlay.class);

        fullScreenRuns = 0;
        backToMenuRuns = 0;

        Runnable toggleFullScreenAction = () -> fullScreenRuns++;
        Runnable backToMenuAction = () -> backToMenuRuns++;

        controller = new PauseOverlayController(gameManager,pauseOverlay,helpOverlay,toggleFullScreenAction,backToMenuAction);
    }

    @Test
    void helpClickedShowsHelp() {
        controller.onHelpClicked();

        verify(helpOverlay).setVisible(true);
        verify(helpOverlay).repaint();
    }

    @Test
    void toggleVentCallsGameManager() {
        controller.onToggleVent(true);

        verify(gameManager).toggleVent(true);
    }

    @Test
    void saveClickedCallsGameManager() {
        controller.onSaveClicked();

        verify(gameManager).save();
    }

    @Test
    void flyModePressedCallsGameManager() {
        controller.onFlyModePressed();

        verify(gameManager).toggleFlyMode();
    }

    @Test
    void toggleBlueprintCallsGameManager() {
        controller.onToggleBlueprint(false);

        verify(gameManager).setBlueprintEnabled(false);
    }

    @Test
    void toggleFullScreenRunsAction() { //Incrémente fulscreenrun si l'action est appelée
        controller.onToggleFullScreen(true);

        assertEquals(1, fullScreenRuns);
    }

    @Test
    void toggleSoundMutesAndUpdatesOverlay() {
        try (MockedStatic<SoundPlayer> soundPlayer = mockStatic(SoundPlayer.class)) {
            controller.onToggleSound(true);

            soundPlayer.verify(() -> SoundPlayer.setMuted(false));
            verify(pauseOverlay).setSoundOn(true);
        }
    }

    @Test
    void quitClickClosePauseAndBackToMenu() {
        controller.onQuitClicked();

        verify(pauseOverlay).setPauseVisible(false);
        assertEquals(1, backToMenuRuns);
    }

    @Test
    void closeClickResumeTimer() { 
        GameLoop loop = mock(GameLoop.class);
        when(gameManager.getGameLoop()).thenReturn(loop);

        controller.onCloseClicked();

        verify(pauseOverlay).setPauseVisible(false);
        verify(loop).resumeTimer();
    }

    @Test
    void closeClickDoesNothingWhenNoLoop() {
        when(gameManager.getGameLoop()).thenReturn(null);

        controller.onCloseClicked();

        verify(pauseOverlay).setPauseVisible(false);
    }

    @Test
    void toggleTeamKillCallsGameManagerAndRepaint() {
        controller.onToggleTeamKill(true);

        verify(gameManager).setTeamKillEnabled(true);
        verify(pauseOverlay).repaint();
    }

    @Test
    void nullDependenciesDoNotCrash() {
        PauseOverlayController c = new PauseOverlayController(null, null, null, null, null);

        c.onHelpClicked();
        c.onToggleVent(true);
        c.onSaveClicked();
        c.onFlyModePressed();
        c.onToggleBlueprint(true);
        c.onToggleFullScreen(true);
        c.onToggleSound(true);
        c.onQuitClicked();
        c.onCloseClicked();
        c.onToggleTeamKill(true);
    }
}

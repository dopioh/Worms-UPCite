package fr.upc.g5e4.controller;
import fr.upc.g5e4.model.GameManager;
import fr.upc.g5e4.model.SoundPlayer;
import fr.upc.g5e4.view.HelpOverlay;
import fr.upc.g5e4.view.PauseOverlay;

public class PauseOverlayController implements PauseOverlay.Listener {

    private final GameManager gameManager;
    private final PauseOverlay pauseOverlay;
    private final HelpOverlay helpOverlay;

    private final Runnable toggleFullScreenAction;
    private final Runnable backToMenuAction;

    public PauseOverlayController(GameManager gameManager,PauseOverlay pauseOverlay,HelpOverlay helpOverlay,Runnable toggleFullScreenAction,Runnable backToMenuAction) {
        this.gameManager = gameManager;
        this.pauseOverlay = pauseOverlay;
        this.helpOverlay = helpOverlay;
        this.toggleFullScreenAction = toggleFullScreenAction;
        this.backToMenuAction = backToMenuAction;
    }

    @Override
    public void onHelpClicked() {
        if (helpOverlay != null) {
            helpOverlay.setVisible(true);
            helpOverlay.repaint();
        }
    }

    @Override
    public void onToggleVent(boolean enabled) {
        if (gameManager != null) gameManager.toggleVent(enabled);
    }

    @Override
    public void onSaveClicked() {
        if (gameManager != null) gameManager.save();
    }

    @Override
    public void onFlyModePressed() {
        if (gameManager != null) gameManager.toggleFlyMode();
    }

    @Override
    public void onToggleBlueprint(boolean enabled) {
        if (gameManager != null) gameManager.setBlueprintEnabled(enabled);
    }

    @Override
    public void onToggleFullScreen(boolean enabled) {
        if (toggleFullScreenAction != null) toggleFullScreenAction.run();
    }

    @Override
    public void onToggleSound(boolean enabled) {
        SoundPlayer.setMuted(!enabled);
        if (pauseOverlay != null) pauseOverlay.setSoundOn(enabled);
    }

    @Override
    public void onQuitClicked() {
        if (pauseOverlay != null) pauseOverlay.setPauseVisible(false);
        if (backToMenuAction != null) backToMenuAction.run();
    }

    @Override
    public void onCloseClicked() {
        if (pauseOverlay != null) pauseOverlay.setPauseVisible(false);
        if (gameManager != null && gameManager.getGameLoop() != null) {
            gameManager.getGameLoop().resumeTimer();
        }
    }

    @Override
    public void onToggleTeamKill(boolean enabled) {
        if (gameManager != null) gameManager.setTeamKillEnabled(enabled);
        if (pauseOverlay != null) pauseOverlay.repaint();
    }
}

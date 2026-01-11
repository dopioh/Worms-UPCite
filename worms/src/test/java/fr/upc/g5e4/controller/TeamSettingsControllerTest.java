package fr.upc.g5e4.controller;

import fr.upc.g5e4.model.TeamSettingsModel;
import fr.upc.g5e4.view.TeamSettingsView;
import fr.upc.g5e4.view.UI. PillButton;
import fr.upc.g5e4.view.UI.PillComboBox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter. api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.event.ActionListener;

import static org.mockito.Mockito.*;

/**
 * Test simple de TeamSettingsController
 */
@ExtendWith(MockitoExtension.class)
public class TeamSettingsControllerTest {

    @Mock
    private TeamSettingsModel mockModel;

    @Mock
    private TeamSettingsView mockView;

    @Mock
    private PillButton mockSaveNextGameButton;

    @Mock
    private PillButton mockSaveCustomButton;

    @Mock
    private PillComboBox<String> mockSkinPillButton;

    @Mock
    private PillComboBox<Integer> mockChangeNumberTeamButton;

    private TeamSettingsController controller;

    @BeforeEach
    public void setUp() {

        // renvoie les boutons mock mock
        when(mockView.getSaveNextGameButton()).thenReturn(mockSaveNextGameButton);
        when(mockView.getSkinPillButton()).thenReturn(mockSkinPillButton);
        when(mockView.getSaveCustom()).thenReturn(mockSaveCustomButton);
        when(mockView.getChangeNumberTeam()).thenReturn(mockChangeNumberTeamButton);

        controller = new TeamSettingsController(mockModel, mockView);
    }

    @Test
    public void CallSaveDataNextGame() {

        // attrape le listeer ajoutee au bouton
        ArgumentCaptor<ActionListener> listenerCaptor = ArgumentCaptor.forClass(ActionListener.class);
        verify(mockSaveNextGameButton).addActionListener(listenerCaptor.capture());
        ActionListener listener = listenerCaptor.getValue();

        listener.actionPerformed(null);

        // verifie que la methode a ete called
        verify(mockModel, times(1)).saveDataNextGame();
    }

    @Test
    public void CallsChangePreviewImageSKINBUTTON() {

        ArgumentCaptor<ActionListener> listenerCaptor = ArgumentCaptor.forClass(ActionListener.class);
        verify(mockSkinPillButton).addActionListener(listenerCaptor.capture());
        ActionListener listener = listenerCaptor.getValue();

        listener.actionPerformed(null);

        verify(mockModel, times(1)).changePreviewImage();
    }

    @Test
    public void testSaveCustomButton_CallsSaveCustom() {

        ArgumentCaptor<ActionListener> listenerCaptor = ArgumentCaptor.forClass(ActionListener. class);
        verify(mockSaveCustomButton).addActionListener(listenerCaptor.capture());
        ActionListener listener = listenerCaptor.getValue();

        listener.actionPerformed(null);

        verify(mockModel, times(1)).saveCustom();
    }

    @Test
    public void CallsChangePreviewImageNUMBERTEAMBUTTON() {

        ArgumentCaptor<ActionListener> listenerCaptor = ArgumentCaptor.forClass(ActionListener.class);
        verify(mockChangeNumberTeamButton).addActionListener(listenerCaptor.capture());
        ActionListener listener = listenerCaptor.getValue();


        listener.actionPerformed(null);

        // changePreviewImage est called 2 fois :  une fois par skinPillButton, une fois par changeNumberTeam
        verify(mockModel, times(1)).changePreviewImage();
    }
}
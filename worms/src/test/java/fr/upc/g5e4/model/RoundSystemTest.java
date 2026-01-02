package fr.upc.g5e4.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

/**
 * Tests unitaires pour le système de rounds avec support N équipes
 */
public class RoundSystemTest {

    private GameManager gameManager;

    @BeforeEach
    void setUp() {
        // Utilise le GameManager avec 2 équipes par défaut
        TeamConfig.NBR_EQUIPE = 2;
        if (TeamConfig.aCustomForATeam == null) {
            TeamConfig.aCustomForATeam = new ArrayList<>();
        }
        gameManager = new GameManager();
    }

    @Test
    void testInitialRoundState() {
        assertEquals(1, gameManager.getRoundNumber(), "Le round initial devrait être 1");
        assertEquals(0, gameManager.getCurrentTeamIndex(), "L'index de l'équipe initiale devrait être 0");
        assertNotNull(gameManager.getCurrentTeam(), "L'équipe initiale ne devrait pas être null");
    }

    @Test
    void testNextRoundIncrementsRoundNumber() {
        int initialRound = gameManager.getRoundNumber();

        gameManager.nextRound();

        assertEquals(initialRound + 1, gameManager.getRoundNumber(),
                "nextRound devrait incrémenter le numéro de round");
    }

    @Test
    void testNextRoundAlternatesBetweenTwoTeams() {
        // GameManager par défaut a 2 équipes
        TeamModel firstTeam = gameManager.getCurrentTeam();
        assertNotNull(firstTeam);

        // Round 2: devrait passer à l'équipe suivante
        gameManager.nextRound();
        TeamModel secondTeam = gameManager.getCurrentTeam();
        assertNotNull(secondTeam);
        assertNotEquals(firstTeam, secondTeam, "nextRound devrait changer d'équipe");

        // Round 3: devrait revenir à la première équipe
        gameManager.nextRound();
        TeamModel thirdTeam = gameManager.getCurrentTeam();
        assertEquals(firstTeam, thirdTeam, "Après 2 nextRound, devrait revenir à la première équipe");
    }

    @Test
    void testGetCurrentTeamName() {
        String initialName = gameManager.getCurrentTeamName();
        assertNotNull(initialName);
        assertFalse(initialName.isEmpty());

        gameManager.nextRound();
        String nextName = gameManager.getCurrentTeamName();
        assertNotNull(nextName);
        assertFalse(nextName.isEmpty());
        assertEquals(initialName, nextName, "Le nom de l'équipe devrait changer après nextRound");
    }

    @Test
    void testRoundNumberProgression() {
        assertEquals(1, gameManager.getRoundNumber());

        for (int i = 2; i <= 5; i++) {
            gameManager.nextRound();
            assertEquals(i, gameManager.getRoundNumber(),
                    "Le numéro de round devrait être " + i + " après " + (i-1) + " appels à nextRound");
        }
    }

    @Test
    void testCurrentTeamIndexCycles() {
        // Commence à 0
        assertEquals(0, gameManager.getCurrentTeamIndex());

        // Après 1 nextRound: devrait être 1
        gameManager.nextRound();
        assertEquals(1, gameManager.getCurrentTeamIndex());

        // Après 2 nextRound: devrait revenir à 0
        gameManager.nextRound();
        assertEquals(0, gameManager.getCurrentTeamIndex());
    }

    @Test
    void testMultipleRoundCycles() {
        int initialRound = gameManager.getRoundNumber();

        // Faire plusieurs cycles
        for (int i = 0; i < 6; i++) {
            gameManager.nextRound();
        }

        // Après 6 rounds (3 cycles complets), le round number devrait avoir augmenté de 6
        assertEquals(initialRound + 6, gameManager.getRoundNumber());
    }

    @Test
    void testNextRoundDoesNotThrowExceptions() {
        // nextRound ne devrait jamais planter, même appelé plusieurs fois
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 10; i++) {
                gameManager.nextRound();
            }
        }, "nextRound ne devrait pas lever d'exceptions même après de multiples appels");
    }

    @Test
    void testRoundTimeRemaining() {
        // Le temps de round devrait être initialisé
        int timeRemaining = gameManager.getRoundTimeRemaining();
        assertTrue(timeRemaining > 0, "Le temps restant devrait être positif au départ");

        int timeSave = gameManager.getRoundTimeSave();
        assertTrue(timeSave > 0, "Le temps sauvegardé devrait être positif");
    }

    @Test
    void testWindChangesBetweenRounds() {
        // Vérifier que le vent est défini avant nextRound
        WindModel initialWind = gameManager.getWindModel();
        assertNotNull(initialWind.getDir(), "La direction du vent initiale devrait être définie");

        // Après nextRound, le vent devrait toujours être dans les limites
        gameManager.nextRound();

        WindModel newWind = gameManager.getWindModel();
        assertNotNull(newWind.getDir(), "La direction du vent devrait être définie après nextRound");
        assertTrue(newWind.getStrength() >= 0.15f && newWind.getStrength() <= 1.0f,
                "La force du vent devrait être entre 0.15 et 1.0 après nextRound");
    }

    @Test
    void testRoundNumberNeverGoesBelowOne() {
        // Le round number commence à 1 et ne devrait jamais descendre en dessous
        assertTrue(gameManager.getRoundNumber() >= 1, "Le numéro de round devrait toujours être >= 1");

        for (int i = 0; i < 5; i++) {
            gameManager.nextRound();
            assertTrue(gameManager.getRoundNumber() >= 1,
                    "Le numéro de round devrait toujours être >= 1 après nextRound");
        }
    }
}
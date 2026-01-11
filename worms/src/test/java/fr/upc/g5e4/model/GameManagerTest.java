package fr.upc.g5e4.model;

import fr.upc.g5e4.view.WindOverlay;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameManagerTest {
    private static GameManager newGameManagerWithoutConstructor() {
        try {
            // Unsafe permet d'allouer l'objet sans exécuter le constructeur
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Object unsafe = theUnsafe.get(null);
            return (GameManager) unsafeClass.getMethod("allocateInstance", Class.class).invoke(unsafe, GameManager.class);
        } catch (Exception e) {
            throw new RuntimeException("Impossible de créer GameManager sans constructeur. ", e);
        }
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Impossible de set le champ: " + fieldName, e);
        }
    }

    private static Object getField(Object target, String fieldName) {
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(target);
        } catch (Exception e) {
            throw new RuntimeException("Impossible de lire le champ: " + fieldName, e);
        }
    }

    @Test
    void markWeaponUsedSetOnce() {
        GameManager gm = newGameManagerWithoutConstructor();

        gm.markWeaponUsed("AK47");
        assertEquals("AK47", gm.getWeaponUsedThisTurn());

        gm.markWeaponUsed("Grenade");
        assertEquals("AK47", gm.getWeaponUsedThisTurn());
    }

    @Test
    void resetWeaponUsedFlagClear() {
        GameManager gm = newGameManagerWithoutConstructor();

        setField(gm, "weaponUsedThisTurn", "AK47");

        gm.resetWeaponUsedFlag();

        assertNull(gm.getWeaponUsedThisTurn());
    }

    @Test
    void remainingGadgetsThisTurnReturn2MinusUsed() {
        GameManager gm = newGameManagerWithoutConstructor();

        setField(gm, "gadgetsUsedThisTurn", 0);
        assertEquals(2, gm.getRemainingGadgetsThisTurn());

        setField(gm, "gadgetsUsedThisTurn", 1);
        assertEquals(1, gm.getRemainingGadgetsThisTurn());

        setField(gm, "gadgetsUsedThisTurn", 2);
        assertEquals(0, gm.getRemainingGadgetsThisTurn());
    }

    @Test
    void toggleFlyModeInvertValue() {
        GameManager gm = newGameManagerWithoutConstructor();

        // état initial faux
        setField(gm, "flyModeEnabled", false);

        gm.toggleFlyMode();
        assertTrue(gm.isFlyModeEnabled());

        gm.toggleFlyMode();
        assertFalse(gm.isFlyModeEnabled());
    }

    @Test
    void teamKillEnabledSetGet() {
        GameManager gm = newGameManagerWithoutConstructor();

        gm.setTeamKillEnabled(true);
        assertTrue(gm.isTeamKillEnabled());

        gm.setTeamKillEnabled(false);
        assertFalse(gm.isTeamKillEnabled());
    }

    @Test
    void blueprintEnabledSetGet() {
        GameManager gm = newGameManagerWithoutConstructor();

        gm.setBlueprintEnabled(true);
        assertTrue(gm.isBlueprintEnabled());

        gm.setBlueprintEnabled(false);
        assertFalse(gm.isBlueprintEnabled());
    }

    @Test
    void toggleVentDisableForce0AndHideOverlay() {
        GameManager gm = newGameManagerWithoutConstructor();

        // WindModel 
        WindModel windModel = new WindModel();
        windModel.setStrength(0.7f, false);
        setField(gm, "windModel", windModel);

        // valeurs du tour mémorisées
        setField(gm, "turnWindDir", WindModel.Dir.NE);
        setField(gm, "turnWindStrength", 0.8f);

        WindOverlay overlay = mock(WindOverlay.class);
        setField(gm, "windOverlay", overlay);

        // disable
        gm.toggleVent(false);

        assertFalse(gm.isWindEnabled());
        assertEquals(0f, gm.getWindModel().getStrength());
        assertEquals(0f, gm.getEffectiveWindStrength());

        verify(overlay).setVisible(false);
        verify(overlay).repaint();
    }

    @Test
    void toggleVentEnableRestoreTurnWindAndShowOverlay() {
        GameManager gm = newGameManagerWithoutConstructor();

        WindModel windModel = new WindModel();
        setField(gm, "windModel", windModel);

        setField(gm, "turnWindDir", WindModel.Dir.SO);
        setField(gm, "turnWindStrength", 0.65f);

        WindOverlay overlay = mock(WindOverlay.class);
        setField(gm, "windOverlay", overlay);

        // enable
        gm.toggleVent(true);

        assertTrue(gm.isWindEnabled());
        assertEquals(WindModel.Dir.SO, gm.getWindModel().getDir());
        assertEquals(0.65f, gm.getWindModel().getStrength());
        assertEquals(0.65f, gm.getEffectiveWindStrength());

        verify(overlay).setVisible(true);
        verify(overlay).repaint();
    }
}

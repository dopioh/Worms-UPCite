package fr.upc.g5e4.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class SoundPlayerTest {

    @BeforeEach
    void resetStaticState() throws Exception {
        SoundPlayer.setMuted(false);

        Field initialized = SoundPlayer.class.getDeclaredField("initialized");
        initialized.setAccessible(true);
        initialized.setBoolean(null, false);

        Field cache = SoundPlayer.class.getDeclaredField("audioCache");
        cache.setAccessible(true);
        ((java.util.Map<?, ?>) cache.get(null)).clear();
    }

    @Test
    void setMutedChangeState() {
        SoundPlayer.setMuted(true);
        assertTrue(SoundPlayer.isMuted());

        SoundPlayer.setMuted(false);
        assertFalse(SoundPlayer.isMuted());
    }

    @Test
    void initializeInTestsMuteSound() {
        SoundPlayer.setMuted(false);

        SoundPlayer.initialize();

        assertTrue(SoundPlayer.isMuted());
    }

    @Test
    void playWhenMutedNoCrash() {
        SoundPlayer.setMuted(true);

        assertDoesNotThrow(() -> SoundPlayer.play("/sfx/does-not-exist.wav"));
    }

    @Test
    void playDepopSoundNoCrash() {
        SoundPlayer.setMuted(true);

        assertDoesNotThrow(SoundPlayer::playDepopSound);
    }

    @Test
    void playBreakBlockSoundNoCrash() {
        SoundPlayer.setMuted(true);

        assertDoesNotThrow(SoundPlayer::playBreakBlockSound);
    }
}

package fr.upc.g5e4.model;

import org.junit.jupiter.api.*;
import fr.upc.g5e4.model.io.SoundPlayer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SoundPlayerTest {

    private static final String MISSING = "/sfx/does-not-exist.wav";
    private static final String TEST_WAV = "/sfx/test-sound.wav"; // à mettre dans src/test/resources

    @BeforeEach
    void resetCache() throws Exception {
        Map<String, byte[]> cache = getCache();
        cache.clear();
    }

    // -------------------------
    // Tests pour play(String)
    // -------------------------

    @Test
    void play_null_doesNotThrow() {
        assertDoesNotThrow(() -> SoundPlayer.play(null));
    }

    @Test
    void play_blank_doesNotThrow() {
        assertDoesNotThrow(() -> SoundPlayer.play("   "));
    }

    @Test
    void play_missingResource_doesNotThrow() {
        assertDoesNotThrow(() -> SoundPlayer.play(MISSING));
        // Asynchrone: on ne peut pas garantir quand le job tourne, mais on vérifie qu'aucune exception n'est propagée
    }

    @Test
    void play_existingResource_eventuallyCachesBytes_ifTestWavPresent() throws Exception {
        Assumptions.assumeTrue(
                SoundPlayer.class.getResourceAsStream(TEST_WAV) != null,
                "Ajoute un WAV de test: src/test/resources/sfx/test-sound.wav"
        );

        SoundPlayer.play(TEST_WAV);

        // On attend un peu que le thread du pool ait eu le temps d'exécuter playSync
        // (petite attente pour éviter un test flaky)
        boolean cached = waitUntilCached(TEST_WAV, 500);
        assertTrue(cached, "Le son devrait être mis en cache après lecture");
        assertNotNull(getCache().get(TEST_WAV));
        assertTrue(getCache().get(TEST_WAV).length > 0);
    }

    // -------------------------
    // Tests pour loadBytesOrNull (private)
    // -------------------------

    @Test
    void loadBytesOrNull_missingResource_returnsNull() throws Exception {
        Object res = invokePrivateStatic(
                "loadBytesOrNull",
                new Class<?>[]{String.class},
                new Object[]{MISSING}
        );
        assertNull(res);
    }

    @Test
    void loadBytesOrNull_existingResource_returnsBytes_ifTestWavPresent() throws Exception {
        Assumptions.assumeTrue(
                SoundPlayer.class.getResourceAsStream(TEST_WAV) != null,
                "Ajoute un WAV de test: src/test/resources/sfx/test-sound.wav"
        );

        Object res = invokePrivateStatic(
                "loadBytesOrNull",
                new Class<?>[]{String.class},
                new Object[]{TEST_WAV}
        );

        assertNotNull(res);
        byte[] bytes = (byte[]) res;
        assertTrue(bytes.length > 0);
    }

    // -------------------------
    // Tests pour playSync (private)
    // -------------------------

    @Test
    void playSync_missingResource_doesNotThrow() throws Exception {
        assertDoesNotThrow(() -> invokePrivateStatic(
                "playSync",
                new Class<?>[]{String.class},
                new Object[]{MISSING}
        ));
    }

    @Test
    void playSync_cacheHit_withInvalidBytes_doesNotThrow() throws Exception {
        // On force un cache hit avec des bytes invalides -> doit être attrapé par le catch (Exception ignored)
        getCache().put("/sfx/fake.wav", new byte[]{0, 1, 2, 3, 4});

        assertDoesNotThrow(() -> invokePrivateStatic(
                "playSync",
                new Class<?>[]{String.class},
                new Object[]{"/sfx/fake.wav"}
        ));
    }

    // -------------------------
    // Helpers
    // -------------------------

    @SuppressWarnings("unchecked")
    private static Map<String, byte[]> getCache() throws Exception {
        Field f = SoundPlayer.class.getDeclaredField("AUDIO_CACHE");
        f.setAccessible(true);
        return (Map<String, byte[]>) f.get(null);
    }

    private static Object invokePrivateStatic(String methodName, Class<?>[] paramTypes, Object[] args) throws Exception {
        Method m = SoundPlayer.class.getDeclaredMethod(methodName, paramTypes);
        m.setAccessible(true);
        return m.invoke(null, args);
    }

    private static boolean waitUntilCached(String path, long timeoutMs) throws Exception {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeoutMs) {
            if (getCache().containsKey(path) && getCache().get(path) != null) {
                return true;
            }
            Thread.sleep(10);
        }
        return false;
    }
}

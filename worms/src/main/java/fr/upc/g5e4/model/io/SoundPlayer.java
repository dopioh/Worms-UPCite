package fr.upc.g5e4.model.io;

import javax.sound.sampled.*;
import java.io.*;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Joue des effets sonores (WAV) depuis les ressources du classpath.
 * Cache les fichiers en mémoire pour éviter de relire le disque à chaque lecture.
 */
public final class SoundPlayer {
    private SoundPlayer() {}

    /** Cache thread-safe : chemin de ressource → contenu binaire du fichier audio. */
    private static final Map<String, byte[]> AUDIO_CACHE = new ConcurrentHashMap<>();

    /**
     * Pool de threads dédié aux sons.
     * Évite la création d'un nouveau Thread à chaque lecture.
     */
    private static final ExecutorService SFX_EXEC =
            Executors.newCachedThreadPool(r -> {
                Thread t = new Thread(r, "sfx-thread");
                t.setDaemon(true);
                return t;
            });

    /**
     * Joue un son de manière asynchrone
     *
     * @param resourcePath chemin de ressource
     */
    public static void play(String resourcePath) {
        if (resourcePath == null || resourcePath.isBlank()) {
            return;
        }
        SFX_EXEC.submit(() -> playSync(resourcePath));
    }

    /** Joue un son de manière synchrone (interne). */
    private static void playSync(String resourcePath) {
        try {
            byte[] bytes = AUDIO_CACHE.computeIfAbsent(resourcePath, SoundPlayer::loadBytesOrNull);
            if (bytes == null) {
                return;
            }

            try (InputStream raw = new ByteArrayInputStream(bytes);
                 BufferedInputStream bis = new BufferedInputStream(raw);
                 AudioInputStream ais = AudioSystem.getAudioInputStream(bis)) {

                AudioFormat base = ais.getFormat();

                AudioFormat decoded = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        base.getSampleRate(),
                        16,
                        base.getChannels(),
                        base.getChannels() * 2,
                        base.getSampleRate(),
                        false
                );

                try (AudioInputStream dais = AudioSystem.getAudioInputStream(decoded, ais)) {
                    Clip clip = AudioSystem.getClip();
                    clip.open(dais);
                    clip.start();

                    clip.addLineListener(ev -> {
                        if (ev.getType() == LineEvent.Type.STOP) {
                            clip.close();
                        }
                    });
                }
            }

        } catch (Exception ignored) {
            // Ignore pour ne pas stopper le jeu
        }
    }

    /**
     * Charge une ressource en mémoire et renvoie son contenu binaire.
     *
     * @param resourcePath chemin de ressource
     * @return bytes du fichier, ou null si absent / erreur I/O
     */
    
    private static byte[] loadBytesOrNull(String resourcePath) {
        try (InputStream raw = SoundPlayer.class.getResourceAsStream(resourcePath)) {
            if (raw == null) {
                return null;
            }
            return raw.readAllBytes();
        } catch (IOException e) {
            return null;
        }
    }
}
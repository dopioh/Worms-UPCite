package fr.upc.g5e4.model;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Joue des effets sonores (ici .WAV) depuis les ressources du jeu
 * 
 * @author Hugo
 * @version 1.0
 */
public final class SoundPlayer {
    private SoundPlayer() {}
    private static boolean muted = false;

    // Cache pour les clips audio pré-chargés
    private static final Map<String, byte[]> audioCache = new HashMap<>();
    private static boolean initialized = false;
    
    /**
     * Initialise le système audio et pré-charge les sons communs
     * Appeler au démarrage du jeu pour éviter le lag au premier tir
     */
    public static void initialize() {
        if (initialized) return;
        
        // detecter si on est en mode test et muter automatiquement
        if (isRunningTests()) {
            muted = true;
            initialized = true;
            System.out.println("[SFX] Mode test détecté - sons désactivés");
            return;
        }
        
        try {
            // Initialiser le système audio en arrère-plan
            new Thread(() -> {
                try {
                    // Pré-charger le son de tir
                    preloadSound(null);
                    initialized = true;
                    System.out.println("[SFX] Système audio initialisé");
                } catch (Exception e) {
                    System.err.println("[SFX] Erreur initialisation: " + e.getMessage());
                }
            }).start();
        } catch (Exception e) {
            System.err.println("[SFX] Erreur lancement thread: " + e.getMessage());
        }
    }
    
    /**
     * Détecte si le code est exécuté dans un contexte de test
     */
    private static boolean isRunningTests() {
        // regarder la stack trace
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (element.getClassName().startsWith("org.junit.") || 
                element.getClassName().startsWith("org.testng.")) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Pré-charge un son en mémoire
     */
    private static void preloadSound(String resourcePath) {
        try {
            InputStream raw = SoundPlayer.class.getResourceAsStream(resourcePath);
            if (raw == null) {
                System.err.println("[SFX] Introuvable pour pré-chargement: " + resourcePath);
                return;
            }
            
            // Lire tout le fichier en mémoire
            byte[] audioData = raw.readAllBytes();
            audioCache.put(resourcePath, audioData);
            raw.close();
            
            System.out.println("[SFX] Pré-chargé: " + resourcePath);
        } catch (Exception e) {
            System.err.println("[SFX] Erreur pré-chargement: " + resourcePath);
        }
    }

    public static void play(String resourcePath) {
        if (muted) return;
        // Jouer en arrière-plan pour ne pas bloquer le thread principal
        new Thread(() -> playSync(resourcePath)).start();
    }
    
    /**
     * Joue un son de manière synchrone
     * @param resourcePath le chemin de la ressource sonore
     */
    private static void playSync(String resourcePath) {
        try {
            InputStream raw;
            
            // Utiliser le cache si disponible
            if (audioCache.containsKey(resourcePath)) {
                raw = new ByteArrayInputStream(audioCache.get(resourcePath));
            } else {
                raw = SoundPlayer.class.getResourceAsStream(resourcePath);
                if (raw == null) {
                    System.err.println("[SFX] Introuvable: " + resourcePath);
                    return;
                }
            }

            BufferedInputStream bis = new BufferedInputStream(raw);
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(bis);
            AudioFormat base = audioInput.getFormat();

            // Conversion forcée vers un format sûr
            AudioFormat decoded = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    base.getSampleRate(),
                    16,
                    base.getChannels(),
                    base.getChannels() * 2,
                    base.getSampleRate(),
                    false
            );

            AudioInputStream decodedAudio = AudioSystem.getAudioInputStream(decoded, audioInput);

            Clip clip = AudioSystem.getClip();
            clip.open(decodedAudio);
            clip.start();

            clip.addLineListener(ev -> {
                if (ev.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });

        } catch (Exception e) {
            System.err.println("[SFX] Erreur lecture: " + resourcePath);
            e.printStackTrace();
        }
    }
    
    /**
     * son quand un worm meurt
     */
    public static void playDepopSound() {
        play("/sfx/depop.wav");
    }
    
    /**
     * son quand on casse un block
     */
    public static void playBreakBlockSound() {
        play("/sfx/breakblock.wav");
    }

    /**
     * Défini la sonorité On/off
     * @param m
     */
    public static void setMuted(boolean m) {
        muted = m;
    }

    /**
     * est mute ou non
     * @return 
     */
    public static boolean isMuted() {
        return muted;
    }
}

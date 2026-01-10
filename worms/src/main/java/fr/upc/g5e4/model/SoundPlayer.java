package fr.upc.g5e4.model;
import javax.sound.sampled.*;
import java.io.BufferedInputStream;
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
    private static volatile boolean muted = false;

    // Cache pour les clips audio pré-chargés
    private static final Map<String, byte[]> audioCache = new HashMap<>();
    private static boolean initialized = false;
    
    // pour la musique de fond
    private static Clip backgroundMusicClip = null;
    private static boolean manuallyPaused = false;  // true si mise en pause par le menu pause
    
    /**
     * Initialise le système audio et pré-charge les sons communs
     * Appeler au démarrage du jeu pour éviter le lag au premier tir
     */
    public static void initialize() {
        if (initialized) return;
        
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
     *le son d'une arme avec volume réduit 
     */
    public static void playWeaponSound(String resourcePath) {
        if (muted) return;
        new Thread(() -> playSync(resourcePath)).start();
    }
    
    /**
     * Joue un son de manière synchrone
     */
    private static void playSync(String resourcePath) {
    try {
        InputStream raw;
        
        // Utiliser le cache si disponible
        if (audioCache.containsKey(resourcePath)) {
            raw = new java.io.ByteArrayInputStream(audioCache.get(resourcePath));
        } else {
            raw = SoundPlayer.class.getResourceAsStream(resourcePath);
            if (raw == null) {
                System.err.println("[SFX] Introuvable: " + resourcePath);
                return;
            }
        }

        BufferedInputStream bis = new BufferedInputStream(raw);
        AudioInputStream ais = AudioSystem.getAudioInputStream(bis);
        AudioFormat base = ais.getFormat();

        // Conversion forcée vers PCM 16-bit (format sûr)
        AudioFormat decoded = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                base.getSampleRate(),
                16,
                base.getChannels(),
                base.getChannels() * 2,
                base.getSampleRate(),
                false
        );

        AudioInputStream dais = AudioSystem.getAudioInputStream(decoded, ais);

        Clip clip = AudioSystem.getClip();
        clip.open(dais);
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
        
        // Gérer la musique de fond
        if (m) {
            pauseBackgroundMusic();
        } else {
            // reprendre que si pas en pause manuelle
            if (!manuallyPaused) {
                resumeBackgroundMusic();
            }
        }
    }

    /**
     * est mute ou non
     * @return 
     */
    public static boolean isMuted() {
        return muted;
    }
    
    /**
     *Joue une musique de fond en boucle
     * @param resourcePath chemin vers le fichier audio
     */
    public static void playBackgroundMusic(String resourcePath) {
        try {
            // arrête la musique actuelle 
            stopBackgroundMusic();
            
            InputStream raw = SoundPlayer.class.getResourceAsStream(resourcePath);
            if (raw == null) {
                System.err.println("musique Introuvable: " + resourcePath);
                return;
            }
            
            BufferedInputStream bis = new BufferedInputStream(raw);
            AudioInputStream ais = AudioSystem.getAudioInputStream(bis);
            AudioFormat base = ais.getFormat();
            
            // Conversion vers PCM 16-bit (format sûr)
            AudioFormat decoded = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    base.getSampleRate(),
                    16,
                    base.getChannels(),
                    base.getChannels() * 2,
                    base.getSampleRate(),
                    false
            );
            
            AudioInputStream dais = AudioSystem.getAudioInputStream(decoded, ais);
            
            backgroundMusicClip = AudioSystem.getClip();
            backgroundMusicClip.open(dais);
            
            // Joue en boucle infinie
            backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
            
            if (!muted) {
                backgroundMusicClip.start();
            }
            
            System.out.println("Musique lancée: " + resourcePath);
        } catch (Exception e) {
            System.err.println("Erreur lecture: " + resourcePath);
            e.printStackTrace();
        }
    }
    
    /**
     * Stop la musique de fond
     */
    public static void stopBackgroundMusic() {
        if (backgroundMusicClip != null) {
            if (backgroundMusicClip.isRunning()) {
                backgroundMusicClip.stop();
            }
            backgroundMusicClip.close();
            backgroundMusicClip = null;
            System.out.println("Musique arrêtée");
        }
    }
    
    /**
     *met en pause la musique de fond
     */
    public static void pauseBackgroundMusic() {
        manuallyPaused = true;
        if (backgroundMusicClip != null && backgroundMusicClip.isRunning()) {
            backgroundMusicClip.stop();
        }
    }
    
    /**
     * reprend la musique de fond
     */
    public static void resumeBackgroundMusic() {
        manuallyPaused = false;
        // reprendre que si pas mute
        if (!muted && backgroundMusicClip != null && !backgroundMusicClip.isRunning()) {
            backgroundMusicClip.start();
        }
    }
}

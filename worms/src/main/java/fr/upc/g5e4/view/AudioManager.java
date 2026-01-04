package fr.upc.g5e4.view;

import javax.sound.sampled.*;
import java.io.InputStream;

/**
 * gestionnaire audio pour la musique de fond
 */
public class AudioManager {
    private static AudioManager instance;
    private Clip backgroundClip;
    private float volume = 0.5f;
    
    private AudioManager() {
    }
    
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }
    
    /**
     * joue de la musique de fond en boucle
     * @param resourcePath chemin de la ressource 
     */
    public void playBackgroundMusic(String resourcePath) {
        try {
            // arrête la musique actuelle si elle existe
            stopBackgroundMusic();
            
            // charge le fichier audio
            InputStream audioStream = getClass().getResourceAsStream(resourcePath);
            if (audioStream == null) {
                System.err.println("Fichier non trouvé: " + resourcePath);
                return;
            }
            
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioStream);
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audioInputStream);
            
            // définit le volume
            setVolume(volume);
            
            // joue en boucle infinie
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundClip.start();
            
            System.out.println("musique lancée: " + resourcePath);
        } catch (Exception e) {
            System.err.println("Erreur du chargement de la musique: " + e.getMessage());
        }
    }
    
    /**
     * arrête la musique de fond
     */
    public void stopBackgroundMusic() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
            backgroundClip.close();
            System.out.println("Musique arrêtée");
        }
    }
    
    /**
     * Définit le volume (0.0 = muet, 1.0 = volume max)
     */
    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
        if (backgroundClip != null) {
            FloatControl volumeControl = (FloatControl) backgroundClip.getControl(FloatControl.Type.MASTER_GAIN);
            // convertit le volume en décibels
            float dB = 20f * (float) Math.log10(this.volume == 0.0f ? 0.001f : this.volume);
            volumeControl.setValue(dB);
        }
    }
    
    /**
     * obtient le volume actuel 
     */
    public float getVolume() {
        return volume;
    }
    
    /**
     * pause la musique
     */
    public void pauseBackgroundMusic() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
        }
    }
    
    /**
     * reprend la musique en pause
     */
    public void resumeBackgroundMusic() {
        if (backgroundClip != null && !backgroundClip.isRunning()) {
            backgroundClip.start();
        }
    }
}

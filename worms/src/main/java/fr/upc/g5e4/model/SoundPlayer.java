package fr.upc.g5e4.model;
import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

public final class SoundPlayer {
    private SoundPlayer() {}

    public static void play(String resourcePath) {
    try {
        InputStream raw = SoundPlayer.class.getResourceAsStream(resourcePath);
        if (raw == null) {
            System.err.println("[SFX] Introuvable: " + resourcePath);
            return;
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
}

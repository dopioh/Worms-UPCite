package fr.upc.g5e4.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import fr.upc.g5e4.view.LoadingBarPanel;

/**
 * Vue pour la cinématique d'introduction du combat
 * Affiche une animation de présentation avant le début du jeu
 */
public class IntroCinematicView extends JPanel implements ActionListener {
    private LoadingBarPanel loadingPanel;
    private Timer progressTimer;
    private int progress = 0;
    private Runnable onCinematicEnd;
    private JLabel loadingLabel;
    // Textes de la cinématique à affiche
    private final String[] cinematicTexts = {
        "PREPARATION DU COMBAT...",
        "EQUIPES PRETES AU COMBAT !",
        "TERRAIN GENERE",
        "VENT INITIALISE",
        "QUE LE COMBAT COMMENCE !"
    };

    private Font customFont;
    private Font customFontFinal;

    public IntroCinematicView(java.util.List<?> teams, Runnable onCinematicEnd) {
        this.onCinematicEnd = onCinematicEnd;
        setBackground(Color.BLACK);
        setLayout(new GridBagLayout());

        loadingPanel = new LoadingBarPanel();
        loadingPanel.setOpaque(false);
        loadingPanel.setBarColor(new Color(40, 220, 80)); // Vert vif
        loadingPanel.setProgress(0);

        // Label pour afficher le texte sous la barre
        loadingLabel = new JLabel(cinematicTexts[0], SwingConstants.CENTER);
        customFont = null;
        customFontFinal = null;
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("BBHBartle-Regular.ttf");
            if (is != null) {
                customFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.BOLD, 18f);
                customFontFinal = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.BOLD, 26f);
            }
        } catch (Exception e) {
            customFont = new Font("Segoe UI", Font.BOLD, 18);
            customFontFinal = new Font("Segoe UI", Font.BOLD, 26);
        }
        if (customFont == null) {
            customFont = new Font("Segoe UI", Font.BOLD, 18);
        }
        if (customFontFinal == null) {
            customFontFinal = new Font("Segoe UI", Font.BOLD, 26);
        }
        loadingLabel.setFont(customFont);
        loadingLabel.setForeground(Color.WHITE);
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 30, 0);
        gbc.fill = GridBagConstraints.NONE;
        add(loadingPanel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 0, 0);
        add(loadingLabel, gbc);

        setPreferredSize(new Dimension(1280, 720));

        // Timer pour remplir la barre de façon fluide
        progressTimer = new Timer(15, this);
        progressTimer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (progress < 100) {
            progress++;
            loadingPanel.setProgress(progress);
            // Afficher le message correspondant à l'étape (sauf le dernier)
            int step = Math.min(progress * (cinematicTexts.length - 1) / 100, cinematicTexts.length - 2);
            loadingLabel.setFont(customFont);
            loadingLabel.setText(cinematicTexts[step]);
        } else {
            // Afficher le message final bien centré avec la bonne police
            loadingLabel.setFont(customFontFinal);
            loadingLabel.setText("<html><div style='text-align:center;'>" + cinematicTexts[cinematicTexts.length - 1] + "</div></html>");
            progressTimer.stop();
            // Laisser le message final affiché un court instant avant de finir
            Timer endTimer = new Timer(900, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    if (onCinematicEnd != null) {
                        onCinematicEnd.run();
                    }
                }
            });
            endTimer.setRepeats(false);
            endTimer.start();
        }
    }

    /**
     * Force l'arrêt de la cinématique
     */
    public void stopCinematic() {
        if (progressTimer != null) {
            progressTimer.stop();
        }
    }
}
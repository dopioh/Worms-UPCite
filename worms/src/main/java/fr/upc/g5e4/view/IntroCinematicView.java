package fr.upc.g5e4.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import fr.upc.g5e4.model.TeamModel;

/**
 * Vue pour la cinématique d'introduction du combat
 * Affiche une animation de présentation avant le début du jeu
 */
public class IntroCinematicView extends JPanel implements ActionListener {

    private Timer cinematicTimer;
    private Timer fadeTimer;
    private float fadeAlpha = 0.0f;
    private boolean isFadingIn = true;

    private List<TeamModel> teams;
    private Runnable onCinematicEnd;

    private int currentStep = 0;
    private String[] cinematicTexts = {
        "PRÉPARATION DU COMBAT...",
        "ÉQUIPES PRÊTES AU COMBAT !",
        "TERRAIN GÉNÉRÉ",
        "VENT INITIALISÉ",
        "QUE LE COMBAT COMMENCE !"
    };

    public IntroCinematicView(List<TeamModel> teams, Runnable onCinematicEnd) {
                // Ajout : touche pour skipper l'intro (Espace) via InputMap/ActionMap (plus fiable)
                getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "skipIntro");
                getActionMap().put("skipIntro", new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        stopCinematic();
                        if (onCinematicEnd != null) onCinematicEnd.run();
                    }
                });
        this.teams = teams;
        this.onCinematicEnd = onCinematicEnd;

        setBackground(Color.BLACK);
        setLayout(new BorderLayout());


        // Timer pour l'animation de la cinématique
        cinematicTimer = new Timer(1000, this); // Changement toutes les secondes
        cinematicTimer.start();

        // Timer pour le fade in/out
        fadeTimer = new Timer(50, e -> {
            if (isFadingIn) {
                fadeAlpha += 0.05f;
                if (fadeAlpha >= 1.0f) {
                    fadeAlpha = 1.0f;
                    isFadingIn = false;
                }
            } else {
                fadeAlpha -= 0.05f;
                if (fadeAlpha <= 0.0f) {
                    fadeAlpha = 0.0f;
                    isFadingIn = true;
                    currentStep++;
                }
            }
            repaint();
        });
        fadeTimer.start();

        setPreferredSize(new Dimension(1280, 720));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        // Activer l'anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Fond noir total
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Appliquer le fade
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));

        if (currentStep < cinematicTexts.length) {
            drawCurrentStep(g2d);
        }

        g2d.dispose();
    }

    private void drawCurrentStep(Graphics2D g2d) {
        String text = cinematicTexts[currentStep];

        // Police principale plus stylisée et moderne
        Font titleFont = new Font("Segoe UI", Font.BOLD, 52);
        g2d.setFont(titleFont);
        FontMetrics fm = g2d.getFontMetrics();

        // Position centrée
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = getHeight() / 2;

        // Effet de glow/ombre multiple pour plus d'impact
        g2d.setColor(new Color(255, 100, 0, 150)); // Orange glow
        g2d.drawString(text, x + 3, y + 3);
        g2d.setColor(new Color(0, 0, 0, 120)); // Ombre noire
        g2d.drawString(text, x + 1, y + 1);

        // Texte principal en blanc avec léger effet cyan
        g2d.setColor(Color.WHITE);
        g2d.drawString(text, x, y);

        // Effet de brillance subtile
        g2d.setColor(new Color(100, 200, 255, 80));
        g2d.drawString(text, x, y);

        // Informations supplémentaires selon l'étape
        drawStepDetails(g2d, y + 70);
    }

    private void drawStepDetails(Graphics2D g2d, int startY) {
        Font detailFont = new Font("Segoe UI", Font.PLAIN, 26);
        g2d.setFont(detailFont);

        switch (currentStep) {
            case 0: // "PRÉPARATION DU COMBAT..."
                drawTeamsInfo(g2d, startY);
                break;
            case 1: // "ÉQUIPES PRÊTES AU COMBAT !"
                drawTeamsReady(g2d, startY);
                break;
            case 2: // "TERRAIN GÉNÉRÉ"
                drawTerrainInfo(g2d, startY);
                break;
            case 3: // "VENT INITIALISÉ"
                drawWindInfo(g2d, startY);
                break;
            case 4: // "QUE LE COMBAT COMMENCE !"
                drawFinalMessage(g2d, startY);
                break;
        }
    }

    private void drawTeamsInfo(Graphics2D g2d, int startY) {
        g2d.setColor(Color.LIGHT_GRAY);
        String teamCount = teams.size() + " équipe" + (teams.size() > 1 ? "s" : "") + " détectée" + (teams.size() > 1 ? "s" : "");
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(teamCount)) / 2;
        g2d.drawString(teamCount, x, startY);
    }

    private void drawTeamsReady(Graphics2D g2d, int startY) {
        int y = startY;
        for (int i = 0; i < teams.size(); i++) {
            TeamModel team = teams.get(i);
            g2d.setColor(getTeamColor(i));
            String teamInfo = "Équipe " + (i + 1) + ": " + team.getTabWorms().length + " vers";
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(teamInfo)) / 2;
            g2d.drawString(teamInfo, x, y);
            y += 30;
        }
    }

    private void drawTerrainInfo(Graphics2D g2d, int startY) {
        g2d.setColor(Color.GREEN);
        String terrainInfo = "Terrain procédural généré avec succès";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(terrainInfo)) / 2;
        g2d.drawString(terrainInfo, x, startY);
    }

    private void drawWindInfo(Graphics2D g2d, int startY) {
        g2d.setColor(Color.CYAN);
        String windInfo = "Conditions météorologiques initialisées";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(windInfo)) / 2;
        g2d.drawString(windInfo, x, startY);
    }

    private void drawFinalMessage(Graphics2D g2d, int startY) {
        g2d.setColor(Color.YELLOW);
        String finalMsg = "Bonne chance à tous les combattants !";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(finalMsg)) / 2;
        g2d.drawString(finalMsg, x, startY);
    }

    private Color getTeamColor(int teamIndex) {
        Color[] teamColors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA, Color.CYAN};
        return teamColors[teamIndex % teamColors.length];
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (currentStep >= cinematicTexts.length) {
            // Fin de la cinématique
            cinematicTimer.stop();
            fadeTimer.stop();
            if (onCinematicEnd != null) {
                onCinematicEnd.run();
            }
        }
    }

    /**
     * Force l'arrêt de la cinématique
     */
    public void stopCinematic() {
        if (cinematicTimer != null) {
            cinematicTimer.stop();
        }
        if (fadeTimer != null) {
            fadeTimer.stop();
        }
    }
}
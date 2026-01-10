package fr.upc.g5e4.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import fr.upc.g5e4.model.CharacterModel;
import fr.upc.g5e4.model.GameStats;
import fr.upc.g5e4.model.TeamModel;
import fr.upc.g5e4.model.TeamInitialisation;
import fr.upc.g5e4.util.ImageCache;
/**
 * Vue pour l'écran de fin de partie
 * Affiche l'animation des gagnants marchant vers le trophée avec particules
 */
public class EndGameView extends JPanel {
    private TeamModel winningTeam;
    private TeamView winningTeamView;
    private Map<Integer, GameStats> allTeamStats;
    private java.util.List<TeamInitialisation> allTeams;  // Pour récupérer les noms par ID
    private String winningTeamName;
    private int winningTeamId;
    
    private BufferedImage trophyImage;
    private BufferedImage sparkleImage;
    private BufferedImage miniTrophyImage;  // Petite icône de trophée pour les stats
    private BufferedImage backgroundSnapshot;
    private Font customFont;
    private Font statsFont;
    
    // Animation
    private int animationFrame = 0;
    private static final int BASE_WIDTH = 1280;  // Largeur de référence
    private static final int BASE_HEIGHT = 720;  // Hauteur de référence
    private static final int BASE_TROPHY_X = 850;
    private static final int BASE_TROPHY_Y = 430;
    private static final int TROPHY_SIZE = 170;
    
    // Calculer les positions centrées en fonction de la taille de l'écran
    private int getTrophyX() {
        float scale = getScale();
        int scaledBaseX = (int)(BASE_TROPHY_X * scale);
        int scaledWidth = (int)(BASE_WIDTH * scale);
        int offsetX = (getWidth() - scaledWidth) / 2;
        return scaledBaseX + offsetX;
    }
    
    private int getTrophyY() {
        float scale = getScale();
        int scaledBaseY = (int)(BASE_TROPHY_Y * scale);
        int scaledHeight = (int)(BASE_HEIGHT * scale);
        int offsetY = (getHeight() - scaledHeight) / 2;
        return scaledBaseY + offsetY;
    }
    
    private float getScale() {
        // Calculer le facteur de zoom basé sur la taille de l'écran
        float scaleX = (float)getWidth() / BASE_WIDTH;
        float scaleY = (float)getHeight() / BASE_HEIGHT;
        return Math.min(scaleX, scaleY);  // Utiliser le plus petit pour garder les proportions
    }
    
    private int getOffsetX() {
        float scale = getScale();
        int scaledWidth = (int)(BASE_WIDTH * scale);
        return (getWidth() - scaledWidth) / 2;
    }
    
    private int getOffsetY() {
        float scale = getScale();
        int scaledHeight = (int)(BASE_HEIGHT * scale);
        return (getHeight() - scaledHeight) / 2;
    }
    
    // Positions des personnages qui arrivent de la droite
    private ArrayList<WormAnimState> wormStates = new ArrayList<>();
    
    // Particules
    private ArrayList<Particle> particles = new ArrayList<>();
    private Random random = new Random();
    
    // Timer pour l'animation
    private Timer animationTimer;
    
    // Phase de l'animation
    private AnimationPhase currentPhase = AnimationPhase.WORMS_WALKING;
    private int phaseTimer = 0;
    
    // Scroll pour les statistiques
    private int statsScrollOffset = 0;
    private static final int SCROLL_SPEED = 30;
    
    enum AnimationPhase {
        WORMS_WALKING,      // Les worms marchent vers le trophée
        CELEBRATION,        // Les worms sautent autour du trophée
        STATS_DISPLAY       // Affichage des statistiques
    }
    
    /**
     * État d'animation d'un worm
     */
    private static class WormAnimState {
        CharacterModel model;
        double x, y;
        double targetX, targetY;
        double velocityX, velocityY;
        boolean isJumping;
        int jumpCooldown;
        
        WormAnimState(CharacterModel model, double startX, double startY, double targetX, double targetY, int offset) {
            this.model = model;
            this.x = startX;
            this.y = startY;
            this.targetX = targetX;
            this.targetY = targetY;
            this.velocityX = -2.0;  // Vitesse vers la gauche
            this.velocityY = 0;
            this.isJumping = false;
            this.jumpCooldown = 0;
        }
    }
    
    /**
     * Particule scintillante
     */
    private static class Particle {
        double x, y;
        int life;
        int maxLife;
        boolean fadingIn;
        
        Particle(double x, double y) {
            this.x = x;
            this.y = y;
            Random r = new Random();
            this.maxLife = 40 + r.nextInt(40);  // Durée de vie 40-80 frames
            this.life = 0;  // Commence à 0 pour fade in
            this.fadingIn = true;
        }
        
        void update() {
            // fade in puis fade out
            if (fadingIn) {
                life++;
                if (life >= maxLife / 2) {
                    fadingIn = false;
                }
            } else {
                life--;
            }
        }
        
        boolean isDead() {
            return life <= 0 && !fadingIn;
        }
        
        float getAlpha() {
            // Fade in/out smooth
            float progress = (float)life / (maxLife / 2.0f);
            return Math.min(1.0f, progress);
        }
    }
    
    public EndGameView(TeamModel winningTeam, TeamView winningTeamView, String winningTeamName, 
                       Map<Integer, GameStats> allTeamStats, java.util.List<TeamInitialisation> allTeams,
                       BufferedImage backgroundSnapshot) {
        this.winningTeam = winningTeam;
        this.winningTeamView = winningTeamView;
        this.winningTeamName = winningTeamName;
        this.winningTeamId = winningTeam.getTeamID();
        this.allTeamStats = allTeamStats;
        this.allTeams = allTeams;
        this.backgroundSnapshot = backgroundSnapshot;
        
        setLayout(null);
        setPreferredSize(new Dimension(1280, 720));
        setOpaque(true);
        
        loadImages();
        initializeWormStates();
        startAnimation();
        
        // Ajouter le listener de scroll pour les stats
        addMouseWheelListener(e -> {
            statsScrollOffset -= e.getWheelRotation() * SCROLL_SPEED;
            
            // Calculer les limites de scroll
            float scale = getScale();
            int baseRectHeight = (int)(180 * scale);
            int spacing = (int)(20 * scale);
            int totalStatsHeight = allTeamStats.size() * (baseRectHeight + spacing);
            int visibleHeight = getHeight() - (int)(300 * scale) - (int)(50 * scale);
            
            // Limite haute : ne pas scroller au-dessus de la position initiale
            if (statsScrollOffset > 0) {
                statsScrollOffset = 0;
            }
            
            // Limite basse : ne pas scroller plus bas que nécessaire
            int maxScrollDown = Math.min(0, -(totalStatsHeight - visibleHeight));
            if (statsScrollOffset < maxScrollDown) {
                statsScrollOffset = maxScrollDown;
            }
            
            repaint();
        });
    }
    
    private void loadImages() {
        trophyImage = ImageCache.get("/Trophee.png");
        sparkleImage = ImageCache.get("/Sparkle.png");
        miniTrophyImage = ImageCache.get("/Trophee.png");
        
        if (trophyImage == null || sparkleImage == null) {
            System.err.println("Erreur lors du chargement des images EndGameView");
        }

        try {
            // Charger la police bbH Bartle
            Font baseFont = Font.createFont(Font.TRUETYPE_FONT, 
                getClass().getResourceAsStream("/BBHBartle-Regular.ttf"));
            customFont = baseFont.deriveFont(72f); // Taille par défaut
            
            // Charger Google Sans Bold
            Font statsBaseFont = Font.createFont(Font.TRUETYPE_FONT,
                getClass().getResourceAsStream("/GoogleSans-Bold.ttf"));
            statsFont = statsBaseFont.deriveFont(20f); // Taille par défaut
        } catch (Exception e) {
            System.err.println("Erreur chargement images/police fin de partie: " + e.getMessage());
            // Fallback sur Arial si la police ne charge pas
            customFont = new Font("Arial", Font.BOLD, 72);
            statsFont = new Font("Arial", Font.BOLD, 20);
        }
    }
    
    private void initializeWormStates() {
        wormStates.clear();
        CharacterModel[] worms = winningTeam.getTabWorms();
        
        // Compter les worms vivants d'abord
        int aliveCount = 0;
        for (CharacterModel worm : worms) {
            if (worm != null && !worm.isDead()) {
                aliveCount++;
            }
        }
        
        // Utiliser les dimensions de référence pour l'initialisation
        // Positionner les worms hors écran à droite, centrés autour du trophée
        int index = 0;
        int SPACING = 140;  // Espacement entre worms
        double totalWidth = (aliveCount - 1) * SPACING;  // Largeur totale occupée
        double firstWormX = BASE_TROPHY_X - totalWidth / 2.0;  // Position du premier worm pour centrer le groupe
        int wormY = BASE_TROPHY_Y + 80;  // Worms 80px sous le trophée
        
        for (int i = 0; i < worms.length; i++) {
            if (worms[i] != null && !worms[i].isDead()) {
                double startX = BASE_WIDTH + 120 + index * 80;  // Hors écran à droite (position de référence)
                double startY = wormY;
                // Position cible centrée autour du trophée
                double targetX = firstWormX + index * SPACING;
                double targetY = wormY;
                
                WormAnimState state = new WormAnimState(worms[i], startX, startY, targetX, targetY, index * 10);
                state.velocityX = -4.0;
                wormStates.add(state);
                index++;
                System.out.println("Worm " + i + " initialisé à (" + startX + ", " + startY + "), cible: (" + targetX + ", " + targetY + ")");
            }
        }
        System.out.println("Nombre de worms gagnants: " + aliveCount + ", centrage autour de " + BASE_TROPHY_X);
    }
    
    private void startAnimation() {
        animationTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateAnimation();
                repaint();
            }
        });
        animationTimer.start();
    }
    
    private void updateAnimation() {
        animationFrame++;
        phaseTimer++;
        
        switch (currentPhase) {
            case WORMS_WALKING:
                updateWormsWalking();
                // Toujours générer des particules
                generateParticles();
                if (phaseTimer > 120) {  // 2 secondes
                    // Vérifier si tous les worms sont arrivés EXACTEMENT à leur position
                    boolean allArrived = true;
                    for (WormAnimState state : wormStates) {
                        if (state.x > state.targetX) {  // Doit être exactement à la cible ou l'avoir dépassée
                            allArrived = false;
                            break;
                        }
                    }
                    if (allArrived) {
                        currentPhase = AnimationPhase.CELEBRATION;
                        phaseTimer = 0;
                    }
                }
                break;
                
            case CELEBRATION:
                updateCelebration();
                generateParticles();
                if (phaseTimer > 120) {  // 2 secondes de célébration
                    // Vérifier que tous les worms sont au sol avant de passer à STATS_DISPLAY
                    boolean allOnGround = true;
                    for (WormAnimState state : wormStates) {
                        if (state.isJumping) {
                            allOnGround = false;
                            break;
                        }
                    }
                    if (allOnGround) {
                        currentPhase = AnimationPhase.STATS_DISPLAY;
                        phaseTimer = 0;
                    }
                }
                break;
                
            case STATS_DISPLAY:
                // Phase finale - continuer les particules
                if (animationFrame % 10 == 0) {
                    generateParticles();
                }
                break;
        }
        
        updateParticles();
    }
    
    private void updateWormsWalking() {
        for (WormAnimState state : wormStates) {
            // Déplacement horizontal vers la cible
            if (state.x > state.targetX) {
                state.x += state.velocityX;
            } else {
                state.x = state.targetX;  // Verrouille la position exacte pour un centrage parfait
                state.velocityX = 0;
            }
            
            // Saut périodique pendant la marche
            state.jumpCooldown--;
            if (state.jumpCooldown <= 0 && !state.isJumping) {
                state.isJumping = true;
                state.velocityY = -8;
                state.jumpCooldown = 30 + random.nextInt(20);
            }
            
            // Gestion du saut
            if (state.isJumping) {
                state.y += state.velocityY;
                state.velocityY += 0.5;  // Gravité
                
                if (state.y >= state.targetY) {
                    state.y = state.targetY;
                    state.isJumping = false;
                    state.velocityY = 0;
                }
            }
        }
    }
    
    private void updateCelebration() {
        for (WormAnimState state : wormStates) {
            // Continuer le déplacement horizontal si pas encore arrivé
            if (state.x > state.targetX) {
                state.x += state.velocityX;
            } else {
                state.x = state.targetX;
                state.velocityX = 0;
            }
            
            // Faire sauter les worms de manière aléatoire (mais pas trop tard dans la phase)
            state.jumpCooldown--;
            if (state.jumpCooldown <= 0 && !state.isJumping && phaseTimer < 160) {  // Arrêter les nouveaux sauts 20 frames avant la fin
                state.isJumping = true;
                state.velocityY = -6 - random.nextInt(3);
                state.jumpCooldown = 20 + random.nextInt(30);
            }
            
            if (state.isJumping) {
                state.y += state.velocityY;
                state.velocityY += 0.5;
                
                if (state.y >= state.targetY) {
                    state.y = state.targetY;
                    state.isJumping = false;
                    state.velocityY = 0;
                }
            }
        }
    }
    
    private void generateParticles() {
        // Générer des particules statiques autour du trophée
        if (animationFrame % 7 == 0) {
            double angle = random.nextDouble() * Math.PI * 2;
            double radius = 30 + random.nextDouble() * 50;  // Rayon 30-80
            double px = BASE_TROPHY_X + Math.cos(angle) * radius;
            double py = BASE_TROPHY_Y + Math.sin(angle) * radius;
            particles.add(new Particle(px, py));
        }
    }
    
    private void updateParticles() {
        // supprimer les particules mortes
        java.util.List<Particle> toRemove = new java.util.ArrayList<>();
        for (Particle p : particles) {
            if (p.isDead()) {
                toRemove.add(p);
            }
        }
        particles.removeAll(toRemove);
        
        for (Particle p : particles) {
            p.update();
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Dessiner le fond de la map en sombre
        if (backgroundSnapshot != null) {
            // Assombrir l'arrière-plan
            g2d.drawImage(backgroundSnapshot, 0, 0, getWidth(), getHeight(), null);
            g2d.setColor(new Color(0, 0, 0, 180));  // Overlay noir semi-transparent
            g2d.fillRect(0, 0, getWidth(), getHeight());
        } else {
            g2d.setColor(new Color(20, 20, 30));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        
        // Dessiner les particules (derrière)
        drawParticles(g2d, false);
        
        // Dessiner le trophée
        float scale = getScale();
        if (trophyImage != null) {
            int trophyDrawSize = (int)(TROPHY_SIZE * scale);
            // Effet de zoom dezoom
            if (currentPhase == AnimationPhase.CELEBRATION || currentPhase == AnimationPhase.STATS_DISPLAY) {
                trophyDrawSize += (int)(Math.sin(animationFrame * 0.1) * 5 * scale);
            }
            g2d.drawImage(trophyImage, 
                         getTrophyX() - trophyDrawSize/2, 
                         getTrophyY() - trophyDrawSize/2, 
                         trophyDrawSize, trophyDrawSize, null);
        } else {
            // Fallback rectangle doré
            int trophyDrawSize = (int)(TROPHY_SIZE * scale);
            g2d.setColor(new Color(255, 215, 0));
            g2d.fillRect(getTrophyX() - trophyDrawSize/2, getTrophyY() - trophyDrawSize/2, trophyDrawSize, trophyDrawSize);
        }
        
        // Dessiner les worms de l'équipe gagnante
        drawWorms(g2d);
        
        // Dessiner les particules (devant)
        drawParticles(g2d, true);
        
        // Dessiner le texte et les statistiques
        drawVictoryText(g2d);
    }
    
    private void drawWorms(Graphics2D g2d) {
        int offsetX = getOffsetX();
        int offsetY = getOffsetY();
        float scale = getScale();
        
        for (WormAnimState state : wormStates) {
            
            // Dessiner le sprite du worm à la position animée
            if (winningTeamView != null && winningTeamView.getViews() != null) {
                // Trouver la vue correspondante
                CharacterModel[] worms = winningTeam.getTabWorms();
                for (int i = 0; i < worms.length; i++) {
                    if (worms[i] == state.model && i < winningTeamView.getViews().length) {
                        CharacterView view = winningTeamView.getViews()[i];
                        
                        // Calculer la position avec offset et scaling
                        int scaledX = (int)(state.x * scale) + offsetX;
                        int scaledY = (int)(state.y * scale) + offsetY;
                        
                        // Dessiner directement à la position animée
                        int size = (int)(view.getSize() * 3 * scale);  // Triplé pour être bien visible + scaling
                        BufferedImage sprite = view.getSpriteImage();
                        if (sprite != null) {
                            g2d.drawImage(sprite, 
                                         scaledX - size/2, 
                                         scaledY - size/2, 
                                         size, size, null);
                        }
                        break;
                    }
                }
            } else {
                // Fallback carré simple
                int scaledX = (int)(state.x * scale) + offsetX;
                int scaledY = (int)(state.y * scale) + offsetY;
                int size = (int)(50 * scale);
                g2d.setColor(Color.BLUE);
                g2d.fillRect(scaledX - size/2, scaledY - size/2, size, size);
            }
        }
    }
    
    private void drawParticles(Graphics2D g2d, boolean front) {
        int offsetX = getOffsetX();
        int offsetY = getOffsetY();
        float scale = getScale();
        
        for (Particle p : particles) {
            // Utiliser les coordonnées de base pour la comparaison
            if ((front && p.y < BASE_TROPHY_Y) || (!front && p.y >= BASE_TROPHY_Y)) {
                continue;  // Dessiner seulement les particules appropriées
            }
            
            float alpha = p.getAlpha();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            
            // Calculer les positions avec offset et scaling
            int scaledX = (int)(p.x * scale) + offsetX;
            int scaledY = (int)(p.y * scale) + offsetY;
            
            if (sparkleImage != null) {
                int size = (int)((30 + (20 * alpha)) * scale);  // Taille 30-50 pixels (très grandes sparkles) avec scaling
                g2d.drawImage(sparkleImage, scaledX - size/2, scaledY - size/2, size, size, null);
            } else {
                int size = (int)((15 + (10 * alpha)) * scale);
                g2d.fillOval(scaledX - size/2, scaledY - size/2, size, size);
            }
        }
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }
    
    private void drawVictoryText(Graphics2D g2d) {
        // Calculer l'offset pour centrer le contenu
        int offsetX = getOffsetX();
        int offsetY = getOffsetY();
        float scale = getScale();
        
        // Animation fade-in et slide-in depuis la gauche
        float animProgress = 1.0f;
        int slideOffset = 0;
        
        if (currentPhase == AnimationPhase.WORMS_WALKING && phaseTimer <= 60) {
            // Animation progressive pendant les 60 premières frames
            float t = Math.min(1.0f, phaseTimer / 60.0f);
            // Easing ease-out pour une animation plus fluide
            animProgress = 1.0f - (float)Math.pow(1.0f - t, 3);
            slideOffset = (int)((1.0f - animProgress) * -150 * scale);  // Glisse depuis la gauche avec scaling
        }
        
        // Appliquer la transparence de l'animation
        Composite originalComposite = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, animProgress));
        
        // Titre "VICTOIRE!" - ancré à gauche (aligné avec les stats)
        g2d.setColor(new Color(255, 215, 0));  // Or
        g2d.setFont(customFont.deriveFont((float)(72 * scale)));
        String victoryText = "VICTOIRE";
        int victoryX = (int)(100 * scale) + offsetX + slideOffset;  // Aligné avec la boîte de stats
        g2d.drawString(victoryText, victoryX, (int)(150 * scale) + offsetY);
        
        // Nom de l'équipe gagnante - ancré à gauche
        g2d.setFont(customFont.deriveFont((float)(36 * scale)));
        String teamText = getTeamDisplayName(winningTeamId);
        int teamX = (int)(100 * scale) + offsetX + slideOffset;  // Même alignement
        g2d.drawString(teamText, teamX, (int)(200 * scale) + offsetY);
        
        // Restaurer la transparence originale
        g2d.setComposite(originalComposite);
        
        // Statistiques (apparaissent plus tôt - après 60 frames de WORMS_WALKING)
        if ((currentPhase == AnimationPhase.WORMS_WALKING && phaseTimer > 60) || 
            currentPhase == AnimationPhase.CELEBRATION || 
            currentPhase == AnimationPhase.STATS_DISPLAY) {
            drawStatistics(g2d);
        }
    }
    
    private void drawStatistics(Graphics2D g2d) {
        // Calculer l'offset pour centrer le contenu
        int offsetX = getOffsetX();
        int offsetY = getOffsetY();
        float scale = getScale();
        
        // Animation: fade-in et slide-in depuis la gauche
        float animProgress = 1.0f;
        int slideOffset = 0;
        
        // Démarrer l'animation dès que les stats commencent à apparaître (après 60 frames de WORMS_WALKING)
        if (currentPhase == AnimationPhase.WORMS_WALKING && phaseTimer > 60 && phaseTimer <= 120) {
            // Animation progressive pendant 60 frames
            float t = Math.min(1.0f, (phaseTimer - 60) / 60.0f);
            // Easing pour une animation plus fluide
            animProgress = 1.0f - (float)Math.pow(1.0f - t, 3);
            slideOffset = (int)((1.0f - animProgress) * -100 * scale);  // Glisse depuis la gauche avec scaling
        }
        
        // Appliquer la transparence de l'animation
        Composite originalComposite = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, animProgress));
        
        // Afficher les statistiques de toutes les équipes
        int teamCount = allTeamStats.size();
        int rectWidth = (int)(350 * scale);
        int baseRectHeight = (int)(180 * scale);  // Hauteur de base pour chaque équipe
        int spacing = (int)(20 * scale);  // Espacement entre les rectangles
        int startY = (int)(300 * scale) + offsetY + (int)(statsScrollOffset * scale);
        
        int index = 0;
        // Trier pour afficher l'équipe gagnante en premier
        java.util.List<Integer> sortedTeamIds = new java.util.ArrayList<>(allTeamStats.keySet());
        java.util.Collections.sort(sortedTeamIds, new java.util.Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                if (a.equals(winningTeamId)) return -1;
                if (b.equals(winningTeamId)) return 1;
                return a.compareTo(b);
            }
        });
        
        for (Integer teamId : sortedTeamIds) {
            GameStats stats = allTeamStats.get(teamId);
            boolean isWinner = teamId.equals(winningTeamId);
            
            // Récupérer le nom d'affichage de l'équipe
            String displayName = getTeamDisplayName(teamId);
            
            int rectX = (int)(100 * scale) + offsetX + slideOffset;
            int rectY = startY + index * (baseRectHeight + spacing);
            
            // Fond du rectangle - doré pour le gagnant, noir pour les autres
            if (isWinner) {
                g2d.setColor(new Color(255, 215, 0, 200));  // Or semi-transparent
                g2d.fillRoundRect(rectX, rectY, rectWidth, baseRectHeight, (int)(20 * scale), (int)(20 * scale));
                // Bordure dorée plus épaisse
                g2d.setStroke(new BasicStroke((float)(3 * scale)));
                g2d.setColor(new Color(255, 215, 0));
                g2d.drawRoundRect(rectX, rectY, rectWidth, baseRectHeight, (int)(20 * scale), (int)(20 * scale));
            } else {
                g2d.setColor(new Color(0, 0, 0, 180));
                g2d.fillRoundRect(rectX, rectY, rectWidth, baseRectHeight, (int)(20 * scale), (int)(20 * scale));
            }
            
            // Nom de l'équipe
            g2d.setFont(statsFont.deriveFont(Font.BOLD, (float)(24 * scale)));
            g2d.setColor(isWinner ? new Color(139, 69, 19) : Color.WHITE);  // Brun foncé pour le gagnant, blanc pour les autres
            g2d.drawString(displayName, 
                          rectX + (int)(20 * scale), 
                          rectY + (int)(35 * scale));
            
            // Dessiner l'icône du trophée pour le gagnant
            if (isWinner && miniTrophyImage != null) {
                int trophySize = (int)(24 * scale);
                int trophyX = rectX + (int)(20 * scale) + g2d.getFontMetrics().stringWidth(displayName) + (int)(10 * scale);
                int trophyY = rectY + (int)(35 * scale) - (int)(20 * scale);
                g2d.drawImage(miniTrophyImage, trophyX, trophyY, trophySize, trophySize, null);
            }
            
            // Statistiques
            g2d.setFont(statsFont.deriveFont((float)(18 * scale)));
            g2d.setColor(isWinner ? new Color(80, 40, 10) : Color.WHITE);
            
            int statStartY = rectY + (int)(65 * scale);
            int lineHeight = (int)(30 * scale);
            
            String[] statLines = {
                "Dégâts: " + stats.getDamageDealt(),
                "Précision: " + stats.getAccuracy() + "%",
                "Eliminations: " + stats.getKills(),
                "Rounds: " + stats.getRoundsPlayed()
            };
            
            for (int i = 0; i < statLines.length; i++) {
                g2d.drawString(statLines[i], 
                              rectX + (int)(20 * scale), 
                              statStartY + i * lineHeight);
            }
            
            index++;
        }
        
        // Restaurer la transparence originale
        g2d.setComposite(originalComposite);
    }
    
    /**
     * Récupère le nom d'affichage d'une équipe par son ID.
     * @param teamId L'ID de l'équipe
     * @return Le nom d'affichage de l'équipe
     */
    private String getTeamDisplayName(int teamId) {
        if (allTeams == null) {
            return "Team " + teamId;
        }
        
        for (TeamInitialisation team : allTeams) {
            if (team.getTeamModel().getTeamID() == teamId) {
                String name = team.getName();
                // Si le nom est vide, utiliser Team X
                if (name == null || name.isEmpty()) {
                    return "Team " + teamId;
                }
                return name;
            }
        }
        return "Team " + teamId;
    }
    
    public void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }
    
    public boolean isAnimationComplete() {
        return currentPhase == AnimationPhase.STATS_DISPLAY && phaseTimer > 60;
    }
}

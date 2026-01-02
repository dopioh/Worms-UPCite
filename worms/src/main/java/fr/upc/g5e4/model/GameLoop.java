// File: fr/upc/g5e4/model/GameLoop.java
package fr.upc.g5e4.model;

//import fr.upc.g5e4.controller.*;
//import fr.upc.g5e4.model.*;
//import fr.upc.g5e4.view.*;
import fr.upc.g5e4.view.GamePanel;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.*;

/**
 * Cette classe sera seulement pour le Timer du jeu (sans ca le jeu est fixe)
 * Lorsque vous rajouterer vos parties/ class, vous devrez rjt et verifier qu'elle sont bien inclu dans GameLoop.java
 */
public class GameLoop{
    private static final float FIXHZ = 1f/60f;  //60 images par secondes
    private final Timer timer;
    private final GameManager gameManager;
    private final GamePanel gamePanel;

    // 30s à 60 FPS
    private int roundTimeRemaining;

    /** Toute les update de Timer se font dans le Constructeur de GameLoop, pas besoin de creer de methode ici*/

    // Ajout : méthode publique pour reset le timer
    public void resetRoundTimer() {
        this.roundTimeRemaining = 1800;
        gamePanel.setRoundTimer(30);
    }
    public GameLoop(GameManager gameManager){
        this.gameManager = gameManager;
        this.gamePanel = this.gameManager.getGamePanel();
        this.roundTimeRemaining = gameManager.getRoundTimeRemaining();

        // pour pas que 30 passe par dessus le temps sauvegardee dans le cas de resumeGame()
        int initialSeconds = roundTimeRemaining / 60;
        this.gamePanel.setRoundTimer(initialSeconds);

        // Initialiser l'affichage du chrono dans la vue -> Ca sert a rien ? 
        //this.gamePanel.setRoundTimer(30);

        //on multiplie FIXHZ 1000 psq Timer fonctionne en ms
        this.timer = new Timer((int)(FIXHZ*1000),new ActionListener() {         
            @Override
            public void actionPerformed(ActionEvent e){
                // met a jour la caméra
                if (gameManager.getCameraController() != null) {
                    gameManager.getCameraController().update();
                }
                
                // met a jour les projectiles
                if (gameManager.getItemController() != null) {
                    gameManager.getItemController().updateProjectiles(FIXHZ);
                }
                
                // met a jour l'animation du vent
                if (gameManager.getWindOverlay() != null) {
                    gameManager.getWindOverlay().update(FIXHZ);
                }
                
                // met a jour le worm actif
                if (gameManager.getActiveWormController() != null) {
                    gameManager.getActiveWormController().update();
                }
                
                // stopper le mouvement horizontal des worms non-actifs pour qu'ils ne glissent pas
                int activeWormIndex = gameManager.getActiveWormIndex();
                TeamModel activeTeam = gameManager.getCurrentTeam();
                
                ArrayList<TeamModel> allTeams = gameManager.getlistTeams();
                for (TeamModel team : allTeams) {
                    for (int i = 0; i < team.getWormNumberAtStart(); i++) {
                        CharacterModel worm = team.getWorm(i);
                        // stopper le mouvement horizontal si ce n'est pas le worm actif
                        if (worm != null && !worm.isDead() && !(activeTeam == team && i == activeWormIndex)) {
                            worm.stopHorizontalMovement();
                        }
                    }
                }
                
                gameManager.getWorld().step(FIXHZ,8,3);

                /* -> Pas une javadoc puisque javadoc peut pas documenter des variables locale 
                 * @see WaterSensorListener.java
                 * Recupere la liste des Worms qui sont mort (pour le moment seulement dans l'eau)
                 * Et detruit leur body associés (on ne peut plus les controller)
                 * Leur draw et nametag ne depop pas, ca sera a ajouter 
                 */
                ArrayList<CharacterModel> destroy = gameManager.getWSL().getDestroyList();
                if ( destroy != null){
                    for (CharacterModel d : destroy){
                        gameManager.getWorld().destroyBody(d.getBody());
                    }
                    destroy.clear();
                }

                // si le worm actif est mort -> passer immédiatement au round suivant
                try {
                    TeamModel currentTeam = gameManager.getCurrentTeam();
                    int activeIndex = gameManager.getActiveWormIndex();
                    if (activeIndex >= 0 && activeIndex < currentTeam.getWormNumberAtStart()) {
                        CharacterModel activeWorm = currentTeam.getWorm(activeIndex);
                        if (activeWorm == null || activeWorm.isDead()) {
                            gameManager.nextRound();
                            roundTimeRemaining = 1800;
                            gamePanel.setRoundTimer(30);
                        }
                    }
                } catch (Exception ex) {
                    // ignore changements de transition possibles
                }

                // décrémente le temps du round, met à jour la vue et change si nécessaire
                roundTimeRemaining--; 
                gameManager.setRoundTimeSave(roundTimeRemaining);
                if (roundTimeRemaining % 60 == 0) {
                    int secondsLeft = roundTimeRemaining / 60; 
                    gamePanel.setRoundTimer(Math.max(0, secondsLeft));
                }
                if (roundTimeRemaining <= 0) {
                    gameManager.nextRound();        
                    roundTimeRemaining = 1800;      // reset
                    gamePanel.setRoundTimer(30);    // reset affichage
                }
                
                // Vérifier si une équipe a gagné
                String winner = gameManager.checkGameOver();
                if (winner != null) {
                    timer.stop();  // Arrêter le jeu
                    System.out.println("Fin de partie! " + winner + " a gagné!");
                    // Le GameLauncher déclenchera l'écran de fin
                }
                
                gamePanel.repaint();
            }
        });
    }

    public void timerStart(){
        this.timer.start();
    }

    public void timerStop(){
        this.timer.stop();
    }
    
    public boolean isGameOver() {
        return gameManager.checkGameOver() != null;
    }
    
    public String getWinner() {
        return gameManager.checkGameOver();
    }
}

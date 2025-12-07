package fr.upc.g5e4.view.console;

import fr.upc.g5e4.model.*;
import fr.upc.g5e4.model.console.PixelConverterCase;
import fr.upc.g5e4.view.GamePanel;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

import org.jbox2d.dynamics.Body;


import org.jbox2d.common.*;

/**
 * Classe pour dessiner les graphismes de la partie Jeu dans la vue Terminal
 * @see GamePanel pour la version avec Swing
 * Swing -> Lanterna
 */
public class ConsoleGamePanel {
    public static final int echelleConsole = 10;
    private GroundModel groundModel;

    /**
     * Pour creer une instance
     * @param groundModel -> se base sur ca pour creer le terrain
     */
    public ConsoleGamePanel(GroundModel groundModel){
        this.groundModel = groundModel;
    }

    public void drawGround(TextGraphics tx,int XCase, int YCase){
        int[] gmYGroundPixel = groundModel.getGroundY();
        int[] gmYGround = new int[gmYGroundPixel.length];

        for (int i = 0 ; i < gmYGroundPixel.length ; i++){
            gmYGround[i] = PixelConverterCase.transformCoordY(gmYGroundPixel[i]);
        }

        int step = XCase / (gmYGround.length -1);

        tx.setForegroundColor(TextColor.ANSI.GREEN);

        for (int i = 0; i < gmYGround.length -1; i++){
            int x1 = (int) (i * step);
            int x2;

            if (i == gmYGround.length - 2) {
                x2 = XCase - 1;
            } else {
                x2 = (int) ((i + 1) * step);
            }

            int y1 = gmYGround[i];
            int y2 = gmYGround[i+1];

            for (int x = x1; x <= x2 && x < XCase; x++ ){
                int y;
                if (x2 != x1){
                    int deltaY = y2 - y1;          // difference de hauteur entre les 2 points
                    int deltaX = x2 - x1;          // difference de position X entre les 2 points
                    int progression = x - x1;      // ou on en est entre x1 et x2
    
                    y = y1 + (deltaY * progression / deltaX);
                } else {
                    y = y1;
                }
                for (int fillY = y; fillY < YCase; fillY++) {
                    tx.setCharacter(x, fillY, '#');
                }
            }
        }
    }

    /**
     * Methode pour dessiner tous les worms des deux equipes
     * Seulement deux equipes car terminal = version prototype
     * @param tx -> interface pour dessiner
     * @param t1 -> Team1
     * @param t2 -> Team2
     */
    public void drawAllCharacter(TextGraphics tx, TeamModel t1, TeamModel t2){
        TextColor tc1 = TextColor.ANSI.RED;
        TextColor tc2 = TextColor.ANSI.BLUE;
        
        // dessine la premiere equipe
        for (int i = 0 ; i < t1.getTabWorms().length ; i++){    
            drawCharacter(tx, t1.getTabWorms()[i], tc1);
        }

        // dessine la seconde equipe
        for (int i = 0 ; i < t2.getTabWorms().length ; i++){
            drawCharacter(tx, t2.getTabWorms()[i], tc2);
        }
    }
    
    /**
     * Methode prv qui dessine un CharacterModel
     * @param tx -> interface pour dessiner
     * @param characterModel -> le CharacterModel choisi pour etre dessiner
     * @param tc -> la couleur (selon l'equipe)
     */
    private void drawCharacter(TextGraphics tx, CharacterModel characterModel, TextColor tc){
        //System.out.println("drawCharacter est called");       
        if (characterModel.isDead()){
            return;
        }

        tx.setForegroundColor(tc);

        // Pareil que swing, on se base sur son body (Jbox2D)
        Body body = characterModel.getBody();
        Vec2 m_position = body.getPosition();

        int heightMeter = (int)(groundModel.getHeight()/ CharacterModel.getPPM());

        int posCaseX = PixelConverterCase.transformCoordX((int)(m_position.x * CharacterModel.getPPM()));
        int posCaseY = PixelConverterCase.transformCoordY((int)((heightMeter - m_position.y) * CharacterModel.getPPM()));  // Conversion et Inversion Y

        // hauteur et largeur du perso
        int case_WidthPerso = PixelConverterCase.transformCoordX((int)((characterModel.getMWidthPerso()) * CharacterModel.getPPM()));
        int case_HeightPerso =PixelConverterCase.transformCoordY((int)(20 - characterModel.getMHeightPerso()*CharacterModel.getPPM()));

        // verif que la taille n'est jamais a 0
        int width = Math.max(1, case_WidthPerso);
        int height = Math.max(1, case_HeightPerso);


        for (int i = posCaseX - width/2; i <= posCaseX + width/2; i++) { 
            for (int j = posCaseY - height/2; j <= posCaseY + height/2; j++) {
         // verif pour ne pas dessiner hors écran
                if (i >= 0 && i < ConsoleView.getXCase() && j >= 0 && j < ConsoleView.getYCase()) {
                    tx.setCharacter(i, j, '+');
                }
            }
        }
    }
}

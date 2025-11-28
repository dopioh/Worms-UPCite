package fr.upc.g5e4.view;

import fr.upc.g5e4.model.*;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

/**
 * Classe pour initialiser les graphismes de la partie Jeu dans la vue Terminal
 * @see GamePanel pour la version avec Swing
 * Swing -> Lanterna
 */
public class ConsolePanel {
    public static final int echelleConsole = 10;
    private GroundModel groundModel;
    private CharacterModel characterModel;
    private int XCase;
    private int YCase;

    public ConsolePanel(GroundModel groundModel,CharacterModel characterModel, int XCase, int YCase){
        this.groundModel = groundModel;
        this.characterModel = characterModel;
        this.XCase = XCase;
        this.YCase = YCase;
    }

    public void drawGround(TextGraphics tx){    // equivalent dans GamePanel.java
        int[] gmYGround = groundModel.getGroundY();

        int step = XCase / (gmYGround.length -1 );

        tx.setForegroundColor(TextColor.ANSI.GREEN);

        for (int i = 0; i < gmYGround.length -1; i++){
            int x1 = (int) (i * step);
            int x2;

            if (i == gmYGround. length - 2) {
                x2 = XCase - 1;
            } else {
                x2 = (int) ((i + 1) * step);
            }

            int y1 = gmYGround[i];  // pour des soucis de conversion, il faudrait convertir ici et plus dans ConsoleGameManager
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

    /*
    -> Mauvaise Conversion de pixel en case 
    -> ET la physique que j'ai config attend des pixel
    -> Donc faudrait pas convertir en case dans ConsoleGameManager.java mais plutot ici apres avoir appliquer la physique
    
    public void drawCharacter(TextGraphics tx){       
        if (characterModel.isDead()){
            return;
        }

        Body body = characterModel.getBody();
        Vec2 m_position = body.getPosition();   // le centre du body non ?

        int posCaseX = (int)(m_position.x * CharacterModel.getPPM())/ConsoleGameManager.echelleConsole;
        int posCaseY = (int)((20 - m_position.y) * CharacterModel.getPPM()/ConsoleGameManager.echelleConsole);  // Conversion et Inversion Y

        // hauteur et largeur du perso
        int case_WidthPerso = (int)((characterModel.getMWidthPerso()) * CharacterModel.getPPM()/ConsoleGameManager.echelleConsole);
        int case_HeightPerso =(int)(20 - characterModel.getMHeightPerso()*CharacterModel.getPPM())/ConsoleGameManager.echelleConsole;
      
        tx.setForegroundColor(TextColor.ANSI.GREEN);



        for (int i = posCaseX - case_WidthPerso/2 ; i < posCaseX + case_WidthPerso/2 ; i++){
            for (int j = posCaseY - case_HeightPerso/2 ; j < posCaseY + case_HeightPerso/2 ; j++){
                tx.setCharacter(i,j, '@');
            }
        }
    }*/
}

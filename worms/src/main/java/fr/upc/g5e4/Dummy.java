package fr.upc.g5e4;

import java.awt.*;

import org.jbox2d.dynamics.*;

public class Dummy {
    /**
    Dummy (en francais : "factice") est une classe d'essai pour tester la gravite de la classe Terrain, en attandant qu'un vrai classe Personnage soit implementer
    La physique du Dummy sera dans `PhysicsWorld`
    */

    private final int radiusPx;
    private final Body bdDummy;

    public Dummy(Body bDummy,int radiusPx){
        this.bdDummy = bDummy;
        this.radiusPx = radiusPx;

    }

    public Body getBody(){
        return bdDummy;
    }

    public int getRadiusPx(){
        return radiusPx;
    }

    public float getRadiusM(){
        return radiusPx/GamePanel.getPPM();
    }

    public void draw(Graphics g, int panelHeightPx, float ppm){
        Graphics2D g2 = (Graphics2D) g;

        float worldX  = bdDummy.getPosition().x;
        float worldY  = bdDummy.getPosition().y;
       //System.out.println("bd.Dummy.y : " + worldY);

        int cxPx = Math.round(worldX * ppm);
        int cyPx = Math.round(panelHeightPx - (worldY * ppm));

        int diametre = radiusPx * 2;
        //System.out.println("pixel rayon"+diametre);
        g2.setColor(new Color(255, 0, 0));      // rouge
        g2.fillOval(cxPx - radiusPx, cyPx - radiusPx, diametre, diametre);
    }
    
}

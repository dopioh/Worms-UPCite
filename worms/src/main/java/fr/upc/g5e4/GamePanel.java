package fr.upc.g5e4;

//import java.util.EventListener;

import javax.swing.*;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.dynamics.*;

import fr.upc.g5e4.PhysicsWorld.WaterSensorListener;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.geom.Path2D;
import java.util.ArrayList;

public class GamePanel extends JPanel {
    private final GroundModel model;
    private final Dummy dummy;
    private final PhysicsWorld physic;
    private final World world;
    private final Timer timer;
    private final Body water; 

    private static final float FIXHZ = 1f/60f;  //60 images par secondes
    private static final float PPM = 30f;

    public GamePanel(GroundModel model) {
        this.model = model;

        this.physic = new PhysicsWorld();
        this.world = physic.getWorld();
        physic.createGroundFromModel(model);
        this.water = physic.createBodyWater(model);

        Body bDummy = createBodyDummy(model.getWidth()-700, model.getHeight()/6, 10); 
        this.dummy = new Dummy(bDummy, 10);

        physic.limitPanel(model);

        WaterSensorListener wSL = physic.new WaterSensorListener(water,dummy, new ArrayList<Dummy>());
        world.setContactListener(wSL);
        ArrayList<Dummy> destroy = wSL.getDestroyList();

        
        // rjt step pour lancer le timer (une sorte d'update)
        setPreferredSize(new Dimension(model.getWidth(), model.getHeight()));
        this.timer = new Timer((int)(FIXHZ*1000),new ActionListener() {         // on multiplie par 1000 psq Timer fonctionne en ms 
            @Override
            public void actionPerformed(ActionEvent e){
                world.step(FIXHZ,8,3);
                repaint();
                if (destroy != null){
                    for (Dummy d : destroy){
                        world.destroyBody(d.getBody());
                    }
                    destroy.clear();
                }
            }
        });
        timer.start();
    }

    public static float getPPM(){
        return PPM;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Ciel
        g2.setColor(new Color(135, 206, 235));
        g2.fillRect(0, 0, model.getWidth(), model.getHeight());
        
        // Sol
        g2.setColor(new Color(34, 139, 34));
        Path2D path = new Path2D.Double();
        path.moveTo(0, model.getHeight());
        double step = (double) model.getWidth() / (model.getGroundY().length - 1);

        for (int i = 0; i < model.getGroundY().length; i++) {
            path.lineTo(i * step, model.getGroundY()[i]);
        }
        path.lineTo(model.getWidth(), model.getHeight());
        path.closePath();
        g2.fill(path);

        dummy.draw(g2, model.getHeight(), PPM);

        // Eau
        g2.setColor(new Color(75, 147, 240));
        g2.fillRect(0, model.getWaterLevel(), model.getWidth(), model.getHeight() - model.getWaterLevel());


    }

    private Body createBodyDummy(float centerXPx, float centerYPx, float radiusPx){

        float xWorld = centerXPx / PPM;
        float yWorld = (model.getHeight() - centerYPx) / PPM;

        BodyDef bdDummy = new BodyDef();
        bdDummy.type = BodyType.DYNAMIC;
        bdDummy.position.set(xWorld, yWorld);

        FixtureDef fixtureDummy = new FixtureDef();
        CircleShape cs = new CircleShape();
        cs.m_radius = radiusPx / PPM; // rayon en mètres
        fixtureDummy.shape = cs;
        fixtureDummy.restitution = 0.1f;
        fixtureDummy.density = 1.0f;

        Body bDummy = world.createBody(bdDummy);
        bDummy.createFixture(fixtureDummy);

        return bDummy;
    }
}

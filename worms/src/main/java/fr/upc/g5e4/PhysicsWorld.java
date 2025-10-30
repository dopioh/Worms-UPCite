package fr.upc.g5e4;

import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

//import java.awt.geom.RectangularShape;
import java.util.ArrayList;

import org.jbox2d.callbacks.*;
import org.jbox2d.dynamics.contacts.Contact;

public class PhysicsWorld {
    /*
     * On va definir dans cette classe les Boddies du sol et de l'eau (collision, effet lors de la collision etc)
     * On definira egalement un Dummy pour tester que le perso ne traverse pas le sol et depop si il est dans l'eau depuis quelques secondes
     * Pour ca on rajoutera dans la classe Dummy un controller simple qui peut aller a droite et a gauche et qui peut sauter 
     * Separer la classe en deux (implicitement), une partie qui gere la physique du Worm, et l'autre qui gere la physique des projectiles 
     * Chaque projectile est different selon l'arme : Creer un constructeur modulable qui detaille les specificiter de chaque arme et qui vont changer l'impact de la physique des projectiles
     *      -> A definir plus tard quand on implementera serieusement les armes, mtn on se limite que au poids et a la vitesse (dans une autre classe ?)
     */
    private static final float PPM = 30f;
    private final World world;



    public PhysicsWorld(){
        Vec2 gravity = new Vec2(0f, -9.8f);     // pour definir la force de la gravite en m/s^2
        this.world = new World(gravity);
        
        world.setAllowSleep(true);           // optimisation des boddies dynamic non actifs
    }

    public World getWorld(){
        return this.world;
    }

    /*
     * Methode pour creer un body STATIC (pour le moment, on changera surement en Kinematic lorsque on integrera les degradations de terrains)
     * Il faut recup les points de hauteur de groundY, et retracer un "capteur/body" dessus qui sera impacter par la physique kinetic
     * JBox fonctionne avec des metres alors que swing et awt fonctionne avec des pixels donc on convertie 
     * (aussi psq on doit garder des faibles unites de mesures - entre 0.1 et 10m - pour que JBox fonctionne bien)
     * Y est inverser entre Jbox et Swing/Awt donc faudra faire un reverse
     * Mais comment faire ca ?
     */

    public void createGroundFromModel(GroundModel model){ 
        int[] groundY = model.getGroundY();
        int stepPx = model.getWidth()/(groundY.length-1);                      // on recupere l'espace entre chaque valeur de groundY (on peut mtn determiner leur positions)                      
        float[] m_groundY = new float[groundY.length];

        Vec2[] pos = new Vec2[m_groundY.length];                                  // on a transformer les positions des sommets du sol en vecteur 2D
        for (int i = 0; i < groundY.length; i++){
            float xPx = stepPx*i;
            //System.out.println("xPx : " + xPx);
            float yPx = groundY[i];
            float worldX = (float) (xPx / PPM);
            float worldY = (float) ((model.getHeight() - yPx) / PPM); // inversion Y, sans -1
            pos[i] = new Vec2(worldX, worldY);
            //System.out.println("position X i ground : " + pos[i].x);
            //System.out.println("position Y i ground : " + pos[i].y);
        }

        ChainShape chain = new ChainShape();                                        // on relie les points avec une chaine (on utilisera pas ca pour l'eau puisque ce type de chaine peut pas etre rempli)
        chain.createChain(pos, pos.length);                                      // "vortex" c'est sommet aussi puree (a retenir)

        BodyDef bdGround = new BodyDef();
        bdGround.type = BodyType.STATIC;

        FixtureDef fixtureGround = new FixtureDef();
        fixtureGround.shape = chain;
        fixtureGround.friction = 0.5f;                                          // valeur de reference pour tester, on modifiera potientiellement plus tard
        fixtureGround.restitution = 0;

        Body bGround = world.createBody(bdGround);                              // on donne une valeur physique a notre BodyDef
        bGround.createFixture(fixtureGround);                                   // on la relie avec notre Chain/ficture
   
    }


    public Body createBodyWater(GroundModel model){
        int waterLevelPx = model.getWaterLevel();   // 500 en swing
        float m_waterLevel = (model.getHeight() - waterLevelPx)/ GamePanel.getPPM();

        BodyDef bdWater = new BodyDef();
        bdWater.type = BodyType.STATIC;
        // rjt position du centre
        bdWater.position.set(model.getHeight()/PPM, m_waterLevel);

        PolygonShape polysWater = new PolygonShape();
        polysWater.setAsBox(model.getWidth()/2, m_waterLevel/2);

        FixtureDef fdWater = new FixtureDef();
        fdWater.shape = polysWater;
        fdWater.isSensor = true;    // Le Body est traversable mais il emet un signal quand y'a contact (comme un body normal)
        
        Body bWater = world.createBody(bdWater);
        bWater.createFixture(fdWater);
        return bWater;
    }


    public void limitPanel(GroundModel model){    // je mets tout ensemble (wall et ceiling) puisque on les retravaillera pas ailleurs individuellement (pas de couleur ou d'autre appel je ne sais ou)
        
        BodyDef leftWall = new BodyDef();        // mur gauche
        leftWall.type = BodyType.STATIC;
        leftWall.position.set(0, 0);   // en bas a droite (puisque JBox2D)

        EdgeShape eShapeLeftW = new EdgeShape();                   
        eShapeLeftW.set(new Vec2(0, 0), new Vec2(0, model.getHeight()/PPM));    // model.getHeight() est en pixel

        FixtureDef fDefLeftW = new FixtureDef();
        fDefLeftW.shape = eShapeLeftW;
        fDefLeftW.restitution = 0.5f;

        Body bLeftWall = world.createBody(leftWall);
        bLeftWall.createFixture(fDefLeftW);



        BodyDef rightWall = new BodyDef();        // mur droit  
        rightWall.type = BodyType.STATIC;
        rightWall.position.set(model.getWidth() /PPM, 0);   // en bas a gauche (puisque JBox2D), pas besoin d'inverse psq on veut tout y, y - y = 0

        EdgeShape eShapeRightW = new EdgeShape();                   
        eShapeRightW.set(new Vec2(0, 0), new Vec2(0, model.getHeight()/30));
        //System.out.println(new Vec2(model.getWidth(), 0));
        //System.out.println(new Vec2(model.getWidth(), model.getHeight()));

        FixtureDef fDefRightW = new FixtureDef();
        fDefRightW.shape = eShapeRightW;
        fDefRightW.restitution = 0.5f;

        Body bRightWall = world.createBody(rightWall);     
        bRightWall.createFixture(fDefRightW);               
    }


    public class WaterSensorListener implements ContactListener{
        private ArrayList<Dummy> destroy;
        private final Body water;                     // capteur pour savoir si un body touche l'eau (defini avec setControlListener dans une classe interne plus tard)
        private final Dummy dummy;

        public WaterSensorListener(Body water, Dummy dummy, ArrayList<Dummy> destroy){
            this.water = water;
            this.dummy = dummy;
            this.destroy = destroy;
        }

        public ArrayList<Dummy> getDestroyList(){
            return destroy;
        }

        @Override
        public void beginContact(Contact contact){          // Contact est une classe abtraite qui "instancier" automatiquement des que deux body (ou seulement leru fixtures ?) se touche
            Fixture a = contact.getFixtureA();              
            Fixture b = contact.getFixtureB();

            if (fullInWater(a, b)){
                destroy.add(this.dummy);
                System.out.println("DEPOP");
            }
        }

        @Override
        public void endContact(Contact contact){
            
        }

        public boolean fullInWater(Fixture a, Fixture b){
            float waterY = water.getPosition().y;
            //System.out.println("waterY : " + waterY);
            float dummyY = dummy.getBody().getPosition().y;
            //System.out.println("dummyY : " + dummyY);
            float radius = dummy.getRadiusM();
            //System.out.println("radius : " + radius);
            if (dummyY + radius > waterY){
                return false;
            }
            return true;
        }

        @Override
        public void preSolve(Contact contact, Manifold oldManifold){};              // on n'en a pas besoin pour le moment
        @Override
        public void postSolve(Contact contact, ContactImpulse impulse){};

    }
}

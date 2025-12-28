package fr.upc.g5e4.model;

import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.callbacks.*;
import org.jbox2d.dynamics.contacts.Contact;

import fr.upc.g5e4.view.GamePanel;

    /**
     * <p>
     * Classe qui gerera la physique du terrain (sol et eau pour le moment) seulement. 
     * Les Body des Entity ne sont pas a definir ici
     * Mais dans leur propre classe
     * <p>
     * 
     * @see Entity#createBody
     */
public class PhysicsWorld {

    /**
     * <p>
     * {@code PPM} = pixel par mettre
     * Swing/AWT fonctionne avec des pixels
     * JBox2D fonctionne avec des metres 
     * Donc il faut convertir (grace a {@code PPM} et a des formules) pour passer de l'un a l'autre
     * 
     * Formules : 
     * m_worldX = screenHeight_px /PPM 
     * m_worldY = screenHeight_px - screenY_px /PPM 
     * screenX_px = m_worldX * PPM
     * screenY_px = screenHeight - (m_worldY * PPM)
     * 
     * L'axe Y est inversé entre Swing/AWT et JBox2D
     * (0x,0y) = en haut a gauche dans Swing
     * (0x,0y) = en bas a gauche dans Jbox2D (comme en maths)
     * 
     * Les formules au dessus convertissent deja Y
     * <p>
     */
    private static final float PPM = 30f;

    /**
     * {@code world} est "l'initialisitation" du monde d'une partie
     * Il defini la gravité du monde
     * Chaque Body que vous ferez devra etre ajouté à monde
     */
    private final World world;
    
    /**
     * Body du terrain créé à partir du BlockGridModel
     * On le garde en référence pour pouvoir le détruire et le recréer
     */
    private Body blockGridBody;

    

    public PhysicsWorld(){
        Vec2 gravity = new Vec2(0f, -9.8f);     // pour definir la force de la gravite en m/s^2
        this.world = new World(gravity);
        
        world.setAllowSleep(true);           // optimisation des boddies dynamic non actifs
    }

    public World getWorld(){
        return this.world;
    }

    /**
     * Methode pour creer un body STATIC du Sol
     * On changera surement en KINEMATIC lorsque on integrera les degradations de terrains
     */
    public void createGroundFromModel(GroundModel model){ 
        int[] groundY = model.getGroundY();
        int stepPx = model.getWidth()/(groundY.length-1);                                      
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

        ChainShape chain = new ChainShape();                                        // on relie les points avec une chaine (on utilisera pas ca pour l'eau puisque ce type de chaine peut pas etre rempli
        chain.createChain(pos, pos.length);                                      

        BodyDef bdGround = new BodyDef();
        bdGround.type = BodyType.STATIC;

        FixtureDef fixtureGround = new FixtureDef();
        fixtureGround.shape = chain;
        fixtureGround.friction = 0.5f;                                          // valeur de reference pour tester, on modifiera potientiellement plus tard
        fixtureGround.restitution = 0;

        Body bGround = world.createBody(bdGround);                              // on donne une valeur physique a notre BodyDef
        bGround.createFixture(fixtureGround);                                   // on la relie avec notre Chain/ficture
   
    }

    /**
     * Crée le sol à partir d'une grille de blocs (une grille de blocs = un tableau 2D)
     * Génère un contour qui suit la surface des blocs solides
     * @param blockGrid Le modèle de grille de blocs
     * @return Le Body du sol créé
     */
    public Body createGroundFromBlockGrid(BlockGridModel blockGrid) {
        int blockSize = blockGrid.getBlockSize();
        int gridWidth = blockGrid.getGridWidth();
        int gridHeight = blockGrid.getGridHeight();
        int pixelHeight = blockGrid.getPixelHeight();
        
        // création d'une liste de points pour le contour de la surface des blocs
        List<Vec2> contourPoints = new ArrayList<>();
        
        // point de départ en bas à gauche
        contourPoints.add(new Vec2(0, 0));
        
        // on parcourt de gauche à droite pour trouver les points de surface
        for (int gridX = 0; gridX < gridWidth; gridX++) {
            int surfaceGridY = -1;
            for (int gridY = 0; gridY < gridHeight; gridY++) {
                if (blockGrid.getBlock(gridX, gridY).isSolid()) {
                    surfaceGridY = gridY;
                    break;
                }
            }
            
            if (surfaceGridY >= 0) { // si on a trouvé un point de surface
                // on calcule la position en pixels puis en mètres
                float xPx = gridX * blockSize;
                float yPx = surfaceGridY * blockSize;

                // conversion en taille pour le monde physique
                float worldX = xPx / PPM;
                float worldY = (pixelHeight - yPx) / PPM;  // inversion Y
                
                // on ajoute le coin gauche du bloc de surface
                contourPoints.add(new Vec2(worldX, worldY));
                
                // on ajoute le coin droit du bloc de surface
                worldX = (gridX + 1) * blockSize / PPM;
                contourPoints.add(new Vec2(worldX, worldY));
            }
        }
        
        // le point final en bas à droite
        contourPoints.add(new Vec2(blockGrid.getPixelWidth() / PPM, 0));
        
        // si pas assez de points alors on crée un sol plat
        if (contourPoints.size() < 4) {
            contourPoints.clear();
            contourPoints.add(new Vec2(0, 0));
            contourPoints.add(new Vec2(0, pixelHeight / 2f / PPM));
            contourPoints.add(new Vec2(blockGrid.getPixelWidth() / PPM, pixelHeight / 2f / PPM));
            contourPoints.add(new Vec2(blockGrid.getPixelWidth() / PPM, 0));
        }
        
        // simplification du contour
        List<Vec2> simplifiedContour = simplifyContour(contourPoints);
        
        // création du chainshape
        Vec2[] positions = simplifiedContour.toArray(new Vec2[0]);
        
        ChainShape chain = new ChainShape();
        chain.createChain(positions, positions.length);
        
        BodyDef bdGround = new BodyDef();
        bdGround.type = BodyType.STATIC;
        
        FixtureDef fixtureGround = new FixtureDef();
        fixtureGround.shape = chain;
        fixtureGround.friction = 0.0f;
        fixtureGround.restitution = 0;
        
        // definition de la catégorie de collision pour le terrain
        fixtureGround.filter.categoryBits = CharacterModel.CATEGORY_TERRAIN;
        
        Body bodyGround = world.createBody(bdGround);
        bodyGround.createFixture(fixtureGround);
        
        // sauvegarder la référence pour pouvoir la recréer plus tard
        this.blockGridBody = bodyGround;
        
        blockGrid.clearDirty();
        
        return bodyGround;
    }
    
    /**
     * recréer le terrain à partir du BlockGridModel (quand les blocs ont changé)
     * détruit l'ancien body et en crée un nouveau avec la nouvelle configuration
     * @param blockGrid le modèle de grille à utiliser
     */
    public void recreateTerrainFromBlockGrid(BlockGridModel blockGrid) {
        // détruire l'ancien body s'il existe
        if (blockGridBody != null) {
            world.destroyBody(blockGridBody);
            blockGridBody = null;
        }
        
        // recréer le terrain avec la nouvelle configuration
        createGroundFromBlockGrid(blockGrid);
    }
    
    /**
     * Simplification d'un contour en supprimant les points intermédiaires alignés
     * @param points La liste de points à simplifier
     * @return La liste de points simplifiée
     */
    private List<Vec2> simplifyContour(List<Vec2> points) {
        if (points.size() <= 3) {
            return points;
        }
        
        List<Vec2> result = new ArrayList<>();
        result.add(points.get(0));
        
        for (int i = 1; i < points.size() - 1; i++) {
            // on stock le point precedent, le point courant et le point suivant
            Vec2 prev = points.get(i - 1);
            Vec2 curr = points.get(i);
            Vec2 next = points.get(i + 1);
            
            // on verifie que les trois points ne sont pas alignés horizontalement (en y)
            if (Math.abs(prev.y - curr.y) > 0.001f || Math.abs(curr.y - next.y) > 0.001f) {
                result.add(curr);
            }
        }
        
        result.add(points.get(points.size() - 1));
        return result;
    }
    
    /**
     * Crée les limites du terrain pour une grille de blocs
     */
    public void limitPanel(BlockGridModel blockGrid) {
        BodyDef leftWall = new BodyDef();
        leftWall.type = BodyType.STATIC;
        leftWall.position.set(0, 0);

        EdgeShape eShapeLeftW = new EdgeShape();
        eShapeLeftW.set(new Vec2(0, 0), new Vec2(0, blockGrid.getPixelHeight() / PPM));

        FixtureDef fDefLeftW = new FixtureDef();
        fDefLeftW.shape = eShapeLeftW;
        fDefLeftW.restitution = 0.3f;

        Body bLeftWall = world.createBody(leftWall);
        bLeftWall.createFixture(fDefLeftW);

        BodyDef rightWall = new BodyDef();
        rightWall.type = BodyType.STATIC;
        rightWall.position.set(blockGrid.getPixelWidth() / PPM, 0);

        EdgeShape eShapeRightW = new EdgeShape();
        eShapeRightW.set(new Vec2(0, 0), new Vec2(0, blockGrid.getPixelHeight() / PPM));

        FixtureDef fDefRightW = new FixtureDef();
        fDefRightW.shape = eShapeRightW;
        fDefRightW.restitution = 0.3f;

        Body bRightWall = world.createBody(rightWall);
        bRightWall.createFixture(fDefRightW);
    }


    /**
     * Methode pour initialiser le Body de l'eau 
     * Body traversable mais qui emet un signal
     */
    public Body createBodyWater(GroundModel model){
        int waterLevelPx = model.getWaterLevel();   
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


    /**
     * Creer les limites de terrain que ni le joueur ni la cam peut depassé
     * Seulement murs droit et gauche pour le moment
     */
    public void limitPanel(GroundModel model){ 
        
        BodyDef leftWall = new BodyDef();        // mur gauche
        leftWall.type = BodyType.STATIC;
        leftWall.position.set(0, 0);   // en bas a droite (puisque JBox2D)

        EdgeShape eShapeLeftW = new EdgeShape();                   
        eShapeLeftW.set(new Vec2(0, 0), new Vec2(0, model.getHeight()/PPM));    // model.getHeight() est en pixel

        FixtureDef fDefLeftW = new FixtureDef();
        fDefLeftW.shape = eShapeLeftW;
        fDefLeftW.restitution = 0.3f;

        Body bLeftWall = world.createBody(leftWall);
        bLeftWall.createFixture(fDefLeftW);



        BodyDef rightWall = new BodyDef();        // mur droit  
        rightWall.type = BodyType.STATIC;
        rightWall.position.set(model.getWidth() /PPM, 0);   // en bas a gauche (puisque JBox2D), on inverse pas psq on veut tout y, y - y = 0

        EdgeShape eShapeRightW = new EdgeShape();                   
        eShapeRightW.set(new Vec2(0, 0), new Vec2(0, model.getHeight()/30));
        //System.out.println(new Vec2(model.getWidth(), 0));
        //System.out.println(new Vec2(model.getWidth(), model.getHeight()));

        FixtureDef fDefRightW = new FixtureDef();
        fDefRightW.shape = eShapeRightW;
        fDefRightW.restitution = 0.3f;

        Body bRightWall = world.createBody(rightWall);     
        bRightWall.createFixture(fDefRightW);               
    }


    /**
     * Classe Interne pour creer la reaction a la collision
     * On creera surement une autre classe externe comme ca mais globale
     * Qui focntionnera pas seulement que pour le contact entre l'eau et le Worms
     * 
     * @param water : body deja defini qui designe l'eau 
     * @param allWorms : liste de tous les worms du jeu pour vérifier qui tombe dans l'eau
     * @param destroy : liste vide où on rajoutera les Worms qui sont completement dans l'eau
     * 
     * @see CharacterModel#createBody
     */
    public class WaterSensorListener implements ContactListener{
        private ArrayList<CharacterModel> destroy;
        private final Body water;                     
        private final ArrayList<CharacterModel> allWorms;

        public WaterSensorListener(Body water, ArrayList<CharacterModel> allWorms, ArrayList<CharacterModel> destroy){
            this.water = water;
            this.allWorms = allWorms;
            this.destroy = destroy;
        }

        /**
         * @return une liste qui contient tous les Worms dans l'eau
         * Permettra de supprimer leur Body
         * 
         * @see GameManager
         */
        public ArrayList<CharacterModel> getDestroyList(){
            return destroy;
        }

        @Override
        public void beginContact(Contact contact){          // contact est une classe abtraite qui "s'instancie" automatiquement des que deux body (ou seulement leur fixtures ?) se touche
            Fixture a = contact.getFixtureA();              
            Fixture b = contact.getFixtureB();

            // il y'a un body water et un body worm
            Body bodyA = a.getBody();
            Body bodyB = b.getBody();
            
            // Identifier le worm qui touche l'eau
            for (CharacterModel worm : allWorms) {
                if (worm == null || worm.isDead()) continue;
                
                Body wormBody = worm.getBody();
                if (wormBody == bodyA || wormBody == bodyB) {
                    if (fullInWater(worm)) {
                        worm.setDead(true);
                        destroy.add(worm);
                        System.out.println("DEPOP: " + worm.getName() + " est mort dans l'eau!");
                    }
                    break;
                }
            }
        }

        @Override
        public void endContact(Contact contact){
            
        }
        /**
         * Toutes les variables de fullInWater sont en metres
         * Car Jbox2D
         * @param worm Le worm à vérifier
         * @return true si le worm est complètement immergé dans l'eau
        */
        public boolean fullInWater(CharacterModel worm){
            float waterY = water.getPosition().y;
            //System.out.println("waterY : " + waterY);
            float wormY = worm.getBody().getPosition().y;
            //System.out.println("wormY : " + wormY);
            float demiHauteurY = worm.getMWidthPerso() / 2; // On veut que la moitié
            //System.out.println("radius : " + radius);
            if (wormY + demiHauteurY > waterY){
                return false;
            }
            return true;
        }

        /**
         * Pas besoin de definir ca, ca en sert a rien pour le moment
         * On est obliger de les {@code @Override} car classe abstraire 
         */
        @Override
        public void preSolve(Contact contact, Manifold oldManifold){};              // on n'en a pas besoin pour le moment
        @Override
        public void postSolve(Contact contact, ContactImpulse impulse){};

    }
}

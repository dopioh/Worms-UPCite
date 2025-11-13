package fr.upc.g5e4;

import static org.junit.jupiter.api.Assertions.*;

import org.jbox2d.dynamics.World;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

//import fr.upc.g5e4.model.GroundModel;
import fr.upc.g5e4.model.PersonnageModel;
import fr.upc.g5e4.controller.PersonnageController;

/**
 * Tests unitaires pour PersonnageController (avec Junit)
 * 
 */
public class PersonnageControllerTest {
    private static GameLauncher gL;
    private static GameManager gm;
    private static World world;

    private final static int FIXHZ = 60; // J'aurais pu le recup avec un getter mais on part du principe qu'il sera toujours à 60 dans ce projet
    
    /**
     * Initalise pour toute les instances de Tests un meme GameLuncher
     * @BeforeAll est exécuté une seule fois avant tous les tests
     */
    @BeforeAll
    public static void setupClass(){
        gL = new GameLauncher();
        gm = gL.getGameManager();
        world = gm.getWorld();
        System.out.println("GameLauncher et GameManager initialisés");
    }

    // @Tests activation/désactivation touches
    @Test
    public void testActivateLeft() {
        PersonnageModel model = gm.getPersonnageModel();
        PersonnageController controller = gm.getPersonnageController();
        double positionInitiale = model.getXcord();

        controller.activateLeft();

        /**
         * Il faut laisser du temps (2sec) pour que le perso ait le temps de bouger
         */
        for (int i = 0; i < 120 ; i++){
            world.step(FIXHZ, 8, 3);
            controller.update();
        }
        
        controller.deactivateLeft();

        assertTrue(model.getXcord() < positionInitiale, "le worm devrait se déplacer vers la gauche");
    }
    
    
    @Test
    public void testActivateRight() {
        PersonnageModel model = gm.getPersonnageModel();
        PersonnageController controller = gm.getPersonnageController();
        double positionInitiale = model.getXcord();

        controller.activateRight();

        /**
         * Il faut laisser du temps (2sec) pour que le perso ait le temps de bouger
         */
        for (int i = 0; i < 120 ; i++){
            world.step(FIXHZ, 8, 3);
            controller.update();
        }
        
        controller.deactivateRight();
        assertTrue(model.getXcord() > positionInitiale, "le worm devrait se déplacer vers la droite");
    }
    

    @Test
    public void testDeactivateRight() {
        PersonnageModel model = gm.getPersonnageModel();
        PersonnageController controller = gm.getPersonnageController();

        controller.activateRight();

        for (int i = 0; i < 1 ; i++){   // 1 pour pas biaiser le test, sinon le worms a le temps de tomber et glisser
            world.step(FIXHZ, 8, 3);
            controller.update();
        }


        controller.deactivateRight();

        for (int i = 0; i < 1 ; i++){ 
            world.step(FIXHZ, 8, 3);
            controller.update();
        }
        double positionInitiale = model.getXcord();

        assertEquals(positionInitiale, model.getXcord(), "le worm ne devrait pas bouger");
    }

    @Test
    public void testDeactivateLeft() {
        PersonnageModel model = gm.getPersonnageModel();
        PersonnageController controller = gm.getPersonnageController();

        controller.activateLeft();

        for (int i = 0; i < 1 ; i++){   // 1 pour pas biaiser le test, sinon le worms a le temps de tomber et glisser
            world.step(FIXHZ, 8, 3);
            controller.update();
        }


        controller.deactivateLeft();

        for (int i = 0; i < 1 ; i++){ 
            world.step(FIXHZ, 8, 3);
            controller.update();
        }
        double positionInitiale = model.getXcord();

        assertEquals(positionInitiale, model.getXcord(), "le worm ne devrait pas bouger");
    }

    
    // @Tests méthode update()
    
    @Test
    public void testUpdateWithoutKeyPress() {
        PersonnageModel model = gm.getPersonnageModel();
        PersonnageController controller = gm.getPersonnageController();
        double positionInitiale = model.getXcord();
        
        boolean hasMoved = true;
        for (int i = 0; i < 1 ; i++){   // j'ai mis une minuscule valeur pour pas que le worms glisse sur le terrain et biase le test
            world.step(FIXHZ, 8, 3);
            hasMoved = controller.update();
        }
        assertFalse(hasMoved, "update() devrait retourner false si aucune touche n'est enfoncée");
        assertEquals(positionInitiale, model.getXcord(), "la position ne devrait pas changer");
    }
    
    /**
     * //@Test
     * public void testUpdateWithLeftKeyPress() {}
     * testUpdateWithRightKeyPress(){}
     * La Vitesse n'est pas égale à la distance de déplacement
     * On peut pas tester ces fonctions simplement en fesant
     * assertEquals(100 + model.getSpeed(), model.getXcord(), "la position X devrait augmenter de la vitesse");
     * Simplement tester si le perso va a droite a gauche suffit
     */
    
    
    @Test
    public void testUpdateWithLeftAndRightKeyPress() {
        PersonnageModel model = gm.getPersonnageModel();
        PersonnageController controller = gm.getPersonnageController();
        double positionInitiale = model.getXcord();
        
        controller.activateLeft();
        controller.activateRight();
        boolean hasMoved = true;
        for (int i = 0; i < 1 ; i++){
            world.step(FIXHZ, 8, 3);
            hasMoved = controller.update();
        }

        assertFalse(hasMoved, "update() devrait retourner false car les mouvements s'annulent");
        assertEquals(positionInitiale, model.getXcord(), "la position ne devrait pas changer (forces opposées)");
    }
    
    // @Tests de la méthode teleport()
    
    /**
     * Teleportation pas encore integré dans la logique du jeu 
     * donc pas besoin de verifier avec la physique
     */
    @Test
    public void testTeleport() {
        PersonnageModel model = gm.getPersonnageModel();
        PersonnageController controller = gm.getPersonnageController();
        
        controller.teleport(500, 300);
        
        assertEquals(500, model.getXcord(), "la position X devrait être 500");
        assertEquals(300, model.getYcord(), "la position Y devrait être 300");
    }

}

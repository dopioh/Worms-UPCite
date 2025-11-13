package fr.upc.g5e4;

import static org.junit.jupiter.api.Assertions.*;

import org.jbox2d.dynamics.World;
//import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//import fr.upc.g5e4.model.GroundModel;
import fr.upc.g5e4.model.PersonnageModel;
import fr.upc.g5e4.controller.PersonnageController;

/**
 * Tests unitaires pour PersonnageController (avec Junit)
 * 
 */
public class PersonnageControllerTest {
    private GameLauncher gL;
    private GameManager gm;
    private World world;

    private PersonnageModel model;
    private PersonnageController controller;

    private final static float FIXHZ = 1f/60f; // J'aurais pu le recup avec un getter mais on part du principe qu'il sera toujours à 60 dans ce projet
    
    /**
     * Chaque fonction recrée un monde
     * Car je n'arrivais pas à réinitialiser les positions en initialisant gl une seule fois
     * avec @BeforeAll
     * Du coup le test prend plus de temps et consomme plus de ressources
     * Ca pourra etre optimisé plus tard
     */
    @BeforeEach
    public void setUp() {

        gL = new GameLauncher(false);
        gm = gL.getGameManager();
        world = gm.getWorld();

        model = gm.getPersonnageModel();
        controller = gm.getPersonnageController();
        
        // Désactiver toutes les touches au cas où un test précédent les aurait laissées actives
        controller.deactivateLeft();
        controller.deactivateRight();
        controller.deactivateJump();
    }
    // @Tests activation/désactivation touches
    @Test
    public void testActivateLeft() {
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
        double positionInitiale = model.getXcord();

        controller.activateRight();

        for (int i = 0; i < 120 ; i++){
            world.step(FIXHZ, 8, 3);
            controller.update();
        }
        
        controller.deactivateRight();

        assertTrue(model.getXcord() > positionInitiale, "le worm devrait se déplacer vers la droite");
    }

    

    @Test
    public void testDeactivateRight() {

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
        
        controller.teleport(500, 300);
        
        assertEquals(500, model.getXcord(), "la position X devrait être 500");
        assertEquals(300, model.getYcord(), "la position Y devrait être 300");
    }

}

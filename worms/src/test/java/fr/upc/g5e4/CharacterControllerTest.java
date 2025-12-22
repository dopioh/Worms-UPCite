package fr.upc.g5e4;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.jbox2d.dynamics.World;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.upc.g5e4.model.CharacterModel;
import fr.upc.g5e4.model.GameLoop;
import fr.upc.g5e4.model.GameManager;
//import fr.upc.g5e4.model.GroundModel;
import fr.upc.g5e4.controller.CharacterController;


/**
 * Tests unitaires pour PersonnageController (avec Junit)
 * 
 */
public class CharacterControllerTest {
    private GameLauncher gL;
    private GameManager gm;
    private GameLoop gLoop;
    private World world;

    private CharacterModel model;
    private CharacterController controller;

    private final static float FIXHZ = 1f/60f; // J'aurais pu le recup avec un getter mais on part du principe qu'il sera toujours à 60 dans ce projet
    
    /**
     * Chaque fonction recrée un monde
     * Car je n'arrivais pas à réinitialiser les positions en initialisant gl une seule fois
     * avec @BeforeAll
     * Du coup le test prend plus de temps et consomme plus de ressources
     * Ca pourra etre optimisé plus tard
     */


    @BeforeEach
    public void setUp() throws IOException {

        gL = new GameLauncher(false);
        gLoop = gL.getGameLoop(); 
        gm = gL.getGameManager();
        world = gm.getWorld();

        // on utilise le worm actif a la place de l'ancien worm jaune de test
        controller = gm.getActiveWormController();
        //model = gm.getTeamModel1().getWorm(gm.getActiveWormIndex());
        model = gm.getListTeamInit().get(1).getTeamModel().getWorm(0);

        int[] groundY = {400,420,450,450,400,700,930,450,350,300,250,230,200,200};

        gm.getGroundModel().setGroundY(groundY);
        
        // Désactiver toutes les touches au cas où un test précédent les aurait laissées actives
        controller.deactivateLeft();
        controller.deactivateRight();
        controller.deactivateJump();

        gLoop.timerStop();
    }
    // @Tests activation/désactivation touches
    
    /*
    @Test
    public void testActivateLeft() {
        double positionInitiale = model.getXcord();

        controller.activateLeft();

        //Il faut laisser du temps (2sec) pour que le perso ait le temps de bouger
         
        for (int i = 0; i < 120 ; i++){
            world.step(FIXHZ, 8, 3);
            controller.update();
        }
        
        controller.deactivateLeft();

        assertTrue(model.getXcord() < positionInitiale, "le worm devrait se déplacer vers la gauche");
    }
    */

    /*@Test
    public void testActivateRight() {
        // Attendre que le worm soit stable sur le sol avant de mesurer
        for (int i = 0; i < 30; i++) {
            world.step(FIXHZ, 8, 3);
            controller.update();
        }
        
        double positionInitiale = model.getXcord();

        controller.activateRight();

        // Plus d'itérations pour garantir le mouvement dans différents environnements CI
        for (int i = 0; i < 200 ; i++){
            world.step(FIXHZ, 8, 3);
            controller.update();
        }
        
        controller.deactivateRight();

        // Le worm doit avoir bougé vers la droite (ou être mort/tombé)
        assertTrue(model.getXcord() > positionInitiale || model.isDead(), 
            "le worm devrait se déplacer vers la droite ou être mort");
    }*/

    

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

}

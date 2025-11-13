package fr.upc.g5e4;

import fr.upc.g5e4.model.PersonnageModel;
import fr.upc.g5e4.model.GroundModel;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
/*
 * Classe de test unitaire pour tester la classe PersonnageModel.
 * Des mocks sont utiliser pour isoler les tests.
 */
class PersonnageModelTest {

    private GroundModel mockGround;
    private World mockWorld;
    private Body mockBody;
    private PersonnageModel personnage;

    @BeforeEach
    void setUp() {
        // Création des mocks
        mockGround = mock(GroundModel.class);
        mockWorld = mock(World.class);
        mockBody = mock(Body.class);

        // world.createBody retourne notre mockBody
        when(mockWorld.createBody(any())).thenReturn(mockBody);

        // mock getPosition pour éviter NullPointerException dans isOnGround()
        when(mockBody.getPosition()).thenReturn(new Vec2(0f, 0f));

        // mock getWorldCenter pour éviter null dans jump()
        when(mockBody.getWorldCenter()).thenReturn(new Vec2(0f, 0f));

        // mock du raycast pour qu'il ne touche rien → isOnGround retourne false si nécessaire
        doAnswer(invocation -> null).when(mockWorld).raycast(any(), any(), any());

        // Création du personnage : injecte le mockBody via createBody()
        personnage = new PersonnageModel(mockGround, mockWorld, 100, 200) {
            @Override
            public Body createBody() {
                return mockBody; // injecte le mockBody
            }

            @Override
            public boolean isOnGround() {
                return true; // par défaut pour testJumpWhenOnGround
            }
        };
    }

    @Test
    void testAttributsInitiaux() {
        assertEquals(100, personnage.getXcord());
        assertEquals(200, personnage.getYcord());
        assertEquals(100.0, personnage.getHealth());
        assertEquals(50.0, personnage.getShield());
        assertFalse(personnage.isDead());
        assertNotNull(personnage.getBody());
    }

    @Test
    void testSettersEtGetters() {
        personnage.setHealth(80);
        personnage.setShield(30);
        personnage.setSpeed(12);
        personnage.setJump(6);
        personnage.setDead(true);

        assertEquals(80, personnage.getHealth());
        assertEquals(30, personnage.getShield());
        assertEquals(12, personnage.getSpeed());
        assertEquals(6, personnage.getJump());
        assertTrue(personnage.isDead());
    }

    @Test
    void testJumpWhenOnGround() {
        // jump() utilise mockBody via createBody()
        personnage.jump();
        // Vérifie que applyLinearImpulse a été appelé avec n'importe quel Vec2 pour l'impulsion et le point
        verify(mockBody, times(1)).applyLinearImpulse(any(Vec2.class), any(Vec2.class));
    }

    @Test
    void testJumpWhenNotOnGround() {
        PersonnageModel persoAir = new PersonnageModel(mockGround, mockWorld, 100, 200) {
            @Override
            public Body createBody() {
                return mockBody;
            }

            @Override
            public boolean isOnGround() {
                return false; // force le personnage à être en l'air
            }
        };

        persoAir.jump();
        verify(mockBody, never()).applyLinearImpulse(any(Vec2.class), any(Vec2.class));
    }

    @Test
    void testIsOnGroundReturnFalseQuandAucunRaycast() {
        PersonnageModel persoTest = new PersonnageModel(mockGround, mockWorld, 100, 200) {
            @Override
            public Body createBody() {
                return mockBody;
            }

            @Override
            public boolean isOnGround() {
                return super.isOnGround(); // appelle la vraie méthode
            }
        };

        assertFalse(persoTest.isOnGround());
    }

    @Test
    void testToStringContientNomEtCoordonnees() {
        String str = personnage.toString();
        assertTrue(str.contains("Worm"));
        assertTrue(str.contains("Position"));
        assertTrue(str.contains("Health"));
    }
}

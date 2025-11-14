package fr.upc.g5e4.camera;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import fr.upc.g5e4.model.CharacterModel;
import fr.upc.g5e4.model.camera.CameraModel;

class CameraTest {

    /**
     * Classe de tests unitaires pour CameraModel.
     * 
     * Ici on teste tout ce qui concerne la caméra :
     * - le centrage sur le personnage
     * - le respect des limites de la map (clamp)
     * - le déplacement manuel
     * - le zoom avant et arrière
     * - le comportement sur des positions aléatoires
     *
     * J'ai choisi d'éviter d'utiliser les dépendances,
     * pour qu'en cas de modification, les tests ne soient pas tourner en erreur.
     */
    static class TestPersonnageModel extends CharacterModel {
        private double x, y;

        public TestPersonnageModel(double x, double y) {
            super(null, null, 0, 0); // Ne pas utiliser world ou model
            this.x = x;
            this.y = y;
        }

        @Override
        public double getXcord() {
            return x;
        }

        @Override
        public double getYcord() {
            return y;
        }

        public void setPosition(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public org.jbox2d.dynamics.Body createBody() {
            return null; // On ne crée aucun body pour le test. Cette fonction est un semblant de mock
                         // pour satisfaire JBox2D.
        }
    }

    @Test
    // Test du centrage de la caméra sur un personnage.
    void testCameraCenterOnCharacter() {
        CameraModel camera = new CameraModel(800, 600, 2000, 1200, 1);
        TestPersonnageModel personnage = new TestPersonnageModel(1000, 600);

        camera.centerOn(personnage);

        assertEquals(personnage.getXcord() - 800 / 2.0, camera.getX(), 0.001);
        assertEquals(personnage.getYcord() - 600 / 2.0, camera.getY(), 0.001);
    }

    @Test
    // Test que la caméra ne sort jamais des limites de la map.
    void testCameraClampLimits() {
        CameraModel camera = new CameraModel(800, 600, 1000, 800, 1);

        // Bord supérieur gauche
        TestPersonnageModel p1 = new TestPersonnageModel(-100, -50);
        camera.centerOn(p1);
        assertEquals(0, camera.getX(), 0.001);
        assertEquals(0, camera.getY(), 0.001);

        // Bord inférieur droit
        TestPersonnageModel p2 = new TestPersonnageModel(1200, 900);
        camera.centerOn(p2);
        assertEquals(1000 - 800, camera.getX(), 0.001); // worldWidth - viewWidth
        assertEquals(800 - 600, camera.getY(), 0.001); // worldHeight - viewHeight
    }

    @Test
    /*
     * Test la caméra sur des positions aléatoires, y compris hors limites,
     * pour vérifier que le clamp fonctionne correctement.
     */
    void testCameraRandomPositions() {
        CameraModel camera = new CameraModel(800, 600, 2000, 1200, 1);

        for (int i = 0; i < 1000; i++) {
            // Propose des positions aléatoires avec certaines étant hors limite
            double px = Math.random() * 3000 - 500;
            double py = Math.random() * 2000 - 200;

            TestPersonnageModel personnage = new TestPersonnageModel(px, py);
            camera.centerOn(personnage);

            // On vérifie que la caméra ne sort jamais des limites
            assertTrue(camera.getX() >= 0 && camera.getX() <= 2000 - 800);
            assertTrue(camera.getY() >= 0 && camera.getY() <= 1200 - 600);
        }
    }

    /*
     * Test le zoom avant
     */
    @Test
    void testZoomIn() {
        CameraModel camera = new CameraModel(800, 600, 2000, 1200, 1);
        TestPersonnageModel personnage = new TestPersonnageModel(1000, 600);

        camera.centerOn(personnage);

        int oldZoomedWidth = camera.getZoomedViewWidth();
        int oldZoomedHeight = camera.getZoomedViewHeight();

        camera.zoom(2.0, personnage); // zoom avant x2

        assertEquals(oldZoomedWidth / 2.0, camera.getZoomedViewWidth(), 0.001);
        assertEquals(oldZoomedHeight / 2.0, camera.getZoomedViewHeight(), 0.001);

        // La caméra doit rester centrée sur le personnage
        assertEquals(personnage.getXcord() - camera.getZoomedViewWidth() / 2.0, camera.getX(), 0.001);
        assertEquals(personnage.getYcord() - camera.getZoomedViewHeight() / 2.0, camera.getY(), 0.001);
    }

    /*
     * Test le zoom arrière
     */
    @Test
    void testZoomOut() {
        CameraModel camera = new CameraModel(800, 600, 2000, 1200, 2.0); // départ zoom x2
        TestPersonnageModel personnage = new TestPersonnageModel(1000, 600);

        camera.centerOn(personnage);

        int oldZoomedWidth = camera.getZoomedViewWidth();
        int oldZoomedHeight = camera.getZoomedViewHeight();

        camera.zoom(1.0, personnage); // zoom arrière x0.5

        assertEquals(oldZoomedWidth * 2.0, camera.getZoomedViewWidth(), 0.001);
        assertEquals(oldZoomedHeight * 2.0, camera.getZoomedViewHeight(), 0.001);

        // La caméra doit rester centrée sur le personnage
        assertEquals(personnage.getXcord() - camera.getZoomedViewWidth() / 2.0, camera.getX(), 0.001);
        assertEquals(personnage.getYcord() - camera.getZoomedViewHeight() / 2.0, camera.getY(), 0.001);
    }

    /*
     * Test le déplacement caméra
     */
    @Test
    void testMoveCamera() {
        CameraModel camera = new CameraModel(800, 600, 2000, 1200, 1);
        TestPersonnageModel personnage = new TestPersonnageModel(1000, 600);

        camera.centerOn(personnage);

        double originalX = camera.getX();
        double originalY = camera.getY();

        camera.move(100, 50);

        // La caméra doit se décaler du delta
        assertEquals(originalX + 100, camera.getX(), 0.001);
        assertEquals(originalY + 50, camera.getY(), 0.001);
    }

}

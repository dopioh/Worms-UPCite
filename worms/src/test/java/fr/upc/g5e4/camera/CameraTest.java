package fr.upc.g5e4.camera;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import fr.upc.g5e4.model.PersonnageModel;
import fr.upc.g5e4.model.camera.CameraModel;

/**
 * Tests unitaires pour CameraModel.
 * 
 * On teste ici :
 * 1. Le centrage de la caméra sur un personnage.
 * 2. Le respect des limites du monde (clamp).
 * 3. Des positions aléatoires pour vérifier la robustesse.
 * 
 * Pour éviter les dépendances, j'ai crée une classe TestPersonnageModel
 * qui ne nécessite pas d'autres classes.
 */

class CameraTest {

    /**
     * Classe pour tester CameraModel
     * J'ai choisi d'éviter d'utiliser les dépendances,
     * pour qu'en cas de modification, les tests ne soient pas tourner en erreur.
     */
    static class TestPersonnageModel extends PersonnageModel {
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
            return null; // On ne crée aucun body pour le test
        }
    }

    @Test
    // Test du centrage de la caméra sur un personnage.
    void testCameraCenterOnCharacter() {
        CameraModel camera = new CameraModel(800, 600, 2000, 1200);
        TestPersonnageModel personnage = new TestPersonnageModel(1000, 600);

        camera.centerOn(personnage);

        assertEquals(personnage.getXcord() - 800 / 2.0, camera.getX(), 0.001);
        assertEquals(personnage.getYcord() - 600 / 2.0, camera.getY(), 0.001);
    }

    @Test
    // Test que la caméra ne sort jamais des limites du monde.
    void testCameraClampLimits() {
        CameraModel camera = new CameraModel(800, 600, 1000, 800);

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

    /**
     * Test la caméra sur des positions aléatoires, y compris hors limites,
     * pour vérifier que le clamp fonctionne correctement.
     */

    @Test
    void testCameraRandomPositions() {
        CameraModel camera = new CameraModel(800, 600, 2000, 1200);

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
}

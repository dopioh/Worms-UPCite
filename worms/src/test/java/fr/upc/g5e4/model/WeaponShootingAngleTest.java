package fr.upc.g5e4.model;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.upc.g5e4.model.weapons.Ak47;
import fr.upc.g5e4.model.weapons.BaseballBat;
import fr.upc.g5e4.model.weapons.Grenade;
import fr.upc.g5e4.model.weapons.RocketLauncher;

public class WeaponShootingAngleTest {
    private Weapon ak47;
    private Weapon baseballBat;
    private Weapon grenade;
    private Weapon rocketLauncher;

    @BeforeEach
    public void setUp() {
        ak47 = new Ak47();
        baseballBat = new BaseballBat();
        grenade = new Grenade();
        rocketLauncher = new RocketLauncher();
    }

    @Test
    public void testDefaultFiringAngleIsZero() {
        assertEquals(0, ak47.getShootAngle(), 0.0001);
    }

    @Test
    public void testSetShootingAngleHorizontal() {
        double angle = 0;
        ak47.setShootingAngle(angle);
        assertEquals(angle, ak47.getShootAngle(), 0.0001);
    }

    @Test
    public void testSetShootingAngle45Degrees() {
        double angle = Math.PI / 4;  // 45 degrés
        ak47.setShootingAngle(angle);
        assertEquals(angle, ak47.getShootAngle(), 0.0001);
    }

    @Test
    public void testSetShootingAngle90Degrees() {
        double angle = Math.PI / 2;  // 90 degrés (vertical)
        ak47.setShootingAngle(angle);
        assertEquals(angle, ak47.getShootAngle(), 0.0001);
    }

    @Test
    public void testSetShootingAngleNegative() {
        double angle = -Math.PI / 6;  // -30 degrés
        ak47.setShootingAngle(angle);
        assertEquals(angle, ak47.getShootAngle(), 0.0001);
    }

    @Test
    public void testSetShootingAngleVarious() {
        double[] angles = {0, Math.PI / 6, Math.PI / 4, Math.PI / 3, Math.PI / 2};
        
        for (double angle : angles) {
            ak47.setShootingAngle(angle);
            assertEquals(angle, ak47.getShootAngle(), 0.0001,
                "Angle " + Math.toDegrees(angle) + "° should be stored correctly");
        }
    }

    @Test
    public void testSetShootingAngleAllWeapons() {
        double angle = Math.PI / 4;
        
        ak47.setShootingAngle(angle);
        baseballBat.setShootingAngle(angle);
        grenade.setShootingAngle(angle);
        rocketLauncher.setShootingAngle(angle);
        
        assertEquals(angle, ak47.getShootAngle(), 0.0001);
        assertEquals(angle, baseballBat.getShootAngle(), 0.0001);
        assertEquals(angle, grenade.getShootAngle(), 0.0001);
        assertEquals(angle, rocketLauncher.getShootAngle(), 0.0001);
    }

    @Test
    public void testSetShootingAngleMultipleTimes() {
        // Premier angle
        double angle1 = Math.PI / 6;
        ak47.setShootingAngle(angle1);
        assertEquals(angle1, ak47.getShootAngle(), 0.0001);
        
        // Deuxième angle
        double angle2 = Math.PI / 3;
        ak47.setShootingAngle(angle2);
        assertEquals(angle2, ak47.getShootAngle(), 0.0001);
        
        // Troisième angle
        double angle3 = Math.PI / 2;
        ak47.setShootingAngle(angle3);
        assertEquals(angle3, ak47.getShootAngle(), 0.0001);
    }

    @Test
    public void testSetShootingAngleSmallValues() {
        double angle = 0.01;  // Très petit angle
        ak47.setShootingAngle(angle);
        assertEquals(angle, ak47.getShootAngle(), 0.0001);
    }

    @Test
    public void testSetShootingAngleLargeValues() {
        double angle = 3.0;  // ~172 degrés
        ak47.setShootingAngle(angle);
        assertEquals(angle, ak47.getShootAngle(), 0.0001);
    }

    @Test
    public void testSetShootingAnglePi() {
        double angle = Math.PI;  // 180 degrés
        ak47.setShootingAngle(angle);
        assertEquals(angle, ak47.getShootAngle(), 0.0001);
    }

    @Test
    public void testShootingAngleIndependentBetweenWeapons() {
        double angle1 = Math.PI / 4;
        double angle2 = Math.PI / 3;
        
        ak47.setShootingAngle(angle1);
        baseballBat.setShootingAngle(angle2);
        
        assertEquals(angle1, ak47.getShootAngle(), 0.0001);
        assertEquals(angle2, baseballBat.getShootAngle(), 0.0001);
    }
}

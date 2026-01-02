package fr.upc.g5e4.weapons;

import fr.upc.g5e4.model.weapons.Ak47;
import fr.upc.g5e4.model.weapons.BaseballBat;
import fr.upc.g5e4.model.weapons.Grenade;
import fr.upc.g5e4.model.weapons.RocketLauncher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests pour les propriétés de dégâts terrain des armes
 */
public class TerrainDamageWeaponsTest {

    @Test
    public void testAk47TerrainDamage() {
        Ak47 ak47 = new Ak47();
        
        // AK-47 doit faire 5 PV de dégâts terrain
        assertEquals(5, ak47.getTerrainDamage());
        
        // Rayon de blast: 1.0 bloc
        assertEquals(1.0, ak47.getTerrainBlastRadius(), 0.01);
    }

    @Test
    public void testGrenadeTerrainDamage() {
        Grenade grenade = new Grenade();
        
        // Grenade doit faire 7 PV de dégâts terrain
        assertEquals(7, grenade.getTerrainDamage());
        
        // Rayon de blast: 5.0 blocs
        assertEquals(5.0, grenade.getTerrainBlastRadius(), 0.01);
    }

    @Test
    public void testRocketLauncherTerrainDamage() {
        RocketLauncher rocket = new RocketLauncher();
        
        // RocketLauncher doit faire 9 PV de dégâts terrain
        assertEquals(9, rocket.getTerrainDamage());
        
        // Rayon de blast: 7.0 blocs
        assertEquals(7.0, rocket.getTerrainBlastRadius(), 0.01);
    }

    @Test
    public void testBaseballBatTerrainDamage() {
        BaseballBat bat = new BaseballBat();
        
        // le baseball bat fait 5 PV de dégâts terrain (arme de mêlée)
        assertEquals(5, bat.getTerrainDamage());
        
        // rayon de blast: 0.0 bloc (pas d'explosion)
        assertEquals(0.0, bat.getTerrainBlastRadius(), 0.01);
    }

    @Test
    public void testTerrainDamageComparison() {
        Ak47 ak47 = new Ak47();
        Grenade grenade = new Grenade();
        RocketLauncher rocket = new RocketLauncher();
        
        // vérifier l'ordre: AK-47 (5) < Grenade (7) < RocketLauncher (9)
        assertTrue(ak47.getTerrainDamage() < grenade.getTerrainDamage());
        assertTrue(grenade.getTerrainDamage() < rocket.getTerrainDamage());
    }

    @Test
    public void testTerrainBlastRadiusComparison() {
        Ak47 ak47 = new Ak47();
        Grenade grenade = new Grenade();
        RocketLauncher rocket = new RocketLauncher();
        
        // vérifier l'ordre: AK-47 (1.0) < Grenade (5.0) < RocketLauncher (7.0)
        assertTrue(ak47.getTerrainBlastRadius() < grenade.getTerrainBlastRadius());
        assertTrue(grenade.getTerrainBlastRadius() < rocket.getTerrainBlastRadius());
    }
}

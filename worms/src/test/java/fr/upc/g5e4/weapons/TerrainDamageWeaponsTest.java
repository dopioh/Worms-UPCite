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
        
        // AK-47 doit faire 1 PV de dégâts terrain
        assertEquals(1, ak47.getTerrainDamage());
        
        // Rayon de blast: 0.5 bloc
        assertEquals(0.5, ak47.getTerrainBlastRadius(), 0.01);
    }

    @Test
    public void testGrenadeTerrainDamage() {
        Grenade grenade = new Grenade();
        
        // Grenade doit faire 3 PV de dégâts terrain
        assertEquals(3, grenade.getTerrainDamage());
        
        // Rayon de blast: 2.0 blocs
        assertEquals(2.0, grenade.getTerrainBlastRadius(), 0.01);
    }

    @Test
    public void testRocketLauncherTerrainDamage() {
        RocketLauncher rocket = new RocketLauncher();
        
        // RocketLauncher doit faire 5 PV de dégâts terrain
        assertEquals(5, rocket.getTerrainDamage());
        
        // Rayon de blast: 3.0 blocs
        assertEquals(3.0, rocket.getTerrainBlastRadius(), 0.01);
    }

    @Test
    public void testBaseballBatTerrainDamage() {
        BaseballBat bat = new BaseballBat();
        
        // le baseball bat fait 1 PV de dégâts terrain (arme de mêlée)
        assertEquals(1, bat.getTerrainDamage());
        
        // rayon de blast: 0.5 bloc
        assertEquals(0.5, bat.getTerrainBlastRadius(), 0.01);
    }

    @Test
    public void testTerrainDamageComparison() {
        Ak47 ak47 = new Ak47();
        Grenade grenade = new Grenade();
        RocketLauncher rocket = new RocketLauncher();
        
        // vérifier l'ordre: AK-47 (1) < Grenade (3) < RocketLauncher (5)
        assertTrue(ak47.getTerrainDamage() < grenade.getTerrainDamage());
        assertTrue(grenade.getTerrainDamage() < rocket.getTerrainDamage());
    }

    @Test
    public void testTerrainBlastRadiusComparison() {
        Ak47 ak47 = new Ak47();
        Grenade grenade = new Grenade();
        RocketLauncher rocket = new RocketLauncher();
        
        // vérifier l'ordre: AK-47 (0.5) < Grenade (2.0) < RocketLauncher (3.0)
        assertTrue(ak47.getTerrainBlastRadius() < grenade.getTerrainBlastRadius());
        assertTrue(grenade.getTerrainBlastRadius() < rocket.getTerrainBlastRadius());
    }
}

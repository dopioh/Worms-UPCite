package fr.upc.g5e4.Inventory;
import fr.upc.g5e4.model.InventoryModel;
import fr.upc.g5e4.model.InventoryModel.InventoryJson;
import fr.upc.g5e4.model.InventoryModel.InventoryRoot;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryModelTest {

    // Petit helper pour créer un item rapidement
    private InventoryJson makeItem(String name,
                                   String type,
                                   int maxUsages,
                                   boolean isInfinite,
                                   Integer remainingUses) {
        InventoryJson item = new InventoryJson();
        item.name = name;
        item.objectType = type;
        item.maxUsages = maxUsages;
        item.isInfinite = isInfinite;
        item.remainingUses = remainingUses;
        item.useThisTurn = 0;
        return item;
    }

    /* ---------- hasRemainingUses() ---------- */

    @Test
    void testHasRemainingUsesInfinite() {
        InventoryJson item = makeItem("Bat", "WEAPON", 2, true, null);

        assertTrue(item.hasRemainingUses(),
                "Un objet infini devrait toujours avoir des utilisations restantes");
    }

    @Test
    void testHasRemainingUsesFiniteZero() {
        InventoryJson item = makeItem("Grenade", "WEAPON", 1, false, 0);

        assertFalse(item.hasRemainingUses(),
                "Un objet avec remainingUses = 0 ne devrait plus avoir d’utilisations restantes");
    }

    @Test
    void testHasRemainingUsesFinitePositive() {
        InventoryJson item = makeItem("Grenade", "WEAPON", 2, false, 3);

        assertTrue(item.hasRemainingUses(),
                "Un objet avec remainingUses > 0 devrait avoir des utilisations restantes");
    }

    /* ---------- canUseThisTurn() ---------- */

    @Test
    void testCanUseThisTurnBelowMax() {
        InventoryJson item = makeItem("Bat", "WEAPON", 2, true, null);
        item.useThisTurn = 1; // déjà utilisé 1 fois

        assertTrue(item.canUseThisTurn(),
                "Si useThisTurn < maxUsages et objet utilisable, on doit pouvoir utiliser");
    }

    @Test
    void testCanUseThisTurnAtMax() {
        InventoryJson item = makeItem("Bat", "WEAPON", 2, true, null);
        item.useThisTurn = 2; // déjà utilisé maxUsages fois

        assertFalse(item.canUseThisTurn(),
                "Si useThisTurn >= maxUsages, on ne doit plus pouvoir utiliser ce tour");
    }

    @Test
    void testCanUseThisTurnNoAmmo() {
        InventoryJson item = makeItem("Grenade", "WEAPON", 5, false, 0);
        item.useThisTurn = 0;

        assertFalse(item.canUseThisTurn(),
                "Si remainingUses = 0, canUseThisTurn doit être false même si useThisTurn < maxUsages");
    }

    /* ---------- registerUseThisTurn() ---------- */

    @Test
    void testRegisterUseThisTurnSuccessFinite() {
        InventoryJson item = makeItem("Grenade", "WEAPON", 3, false, 2);
        item.useThisTurn = 1;

        boolean ok = item.registerUseThisTurn();

        assertTrue(ok, "registerUseThisTurn doit réussir si on a encore des utilisations possibles");
        assertEquals(2, item.useThisTurn,
                "useThisTurn doit être incrémenté");
        assertEquals(1, item.remainingUses,
                "remainingUses doit être décrémenté pour un objet non infini");
    }

    @Test
    void testRegisterUseThisTurnFailWhenNoAmmo() {
        InventoryJson item = makeItem("Grenade", "WEAPON", 3, false, 0);
        item.useThisTurn = 0;

        boolean ok = item.registerUseThisTurn();

        assertFalse(ok, "registerUseThisTurn doit échouer si il n’y a plus de munitions");
        assertEquals(0, item.useThisTurn,
                "useThisTurn ne doit pas bouger en cas d’échec");
    }

    @Test
    void testRegisterUseThisTurnInfinite() {
        InventoryJson item = makeItem("Bat", "WEAPON", 2, true, null);
        item.useThisTurn = 0;

        boolean ok = item.registerUseThisTurn();

        assertTrue(ok, "Un objet infini doit pouvoir être utilisé tant que useThisTurn < maxUsages");
        assertEquals(1, item.useThisTurn);
        assertNull(item.remainingUses,
                "remainingUses doit rester null pour un objet infini");
    }

    /* ---------- resetTurn() ---------- */

    @Test
    void testResetTurnResetsUseThisTurn() {
        InventoryJson item = makeItem("Bat", "WEAPON", 3, true, null);
        item.useThisTurn = 3;

        item.resetTurn();

        assertEquals(0, item.useThisTurn,
                "resetTurn doit remettre useThisTurn à 0");
    }

    /* ---------- getRemainingUseThisTurn() ---------- */

    @Test
    void testGetRemainingUseThisTurn() {
        InventoryJson item = makeItem("Bat", "WEAPON", 3, true, null);
        item.useThisTurn = 1;

        assertEquals(2, item.getRemainingUseThisTurn(),
                "getRemainingUseThisTurn doit retourner maxUsages - useThisTurn");
    }

    /* ---------- getMaxUsages() ---------- */

    @Test
    void testGetMaxUsages() {
        InventoryJson item = makeItem("Bat", "WEAPON", 5, true, null);

        assertEquals(5, item.getMaxUsages(),
                "getMaxUsages doit retourner la valeur du champ maxUsages");
    }

    /* ---------- toString() (optionnel) ---------- */

    @Test
    void testToStringFormat() {
        InventoryJson item = makeItem("Bat", "WEAPON", 1, true, null);

        String s = item.toString();
        assertTrue(s.contains("Bat"),
                "toString devrait contenir le nom de l’objet");
        assertTrue(s.contains("WEAPON") || s.contains("weapon"),
                "toString devrait contenir le type de l’objet");
    }

    /* ---------- InventoryModel.load() ---------- */

    /* NE MARCHE PAS POUR LE MOMENT */
    @Test  
    void testLoadJsonFromResources() throws IOException {
        // Attention : le chemin doit correspondre à ton vrai fichier
        // (tu peux adapter si besoin)
        String path = "worms/src/main/resources/inventory.json";

        InventoryRoot root = InventoryModel.load(path);

        assertNotNull(root, "load doit retourner un InventoryRoot non null");
        assertNotNull(root.inventory, "root.inventory ne doit pas être null");
        assertFalse(root.inventory.isEmpty(), "L’inventaire chargé ne doit pas être vide");

        // Exemple : vérifier qu’on retrouve un des objets attendus
        boolean foundBat = root.inventory.stream()
                .anyMatch(item -> "BaseballBat".equals(item.name));
        assertTrue(foundBat, "L’inventaire devrait contenir un objet 'BaseballBat'");
    }
}

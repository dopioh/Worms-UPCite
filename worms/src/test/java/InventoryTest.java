import fr.upc.g5e4.inventory.model.*;
import fr.upc.g5e4.inventory.controller.InventoryLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryTest {

    @Test
    void itemStack_useLimited_decrementsUntilZero() {
        Item ak = new Item("AK47", "AK47", Item.Type.WEAPON, 10);
        ItemStack stack = new ItemStack(ak, 2, false);

        assertTrue(stack.canUse());
        stack.consumeOne();
        assertEquals(1, stack.getQuantity());

        assertTrue(stack.canUse());
        stack.consumeOne();
        assertEquals(0, stack.getQuantity());

        assertFalse(stack.canUse());
        stack.consumeOne(); // no-op
        assertEquals(0, stack.getQuantity());
    }

    @Test
    void itemStack_useUnlimited_neverDecrements() {
        Item rope = new Item("rope", "Corde", Item.Type.TOOL, 0);
        ItemStack stack = new ItemStack(rope, 0, true);

        assertTrue(stack.canUse());
        stack.consumeOne(); // no-op
        assertEquals(0, stack.getQuantity());
        assertTrue(stack.canUse());
    }

    @Test
    void teamInventory_put_get_all_use() {
        TeamInventory inv = new TeamInventory();
        Item ak = new Item("AK47", "AK47", Item.Type.WEAPON, 10);
        Item rope = new Item("rope", "Corde", Item.Type.TOOL, 0);

        inv.put(new ItemStack(ak, 3, false));
        inv.put(new ItemStack(rope, 0, true));

        assertEquals(2, inv.all().size());

        Optional<ItemStack> akStack = inv.get("AK47");
        assertTrue(akStack.isPresent());
        assertEquals(3, akStack.get().getQuantity());

        assertTrue(inv.use("AK47"));
        assertEquals(2, akStack.get().getQuantity());

        assertTrue(inv.use("rope")); // illimité
        assertEquals(0, inv.get("rope").orElseThrow().getQuantity());

        assertFalse(inv.use("bazooka")); // inconnu
    }

    @Test
    void loader_load_validJson_buildsInventory(@TempDir Path tmp) throws IOException {
        Path json = tmp.resolve("inventory.json");
        String content = """
                {
                  "objects": [
                    { "id": "AK47", "name": "AK47", "type": "WEAPON", "damage": 10 },
                    { "id": "rope", "name": "Corde", "type": "TOOL" }
                  ],
                  "inventory": [
                    { "itemId": "AK47", "quantity": 30, "unlimited": false },
                    { "itemId": "rope", "quantity": 0, "unlimited": true }
                  ]
                }
                """;
        Files.writeString(json, content, StandardCharsets.UTF_8);

        TeamInventory inv = InventoryLoader.load(json.toString());

        assertEquals(2, inv.all().size());
        ItemStack ak = inv.get("AK47").orElseThrow();
        assertEquals(30, ak.getQuantity());
        assertFalse(ak.isUnlimited());
        assertEquals(Item.Type.WEAPON, ak.getItem().type());
        assertEquals(10, ak.getItem().damage());

        ItemStack rope = inv.get("rope").orElseThrow();
        assertTrue(rope.isUnlimited());
        assertEquals(Item.Type.TOOL, rope.getItem().type());
    }

    @Test
    void loader_save_then_reload_preservesData(@TempDir Path tmp) throws IOException {
        TeamInventory inv = new TeamInventory();
        Item ak = new Item("AK47", "AK47", Item.Type.WEAPON, 10);
        Item rope = new Item("rope", "Corde", Item.Type.TOOL, 0);
        inv.put(new ItemStack(ak, 5, false));
        inv.put(new ItemStack(rope, 0, true));

        Path out = tmp.resolve("out.json");
        InventoryLoader.save(inv, out.toString());

        TeamInventory reloaded = InventoryLoader.load(out.toString());
        assertEquals(2, reloaded.all().size());
        assertEquals(5, reloaded.get("AK47").orElseThrow().getQuantity());
        assertTrue(reloaded.get("rope").orElseThrow().isUnlimited());
    }
}

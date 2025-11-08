package fr.upc.g5e4.inventory.controller;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import fr.upc.g5e4.inventory.model.Item;
import fr.upc.g5e4.inventory.model.ItemStack;
import fr.upc.g5e4.inventory.model.TeamInventory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Utilitaire pour charger / sauvegarder l'inventaire depuis/vers un fichier JSON.
 * Format attendu :
 * {
 *   "objects": [{ "id": "AK47", "name": "AK47", "type": "WEAPON", "damage": 10 }, ...],
 *   "inventory": [{ "itemId": "AK47", "quantity": 30, "unlimited": false }, ...]
 * }
 */
public final class InventoryLoader {

    private InventoryLoader() {}

    // --- DTOs internes pour le mapping JSON ---
    static final class JsonItem {
        String id;
        String name;
        Item.Type type;
        Integer damage; // peut être null
    }

    static final class JsonStack {
        @SerializedName("itemId")
        String itemId;
        Integer quantity;
        Boolean unlimited;
    }

    static final class Root {
        List<JsonItem> objects = new ArrayList<>();
        List<JsonStack> inventory = new ArrayList<>();
    }

    // --- API publique ---

    /** Charge un inventaire à partir d'un fichier JSON. */
    public static TeamInventory load(String jsonPath) throws IOException {
        String json = Files.readString(Path.of(jsonPath), StandardCharsets.UTF_8);
        Gson gson = new Gson();
        Root root = gson.fromJson(json, Root.class);
        if (root == null) root = new Root();
        if (root.objects == null) root.objects = new ArrayList<>();
        if (root.inventory == null) root.inventory = new ArrayList<>();

        Map<String, Item> itemsById = new HashMap<>();

        // Construire les Item
        for (JsonItem ji : root.objects) {
            if (ji == null || ji.id == null || ji.id.isBlank()) {
                throw new IllegalArgumentException("Objet JSON invalide: id manquant");
            }
            Item.Type type = (ji.type != null) ? ji.type : Item.Type.TOOL;
            String name = (ji.name == null || ji.name.isBlank()) ? ji.id : ji.name;
            int damage = (ji.damage != null) ? ji.damage : 0;

            Item item = new Item(ji.id, name, type, damage);
            itemsById.put(ji.id, item);
        }

        // Construire l'inventaire d'équipe
        TeamInventory inv = new TeamInventory();
        for (JsonStack js : root.inventory) {
            if (js == null || js.itemId == null || js.itemId.isBlank()) {
                throw new IllegalArgumentException("Entrée inventory invalide: itemId manquant");
            }
            Item item = itemsById.get(js.itemId);
            if (item == null) {
                throw new IllegalStateException("itemId inconnu dans inventory: " + js.itemId);
            }
            int qty = (js.quantity != null && js.quantity > 0) ? js.quantity : 0;
            boolean unlimited = (js.unlimited != null) && js.unlimited;

            inv.put(new ItemStack(item, qty, unlimited));
        }

        return inv;
    }

    /** Sauvegarde un inventaire vers un fichier JSON (même format que load). */
    public static void save(TeamInventory inv, String jsonPath) throws IOException {
        Root root = new Root();
        Set<String> seen = new HashSet<>();

        for (ItemStack s : inv.all()) {
            Item it = s.getItem();

            if (seen.add(it.id())) {
                JsonItem ji = new JsonItem();
                ji.id = it.id();
                ji.name = it.name();
                ji.type = it.type();
                ji.damage = it.damage();
                root.objects.add(ji);
            }

            JsonStack js = new JsonStack();
            js.itemId = it.id();
            js.quantity = s.getQuantity();
            js.unlimited = s.isUnlimited();
            root.inventory.add(js);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String out = gson.toJson(root);
        Files.writeString(Path.of(jsonPath), out, StandardCharsets.UTF_8);
    }
}
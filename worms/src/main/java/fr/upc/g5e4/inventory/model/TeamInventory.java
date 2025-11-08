package fr.upc.g5e4.inventory.model;

import java.util.*;

public class TeamInventory {
    private final Map<String, ItemStack> stock = new LinkedHashMap<>();  //Stockage sous forme de pile
    private final List<Runnable> observateurs = new ArrayList<>();       //Mettre à jour l'UI (Sauvegarde des changements de données)

    public void put(ItemStack quantity){                                 //Met a jour les informations d'un objet de "stock"    
        String id = quantity.getItem().id();
        stock.put(id, quantity);
        stateChange();
    }

    public boolean use(String itemId){                                   //Utilise une fois le "itemId"
        if (itemId == null) return false;
        ItemStack quantity = stock.get(itemId);
        if (quantity == null) return false;
        if (!quantity.canUse()) return false;
        quantity.consumeOne();
        stateChange();
        return true;
    }

    public Collection<ItemStack> all(){                                  //Renvoie tous les items 
        return Collections.unmodifiableCollection(stock.values());
    }

    public Optional<ItemStack> get(String itemId){                       //Récupere une pile du stock
        return Optional.ofNullable(stock.get(itemId));
    }

    public void addObservateurs(Runnable r){                             //Indique qu'il y a changement de donnée
        if (r != null) observateurs.add(r);
    }

    private void stateChange() {   // Changement d'état
        for (Runnable r : observateurs){
            try {
                r.run();
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }
        }
    }
}

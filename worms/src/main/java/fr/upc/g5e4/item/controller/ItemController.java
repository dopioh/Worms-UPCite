package fr.upc.g5e4.item.controller;

import fr.upc.g5e4.item.model.Item;
import fr.upc.g5e4.item.view.ItemView;

public class ItemController {
    private final ItemView view;

    public ItemController(ItemView view) {
        this.view = view;
    }
    public void use(Item item){
        boolean ok = item.use();
        view.showUseResult(ok);
        view.showItem(item);
    }
}

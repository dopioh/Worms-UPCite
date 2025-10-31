package fr.upc.g5e4.item.controller;

import fr.upc.g5e4.item.model.weapons.*;
import fr.upc.g5e4.item.model.gadgets.*;
import fr.upc.g5e4.item.view.ItemView;

public class Demo {
    public static void main(String[] args) {
        var view = new ItemView();
        var controller = new ItemController(view);

        var ak = new Ak47();
        var bumper = new Bumper();

        view.showItem(ak);
        controller.use(ak);   // tir 1
        controller.use(ak);   // tir 2

        view.showItem(bumper);
        controller.use(bumper);
    }
}

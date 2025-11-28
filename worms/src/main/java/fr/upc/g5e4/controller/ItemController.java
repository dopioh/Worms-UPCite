package fr.upc.g5e4.controller;

import org.jbox2d.dynamics.World;
import fr.upc.g5e4.model.CharacterModel;
import fr.upc.g5e4.model.Item;
import fr.upc.g5e4.model.Weapon;
import fr.upc.g5e4.view.ItemView;

public class ItemController {
    private final ItemView view;
    private World world;
    private CharacterModel[] allWorms;

    public ItemController(ItemView view) {
        this.view = view;
    }

    /**
     * Définit le monde physique pour les raycast des armes
     * @param world Le monde JBox2D
     */
    public void setWorld(World world) {
        this.world = world;
    }

    /**
     * Définit le tableau de tous les worms pour les raycast
     * @param allWorms Tableau de tous les worms du jeu
     */
    public void setAllWorms(CharacterModel[] allWorms) {
        this.allWorms = allWorms;
    }

    /**
     * Utilise un item standard
     * @param item L'item à utiliser
     */
    public void use(Item item){
        boolean ok = item.use();
        view.showUseResult(ok);
        view.showItem(item);
    }

    /**
     * Utilise une arme avec raycast pour tirer sur les worms
     * @param weapon L'arme à utiliser
     * @param shooter Le worm qui tire
     * @param direction Direction du tir (1 pour droite, -1 pour gauche)
     */
    public void useWeaponOnTarget(Weapon weapon, CharacterModel shooter, int direction) {
        if (world == null || allWorms == null) {
            return;
        }

        if (weapon.use()) {
            weapon.fire(shooter, world, allWorms, direction);
            view.showItem(weapon);
        } else {
            view.showUseResult(false);
        }
    }
}

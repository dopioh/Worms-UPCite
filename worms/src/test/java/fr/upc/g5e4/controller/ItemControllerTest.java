package fr.upc.g5e4.controller;

import fr.upc.g5e4.controller.ItemController;
import fr.upc.g5e4.model.Item;
import fr.upc.g5e4.model.weapons.Ak47;
import fr.upc.g5e4.view.ItemView;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemControllerTest {

    /** Vue factice qui enregistre le dernier état affiché */
    static class FakeItemView extends ItemView {
        boolean lastOk;
        String lastLine;

        @Override
        public void showUseResult(boolean ok) {
            this.lastOk = ok;
        }

        @Override
        public void showItem(Item item) {
            // capture un bout d’info pour vérifier l’appel
            this.lastLine = item.getName() + ":" + (item.isInfinite() ? "inf" : item.getRemainingUses());
        }
    }

    @Test
    void controller_callsModelAndView() {
        var view = new FakeItemView();
        var controller = new ItemController(view);
        var ak = new Ak47(); // 30 usages

        controller.use(ak);  // tir 1
        assertTrue(view.lastOk);
        assertEquals("AK-47:29", view.lastLine);

        controller.use(ak);  // tir 2
        assertTrue(view.lastOk);
        assertEquals("AK-47:28", view.lastLine);
    }
}

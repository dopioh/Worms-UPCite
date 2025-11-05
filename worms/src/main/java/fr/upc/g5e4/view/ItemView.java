package fr.upc.g5e4.view;

import fr.upc.g5e4.model.Item;

public class ItemView {
    
    public void showItem(Item item){
        String remaining = item.isInfinite() ? "inf" : String.valueOf(item.getRemainingUses());
        System.out.println("Item : " + item.getName() 
                      + " | Type : " + item.getObjectType()
                      + " | uses : " + remaining);
    }

    public void showUseResult(boolean ok){
        System.out.println(ok ? "Action effectuée" : "Plus d'utilisations");
    }

}

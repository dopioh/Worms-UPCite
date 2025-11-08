    package fr.upc.g5e4.inventory.model;

    public class ItemStack {
        private final Item item;
        private int quantity;
        private final boolean unlimited;
    
        public ItemStack(Item item, int quantity, boolean unlimited) {
            this.item = item;
            this.quantity = Math.max(0, quantity);  //Soit 0, Soit avec une quantité donnée
            this.unlimited = unlimited;
        }
    
        public Item getItem(){
            return item;
        }

        public boolean isUnlimited(){
            return unlimited;
        }
    
        public int getQuantity(){
            return quantity;
        }

        public boolean canUse(){
            return unlimited || quantity>0;
        }

        public void consumeOne(){
            if(!unlimited && quantity>0){
                quantity--;
            } 
        }
        
        @Override
        public String toString() {
            String rep = unlimited ? "∞" : String.valueOf(quantity);
            return item.name() + " — " + rep;
        }
    }

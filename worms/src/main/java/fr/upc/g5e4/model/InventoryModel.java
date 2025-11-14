package fr.upc.g5e4.model;

import com.google.gson.*;                 //Gson sert à faire un transfert de données "json to java"
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import java.util.List;

public class InventoryModel {

    public static class InventoryJson{        //DTO pour le fichier Json
        public String objectType, name;   
        public int maxUsages;             //Nombre d'utilisation max pendant le tour
        public boolean isInfinite;
        public Integer remainingUses;     //null si isinfinite

        public int useThisTurn = 0;

        @Override
        public String toString(){
            return name +"("+ objectType +")";
        }

        public boolean hasRemainingUses(){          //Auxiliaire de register use
            if(isInfinite) return true;
            if (remainingUses == null) return false;
            return remainingUses > 0;
        }

    
        public boolean canUseThisTurn(){
            if(useThisTurn>= maxUsages) return false;
            return hasRemainingUses();
        }

        public boolean registerUseThisTurn(){   
            if(!canUseThisTurn())return false;
            useThisTurn++;
            if(!isInfinite && remainingUses !=null) remainingUses--;
            return true;
        }

        public void resetTurn(){    //Nouveau tour
            useThisTurn = 0;
        }

        public int getRemainingUseThisTurn(){       
            return maxUsages - useThisTurn;
        }

        public int getMaxUsages(){
            return maxUsages;
        }
    }

    public static class InventoryRoot{        //DTO cela représente la racine du fichier Json
        public List<InventoryJson> inventory;  
    }

    //Lecture du Json depuis jsonPath
    public static InventoryRoot load(String jsonPath) throws IOException{      //IOException pour mieux gérer l'erreur                
        Gson gson = new Gson();
        String json = Files.readString(Path.of(jsonPath), StandardCharsets.UTF_8);    
        InventoryRoot root = gson.fromJson(json, InventoryRoot.class);
        return root;
    }
    
}

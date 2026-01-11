package fr.upc.g5e4.model;

import com.google.gson.*;                 //Gson sert à faire un transfert de données "json to java"
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import java.util.List;

/**
 * Modèle d'inventaire chargé depuis une ressource JSON du classpath
 *
 * Contient des DTO (Data Transfer Objects) destinés à mapper la structure JSON
 * 
 * {@link InventoryRoot} représente la racine du JSON
 * {@link InventoryJson} représente un objet d'inventaire (arme/item) et gère ses usages
 * @author Hugo
 * @version 1.0
 */

public class InventoryModel {

    /**
     * DTO correspondant à un objet dans l'inventaire (structure JSON).
     *
     * le nombre maximum d'utilisations par tour
     * le nombre d'utilisations restantes au total
     * le compteur d'utilisations dans le tour courant
     */
    public static class InventoryJson{       
        public String objectType, name;   
        public int maxUsages;             
        public boolean isInfinite;
        public Integer remainingUses;    
        public int useThisTurn = 0;

        /**
         * Représentation textuelle 
         *
         * @return une chaîne au format name(objectType)
         */        
        @Override
        public String toString(){
            return name +"("+ objectType +")";
        }

         /**
         * Indique s'il reste des utilisations disponibles au total
         *
         * @return true si infini ou remainingUses > 0, false sinon
         */
        public boolean hasRemainingUses(){     
            if(isInfinite) return true;
            if (remainingUses == null) return false;
            return remainingUses > 0;
        }

        /**
         * Indique si l'objet peut être utilisé pendant le tour courant :
         * 
         * le nombre max d'usages par tour n'est pas dépassé
         * il reste des utilisations totales (sauf infini)
         *
         * @return true si l'objet peut être utilisé, false sinon
         */
        public boolean canUseThisTurn(){
            if(useThisTurn>= maxUsages) return false;
            return hasRemainingUses();
        }

        /**
         * Enregistre une utilisation pendant le tour courant.
         * Incrémente {@link #useThisTurn} et décrémente {@link #remainingUses} si l'objet n'est pas infini.
         *
         * @return true si l'utilisation a été enregistrée, false sinon
         */
        public boolean registerUseThisTurn(){   
            if(!canUseThisTurn())return false;
            useThisTurn++;
            if(!isInfinite && remainingUses !=null) remainingUses--;
            return true;
        }

        /**
         * Réinitialise le compteur d'utilisations pour un nouveau tour.
         */
        public void resetTurn(){    
            useThisTurn = 0;
        }

        /**
         * Retourne le nombre d'utilisations encore possibles pendant le tour courant.
         *
         * @return maxUsages - useThisTurn
         */
        public int getRemainingUseThisTurn(){       
            return maxUsages - useThisTurn;
        }

        /**
         * Retourne le nombre maximum d'utilisations autorisées par tour.
         *
         * @return maxUsages
         */
        public int getMaxUsages(){
            return maxUsages;
        }
    }

    /**
     * DTO correspondant à la racine du fichier JSON.
     */
    public static class InventoryRoot{        
        public List<InventoryJson> inventory;  
    }

    /**
     * Charge un inventaire depuis une ressource JSON du classpath.
     *
     * @param resourcePath chemin de ressource 
     * @return la racine JSON mappée en {@link InventoryRoot}
     * @throws IOException si la ressource n'existe pas ou n'est pas lisible
     */
    public static InventoryRoot load(String resourcePath) throws IOException{             
        Gson gson = new Gson();
        try (InputStream is = InventoryModel.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Ressource introuvable: " + resourcePath);
            }
            String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            InventoryRoot root = gson.fromJson(json, InventoryRoot.class);
            return root;
        }
    }
    
}

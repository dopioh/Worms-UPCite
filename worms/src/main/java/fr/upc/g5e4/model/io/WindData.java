package fr.upc.g5e4.model.io;

import fr.upc.g5e4.model.*;
import fr.upc.g5e4.model.WindModel.Dir;

/**
 * Classe pour transformer un WindModel en WindData
 * Car certans attributs sont inutiles 
 * 
 * Pour plus de detail sur la comment fonctionne le vent 
 * @see WindModel
 * @see WindOverlay
 * 
 * @author Mei
 */
public class WindData {
    // WindModel : 

    // Dir :
    private int dx;
    private int dy;
    private String label;

    // Strength 
    private float strength;

    public WindData(int dx, int dy,String label, float strength){
        this.dx = dx;
        this.dy = dy;
        this.label = label;
        this.strength = strength;
    }

    /**
     * Methode static pour passer de WindModel a WindData
     * @param windModel
     * @return sa version minimal en Data
     */
    public static WindData WindModelToWindData(WindModel windModel) {
        Dir dir = windModel.getDir();
        return new WindData(dir.dx, dir. dy, dir.label, windModel.getStrength());
    }

    /**
     * Applique les donnees sauvegardees à un WindModel existant
     */
    public void applyToWindModel(WindModel windModel) {
        // retrouver la direction a partir du label
        Dir dir = findDirByLabel(this.label);
        if (dir != null) {
            windModel.setDir(dir);
        }
        windModel.setStrength(this.strength, true);
    }

    /**
     * Retourne une valeur de l'enum Dir à partir de son label
     */
    private Dir findDirByLabel(String label) {
        for (Dir d : Dir.values()) {
            if (d.label.equals(label)) {
                return d;
            }
        }
        // valeur par defaut si non trouve
        return Dir.E;
    }

    // Getters
    public int getDx() { 
        return dx; 
    }

    public int getDy() {
         return dy; 
        }

    public String getLabel() { 
        return label; 
    }
    
    public float getStrength() { 
        return strength; 
    }
}

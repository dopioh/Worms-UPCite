package fr.upc.g5e4.model;

public class SkinModel {

    private String skinName;
    private String filePath;

    private boolean unlocked;

    /**
     * Constructeur pour initialiser un skin avec son statut
     * @param filePath le chemin du skin
     * @param unlocked true si le joueur la debloquer, false sinon
     */
    public SkinModel(String skinName,String filePath, boolean unlocked){
        this.skinName = skinName;
        this.filePath = filePath;
        this.unlocked = unlocked;
    }

    public String getName(){
        return this.skinName;
    }

    public String getFilePath(){
        return this.filePath;
    }

    public boolean isUnlocked(){
        return this.unlocked;
    }
}

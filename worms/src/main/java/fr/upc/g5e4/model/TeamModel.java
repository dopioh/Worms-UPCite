package fr.upc.g5e4.model;

import org.jbox2d.dynamics.World;

import fr.upc.g5e4.model.io.*;

/**
 * Modèle pour une équipe de worms
 */
public class TeamModel {
    private int teamID; // ID de l'equipe, peut pas etre final sinon on peut pas load une partie
    private int number; // nbr de worms dans l'equipe
    private CharacterModel[] tabModelWorms;

    private static int TEAMSNUMBER = 0;
    
    /**
     * Constructeur de la classe TeamModel avec initialisation automatique des worms
     * @param groundModel Le modèle du terrain
     * @param world Le monde physique JBox2D
     * @param teamName Le nom de l'équipe
     * @param startX La position X de départ pour le premier worm
     * @param spacing L'espacement entre chaque worm
     * @param wormNumber Le nombre de worms dans l'équipe
     */
    public TeamModel(GroundModel groundModel, World world, String teamName, double startX, double spacing, int wormNumber, boolean console) {
        if (wormNumber < 1) {
            throw new IllegalArgumentException("Une équipe doit avoir au moins 1 worm");
        }
        this.number = wormNumber;
        this.tabModelWorms = new CharacterModel[number];

        TEAMSNUMBER++;
        this.teamID = TEAMSNUMBER;
        
        // Initialisation automatique des worms et position espacée
        for (int i = 0; i < number; i++) {
            double xPosition = startX + (i * spacing);
            double yPosition = groundModel.getHeight() - 500;
            
            String wormName = "Worm";
            
            tabModelWorms[i] = new CharacterModel(
                groundModel, 
                world, 
                wormName, 
                xPosition, 
                yPosition, 
                100.0,  // health
                50.0,   // shield
                15.0,   // speed
                5.0,     // jump
                console
            );
        }
    }
    
    // Getters et Setters   
    public CharacterModel[] getTabWorms() {
        return tabModelWorms;
    }

    public CharacterModel getWorm(int index) {
        if (index < 0 || index >= number) {
            throw new IndexOutOfBoundsException("Hors tableau: " + index);
        }
        return tabModelWorms[index];
    }

    public void removeWorm(int index) {
        if (index < 0 || index >= number) {
            throw new IndexOutOfBoundsException("Hors tableau: " + index);
        }
        tabModelWorms[index] = null;
    }

    public void addWorm(CharacterModel worm, int index) {
        if (index < 0 || index >= number) {
            throw new IndexOutOfBoundsException("Hors tableau: " + index);
        }
        if (tabModelWorms[index] != null) {
            throw new IllegalArgumentException("Worm déjà placé à l'index: " + index);
        }
        tabModelWorms[index] = worm;
    }
    
    // recuperer le nombre de worm a l'initialisation de la team
    public int getWormNumberAtStart() {
        return number;
    }

    public int getAliveWormNumber() {
        int aliveCount = 0;
        for (CharacterModel worm : tabModelWorms) {
            if (worm != null && !worm.isDead()) {
                aliveCount++;
            }
        }
        return aliveCount;
    }


    // Getter Data
    public TeamData toData(){
        return new TeamData(teamID, number, tabModelWorms);
    }

    // Setter Data
    public void loadFromData(TeamData teamData){
        this.teamID = teamData.getTeamID();
        this.number = teamData.getWormsNumber();
    
    }

    public CharacterModel[] dataToModel(CharacterData[] dataTabCharacter){
        CharacterModel[] result = new CharacterModel[dataTabCharacter.length];
        for (int i = 0 ; i < result.length ; i++){
            result[i] = new CharacterModel(null, null, i, i, false);
        }
        return result;
    }

    
}

package fr.upc.g5e4.model.io;

import java.util.ArrayList;

import fr.upc.g5e4.model.SkinModel;

/* On va save ici les equipes personnaliser 
    -> save les equipes grace a TeamData qui ressemble a un TeamModel
        ou faire un nv TeamInitData ? -> pour attribuer correctement la vue aussi

    -> Il faudra un fichier SaveLoad aussi pour generer une CustomTeamsaveData a partir d'un json
        -> SaveLoad sera appeler dans une classe pour initialiser les settings de l'equipe

    -> la save des equipes sera stocke dans ./sauvegardes et s'appelera sauvegardeCustomTeam.txt

    Y'a besoin d'une classe CustomTeamSaveData ? Oui pour le nbr d'equipes et les stocker dans des listes

    CustomTeamData :
    Nom d'equipe 
    skin pour tous les worms 

    -> On peut personnaliser jusqu'a 6 Worms, 
    si on decide de faire des equipes de plus de 6, les worms vont boucler
 */

/**
* Classe stocker une equipe personnaliser
*/
public class CustomTeamData {

    private String teamName;

    private SkinModel skin;

    // couleur equipe

    private int nbrWorms;

    // TeamData ICI mais faut personnaliser TeamModel et TeamInitialisation
    // Pas forcement ?
    // On stocke juste les donnees que on veut ici 
    // Que on fou dans un json
    // et ca sera assembler quand on va charger une game 

    private ArrayList<String> WormsName;


}

package fr.upc.g5e4.model;
import fr.upc.g5e4.view.TutorialView;

/**
 * Model pour le tutoriel
 * @author Nassim
 */
public class TutorialModel {
    
    private int currentPage = 0;
    private final String[][] tutorialPages;
    private TutorialView view;
    
    public TutorialModel() {
        // les pages c'est un tableau 2D de string
        tutorialPages = new String[][] {
            {
                "COMMENT JOUER ?",
                "Worm est un jeu de stratégie au tour par tour. Chaque joueur contrôle une équipe de",
                "petits vers de terre. L'objectif est d'éliminer tous les vers adverses en utilisant ses",
                "propres vers et une multitude d'armes et d'objets spéciaux !",
                "",
                "Le jeu se déroule en tour de 30 secondes dans lesquels un joueur contrôle un worm.",
                "Durant un tour, le worm peut choisir une arme pour tirer ainsi que 2 objets spéciaux.",
                "",
                "Les touches clavier :",
                "Q et D : pour déplacer le worm à gauche et à droite",
                "Z : pour sauter",
            },
            {
                "INFORMATIONS IMPORTANTES",
                "• Attention terrain destructible ! Plus la case de terrain est claire plus elle est fragile",
                "• Un worm ne peut jouer qu'une seule arme et deux objets maximum par tour",
                "• Le worm peut cumuler deux mêmes objets, les effets seront doublés mais la durée",
                "ne sera pas augmentée",
                "• Les munitions ne sont pas illimitées et chaque arme a un nombre de munition limité à",
                "chaque tour (EX : Bazooka a 2 munitions par equipe mais seulement 1 munition par tour)",
                "• Tomber dans l'eau trop profondement entraîne la mort immédiate du worm",
                "• Chaque equipe en vie gagne 5$ à la fin de son tour",
                "• Le vent influence la trajectoire des projectiles (indiqué en bas à droite de l'écran)", 
                "• Chaque skin acheté dans la boutique est permanent et peut être utilisé à chaque partie",
                
            },
            {
                "Arsenal et objets spéciaux",
                "• Bazooka : Arme de base, inflige de gros dégâts aux worms et au terrain",
                "• Grenade : Explose, inflige des dégâts et a une courte portée",
                "• Ak47 : Fait plusieurs petits tirs rapides",
                "• Bat de baseball : Arme de mêlée, inflige des dégâts modérés au corps à corps",
                "• Drill : Permet de creuser (clique gauche) et de construire (clique droit) des blocks sur le terrain",
                "• TP : Permet de se teleporter en cliquant sur un endroit de la map",
                "",
                "• Potion PV : Restaure des points de vie sur la durée",
                "• Potion Bouclier : Réduit les dégâts subis pendant une durée limitée",
                "• Potion Vitesse : Augmente la vitesse de déplacement pendant une durée limitée",
                "• Potion Saut : Augmente la hauteur de saut pendant une durée limitée",
            },
            {
                "CREDITS",
                "Ce jeu a été développé par le groupe G5E4 dont les membres sont : ",
                "• MILITON Mei",
                "• MAZRI Nassim",
                "• WU Billy",
                "• DESROQUES Ian",
                "• SHAN Hugo",
                "• ROSE Raphael",
            }
        };
    }
    
    public void setView(TutorialView view) {
        this.view = view;
    }
    
    /**
     * page suivante
     * @return true si changement effectué
     */
    public boolean nextPage() {
        if (currentPage < tutorialPages.length - 1) {
            currentPage++;
            if (view != null) {
                view.repaint();
            }
            return true;
        }
        return false;
    }
    
    /**
     * page précédente
     * @return true si changement effectué
     */
    public boolean previousPage() {
        if (currentPage > 0) {
            currentPage--;
            if (view != null) {
                view.repaint();
            }
            return true;
        }
        return false;
    }
    
    // Reset la pagination a la premiere page
    public void reset() {
        currentPage = 0;
        if (view != null) {
            view.repaint();
        }
    }
    
    // Getters
    public int getCurrentPage() {
        return currentPage;
    }
    
    public String[] getCurrentPageContent() {
        return tutorialPages[currentPage];
    }
    
    public int getTotalPages() {
        return tutorialPages.length;
    }
    
    public boolean hasNextPage() {
        return currentPage < tutorialPages.length - 1;
    }
    
    public boolean hasPreviousPage() {
        return currentPage > 0;
    }
}

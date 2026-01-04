
# Changelog

## v.0.0 Configuration (Avant le premier sprint)

### Added

- Création de la base Maven
- CONTRIBUTING.md
- .gitignore
- LICENSE (MIT)
- gitlab-ci.yml
- DESIGN.md

## [v.0.1 : SPRINT 1](https://gitlab.informatique.u-paris.fr/militon/projet-worms/-/milestones/1)

### Added

- Format **MVC**
- Tests unitaires pour une grande partie des classes
- La physique du jeu (gravité et eau qui détruit le worms)
- GameManager, GameLoop et GameLauncher pour lancer une partie
    - GameManager : génère tous les éléments nécessaires (physique, perso, etc.)
    - GameLoop : Timer principal du jeu (60 fps)
    - GameLauncher : initialise GameManager et GameLoop
- Un Worms (Character, carré rouge initialement)
    - Peut se déplacer et sauter
    - Intégré à la physique du monde
- Classe Item
    - Weapons : AK-47, Grenade, Rocket Launcher, Baseball Bat
    - Gadget : Bumper
- Classe Inventaire
- Classe Donnée Joueur
    - Système de sauvegarde (JSON)
- Classe Camera

### Configuration

- Passage du projet sur **Java 23** (anciennement Java 17)
- Modification de pom.xml
    - Ajout de plugins et de librairies
- Modification de gitlab-ci.yml
    - Ajout de la phase `deploy` qui génère un .jar


## [v.0.2 : SPRINT 2](https://gitlab.informatique.u-paris.fr/militon/projet-worms/-/milestones/2)

### Added
- Écran d'accueil
- Equipes de worms (equipes variables : nb worm / couleurs worms)
    - Chaque worm d'une equipe est jouable, on switch de worm en appuyant sur `A`
- tire et dégâts
    - les vers peuvent désormais tirer et infliger des dégâts
        - Détection de la souris pour viser
        - click gauche pour tirer
        - Touche e, pour changer d'arme
- Raccourcis
    - `Esc` pour quitter le jeu
    - `I` pour ouvrir l’inventaire
- vue console : on peut désormais lancer le jeu en mode console
- JaCoCo et de Mockito
- Mise en place d’un inventaire consultable en jeu

### Modified
- Vitesse et du saut
    - Ajustement des valeurs de vitesse de déplacement et de saut pour équilibrer le gameplay
- Camera
    - Les déplacements, le zoom et le recentrage de la caméra se font maintenant avec un glissement fluide

## [v.0.3 : SPRINT 3](https://gitlab.informatique.u-paris.fr/militon/projet-worms/-/milestones/3)

### Added

- Mode plein écran (16:9)
- MAP procédurale avec bruit de Perlin
- Sprites pour les worms via le constructeur d'équipe
- Sauvegarde partielle
    - Enregistrement dans `sauvegarde.txt` (JSON) avec la touche `J`
- Système de visée amélioré avec arc de cercle
- Liaison tir & armes
    - L'angle de l'arme suit la position de la souris
    - Balles physiques
- Effets sonores pour les armes
- Désactivation des collisions entre worms
- Vent avec affichage graphique
- Documentation
    - Fichier `ARCHITECTURE.md`
    - Amélioration du `CONTRIBUTING.md`
- Constructeurs `TeamView` et `CharacterView` avec chemin vers sprite
- Fonction `loadSprite` avec gestion d'erreurs
- Affichage du sprite dans la bonne direction

### Modified

- Focus caméra sur le personnage actif
- Système de rounds pour N équipes (au lieu de 2)
- Structure du projet
    - Séparation du `GameLauncher` et des Overlays
    - Séparation du JFrame
    - Suppression de `Entity.java` obsolète

### Fixed

- `.jar` injouable à cause du chargement de `inventory.json`
- Suppression complète du worm mort (sprite + body physique)


## [v.0.4 : SPRINT 4](https://gitlab.informatique.u-paris.fr/militon/projet-worms/-/milestones/4)

### Added

- Cinématique d'introduction (5 secondes)
    - Affichage des équipes et nombre de worms
    - Présentation du terrain et conditions météo
    - Effets de fade et transitions fluides
- Nametag
    - Affichage des items (potions, etc.)
- Nouveau système de map (tableau 2D de blocks)
- Écran de fin de partie
    - Animation des worms gagnants vers le trophée
    - Statistiques de jeu (dégâts, précision, éliminations, rounds)
    - Particules et effets visuels
    - Bouton retour au menu
    - Support N équipes et plein écran
    - Stat box scrollable
- Système de statistiques pendant la partie
- Paramètres du jeu (`current_skin`)
- Chargement des sauvegardes depuis le menu ("Poursuivre une partie")
- Tir/dégâts dans la version Console

### Modified

- Support pour N équipes (au lieu de 2 hardcodées)
    - Refactorisation GameManager, GameLoop et GamePanel avec listes dynamiques
- Nametag : HP et Shield en barres horizontales (au lieu de chiffres)
- `CONTRIBUTING.md` : Guide nametag
- Touche plein écran : `F` (au lieu de F11)
- Physique de recul sur les worms touchés

### Fixed

- Tout le jeu en une seule fenêtre
- Emplacement overlay vent
- Système de map avec tableau 2D
- N équipes fonctionnelles
- Retour aux mouvements originaux et du saut (`Z`)
- Initialisation overlay Inventaire dans GameManager

### Optimisation

- Performance
    - Centralisation repaint dans un seul Timer
    - Pré-création d'objets graphiques réutilisables
- Lag Audio : Système de cache avec pré-chargement


## [v.0.5 : SPRINT 5](https://gitlab.informatique.u-paris.fr/militon/projet-worms/-/milestones/5) (unreleased)

### Added

- Algorithme FloodFill pour supprimer les blocks non liés au sol
- Fixture unique par block
- Boutique
- Armes/Items graphiquement attachés au worm
- Sons pour chaque arme
- Plusieurs buffs (Speed, jump)
- Potion (Speed, jump, heal, shield)
- sprites pour gagdets 
- sons pour les gadgets
- Degats de zone 
- Readme.md expliquant le projet (comment jouer etc)
- Ajout d’un pointeur graphique (flèche rouge) au-dessus du joueur actif dans la vue GamePanel pour indiquer visuellement le worm en cours de jeu.

### Fixed

- Double tir
- Fix tir lié au zoom
- body des blocks invisible après destruction
- l'image de fond dans main menu qui ne redimentionne pas bien lors du passage en full screen sur un grand ecran
- fix `player_save.txt` quand le dossier `sauvegardes` n'existe pas
- fix save pour ajouter les buffs

### Modified

- Suppression des fleches haut et bas, pour une trajectoire seulement avec la souris
- Maj du DIAGRAMME.MD
- Maj du ARCHITECTURE.md
- Amélioration de CHANGELOG.md
- Remonte le niveau de l'eau 
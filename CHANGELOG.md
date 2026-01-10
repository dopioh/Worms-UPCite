# Changelog

## v.0.0 Configuration (Avant le premier sprint)
---

### Added

- Création de la base Maven

### Added
- CONTRIBUTING.md
- .gitignore
- LICENSE (MIT)
- gitlab-ci.yml
- DESIGN.md


## [v.0.1 : SPRINT 1](https://gitlab.informatique.u-paris.fr/militon/projet-worms/-/milestones/1)
---

### Added Java

- format **MVC** 
- Des tests unitaires pour une grande partie des classes  
- La physique du jeu (Gravite et eau qui detruit le worms)  
- GameManager, GameLoop et GameLauncher initialiser pour lancer une partie  
    - GameManager : genere tous les elements que necessite une partie (physique, perso etc)  
    - GameLoop : Timer principale du jeu (60 fps) pour faire avancer la partie  
    - GameLauncher : initialise GameManager et GameLoop pour lancer une partie (avec ou sans fenetre)  
- Un Worms (pour le moment Character, est un carre rouge)  
    - Peut se deplacer et sauter
    - Est integrer a la physique du monde
- Classe Item
    - Weapon (AK-47, Grenade, Rocket Launcher, Baseball Bat)
    - Gadget (Bumper)
- Classe inventaire
    - InventoryJson
    - JDialog pour agir avec les armes et gadget selon les données de InventoryJson 
- Classe Donnée Joueur
    - Système de sauvegarde des données (JSON)
- Classe Camera

### Added/Modified Configuration

- Passage du projet sur **Java 23** (anciennement Java 17)  
 - Modification de pom.xml  
    - Ajout de plugin et de librairies  
- Modification de gitlab-ci.yml  
    - Ajout de la phase `deploy`, qui genere un .jar


## [v.0.2 : SPRINT 2](https://gitlab.informatique.u-paris.fr/militon/projet-worms/-/milestones/2)
---

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
- Inventaire 
    - L'inventaire passe en JComponent
    - Le worm est textuellement équipé 

## [v.0.3 : SPRINT 3](https://gitlab.informatique.u-paris.fr/militon/projet-worms/-/milestones/3)
---

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
- SFX bruit de Tir
- Vent

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
---

### Added

- Cinématique d'introduction (5 secondes)
    - Affichage des équipes et nombre de worms
    - Présentation du terrain et conditions météo
    - Effets de fade et transitions fluides
- Nametag
    - Affichage de HP et Shield en chiffre -> barre horizontale
- `CONTRIBUTING.md`
    - Guide d'utilisation sur le nametag
- Touche full screen F11 -> F
- Implémentation de la physique de recul sur les worms touchés
- Support pour N équipes au lieu de 2 équipes hardcodées
    - Refactorisation de GameManager, GameLoop et GamePanel pour utiliser des listes dynamiques
    - Possibilité d'ajouter dynamiquement des équipes via GameManager.addTeam()
- Overlay HUD,Pause,Help

### Fixed
- Tout le jeu est désormais en une seule fenêtre
- Emplacement overlay vent
- Nouveau systeme de map 
    - MAP en tableau 2D remplie d'objet "block"
- **N equipes** fonctionnelles desormais (2 auparavant)
- Retour aux mouvements originaux
- Retour du saut (avec Z)
- Initialisation de l'overlay Inventaire decaler dans GameManager (precedemment GameLauncher)

### Optimisation

- Performance
    - Centralisation de repaint dans un seul Timer (gameLoop) pour avoir un seul repaint()
    - Pré-création d'objets graphiques réutilisables (couleurs, strokes) pour réduire les allocations mémoire
- Lag Audio : Système de cache avec pré-chargement des sons en mémoire


## [v.0.5 : SPRINT 5](https://gitlab.informatique.u-paris.fr/militon/projet-worms/-/milestones/5) (unreleased)

### Added

- **Page réglages**
    - Système d'onglets : Map, Équipes, Autre
    - Support du plein écran avec scaling automatique
    - Bouton retour vers le menu principal
- **Composant PillButton**
    - Bouton arrondi stylisée avec effet hover et plus
- **Composant PillComboBox**
    - Liste déroulante stylisée
- **Onglet Map dans les réglages**
    - Prévisualisation en temps réel de la map
- **Composant PillTextField**
    - Entrée de texte qui suit la DA
- **Customisation d'équipe**
    - Se save dans `player_save.txt` 
    - Choix nom d'équipe, skin de l'équipe (parmi ceux débloqués) et nom des 6 worms de l'équipe
- **Onglet Équipe**
    - À Gauche :  
        - Choix du nombre d'équipes, de worms et attribution des CustomTeams pour la prochaine partie
        - On peut lancer une game sans configurer (réglages des équipes par défaut)
    - À Droite :  
        - Système de customisation d'équipe
- **Skins**
    - Ajout des 5 skins (hors wormPixel) débloquables
- Algorithme FloodFill pour supprimer les blocks non liés au sol
- Fixture unique par block
- Boutique
- Armes/Items graphiquement attachés au worm
- Sons pour chaque arme
- Plusieurs buffs (Speed, jump)
- Potion (Speed, jump, heal, shield)
- Sprites pour gadgets 
- Sons pour les gadgets
- Dégâts de zone 
- Readme.md expliquant le projet (comment jouer etc)
- Ajout d'un pointeur graphique (flèche rouge) au-dessus du joueur actif dans la vue GamePanel pour indiquer visuellement le worm en cours de jeu
- Background avec effet parallax
    - Choix dans paramètre onglet map
- AUTHORS.md
- CONTRIBUTING.MD
- DESIGN.MD
- Système d'images en cache pour optimiser
- Animation de marche pour les worms (rotation)
- **Arbres**
    - Disparition automatique des décos quand le sol en dessous est détruit

### Modified

- Suppression de tous les raccourcis Clavier
    - Remplacé par des boutons dans paramètres (Fonctionnalités Fly/BluePrint/FullScreen)
    - Remplacé par le scroll souris (Zoom)
- **Système de plein écran**
    - Passage du mode `setFullScreenWindow` au plein écran simulé
    - Gestion du notch macOS et du dock
- Suppression des flèches haut et bas, pour une trajectoire seulement avec la souris
- Maj du DIAGRAMME.MD
- Maj du ARCHITECTURE.md
- Amélioration de CHANGELOG.md
- Remonte le niveau de l'eau
- Couleur de l'eau
- Taille des blocks
- **Arriere plan de la map**
    - ajustement pour qu'il a un peu moins de presence pour mettre en valeur plus les worms et terrain que l'arriere plan
- **Animation**
    - ajustement de l'animation walk pour rendre un peu moins agressive
- Création d'équipe avec un nom existant ou nom vide maintenant bloquée
- Confirmation de création d'équipe avec un popup

### Fixed

- Double tir
- Fix tir lié au zoom
- Body des blocks invisible après destruction
- L'image de fond dans main menu qui ne redimensionne pas bien lors du passage en full screen sur un grand écran
- Fix `player_save.txt` quand le dossier `sauvegardes` n'existe pas
- Fix save pour ajouter les buffs
- JComboBox invisibles en plein écran
- Problème du notch et menu macOS sur le contenu en plein écran
- Image de prévisualisation pas mise à jour au retour dans les settings
- Coins pixelisés sur les images arrondies
- Suppression de touche Echap dans main menu
- Adaptation des Tests pour le nouveau système d'équipe
- Certaines images chargeaient deux fois dans le cache
- **full screen**
    - Menu Réglage
    - Titre main menu
- Des sons jouée pendant les test
- **Écran de fin de partie**
    - Stats par ID d'équipe au lieu du nom
    - Noms d'équipes par défaut : Team 1, Team 2...
    - Comptage correct des rounds dans les stats
    - Kills par rocket launcher maintenant comptabilisés dans stats
    - Précision limitée à 100% max (un tir = un hit même en zone)
- Systeme de detection des worms sous la map

### Configuration

- Ajout de support Java 24 et 25
    - JaCoCo : 0.8.12 → 0.8.13
    - Mockito : 5.14.2 → 5.21.0

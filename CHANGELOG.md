
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

### Fixed
- `.jar` injouable

--------- a completer ----------

## [v.0.4 : SPRINT 4](https://gitlab.informatique.u-paris.fr/militon/projet-worms/-/milestones/4) 
---

### Added
- Nametag
    - Affichage des itemes (potions...) sur le nametag
- Nouveau systeme de map 
    - MAP en tableau 2D remplie d'objet "block"
- **Cinématique d'introduction** : Intro cinématique avec barre de chargement cartoon et messages    synchronisés
- Ajout du fichier LoadingBarPanel.java
- **Écran de fin de partie avec animation**
  - Animation des worms gagnants marchant vers le trophée
  - Affichage des statistiques de jeu (dégâts, précision de tir, éliminations, rounds joué)
  - Particules et effets visuels
  - Bouton retour au menu
  - Support du plein écran
  - Support du N equipe
  - Stat box glissable avec scroll
- Système de statistiques pour tracker des actions pendant la partie
- Endroit pour stocker les paramètres du jeu
    - `current_skin`
- Ajout Sprite `wormPixel.png` dans `ressources`
- Une sauvegarde peut desormais etre lancer depuis le menu d'acceuil en cliquant sur "Poursuivre une partie"
- Possibilitee de tirer/faire des degats/tuer un worm dans la version Console du jeu (tire assez limiter)
- **Diagramme de classes** : Ajout et documentation du diagramme de classes du projet (GameManager, GameLoop, GamePanel, etc.)
- **Système de configuration de terrains** : Nouvelle classe MapConfig pour gérer les types et tailles de maps
    - 4 types de terrains disponibles :
        - STANDARD : Terrain avec plateformes de spawn (mode original)
        - MOUNTAINS : Hautes montagnes avec relief prononcé
        - ISLANDS : Terrain wavy avec zones d'eau au centre pour éviter les spawn kills
        - FLAT : Terrain complètement plat
    - 3 tailles de maps : SMALL (800x720), MEDIUM (1280x720), LARGE (1700x720)
- Ajout de la dégradation de la map
- Ajout d'une courbe pour la trajectoire de la map
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
- **Performance**
    - Centralisation de repaint dans un seul Timer (gameLoop) pour avoir un seul repaint()
    - pré-création d'objets graphiques réutilisables (couleurs, strokes) pour réduire les allocations mémoire pour chaque cube
- **Lag Audio** : Système de cache avec pré-chargement des sons en mémoire pour éliminer le lag au premier tir


## [v.0.5 : SPRINT 5](https://gitlab.informatique.u-paris.fr/militon/projet-worms/-/milestones/5) (unreleased)
---

### Added
- **Page reglages**
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
    - Entree de texte qui suit la DA
- **Customisation d'equipe**
    - Se save dans `player_save.txt` 
    - Choix nom d'equipe, skin de l'equipe (parmi ceux debloquer) et nom des 6 worms de l'equipe
- **Onglet Equipe**
    A Gauche :  
    - Choix du nombre d'equipe, de worms et attribution des CustomTeams pour la prochaine partie
    - on peut lancer une game sans configurer (reglages des equipes par defaut)
    A Droite :  
    - Systeme de customisation d'equipe
- **Skins**
    - Ajout des 5 skins (hors wormPixel) debloquable


### Modified
- **Système de plein écran**
    - Passage du mode `setFullScreenWindow` au plein écran simulé
    - Gestion du notch macOS et du dock

### Fixed
- JComboBox invisibles en plein écran
- Problème du notch et menu macOS sur le contenu en plein ecran
- Image de prévisualisation pas mise à jour au retour dans les settings
- Coins pixelisés sur les images arrondies
- Suppression de touche Echap dans main menu
- Adaptation des Test pour le nouveau systeme d'equipe


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

### Fixed

- Double tir
- Fix tir lié au zoom
- body des blocks invisible après destruction
- l'image de fond dans main menu qui ne redimentionne pas bien lors du passage en full screen sur un grand ecran

### Modified

- Suppression des fleches haut et bas, pour une trajectoire seulement avec la souris
- Maj du DIAGRAMME.MD
- Maj du ARCHITECTURE.md
- Amélioration de CHANGELOG.md

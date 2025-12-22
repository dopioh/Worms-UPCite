# Changelog

## v.0.0 Configuration : (Avant le premier sprint)
---

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

## [v.0.3 : SPRINT 3](https://gitlab.informatique.u-paris.fr/militon/projet-worms/-/milestones/3)
---

### Added
- Mode plein ecran
    - passage du jeu en 16:9
- MAP Procédurale 
    - avec bruit de perlin et amplification des bruits
- SPRITES 
    - les worms ont des sprites via le constucteur de leur equipe
- Comportement worm
    - le worm est totalement supprimé de la map quand il meurt (sprite + body physique) 
- Sauvegarde
    - une partie peut etre enregistree dans un fichier `sauvegarde.txt` en json avec la touche **_J_**

### Fixed
- `.jar` injouable

--------- a completer ----------

## [v.0.4 : SPRINT 4](https://gitlab.informatique.u-paris.fr/militon/projet-worms/-/milestones/4) (unreleased)
---

### Added
- Nametag
    - Affichage des itemes (potions...) sur le nametag
- Nouveau systeme de map 
    - MAP en tableau 2D remplie d'objet "block"

### Modified
- Nametag
    - Affichage de HP et Shield en chiffre -> barre horizontale
- `CONTRIBUTING.md`
    - Guide d'utilisation sur le nametag
- Touche full screen F11 -> F
- Implémentation de la physique de recul sur les worms touchés

### Fixed
- Tout le jeu est désormais en une seule fenêtre
- Emplacement overlay vent
- Nouveau systeme de map 
    - MAP en tableau 2D remplie d'objet "block"
- **N equipes** fonctionnelles desormais (2 auparavant)

### Optimisation
- **Performance**
    - Suppression du double `repaint()` et pré-création d'objets graphiques réutilisables (couleurs, strokes) pour réduire les allocations mémoire
    - Pré-création d'objets graphiques réutilisables
- **Lag Audio** : Système de cache avec pré-chargement des sons en mémoire pour éliminer le lag au premier tir

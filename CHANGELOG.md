
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
- **Cinématique d'introduction** : Animation de présentation avant le début du combat
    - Affichage des équipes participantes avec nombre de worms
    - Présentation du terrain généré procéduralement
    - Affichage des conditions météorologiques (vent)
    - Animation avec effets de fade et transitions fluides
    - Durée de 5 secondes avant lancement automatique du jeu
- Nametag
    - Affichage des itemes (potions...) sur le nametag
- Nouveau systeme de map 
    - MAP en tableau 2D remplie d'objet "block"
- **Cinématique d'introduction** : Animation de présentation avant le début du combat
    - Affichage des équipes participantes avec nombre de worms
    - Présentation du terrain généré procéduralement
    - Affichage des conditions météorologiques (vent)
    - Animation avec effets de fade et transitions fluides
    - Durée de 5 secondes avant lancement automatique du jeu
- Écran de fin de partie avec animation
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
- Une sauvegarde peut desormais etre lancer depuis le menu d'acceuil en cliquant sur "Poursuivre une partie"
- Possibilitee de tirer/faire des degats/tuer un worm dans la version Console du jeu (tire assez limiter)

### Modified
- Support pour N équipes au lieu de 2 équipes hardcodées
    - Refactorisation de GameManager, GameLoop et GamePanel pour utiliser des listes dynamiques
    - Possibilité d'ajouter dynamiquement des équipes via GameManager.addTeam()
- Nametag
    - Affichage de HP et Shield en chiffre -> barre horizontale
- `CONTRIBUTING.md`
    - Guide d'utilisation sur le nametag
- Touche full screen F11 -> F
- Implémentation de la physique de recul sur les worms touchés
- Support pour N équipes au lieu de 2 équipes hardcodées
    - Refactorisation de GameManager, GameLoop et GamePanel pour utiliser des listes dynamiques
    - Possibilité d'ajouter dynamiquement des équipes via GameManager.addTeam()

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
- **Performance**
    - Centralisation de repaint dans un seul Timer (gameLoop) pour avoir un seul repaint()
    - pré-création d'objets graphiques réutilisables (couleurs, strokes) pour réduire les allocations mémoire pour chaque cube
- **Lag Audio** : Système de cache avec pré-chargement des sons en mémoire pour éliminer le lag au premier tir


## [v.0.5 : SPRINT 5](https://gitlab.informatique.u-paris.fr/militon/projet-worms/-/milestones/5) (unreleased)
---

### Added
- Algorithme FloodFill
    - permet de supprimer les blocks qui ne sont pas liés au sol
- Fixture unique par block
- Boutique
- Arme/Item graphiquement attachés au worm
- Sons pour chaque arme

### Fixed
- body des blocks invisible après destruction


### Added
- Plusieurs buffs (Speed, jump)
- Potion (Speed, jump, heal, shield)
- sprites pour gagdets 
- sons pour les gadgets

### Fixed
- Double tir

### Changed 
- Suppression des fleches haut et bas, pour une trajectoire seulement avec la souris


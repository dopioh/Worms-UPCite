# Changelog

## v.0.1 Configuration : (Avant le premier sprint)

- Création de la base (Maven)

### Added
- CONTRIBUTING.md
- .gitignore
- LICENSE (MIT)
- gitlab-ci.yml
- DESIGN.md

## v.0.1 : SPRINT 1

### Added Java <!-- Remplir si vous voulez avant et pdt la reunion du TP de fin de SPRINT 1 -->

- Ajout format **MVC**
- Ajout des tests unitaires pour une grande partie des classes  
- Ajout de la physique du jeu (Gravite et eau qui detruit le worms)  
- GameManager, GameLoop et GameLauncher initialiser pour lancer une partie  
    - GameManager : genere tous les elements que necessite une partie (physique, perso etc)  
    - GameLoop : Timer principale du jeu (60 fps) pour faire avancer la partie  
    - GameLauncher : initialise GameManager et GameLoop pour lancer une partie (avec ou sans fenetre)  
- Ajout d'un Worms (pour le moment Character, est un carre rouge)  
    - Peut se deplacer et sauter
    - Est integrer a la physique du monde


... a completer avec les armes etc

### Added/Modified Configuration

- Passage du projet sur **Java 23** (anciennement Java 17)  
 - Modification de pom.xml  
    - Ajout de plugin et de librairies  
- Modification de gitlab-ci.yml  
    - Ajout de la phase `deploy`, qui genere un .jar




## v.0.2 : SPRINT 2
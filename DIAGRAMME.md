# Spécifications Techniques : Architecture Projet Worms

Ce document décrit la structure logicielle du projet, basée sur un découpage MVC strict et l'intégration du moteur physique JBox2D.

---

## 1. Noyau du Système (Core)

L'exécution repose sur la synchronisation entre la logique métier et le moteur physique.

* **GameLauncher** : Point d'entrée principal. Responsable de l'injection de dépendances et de l'initialisation du `GameManager`.
* **GameLoop** : Thread cadencé à 16ms (~60 FPS). Elle orchestre les cycles `update()` (logique) et `repaint()` (rendu).
* **GameManager** : Pivot central. Gère le cycle de vie des rounds, le switch d'équipes et la coordination entre les contrôleurs et le `PhysicsWorld`.

---

## 2. Implémentation du Pattern MVC

### Modèles (State & Logic)
Le package `models/` encapsule l'état du jeu et les règles physiques.

* **Gestion des Équipes** : 
    * `TeamModel` : Conteneur des données d'équipe (couleurs, liste de worms).
    * `TeamInitialisation` : Logique de déploiement et de configuration initiale des escouades.
* **Entités & Physique** :
    * `CharacterModel` : État dynamique d'un Worm (HP, inventaire).
    * `PhysicsWorld` : Instance JBox2D gérant les corps physiques et la gravité.
    * `WindModel` : Influence les trajectoires balistiques des projectiles.
    * `WaterSensorListener` : Détecte l'immersion des corps via des capteurs physiques (JBox2D Sensors).
* **Système d'Items** :
    * `Weapon` : Classe abstraite étendue par `Ak47`, `RocketLauncher`, `Grenade`, `BaseballBat`.
    * `Gadget` : Items utilitaires (ex: `Bumper`).
    * `Projectile` : Entité physique autonome générée lors d'un tir.
* **Données & Stats** :
    * `PlayerData` / `GameStats` : Persistance des profils et calcul des performances en fin de match (Kills, dégâts cumulés).
* **Terrain** :
    * `GroundModel` / `BlockGridModel` : Représentation matricielle de la carte.
    * `TerrainGenerator` / `PerlinNoise` : Algorithmes de génération procédurale.

### Vues (UI & Rendering)
Le package `views/` gère l'affichage via Graphics2D et des composants Swing personnalisés.

* **États de Jeu** :
    * `MainMenuView` : Interface d'accueil.
    * `GamePanel` : Canvas principal de la simulation.
    * `EndGameView` : Écran de résultats (score final et statistiques).
* **Composants graphiques** :
    * `BlockGridView` : Rendu optimisé de la grille de terrain.
    * `NameTagView` : Overlay affichant les noms et barres de vie au-dessus des entités.
    * `CharacterView` / `ProjectileView` : Gestion des sprites et des animations de particules.
    * `TrajectoryRenderer` : Tracé prédictif de la trajectoire avant le tir.
    * `RoundedButton` : Composant UI étendu pour l'esthétique des menus.

### Contrôleurs (Coordination)
Le package `controllers/` fait le pont entre les inputs et les modèles.

* **Gameplay** :
    * `CharacterController` : Mouvements, sauts et visée.
    * `ItemController` : Gestion de l'inventaire et activation des armes/gadgets.
    * `CameraController` : Gestion du Smooth Follow (Lerp) et du zoom sur l'action.
* **Navigation** :
    * `MainMenuController` / `EndGameController` : Logique de transition entre les écrans.
* **Inputs** :
    * `SwingInputController` : Centralisation des Key/Mouse Listeners.

---

## 3. Mode Console (Debug & Headless)

Pour le développement et les tests unitaires de la logique métier, une branche `console/` permet de faire tourner le jeu sans l'overhead graphique.

* **Composants** : `ConsoleGameManager`, `ConsoleInputController`, `ConsoleLogics`.
* **Usage** : Permet de simuler des tours de jeu et de valider les calculs de dégâts/mouvements via des flux textes.

---

## 4. Liaisons et Flux de données

1.  **Entrée** : Le `SwingInputController` capte un événement.
2.  **Traitement** : Le `CharacterController` applique une force (`applyLinearImpulse`) au corps JBox2D associé dans le `PhysicsWorld`.
3.  **Simulation** : JBox2D résout les collisions (via `WaterSensorListener` ou `ContactListener`).
4.  **Mise à jour** : Le `GameManager` vérifie les conditions de victoire via `GameStats`.
5.  **Rendu** : Le `GamePanel` itère sur les vues (`CharacterView`, `BlockGridView`) pour rafraîchir l'écran.



---
**Utilitaires :**
- `FileUtile` : Gestion des entrées/sorties (sauvegardes, chargement de assets).
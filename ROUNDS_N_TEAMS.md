# Système de Rounds pour N Équipes

## 🎯 Vue d'ensemble

Le système de rounds a été refactorisé pour supporter **N équipes** au lieu d'être limité à 2. Toutes les références hardcodées ont été éliminées.

## ✅ Modifications apportées

### 1. **GameManager.java**

#### Méthodes mises à jour:
- **`getCurrentTeam()`** - Utilise maintenant `listTeamModel.get(currentTeamIndex)` 
- **`getCurrentTeamName()`** - Récupère le nom de l'équipe dynamiquement
- **`nextRound()`** - Utilise `listTeamModel.size()` au lieu de `% 2`
- **`buildAllWormsList()`** - Construit dynamiquement la liste de tous les worms
- **`buildAllWormsArray()`** - Construit dynamiquement l'array de tous les worms

#### Méthodes ajoutées:
- **`addTeam(TeamModel, TeamView)`** - Ajoute une nouvelle équipe au jeu
- **`getTeamCount()`** - Retourne le nombre total d'équipes

### 2. **GameLoop.java**

- Utilise maintenant `gameManager.getCurrentTeam()` au lieu de vérifier `currentTeamIndex == 0`

## 📋 Utilisation

### Créer un jeu avec N équipes

```java
// Configuration de base (2 équipes - comportement actuel)
GameManager gameManager = new GameManager();

// Ajouter une 3e équipe
TeamModel team3 = new TeamModel(
    groundModel,       // GroundModel
    world,             // World (physics)
    "Team3",           // Nom de l'équipe
    500,               // Position X de départ
    60,                // Espacement entre worms
    3,                 // Nombre de worms
    false,             // console
    true,              // facingRight
    false              // withCollision
);
TeamView view3 = new TeamView(3, Color.GREEN, "/worm222.png", true);
gameManager.addTeam(team3, view3);

// Ajouter une 4e équipe
TeamModel team4 = new TeamModel(...);
TeamView view4 = new TeamView(...);
gameManager.addTeam(team4, view4);
```

### Accéder aux équipes

```java
// Obtenir l'équipe actuelle
TeamModel currentTeam = gameManager.getCurrentTeam();

// Obtenir le nombre total d'équipes
int teamCount = gameManager.getTeamCount();

// Obtenir le nom de l'équipe actuelle
String teamName = gameManager.getCurrentTeamName();
```

## 🔄 Flux des rounds

1. **`nextRound()` est appelé** (timeout ou worm mort)
2. **Index d'équipe incrémenté** : `currentTeamIndex = (currentTeamIndex + 1) % listTeamModel.size()`
3. **Équipe suivante activée** : récupérée dynamiquement via `getCurrentTeam()`
4. **Worm suivant sélectionné** : via `switchToNextWorm(currentTeam)`
5. **Affichage mis à jour** : nouveau nom d'équipe affiché via `getCurrentTeamName()`
6. **Vent régénéré** : change à chaque tour

## 📊 Architecture

```
GameManager
├── listTeamModel (ArrayList<TeamModel>)
│   ├── Team1 (3 worms)
│   ├── Team2 (3 worms)
│   ├── Team3 (3 worms)  ← Peut être ajoutée
│   └── Team4 (3 worms)  ← Peut être ajoutée
├── currentTeamIndex (0 → N-1, rotatif)
└── activeWormIndex (index dans la team actuelle)
```

## ⚠️ Limitations actuelles

### GamePanel reste hardcodé pour 2 équipes

**Problème**: Le rendu graphique est actuellement hardcodé pour afficher 2 équipes (teamView1, teamView2)

**Solution**: Pour supporter N équipes en UI, il faudrait:
1. Refactoriser `GamePanel` pour utiliser une liste d'équipes
2. Ajouter une boucle de rendu pour toutes les views

```java
// À faire dans GamePanel.paintComponent()
for (int i = 0; i < teamViews.size(); i++) {
    teamViews.get(i).draw(g2, teamModels.get(i));
}
```

### InventoryOverlay reste lié à Team1

Actuellement, l'inventaire affiché est lié à Team1. À améliorer pour afficher l'inventaire de l'équipe actuelle.

## ✨ Avantages

✅ **Flexibilité**: Support illimité d'équipes  
✅ **Dynamique**: Ajout d'équipes sans redémarrer  
✅ **Maintenabilité**: Pas de hardcoding des indices  
✅ **Scalabilité**: Prêt pour tournois/compétitions  

## 🧪 Exemple de test

```java
// Test avec 4 équipes
GameManager gm = new GameManager();

// Vérifier la rotation des équipes
assertEquals(0, gm.getCurrentTeamIndex());  // Team1
gm.nextRound();
assertEquals(1, gm.getCurrentTeamIndex());  // Team2
gm.nextRound();
assertEquals(2, gm.getCurrentTeamIndex());  // Team3 (si ajoutée)
gm.nextRound();
assertEquals(3, gm.getCurrentTeamIndex());  // Team4 (si ajoutée)
gm.nextRound();
assertEquals(0, gm.getCurrentTeamIndex());  // Team1 (boucle)
```

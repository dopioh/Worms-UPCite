# Structure du projet (où mettre quoi)

## Code Java (toutes les classes)
worms/src/main/java/fr/upc/g5e4/...

## Ressources non-Java (Image, audio, etc...)
worms/src/main/resources/...

----------------------------------------
# NameTag

Interface visuelle pour afficher les informations du worm :

```
    🧪  🧪  🧪        <- Items (potions, armes, etc.)
    ▓▓▓▓░░░░░░       <- Barre de shield (bleue, 0-50)
    ▓▓▓▓▓▓▓░░░       <- Barre de vie (verte, 0-100 HP)
```

**Note :** Par défaut, aucun item n'est affiché. Les items doivent être ajoutés

## Utilisation

### Ajouter des items

```java
nameTagView.addItemFromResource("/items/potion.png", "potion_health");
```

### Retirer les items

```java
nameTagView.clearItems(); // Retire tous les items
nameTagView.removeItem("potion_health"); // Retire un item spécifique par son nom
```

### Visibilité

```java
characterView.setShowNameTag(false); // Masquer
teamView.setShowAllNameTags(true);   // Pour toute une équipe
```

### Exemple en jeu

Appuyez sur **H** pour ajouter un item de test au worm actif (démo)

----------------------------------------
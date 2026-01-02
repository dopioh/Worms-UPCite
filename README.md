# Projet Worms

Jeu 2D de type [Worms](https://fr.wikipedia.org/wiki/Worms_(jeu_vidéo)) développé en Java, où des équipes de vers s'affrontent tour à tour sur un terrain destructible avec un petit arsenal soumis à une physique simple (gravité, collisions, vent)

## Prérequis

- **Java 23** ou supérieur
- **Apache Maven 3.6+**

## Compilation

Depuis le répertoire racine du projet, naviguez vers le dossier `worms` et compilez avec Maven :

```bash
cd worms
mvn clean package
```

Cette commande va :
1. Télécharger toutes les dépendances nécessaires
2. Compiler le code source
3. Exécuter les tests
4. Générer un fichier JAR exécutable dans `target/`

Le fichier JAR généré sera nommé `worms-app.jar` et contiendra toutes les dépendances nécessaires.

## Exécution

Après la compilation, **depuis le dossier racine du projet** :

```bash
java -jar worms/target/worms-app.jar
```

## Utilisation du jeu

### Menu principal

Au lancement, vous arrivez sur le menu principal avec les options suivantes :
- **Nouvelle Partie** : Démarrer une nouvelle partie
- **Poursuivre une partie** : Charger une partie sauvegardée
- **Reglages** : Configurer les paramètres du jeu (map, équipes, etc.)
- **Boutique** : Acheter des items et des armes
- **Tutoriel** : Apprendre les bases du jeu
- **Quitter** : Fermer le jeu

### Déroulement d'une partie

1. Des équipes s'affrontent tour par tour
2. Chaque tour, vous contrôlez un ver de votre équipe
3. Déplacez-vous sur le terrain et utilisez votre arsenal pour éliminer ou pousser dans l'eau l'équipe adverse
4. Le terrain est destructible - utilisez-le à votre avantage !
5. La partie se termine lorsqu'une équipe n'a plus de vers

### Contrôles en jeu

- **Q/D** : Déplacer le ver
- **Z** : Sauter
- **Clic gauche** : Tirer/Utiliser l'arme ou gadget
- **Échap** : Menu pause
- **Scroll** : Zoomer/Dézoomer la caméra

### Inventaire

Pendant la partie, appuyez sur le sac pour ouvrir l'inventaire avec ses trois onglets :

#### Armes
- Consultez et sélectionnez vos armes disponibles

#### Gadgets
- Accédez à vos items spéciaux et potions

#### Boutique
- Jouez à des mini-jeux pour gagner de l'argent supplémentaire

### Arsenal disponible

**Armes :**
- **AK-47** : Arme automatique avec cadence de tir rapide
- **Bazooka** : Lance-roquettes à longue portée avec explosion
- **Grenade** : Projectile explosif avec rebond
- **Batte de baseball** : Arme de mêlée pour repousser les ennemis

**Gadgets :**
- **Bumper** : Trampoline pour faire rebondir les worms
- **Potion de vie** : Restaure les points de vie
- **Potion de vitesse** : Augmente la vitesse de déplacement
- **Potion de saut** : Améliore la hauteur de saut
- **Potion de bouclier** : Offre une protection temporaire

### Système de sauvegarde

On peut sauver des parties dans le dossier `sauvegardes/` :
- `sauvegarde.txt` : Sauvegarde manuelle
- `player_save.txt` : Sauvegarde des profils joueurs

## Tests

Pour exécuter les tests unitaires :

```bash
cd worms
mvn test
```

Pour générer un rapport de couverture de code avec JaCoCo :

```bash
cd worms
mvn clean verify
```

Le rapport sera disponible dans `target/site/jacoco/index.html`

## Structure du projet

```
worms/
├── src/
│   ├── main/
│   │   ├── java/          # Code source
│   │   └── resources/     # Ressources (sprites, sons, etc.)
│   └── test/
│       └── java/          # Tests unitaires
├── sauvegardes/           # Fichiers de sauvegarde
└── pom.xml                # Configuration Maven
```
Voir [ARCHITECTURE.md](worms/ARCHITECTURE.md) pour la structure complète

## Documentation

- [DESIGN.md](DESIGN.md) : Décisions de conception
- [CHANGELOG.md](CHANGELOG.md) : Historique des modifications
- [CONTRIBUTING.md](CONTRIBUTING.md) : Guide de contribution
- [AUTHORS.md](AUTHORS.md) : Liste des contributeurs du projet

## Problèmes connus

Si vous rencontrez des problèmes :

1. **Le JAR ne se lance pas** : Vérifiez que vous utilisez Java 23+
2. **Erreur de compilation Maven** : Assurez-vous d'avoir une connexion internet pour télécharger les dépendances

## Licence

Voir le fichier [LICENSE](LICENSE) pour les détails.

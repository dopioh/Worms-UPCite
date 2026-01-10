# Guide de contribution

Merci de votre intérêt pour contribuer au projet Worms ! Toute contribution est la bienvenue, que ce soit pour corriger des bugs, ajouter de nouvelles fonctionnalités, améliorer la documentation ou optimiser le code existant.

## Table des matières

- [Comment contribuer](#comment-contribuer)
- [Configuration de l'environnement](#configuration-de-lenvironnement)
- [Processus de développement](#processus-de-développement)
- [Structure du projet](#structure-du-projet)
- [Tests](#tests)
- [Soumettre une merge request](#soumettre-une-merge-request)


## Comment contribuer

### Signaler un bug

Si vous trouvez un bug, veuillez créer un ticket en incluant :
- Une description claire du problème avec image/video
- Les étapes amenant le bug
- Votre environnement (Système d'exploitation, version de Java, version Maven)

### Proposer une amélioration

Pour proposer une nouvelle fonctionnalité :
>1. Vérifiez qu'elle n'a pas déjà été ajoutée/proposée
>2. Créez un ticket
>3. Expliquez l'utilité


## Configuration de l'environnement

### Prérequis

- **Java 23-25** (Java23 conseillé)
- **Apache Maven 3.9
- **Git** pour la gestion de version

### Installation

1. Forkez le dépôt
2. Clonez votre fork localement :
   ```bash
   git clone https://moule.informatique.univ-paris-diderot.fr/militon/projet-worms
   cd projet-worms
   ```

3. Compilez le projet :
   ```bash
   cd worms
   mvn clean install
   ```

4. Vérifiez que tous les tests passent :
   ```bash
   mvn test
   ```

## Processus de développement

### Workflow Git

1. Créez une branche pour votre fonctionnalité ou correction :
   ```bash
   git checkout -b {nomdelabranche}
   ```

2. Effectuez vos modifications en suivant les standards de code

3. Committez vos changements :
   ```bash
   git commit -m ""
   ```

4. Poussez votre branche :
   ```bash
   git push origin {nomdelabranche}
   ```

## Standards de code

### Java

- conventions de nommage Java :
  - Classes : `PascalCase`
  - Méthodes et variables : `camelCase`
  - Constantes : `FULLCAPS`
- Commentez le code avec JavaDoc

### Structure du projet

Respectez l'architecture MVC (Model View Controller) :
```
worms/src/main/java/fr/upc/g5e4/
├── model/          # Logique métier et données
├── view/           # Interface graphique
└── controller/     # Gestion des événements et contrôles
```

## Tests

### Écriture des tests

- Chaque fonctionnalité importante doit avoir des tests
- Utilisez **JUnit** pour les tests unitaires

Exemple :
```java
@Test
// exemple de test
void shouldDealDamageWhenProjectileHitsCharacter() {
    Character character = new Character(100);
    Projectile projectile = new Grenade(50);
    
    projectile.hit(character);

    assertEquals(50, character.getHealth()); // true si le health est à 50, false sinon
}
```

### Exécution des tests

```bash
mvn test
```

### Fichiers markdown

- Mettre à jour [README.md](README.md) si nécessaire
- Ajouter les changements dans [CHANGELOG.md](CHANGELOG.md)
- Ajouter votre nom dans [AUTHORS.MD](AUTHORS.MD)

## Soumettre une contribution

### Merge Request

1. Assurez-vous que votre code respecte ce qui est dit dans ce fichier
2. Vérifiez que tous les tests passent
3. Lire la template puis la supprimé de la description de merge request

### Description de la MR

Incluez dans votre Merge Request :
- **Titre clair** Titre pas trop long et explicite
- **Description** : Expliquation simple des ajouts
- **Tickets liées** : Référencez les tickets concernées (#123)

### Revue de code

- Soyez ouvert aux retours et suggestions
- Répondez aux commentaires de manière constructive
- Effectuez les modifications demandées
- Une fois approuvée, votre PR sera mergée !

Merci pour votre contribution !👍
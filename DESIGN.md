# DESIGN.MD

## Gestion du projet : [Maven](https://github.com/apache/maven)
Nous avons utiliser Maven pour de standardiser la structure du projet, pour gérer les dépendances, et pour automatiser le build et les tests sur nos machines et sur l'intégration continue.

## Moteur physique : [JBox2D](https://github.com/jbox2d/jbox2d) 
Nous avons utilisés le moteur physique JBox2D lancé par Daniel Murphy, un portage du moteur Box2D fait par Erin Catto et du moteur LiquiFun de Google. Box2D à notemment été utilisé pour le très célèbre AngryBid.

>Cependant, nous avons utilisés la documentation de Box2D car elle était très complète et très facile à adapter en Java. 

## Affichage Graphique : AWT & Swing
Notre jeu utilise les bibliothèques AWT et Swing (natives a Java) pour faire le rendu graphique.

## Gestion des données : [Gson](https://github.com/google/gson)
Afin d'implémenter notre système de sauvegarde et d'inventaire, nous avons utilisés la bibliothèque Gson de Google qui permet de convertir des objets Java en Json et inversement. 

## Interface Console : [Lanterna](https://github.com/mabe02/lanterna)
Nous avons utilisés la bibliothèque Lanterna, qui permet de faire des interfaces graphiques dans la console. C'est une librairie simple à comprendre et très complète.

## Génération Procédurale : [JNoise](https://github.com/Articdive/JNoise)
Pour la génération procédurale de la map, nous avons utiliser la bibliothèque JNoise qui possède différents algorithmes pour générer du bruit. Nous avons utiliser le bruit de Perlin, utilisé par le très célèbre Minecraft, afin de génerer des bruits que nous avons ensuite amplifiés avec notre système d'octave. 

## Bibliothèques de Tests : [JUnit](https://github.com/junit-team/junit-framework) [Mockito](https://github.com/mockito/mockito)
JUnit nous a permis de tester notre code avec les tests unitaires. En compagnie de Mockito pour faire du mocking d'objets pour nos tests quand nous n'avions pas le code de l'un de nos collégues. 

## Couverture des tests : [Jacoco](https://github.com/jacoco/jacoco)
Nous avons utiliser la bibliothèque Jacoco pour faire la couverture de l'ensemble de nos tests.
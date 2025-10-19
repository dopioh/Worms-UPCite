# Projet Worms

## INFO

J'ai créé le projet Gitlab de notre groupe pour pouvoir avoir une base, si vous avez quelconque suggestions/modifications n'hésitez pas à l'évoquer et l'intégrer directement si c'est validé par le groupe.

On communiquera principalement sur le [kanban](https://moule.informatique.u-paris.fr/militon/projet-worms/-/boards) - pour toutes discussions plus complexes un groupe Discord a été créé - de préférence pour l'historique et la notation, il est mieux de seulement communiquer sur le projet Gitlab.

## SUJET ET NOTATION

Le [sujet](https://moodle.u-pariscite.fr/pluginfile.php/3407236/mod_resource/content/1/projet_2025.pdf) et la [grille de notation](https://docs.google.com/spreadsheets/d/e/2PACX-1vR4JzgF5x4Qx_-W3lg4KVsW8sQGoXe7YrFOyvE-kLjK3zgn-BslKGiMzYN9O1Is8aALiQz-UXFmLiRk/pubhtml) sont disponibles sur moodle.

Le projet consiste à créer en Java un jeu 2D de type [Worms](https://fr.wikipedia.org/wiki/Worms_(jeu_vidéo)), où deux équipes de vers s’affrontent tour à tour sur un terrain destructible à l’aide d’un petit arsenal soumis à une physique simple (gravité, collisions, vent).
Toutes les fonctionnalités obligatoires sont décrites dans le sujet, il est également conseillé (même *obligatoire*) de rajouter des fonctionnalités supplémentaires pour espérer avoir une note correcte.

Je possède la [derniere version](https://store.steampowered.com/app/327030/Worms_WMD/?l=french) du jeu Worms fait par le studio Team17 sur mon mac si vous voulez voir a quoi le jeu ressemble.

## MARKDOWN

Les fichiers tels que celui-ci ou ceux à rédiger pour avoir un affichage correct sur GitLab devront être rédigés en **markdown** (.md), un PDF synthétisant toute la syntaxe et comment *MD* fonctionne par rapport à HTML et disponible [ici](https://markdown.p2hp.com/assets/book/markdown-guide.pdf).
[Ici](https://markdownlivepreview.com) un site pour avoir un aperçu de ce que vous écrivez en *MD*.

## ORGANISATION

Pour le moment, l'organisation ainsi que nos rôles attribués restent encore en suspend, n'hésitez pas à relire le [CM2](https://moodle.u-pariscite.fr/pluginfile.php/2671492/mod_resource/content/3/cours-02.pdf) qui explique comment fonctionne la méthode **Scrum** - celle que l'on va utiliser pour ce projet - avant que l'on s'organise.

Je peux déjà confirmer qu'on fonctionnera avec ***une fonctionnalité = une branche***, qui sera merge avec la branche `dev` quand elle sera finie puis `dev` avec `main` quand tout sera prêt pour une release.

## Files à rajouter pour le projet git

Plusieurs fichiers seront obligatoires à integrer à ce git, parmi eux :

```
CHANGELOG.md    // Une sorte d'historique du projet où chaque gros changements devra y etre inscrit avec la date et la version du projet.
LICENCE.md      // Un fichier Copyright qui indique les droits et les restrictions d’utilisation du code. (Ce qui devra etre *null* pour nous puisque notre code - et nos idees - doivent rester strictement entre nous.)
```




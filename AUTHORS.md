# Participants au Projet Worms du Groupe 5 Equipe 4 :

- [Militon Mei](https://moule.informatique.u-paris.fr/militon)
    
    - Premiere version gitlab du projet (1ere version README.md)

    - Premiere version du Terrain avec

    - Physique du jeu avec Loop du jeu

    - Ajout limite de la map

    - Set Up projet (release, CI)

        - 4 phases (build,test,deploy et release auto)

    - Ajout Vue Console
        - Terrain
        - Equipes
        - Data Equipe 
        - Menu simple 
        - Tir simple 
        
    - Ajout Systeme de Sauvegarde 

    - Refactor code pour passer a N-equipes

    - Ajout Equipes Custom (save dans `player_save.txt`)

    - Ajout Interface Settings
        - Creer une equipe Custom
        - Choisir les equipes pour la prochaine partie

    - Ajout nom Worm
    
    - Amelioration de la fluidite du jeu

    - Creation 5 skins Worm 

    - Review/merge de la plupart des MR

    - Brainstorm de la plupart des idees

- [MAZRI Nassim](https://moule.informatique.u-paris.fr/mazri)

    - Ajout des deplacements

    - Ajout classe worm

    - Ajout disparition d'un worm mort

    - 1ere version du nametag

    - Ajout des sprite pour les worms

    - Ajout des equipes

    - Géneration procédurale du terrain

    - Terrain par tableaux 2D de block

    - Boutique

    - Casino

    - SFX pour les armes, les worms et les blocks

    - Ajout de tickets, Validation et Acceptations de MR

    - Brainstorm d'idées pour tout le projet

    - Ajout CONTRIBUTING.md

    - Ajout systeme d'image dans le cache


- [WU Billy](https://moule.informatique.u-paris.fr/wub)
- [SHAN Hugo](https://moule.informatique.u-paris.fr/shanh)

* Approbation de certaines MR
* BrainStorming de temps en temps

###### ***Sprint 1  oct. 26, 2025–nov. 14, 2025***



Ajout:

* &nbsp;Création de l'interface de l'inventaire (JDialog) 
* &nbsp;Modèle de l'inventaire (Utilisation de chaque item par round/totaux,etc..)
* &nbsp;Création d'une classe Item temporaire (Armes Gadgets) 
* &nbsp;Inventory.json (JavaScript Object Notation) pour stocker chaque information (C'est la les statistiques de chaque arme)



Problèmes: 

* Certaines fonctions du modèles n'étaient pas correctement liées



###### ***Sprint 2 nov. 15, 2025–nov. 28, 2025***



Suppressions:

* Interface de l'inventaire en Jdialog (ne plaisait pas à tout le monde)
* Modèle de classe Items



Ajout:

* Raccourci clavier "I" et "Esc" (Pour l'ouverture du Overlay et la fermeture du jeu)
* [Icone du sac](https://fr.pngtree.com/freepng/vector-cartoon-inventory-game-ui_6038258.html) cliquable en haut a droite de l'écran (Ouvrant l'inventaire)
* Interface Overlay interactif (moche) 



Problèmes:

* (trouvé a la fin du sprint) Le détecteur de clic souris prenait le dessus sur celui du clic et empêchait le tir 



###### ***Sprint 3 nov. 28, 2025–déc. 12, 2025***



Ajouts: 

* Une fonction Contains(x,y) servant a regler le problème de souris
* Liaison des tirs à l'inventaire (Utilisation décrémente l'inventaire) 
* Ajout du premier SFX (AK47) à toutes les armes pour le moment
* Modèle du vent j'ai aussi tenté de l'appliquer au tir mais j'ai un peu fait foiré la fonction de Billy
* Interface Overlay du vent 



* Suppression du clic souris pour activer le sac



Problèmes:

* Le tir avait une trajectoire étrange
* Le tir n'était au final pas bien lié a l'inventaire (tache transférée a une autre personne)
* L'overlay du vent était pas optimisé et un peu lourd, ce qui donnait un frameRate très bas au jeu
* L'overlay du vent était fixé aux coordonnées(fixe pas des variables) de la fenêtre de jeu de base mais quand le plein écran est activé il ne s'actualisait pas avec les coordonnées de l'écran plein





###### ***Sprint 4 déc. 13, 2025–déc. 26, 2025***



Découverte du site https://www.flaticon.com/fr/ pour les icones



* Restructuration des tickets sur Gitlab
* Changement de la valeur de position de l'overlay Vent par variable
* Modification de Esc pour quitter le jeu par l'ouverture d'un overlay Pause



Ajouts:

* Création d'overlay (Pause/Help/HUD):

&nbsp;  - Pause: une page de parametres simple et nulle avec des placeholders pour VENT QUIT SAVE

&nbsp;  - Help: une page de guide de tous les raccourcis du jeu sur le moment

&nbsp;  - HUD: Les informations du joueur qui sont toujours affichées (Barre de vie/bouclier) + Sac cliquable de l'inventaire + Barre de timer du jeu 

* Overlay Inventaire graphiquement amélioré (Mit sur la gauche, Icone d'image pour chaque objet, Différentes sections pour (ARMES GADGET et CRAFT(idée qui au final était abandonnée))
* Inventaire Hover de l'arme ou d'un gadget affiche sa description



Problèmes:

* La barre de timer ne marchait pas encore
* Les couleurs des barres étaient pas bonnes (Verte pour les 2) et positions inversées
* Le sac cliquable 
* L'inventaire était partagé pour chacune des équipes
* On pouvait tirer autant de fois que voulu 
* Les icones ne s'affichait pas partout (Inventaire/HUD) seulement une partie fonctionnait
* A chaque début de tour un worm était déjà équipée d'une arme par défaut



###### ***Sprint 5 déc. 27, 2025–jan. 9, 2026***



Fix:

* Les worms n'ont plus rien dans leur main quand le tour commence (Le garde par contre quand le tour est fini)
* Icones entièrement réglé
* Timer qui (glisse) et qui marche bien
* Couleurs des barres et position
* Clic sur chacun des Overlays 
* Inventaire différé pour chaque team
* Limitations des tirs (Billy s'en est occupé)



Suppressions:

* Chacune des touches raccourcis claviers
* Texte du timer écrit en haut a gauche (Remplacé par la barre timer)



Ajouts:

* Les touches claviers supprimées avait toutes des fonctionnalités (Vol,FullScreen,OuverturePause,etc..)

donc ajout d'un bouton pour mettre fin au tour, Ajout d'un icone parametre cliquable pour ouvrir Pause

* Pause est maintenant une page de parametres contenant des boutons simple cliquable(Sauvegarde,Help,Quit, Vol) mais aussi des boutons avec toggle (Vol,FullScreen,Son,BluePrint)
* Quit est d'ailleurs rélié au menu principal
* Ajout de la possibilité "ignorer" le son 
* Ajout de bouton sur le HUDOverlay et scroll souris pour bouger la caméra
* Ajout de l'image de l'arme ainsi que de ses munitions dans la description (InventoryOverlay) ainsi que dans le (HUD)
* Liaison des paramètres Menu et enJeu (Le son restera marqué éteint si l'un change l'autre aussi, le fond d'écran aussi)
* Amélioration de tous les overlays avec effet Hover
* Pause sur le jeu a l'ouverture de PauseOverlay



Problèmes:

* Je ne sais pas comment bien optimiser le code, le jeu est devenu très lent après coup car trop d'actualisation par seconde
* Certains usages dans mes fichiers sont border (dans InventoryOverlay.java et HUDOverlay.java) pour rester fixé au MVC, étant donné que ce sont des "View Interactifs" le fait de pouvoir appeler .nextRound() au clic souris par exemple est peut être par approprié

Solution : Création d'un overlayManager (Partie Controller) mais l'idée a été abandonnée car a la fin du sprint quand tout le monde a merge, il y avait trop de conflit qui résultait de ce changement. J'aurais du le créer dès le début. 

- [DESROQUES Ian](https://moule.informatique.u-paris.fr/desroque)
- [ROSE Raphael](https://moule.informatique.u-paris.fr/rose)
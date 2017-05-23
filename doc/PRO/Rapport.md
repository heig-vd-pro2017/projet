## Introduction
Durant le quatrième semestre de la section TIC de l'HEIG-VD, nous devons effectuer un projet par groupes de cinq ou six personnes, le but étant de mettre en œuvre les connaissances que nous avons acquises au long des semestres précédents à travers un projet conséquent. Nous devrons prendre conscience des difficultés liées au travail de groupe, ainsi qu'apprendre à planifier un travail sur plusieurs mois. Au terme du semestre, nous devons rendre une programme complet et fonctionnel, avec une documentation adéquate et être capables de le présenter et le défendre.
Dans le cadre du projet, l'équipe de programmation est composée du chef d'équipe Ludovic Delafontaine, de son remplaçant Lucas Elisei et des membres David Truan, Thibaut Togue, Yosra Harbaoui et Denise Gemesio.
## Objectif
Le but est de réaliser un programme client-serveur permettant d'écouter de la musique. Son utilité prend tout son sens lors d'évènements festifs et communautaires. En effet, notre programme permet aux utilisateurs d'envoyer leurs propres chansons à un serveur défini par l'organisateur de la soirée. Des fonctionnalités spéciales permettent de vivre une expérience musicale uniques :
- Voter pour ou contre une chanson permet de la placer plus en avant ou en arrière dans la queue de lecture.  
- Monter le volume, arrêter et redémarrer la musique sont exécutés si la majorité du public le désire.
- Ajouter des chansons écoutées durant la soirée dans une liste de favoris afin de retrouver le titre et l'artiste d'un coup de coeur.
## Abstraction / Conception / Architecture
Description  diagramme des cas d'utilisation avec figure. Diagramme UML, la sauvegarde...
## Implémentation / Description technique
utiliser les packages pour la description 
Ne pas mettre toutes les classes ! 
### Gestionnaire de configuration
Nous avons choisi d'implémenter un gestionnaire de configuration utilisant le fichier commusica.properties pour permettre à l'utilisateur de configurer le programme. Elle donne accès aux paramètre suivants :               
 +  SERVER_NAME : choix nom du serveur auquel les participants pourront se connecter
 +  PLAYLIST_NAME : choix du nom de la playlist pour la soirée
 +  DEBUG : au niveau développement, choisir ou non d'afficher les logs
 +  DATE_FORMAT : choix du format de la date
 +  VOLUME_STEP : choix du pas d'augmentation et abaissement de la musique
 +  TRACKS_DIRECTORY : choix du chemin relatif où les chansons seront stockées
 +  TIME_BEFORE_SESSION_INACTIVE : choix du délai d'inactivité d'une session
 +  TIME_BETWEEN_PLAYLIST_UPDATES : choix du délai de mise à jour des playlists et leurs chansons
###  Core
###  Database
###  File
###  Network
###  Playlist
###  Interface graphique
###  Utils

## Parties manquantes par rapport au cahier des charges
## Tests réalisés
## Problèmes subsistants
## Améliorations futures

## Planification / organisation

## Conclusion du projet
### Bilan du projet
### Bilan du groupe
#### Ludovic
#### Lucas
#### David
#### Thibaut
#### Yosra
#### Denise

# Annexes
## Cahier des charges 
## Journal de travail
## Panification initiale et son évolution

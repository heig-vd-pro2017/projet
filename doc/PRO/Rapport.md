---
title: Commusica
author:
    - tofind
header-includes:
    - \usepackage{fancyhdr}
    - \pagestyle{fancy}
    - \fancyhead[CO,CE]{Commusica}
    - \fancyhead[LO,LE]{}
    - \fancyhead[RO,RE]{}
    - \renewcommand{\contentsname}{Table des matières}
    - \lstset{breaklines=true}
    - \lstset{backgroundcolor=\color[RGB]{248,248,248}}
    - \lstset{language=java}
    - \lstset{basicstyle=\small\ttfamily}
    - \lstset{extendedchars=true}
    - \lstset{tabsize=2}
    - \lstset{columns=fixed}
    - \lstset{showstringspaces=false}
    - \lstset{frame=trbl}
    - \lstset{frameround=tttt}
    - \lstset{framesep=4pt}
    - \lstset{numbers=left}
    - \lstset{numberstyle=\tiny\ttfamily}
toc: yes
---

\newpage

# Introduction
Durant le quatrième semestre de la section TIC de l'HEIG-VD, nous devons effectuer un projet par groupes de cinq ou six personnes, le but étant de mettre en œuvre les connaissances que nous avons acquises au long des semestres précédents à travers un projet conséquent. Nous devrons prendre conscience des difficultés liées au travail de groupe, ainsi qu'apprendre à planifier un travail sur plusieurs mois. Au terme du semestre, nous devons rendre une programme complet et fonctionnel, avec une documentation adéquate et être capables de le présenter et le défendre.
Dans le cadre du projet, l'équipe de programmation est composée du chef d'équipe Ludovic Delafontaine, de son remplaçant Lucas Elisei et des membres David Truan, Thibaut Togue, Yosra Harbaoui et Denise Gemesio.  
Dans ce rapport, nous allons expliquer notre démarche de travail et les principaux choix d'architecture et de design de code que nous avons choisis. Il sera structuré selon les principaux paquets de notre application. Plus un paquet ou une classe est complexe et importante au sein de notre projet, plus la description sera importante et entrera dans les détails.


## Objectif
Le but de notre programme est de proposer une application client-serveur qui permettra aux clients d'envoyer des fichiers musicaux au serveur pour que celui-ci les joue. Il se démarque d'une simple application de streaming dans le fait que la liste de lecture ne peut être changée que par les clients par le biais d'un système de votes positifs ou négatifs. Ceux-ci permettent à un morceau présent d'être placé plus en avant ou en arrière dans la liste de lecture. Ceci permet donc à chacun de donner son avis, tout en centralisant la lecture de la musique sur un seul ordinateur. En plus de cela, l'application met à disposition les fonctionnalités suivantes pour une expérience encore plus communautaire:
+ Vote pour passer au morceau suivant. Lorsqu'une majorité (plus de 50%) des clients ont voté pour passer au morceau suivant, la piste en écoute est remplacée par le morceau qui la suit dans la liste de lecture.
+ Le même principe de vote est appliqué pour augmenter ou diminuer le volume.
+ Système de favoris pour permettre aux utilisateurs de sauvegarder les informations (titre, auteur, etc.) en local dans une playlist spécifique.  

Cette application visera principalement les soirées avec plusieurs personnes et répondra à l'éternel problème de devoir se passer une prise jack ou de devoir se battre pour pouvoir passer un morceau que l'on aime.

## Abstraction / Conception / Architecture
Description  diagramme des cas d'utilisation avec figure. Diagramme UML, la sauvegarde...

## Implémentation / Description technique
utiliser les packages pour la description
Ne pas mettre toutes les classes !

### Gestionnaire de configuration

Nous avons choisi d'implémenter un gestionnaire de configuration utilisant le fichier commusica.properties pour permettre à l'utilisateur de configurer le programme. Elle donne accès aux paramètres suivants :

- SERVER_NAME : choix nom du serveur auquel les participants pourront se connecter
- PLAYLIST_NAME : choix du nom de la playlist pour la soirée
- DEBUG : au niveau développement, choisir ou non d'afficher les logs
- DATE_FORMAT : choix du format de la date
- VOLUME_STEP : choix du pas d'augmentation et abaissement de la musique
- TRACKS_DIRECTORY : choix du chemin relatif où les chansons seront stockées
- TIME_BEFORE_SESSION_INACTIVE : choix du délai d'inactivité d'une session
- TIME_BETWEEN_PLAYLIST_UPDATES : choix du délai de mise à jour des playlists et leurs chansons

### Package core
Pour garder un niveau d'abstraction le plus élevé possible, nous avons voulu faire transiter à travers un contrôleur toutes les informations venant du réseau et des utilisateurs, le but étant d'avoir le même point d'entrée que l'on soit client ou serveur. Pour cela, il nous fallait un contrôleur central qui puisse être appelé de la même façon quel que soit le choix de l'identité - client ou serveur. C'est alors à celui-ci de vérifier l'existence d'une fonction et de communiquer l'action à exécuter à l'entité concernée. Notre raisonnement nous a mené à nous tourner vers la réflexivité offerte par **Java** pour résoudre ce problème. Ce mécanisme permet d'instancier des méthodes à l'exécution en utilisant la méthode `invoke(Object obj, Object... args)` ayant comme premier paramètre un String représentant le nom de la méthode à invoquer et comme deuxième paramètre un tableau d'`Object` contentant les différents arguments dont la méthode invoquée a besoin (voir utilisation dans notre programme **FIGURE**).

Il nous fallait maintenant une classe qui puisse jouer le rôle du contrôleur. Nous avons développé les **Core** pour cela qui sont tous dans le paquet *core*.
![Classes principales du package *core*](fr)

#### Classes du package
##### Core
Classe statique qui joue le rôle de point d'entrée. Elle dispose d'un attribut `AbstractCore` qui sera instancier soit en `ClientCore` ou en `ServerCore`. Elle met aussi à disposition des méthodes statiques qui nous permettrons de les appeler depuis les autres classes du programme.  
Parmi ces méthodes, la plus importante est la suivante:  

```java
public static String execute(String command, ArrayList<Object> args)
```
 Cette méthode se contente d'appeler la méthode du même nom de l'instance de `AbstractCore` de la classe et sera appelée partout ou une action du Core est demandée.

Elle contient aussi des méthodes lui permettant de se paramétrer comme client ou serveur.

##### AbstractCore
Cette classe abstraite met à disposition les méthodes pour permettre à ses sous-classes de s'exécuter correctement. Contrairement à `Core` cette classe va utiliser la réflectivité dans se méthode execute() comme ceci:  

```java
public synchronized String execute(String command, ArrayList<Object> args) {

    String result = "";

    try {
        Method method = this.getClass().getMethod( command, ArrayList.class);
        result = (String) method.invoke(this, args);
    } catch (NoSuchMethodException e) {
        // Do nothing
    } catch (IllegalAccessException | InvocationTargetException e) {
        LOG.error(e);
    }

    return result;
}
```

On reçoit une commande et un tableau correspondant aux arguments de la méthode à invoquer. Ensuite le programme essaie de retrouver la méthode ayant un nom correspondant à la commande envoyée si elle est disponible dans l'instance de la classe. Si c'est le cas elle va l'invoquer et donc exécuter ladite méthode. C'est ici que tout prend son sens car on a maintenant une instance d'`AbstractCore` qui est soit `ClientCore` ou `ServerCore` avec une seule méthode pour en appeler d'autres qui seront-elles implémentées dans les sous-classes de `AbstractCore`.

##### ServerCore et ClientCore
Ces deux classe qui héritent de `AbstractCore` et qui implémentent `ICore` sont les parties les plus importantes du projet et c'est ici que la majorité des actions (transfert de la musique, action à effectuer lors d'un appui dur un bouton, etc...) se fera. Lors de l'envoie des commandes ces classes fonctionnent un peu avec un système d'états qui peuvent être changé en recevant des commandes depuis le réseau ou depuis le code. Elles ont une forte interaction avec les classes s'occupant du réseau puisque c'est ici que toutes les informations reçues depuis le réseau vont passer. Grace à la réflectivité offerte par l'`AbstractCore` il est donc extrêmement facile de définir de nouvelles méthodes dans ces classes. Il suffi de passer les bonnes commandes à la méthode `execute()`.

##### Synthèse du paquet core
Grace à ses classes, nous avons réglé le problème de contrôleur central par lequel tout transitera. La réception des commandes à invoquer sera expliquée plus tard dans le chapitre sur le paquet `Network` et lors des explications sur la liaison entre l'interface graphique et le code.





###  Database
La sauvegarde et le chargement font partie des points importants de notre application, car elle a été conçue pour permettre, par exemple, à un utilisateur de sauvegarder les metadatas des chansons qui lui plaisent dans la base de données. Ce package est constitué essentiellement de la classe **DatabaseManager.java** donc le rôle est d'assurer le CRUD (Create, Read, Update, Delete) de la base des données de notre application  et d'assuer la fermeture de la connexion à celle-ci.

Pour l'implémentation nous avons choisir le framework Hibernate qui simplifie le développement de l'application java pour interagir avec la base des données. C'est un outil open source, léger. ORM(Object Relational Mapping)
Un outil ORM simplifie la création des données, la manipulation de données et l'accès aux données. C'est une technique de programme qui mappe l'objet aux données stockées dans la base des données.

La performance du framework hibernate est rapide car le cache est utilisé en interne dans le cadre hiberné.
Le framework Hibernate offre la possibilité de créer automatiquement les tables de la base des données. Il m'est donc pas nécessaires de créer manuellement des tables dans la base de données.

###  File
 Le package File a pour rôle d'assuré la gestion des fichiers en interagissant avec le file système . Il est constitué de 02 classe **FileManager** et **FilesFormats**.

 + **FilesFormats**: Vue que notre application supporte trois formats mp3, m4a et wav, la classe permet de définit les caractéristique d'un fichier c'est à dire tous les éléments nous permettant de savoir le type du fichier.
 + **FileManager**:
   Cette classe permet de supprimer, stocké et déterminé le type de fichier.
 Pour retrouver l'extension du fichier nous avons procédé de telle maniéré :

	+ Pour les fichiers mp3, on regarde les 3 premiers bytes depuis le début du fichier.
	+ Pour les m4a, on regarde les premiers octets mais en partant du quatrième octet  depuis le début du fichier
	+  Pour les wav, à partir du huitième octet depuis le début du fichier.

Connaitre le type de fichier nous permettra de traiter que les fichiers supporté pas notre plateforme et aussi en termes de sécurité éviter qu'un utilisateur face planter le serveur en envoyant un fichier qui n'est pas supporté par celui-ci.

##  File

##  Network

##  Playlist

##  Interface graphique

##  Utils

# Parties manquantes par rapport au cahier des charges

# Tests réalisés

# Problèmes subsistants

# Améliorations futures

# Planification / organisation

# Conclusion du projet

# Bilan du projet

## Lucas
## David
## Thibaut
## Yosra
## Denise

# Glossaire

# Sources
https://blog.axopen.com/2013/11/les-cles-primaires-composees-avec-hibernate-4/
https://vladmihalcea.com/2016/08/01/the-best-way-to-map-a-composite-primary-key-with-jpa-and-hibernate/

# Annexes
- Cahier des charges
- Journal de travail
- Panification initiale et son évolution
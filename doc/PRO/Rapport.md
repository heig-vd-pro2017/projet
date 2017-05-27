---
title: Commusica

subtitle: Tests

author:
- Ludovic Delafontaine
- Lucas Elisei
- David Truan
- Denise Gemesio
- Thibaut Togue
- Yosra Harbaoui

date: HEIG-VD 2017 - PRO

geometry: margin=2cm



header-includes:
    - \usepackage{etoolbox}
    - \usepackage{fancyhdr}
    - \usepackage[T1]{fontenc}
    - \usepackage{xcolor}

    # Some beautiful colors.
    - \definecolor{pblue}{rgb}{0.13, 0.13, 1.0}
    - \definecolor{pgray}{rgb}{0.46, 0.45, 0.48}
    - \definecolor{pgreen}{rgb}{0.0, 0.5, 0.0}
    - \definecolor{pred}{rgb}{0.9, 0.0, 0.0}

    - \renewcommand{\ttdefault}{pcr}

    # 'fancyhdr' settings.
    - \pagestyle{fancy}
    - \fancyhead[CO,CE]{}
    - \fancyhead[LO,LE]{Commusica}
    - \fancyhead[RO,RE]{PRO 2017}

    # Redefine TOC title.
    - \renewcommand{\contentsname}{Table des matières}
    - \setcounter{tocdepth}{2}

    # Redefine TOF title.
    - \renewcommand{\listfigurename}{Table des figures}

    # 'listings' settings.
    - \lstset{breaklines = true}
    - \lstset{backgroundcolor = \color{black!10}}
    - \lstset{basicstyle = \ttfamily}
    - \lstset{breakatwhitespace = true}
    - \lstset{columns = fixed}
    - \lstset{commentstyle = \color{pgreen}}
    - \lstset{extendedchars = true}
    - \lstset{frame = trbl}
    - \lstset{frameround = none}
    - \lstset{framesep = 2pt}
    - \lstset{keywordstyle = \bfseries}
    - \lstset{keywordsprefix = {@}}                           # Java annotations.
    - \lstset{language = Java}
    - \lstset{numbers=left,xleftmargin=2em,xrightmargin=0.25em}
    - \lstset{numberstyle = \small\ttfamily}
    - \lstset{showstringspaces = false}
    - \lstset{stringstyle = \color{pred}}
    - \lstset{tabsize = 2}

    # 'listings' not page breaking.
    - \BeforeBeginEnvironment{lstlisting}{\begin{minipage}{\textwidth}}
    - \AfterEndEnvironment{lstlisting}{\end{minipage}}
---

\newpage

\tableofcontents

\listoffigures

\newpage

# Introduction
Durant le quatrième semestre de la section TIC de l'HEIG-VD, nous devons effectuer un projet par groupes de cinq ou six personnes, le but étant de mettre en oeuvre les connaissances que nous avons acquises au long des semestres précédents à travers un projet conséquent. Nous devrons prendre conscience des difficultés liées au travail de groupe, ainsi qu'apprendre à planifier un travail sur plusieurs mois. Au terme du semestre, nous devons rendre un programme complet et fonctionnel, avec une documentation adéquate et être capables de le présenter et le défendre.

Dans le cadre du projet, l'équipe de programmation est composée du chef d'équipe Ludovic Delafontaine, de son remplaçant Lucas Elisei et des membres David Truan, Thibaut Togue, Yosra Harbaoui et Denise Gemesio.  
Dans ce rapport, nous allons expliquer notre démarche de travail et les principaux choix d'architecture et de design de code. Il sera structuré selon les principaux paquets de notre application. Plus un paquet ou une classe est complexe et importante au sein de notre projet, plus la description sera dense et entrera dans les détails.


## Objectif
Le but de notre programme est de proposer une application client-serveur qui permettra aux clients d'envoyer des fichiers musicaux au serveur pour que celui-ci les joue. Il se démarque d'une simple application de lecture en continu (streaming) dans le fait que la liste de lecture ne peut être changée que par les clients par le biais d'un système de votes positifs ou négatifs. Ceux-ci permettent à un morceau présent d'être placé plus en avant ou en arrière dans la liste de lecture. Ceci permet donc à chacun de donner son avis, tout en centralisant la lecture de la musique sur un seul ordinateur. En plus de cela, l'application met à disposition les fonctionnalités suivantes pour une expérience encore plus communautaire:

- Vote pour passer au morceau suivant. Lorsqu'une majorité absolue (plus de 50 pourcent) des clients ont voté pour passer au morceau suivant, la piste en écoute est remplacée par le morceau qui la suit dans la liste de lecture.
- Même principe de vote pour augmenter ou diminuer le volume.
- Système de favoris pour permettre aux utilisateurs de sauvegarder les informations (titre, auteur, etc.) en local dans une liste de lecture spécifique.

Cette application visera principalement les soirées avec plusieurs personnes et répondra à l'éternel problème de devoir se passer une prise jack ou de devoir se battre pour pouvoir passer un morceau que l'on aime.


# Conception / Architecture
Description  diagramme des cas d'utilisation avec figure. Diagramme UML, la sauvegarde...


# Description technique

utiliser les packages pour la description
Ne pas mettre toutes les classes !


## Gestionnaire de configuration
Nous avons choisi d'implémenter un gestionnaire de configuration utilisant le fichier commusica.properties pour permettre à l'utilisateur de configurer le programme. Il donne accès aux paramètres suivants :    

 +  DEBUG : permets de choisir d'afficher ou non la sortie du programme
 +  DATE_FORMAT : choix du format de la date
 +  VOLUME_STEP : choix du pas d'augmentation et abaissement de la musique
 +  TRACKS_DIRECTORY : choix du chemin relatif où les chansons seront stockées
 +  TIME_BEFORE_SESSION_INACTIVE : choix du délai d'inactivité d'une session
 +  TIME_BETWEEN_PLAYLIST_UPDATES : choix du délai de mise à jour des playlists et leurs chansons


## Package core
Pour garder un niveau d'abstraction le plus élevé possible, nous avons voulu faire transiter à travers un contrôleur toutes les informations venant du réseau et des utilisateurs, le but étant d'avoir le même point d'entrée que l'on soit client ou serveur. Pour cela, il nous fallait un contrôleur central qui puisse être appelé de la même façon, quel que le choix de l'identité - client ou serveur. C'est alors à celui-ci de vérifier l'existence d'une fonction et de communiquer l'action à exécuter à l'entité concernée. Notre raisonnement nous a mené à nous tourner vers la réflexivité offerte par Java pour résoudre ce problème. Ce mécanisme permet d'instancier des méthodes à l'exécution en utilisant la méthode `invoke(Object obj, Object... args)` ayant comme premier paramètre un String représentant le nom de la méthode à invoquer et comme deuxième paramètre un tableau d'`Object` contentant les différents arguments dont la méthode invoquée a besoin (voir utilisation dans notre programme **FIGURE**).


Il nous fallait maintenant une classe qui puisse jouer le rôle du contrôleur. Nous avons développé les `Core` pour cela qui sont tous dans le paquet `core`.

### Classes du package
#### Core
C'est une classe statique qui joue le rôle de point d'entrée. Elle dispose d'un attribut `AbstractCore` qui sera instancié soit en `ClientCore` ou en `ServerCore`. Elle met aussi à disposition des méthodes statiques qui nous permettront de les appeler depuis les autres classes du programme.  
Parmi ces méthodes, la plus importante est la suivante:  

```java
public static String execute(String command, ArrayList<Object> args)
```
 Cette méthode se contente d'appeler la méthode du même nom de l'instance de `AbstractCore` de la classe et sera appelée partout où une action du Core est demandée.
Elle contient aussi des méthodes lui permettant de se paramétrer comme client ou serveur.

#### ICore
C'est un interface qui définit ce qui est nécessaire au Core à savoir des méthodes permettant d'envoyer des messages en Unicast ou en Multicast et une méthode pour stopper le Core. Toutes les classes héritant de `AbstractCore` doivent implémenter cette interface.

#### AbstractCore
Cette classe abstraite met à disposition les méthodes permettant à ses sous-classes de s'exécuter correctement. Contrairement à `Core` cette classe va utiliser la réflexivité dans sa méthode execute(), comme ceci:  

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
Nous recevons une commande et un tableau correspondant aux arguments de la méthode à invoquer. Ensuite, le programme essaie de trouver la méthode ayant un nom correspondant à la commande, si elle est disponible dans l'instance de la classe. Si c'est le cas, cette dernière va l'invoquer et donc exécuter ladite méthode, sinon une exception est levée. C'est grâce à cette méthode que tout prend son sens, car on a maintenant une instance d'`AbstractCore` qui est soit `ClientCore` soit `ServerCore` avec une seule méthode pour en appeler d'autres qui seront, elles, implémentées dans les sous-classes d' `AbstractCore`.

#### ServerCore et ClientCore
Ces deux classes héritant de `AbstractCore` et implémentant `ICore` sont les classes les plus importantes du projet. C'est ici que la majorité des actions (transfert de la musique, action à effectuer lors d'un appui sur un bouton, etc.) se fera. Lors de l'envoi des commandes, ces classes fonctionnent avec un système d'états dans lequel ces derniers peuvent être changés en recevant des commandes depuis le réseau ou depuis le code. Elles ont une forte interaction avec les classes s'occupant des échanges réseau puisque c'est ici que toutes les informations reçues depuis le réseau vont passer. Grâce à la réflexivité offerte par l'`AbstractCore`, il est donc extrêmement facile de définir de nouvelles méthodes dans ces classes. Pour cela, il faut déclarer une méthode portant le nom d'une commande - commandes qui seront toutes listées dans la classe `ApplicationProtocol`.

### Synthèse du paquet core
Grâce à ces classes, nous avons réglé le problème de contrôleur central par lequel tout transitera. La réception des commandes à invoquer sera expliquée plus tard dans le chapitre sur le paquet `Network` et lors des explications sur la liaison entre l'interface graphique et le code.


## Package database
La sauvegarde et le chargement font partie des points importants de notre application, car elle a été conçue pour permettre, par exemple, à un utilisateur de sauvegarder les metadatas des chansons qui lui plaisent dans la base de données. Ce package est constitué essentiellement de la classe `DatabaseManager` donc le rôle est d'assurer le CRUD (Create, Read, Update, Delete) de la base de données de notre application et d'assurer la fermeture de la connexion à celle-ci.

Pour l'implémentation, nous avons choisi le framework Hibernate qui simplifie le développement de l'application Java pour interagir avec la base de données. C'est un outil open source et léger.
Un outil ORM (Object Relational Mapping) simplifie la création, la manipulation et l'accès aux données. C'est une technique de programmation qui mappe l'objet aux données stockées dans la base de données.


## Package file
Le package `file` a pour rôle d'assurer la gestion des fichiers en interagissant avec le système de fichiers . Il est constitué de deux classes: `FileManager` et `FilesFormats`.

La performance du framework Hibernate est rapide, car le cache est utilisé en interne dans le cadre hiberné.
Le framework Hibernate offre la possibilité de créer automatiquement les tables de la base de données. Il n'est donc pas nécessaire de les créer manuellement.

 + `FilesFormats`: vu que notre application supporte trois formats mp3, m4a et wav, la classe permet de définir les caractéristiques d'un fichier, c'est-à-dire tous les éléments nous permettant de connaître le type du fichier.
 + `FileManager`: cette classe permet de supprimer, stocker et déterminer le type de fichier.
 Pour retrouver l'extension du fichier, nous avons procédé de la manière suivante :

	+ Pour les fichiers mp3, nous regardons les trois premiers bytes depuis le début du fichier.
	+ Pour les m4a, on regarde les premiers octets, mais en partant du quatrième octet depuis le début du fichier
	+  Pour les wav, à partir du huitième octet depuis le début du fichier.

Connaître le type de fichier nous permettra de traiter uniquement les fichiers supportés pas notre plateforme et aussi, en termes de sécurité, éviter qu'un utilisateur fasse planter le serveur en envoyant un fichier qui n'est pas supporté par celui-ci.


##  Package media
** DG : il me semblerait pas mal d'expliquer dans les grandes lignes à quoi sert ce package**
### Classes du package
#### EphermeralPlaylist
La classe EphermeralPlaylist représente la playlist en cours de construction, c'est-à-dire la playlist en cours de lecture. Cela permet de mettre à jour l'interface graphique lors d'une action sur un élément de la playlist. La mise à jour se fait grâce au pattern observeur à travers la liste `ObservableSortedPlaylistTrackList`, qui joue en même temps le rôle d'observable et d'observeur. Elle observe des chansons de la liste dans le but de changer l'état de la playlist en cas d'upvote ou downvote, et devient observable dans le cas où elle envoie des notifications lors des mises à jour. Dans cette classe, nous avons aussi le champ `delegate` qui représente la liste de lecture qui sera enregistrée dans la base de données pour le suivi de celle-ci.

#### Player
Comme son nom l'indique, il s'agit d'une classe permettant de réaliser les actions de base sur la musique (play, pause, stop, next, previous). Pour l'implémentation, nous avions le choix entre `Mediaplayer` et `sourceDataLine`, nous avons préféré utiliser `Mediaplayer` pour les raisons suivantes:

 + Facile à implémenter
 + Accepte plus de formats que sourceDataLine. Par exemple, mp3 n'est pas supporté par sourceDataLine.

 Le concept de JavaFX media est basé sur les entités suivantes:

  + `Media` media resource, contient des informations sur les médias, telles que leur source, leur résolution et leurs métadonnées
  + `MediaPlayer` est le composant clé fournissant les contrôles pour la lecture de médias.
  + `MediaView` permettant de supporter l'animation, la translucidité et les effets.

Nous avons aussi utilisé dans cette classe les propriétés JavaFX dans le but de mettre à jour de manière automatique l'interface utilisateur lorsqu'une modification se produit.

#### SavedPlaylist
**DG : Est-ce qu'on a vraiment besoin de décrire cette classe en une ligne?**
Comme son nom l'indique, elle permet de sauvegarder les playlists.

#### Track

Cette classe représenté une chanson, elle en regroupe toutes les informations nécessaires à une identité unique. Nous remarquons, dans cette classe, que nous avons trois constructeurs :

  + Le constructeur vide : toutes les classes persistantes doivent avoir un constructeur par défaut pour que Hibernate puisse les instancier en utilisant le constructeur `Constructor.newInstance()`.
  + `public Track(String id, String title, String artist, String album, Integer length, String uri)` : constructeur permettant de créer une instance de `Track` lorsque tous les paramètres sont connus.
  + `public Track(AudioFile audioFile)` : constructeur permettant de créer une instance de `Track` à partir d'un fichier audio. Il est utile lorsque nous souhaitons transférer un fichier et effectuer un contrôle sur une chanson au lieu de vérifier le fichier audio lui-même.


##  Package network
Une autre partie importante de notre programme était la gestion du réseau entre les client et le serveur. Nous nous sommes longuement penchés sur la question de qui devait avoir quelles responsabilités. Notre application demande plusieurs points au niveau du réseau:

+ Avoir un protocole réseau qui se base sur des commandes avec arguments.
+ Le serveur doit pouvoir gérer plusieurs clients, mais sans devoir garder une connexion constante entre chaque client et le serveur.
+ Les clients doivent pouvoir avoir une découverte des serveurs disponibles.
+ Les serveur doivent pouvoir envoyer une mise à jour de leur liste de lecture actuelle à tous les clients. Ces derniers ne devront traiter que les informations qui viennent du serveur auquel ils sont connectés.
+ Un client ne doit pas pouvoir upvoter/downvoter plusieurs fois le même morceau.

Nous avons décidé d'opter pour une architecture avec un thread réceptionniste `Server` au niveau du serveur qui va attendre une nouvelle connexion de client sur son socket et lancer un thread `UnicastClient` qui va s'occuper de la communication avec le client. Cette communication se fait via un socket unicast puisque toutes les informations, mis à part la mise à jour de la playlist, vont transiter du client au serveur.  
La classe `UnicastClient` va recevoir les commandes venant du réseau et en renvoyer. Sa force réside dans le fait qu'elle peut être utilisée aussi bien du côté serveur que du côté client grâce à un système de lecture de flux d'entrée, tant que la fin d'une commande n'est pas détectée ou que l'autre partie n'a pas fermé son socket. Le socket est fermé lorsque la commande `END_OF_COMMUNICATION` est reçue.  
Après la réception de la ligne `END_OF_COMMAND`, la lecture du flux d'entrée est arrêtée et la commande est séparée pour en extraire la partie commande et ses différents arguments.


##  Package playlist
Le package `playlist` met en oeuvre ce qui a trait à la gestion des playlists, dans notre cas :

  +  Le lien entre une certaine chanson et les playlists dans lesquelles elle se trouve.
  +  La sélection d'une certaine playlist.
  +  La gestion des upvotes et downvotes concernant les chansons contenues dans une playlist spécifique.

### Classes du package
#### PlaylistManager
La classe `PlaylistManager` représente un gestionnaire de playlists et a plusieurs utilités :

  +  Récupérer la playlist en cours de création
  +  Récupérer les playlists sauvegardées
  +  Récupérer la playlist des favoris
  +  Ajouter/supprimer des chansons à la playlist des favoris
  +  Créer/supprimer une playlist
#### PlaylistTrack
La classe `PlaylistTrack` permet non seulement de représenter le lien entre une chanson et une playlist, mais aussi de connaître le nombre de votes de la chanson, ce qui sera ensuite utile au niveau de la classe `VoteComparator` qui organise les chansons dans la playlist selon le nombre de votes. Cela peut être fait grâce au fait que `PlaylistTrack` met à disposition une variable `votesProperty` à laquelle un observeur a été ajouté afin que l'interface graphique se réorganise correctement.
#### PlaylistTrackId
Cette classe permet de créer le lien entre une certaine playlist et une chanson. Grâce à l'implémentation d'un hashcode, nous pouvons se servir de celui-ci afin de vérifier que la chanson reliée à la playlist n'existe pas déjà.
#### VoteComparator
Le comparateur de vote ne possède qu'une fonction. Celle-ci sert tout simplement à déterminer entre deux chansons, laquelle a le plus grand nombre de votes. Cela a été créé dans le but de réorganiser la playlist en commençant par les chansons les plus votées.

##  Package et ressources ui
Concernant l'interface graphique, nous avons utilisé la librairie JavaFX. Celle-ci nous a permis de faire usage de l'outil SceneBuilder afin de développer, en premier lieu, une maquette qui s'est ensuite développée, à travers plusieurs étapes, en l'interface graphique que nous connaissons aujourd'hui. Le fonctionnement JavaFX demande à avoir deux notions qui communiquent entre elles: un ou plusieurs fichiers FXML qui définissent l'arrangement de la fenêtre et une ou plusieurs classes Java qui permettent de lancer la fenêtre et communiquer avec ses composants.
Il est donc intéressant de connaître le cheminement que nous avons parcouru jusqu'au résultat actuel.
Dans un premier temps, nous avons développé un fichier FXML grâce à SceneBuilder. Grâce à celui-ci, nous avons pu apprendre les bons usages FXML. Nous avons ensuite créé un fichier Java depuis lequel nous étions capables lancer la fenêtre au démarrage du programme. Cependant, le code se développant devenant de plus en plus important, nous avons pris la décision de diviser aussi bien les fichiers FXML que les fichiers Java en plusieurs sections permettant d'avoir un regard plus précis sur chaque partie de notre implémentation.
Ainsi, nous avons aujourd'hui plusieurs classes Java et plusieurs fichiers FXML qui sont reliés à leur classe principale `UIController.java` respectivement `main.fxml`.

### Classes du package
La description des classes se fera selon l'ordre des vues dans l'interface graphique, en partant de la vue en haut à gauche pour finir par la vue en bas au centre. Nous allons tout d'abord commencer par la fenêtre de configuration apparaissant au lancement du programme, pour continuer avec le controleur. Le reste des classes sera ensuite abordé.

#### ClientServerDialog
** DG : revoir texte car certaines choses ont changé depuis **
`ClientServerDialog` est la première fenêtre lancée par le programme. Son lancement se passe alors dans la classe principale `Commusica`. Cette fenêtre permettra tout simplement de choisir entre deux rôles : celui du serveur ou de l'utilisateur lambda.
Le choix sera communiqué au Core qui prendra connaissance de la décision, configurera le programme et exécutera le lancement de l'interface graphique appropriée.
Après cela, cette classe lancera l'`UIController`.
Dans le cas où l'utilisateur ne répond pas à la question posée dans la fenêtre de dialogue et la ferme, le programme s'arrête.

#### UIController
** DG : revoir texte car certaines choses ont changé depuis **
`UIController` est la classe qui permet de lier le reste des classes entre elles. Elle va, en premier lieu, mettre à jour la partie en haut à gauche contenant les playlists et la playlist sélectionnée sera par défaut celle en cours de construction.
Mis à part la configuration initiale de la fenêtre, `UIController` permet aussi toutes les actions basiques de l'interface graphique : 

+  Afficher des alertes
+  Obtenir la playlist actuellement visualisée
+  Mettre à jour et afficher les playlists
+  Fermer la fenêtre proprement lorsque l'utilisateur décide d'arrêter le programme
`UIController` va tout simplement faire appel aux différentes classes du package `ui` afin de s'informer de l'état de chaque partie composant l'UI lors d'une demande depuis l'extérieur.

#### PlaylistsListView
`PlaylistsListView` concerne la vue en haut à gauche affichant les playlists disponibles :

  +  **PLAYING** : la playlist en cours de création
  +  **FAVORITES** : la playlist des favoris
  +  **SAVED** : la liste des playlists sauvegardées d'anciens événements
Comme spécifié au chapitre précédent, la liste sélectionnée par défaut est la liste en cours de création.
Dans la classe `PlaylistsListView`, nous faisons usage d'une méthode de la classe `FXCollections` permettant d'attacher un observeur à n'importe quel objet du programme. Ainsi, nous pouvons facilement modifier l'affichage des playlists au fur et à mesure des actions faites au niveau du serveur ou du client.

#### TracksListView
La classe `TracksListView` agit sur le panneau en haut au centre de l'interface graphique principale en le dessinant et définissant les usages basiques de celui-ci.
Cette classe permet d'afficher une playlist et glisser/déposer un élément audio au sein du panneau, grâce à la méthode `initializeDragAndDrop()`. La liste de lecture est initialisée comme liste observable, ce qui fait que dès qu'un changement subvient, celle-ci se met à jour. Cependant, elle n'est pas encore peuplée par les chansons contenues dans la playlist. Cette question ainsi que celle des upvotes, downvotes et favoris sont traitées dans une autre classe implémentée spécialement pour cet usage : `PlaylistTrackCell`.

##### initializeDragAndDrop()
La méthode `initializeDragAndDrop()` de la classe `TracksListView` mérite une explication plus détaillée. Nous avons longtemps réfléchi à la meilleure façon d'implémenter le téléchargement d'une chanson. Le "drag and drop" (glisser/déposer) nous a finalement semblé être la technique la plus intuitive d'ajout de chansons.
Cette méthode relativement complexe nous permet donc de déterminer quand une personne  a déposé un fichier dans le panneau et ce, grâce à la méthode JavaFX `setOnDragDropped()` de la classe `TransferMode`. C'est alors que nous allons faire usage du constructeur de la classe `Track` prenant en paramètre un `AudioFile`. 
Si c'est le serveur qui a glissé/déposé une chanson, alors la méthode appellera directement la méthode du `PlaylistManager` permettant d'ajouter une chanson.
Dans le cas du client, la méthode passera d'abord par la classe `Core` à laquelle il enverra la commande `SEND_TRACK_REQUEST` avec comme argument l'URI de la chanson.
Nous remarquons ici, encore une fois, l'intérêt et l'importance de la classe `Core`.

##### PlaylistTrackCell
`PlaylistTrackCell` est une classe utilisée dans chaque cellule de la liste afin de définir les boutons d'upvote, downvote et favoris et leurs actions. Elle va également permettre de définir le titre, l'artiste, l'album et le nombre de votes d'une chanson.
Concernant les votes, deux fonctions - une pour les votes positifs et l'autre pour les votes négatifs - permettent de communiquer avec le `Core` à travers des commandes. Les commandes - `SEND_DOWNVOTE_TRACK_REQUEST` et `SEND_UPVOTE_TRACK_REQUEST` - sont utilisées dans ces deux cas spécifiques car l'incidence qu'aura un vote sera globale à tous les participants. Ainsi, le `Core` doit être averti du fait que l'événement a eu lieu pour en informer le serveur afin qu'il renvoie l'information à tout le monde. Encore une fois, le `Core` use de son pouvoir de messager à travers le programme.
Dans le cas des favoris, il n'y a nul besoin de passer par le `Core` car tout ce que l'utilisateur veut, c'est enregistrer l'information dans sa liste personnelle de chansons favorites.

#### SettingsView
**DG : TODO**
*En haut à droite*

#### PreviousTrackView
Dans le panneau en bas à gauche, nous pouvons apercevoir un espace réservé à la chanson qui vient de se terminer. Ce panneau nous a semblé utile de par le fait que, souvent, nous nous sommes personnellement retrouvés à vouloir noter le nom d'une chanson que nous venions d'écouter et, le temps de prendre notre téléphone pour identifier ladite chanson, celle-ci avait eu le temps de se terminer. Ainsi, ce panneau offre la possibilité à tout un chacun de retrouver facilement et sauvegarder en un seul "clic" les informations d'une chanson.
Pour ce panneau, nous n'avons pas repris le même type de cellule que dans le panneau central du haut, car il n'y a pas de sens au fait de pouvoir upvoter ou downvoter une chanson déjà écoutée. C'est pourquoi, nous avons créé un panneau sur mesure contenant uniquement l'étoile des favoris et permettant ainsi uniquement d'ajouter la chanson dans ses favoris.

#### CurrentTrackView
Dans le panneau du bas, au milieu, nous pouvons apercevoir le résumé de la chanson actuellement en écoute. Les boutons ainsi que les informations sont exactement les mêmes que dans le cas de la dernière chanson grisée affichée dans la liste de lecture du panneau en haut au centre.
Nous avons choisi cet affichage de façon à pouvoir faciliter l'accès à la chanson actuelle si jamais l'utilisateur avait décidé de faire défiler la liste de lecture et aurait perdu de vue la chanson actuelle.
Le vrai défi de cette classe a cependant été celui de pouvoir remplir la jauge d'écoute selon l'avancement de la chanson. Cela a évidemment été fait à travers un observeur sur l'instance de `Player` qui possède l'information sur le temps écoulé.
Sur la gauche du panneau central, nous pouvons également apercevoir des boutons de controle.

##### PlayerControlsView
`PlayerControlsView` qui se trouve dans le même panneau que `CurrentTrackView`représente les boutons "play/pause", "chanson suivante", "chanson précédente" et "volume". Ces quatre boutons représentent en fait cinq actions distinctes qui transiteront toutes à travers le `Core`. En effet, nous nous trouvons encore une fois face à une classe de l'interface graphique dont le `Core` est indispensable à son bon fonctionnement.
Le `Core` est en mesure de déterminer vers qui il devra tourner la demande d'action à travers l'une des commandes suivantes :

  +  SEND_TURN_VOLUME_DOWN_REQUEST : pour baisser le volume
  +  SEND_TURN_VOLUME_UP_REQUEST : pour augmenter le volume
  +  SEND_NEXT_TRACK_REQUEST : pour écouter la chanson suivante
  +  SEND_PREVIOUS_TRACK_REQUEST : pour écouter la chanson précédente
  +  SEND_PLAY_PAUSE_REQUEST : pour arrêter ou démarrer la musique
Dans ces cas précis, c'est la classe `UserSessionManager`qui sera concernée par la commande. 
Finalement, nous voyons dans cette classe encore une trace de ce que nous avions initialement implémenté. En effet, comme dans tous controleurs de musique, les boutons play/pause, chanson suivante et chanson précédente sont toujours présents. Cependant, dans le concept que nous visions à créer, nous n'avons jamais voulu permettre aux utilisateurs de revenir en arrière mais bien de se trouver dans un flux continu de musique.

### Fichiers FXML
Le fichier FXML de base, tout comme le fichier Java de base, a été découpé en plusieurs parties histoire de pouvoir plus facilement comprendre l'implémentation et la modifier.
Comme dans les fichiers Java, nous avons un fichier principal, sans surprise `main.fxml` qui permet de découper l'interface principale en plusieurs panneaux. Ensuite, pour chacun des panneaux, nous avons des fichiers portant le même nom que leurs classes Java.
Ce qu'il faut savoir par rapport aux fichiers FXML, c'est que nous avons dû ajouter un ID à chaque structure dont les actions nous intéressaient, et, pour certaines, nous avons également dû lier une action à une certaine méthode. Ainsi, dès l'instant que, dans le code Java, nous rencontrons un `@FXML`, cela veut dire que nous avons un lien direct avec les structures des fichiers FXML.

##  Package utils
Le package `utils` réunit tous les utilitaires dont nous avons eu besoin au sein de plusieurs classes et dont l'implémentation n'avait aucun sens au sein desdites classes. L'utilité de chaque classe diffère alors énormément.
### Classes du package
#### Configuration
Cette classe permet la récupération des configurations de base du programme. Elle fixe le fichier de configuration que nous avons introduit précédemment, au chapitre **Gestionnaire de configuration** et en tire des informations.
#### EphemeralPlaylistSerializer
Cette classe permet de sérialiser et désérialiser une playlist en JSON. L'utilité de cette classe réside alors principalement dans la communication réseau.
#### Logger
Cette classe a été créée uniquement pour assouvir le besoin d'un débogueur indiquant dans quelle classe a lieu une action. Des couleurs ont été attribuées aux différentes notifications.

  +  Bleu pour les informations
  +  Rouge pour les erreurs
  +  Vert pour les succès
  +  Jaune pour les avertissements
L'affichage des logs peut tout à fait être désactivé au niveau du fichier de configuration `commusica.properties` en réglant la valeur de `DEBUG` à 0.

#### Network
Cette classe permet de récupérer toutes les informations basiques de la machine concernant le réseau. Elle va en outre permettre de récupérer les interfaces disponibles nécessaires à la connexion à un certain serveur et de configurer le réseau pour le reste de l'application.
#### ObservableSortedPlaylistTracklist
Cette classe permet de récupérer les informations nécessaires à l'affichage des chansons dans la playlist en écoute. Cet utilitaire a été créé afin de pouvoir faciliter la récupération d'informations depuis les classes mettant en oeuvre l'interface graphique.
#### Serialize
Grâce à la librairie Gson de Google, cette classe est utilisée dans la sérialisation et désérialisation d'objets.

#### Session
Ce package permet de gérer les sessions des utilisateurs. Avant tout, nous allons expliquer l'importance de la session pour une communication client-serveur : la session permet aux serveurs de mémoriser des informations relatives au client, d'une requête à l'autre. Le contenu d'une session est conservé jusqu'à ce que l'utilisateur ferme sa connexion ou reste inactif trop longtemps.
 
##### ServerSession
Cette classe permet de gérer la session d'un serveur. Le champs privé `update` permet de définir le temps où la session reste valable.

#####  ServerSessionManager
Cette classe, comme son nom l'indique, permet de gérer les différentes sessions des serveurs. Elle est constituée de :

 + Une `Map` permettant de stocker les sessions des différents serveurs.

+ `ScheduledExecutorService` permettant de nettoyer les sessions dont le délai a expiré. C'est un service qui peut également planifier des tâches à exécuter après un délai ou à plusieurs reprises avec un intervalle de temps fixe entre chaque exécution et ce, de manière asynchrone. C'est un nouveau thread qui s'occuepera de gérer cela, et non par le thread passant la tâche à `ScheduledExecutorService`.
  
##### UserSession
Cette classe permet de gérer la session d'un utilisateur. Le champs privé `update` permet de définir le temps où la session reste valable.

##### UserSessionManager
** DG : Est-ce qu'il ne faudrait pas développer cette classe un peu plus? elle me semble relativement importante. **
Cette classe, comme son nom l'indique, permet de gérer les différentes sessions des utilisateurs. 


## Technologies utilisées 
 
### GitHub
** DG : on confond Git et GitHub dans les énumérations, à revoir **
Github est un outil gratuit permettant d'héberger du code open source, et propose également des plans payants pour les projets privés.
Nous avons utilisé Github pour les raisons suivantes:

+ Elle permet une meilleure gestion des branches
+ Fusion ("merge"): quand un fichier a été modifié par plusieurs personnes en même temps, Git sait s'adapter et choisir un algorithme qui fusionne intellignemment les lignes du fichier qui ont été modifiées.
+ Le nombre de personnes par repository n'est pas réduit comme dans Bitbucket, par exemple.
### IntelliJ IDEA
Un environnement de développement intégré, autrement dit, un ensemble d'outils destinés au développement logiciel. Les avantages dans l'utilisation d'IntelliJ IDEA sont les suivantes.
   
 + Il nous propose la fonctionnalité de pull-request pour GitHub.
 + Une autocomplétion hors pair.
 + L'analyse et inspection : il analyse en temps réel et en permanence le code, à la recherche de problèmes potentiels.
   
### Apache Maven
Apache Maven est un outil puissant de gestion de projet basé sur POM (modèle d'objet de projet). Il est utilisé pour la construction, la dépendance et la documentation des projets. Les avantages d'utilisation Maven sont les suivants :

+ Il facilite la construction d'un projet.
+ Il fournit un processus de construction uniforme (le projet Maven peut être partagé par tous les projets Maven). ** DG : pas très clair **
+ Il fournit des informations sur le projet (document, liste de dépendances, rapports de tests, etc.).

### Scene Builder

Scene builder est un outil qui permet de créer des fichiers au formats FXML via un éditeur graphique. Les avantages de Scene Builder sont les suivants :

** DG : TODO **

### Wireshark
Wireshark est un outil essentiel pour comprendre les mécanismes de fonctionnement des protocoles de commuinication sur les réseaux. Il capture des paquets directement sur les interfaces du système utilisé ou lit des fichiers de captures sauvegardées. Nous l'avons utilisé dans notre projet pour sniffer la communication entre le client et le serveur afin de controler le bon fonctionnement de la communication réseau.

### JAudiotagger
** DG : il me semble intéressant de développer un peu plus cet outil **
JAudiotagger est une API Java pour la lecture et l'écriture des métadonnées des fichiers audio. Il supporte des formats tels que MP3, MP4, WAV, etc.

### JavaFX
** DG : développer un peu plus les avantages de JavaFX, peut-être en comparaison avec Swing **
JavaFX est une bibliothèque Java permettant la création d'applications Desktop. Les applications écrites à l'aide de cette bibliothèque peuvent fonctionner sur plusieurs plateformes. Les applications développées à l'aide de JavaFX peuvent fonctionner sur différents périphériques tels que les ordinateurs, les téléviseurs, les tablettes, etc.

### Capsule
** DG : TODO **


## Tests réalisés

** DG : cette liste me semble relativement massive, on pourrait faire des sous-chapitres **
** DG : attention, certains de ces points, à cause de la tournure de phrase, sont des observations et non des tests **
** DG : j'ai déplacé certaines observations de points qui ne marchent pas dans "Problèmes subsistants" **

+ Lancement du programme avec chacun des rôles
+ Choix entre serveur et client fonctionnel
+ La playlist est bien émise de la part du serveur 
+ La liste des serveurs disponibles se met bien à jour côté client 
+ Le client peut sélectionner et se connecter au serveur
+ Le client reçoit et met à jour sa playlist en fonction de celle du serveur auquel il est connecté (IL FAUDRA TESTER AVEC PLUSIEURS SERVEURS)
+ Le client peut envoyer une chanson au serveur qui l'accepte ou la refuse (A TESTER PLUS EN DÉTAILS)
+ Le serveur reçoit la chanson et met à jour sa playlist (PAS SÛR QUE ÇA SOIT MIS À JOUR DANS LA DB)
+ Le client reçoit la mise à jour de la playlist
+ Le client peut upvoter et downvoter une chanson et cette dernière se met à jour. ** DG: ce qui suit fait plutôt partie du chapitre "Problèmes subsistants"** Ne marche pas côté serveur.
+ Le client peut augmenter et diminuer le volume. Ne marche pas côté serveur. **DG : "Problèmes subsistants**
+ Coté client, la base de données se met bien à jour lors de l'ajout de chansons, mais la date à laquelle elle a été jouée manque dans l'EphemeralPlaylist et donc ne se met pas à jour dans la base de données du client. **DG : "Problèmes subsistants**
+ Le bouton play/pause marche et change d'état côté client et le bouton marche côté serveur, mais dans ce dernier cas, le bouton côté client ne change pas d'état. **DG : "Problèmes subsistants**
+ La PlaylistTrack se met bien à jour dans la base de données lorsqu'une chanson a été jouée côté client.
+ Favoriser une chanson côté client l'enregistre bien dans la base de données
+ A la fermeture du programme côté client, toutes les tracks qui n'ont pas été jouées sont bien effacées de la base de données
+ A la fermeture du programme côté serveur, les tracks enregistrées sont bien effacées
+ Côté serveur, les tracks sont bien ajoutées à la base de données.

## Problèmes subsistants
+ Côté client, une chanson qui n'a pas été jouée se met au dessus des chansons qui ont déjà été jouées si elle a plus de vote que les chansons déjà jouées
+ La barre du temps est manquante au niveau du client
+ Le bouton *favoris* situé dans l'interface de contrôle ne marche pas côté client et serveur 
+ Le fait de favoriser une chanson ne l'enregistre pas côté serveur (ne s'affiche pas dans la playlist "Favoris")
+ Les playlists tracks associées ne sont par contre pas effacées (il doit manquer le CASCADE au niveau de la db pour que ça efface aussi)

## Améliorations potentielles

## Planification / organisation

# Conclusion

# Bilan

## Bilan du groupe

## Ludovic
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
## Cahier des charges
## Journal de travail
## Panification initiale et son évolution

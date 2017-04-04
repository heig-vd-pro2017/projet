# Procès verbaux
*Les PV sont en ordre anti-chronologique afin d'avoir accès aux dernières nouvelles plus rapidement !*

## 28.03.2017
* Définition de la hiérarchie des fichiers concernant l'interface graphique: un fichier `.css` global permettant de styliser la globalité des éléments graphiques. Ce fichier sera sûrement généré à l'aide d'un compilateur **SASS** ou **LESS**.
* Il faut trouver des icônes plus adaptées que du SVG pur.
* Définition des différentes méthodes de la classe `FileManager`:
    * Méthode privée `freeSpace(path)` permettant de connaître l'espace disque disponible selon un chemin de fichier/dossier.
    * Méthode privée `reserve(size)` permettant de réserver de l'espace pour sauvegarder une chanson (enlève l'espace nécessaire à l'espace disque disponible).
    * Méthode publique `save(filename, file)` permettant de sauvegarder un fichier dans l'espace de stockage.
    * Méthode publique `delete(filename)` permettant de supprimer un fichier de l'espace de stockage.
    * Un *URI* est relatif et non absolu.
* Définition des différentes méthodes de la classe `PlaylistsManager`:
    * Méthode publique `save(playlist)` permettant de créer/sauvegarder une playlist.
    * Méthode publique `delete(playlist)` permettant de supprimer une playlist existante.
    * Méthode publique `rename(playlist)` permettant de renommer une playlist existante.
    * Méthode publique `load(playlist)` permettant de charger une playlist.
* Fichier de configuration:
    * Une variable contenant le chemin du dossier de stockage des fichiers.
* Hiérarchie des fichiers du programme:  
    * `./commusica.jar` (*à voir*: contient le fichier `.db`)
    * `./commusica.db`
    * `./configuration.properties` (l'extension reste à définir)
    * `./tracks/`: contient les chansons (défini par le fichier de configuration).

## 21.03.2017
Nous avons décidé d'utiliser maven et avons mis en place la hiérarchie de dossiers du projet.
On va abandonner l'idée de GitBook et utiliser Markdown tout de même, pour la documentation.

## 17.03.2017
Nous avons décidé de mettre en place le projet selon une architecture RESTful. Cela nous permettra plus aisément de développer des plugins.
* La semaine prochaine, Ludo et Thibaut démarreront la mise en place de la base de données. Ca prendra environ deux semaines.
* Lucas et Denise, plutôt que de démarrer la mise en place du démarrage et arrêt de l'application, se lanceront dans la création de l'interface graphique à travers Sandbuilder.
* David et Yosra démarreront la mise en place du serveur et le protocole de communication.

## 14.03.2017
* Retour de M. Rentsch sur le cahier des charges:
    * Revoir le contexte : spécifier que le projet se fait dans le contexte du cours PRO au sein de l'HEIG-VD
    * Corriger les titres de l'anglais au français
    * Créer une page de garde avec le titre du projet et les auteurs
    * Mockup : en avoir plusieurs, faits à l'aide d'un logiciel avec des explications
    * Fonctionnalités : plutôt sous forme de diagramme des cas d'utilisation avec une description des fonctionnalités
    * Faire très attention à la liaison des blocs dans le schéma BD
* Discussions de la planification
    * Refaire le diagramme de Gantt avec le template donné par M. Rentsch
    * Création d'un journal de travail pour tout le monde qui début à partir du 15.03.2017
* Discussions autour de l'architecture du programme:
    * Diagramme UML
    * Diagramme de séquence
    * Base de données
    * Protocole de communication

## 13.02.2017
* Discussions autour de l'interface du programme:
    * Connexion : Au niveau de l'interface, on pourra se connecter en tant que serveur ou utilisateur. On aura une boîte
  en accordéon qui nous permettra de choisir le réglage désiré. La fenêtre "Se connecter" sera ouverte par défaut.
  * Interface : **SceneBuilder: une personne pour faire l'interface et la prendre comme exemple de façon à pouvoir
  faire le mockup ainsi que l'implémentation.** Donnera la possibilité de redimensionner la fenêtre.
  * Communication réseau (cours RES) : Protocole à définir, mais l'implémentation sera apprise en classe d'ici
  là. **Prendre de l'avance sur le cours RES pour mettre en place le protocole.**
  * Base de données : utilisation de Hibernate pour SQLite. La configuration faite par Lucas sera sur GitHub.

## 07.02.2017
* Rendu du cahier des charges
* Début de la discussion de l'architecture du programme:
    * Diagramme UML

## 02.02.2017 - 07.02.2017
* Approfondissements dans les fonctionnalités du cahier des charges
* Mise en commun de tous les points importants du fonctionnement de l'application, autant du point de vue des clients
que du serveur
* Rédaction du cahier des charges
* Elaboration de la planification

## 01.03.2017
* Début de la structure du cahier des charges

## 28.02.2017
* Retour de la part de M. Rentsch sur les propositions
* Documentation sur les différents outils qui pourraient servir pour notre projet

## 24.02.2017
* Rendu des abstracts

## 21.02.2017 - 23.02.2017
* Propositions de projets
* Choix de deux projets qui nous tiennent à coeur
* Approfondissements dans les deux projets
* Enumération des fonctionnalités des deux projets
* Rédaction des abstracts pour les deux projets

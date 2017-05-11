# Journal de travail
*Le journal de travail débute le 15.03.2017*

## 10.05.2017
### Ludovic Delafontaine
* Fusion du travail de tout le monde sur la branche master (0h45)

## 07.05.2017
### Ludovic Delafontaine
* Améliorations aux notions de client/serveur (3h00)

## 06.05.2017
### Lucas Elisei
* Finalisation de la fusion du panneau "chanson précédente" (0h30).
* Correction de quelques bugs liés aux précédentes itérations (1h30).

### Ludovic Delafontaine
* Améliorations aux notions de client/serveur (6h00)

## 05.05.2017
### Ludovic Delafontaine
* Revue de code afin de pouvoir tout fusionner (2h00)
* Améliorations aux notions de client/serveur (2h00)

## 03.05.2017
### Lucas Elisei
* Fusion du panneau "playlist en cours de lecture" avec le code (1h00).
* Implémentation d'une structure de données pour le tri des chansons des playlists (2h00).
* Début de la fusion du panneau "chanson précédente" avec le code (0h15).

## 02.05.2017
### David Truan
* Finition de quelques bugs du serveur (1h).
* Délégation du transfert de fichier au FileManager(10min).
* Refonte de la classe Session (30min).

### Lucas Elisei
* Fusion du panneau "chanson en cours de lecture" avec le code (1h00).

## 01.05.2017
### David Truan
* Mise au propre du network (2h)
    * Déconnexion client/serveur.
    * Singletons des threads.
    * Remise en forme du client.

### Lucas Elisei
* Documentation quant à la fusion du code et de l'interface graphique (0h30).

## 30.04.2017
### David Truan
* Transfert de fichier .mp3 et implémentation dans le projet (2h)
* Mise au propre des classes du package network et finition du protocole (1h)
* Mise en place de la mise à jour de la playlist (1h)

## 23.04.2017
### Lucas Elisei
* Implémentation du player dans l'interface graphique (1h00)
* Découpage de l'interface graphique en modules (2h00)

## 21.04.2017
### Lucas Elisei
* Implémentation de chargement de playlists dans l'interface graphique (4h)

## 20.04.2017
### Ludovic Delafontaine
* Suite des tests et version fonctionnelle du player (1h30)

## 19.04.2017
### Ludovic Delafontaine
* Reprise du player, playlist manager et filemanager et intégration avec le reste du projet (4h00)

## 17.04.2017
### Thibaut Togue Kamga
* Finalisation du player et filemanage valider par ludovic.(1h30)

### Lucas Elisei
* Mise à jour de l'interface graphique (panneau central) selon les choix retenus lors de la précédente réunion. (1h00)

## 11.04.2017
### Ludovic Delafontaine
* Début de la mise à jour du schéma UML selon la réelle implémentation (1h00)

## 10.04.2017
### Ludovic Delafontaine
* Réalisation de la présentation (2h00)

### Thibaut Togue Kamga
* Implémentation du player sans playlistManager et test  (3h)		                                                       

## 08.04.2017
### David Truan
* Ajout d'une méthode pour choisir sa bonne interface et modification du code pour prendre en compte cela. (2h)
* Ajout de fonctionnalités au programme de test. (10min)
* Mise au propre rapide des classes et création de a classe Protocol et de packages client/server. (1h)

## 06.04.2017
### Thibaut Togue Kamga
* Tuto playerMedia pour l'implémentation du player(2h)

## 05.04.2017
### Ludovic Delafontaine
* Ajout de la classe Playlist et de sa table associée pour la base de données (01:00)
* Ajout de la classe permettant de récupérer des propriétés depuis un fichier de configuration (00:30)
* Documentation des différentes classes (00:30)

## 03.04.2017
### Ludovic Delafontaine
* Finalisation de la classe Track avec l'ajout de l'interface DatabaseObject
* Merge avec master pour Track

### Togue Kamga Thibaut
 * Implémentation du player avec SourceDataline mais
  inutile cas impossible de lire les mp3 (4h)

## 02.04.2017
### Denise Gemesio
* Fenêtre rendue "redimensionnable" au niveau de la partie player de l'interface (2h)

## 01.04.2017
### David Truan
* Tests pour le Multicast. Toujours des problèmes (2h)

## 31.03.2017
### Ludovic Delafontaine
* Suite de la base de données avec les classes Playlist, PlaylistTrack et PlaylistTrackId (2h30)

### Yosra Harbaoui
* Implémentation simple d'une connexion client/serveur pour tester la connectivité.

## 30.03.2017
### David Truan
* Réflexion sur l'implémentation et début de code pour la découverte de serveurs par les clients (2h)

### Togue Kamga Thibaut
* Tuto sur les file poperties de java et implémentation
du fichier de configuration, modification du file Manager d'après la discussion avec le group pour la réservation de la mémoire et test(3h)

## 29.03.2017
### Lucas Elisei (1h)
* Ajout des actions d'*upvote* et *downvote* pour les chansons (seulement graphique).

## 28.03.2017
### Lucas Elisei (4h)
* Ajout du style des cellules représentant des chansons dans la playlist en cours de lecture (panneau central).
* Tests du player et suite de la base de données (2h00)

## 27.03.2017
### Ludovic Delafontaine
* Création de la classe Playlist et tests avec la base de données associée (5h00)

### Togue Kamga Thibaut
* implémentation du fileManager(2h)

## 26.03.2017
### Denise Gemesio
* Intergace graphique: modification du .fxml avec Scene Builder

### Ludovic Delafontaine
* Mise en place et configuration des outils de compilation (Maven) (2h00)
* Début de la classe Track avec sa table dans la base de données à l'aide de Hibernate (1h30)

## 25.03.2017
### David Truan
* Documentation et première implémentation (test) du multicast en Java. (2h)
* Réévaluation de l'intêret de NetPort en tant que classe. (30min)

### Yosra Harbaoui
* Documentation sur les différents "types" de communications entre un serveur et un client.

## 22.03.2017
### Denise Gemesio
* Interface graphique: tutoriel et documentation (2h30)
   * Installation de Scene Builder et configuration de Intellij
   * Tutoriel sur http://code.makery.ch/library/javafx-8-tutorial/fr/

### Ludovic Delafontaine
* Player
    * Tests de lecture de fichiers audio
    * Tests de récupération des metadatas des fichiers

### Thibaut Togue
* Tutoriel sur [https://www.jmdoudoux.fr/java/dej/chap-hibernate.htm](https://www.jmdoudoux.fr/java/dej/chap-hibernate.htm) pour la réalisation de la couche persistance de l'application avec **ORM**(2h)

## 21.03.2017
### Lucas Elisei
* Interface graphique: premier jet (1h30)
    * Mise en place des principaux composants graphiques
    * Affichage d'une liste de playlists

### David Truan
* Meilleur division client/serveur et base du protocole (1h).

### Ludovic Delafontaine
* Avancement dans la base de données
    * Corrections et améliorations

### Yosra Harbaoui
* Documentation sur l'implémentation client/serveur.

## 20.03.2017
### David Truan
* Essais et documentation sur la partie client/serveur (3h).

### Thibaut Togue
* test du fichier Sqlite à travers des conteneurs docker et correction des bugs(2h)                              

## 18.03.2017
### Ludovic Delafontaine
* Création du schéma de la base de données
* Mise à jour du diagramme UML

## 15.03.2017
### Ludovic Delafontaine
* Ajout des éléments manquants dans les PV
* Création du journal de travail
* Corrections du diagramme de séquence
* Documentation sur Reflexion

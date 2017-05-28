<!---
  Quelques commandes utiles:

  1. Pour compiler le document, il faut que vous ayiez installé Pandoc et XeLaTeX (ou XeTeX).
      La commande pour compiler le document est la suivate:

      pandoc --latex-engine=xelatex --listings Rapport.md -o Rapport.pdf
-->

---
lang: fr

numbersections: true

papersize: a4

geometry: margin=2cm

header-includes:
    - \usepackage{etoolbox}
    - \usepackage{fancyhdr}
    - \usepackage[T1]{fontenc}
    - \usepackage{xcolor}
    - \usepackage{graphicx}
    - \usepackage{tikz}
    - \usepackage{hyperref}
    - \usepackage{caption}

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
    - \fancyhead[RO,RE]{HEIG-VD - PRO 2017}

    # Redefine TOC style.
    - \setcounter{tocdepth}{1}

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

    # Set links colors
    - \hypersetup{colorlinks,citecolor=black,filecolor=black,linkcolor=black,urlcolor=black}
---
\makeatletter
\renewcommand{\@maketitle}{%
\newpage
\null
\vfil
\begingroup
\let\footnote\thanks
\begin{center}
{\huge\@title}\vskip1.5em
\includegraphics[width=10cm, height=10cm]{images/logo.png}\vskip1.5em
{\LARGE Manuel utilisateur}\vskip1.5em
{\large\@author}\vskip1.5em
{\large\@date}
\end{center}
\endgroup
\vfil
}
\makeatother

\title{Commusica\\Le lecteur de musique communautaire et égalitaire}

\author{Chef de projet: Ludovic Delafontaine\\
   Chef remplaçant: Lucas Elisei\\
   Membres: David Truan, Denise Gemesio, Thibaut Togue, Yosra Harbaoui\\
   Responsable du cours: René Rentsch}

\date{HEIG-VD - Semestre d'été 2017}

\maketitle

\begin{tikzpicture}[remember picture,overlay]
   \node[anchor=north east,inner sep=0.25cm] at (current page.north east)
              {\includegraphics[width=5cm]{images/heig-vd.png}};
\end{tikzpicture}

\newpage

\newpage

\tableofcontents

\listoffigures

\newpage

# Introduction
Ce document est le manuel d'utilisation de l'application **Commusica** développée dans le cadre d'un projet de semestre de la section TIC de la HEIG-VD.

**Commusica** est une application permettant aux utilisateurs d'envoyer des fichiers musicaux à un autre utilisateur qui se sera préalablement configuré comme serveur. La communication se fait via un réseau local sans fil ou câblé.
Son intêret et de proposer une expérience communautaire en permettant à tous les utilisateurs de changer l'ordre de la liste de lecture en cours en votant pour ou contre les morceaux s'y trouvant. Ces derniers vont alors changer d'ordre. Le contrôle du volume, de l'arrêt, de la mise en marche du morceau en cours et du passage au morceau suivant, fonctionnent sur un principe de vote et sont effectués lorsque la majorité des utilisateurs actifs d'un serveur ont voté pour réaliser l'action.

Version de Commusica: 1.0

Version du manuel utilisateur: 1.0

# Prérequis
## Système d'exploitation et logiciels
**Commusica** a été testé sur les systèmes suivants et peut donc être utilisé sur:

- Windows 10
- Mac OS 10.11.6

Commusica nécessite au minimum la version 8 de Java, téléchargeable sur leur site [oracle.com/technetwork/java/javase/downloads](oracle.com/technetwork/java/javase/downloads)

## Infrastructure
Pour une configuration en tant que serveur, il faut prévoir suffisamment d'espace de stockage pour recevoir les fichiers audio temporaires. Cela peut donc varier selon l'usage, mais il est préférable de prévoir 1 Go d'espace libre.

De plus, il est nécessaire d'avoir un système qui puisse diffuser de la musique afin que l'application marche.

# Lancement
Les étapes suivantes vont vous permettre de lancer l'application.

- Commencer par récupérer la dernière version de **Commusica** puis enregistrez-la sur le disque dur.
- S'assurer d'avoir les deux fichiers suivants pour le bon fonctionnement de **Commusica** :
    - `commusica-1.0.jar`
    - `commusica-1.0.properties`

## Windows
- Double-cliquer sur le fichier `commusica-1.0.jar`
- Le programme devrait se lancer

Si cela ne marche pas :

- Ouvrir PowerShell depuis le menu `Démarrer`
- Se déplacer à l'endroit où est sauvegardé le fichier `commusia-1.0.jar`
- Taper la commande `java -jar commusica-1.0.jar`
- Appuyer sur `Enter`
- Le programme devrait se lancer

Si un message d'avertissement de la part du pare-feu s'ouvre, il est nécessaire d'autoriser **Commusica** à communiquer sur les réseaux privés.

## Mac OS
- Ouvrir le Terminal
- Se déplacer à l'endroit où est sauvegardé le fichier `commusia-1.0.jar`
- Taper la commande `java -jar commusica-1.0.jar`
- Appuyer sur `Enter`
- Le programme devrait se lancer

# Utilisation
Les étapes suivantes vous expliquer comment utiliser l'application.

*Rappel: certaines actions nécessitent que la majorité des personnes utilisant le système aient fait le même souhait. C'est la raison pour laquelle votre action peut avoir été prise en compte mais qu'elle n'a pas d'effet immédiat.*

## Choix du lancement de l'application
Une fois l'application lancée, la fenêtre ci-dessous apparaît.

\begin{minipage}{\linewidth}
  \centering
  \includegraphics{figures_manuel_utilisateur/premiere_fenetre.PNG}
  \captionof{figure}{Choix de lancement du programme}
\end{minipage}

Vous avez le choix entre lancer l'application en tant que serveur ou client.

- Serveur: réceptionnera la musique et la lira sur le système audio.
- Client: peut envoyer des morceaux de musique au serveur et interagir avec celui-ci.

## Serveur
Les explications suivantes concernent le lancement de l'application en tant que serveur.

Quand l'application est lancée, la fenêtre suivante sera affichée à l'écran.
\begin{minipage}{\linewidth}
  \centering
  \includegraphics[width=\linewidth]{figures_manuel_utilisateur/Server.PNG}
  \captionof{figure}{Interface de l'application lancée en tant que serveur}
\end{minipage}

### Configuration du serveur
Quand vous choisissez de lancer l'application en tant que serveur, une fênetre apparaîtera pour vous demander de nommer votre serveur.

\begin{minipage}{\linewidth}
  \centering
  \includegraphics{figures_manuel_utilisateur/nomServeur.PNG}
  \captionof{figure}{Dialogue pour donner un nom au serveur}
\end{minipage}

## Client
Les explications suivantes concernent le lancement de l'application en tant que client.

Quand l'application est lancée, la fenêtre suivante sera affichée à l'écran.
\begin{minipage}{\linewidth}
  \centering
  \includegraphics[width=\linewidth]{figures_manuel_utilisateur/client.PNG}
  \captionof{figure}{Interface de l'application lancée en tant que client}
\end{minipage}

### Choix du serveur
Quand vous choisissez de lancer l'application en tant que client, vous aurez le choix de vous connecter à l'un des serveurs. Vous pourrez choisir parmi une liste de serveurs disonibles.

\begin{minipage}{\linewidth}
  \centering
  \includegraphics{figures_manuel_utilisateur/server_list.PNG}
  \captionof{figure}{Choix du serveur}
\end{minipage}

Une fois connecté, vous pouvez voir la playlist en cours de lecture et toutes les informations liées aux morceaux de cette playlist.

## Interface commune
Les explications suivantes sont communes aux deux façons de lancer l'application, que ce soit en tant que serveur ou en tant que client.

### Choix de l'interface réseau
Le panneau `Settings` vous offre la possibilité de choisir l'interface réseau à utiliser parmi les interfaces que votre ordinateur met à disposition. Une interface par défaut est sélectionnée, mais si les clients ne vous voient pas ou si vous ne voyez pas le serveur auquel vous souhaitez vous connecter, il est peut-être nécessaire de changer l'interface réseau dans la liste déroulante.

Si tout marche, il n'est pas nécessaire de changer d'interface.

\begin{minipage}{\linewidth}
  \centering
  \includegraphics{figures_manuel_utilisateur/settings.PNG}
  \captionof{figure}{Choix de l'interface réseau}
\end{minipage}

### Choix des listes de lecture et favoris
Ce panneau vous permet de naviguer entre la liste de lecture actuelle, vos morceaux enregistrés en temps que favoris et les différentes listes de lecture qui ont été sauvegardées sur votre ordinateur lors d'utilisation précédentes de **Commusica**.

\begin{minipage}{\linewidth}
  \centering
  \includegraphics{figures_manuel_utilisateur/client_Playlists.jpg}
  \captionof{figure}{Listes de lectures}
\end{minipage}

1. Le panneau `Playlists` contient toutes les listes de lectures.
2. La playlist en cours de lecture
3. La liste des anciennes playlists sauvegardées
4. Les playlists préférées des utilisations précédentes.

### Ajouter de la musique à la liste de lecture en cours
Afin d'ajouter de la musique à la liste de lecture en cours, il suffit de "Glisser-Déposer" le(s) morceau(x) souhaitée(s) dans le centre de l'interface et celles-ci seront ajoutées au système. Si votre morceau n'apparait pas dans l'interface, il est peut-être nécessaire de recommencer. Cela est dû à d'éventuels problèmes réseaux et le programme n'autorise pas un transfert de fichiers prenant plus de 15 secondes.

### Liste de lecture du serveur
Les morceaux présents actuellement dans la playlist en cours de lecture.

\begin{minipage}{\linewidth}
  \centering
  \includegraphics{figures_manuel_utilisateur/client_Playlist_playing.PNG}
  \captionof{figure}{Liste de lecture en cours}
\end{minipage}

Les morceaux lus sont grisés. Le dernier morceau grisé est celui en cours de lecture.

#### Gestion d'une chanson  
Vous avez maintenant une vue sur la liste de lecture du serveur. Vous pouvez, ainsi, voter pour ou contre une ou plusieurs chansons à l'aide des flèches haut et bas. Ceci aura pour conséquence de la réorganiser.

Vous ne pouvez voter qu'une seule fois pour ou qu'une seule fois contre un morceau.
Rien ne vous empêche d'annuler votre vote, mais vous ne pouvez pas voter indéfiniment. De plus, le morceau en cours de lecture ne peut pas être voté pour ou contre, cela n'aura pas d'effet.

Plus le nombre total de votes est grand, plus le morceau aura de chance d'être joué après le morceau en cours de lecture.

\begin{minipage}{\linewidth}
  \centering
  \includegraphics[width=\linewidth]{figures_manuel_utilisateur/track-cell.png}
  \captionof{figure}{Interface de l'application lancée en tant que client}
\end{minipage}

1. Informations du morceau
2. Possiblité de favoriser/défavoriser le morceau
3. Voter pour le passage du morceau
4. Voter contre le passage du morceau
5. Total de votes pour le morceau

### Contrôle de la musique

\begin{minipage}{\linewidth}
  \centering
  \includegraphics{figures_manuel_utilisateur/track_playing.png}
  \captionof{figure}{Contrôle de la musique}
\end{minipage}

1. Arrêter/jouer la musique si la mojorité des utilisateurs le demande
2. Passer au morceau suivante si la mojorité des utilisateurs le demande
3. Augmenter le volume si la mojorité des utilisateurs le demande
4. Diminuer le volume si la mojorité des utilisateurs le demande
5. Informations sur le morceau
6. Temps écoulé pour le morceau
7. Mettre en favori/retirer des favoris le morceau
8. Nombre de votes reçus pour ce morceau

### Morceau précédent  
Les informations concernant le morceau précédant le morceau en cours de lecture.

\begin{minipage}{\linewidth}
  \centering
  \includegraphics{figures_manuel_utilisateur/client_previous_track.PNG}
  \captionof{figure}{Morceau précédent}
\end{minipage}

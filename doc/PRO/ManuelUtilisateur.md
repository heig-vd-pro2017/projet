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
    - \setcounter{tocdepth}{2}

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
{
    \large
    Groupe 4B\vskip0.4em
    \begin{tabular}{ll}
        \underline{Chef de projet} & Ludovic Delafontaine \\
        \underline{Chef remplaçant} & Lucas Elisei \\
        \underline{Membres} & David Truan \\
        & Denise Gemesio \\
        & Thibault Togue \\
        & Yosra Harbaoui \\
        \\
        \underline{Professeur} & René Rentsch \\
    \end{tabular}}\vskip1.5em
{\large\@date}
\end{center}
\endgroup
\vfil
}
\makeatother

\title{%
  Commusica \vskip0.4em
  \large Le lecteur de musique communautaire et égalitaire}

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
Ce document est le manuel d'utilisation de l'application **Commusica**, développée dans le cadre du projet de semestre de la section TIC de la HEIG-VD.

**Commusica** est une application permettant aux utilisateurs d'envoyer des fichiers musicaux à un autre utilisateur, qui a préalablement choisi le rôle de serveur. La communication se fait via un réseau local sans fil ou câblé.
Son intérêt est de proposer une expérience communautaire en permettant à tous les utilisateurs de changer l'ordre d'écoute de la liste de lecture en cours, en votant pour ou contre les morceaux la composant. Le contrôle du volume, de l'arrêt, de la mise en marche du morceau en cours et du passage au morceau suivant fonctionnent sur un principe de vote et sont effectués lorsque la majorité des utilisateurs actifs d'un serveur ont voté pour réaliser l'action.

Version de **Commusica**: 1.0

Version du manuel utilisateur: 1.0

# Prérequis
## Système d'exploitation et logiciels
**Commusica** a été testé sur les systèmes d'exploitation suivants et peut donc être utilisé sur:

- Windows 10
- Mac OS 10.11.6

**Commusica** nécessite au minimum la version 8 de Java, téléchargeable sur leur site, en cliquant sur le lien suivant : [oracle.com/technetwork/java/javase/downloads](oracle.com/technetwork/java/javase/downloads)

## Infrastructure
Pour une configuration en tant que serveur, il faut prévoir suffisamment d'espace de stockage pour recevoir les fichiers audio temporaires. Cela peut donc varier selon l'usage, mais il est préférable de prévoir 1 Go d'espace libre minimum.

De plus, il est nécessaire d'avoir un système qui puisse diffuser de la musique afin que l'application marche et tous les clients doivent être connectés au même réseau local.

# Lancement
Les étapes suivantes vous permettent de lancer l'application :

- Récupérer la dernière version de **Commusica**
- Enregistrer **Commusica** sur le disque dur
- S'assurer d'avoir ces deux fichiers :
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
Les étapes suivantes vous expliquent comment utiliser l'application.

*Attention: pour que certaines actions fonctionnent, il est nécessaire que la majorité des personnes aient sélectionné ladite action. Ainsi, il est possible que cette dernière ne soit pas effective immédiatement.*

## Choix du lancement de l'application
Une fois l'application lancée, la fenêtre ci-dessous apparaît.

\begin{minipage}{\linewidth}
  \centering
  \includegraphics[scale=0.9]{mu-images/client-server-chooser.png}
  \captionof{figure}{Choix de lancement du programme}
\end{minipage}

Vous avez le choix de lancer l'application en tant que serveur ou en tant que client :

- Serveur: réceptionne la musique et la lit
- Client: peut envoyer des morceaux de musique au serveur et interagir avec celui-ci

## Serveur
Les explications suivantes concernent le lancement de l'application en tant que serveur.

### Configuration du serveur
Quand vous choisissez de lancer l'application en tant que serveur, une fenêtre apparaît pour vous demander de nommer votre serveur. Le nom du serveur devient également le nom de la nouvelle liste de lecture. En cas d'appui sur `Annuler`, l'application se ferme.

\begin{minipage}{\linewidth}
  \centering
  \includegraphics{mu-images/server-name.png}
  \captionof{figure}{Fenêtre permettant de donner un nom au serveur}
\end{minipage}

1. Choix du nom du serveur ainsi que de la nouvelle liste de lecture

### Interface serveur
Quand le choix du nom de serveur a été fait, la fenêtre principale est affichée à l'écran.

\begin{minipage}{\linewidth}
  \centering
  \includegraphics[width=\linewidth]{mu-images/main-server-interface.png}
  \captionof{figure}{Interface de l'application lancée en tant que serveur}
\end{minipage}

1. Liste des listes de lectures disponibles
2. Liste des morceaux de musique présents dans la liste de lecture sélectionnée
3. Réglages
4. Détails du morceau venant de se terminer
5. Contrôles sur la musique et détails du morceau en écoute

### Réglages du côté serveur
Le panneau `Settings` vous offre la possibilité de choisir l'interface réseau à utiliser parmi les interfaces que votre ordinateur met à disposition. Une interface par défaut est sélectionnée, mais si les clients ne vous voient pas, il est peut-être nécessaire de changer l'interface réseau dans la liste déroulante.

Si tout marche, il n'est pas nécessaire de changer d'interface.

\begin{minipage}{\linewidth}
  \centering
  \includegraphics[scale=1]{mu-images/server-settings.png}
  \captionof{figure}{Paramètres du serveur}
\end{minipage}

1. Nom du serveur permettant de s'en souvenir après sa configuration
2. Choix de l'interface réseau

## Client
Les explications suivantes concernent le lancement de l'application en tant que client.

### Interface client
Quand vous choisissez de lancer l'application en tant que client, la fenêtre suivante est affichée à l'écran.

\begin{minipage}{\linewidth}
  \centering
  \includegraphics[width=\linewidth]{mu-images/main-client-interface.jpg}
  \captionof{figure}{Interface de l'application lancée en tant que client}
\end{minipage}

1. Liste des listes de lectures
2. Liste de lecture en cours de lecture
3. Réglages
4. Morceau précédent
5. Contrôles sur la musique et détails du morceau en écoute

### Réglages du côté client
Quand vous choisissez de lancer l'application en tant que client, vous avez le choix de vous connecter à l'un des serveurs. Vous pouvez choisir parmi une liste de serveurs disonibles.

Le panneau `Settings` vous offre la possibilité de choisir l'interface réseau à utiliser parmi les interfaces que votre ordinateur met à disposition. Une interface par défaut est sélectionnée, mais si vous ne voyez pas le serveur auquel vous souhaitez vous connecter, il est peut-être nécessaire de changer l'interface réseau dans la liste déroulante.

Si tout marche, il n'est pas nécessaire de changer d'interface.

Si vous êtes sur Windows et continuez de rencontrer des problèmes au niveau de l'affichage des serveurs, suivez les étapes ci-dessous :

- Ouvrir le `Panneau de configuration`
- Ouvrir `Gestionnaire de périphériques`
- Désactiver le périphérique réseau désiré
- Réactiver le périphérique réseau désiré
- Relancer **Commusica**

Le programme devrait maintenant voir les serveurs disponibles.

\begin{minipage}{\linewidth}
  \centering
  \includegraphics{mu-images/client-settings.png}
  \captionof{figure}{Réglages}
\end{minipage}

1. Choix du serveur
2. Choix de l'interface réseau

Une fois proprement connecté, la playlist en cours de lecture et toutes les informations liées aux morceaux de cette playlist sont visibles. Si cela n'est pas le cas, pensez à vérifier que l'interface réseau soit correcte.

## Interface commune
Les explications suivantes sont communes aux deux façons d'utiliser l'application, que ce soit en tant que serveur ou en tant que client.

### Choix des listes de lecture et favoris
Ce panneau vous permet de naviguer entre la liste de lecture actuelle, votre liste de morceaux favoris et les différentes listes de lecture qui ont été sauvegardées sur votre ordinateur lors d'utilisations précédentes de **Commusica**.

\begin{minipage}{\linewidth}
  \centering
  \includegraphics{mu-images/playlists.png}
  \captionof{figure}{Listes de lectures}
\end{minipage}

1. La liste de lecture en cours de lecture
2. La liste des favoris
3. Les listes de lecture des utilisations précédentes


### Ajouter de la musique à la liste de lecture en cours de lecture
Afin d'ajouter de la musique à la liste de lecture en cours, il suffit de glisser-déposer le(s) morceau(x) souhaitée(s) dans le centre de l'interface et celles-ci seront ajoutées au programme. Si votre morceau n'apparaît pas dans l'interface après trente secondes, il est nécessaire de reproduire l'action, le transfert ayant pris trop de temps la première fois.

### Liste de lecture en cours de lecture

Les morceaux lus sont grisés. Le dernier morceau grisé en partant du haut est celui en cours de lecture.

\begin{minipage}{\linewidth}
  \centering
  \includegraphics{mu-images/track-cells.png}
  \captionof{figure}{Liste de lecture en cours de lecture}
\end{minipage}

#### Gestion d'un morceau de musique

\begin{minipage}{\linewidth}
  \centering
  \includegraphics[scale=1]{mu-images/track-cell.png}
  \captionof{figure}{Gestion d'un morceau de musique}
\end{minipage}

1. Informations du morceau de musique
2. Ajouter/retirer le morceau des favoris
3. Voter pour le morceau
4. Voter contre le morceau
5. Total de votes pour le morceau

Votre vote aura pour conséquence d'augmenter ou diminuer le score du morceau d'au maximum un point. Le vote sur le morceau en cours de lecture n'aura pas d'effet.
Ceci a pour conséquence de réorganiser la liste, les morceaux les plus votés se trouvant en début de liste.

### Contrôles sur la musique

\begin{minipage}{\linewidth}
  \centering
  \includegraphics{mu-images/player}
  \captionof{figure}{Contrôles sur la musique et détails du morceau en écoute}
\end{minipage}

1. Présent uniquement pour l'esthétique, ce bouton ne fait rien
2. Arrêter/jouer la musique si la majorité des utilisateurs le demande
3. Passer au morceau suivant si la mojorité des utilisateurs le demande
4. Diminuer le volume si la majorité des utilisateurs le demande
5. Augmenter le volume si la majorité des utilisateurs le demande
6. Informations du morceau de musique
7. Temps écoulé pour le morceau
8. Ajouter/retirer le morceau des favoris
9. Voter pour le morceau
10. Voter contre le morceau
11. Total de votes pour le morceau

### Morceau précédent  
Grâce à cet encart, vous avez encore la possibilité d'ajouter à vos favoris le morceau qui vient de passer.

\begin{minipage}{\linewidth}
  \centering
  \includegraphics{mu-images/previous-track.png}
  \captionof{figure}{Morceau précédent}
\end{minipage}

1. Informations du morceau de musique
2. Ajouter/retirer le morceau des favoris

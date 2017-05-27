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

---
\makeatletter
\renewcommand{\@maketitle}{%
\newpage
\null
\vfil
\begingroup
\let\footnote\thanks
\begin{center}
{\LARGE\@title}\vskip1.5em
\includegraphics[width=10cm, height=10cm]{logo.png}\vskip1.5em
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
   Chef adjoint: Lucas Elisei\\
   Membres: David Truan, Denise Gemesio, Thibaut Togue, Yosra Harbaoui\\
   Responsable du cours: René Rentsch}

\date{HEIG-VD - Semestre d'été 2017}

\maketitle

\begin{tikzpicture}[remember picture,overlay]
   \node[anchor=north east,inner sep=0.25cm] at (current page.north east)
              {\includegraphics[scale=0.3]{heig-vd.png}};
\end{tikzpicture}

\newpage

\newpage

\tableofcontents

\listoffigures

\newpage

# Introduction
version de la doc / version du produit

Ce document est le manuel d'utilisation de l'application `Commusica` développée dans le cadre d'un projet de semestre de la section TIC de la HEIG-VD.

Commusica est une application de lecture en continu (streaming). Grâce à une communication client/serveur, elle permettera aux clients d'envoyer des fichiers musicaux au serveur pour que celui-ci les joue.

# Prérequis
Quels OS utilisés ? quels progs ?
Commusica peut être utilisée sur:
- Windows 10
- MAC OS 10.11.6


Pour pouvoir lancer l'application, Le `Java Development Kit (JDK)` doit être installé. Il est téléchargeable gratuitement [ici](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) en choisissant la version qui correspond au système d'exploitation utilisé comme indiqué ci-dessous.
![alt text]()

L'application est sur le CD du projet ou téléchargeable [ici](https://github.com/heig-vd-pro2017/projet).

# Lancement

- Ouvrez PowerShell (si vous utiliser Windows) ou Terminal (si vous utilisez Mac OS).
- tapez : `java -jar commusica-1.0-SNAPSHOT-capsule.jar` puis sur `Entrer`

l'application sera lancée directement après avoir taper la ligne ci-dessus.

# Utilisation
## Première fenêtre
Une fois l'application est lancée, la fenêtre ci-dessous apparaît.
![alt text]()

Vous avez deux choix :
- être Serveur
- être Client

1. Serveur:
- Si vous choisissez d'être un serveur, vous cliquez sur `Yes`. 
- Vous pouvez ensuite donner un nom à votre serveur puis cliquer sur `OK`.

2. Client:
- Si vous choisissez d'être un client, vous cliquez sur `No`. 

## Serveur
![alt text]()
(1) Settings : Vous pouvez effectuer les différents paramétrages ici.  
(1.1) Network interface: Vous pouvez choisir l'interface de communication parmi les interfaces existantes. Une interface par défaut           est mise à disposition.
    
(2) Playlists : contient la playlist en cours de lecture `PLAYING`(1.1), la playlist des chansons préférées `FAVORITES` (1.2) et la liste des palylists sauvegardées des utilisations précédentes (1.3).

(3) Les chansons en cours de lecture.  
(3.1) Le nombre total des votes pour chaque chanson
  
(4) La chanson en cours de lecture.

(5) Les informations concernant la chanson précédent la chanson en cours de lecture.

## Client
![alt text]()
(1) Settings : Vous pouvez effectuer les différents paramétrages ici.
    (1.1) Servers list: Vous pouvez choisir la connexion à un server parmi les serveurs existants.
    (1.2) Network interface: Vous pouvez choisir l'interface de communication parmi les interfaces existantes. Une interface par défaut           est mise à disposition.  

(2) Playlists : contient la playlist en cours de lecture `PLAYING`(1.1), la playlist des chansons préférées `FAVORITES` (1.2) et la liste des palylists sauvegardées des utilisations précédentes (1.3).

(3) Les chansons en cours de lecture.  
(3.1) Le nombre total des votes pour chaque chanson, vous pouvez voter pour ou contre une chanson. Plus le nombre total de votes est           grand, plus la chanson est aura plus de chance d'être joué après la chanson en cours de lecture.  
(3.2) Vous pouvez favoriser une chanson en cliquant sur cette étoile.
  
(4) La chanson en cours de lecture.  
(4.1) Si vous voulez passer à la chanson suivante, vous pouvez cliquer sur ce bouton. La chanson suivante sera jouée si la mojorité           des présents le demande.  
(4.2) Si vous voulez augmenter ou diminuer le volu,e, vous pouvez cliquer sur ce bouton. Le volume sera modifié si la mojorité                 des présents le demande.

(5) Les informations concernant la chanson précédent la chanson en cours de lecture.

# Références/plus d'infos/ aide (points de contact)

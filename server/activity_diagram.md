## Serveur
- Utilisation d'id pour sauvegarder les sessions
- Classe Session et SessionManager.
- SessionManager gère la suppression/ajout de session.
    - Gère la suppression de session obsolètes (pas encore bien implémenté dans NetworkManager...)
- Une session a:
    - Un `id` unique qui lui permet de s'identifié à chaque connexion et qui lui est
    attribué dès que la Session est créée.
    - Un `Timestamp` qui permettra de vérifier si elle est obsolète (possible de
         définir le temps d'obsolésence).

![Diagramme d'activité du serveur provisoire](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/heig-vd-pro2017/projet/master/server/activity_diagram.plantuml)

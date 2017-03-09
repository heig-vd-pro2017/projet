# Documentation
- La notation Doxygen ([doxygen.org](doxygen.org)) est utilisée pour documenter le code
- La documentation des méthodes et du code est en anglais
- Une entête de fichier est utilisée pour chaque fichier
- La tabulation est de quatre espaces

# Variables et méthodes
- Utilisation de la notation camel case pour les variables et méthodes
- Les variables de types statiques sont en majuscules
- Ne pas utiliser de caractères spéciaux

# Les instructions de branchement
Les instructions de branchement sont écrites de la façon suivante, le `_` illustrant un espace:

```
if_(condition1_&&_condition2)_{
    ...
}_else_{
    ...
}
```

```
while_(condition1_&&_condition2)_{
    ...
}
```

```
switch_(condition1_&&_condition2)_{

    case_1:
        ...
        break;
    case_2:
        ...
        break;
    default:
        ...
        break;
}
```

```
for_(int_i_=_0;_i_<_10;_++i)_{
    ...
}
```

# Classes
On essaie d'avoir la structure suivante:

```
class MaClasse {
    attributs
        - static
        - public
        - protected
        - private
    méthodes
        - static
        - public
        - protected
        - private
}
```

# Exemple complet
```
/**
 * @author Auteur 1
 * @author Auteur 2
 *
 * @class MaClasse
 *
 * @brief This class is an example.
 *
 * Its description continues on multiple lines without any problem. However, if a
 * comment seems too long, feel free to break the comment in two lines.
 */
 class MaClasse {

    /**
     * I_AM_A_STATIC_VARIABLE description.
     */
    private final static int I_AM_A_STATIC_VARIABLE = 10;

    /**
     * iAmAString description.
     */
    public String iAmAString;

    /**
     * iAmACharacter description.
     */
    protected char iAmACharacter;

    /**
     * amIABoolean description.
     */
    private boolean amIABoolean;

    /**
     * @brief Description about the method
     *
     * @param x Short description
     * @param y Short description
     *
     * @return int The multiplication of x and y
     */
    public int myMethod(int x, int y) {
        return x * y;
    }

    /**
     * @brief Description about the method
     *
     * @return void
     */
    public void myMethod2() {
        // In-method comment.
        System.out.println("Hi");
    }

 }
```

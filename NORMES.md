# Documentation
- La notation deoxygen ([doxygen.org](doxygen.org)) sera utilisée pour documenter le code
- La documentation des méthodes et du code sera en anglais
- Une entête de fichier sera utilisée pour chaque fichier
- La tabulation est quatre espaces

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
 * \author Auteur 1
 * \author Auteur 2
 * \class MaClasse.java
 * \brief This class is an example.
 *
 * Its description continues on multiple lines without any problem. However, if a
 * comment seems too long, feel free to break the comment in two lines.
 */

 class MaClasse {

    private final static int I_AM_A_STATIC_VARIABLE = 10; /**< Comment after the member */
    public String iAmAString; /**< Comment after the member */
    protected char iAmACharacter; /**< Comment after the member */
    private booleand amIABoolean; /**< Comment after the member */

    /** @brief Description about the method
     *  @param x Short description
     *  @param y Short description
     *  @return int The multiplication of x and y
     */
    public int myMethod(int x, int y) {
        return x * y;
    }

    /** @brief Description about the method
     *  @return void
     */
    public void myMethod2() {
        System.out.println("Hi");
    }

 }
```

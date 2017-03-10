# Documentation
- La documentation des méthodes et du code sera en anglais
- La tabulation est quatre espaces
- La longeur maximale les lignes de code est sur 120 caractères
- La notation doxygen ([doxygen.org](doxygen.org)) sera utilisée pour documenter le code, avec le caractère `@` et non `\`
- Une entête de fichier (doxygen) sera utilisée pour chaque fichier

# Entêtes
Les entêtes de fonctions et fichiers devront respecter la forme suivante:

```
/**
 * This is a correct comment
 */
```

```
/**
 * This is also
 * a correct comment
 */
```

```
/**
 * As well as this one in case that the documentation is very long and have "multiple parts".
 *
 * So we leave a bit of space for a better reading experience.
 */
```

Ne pas écrire d'entêtes commme ceux-ci:
```
/** This comment starts at the very top...
 * ...but should has started here.
 */
```

```
/**
 *
 * Why do we leave empty spaces before and after ? Isn't it useless ?
 *
 */
```

# Fonctions
L'entête des fonctions devra respecter la forme suivante:
```
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
```

Au sein de fonction, ne pas utiliser la forme de commentaires multi-lines:
```
/**
 * This is a comment
 * inside a function
 */
```
car si l'on souhaite commenter une partie de la fonction, les caractères `*/` couperont la commentation de la fonction.

Utiliser la forme suivante:
```
//! This is a multi-line comment
//! inside a function
```

Les parenthèses aux fonctions restent collées au nom de la fonction, comme en mathématiques:
```
public int iAmACoolFunctionWithoutAnySpaces(int iHateSpaces) {
    ...
}
```

```
...

int a = iAmACoolFunctionWithoutAnySpaces(4);

...
```

# Variables
Pour commenter des variables, on utilise la forme:
```
//! The current volume of the application
private unsigned int volume;

//! The playlist containing all the musics
private ArrayList<Music> playlist;
```

# Entête de fichier
L'entête des fichiers devra respecter la forme suivante:
```
/**
 * @brief This class is an example.
 *
 * Its description continues on multiple lines without any problem. However, if a comment is too long (max. 120
 * characters), feel free to break the comment in two lines.
 */
```

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
On essaie d'utiliser la forme suivante:

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
 * @brief This class is an example.
 *
 * Its description continues on multiple lines without any problem. However, if a comment seems too long (max. 120
 * characters), feel free to break the comment in two lines.
 */
 class MaClasse {

    //! Comment before the member
    private final static int I_AM_A_STATIC_VARIABLE = 10;

    //! Comment before the member
    public String iAmAString;

    //! Comment before the member
    protected char iAmACharacter;

    //! Comment before the member
    private booleand amIABoolean;

    /**
     * @brief Description about the method
     *
     * @param x Short description
     * @param y Short description
     *
     * @return int The multiplication of x and y
     */
    public int myMethod(int x, int y) {

        //! This part is so important that it needs
        //! multiple lines to describe it
        return x * y;
    }

    /**
     * @brief Description about the method
     *
     * @param c Change the message
     *
     * @return void
     */
    public void myMethod2(boolean c) {

        //! Change the message
        if (c) {
            System.out.println("Hi");
        } else {
            System.out.println("Good bye");
        }
    }
 }
```

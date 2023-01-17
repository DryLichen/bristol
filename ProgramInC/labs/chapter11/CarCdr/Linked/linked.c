#include "../lisp.h"
#include "specific.h"

// Returns element 'a' - this is not a list, and
// by itelf would be printed as e.g. "3", and not "(3)"
lisp* lisp_atom(const atomtype a) {
    lisp* leaf = NULL;
    leaf = (lisp*)ncalloc(1, sizeof(lisp));

    leaf->car = NULL;
    leaf->cdr = NULL;
    leaf->atom = a;

    return leaf;
}

// Returns a list containing the car as 'l1'
// and the cdr as 'l2'- data in 'l1' and 'l2' are reused,
// and not copied. Either 'l1' and/or 'l2' can be NULL
lisp* lisp_cons(const lisp* l1,  const lisp* l2) {
    lisp* newHead = lisp_atom(DEFAULT);

    newHead->car = (lisp*)l1;
    newHead->cdr = (lisp*)l2;

    return newHead;
}

// Returns the car (1st) component of the list 'l'.
// Does not copy any data.
lisp* lisp_car(const lisp* l) {
    // null pointer
    if (l == NULL) {
        return NULL;
    }

    return l->car;
}

// Returns the cdr (all but the 1st) component of the list 'l'.
// Does not copy any data.
lisp* lisp_cdr(const lisp* l) {
    // null pointer
    if (l == NULL) {
        return NULL;
    }

    return l->cdr;
}

// Returns the data/value stored in the cons 'l'
atomtype lisp_getval(const lisp* l) {
    if (l == NULL) {
        exit(EXIT_FAILURE);
    }
    return l->atom;
}

// Returns a deep copy of the list 'l'
lisp* lisp_copy(const lisp* l) {
    // null case
    if (l == NULL) {
        return NULL;
    }

    // leaf case
    if (lisp_isatomic(l)) {
        return lisp_atom(lisp_getval(l)); 
    }

    // general case
    lisp* copyLisp = lisp_atom(DEFAULT);
    copyHelper(copyLisp, l);

    return copyLisp;
}

void copyHelper(lisp* copyLisp, const lisp* l) {
    if (l == NULL) {
        return;
    }

    if (lisp_car(l) == NULL) {
        return;
    }
    copyLisp->car = lisp_atom((lisp_getval(lisp_car(l))));

    if (lisp_cdr(l) == NULL) {
        return;
    }
    copyLisp->cdr = lisp_atom(DEFAULT);

    copyHelper(lisp_car(copyLisp), lisp_car(l));
    copyHelper(lisp_cdr(copyLisp), lisp_cdr(l));
}

// Returns number of components in the list.
int lisp_length(const lisp* l) {
    int count = 0;
    lisp* ptr = (lisp*)l;

    // leaf case
    if (lisp_isatomic(l)) {
        return 0;
    }

    while (ptr != NULL) {
        ptr = lisp_cdr(ptr);
        count++;
    }

    return count;
}

// Returns stringified version of list
void lisp_tostring(const lisp* l, char* str) {
    // leaf case
    if (lisp_isatomic(l)) {
        char tmp[STRLEN];
        assert(sprintf(tmp, "%i", lisp_getval(l)) != -1);
        strcpy(str, tmp);
        return;
    }

    // general case
    *str = '(';
    int ptr = 0;
    toStringHelper(l, str, &ptr);
    writeChar(str, &ptr, '\0');
}

void toStringHelper(const lisp* l, char* str, int* ptr) {
    // write a ) when the end of a lisp is detected
    if (l == NULL) {
        writeChar(str, ptr, ')');
        return;
    }

    // write atoms
    if (lisp_isatomic(l)) {
        // insert a space if the last char is a digit or a right bracket
        if (insertSpace(str, ptr)) {
            writeChar(str, ptr, ' ');
        }
        char tmp[STRLEN];
        assert(sprintf(tmp, "%i", lisp_getval(l)) != -1);
        strcpy(str + *ptr + 1, tmp);
        (*ptr) += strlen(tmp);
        return;
    }

    // write left brackets 
    if (insertBracket(l)) {
        // insert a space when the last char is a digit or a right bracket
        if (insertSpace(str, ptr)) {
            writeChar(str, ptr, ' ');
        }
        writeChar(str, ptr, '(');
    }
    
    toStringHelper(lisp_car(l), str, ptr);
    toStringHelper(lisp_cdr(l), str, ptr);
}

// write char c into the next position of str
void writeChar(char* str, int* ptr, char c) {
    (*ptr)++;
    *(str + *ptr) = c;
}

// Returns a boolean depending up whether l points to an atom (not a list)
bool lisp_isatomic(const lisp* l) {
    if (l == NULL) {
        return false;
    }

    if (lisp_car(l) == NULL && lisp_cdr(l) == NULL) {
        return true;
    }

    return false;
}

// return true when the next char should be a left bracket
// a left bracket is needed when a child node is detected
bool insertBracket(const lisp* l) {
    if (l == NULL) {
        return false;
    }
    // return false when detect a mother node
    if (lisp_isatomic(lisp_car(l))) {
        return false; 
    }

    return true;
}

// return true when the next char should be space
// a space should be inserted between two digit or a right bracket and a digit 
bool insertSpace(char* str, int *ptr) {
    char c = *(str + *ptr);
    if (isdigit(c) || c == ')') {
        return true;
    }

    return false;
}

// Clears up all space used
// Double pointer allows function to set 'l' to NULL on success
void lisp_free(lisp** l) {
    if (*l == NULL) {
        return;
    }

    lisp_free(&((*l)->car));
    lisp_free(&((*l)->cdr));
    free((*l));
    *l = NULL;
}

























/* ------------- Tougher Ones : Extensions ---------------*/

// Builds a new list based on the string 'str'
lisp* lisp_fromstring(const char* str) {
    // null case
    if (strcmp(str, "()") == 0) {
        return NULL;
    }
    
    // process string
    invalidChar(str);

    char spaceBuff[STRLEN];
    memset(spaceBuff, '\0', STRLEN);
    noRowSpace(str, spaceBuff);

    char buff[STRLEN];
    processStr(spaceBuff, buff);

    // check if the literal lisp is valid
    if (!isValid(buff)) {
        printf("Invalid");
        exit(EXIT_FAILURE);
    }

    // leaf case
    if (isdigit(*buff) || *buff == '-') {
        int tmp;
        assert(sscanf(buff, "%d", &tmp) == 1);
        return lisp_atom(tmp);
    }

    // general case
    lisp* l = lisp_atom(DEFAULT);
    int ptr = 1;
    fromStringHelper(l, buff, &ptr);

    return l;
}

void fromStringHelper(lisp* l, char* str, int* ptr) {
    if (*ptr == (int)strlen(str)) {
        return;
    }

    if (isdigit(str[*ptr]) || str[*ptr] == '-') {
        int tmp = 0;
        assert(sscanf(str + *ptr, "%d", &tmp) == 1);
        l->car = lisp_atom(tmp);
        (*ptr) += getNumLen(tmp);
    }

    if (str[*ptr] == ' ') {
        l->cdr = lisp_atom(DEFAULT);
        (*ptr)++;
        fromStringHelper(l->cdr, str, ptr);
    }

    if (str[*ptr] == '(') {
        l->car = lisp_atom(DEFAULT);
        (*ptr)++;
        fromStringHelper(l->car, str, ptr);
        // after the traversal of children, if a space detected, traverse parents horizontally
        if (str[*ptr] == ' ') {
            l->cdr = lisp_atom(DEFAULT);
            (*ptr)++;
            fromStringHelper(l->cdr, str, ptr);
        }
    }

    if (str[*ptr] == ')') {
        (*ptr)++;
        return;
    }
}

// check if there are invalid characters in string
void invalidChar(const char* str) {
    for (int i = 0; i < (int)strlen(str); i++) {
        bool flag = false;
        char c = str[i];
        
        if (isdigit(c)) {
            flag = true;
        }
        if (c == '-') {
            flag = true;
        }
        if (c == ' ') {
            flag = true;
        }
        if (c == '(' || c == ')') {
            flag = true;
        }

        if (!flag) {
            printf("Illegal character in literal lisp?");
            exit(EXIT_FAILURE);
        }
    }
}

// delete redundant row spaces and just keep one space
void noRowSpace(const char* originStr, char* buff) {
    char str[STRLEN];
    strcpy(str, originStr);
    char tmp[STRLEN][STRLEN];
    int count = 0;

    char *p = strtok(str, " ");
    while (p != NULL) {
        strcpy(tmp[count], p);
        tmp[count][strlen(tmp[count]) + 1] = '\0';
        tmp[count][strlen(tmp[count])] = ' ';
        count++;
        p = strtok(NULL, " ");
    }

    // concatenate characters
    for (int i = 0; i < count; i++) {
        strcat(buff, tmp[i]);
    }
    
    // delete the final space
    buff[strlen(buff) - 1] = '\0';
    if (buff[strlen(buff) - 1] == ' ') {
        buff[strlen(buff) - 1] = '\0';
    }
}

// process string to get a standard string
void processStr(char* str, char* buff) {
    int buffPtr = 0;
    int length = strlen(str);
    for (int i = 0; i < length; i++) {
        // 1. space rules after digits
        if (isdigit(str[i])) {
            buff[buffPtr++] = str[i];
            // space between a digit and a left bracket
            if (i + 1 < length && str[i + 1] == '(') {
                buff[buffPtr++] = ' ';
            } else if (i + 1 < length && str[i + 1] == '-') {
                printf("Negative sign should not be after a digit?");
                exit(EXIT_FAILURE);
            }
        }

        // 2. space rules after left brackets
        if (str[i] == '(') {
            if (i + 1 < length && str[i + 1] == ' ') {
                buff[buffPtr++] = str[i++];
            } else {
                buff[buffPtr++] = str[i];
            }
        }

        // 3. space rules after right brackets
        if (str[i] == ')') {
            buff[buffPtr++] = str[i];
            if (i + 1 < length && str[i + 1] == '(') {
                buff[buffPtr++] = ' ';
            } else if (i + 1 < length && isdigit(str[i + 1])) {
                buff[buffPtr++] = ' ';
            } else if (i + 1 < length && str[i + 1] == '-') {
                buff[buffPtr++] = ' ';
            }
        }

        // 4. space rules after negative sign
        if (str[i] == '-') {
            if (i + 1 < length && !isdigit(str[i + 1])) {
                printf("Expect digits after negative sign?");
                exit(EXIT_FAILURE);
            }
            buff[buffPtr++] = str[i];
        }

        // 5. space rules after space
        if (str[i] == ' ') {
            if (i + 1 < length && str[i + 1] != ')') {
                buff[buffPtr++] = str[i];
            }
        }
    }
    buff[buffPtr] = '\0';
}

// check if a processed literal lisp is valid
bool isValid(char* str) {
    // the number of ( and ) should be equal
    int countL = 0, countR = 0;
    for (int i = 0; i < (int)strlen(str); i++) {
        if (str[i] == '(') {
            countL++;
        }
        if (str[i] == ')') {
            countR++;
        }
    }
    if (countL != countR) {
        return false;
    }

    // not leaf case
    if (countL != 0) {
        if (str[0] != '(' || str[strlen(str) - 1] != ')') {
            return false;
        }
    }

    return true;
}

// return the number of digits, including the negative sign
int getNumLen(int num) {
    int count = (num <= 0) ? 1 : 0;

    while (num != 0) {
        num /= 10;
        count++;
    }
    
    return count;
}

// Returns a new list from a set of existing lists.
// A variable number 'n' lists are used.
// Data in existing lists are reused, and not copied.
// You need to understand 'varargs' for this.
lisp* lisp_list(const int n, ...) {
    va_list valist;
    va_start(valist, n);

    // initiate the first node and the pointer pointing to the last item
    lisp* newLisp = lisp_atom(DEFAULT);
    lisp* ptr = newLisp;
    lisp* l = NULL;

    for (int i = 0; i < n; i++) {
        l = va_arg(valist, lisp*);
        if (l != NULL) {
            ptr->car = l;
            if (i != n - 1) {
                ptr->cdr = lisp_atom(DEFAULT);
                ptr = lisp_cdr(ptr);
            }
        }
    }

    va_end(valist);
    return newLisp;
}

// Allow a user defined function 'func' to be applied to
// each atom in the list l.
// The user-defined 'func' is passed a pointer to a cons,
// and will maintain an accumulator of the result.
void lisp_reduce(void (*func)(lisp* l, atomtype* n), lisp* l, atomtype* acc) {
    if (l == NULL) {
        return;
    }
    
    if (lisp_isatomic(l)) {
        func(l, acc);
        return;
    }

    lisp_reduce(func, lisp_car(l), acc);
    lisp_reduce(func, lisp_cdr(l), acc);
}

void test() {
    char str[STRLEN];

    // tests for initiation methods
    lisp* l1 = lisp_cons(lisp_atom(0), lisp_cons(lisp_atom(2), NULL));
    lisp* l2 = lisp_cons(lisp_atom(3), lisp_cons(l1, NULL));
    lisp* l3 = lisp_cons(lisp_atom(-1), lisp_cons(l2, lisp_cons(lisp_atom(405), NULL)));

    // tests for lisp_cdr, car and getval
    assert(lisp_getval(lisp_car(l1)) == 0);
    assert(lisp_getval(lisp_car(lisp_car(lisp_cdr(l2)))) == 0);

    // tests for deep copy
    lisp* l4 = NULL;
    lisp* l5 = NULL;
    assert(lisp_copy(l4) == NULL);
    l4 = lisp_atom(-3);
    l5 = lisp_copy(l4);
    assert(lisp_car(l5) == NULL);
    assert(lisp_getval(l5) == -3);
    assert(lisp_cdr(l5) == NULL);
    assert(&l4 != &l5);
    lisp_free(&l4);
    lisp_free(&l5);
    l4 = lisp_copy(l3);
    assert(lisp_getval(lisp_car(lisp_cdr(lisp_cdr(l4)))) == 405);
    lisp_free(&l4);

    // tests for lisp_length
    assert(lisp_length(l1) == 2);
    assert(lisp_length(l2) == 2);
    assert(lisp_length(l3) == 3);

    // tests for isatomic
    l4 = lisp_atom(0);
    assert(lisp_isatomic(l4));
    lisp_free(&l4);

    // tests for writeChar
    *str = 'a';
    *(str + 1) = 'b';
    *(str + 2) = '7';
    int ptr = 0;
    writeChar(str, &ptr, 'c');
    assert(*str == 'a' && str[ptr] == 'c');
    // tests for insertBracket and insertSpace
    assert(lisp_cdr(l2));
    ptr = 2;
    assert(insertSpace(str, &ptr));

    // tests for toString
    lisp_tostring(l1, str);
    assert(strcmp(str, "(0 2)") == 0);
    lisp_tostring(l2, str);
    assert(strcmp(str, "(3 (0 2))")  == 0);
    lisp_tostring(l3, str);
    assert(strcmp(str, "(-1 (3 (0 2)) 405)") == 0);

    // tests for lisp_free
    lisp_tostring(l3, str);
    lisp_free(&l3);
    assert(l3 == NULL);

    // tests for fromString
    char inp[5][STRLEN] = {"()", "(1)", "-3", "(0 (1 -2) 3 4 50)", "((-1 2) (3 (0) 4) (5 (6 7)))"};
    for(int i=0; i<5; i++){
      lisp* f1 = lisp_fromstring(inp[i]);
      lisp_tostring(f1, str);
      assert(strcmp(str, inp[i])==0);
      lisp_free(&f1);
      assert(!f1);
    }    

    // tests for getNumLen
    assert(getNumLen(445) == 3);
    assert(getNumLen(0) == 1);
    assert(getNumLen(-445) == 4);

    // tests for lisp_list
    l1 = lisp_cons(lisp_atom(0), lisp_cons(lisp_atom(2), NULL));
    l2 = lisp_list(2, lisp_atom(3), l1);
    l3 = lisp_list(3, l2, lisp_copy(l1), lisp_atom(-100));
    lisp_tostring(l3, str);
    assert(strcmp("((3 (0 2)) (0 2) -100)", str) == 0);
    lisp_free(&l3);
    assert(!l3);
}

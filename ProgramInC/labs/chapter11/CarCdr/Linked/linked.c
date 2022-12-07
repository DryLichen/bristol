#include "../lisp.h"
#include "../../../../../ADTs/General/general.h"
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
    if (l == NULL) {
        return NULL;
    }

    return l->car;
}

// Returns the cdr (all but the 1st) component of the list 'l'.
// Does not copy any data.
lisp* lisp_cdr(const lisp* l) {
    if (l == NULL) {
        return NULL;
    }

    return l->cdr;
}

// Returns the data/value stored in the cons 'l'
atomtype lisp_getval(const lisp* l) {
    if (l == NULL) {
        fprintf(stderr, "Wrong parameter");
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

    // leaf casee
    if (lisp_isatomic(l)) {
        return lisp_atom(lisp_getval(l)); 
    }

    // general cases
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
    // only a leaf
    if (lisp_isatomic(l)) {
        char tmp[STRLEN];
        sprintf(tmp, "%i", lisp_getval(l));
        strcpy(str, tmp);
        return;
    }

    // general cases
    *str = '(';
    int ptr = 0;
    toStringHelper(l, str, &ptr);
    writeChar(str, &ptr, '\0');
}

void toStringHelper(const lisp* l, char* str, int* ptr) {
    if (l == NULL) {
        writeChar(str, ptr, ')');
        return;
    }

    if (lisp_isatomic(l)) {
        if (isSpace(str, ptr)) {
            writeChar(str, ptr, ' ');
        }
        char tmp[STRLEN];
        sprintf(tmp, "%i", lisp_getval(l));
        strcpy(str + *ptr + 1, tmp);
        (*ptr) += strlen(tmp);
        return;
    }

    if (isBracket(l)) {
        if (isSpace(str, ptr)) {
            writeChar(str, ptr, ' ');
        }
        writeChar(str, ptr, '(');
    }
    
    toStringHelper(lisp_car(l), str, ptr);
    toStringHelper(lisp_cdr(l), str, ptr);
}

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

bool isBracket(const lisp* l) {
    if (l == NULL) {
        return false;
    }

    if (lisp_isatomic(lisp_car(l))) {
        return false; 
    }

    return true;
}

bool isSpace(char* str, int *ptr) {
    char c = *(str + *ptr);
    if ((c <= '9' && c >= '0') || c == ')') {
        return true;
    }

    return false;
}

// Clears up all space used
// Double pointer allows function to set 'l' to NULL on success
void lisp_free(lisp** l) {
    freeHelper(*l);
    *l = NULL;
}

void freeHelper(lisp* l) {
    if (l == NULL) {
        return;
    }

    freeHelper(l->car);
    freeHelper(l->cdr);
    free(l);
}


/* ------------- Tougher Ones : Extensions ---------------*/

// Builds a new list based on the string 'str'
lisp* lisp_fromstring(const char* str) {
    // null case
    if (strcmp(str, "()") == 0) {
        return NULL;
    }

    // leaf case
    if (isdigit(*str)) {
        if (strlen(str) == 1) {
            return lisp_atom(*str);
        }
        
        fprintf(stderr, "Wrong format of lisp string!");
        exit(EXIT_FAILURE);
    }

    // general cases
    lisp* l = lisp_atom(DEFAULT);
    int ptr = 1;

    fromStringHelper(l, str, &ptr);

    return l;
}

void fromStringHelper(lisp* l, const char* str, int* ptr) {
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
        return;
    }

    if (str[*ptr] == '(') {
        l->car = lisp_atom(DEFAULT);
        (*ptr)++;
        fromStringHelper(l->car, str, ptr);
        if (str[*ptr] == ' ') {
            l->cdr = lisp_atom(DEFAULT);
            (*ptr)++;
            fromStringHelper(l->cdr, str, ptr);
        }
        return;
    }

    if (str[*ptr] == ')') {
        (*ptr)++;
        return;
    }
}

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
    lisp* newLisp = getListNode(va_arg(valist, lisp*));
    lisp* ptr = getLast(newLisp);
    lisp* l = NULL;

    for (int i = 1; i < n; i++) {
        l = va_arg(valist, lisp*);
        ptr->cdr = getListNode(l);
        ptr = getLast(ptr);
    }

    va_end(valist);
    return newLisp;
}

// return a new lisp if source node is a leaf, otherwise return source node
lisp* getListNode(lisp* src) {
    // leaf case
    if (lisp_isatomic(src)) {
        lisp* dest = lisp_atom(DEFAULT);
        dest->car = src;
        return dest;
    }

    // lisp case
    return src;
}

// get the last node of a lisp
lisp* getLast(lisp* l) {
    while (lisp_cdr(l) != NULL) {
        l = lisp_cdr(l);
    }

    return l;
}

// Allow a user defined function 'func' to be applied to
// each atom in the list l.
// The user-defined 'func' is passed a pointer to a cons,
// and will maintain an accumulator of the result.
// void lisp_reduce(void (*func)(lisp* l, atomtype* n), lisp* l, atomtype* acc);

// void reduceHelper(atomtype(*func)(lisp* l), lisp* l) {

// }

void test() {
    char str[STRLEN];
    lisp* l1 = lisp_cons(lisp_atom(-1), lisp_cons(lisp_atom(0), lisp_cons(lisp_atom(1), NULL)));

    lisp_tostring(l1, str);
    printf("%s\n", str);
    lisp_free(&l1);
}

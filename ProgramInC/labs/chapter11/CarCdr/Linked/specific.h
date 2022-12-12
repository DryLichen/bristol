#include <ctype.h>

#define LISPIMPL "Linked"
#define STRLEN 1000
#define DEFAULT 0

struct lisp {
    lisp* car;
    lisp* cdr;
    atomtype atom;
};

void copyHelper(lisp* copyLisp, const lisp* l);
void toStringHelper(const lisp* l, char* str, int* ptr);
bool isBracket(const lisp* l);
bool isSpace(char* str, int *ptr);
void writeChar(char* str, int* ptr, char c);
void fromStringHelper(lisp* l, const char* str, int* ptr);
int getNumLen(int num);
void test();

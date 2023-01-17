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
bool insertBracket(const lisp* l);
bool insertSpace(char* str, int *ptr);
void writeChar(char* str, int* ptr, char c);
void fromStringHelper(lisp* l, char* str, int* ptr);
void invalidChar(const char* str);
void noRowSpace(const char* originStr, char* buff);
void processStr(char* str, char* buff);
bool isValid(char* str);
int getNumLen(int num);
void test();

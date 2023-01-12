#include "lisp.h"
#include "specific.h"

#define MAXNUMTOKENS 1000
#define MAXTOKENSIZE 100
#define BUFLEN 256
#define VARNUM 26
#define VARLEN 1
#define DEFADDR -1

typedef struct program {
    char wds[MAXNUMTOKENS][MAXTOKENSIZE];
    int ptr;
} Program;

void prog(Program* p);
void instrcts(Program* p);
void instrct(Program* p);
bool isList(char* str);
void list(Program* p);
void getList(Program* p, int addr);
bool isIO(char* str);
void io(Program* p);
int var(Program* p);
void literal(Program* p);
bool strSame(char* str1, char* str2);
void error(char* str);
void test();

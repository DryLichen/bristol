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

Program* getTokens(FILE* fp);
void prog(Program* p);
void instrcts(Program* p);
void instrct(Program* p);
bool isRetFun(char* str);
bool isListFun(char* str);
bool isIntFun(char* str);
bool isBoolFun(char* str);
void retFun(Program* p);
void listFun(Program* p);
void intFun(Program* p);
void boolFun(Program* p);
void getList(Program* p);
bool isIOFun(char* str);
void ioFun(Program* p);
void setFun(Program* p);
void printFun(Program* p);
bool isIfFun(char* str);
void ifFun(Program* p);
bool isLoopFun(char* str);
void loopFun(Program* p);
void var(Program* p);
void string(Program* p);
void literal(Program* p);
bool strSame(char* str1, char* str2);
void error(char* str);
void test();

#include "lisp.h"
#include "specific.h"

#define MAXNUMTOKENS 1000
#define MAXTOKENSIZE 100
#define BUFLEN 256
// 26 alpha + 174 temporary variable + 100 child variable + 100 mother variable
#define VARNUM 400
#define TEMPSTART 26
#define CHILDSTART 200
#define MOTHSTART 300
#define VARLEN 1
#define NONE -1
#define DEFADDR -2
#define TRUE -3
#define FALSE -4

typedef struct program {
    char wds[MAXNUMTOKENS][MAXTOKENSIZE];
    int ptr;
} Program;

void initLisps();
Program* getTokens(FILE* fp);
void prog(Program* p);
void instrcts(Program* p);
void instrct(Program* p);
bool isRetFun(char* str);
bool isListFun(char* str);
bool isIntFun(char* str);
bool isBoolFun(char* str);
bool isLisp(int addr);
bool isAtom(int addr);
int idleTemp();
int idleChild();
int idleMoth();
bool isIOFun(char* str);
void ioFun(Program* p);
void setFun(Program* p);
void printFun(Program* p);
bool isBool(int addr);
bool isIfFun(char* str);
void ifFun(Program* p);
bool isLoopFun(char* str);
void loopFun(Program* p);
void string(Program* p);
bool strSame(char* str1, char* str2);
void error(char* str);
void freeSpace();
void testNuclei();

#ifdef INTERP
    int retFun(Program* p);
    int listFun(Program* p);
    int intFun(Program* p);
    int boolFun(Program* p);
    int getList(Program* p);
    int var(Program* p);
    int literal(Program* p);
#else
    void retFun(Program* p);
    void listFun(Program* p);
    void intFun(Program* p);
    void boolFun(Program* p);
    void getList(Program* p);
    void var(Program* p);
    void literal(Program* p);
#endif

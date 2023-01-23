#include "lisp.h"
#include "specific.h"

#define MAXNUMTOKENS 1000
#define MAXTOKENSIZE 100
#define BUFLEN 256
// 26 alphabet + 174 temporary
#define VARNUM 200
#define TEMPSTART 26
#define VARLEN 1
#define NONE -1
#define DEFADDR -2
#define TRUE -3
#define FALSE -4
#define TESTTOKENS 17

// #define INTERP

typedef struct program {
    char wds[MAXNUMTOKENS][MAXTOKENSIZE];
    int ptr;
} Program;

void initLisps();

Program* getTokens(FILE* fp);
void clearEnd(char* buffer, int* len);
void processWords(Program*p, char* buffer, int* i, int* len, int* count, int* wordPtr);
void processLit(Program*p, char* buffer, int* i, int* len, int* count, int* wordPtr);
void processOther(Program*p, char* buffer, int* i, int* len, int* count, int* wordPtr);

void prog(Program* p);
void instrcts(Program* p);
void instrct(Program* p);

bool isRetFun(char* str);
bool isListFun(char* str);
bool isIntFun(char* str);
bool isBoolFun(char* str);

bool isIOFun(char* str);
void ioFun(Program* p);
void setFun(Program* p);
void printFun(Program* p);

bool isIfFun(char* str);
void ifFun(Program* p);

bool isLoopFun(char* str);
void loopFun(Program* p);

void string(Program* p);
bool strSame(char* str1, char* str2);
void checkLBrace(Program* p);
void checkRBrace(Program* p);
void error(char* str);

void testToken();
void testBasic();

#ifdef INTERP
    int retFun(Program* p);
    int listFun(Program* p);
    void listHelper(int addr);
    int intFun(Program* p);
    void intHelper(int addr);
    int boolFun(Program* p);
    int boolHelper(Program* p);

    bool isInit(int addr);
    bool isLisp(int addr);
    bool isAtom(int addr);
    bool isBool(int addr);

    int idleTemp();

    int getList(Program* p);
    void ifHelper(Program* p);
    int var(Program* p);
    int literal(Program* p);
    
    void freeLisps();

    void testInterp();
#else
    void retFun(Program* p);
    void listFun(Program* p);
    void intFun(Program* p);
    void boolFun(Program* p);

    void getList(Program* p);
    void var(Program* p);
    void literal(Program* p);

    void testParse();
#endif

#ifdef EXTENS
void elifsFun(Program* p);
#ifdef INTERP
bool elifFun(Program* p);
#else
void elifFun(Program* p);
#endif
#endif

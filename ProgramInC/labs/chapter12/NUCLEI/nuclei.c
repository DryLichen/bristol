#include "nuclei.h"

#ifdef INTERP
lisp* lisps[VARNUM];
#endif

int main(int argc, char const *argv[]) {
    test();

    // Program* program = NULL;
    // program = (Program*)ncalloc(1, sizeof(Program));

    return 0;
}

void getTokens(const char* fileName) {
    FILE* fp = fopen(fileName, "r");
    char tokens[MAXNUMTOKENS][MAXTOKENSIZE];
    char buffer[BUFLEN];
    while (fgets(buffer, BUFLEN, fp)) {
        
        /* code */
    }
    
    // for (int i = 0; i < MAXNUMTOKENS; i++) {
        
    // }
    
}

void prog(Program* p) {
    if (!strSame(p->wds[p->ptr], "(")) {
        error("Lack the first left bracket?");
    }
    (p->ptr)++;
    instrcts(p);
}

void instrcts(Program* p) {
    if (strSame(p->wds[p->ptr], ")")) {
        return;
    }
    instrct(p);
    instrcts(p);
}

void instrct(Program* p) {
    if (!strSame(p->wds[p->ptr], "(")) {
        error("Lack the left bracket for a function?");
    }
    (p->ptr)++;

    if (isRetFun(p->wds[p->ptr])) {
        retFun(p);
    } else if (isIOFun(p->wds[p->ptr])) {
        ioFun(p);
    } else if (isIfFun(p->wds[p->ptr])) {
        ifFun(p);
    } else if (isLoopFun(p->wds[p->ptr])) {
        isLoopFun(p);
    } else {
        error("Lack a function?");  
    }

    if (!strSame(p->wds[p->ptr], ")")) {
        error("Lack the right bracket for a function?");
    }
    (p->ptr)++;
}

bool isRetFun(char* str) {
    if (isListFun(str) || isIntFun(str) || isBoolFun(str)) {
        return true;
    }
    return false;
}

bool isListFun(char* str) {
    if (strSame(str, "CAR") || strSame(str, "CDR") || strSame(str, "CONS")) {
        return true;
    }
}

bool isIntFun(char* str) {
    if(strSame(str, "PLUS") || strSame(str, "LENGTH")) {
        return true;
    }
}

bool isBoolFun(char* str) {
    if (strSame(str, "LESS") || strSame(str, "GREATER") || strSame(str, "EQUAL")) {
        return true;
    }
}

void retFun(Program* p) {
    if (isListFun(p->wds[p->ptr])) {
        listFun(p);
    }
    if (isIntFun(p->wds[p->ptr])) {
        intFun(p);
    }
    if (isBoolFun(p->wds[p->ptr])) {
        boolFun(p);
    }
}

void listFun(Program* p) {
    if (strSame(p->wds[p->ptr], "CAR")) {
        (p->ptr)++;
        getList(p, DEFADDR);
        return;
    } 
    if (strSame(p->wds[p->ptr], "CDR")) {
        (p->ptr)++;
        getList(p, DEFADDR);
        return;
    } 
    if (strSame(p->wds[p->ptr], "CONS")) {
        (p->ptr)++;
        getList(p, DEFADDR);
        getList(p, DEFADDR);
        return;
    }
    
    error("Lack a list function?");
}

void intFun() {

}

void boolFun() {

}

// set the variable referred by addr
// if addr = -1, no need to set variables
void getList(Program* p, int addr) {
    if (addr == -1) {

    }

    if (p->wds[p->ptr][0] == '\'') {
        literal(p);
        return;
    }
    if (strSame(p->wds[p->ptr], "NIL")) {
        (p->ptr)++;
        return;
    }
    if (strSame(p->wds[p->ptr], "(")) {
        (p->ptr)++;
        list(p);
        if (!strSame(p->wds[p->ptr], ")")) {
            error("Lack a right single-quote for list?");
        }
        (p->ptr)++;
        return;
    }

    error("Lack a list?");
}

bool isIO(char* str) {
    if (strSame(str, "SET") || strSame(str, "PRINT")) {
        return true;
    }
    return false;
}

void ioFun(Program* p) {
    // set a variable
    if (strSame(p->wds[p->ptr], "SET")) {
        (p->ptr)++;
        int addr = var(p);
        getList(p, addr);
        return;
    }

    // print a variable 
    if (strSame(p->wds[p->ptr], "PRINT")) {
        (p->ptr)++;
        var(p);
        return;
    }
}

bool isIfFun(char* str) {
    return strSame(str, "IF");
}

void ifFun() {

}

bool isLoopFun(char* str) {
    return strSame(str, "WHILE");
}

void loopFun() {

}

// Check if a letter is a valid variable. If so, return the address of 
// the variable in the variable array
int var(Program* p) {
    if ((p->wds[p->ptr])[0] > 'Z' || (p->wds[p->ptr])[0] < 'A') {
        error("Expect a letter as variable?");
    }

    if (strlen(p->wds[p->ptr]) != VARLEN) {
        error("Length of a variable is too long?");
    }

    int addr = p->wds[p->ptr][0] - 'A';
    (p->ptr)++;
    return addr;
}

void string(Program* p) {
    if (p->wds[p->ptr][strlen(p->wds[p->ptr]) - 1] != '\"') {
        error("Expect a string?");
    }

    (p->ptr)++;
}

void literal(Program* p) {
    // check the right single-quote
    if (p->wds[p->ptr][strlen(p->wds[p->ptr]) - 1] != '\'') {
        error("Lack a right single-quote for literal list?");
    }

    // check if the literal list is valid 
    #ifdef INTERP
    int countL = 0, countR = 0;
    for (int i = 0; i < (int)strlen(p->wds[p->ptr]); i++) {
        if (p->wds[p->ptr][i] == '(') {
            countL++;
        } else if (p->wds[p->ptr][i] == ')') {
            countR++;
        }
    }
    if (countL == countR) {
        (p->ptr)++;
        return;
    }
    error("Wrong form of literal list?");
    #endif
}

bool strSame(char* str1, char* str2) {
    return strcmp(str1, str2) == 0 ? true : false;
}

void error(char* str) {
    fprintf(stderr, "Fatal Error : %s \n", str);
    exit(EXIT_FAILURE);
}

void test() {
    char wds[MAXNUMTOKENS][MAXTOKENSIZE] = {"(", "(", "SET", "A", "'1'", ")", "(", "PRINT", "A", ")", ")"};
    Program p;
    p.ptr = 0;
    for (int i = 0; i < MAXNUMTOKENS; i++) {
        strcpy(p.wds[i], wds[i]);
    }
    
    prog(&p);
    fprintf(stdout, "Parsed OK!");
}

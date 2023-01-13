#include "nuclei.h"

#ifdef INTERP
lisp* lisps[VARNUM];
#endif

int main(int argc, char const *argv[]) {
    test();

    if (argc != 2) {
        error("Wrong amount of parameters?");
    }
    FILE* fp = fopen(argv[1], "r");
    if (fp == NULL) {
        error("Can't open file?");
    }

    Program* program = getTokens(fp);
    prog(program);
    printf("Parsed OK!\n");

    fclose(fp);
    free(program);
    return 0;
}

Program* getTokens(FILE* fp) {
    Program* program = NULL;
    program = (Program*)ncalloc(1, sizeof(Program));
    program->ptr = 0;

    char buffer[BUFLEN];
    int count = 0;
    int wordPtr = 0;
    bool flag;
    while (fgets(buffer, BUFLEN, fp) != NULL) {
        if (buffer[0] != '#') {
            for (int i = 0; i < (int)strlen(buffer); i++, wordPtr = 0, flag = false) {
                // bracket
                if (buffer[i] == '(' || buffer[i] == ')') {
                    program->wds[count][wordPtr++] = buffer[i];
                    flag = true;
                }
                
                // word
                if (isalpha(buffer[i])) {
                    while (isalpha(buffer[i])) {
                        program->wds[count][wordPtr++] = buffer[i++];
                    }
                    i--;
                    flag = true;
                }

                // string or list
                if (buffer[i] == '\"' || buffer[i] == '\'') {
                    char c = buffer[i];
                    program->wds[count][wordPtr++] = buffer[i++];
                    while (buffer[i] != c) {
                        program->wds[count][wordPtr++] = buffer[i++];
                    }
                    program->wds[count][wordPtr++] = buffer[i];
                    flag = true;
                }

                // tail process
                if (flag) {
                    program->wds[count][wordPtr] = '\0';
                    count++;
                }
            }
        }
    }
    
    return program;
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
        (p->ptr)++;
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
        loopFun(p);
    } else {
        error("Lack a function?");  
    }

    if (!strSame(p->wds[p->ptr], ")")) {
        error("Lack the right bracket for a function?");
    }
    (p->ptr)++;
}

bool isRetFun(char* str) {
    return isListFun(str) || isIntFun(str) || isBoolFun(str);
}

bool isListFun(char* str) {
    return strSame(str, "CAR") || strSame(str, "CDR") || strSame(str, "CONS");
}

bool isIntFun(char* str) {
    return strSame(str, "PLUS") || strSame(str, "LENGTH");
}

bool isBoolFun(char* str) {
    return strSame(str, "LESS") || strSame(str, "GREATER") || strSame(str, "EQUAL");
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
        // getList(p, DEFADDR);
        getList(p);
        return;
    } 

    if (strSame(p->wds[p->ptr], "CDR")) {
        (p->ptr)++;
        // getList(p, DEFADDR);
        getList(p);
        return;
    } 

    if (strSame(p->wds[p->ptr], "CONS")) {
        (p->ptr)++;
        // getList(p, DEFADDR);
        // getList(p, DEFADDR);
        getList(p);
        getList(p);
        return;
    }
    
    error("Lack a list function?");
}

void intFun(Program* p) {
    if (strSame(p->wds[p->ptr], "PLUS")) {
        (p->ptr)++;
        // getList(p, DEFADDR);
        // getList(p, DEFADDR);
        getList(p);
        getList(p);
        return;
    }

    if (strSame(p->wds[p->ptr], "LENGTH")) {
        (p->ptr)++;
        // getList(p, DEFADDR);
        getList(p);
        return;
    }

    error("Lack a int function?");
}

void boolFun(Program* p) {
    if (strSame(p->wds[p->ptr], "LESS")) {
        (p->ptr)++;
        // getList(p, DEFADDR);
        // getList(p, DEFADDR);
        getList(p);
        getList(p);
        return;
    }
    
    if (strSame(p->wds[p->ptr], "GREATER")) {
        (p->ptr)++;
        // getList(p, DEFADDR);
        // getList(p, DEFADDR);
        getList(p);
        getList(p);
        return;
    }
    
    if (strSame(p->wds[p->ptr], "EQUAL")) {
        (p->ptr)++;
        // getList(p, DEFADDR);
        // getList(p, DEFADDR);
        getList(p);
        getList(p);
        return;
    }

    error("Lack a bool fnution?");
}

// set the variable referred by addr
// if addr = -1, no need to set variables

// void getList(Program* p, int addr) {

void getList(Program* p) {
    // if (addr == -1) {
        
    // }

    if (strSame(p->wds[p->ptr], "NIL")) {
        (p->ptr)++;
        return;
    }

    if (isalpha(p->wds[p->ptr][0])) {
        var(p);
        return;
    }

    if (p->wds[p->ptr][0] == '\'') {
        literal(p);
        return;
    }

    if (strSame(p->wds[p->ptr], "(")) {
        (p->ptr)++;
        retFun(p);
        if (!strSame(p->wds[p->ptr], ")")) {
            error("Lack a right single-quote for list?");
        }
        (p->ptr)++;
        return;
    }

    error("Lack a list?");
}

bool isIOFun(char* str) {
    return strSame(str, "SET") || strSame(str, "PRINT");
}

void ioFun(Program* p) {
    if (strSame(p->wds[p->ptr], "SET")) {
        (p->ptr)++;
        setFun(p);
        return;
    }

    if (strSame(p->wds[p->ptr], "PRINT")) {
        (p->ptr)++;
        printFun(p);
        return;
    }
}

// set a variable
void setFun(Program* p) {
    var(p);
    getList(p);
    return;
}

// print a variable 
void printFun(Program* p) {
    if (p->wds[p->ptr][0] == '\"') {
        string(p);
        return;
    } else {
        getList(p);
        return;
    }
}

bool isIfFun(char* str) {
    return strSame(str, "IF");
}

void ifFun(Program* p) {
    (p->ptr)++;

    if (!strSame(p->wds[p->ptr], "(")) {
        error("Lack a left bracket in if funtion?");
    }
    (p->ptr)++;

    boolFun(p);

    if (!strSame(p->wds[p->ptr], ")")) {
        error("Lack a right bracket in if funtion?");
    }
    (p->ptr)++;

    if (!strSame(p->wds[p->ptr], "(")) {
        error("Lack a left bracket in if funtion?");
    }
    (p->ptr)++;

    instrcts(p);

    if (!strSame(p->wds[p->ptr], "(")) {
        error("Lack a left bracket in if funtion?");
    }
    (p->ptr)++;

    instrcts(p);
}

bool isLoopFun(char* str) {
    return strSame(str, "WHILE");
}

void loopFun(Program* p) {
    (p->ptr)++;

    if (!strSame(p->wds[p->ptr], "(")) {
        error("Lack a left bracket?");
    }
    (p->ptr)++;

    boolFun(p);

    if (!strSame(p->wds[p->ptr], ")")) {
        error("Lack a right bracket?");
    }
    (p->ptr)++;

    if (!strSame(p->wds[p->ptr], "(")) {
        error("Lack a left bracket?");
    }
    (p->ptr)++;

    instrcts(p);
}

// Check if a letter is a valid variable. 
// If so, return the address of the variable in the variable array
void var(Program* p) {
    if (strlen(p->wds[p->ptr]) != VARLEN) {
        error("Length of the variable is too long?");
    }

    if ((p->wds[p->ptr])[0] > 'Z' || (p->wds[p->ptr])[0] < 'A') {
        error("Expect a alphabet letter?");
    }

    (p->ptr)++;
    // int addr = p->wds[p->ptr][0] - 'A';
    // return addr;
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
    (p->ptr)++;

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
    // char wds[MAXNUMTOKENS][MAXTOKENSIZE] = {"(", "(", "SET", "A", "'1'", ")", "(", "PRINT", "A", ")", ")"};
    // Program p;
    // p.ptr = 0;
    // for (int i = 0; i < MAXNUMTOKENS; i++) {
    //     strcpy(p.wds[i], wds[i]);
    // }
    
    // prog(&p);
    // fprintf(stdout, "Parsed OK!");
}

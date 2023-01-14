#include "nuclei.h"

#ifdef INTERP
lisp* lisps[VARNUM];
#endif

int main(int argc, char const *argv[]) {
    void testNuclei();

    if (argc != 2) {
        error("Wrong amount of parameters?");
    }
    FILE* fp = fopen(argv[1], "r");
    if (fp == NULL) {
        error("Can't open file?");
    }

    #ifdef INTERP
        initLisps();
    #endif

    Program* program = getTokens(fp);
    prog(program);

    #ifndef INTERP
        printf("Parsed OK\n");
    #endif

    fclose(fp);
    free(program);
    return 0;
}

#ifdef INTERP
// initialize the variable array
void initLisps() {
    for (int i = 0; i < VARNUM; i++) {
        lisps[i] = (lisp*)DEFADDR;
    }
}
#endif

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

// return the address of the variable
#ifdef INTERP
int retFun(Program* p) {
    if (isListFun(p->wds[p->ptr])) {
        return listFun(p);
    }
    if (isIntFun(p->wds[p->ptr])) {
        return intFun(p);
    }
    if (isBoolFun(p->wds[p->ptr])) {
        return boolFun(p);
    }

    error("Expect a return fucntion?");
    return NONE;
}
#else
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
#endif

// return the address of a lisp
#ifdef INTERP
int listFun(Program* p) {
#else
void listFun(Program* p) {
#endif
    if (strSame(p->wds[p->ptr], "CAR")) {
        (p->ptr)++;

        #ifdef INTERP
            int addr = getList(p);
            // in case there is a bool in the variable
            if (!isLisp(addr)) {
                error("Expect a lisp structure?");
            }

            int carAddr = idleChild();
            lisps[carAddr] = lisp_car(lisps[addr]);
            return carAddr;
        #else
            getList(p);
            return;
        #endif
    } 

    if (strSame(p->wds[p->ptr], "CDR")) {
        (p->ptr)++;

        #ifdef INTERP
            int addr = getList(p);
            // in case there is a bool in the variable
            if (!isLisp(addr)) {
                error("Expect a lisp structure?");
            }

            int cdrAddr = idleChild();
            lisps[cdrAddr] = lisp_cdr(lisps[addr]);
            return cdrAddr;
        #else
            getList(p);
            return;
        #endif
    } 

    if (strSame(p->wds[p->ptr], "CONS")) {
        (p->ptr)++;

        #ifdef INTERP
            int addr1 = getList(p);
            int addr2 = getList(p);
            // in case there is a bool in the variable
            if (!isLisp(addr1) || !isLisp(addr2)) {
                error("Expect a lisp structure?");
            }

            int consAddr = idleMoth();
            lisps[consAddr] = lisp_cons(lisps[addr1], lisps[addr2]);
            return consAddr;
        #else
            getList(p);
            getList(p);
            return;
        #endif
    }
    
    error("Lack a list function?");
    #ifdef INTERP
        return NONE;
    #endif
}

// return the address of an lisp atom
#ifdef INTERP
int intFun(Program* p) {
#else
void intFun(Program* p) {
#endif
    if (strSame(p->wds[p->ptr], "PLUS")) {
        (p->ptr)++;

        #ifdef INTERP
            int addr1 = getList(p);
            int addr2 = getList(p);
            // check if the operands are atoms
            if (!isAtom(addr1) || !isAtom(addr2)) {
                error("Expect an atom for plus?");
            }

            int sum = lisp_getval(lisps[addr1]) + lisp_getval(lisps[addr2]);
            int plusAddr = idleTemp();
            lisps[plusAddr] = lisp_atom(sum);
            return plusAddr;
        #else
            getList(p);
            getList(p);
            return;
        #endif
    }

    if (strSame(p->wds[p->ptr], "LENGTH")) {
        (p->ptr)++;

        #ifdef INTERP
            int addr = getList(p);
            // check if the operand is lisp
            if (!isLisp(addr)) {
                error("Expect a lisp structure?");
            }

            int lenAddr = idleTemp();
            lisps[lenAddr] = lisp_atom(lisp_length(lisps[addr]));
            return lenAddr;
        #else
            getList(p);
            return;
        #endif
    }

    error("Lack a int function?");
    #ifdef INTERP
        return NONE;
    #endif
}

#ifdef INTERP
// if a given variable is not bool, return true
bool isLisp(int addr) {
    return (int)lisps[addr] > NONE;
}

// return true if a given address storing an atom
bool isAtom(int addr) {
    if (isLisp(addr)) {
        return lisp_isatomic(lisps[addr]);
    }
    return false;
}
#endif

// return a bool value (not in a lisp structure)
#ifdef INTERP
int boolFun(Program* p) {
#else
void boolFun(Program* p) {
#endif
    if (strSame(p->wds[p->ptr], "LESS")) {
        (p->ptr)++;

        #ifdef INTERP
            int addr1 = getList(p);
            int addr2 = getList(p);
            // check if the operands are atoms
            if (!isAtom(addr1) || !isAtom(addr2)) {
                error("Expect an atom for bool operation?");
            }
            
            int boolAddr = idleTemp();
            if (lisp_getval(lisps[addr1]) < lisp_getval(lisps[addr2])) {
                lisps[boolAddr] = (lisp *)TRUE;
            } else {
                lisps[boolAddr] = (lisp *)FALSE;
            }
            return boolAddr;
        #else
            getList(p);
            getList(p);
            return;
        #endif
    }
    
    if (strSame(p->wds[p->ptr], "GREATER")) {
        (p->ptr)++;

        #ifdef INTERP
            int addr1 = getList(p);
            int addr2 = getList(p);
            // check if the operands are atoms
            if (!isAtom(addr1) || !isAtom(addr2)) {
                error("Expect an atom for bool operation?");
            }
            
            int boolAddr = idleTemp();
            if (lisp_getval(lisps[addr1]) > lisp_getval(lisps[addr2])) {
                lisps[boolAddr] = (lisp*)TRUE;
            } else {
                lisps[boolAddr] = (lisp*)FALSE;
            }
            return boolAddr;
        #else
            getList(p);
            getList(p);
            return;
        #endif
    }
    
    if (strSame(p->wds[p->ptr], "EQUAL")) {
        (p->ptr)++;

        #ifdef INTERP
            int addr1 = getList(p);
            int addr2 = getList(p);
            // check if the operands are atoms
            if (!isAtom(addr1) || !isAtom(addr2)) {
                error("Expect an atom for bool operation?");
            }
            
            int boolAddr = idleTemp();
            if (lisp_getval(lisps[addr1]) == lisp_getval(lisps[addr2])) {
                lisps[boolAddr] = (lisp*)TRUE;
            } else {
                lisps[boolAddr] = (lisp*)FALSE;
            }
            return boolAddr;
        #else
            getList(p);
            getList(p);
            return;
        #endif
    }

    error("Lack a bool fnution?");
    #ifdef INTERP
        return NONE;
    #endif
}

// return the address of the processed variable
#ifdef INTERP
int getList(Program* p) {
#else
void getList(Program* p) {
#endif
    // NIL
    if (strSame(p->wds[p->ptr], "NIL")) {
        (p->ptr)++;

        #ifdef INTERP
            int addr = idleTemp();
            lisps[addr] = NULL;
            return addr;
        #else
            return;
        #endif
    }

    // VAR
    if (isalpha(p->wds[p->ptr][0])) {
        #ifdef INTERP
            return var(p);
        #else
            var(p);
            return;
        #endif
    }

    // literal
    if (p->wds[p->ptr][0] == '\'') {
        #ifdef INTERP
            return literal(p);
        #else
            literal(p);
            return;
        #endif
    }

    // return function
    if (strSame(p->wds[p->ptr], "(")) {
        (p->ptr)++;

        #ifdef INTERP
            int addr = retFun(p);
            if (!strSame(p->wds[p->ptr], ")")) {
                error("Lack a right single-quote for list?");
            }
            (p->ptr)++;
            return addr;
        #else
            retFun(p);
            if (!strSame(p->wds[p->ptr], ")")) {
                error("Lack a right single-quote for list?");
            }
            (p->ptr)++;
            return;
        #endif
    }

    error("Lack a list?");
    #ifdef INTERP
        return NONE;
    #endif
}

#ifdef INTERP
// find an available temporary variable  
// if no one is available, report error
int idleTemp() {
    for (int i = TEMPSTART; i < CHILDSTART; i++) {
        if ((int)lisps[i] == DEFADDR) {
            return i;
        }
    }
    
    error("No available temporary variables?");
    return NONE;
}

// find an available child variable  
// if no one is available, report error
int idleChild() {
    for (int i = CHILDSTART; i < MOTHSTART; i++) {
        if ((int)lisps[i] == DEFADDR) {
            return i;
        }
    }
    error("No available child variables?");
    return NONE;
}

// find an available mother variable
// if no one is available, report error
int idleMoth() {
    for (int i = MOTHSTART; i < VARNUM; i++) {
        if ((int)lisps[i] == DEFADDR) {
            return i;
        }
    }
    error("No available mother variables?");
    return NONE;
}
#endif

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
    #ifdef INTERP
        int addr = var(p);
        int temp = getList(p);
        lisps[addr] = lisps[temp]; 
    #else
        var(p);
        getList(p);
    #endif
}

// print a variable 
void printFun(Program* p) {
    if (p->wds[p->ptr][0] == '\"') {
        string(p);
    } else {
        #ifdef INTERP
            int addr = getList(p);

            // check if it's a lisp
            if (isLisp(addr)) {
                char buff[BUFLEN];
                lisp_tostring(lisps[addr], buff);
                printf("%s\n", buff);
                return;
            }

            // check it it's a bool
            if ((int)lisps[addr] == TRUE) {
                printf("TRUE\n");
                return;
            } 
            if ((int)lisps[addr] == FALSE) {
                printf("FALSE\n");
                return;
            }

            error("Fail to print a uninitialized variable?");
        #else
            getList(p);
        #endif
    }
}

bool isBool(int addr) {
    return addr == TRUE || addr == FALSE;
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

    #ifdef INTERP
        int boolVal = (int)lisps[boolFun(p)];

        if (!strSame(p->wds[p->ptr], ")")) {
            error("Lack a right bracket in if funtion?");
        }
        (p->ptr)++;

        if (!strSame(p->wds[p->ptr], "(")) {
            error("Lack a left bracket in if funtion?");
        }
        (p->ptr)++;

        if (boolVal == TRUE) {
            instrcts(p);

            if (!strSame(p->wds[p->ptr], "(")) {
                error("Lack a left bracket in if funtion?");
            }
            (p->ptr)++;

            // move pointer to the end of if sub block
            int countL = 1, countR = 0;
            while (countL != countR) {
                if (strSame(p->wds[p->ptr], "(")) {
                    countL++;
                } else if (strSame(p->wds[p->ptr], ")")) {
                    countR++;
                }
                (p->ptr)++;
            }
        } else if (boolVal == FALSE) {
            // move the pointer to the start of if sub block
            int countL = 1, countR = 0;
            while (countL != countR) {
                if (strSame(p->wds[p->ptr], "(")) {
                    countL++;
                } else if (strSame(p->wds[p->ptr], ")")) {
                    countR++;
                }
                (p->ptr)++;
            }

            if (!strSame(p->wds[p->ptr], "(")) {
                error("Lack a left bracket in if funtion?");
            }
            (p->ptr)++;

            instrcts(p);
        }
    #else
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
    #endif
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

    #ifdef INTERP
        int beforeLoop = p->ptr;

        while ((int)lisps[boolFun(p)] == TRUE) {
            if (!strSame(p->wds[p->ptr], ")")) {
                error("Lack a right bracket?");
            }
            (p->ptr)++;

            if (!strSame(p->wds[p->ptr], "(")) {
                error("Lack a left bracket?");
            }
            (p->ptr)++;

            instrcts(p);
            p->ptr = beforeLoop;
        }

        if (!strSame(p->wds[p->ptr], ")")) {
            error("Lack a right bracket?");
        }
        (p->ptr)++;

        if (!strSame(p->wds[p->ptr], "(")) {
            error("Lack a left bracket?");
        }
        (p->ptr)++;

        // move the pointer to the end of the loop block
        int countL = 1, countR = 0;
        while (countL != countR) {
            if (strSame(p->wds[p->ptr], "(")) {
                countL++;
            } else if (strSame(p->wds[p->ptr], ")")) {
                countR++;
            }
            (p->ptr)++;
        }
    #else
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
    #endif
}

// Check if a letter is a valid variable. 
// If so, return the address of the variable in the variable array
#ifdef INTERP
int var (Program* p) {
#else
void var(Program* p) {
#endif
    if (strlen(p->wds[p->ptr]) != VARLEN) {
        error("Length of the variable is too long?");
    }

    if ((p->wds[p->ptr])[0] > 'Z' || (p->wds[p->ptr])[0] < 'A') {
        error("Expect a alphabet letter?");
    }


    #ifdef INTERP
        int addr = p->wds[p->ptr][0] - 'A';
        (p->ptr)++;
        return addr;
    #else
        (p->ptr)++;
    #endif
}

void string(Program* p) {
    if (p->wds[p->ptr][strlen(p->wds[p->ptr]) - 1] != '\"') {
        error("Expect a string?");
    }

    #ifdef INTERP
        // process string
        char buff[BUFLEN];
        size_t i = 1;
        for (; i < strlen(p->wds[p->ptr]) - 1; i++) {
            buff[i - 1] = p->wds[p->ptr][i];
        }
        buff[i - 1] = '\0';

        printf("%s\n", buff);
    #endif

    (p->ptr)++;
}

#ifdef INTERP
int literal(Program* p) {
#else
void literal(Program* p) {
#endif
    // check the right single-quote
    if (p->wds[p->ptr][strlen(p->wds[p->ptr]) - 1] != '\'') {
        error("Lack a right single-quote for literal list?");
    }

    // check if the literal list is valid 
    #ifdef INTERP
        // process string
        char buff[BUFLEN];
        size_t i = 1;
        for (; i < strlen(p->wds[p->ptr]) - 1; i++) {
            buff[i - 1] = p->wds[p->ptr][i];
        }
        buff[i - 1] = '\0';

        int addr = idleTemp();
        lisps[addr] = lisp_fromstring(buff);
        (p->ptr)++;
        return addr;
    #else
        (p->ptr)++;
    #endif
}

bool strSame(char* str1, char* str2) {
    return strcmp(str1, str2) == 0 ? true : false;
}

void error(char* str) {
    fprintf(stderr, "Fatal Error : %s \n", str);
    exit(EXIT_FAILURE);
}

#ifdef INTERP
// free all the variables and temp variables
void freeSpace() {
    // free temporary variables
    for (int i = TEMPSTART; i < CHILDSTART; i++) {
        if ((int)lisps[i] > 0) {
            lisp_free(&lisps[i]);
        }
    }

    // free the root of mother variables
    for (int i = CHILDSTART; i < VARNUM; i++) {
        if ((int)lisps[i] > 0) {
            free(lisps[i]);
            lisps[i] = NULL;
        }
    }
}
#endif

void testNuclei() {
    // char wds[MAXNUMTOKENS][MAXTOKENSIZE] = {"(", "(", "SET", "A", "'1'", ")", "(", "PRINT", "A", ")", ")"};
    // Program p;
    // p.ptr = 0;
    // for (int i = 0; i < MAXNUMTOKENS; i++) {
    //     strcpy(p.wds[i], wds[i]);
    // }
    
    // prog(&p);
    // fprintf(stdout, "Parsed OK!");
}

#include "nuclei.h"

#ifdef INTERP
lisp* lisps[VARNUM];
#endif

Program* program = NULL;

int main(int argc, char const *argv[]) {
    #ifdef INTERP
        testInterp();
    #else
        testParse();
    #endif

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
    program = getTokens(fp);
    prog(program);

    #ifdef INTERP
        freeLisps();
    #else
        printf("Parsed OK\n");
    #endif

    free(program);
    return 0;
}

#ifdef INTERP
// initialize the variable array with default address
void initLisps() {
    for (int i = 0; i < VARNUM; i++) {
        lisps[i] = (lisp*)DEFADDR;
    }
}
#endif

// read the file to get all the tokens and store them in program
Program* getTokens(FILE* fp) {
    Program* p = (Program*)ncalloc(1, sizeof(Program));
    p->ptr = 0;

    char buffer[BUFLEN];
    int count = 0, wordPtr = 0, len = 0;
    bool flag = true;
    while (fgets(buffer, BUFLEN, fp) != NULL) {
        clearEnd(buffer, &len);
        for (int i = 0; i < len; i++, wordPtr = 0, flag = true) {
            if (buffer[i] == '(' || buffer[i] == ')') {
                // 1. brackets
                p->wds[count][wordPtr++] = buffer[i];
            } else if (isalpha(buffer[i])) {
                // 2. words inlucding keywords, NIL and variables
                processWords(p, buffer, &i, &len, &count, &wordPtr);
            } else if (buffer[i] == '\"' || buffer[i] == '\'') {
                // 3. string or list
                processLit(p, buffer, &i, &len, &count, &wordPtr);
            } else if (buffer[i] == '#') {
                // 4. when detecting # indicating comment, skip the rest part
                i = len - 1;
                flag = false;
            } else if (!isspace(buffer[i])) {
                // 5. read other chars except space chars
                processOther(p, buffer, &i, &len, &count, &wordPtr);
            } else {
                // 6. spaces
                flag = false;
            }

            // tail process, add terminator at the end of a token
            if (flag) {
                p->wds[count++][wordPtr] = '\0';
            }
        }
    }
    fclose(fp);
    return p;
}

// delete the new line and return char at the end of buffer
void clearEnd(char* buffer, int* len) {
    *len = (int)strlen(buffer);
    if (*len <= 0) {

    }

    if (buffer[*len - 1] == '\n' || buffer[*len - 1] == '\r') {
        buffer[*len - 1] = '\0';
        *len = (int)strlen(buffer);
        if (*len <= 0) {
            return;
        }
        if (buffer[*len - 1] == '\n' || buffer[*len - 1] == '\r') {
            buffer[*len - 1] = '\0';
            *len = (int)strlen(buffer);
        }
    }
}

// read words inlucding keywords, NIL and variables
void processWords(Program* p, char* buffer, int* i, int* len, int* count, int* wordPtr) {
    while (*i < *len && isalpha(buffer[*i])) {
        p->wds[*count][(*wordPtr)++] = buffer[(*i)++];
    }
    (*i)--;
}

// read string or literal lisp
void processLit(Program* p, char* buffer, int* i, int* len, int* count, int* wordPtr) {
    char c = buffer[*i];
    p->wds[*count][(*wordPtr)++] = buffer[(*i)++];
    while (*i < *len - 1 && buffer[*i] != c) {
        p->wds[*count][(*wordPtr)++] = buffer[(*i)++];
    }
    p->wds[*count][(*wordPtr)++] = buffer[*i];
}

// read other chars except space chars
void processOther(Program* p, char* buffer, int* i, int* len, int* count, int* wordPtr) {
    while (*i < *len && !isspace(buffer[*i])) {
        p->wds[*count][(*wordPtr)++] = buffer[(*i)++];
    }
    (*i)--;
}

void prog(Program* p) {
    if (!strSame(p->wds[p->ptr], "(")) {
        error("Expect a first left bracket?");
    }
    (p->ptr)++;
    instrcts(p);
}

// process all the lines inside the program
void instrcts(Program* p) {
    if (strSame(p->wds[p->ptr], ")")) {
        (p->ptr)++;
        return;
    }
    instrct(p);
    instrcts(p);
}

// process only one function, report error when it's not a function 
void instrct(Program* p) {
    if (!strSame(p->wds[p->ptr], "(")) {
        error("Expect a left bracket for a function?");
    }
    (p->ptr)++;

    // call functions in terms of the prefix
    if (isRetFun(p->wds[p->ptr])) {
        retFun(p);
    } else if (isIOFun(p->wds[p->ptr])) {
        ioFun(p);
    } else if (isIfFun(p->wds[p->ptr])) {
        ifFun(p);
    } else if (isLoopFun(p->wds[p->ptr])) {
        loopFun(p);
    } else {
        error("Expect a function?");  
    }

    if (!strSame(p->wds[p->ptr], ")")) {
        error("Expect a right bracket for a function?");
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

// call return functions in terms of the prefix. Report error if not a return function
// return the location of the variable in array
#ifdef INTERP
int retFun(Program* p) {
#else
void retFun(Program* p) {
#endif
    if (isListFun(p->wds[p->ptr])) {
        #ifdef INTERP
            return listFun(p);
        #else
            listFun(p);
        #endif
    } else if (isIntFun(p->wds[p->ptr])) {
        #ifdef INTERP
            return intFun(p);
        #else
            intFun(p);
        #endif
    } else if (isBoolFun(p->wds[p->ptr])) {
        #ifdef INTERP
            return boolFun(p);
        #else
            boolFun(p);
        #endif
    } else {
        error("Expect a return fucntion?");
    }

    #ifdef INTERP
        return NONE;
    #endif
}

// check if the format of return list funcion is correct
// return the location of a lisp structure in array
#ifdef INTERP
int listFun(Program* p) {
#else
void listFun(Program* p) {
#endif
    if (strSame(p->wds[p->ptr], "CAR")) {
        (p->ptr)++;
        #ifdef INTERP
            int addr = getList(p);
            listHelper(addr);

            int carAddr = idleTemp();
            lisps[carAddr] = lisp_copy(lisp_car(lisps[addr]));
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
            listHelper(addr);

            int cdrAddr = idleTemp();
            lisps[cdrAddr] = lisp_copy(lisp_cdr(lisps[addr]));
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
            listHelper(addr1);
            listHelper(addr2);

            int consAddr = idleTemp();
            lisp* l = lisp_cons(lisps[addr1], lisps[addr2]);
            lisps[consAddr] = lisp_copy(l);
            free(l);
            l = NULL;
            return consAddr;
        #else
            getList(p);
            getList(p);
            return;
        #endif
    }
    
    error("Expect a list function?");
    #ifdef INTERP
        return NONE;
    #endif
}

#ifdef INTERP
// check if the variable is uninitialized or it's a bool
void listHelper(int addr) {
    if (!isInit(addr)) {
        error("Variable should be initialized before use?");
    }
    if (!isLisp(addr)) {
        error("Expect a lisp structure?");
    }
}
#endif

// check if the format of return int funcion is correct
// return the location of a lisp atom in array
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
            intHelper(addr1);
            intHelper(addr2);

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
            listHelper(addr);

            int lenAddr = idleTemp();
            lisps[lenAddr] = lisp_atom(lisp_length(lisps[addr]));
            return lenAddr;
        #else
            getList(p);
            return;
        #endif
    }

    error("Expect a int function?");
    #ifdef INTERP
        return NONE;
    #endif
}

#ifdef INTERP
// check if the variable is uninitialized or it's not a atoms
void intHelper(int addr) {
    if (!isInit(addr)) {
        error("Variable should be initialized before use?");
    }
    if (!isAtom(addr)) {
        error("Expect an atom?");
    }
}
#endif

// check if the format of return bool funcion is correct
// return the location of a bool in array (store in array directly, not in lisp)
#ifdef INTERP
int boolFun(Program* p) {
#else
void boolFun(Program* p) {
#endif
    if (strSame(p->wds[p->ptr], "LESS")) {
        (p->ptr)++;

        #ifdef INTERP
            int flag = boolHelper(p);
            int boolAddr = idleTemp();
            lisps[boolAddr] = flag < 0 ? (lisp*)TRUE : (lisp*)FALSE;
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
            int flag = boolHelper(p);
            int boolAddr = idleTemp();
            lisps[boolAddr] = flag > 0 ? (lisp*)TRUE : (lisp*)FALSE;
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
            int flag = boolHelper(p);
            int boolAddr = idleTemp();
            lisps[boolAddr] = flag == 0 ? (lisp*)TRUE : (lisp*)FALSE;
            return boolAddr;
        #else
            getList(p);
            getList(p);
            return;
        #endif
    }

    error("Expect a bool funtion?");
    #ifdef INTERP
        return NONE;
    #endif
}

// check if the variable is uninitialized or it's not a atoms
// return the difference between value1 and value2
#ifdef INTERP 
int boolHelper(Program* p) {
    int addr1 = getList(p);
    int addr2 = getList(p);
    intHelper(addr1);
    intHelper(addr2);

    return lisp_getval(lisps[addr1]) - lisp_getval(lisps[addr2]);
}
#endif

#ifdef INTERP
// if a given variable is initialized, return true
bool isInit(int addr) {
    return (int)lisps[addr] != DEFADDR;
}

// if a given variable is a lisp structure, return true
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

// return true if a variable is bool
bool isBool(int addr) {
    return (int)lisps[addr] == TRUE || (int)lisps[addr] == FALSE;
}
#endif

// check if the tokens are a legal list structure
// return the location of the processed variable in array
#ifdef INTERP
int getList(Program* p) {
#else
void getList(Program* p) {
#endif
    // NIL, return a idle temp variable and set it to NULL
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

    // VAR, return the location of the alphabet variable 
    if (isalpha(p->wds[p->ptr][0])) {
        #ifdef INTERP
            return var(p);
        #else
            var(p);
            return;
        #endif
    }

    // literal, return the location storing literal lisp
    if (p->wds[p->ptr][0] == '\'') {
        #ifdef INTERP
            return literal(p);
        #else
            literal(p);
            return;
        #endif
    }

    // call return functions
    if (strSame(p->wds[p->ptr], "(")) {
        (p->ptr)++;

        #ifdef INTERP
            int addr = retFun(p);
            if (!strSame(p->wds[p->ptr], ")")) {
                error("Expect a right single-quote for list?");
            }
            (p->ptr)++;
            return addr;
        #else
            retFun(p);
            if (!strSame(p->wds[p->ptr], ")")) {
                error("Expect a right single-quote for list?");
            }
            (p->ptr)++;
            return;
        #endif
    }

    error("Expect a list?");
    #ifdef INTERP
        return NONE;
    #endif
}

// find an available temporary variable  
// if no one is available, free a location from the start and return it
#ifdef INTERP
int idleTemp() {
    static int ptr = TEMPSTART;

    for (int i = TEMPSTART; i < VARNUM; i++) {
        if ((int)lisps[i] == DEFADDR) {
            return i;
        }
    }

    int addr = ptr++;
    // only free it when it's a lisp
    if (isLisp(addr)) {
        lisp_free(&lisps[addr]);
    }
    lisps[addr] = (lisp*)DEFADDR;
    if (ptr >= VARNUM) {
        ptr = TEMPSTART;
    }
    return addr;
}
#endif

bool isIOFun(char* str) {
    return strSame(str, "SET") || strSame(str, "PRINT");
}

// call IO functions in terms of the prefix
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

    error("Expect a IO function?");
}

// set a alphabet variable with the deep copy of the tmp list
void setFun(Program* p) {
    #ifdef INTERP
        int setAddr = var(p);
        int addr = getList(p);
        lisp* l = NULL;

        // lisp case
        if (isLisp(addr)) {
            l = lisp_copy(lisps[addr]);
        }
        // bool case
        if (isBool(addr)) {
            l = lisps[addr];
        }

        // if the target location contains a lisp already, free it
        if (isLisp(setAddr)) {
            lisp_free(&lisps[setAddr]);
        }
        lisps[setAddr] = l;
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

            // check if it's a bool
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

bool isIfFun(char* str) {
    return strSame(str, "IF");
}

// if function: if bool {} {}
void ifFun(Program* p) {
    (p->ptr)++;
    checkLBrace(p);

    #ifdef INTERP
        int boolVal = (int)lisps[boolFun(p)];
        checkRBrace(p);
        checkLBrace(p);

        if (boolVal == TRUE) {
            instrcts(p);
            #ifdef EXTENS
                // skip elif
                while (strSame(p->wds[p->ptr], "ELIF")) {
                    (p->ptr)++;
                    checkLBrace(p);
                    ifHelper(p);
                    checkLBrace(p);
                    ifHelper(p);
                }
                checkLBrace(p);
                // move pointer to the end of if sub block
                ifHelper(p);
            #else
                checkLBrace(p);
                // move pointer to the end of if sub block
                ifHelper(p);
            #endif
        } else if (boolVal == FALSE) {
            // move the pointer to the start of if sub block
            #ifdef EXTENS
                ifHelper(p);
                int flag = false;
                while (strSame(p->wds[p->ptr], "ELIF")) {
                    (p->ptr)++;
                    flag = elifFun(p);
                }
                if (!flag) {
                    checkLBrace(p);
                    instrcts(p);
                }
            #else
                ifHelper(p);
                checkLBrace(p);
                instrcts(p);
            #endif
        }
    #else
        boolFun(p);
        checkRBrace(p);
        checkLBrace(p);
        instrcts(p);
        #ifdef EXTENS
            elifsFun(p);
            instrcts(p);
        #else
            checkLBrace(p);
            instrcts(p);
        #endif
    #endif
}

#ifdef INTERP
// move the pointer to the end of a if sub block
void ifHelper(Program* p) {
    int countL = 1, countR = 0;
    while (countL != countR) {
        if (strSame(p->wds[p->ptr], "(")) {
            countL++;
        } else if (strSame(p->wds[p->ptr], ")")) {
            countR++;
        }
        (p->ptr)++;
    }
}
#endif

#ifdef EXTENS
void elifsFun(Program* p) {
    if (strSame(p->wds[p->ptr], "(")) {
        (p->ptr)++;
        return;
    }

    if (strSame(p->wds[p->ptr], "ELIF")) {
        (p->ptr)++;
        elifFun(p);
    }
    elifsFun(p);
}

#ifdef INTERP
bool elifFun(Program* p) {
#else
void elifFun(Program* p) {
#endif
    #ifdef INTERP
        checkLBrace(p);
        int boolVal = (int)lisps[boolFun(p)];
        checkRBrace(p);
        checkLBrace(p);

        if (boolVal == TRUE) {
            instrcts(p);
            // skip elif
            while (strSame(p->wds[p->ptr], "ELIF")) {
                (p->ptr)++;
                checkLBrace(p);
                ifHelper(p);
                checkLBrace(p);
                ifHelper(p);
            }
            checkLBrace(p);
            // move pointer to the end of if sub block
            ifHelper(p);
            return true;
        } else if (boolVal == FALSE) {
            // move the pointer to the start of elif sub block
            ifHelper(p);
            return false;
        }

        return false;
    #else
        checkLBrace(p);
        boolFun(p);
        checkRBrace(p);
        checkLBrace(p);
        instrcts(p);
    #endif
}
#endif

bool isLoopFun(char* str) {
    return strSame(str, "WHILE");
}

// loop function: while bool {}
void loopFun(Program* p) {
    (p->ptr)++;
    checkLBrace(p);

    #ifdef INTERP
        // store the start location of the loop
        int beforeLoop = p->ptr;
        while ((int)lisps[boolFun(p)] == TRUE) {
            checkRBrace(p);
            checkLBrace(p);
            instrcts(p);
            p->ptr = beforeLoop;
        }

        checkRBrace(p);
        checkLBrace(p);

        // move the pointer to the end of the loop block
        ifHelper(p);
    #else
        boolFun(p);
        checkRBrace(p);
        checkLBrace(p);
        instrcts(p);
    #endif
}

// Check if a letter is a valid variable. 
// If so, return the location of the variable in array
#ifdef INTERP
int var (Program* p) {
#else
void var(Program* p) {
#endif
    if (strlen(p->wds[p->ptr]) != VARLEN) {
        error("Length of the variable is too long?");
    }

    if ((p->wds[p->ptr])[0] > 'Z' || (p->wds[p->ptr])[0] < 'A') {
        error("Expect an uppercase alphabet letter?");
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

// return the location of the literal variable
// find a idle temp variable and set it with the literal lisp
#ifdef INTERP
int literal(Program* p) {
#else
void literal(Program* p) {
#endif
    // check the right single-quote
    if (p->wds[p->ptr][strlen(p->wds[p->ptr]) - 1] != '\'') {
        error("Expect a right single-quote for literal list?");
    }

    // check if the literal list is valid 
    #ifdef INTERP
        // process string by deleting single-quotes
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

// return true if two string is the same
bool strSame(char* str1, char* str2) {
    return strcmp(str1, str2) == 0 ? true : false;
}

// check if the left brace exits
void checkLBrace(Program* p) {
    if (!strSame(p->wds[p->ptr], "(")) {
        error("Expect a left bracket for if or loop funtion?");
    }
    (p->ptr)++;
}

// check if the right brace exits
void checkRBrace(Program* p) {
    if (!strSame(p->wds[p->ptr], ")")) {
        error("Expect a left bracket for if or loop funtion?");
    }
    (p->ptr)++;
}

void error(char* str) {
    fprintf(stderr, "Fatal Error : %s \n", str);
    #ifdef INTERP
        freeLisps();
    #endif
    free(program);
    exit(EXIT_FAILURE);
}

#ifdef INTERP
// free all the allocated spaces
void freeLisps() {
    // free all of the variables
    for (int i = 0; i < VARNUM; i++) {
        if (isLisp(i)) {
            lisp_free(&lisps[i]);
        }
        lisps[i] = NULL;
    }
}
#endif

#ifdef INTERP
void testInterp() {
    // test for getToken()
    testToken();

    // test for common functions
    testBasic();
    
    // tests for semantic checkers
    char wds[MAXNUMTOKENS][MAXTOKENSIZE] = {"(", "(", "SET", "A", "\'1\'", ")", "(", "PLUS", "A", "\'3\'", ")", ")"};
    Program p;
    p.ptr = 0;
    for (int i = 0; i < MAXNUMTOKENS; i++) {
        strcpy(p.wds[i], wds[i]);
    }

    // tests for initialization
    initLisps();
    assert((int)lisps[23] == -2);
    assert((int)lisps[100] == -2);

    assert(idleTemp() == TEMPSTART);

    p.ptr = 4;
    assert(literal(&p) == TEMPSTART);
    assert(p.ptr == 5);
    p.ptr = 4;
    assert(getList(&p) == TEMPSTART + 1);
    assert(p.ptr == 5);
    checkRBrace(&p);
    assert(p.ptr == 6);

    p.ptr = 3;
    assert(var(&p) == 0);
    assert(p.ptr == 4);

    p.ptr = 3;
    setFun(&p);
    assert(lisp_getval(lisps[0]) == lisp_getval(lisps[TEMPSTART + 2]));
    assert(lisp_getval(lisps[0]) == 1);
    assert(p.ptr == 5);

    p.ptr = 7;
    assert(intFun(&p) == TEMPSTART + 4);
    assert(lisp_getval(lisps[TEMPSTART + 3]) == 3);
    assert(lisp_getval(lisps[TEMPSTART + 4]) == 4);
    assert(p.ptr == 10);

    p.ptr = 0;
    prog(&p);
    assert(p.ptr == 12);

    freeLisps();
}

#else
void testParse() {
    // test for getToken()
    testToken();

    // test for common functions
    testBasic();

    // initiate a basic program object
    char wds[MAXNUMTOKENS][MAXTOKENSIZE] = {"(", "(", "SET", "A", "\'1\'", ")", "(", "PRINT", "\"NO\"", ")", ")"};
    Program p;
    p.ptr = 0;
    for (int i = 0; i < MAXNUMTOKENS; i++) {
        strcpy(p.wds[i], wds[i]);
    }

    // tests for syntax checkers
    checkLBrace(&p);
    assert(p.ptr = 1);
    p.ptr = 3;
    var(&p);
    assert(p.ptr == 4);
    literal(&p);
    assert(p.ptr == 5);
    checkRBrace(&p);
    assert(p.ptr = 6);
    p.ptr = 4;
    getList(&p);
    assert(p.ptr == 5);
    p.ptr = 2;
    ioFun(&p);
    assert(p.ptr == 5);
    p.ptr = 3;
    setFun(&p);
    assert(p.ptr == 5);

    p.ptr = 8;
    string(&p);
    assert(p.ptr == 9);
    p.ptr = 8;
    printFun(&p);
    assert(p.ptr == 9);

    p.ptr = 0;
    prog(&p);
    assert(p.ptr == 11);
}
#endif

// tests for getToken()
void testToken() {
    FILE* filePtr = fopen("testToken.ncl", "r");
    Program* pFile = NULL;
    pFile = getTokens(filePtr);

    char wds[MAXNUMTOKENS][MAXTOKENSIZE] = {"(", "(", "(", "SET", "A", "\'  1   \'", ")", "&&70",
                                     "3434", "\" AA#", "(", "PRINT", "fs", "7}", "A", ")", ")"};
    Program p;
    p.ptr = 0;
    for (int i = 0; i < TESTTOKENS; i++) {
        strcpy(p.wds[i], wds[i]);
    }

    // check if all the tokens are same
    for (int i = 0; i < TESTTOKENS; i++) {
        assert(strcmp(pFile->wds[i], p.wds[i]) == 0);
    }

    free(pFile);
}

// tests for common functions
void testBasic() {
    // tests for redirector functions
    assert(isRetFun("CAR"));
    assert(isRetFun("PLUS"));
    assert(!isRetFun("IF"));
    assert(isListFun("CONS"));
    assert(!isListFun("PLUS"));
    assert(isIntFun("LENGTH"));
    assert(!isIntFun("WHILE"));
    assert(isBoolFun("GREATER"));
    assert(isIOFun("PRINT"));
    assert(!isIOFun(" PRINT"));
    assert(isIfFun("IF"));
    assert(isLoopFun("WHILE"));

    // helpers
    assert(strSame("ADB", "ADB"));
    assert(!strSame("ADB", "adb"));
}

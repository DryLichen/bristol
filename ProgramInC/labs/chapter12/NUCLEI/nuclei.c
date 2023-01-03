#include "nuclei.h"

#ifdef INTERP
lisp* lisps[VARNUM];
#endif

// int main(int argc, char const *argv[]) {
int main() {
    test();

    // Program* program = NULL;
    // program = (Program*)ncalloc(1, sizeof(Program));

    return 0;
}

void prog(Program* p) {
    if (!strSame(p->wds[p->ptr], "(")) {
        error("Expecting the first left bracket?");
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
        error("Expecting a left bracket for a function?");
    }
    (p->ptr)++;

    if (isList(p->wds[p->ptr])) {
        list(p);
    } else if (isIO(p->wds[p->ptr])) {
        io(p);
    } else {
        error("Expecting a function?");
    }
    
    if (!strSame(p->wds[p->ptr], ")")) {
        error("Expecting a right bracket for a function?");
    }
    (p->ptr)++;
}

bool isList(char* str) {
    if (strSame(str, "CAR") || strSame(str, "CDR") || strSame(str, "CONS")) {
        return true;
    }
    return false;
}

void list(Program* p) {
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
    
    error("Expecting a list function?");
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
            error("Expecting a right single-quote for list?");
        }
        (p->ptr)++;
        return;
    }

    error("Expecting a list?");
}

bool isIO(char* str) {
    if (strSame(str, "SET") || strSame(str, "PRINT")) {
        return true;
    }
    return false;
}

void io(Program* p) {
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

// Check if a letter is a valid variable. If so, return the address of 
// the variable in the variable array
int var(Program* p) {
    if ((p->wds[p->ptr])[0] > 'Z' || (p->wds[p->ptr])[0] < 'A') {
        error("Expecting a variable?");
    }

    int addr = p->wds[p->ptr][0] - 'A';
    (p->ptr)++;
    return addr;
}

void literal(Program* p) {
    // check the right single-quote
    if (p->wds[p->ptr][strlen(p->wds[p->ptr]) - 1] != '\'') {
        error("Expecting a right single-quote for literal list?");
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

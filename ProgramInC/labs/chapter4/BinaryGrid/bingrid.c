#include "bingrid.h"
#include "neillsimplescreen.h"

void printboard(board* brd);
bool pairs(board *brd);
void pairsRows(board *brd, int size, bool *flag);
void pairsColumns(board *brd, int size, bool *flag);
bool oxo(board *brd);
void oxoRows(board *brd, int size, bool *flag);
void oxoColumns(board *brd, int size, bool *flag);
bool counting(board *brd);
void countingRows(board *brd, int size, bool *flag);
void countingColumns(board *brd, int size, bool *flag);
void countingHelper(board *brd, int row, int column, int *zeroCount, int *oneCount);
bool isSquareEven(int num);
bool isSolved(board *brd);

// Given an input string, complete information for board
bool str2board(board* brd, char* str) {
    int strLength = strlen(str);

    // invalid cases
    if (strcmp(str, "") == 0) {
        return false;
    }
    if (!isSquareEven(strLength)) {
        return false;
    }

    // general cases
    brd->sz = sqrt(strLength);
    for (int i = 0; i < brd->sz; i++) {
        for (int j = 0; j < brd->sz; j++) {
            brd->b2d[i][j] = *(str + i * brd->sz + j);
        }
    }

    return true;
}

// Given a board structure, create string version
void board2str(char* str, board* brd) {
    int size = brd->sz;

    // empty board
    if (size == 0) {
        strcpy(str, "");
    }

    // general cases
    for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
            *(str + i * size + j) = brd->b2d[i][j];
        }
    }
    *(str + size * size) = '\0';
}

// Given a board, apply all rules repeatedly - return true if solved, false otherwise
bool solve_board(board* brd) {
    bool flag = false;

    do {
        bool flagPairs = pairs(brd);
        bool flagOxo = oxo(brd);
        bool flagCounting = counting(brd);
        flag = flagPairs || flagOxo || flagCounting;
    } while (flag);
    
    return isSolved(brd);
}

void printboard(board* brd) {
    neillcol c;
    c = red; 
    neillclrscrn();

    int size = brd->sz;
    for (int i = 0; i < size; i++) {
        neillbgcol(c + i % 6);
        for (int j = 0; j < size; j++) {
            neillfgcol(c + j % 6);
            printf("%c", brd->b2d[i][j]);
        }
        printf("\n");
    }

    printf("\n");

    neillbusywait(2.0);
    neillreset();
}

// return true when a board is changed following rule pairs
bool pairs(board *brd) {
    int size = brd->sz;
    bool flag = false;

    pairsRows(brd, size, &flag);
    pairsColumns(brd, size, &flag);

    return flag;
}

// check rows following rule pairs
void pairsRows(board *brd, int size, bool *flag) {
    for(int i = 0; i < size; i++) {
        for (int j = 0; j < size - 1; j++) {
            if (brd->b2d[i][j] != '.' && brd->b2d[i][j] == brd->b2d[i][j + 1]) {
                if (j + 2 < size && brd->b2d[i][j + 2] == '.') {
                    brd->b2d[i][j + 2] = brd->b2d[i][j] == '0' ? '1' : '0';
                    *flag = true;
                }
                if (j - 1 >= 0 && brd->b2d[i][j - 1] == '.') {
                    brd->b2d[i][j - 1] = brd->b2d[i][j] == '0' ? '1' : '0';
                    *flag = true;
                }
            }
        }
    }
}

// check columns following rule pairs
void pairsColumns(board *brd, int size, bool *flag) {
    for (int j = 0; j < size; j++) {
        for (int i = 0; i < size - 1; i++) {
            if (brd->b2d[i][j] != '.' && brd->b2d[i][j] == brd->b2d[i + 1][j]) {
                if (i + 2 < size && brd->b2d[i + 2][j] == '.') {
                    brd->b2d[i + 2][j] = brd->b2d[i][j] == '0' ? '1' : '0';
                    *flag = true;
                }
                if (i - 1 >= 0 && brd->b2d[i - 1][j] == '.') {
                    brd->b2d[i - 1][j] = brd->b2d[i][j] == '0' ? '1' : '0';
                    *flag = true;
                }
            }
        }
    }
}

// return true when a board is changed following rule oxo
bool oxo(board *brd) {
    int size = brd->sz;
    bool flag = false;

    oxoRows(brd, size, &flag);
    oxoColumns(brd, size, &flag);

    return flag;
}

// check rows following rule oxo
void oxoRows(board *brd, int size, bool *flag) {
    for(int i = 0; i < size; i++) {
        for (int j = 0; j < size - 2; j++) {
            if (brd->b2d[i][j + 1] == '.') {
                if (brd->b2d[i][j] != '.' && brd->b2d[i][j] == brd->b2d[i][j + 2]) {
                    brd->b2d[i][j + 1] = brd->b2d[i][j] == '0' ? '1' : '0';
                    *flag = true;
                }
            }
        }
    }
}

// check columns following rule oxo
void oxoColumns(board *brd, int size, bool *flag) {
    for(int j = 0; j < size; j++) {
        for (int i = 0; i < size - 2; i++) {
            if (brd->b2d[i + 1][j] == '.') {
                if (brd->b2d[i][j] != '.' && brd->b2d[i][j] == brd->b2d[i + 2][j]) {
                    brd->b2d[i + 1][j] = brd->b2d[i][j] == '0' ? '1' : '0';
                    *flag = true;
                }
            }
        }
    }
}

// return true when a board is changed following rule counting
bool counting(board *brd) {
    int size = brd->sz;
    bool flag = false;

    countingRows(brd, size, &flag);
    countingColumns(brd, size, &flag);

    return flag;
}

// check rows following rule counting
void countingRows(board *brd, int size, bool *flag) {
    for(int i = 0; i < size; i++) {
        int zeroCount = 0, oneCount = 0;
        // calculate amount of 1 and 0 in ith row
        for (int j = 0; j < size; j++) {
            countingHelper(brd, i, j, &zeroCount, &oneCount);
        }

        if (zeroCount + oneCount != size && zeroCount * 2 == size){
            for (int j = 0; j < size; j++) {
                if (brd->b2d[i][j] == '.') {
                    brd->b2d[i][j] = '1';
                }
            }
            *flag = true;
        } else if (zeroCount + oneCount != size && oneCount * 2 == size) {
            for (int j = 0; j < size; j++) {
                if (brd->b2d[i][j] == '.') {
                    brd->b2d[i][j] = '0';
                }
            }
            *flag = true;
        }
    }
}

// check columns following rule counting
void countingColumns(board *brd, int size, bool *flag) {
    for (int j = 0; j < size; j++) {
        int zeroCount = 0, oneCount = 0;
        // calculate amount of 1 and 0 in ith row
        for (int i = 0; i < size; i++) {
            countingHelper(brd, i, j, &zeroCount, &oneCount);
        }

        if (zeroCount + oneCount != size && zeroCount * 2 == size){
            for (int i = 0; i < size; i++) {
                if (brd->b2d[i][j] == '.') {
                    brd->b2d[i][j] = '1';
                }
            }
            *flag = true;
        } else if (zeroCount + oneCount != size && oneCount * 2 == size) {
            for (int i = 0; i < size; i++) {
                if (brd->b2d[i][j] == '.') {
                    brd->b2d[i][j] = '0';
                }
            }
            *flag = true;
        }   
    }
}

// calculate amount of 1 and 0 in ith row or column
void countingHelper(board *brd, int row, int column, int *zeroCount, int *oneCount) {
    if (brd->b2d[row][column] == '0') {
        (*zeroCount)++;
    } else if (brd->b2d[row][column] == '1') {
        (*oneCount)++;
    }
}

// return true if length of a board is even
bool isSquareEven(int num) {
    for (int i = 2; i <= sqrt(num); i+=2) {
        if (i * i == num) {
            return true;
        }
    }
    return false;
}

// return true if a borad only comprises 0 and 1
bool isSolved(board *brd) {
    for (int i = 0; i < brd->sz; i++) {
        for (int j = 0; j < brd->sz; j++) {
            if (brd->b2d[i][j] == '.') {
                return false;
            }
        }
    }
    return true;
}

void test(void) {
    assert(isSquareEven(4));
    assert(isSquareEven(36));
    assert(!isSquareEven(0));
    assert(!isSquareEven(1));
    assert(!isSquareEven(7));

    board brd;
    char str[256];

    // unsolvable case
    assert(str2board(&brd, "...1.0.........1"));
    printboard(&brd);
    assert(!solve_board(&brd));

    // solvable case
    assert(str2board(&brd, "....0.0....1..0."));
    printboard(&brd);

    assert(counting(&brd));
    printboard(&brd);
    board2str(str, &brd);
    assert(strcmp(str, "..100101..11..00") == 0);

    assert(pairs(&brd));
    printboard(&brd);
    board2str(str, &brd);
    assert(strcmp(str, "..100101.011.100") == 0);
    
    assert(!oxo(&brd));
    printboard(&brd);
    board2str(str, &brd);
    assert(strcmp(str, "..100101.011.100") == 0);
    
    printf("Pass all the tests!\n");
}

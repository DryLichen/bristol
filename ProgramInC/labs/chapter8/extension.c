#include "extension.h"

int main(int argc, const char* argv[]) {
    test();

    show(argc, argv);
    
    return 0;
}

// print all the steps of the solution when the second parameter is -show
// otherwise, only print the amount of steps
void show(int argc, const char* argv[]) {
    Carpark* carpark = NULL;
    Carpark** parkArray = NULL;
    int ptrs[2];

    if (argc == 2) {
        carpark = getRoot(argv[1]);
    } else if (argc == 3 && !strcmp(argv[1], "-show")){
        carpark = getRoot(argv[2]);
    } else {
        fprintf(stderr, "Illegal input for the program!\n");
        exit(EXIT_FAILURE);
    }

    if (!isSolvable(carpark)) {
        fprintf(stderr, "No Solution?\n");
        freeCarpark(carpark);
        exit(EXIT_FAILURE);
    }

    parkArray = getParkArray(carpark);
    if (!bfs(parkArray, ptrs)) {
        fprintf(stderr, "No Solution?\n");
        freeSpace(parkArray, ptrs[1]);
        exit(EXIT_FAILURE);
    }

    printParks(parkArray, ptrs[0], argc == 3);

    freeSpace(parkArray, ptrs[1]);
}

// return false if there is no exit for any of the cars
bool isSolvable(Carpark* carpark) {
    Car* cars = carpark->cars;

    for (int i = 0; i < carpark->carInitNum; i++) {
        if (cars[i].orient) {
            if (carpark->board[0][cars[i].startX] != ROAD && carpark->board[carpark->height - 1][cars[i].startX] != ROAD) {
                return false;
            }
        } else {
            if (carpark->board[cars[i].startY][0] != ROAD && carpark->board[cars[i].startY][carpark->width - 1] != ROAD) {
                return false;
            }
        } 
    }

    return true;
}

// read file and get the original car park
Carpark* getRoot(const char* fileName) {
    FILE* fp = NULL;
    fp = fopen(fileName, "r");
    if (fp == NULL) {
        fprintf(stderr, "Fail to open file %s\n", fileName);
        exit(EXIT_FAILURE);
    }

    Carpark* carpark = NULL;
    carpark = (Carpark*)malloc(sizeof(Carpark));

    int height, width;
    fscanf(fp, "%dx%d ", &height, &width);
    carpark->height = height;
    carpark->width = width;
    
    carpark->board = getBoard(fp);
    // extra chars and weird chars cases
    if (hasWeirdChar(carpark) || hasExtraChar(carpark)) {
        if (hasWeirdChar(carpark)) {
            fprintf(stderr, "Weird chars exist in the car park!\n");
        } else {
            fprintf(stderr, "Extra chars exist in the car park!\n");
        }
        freeBoard(carpark->board);
        free(carpark);
        exit(EXIT_FAILURE);
    }
    
    carpark->carInitNum = getCarNum(carpark->board, height, width);
    // no car case
    if (carpark->carInitNum == 0) {
        fprintf(stderr, "No car in the car park!\n");
        freeBoard(carpark->board);
        free(carpark);
        exit(EXIT_FAILURE);
    }
    carpark->carCurrNum = carpark->carInitNum;
    getCars(carpark);

    carpark->parent = -1;

    return carpark;
}

// get dual pointer of char from the file
char** getBoard(FILE* fp) {
    char** bp = NULL;
    bp = (char**)malloc(sizeof(char*) * MAXLEN);
    for (int i = 0; i < MAXLEN; i++) {
        // we got two chars \r\n at the end of each line
        bp[i] = (char*)calloc(sizeof(char), MAXBUF);
        fgets(bp[i], MAXBUF, fp);
    }

    fclose(fp);
    return bp;
}

// return true if there are illegal chars in the board
bool hasWeirdChar(Carpark* carpark) {
    char** board = NULL;
    board = carpark->board;

    for (int i = 0; i < carpark->height; i++) {
        for (int j = 0; j < carpark->width; j++) {
            if (board[i][j] > 'Z' || board[i][j] < 'A') {
                if (board[i][j] != '#' && board[i][j] != '.') {
                    return true;
                }
            }
        }
    }

    return false;
}

// return true if there are extra chars outside the carpark area int the board
bool hasExtraChar(Carpark* carpark) {
    char** board = carpark->board;
    bool flag = false;

    for (int i = carpark->height; i < MAXLEN; i++) {
        for (int j = 0; j < MAXLEN; j++) {
            char c = board[i][j];
            if (c != '\r' && c != '\n' && c != '\0' && c != 0) {
                flag = true;
            }
        }
    }

    for (int i = 0; i < carpark->height; i++) {
        for (int j = carpark->width; j < MAXLEN; j++) {
            char c = board[i][j];
            if (c != '\r' && c != '\n' && c != '\0' && c != 0) {
                flag = true;
            }
        }
    }

    return flag;
}

// return the number of cars in the board
int getCarNum(char** board, int height, int width) {
    int count = 0;

    for (int i = 'A'; i < 'A' + MAXCARNUM; i++) {
        count += getCarNumHelper(board, height, width, i);
    }

    return count;
}

// return true when letter is in the board
bool getCarNumHelper(char** board, int height, int width, int i) {
    for (int m = 0; m < height; m++) {
        for (int n = 0; n < width; n++) {
            if (board[m][n] == i) {
                return true;
            }
        }
    }

    return false;
}

// get a car array from the board 
void getCars(Carpark* carpark) {
    char **board = carpark->board;
    int height = carpark->height;
    int width = carpark->width;

    Car* cars = NULL;
    cars = (Car*)malloc(sizeof(Car) * carpark->carInitNum);
    carpark->cars = cars;
    int ptr = 0;

    for (int i = 'A'; i < 'A' + MAXCARNUM; i++) {
        findStart(board, height, width, i, &ptr, cars);
    }

    if (!isCarsValid(board, height, width, cars, carpark->carInitNum)) {
        fprintf(stderr, "Invalid car shape!\n");
        freeCarpark(carpark);
        exit(EXIT_FAILURE);
    }
}

// find the starting point of a given letter
void findStart(char** board, int height, int width, char letter, int* ptr, Car* cars) {
    for (int m = 0; m < height; m++) {
        for (int n = 0; n < width; n++) {
            if (board[m][n] == letter) {
                cars[*ptr].letter = letter;
                cars[*ptr].startX = n;
                cars[*ptr].startY = m;
                cars[*ptr].state = true;
                (*ptr)++;
                return;
            }
        }
    }
}

/**
 * Calculate the amount of every letter, then traverse a line or a row starting at 
 * the starting point. If a consecutive group of a same letter with the exactive amount
 * can be found, then this car is valid.
 * Get end and orient
*/
bool isCarsValid(char** board, int height, int width, Car* cars, int carNum) {
    // generate two corresponding array mapping letters and their frequency
    // char letters[carNum];
    int letterNum[MAXCARNUM];
    for (int i = 0; i < carNum; i++) {
        // letters[i] = cars[i].letter;
        letterNum[i] = 0;
    }
    countLetter(board, height, width, cars, letterNum, carNum);

    // if one of the letter is invalid, return false
    return getCarEnd(board, height, width, cars, letterNum, carNum);
}

// get the frequency of letters 
void countLetter(char** board, int height, int width, Car* cars, int* letterNum, int carNum) {
    for (int m = 0; m < height; m++) {
        for (int n = 0; n < width; n++) {
            for (int i = 0; i < carNum; i++) {
                if (cars[i].letter == board[m][n]) {
                    letterNum[i]++;
                }
            } 
        }
    }
}

// get the end of cars, if failed, return false
bool getCarEnd(char** board, int height, int width, Car* cars, int* letterNum, int carNum) {
    for (int i = 0; i < carNum; i++) {
        int endX, endY;

        // vertical case
        if (cars[i].startY + 1 < height - 1 && board[cars[i].startY + 1][cars[i].startX] == cars[i].letter) {
            for (int z = 1; z < letterNum[i]; z++) {
                endY = cars[i].startY + z;
                if (endY < height - 1 && board[endY][cars[i].startX] != cars[i].letter) {
                    return false;
                } else if (endY >= height - 1){
                    return false;
                }
            }

            cars[i].endX = cars[i].startX;
            cars[i].endY = endY;
            cars[i].orient = true;

        // horizontal case
        } else if (cars[i].startX + 1 < width - 1 && board[cars[i].startY][cars[i].startX + 1] == cars[i].letter) {
            for (int z = 1; z < letterNum[i]; z++) {
                endX = cars[i].startX + z;
                if (endX < width - 1 && board[cars[i].startY][endX] != cars[i].letter) {
                    return false;
                } else if (endX >= width - 1){
                    return false;
                }
            }

            cars[i].endX = endX;
            cars[i].endY = cars[i].startY;
            cars[i].orient = false;

        // wrong case
        } else {
            return false;
        }
    }
    
    return true;
}

Carpark** getParkArray(Carpark* carpark) {
    Carpark** parkArray = NULL;
    parkArray = (Carpark**)malloc(sizeof(Carpark*) * MAXPARKNUM);
    parkArray[0] = carpark;

    return parkArray;
}


// /**
bool bestFisrt() {
    
}
//  */



// Breadth First Search
// return true if a soluntion found, store the value of ptr and count
bool bfs(Carpark** parkArray, int* ptrs) {
    // two pointers, one points to the head of queue, another to the fringe
    int ptr = 0, count = 1;
    Carpark* carpark = parkArray[ptr];
    Carpark* newPark = NULL;
    bool flag = false;

    // when ptr equals to count, there will be no solution
    while (carpark->carCurrNum != 0 && ptr < count) {

        for (int i = 0; i < carpark->carInitNum; i++) {
            // if the car is still in the park, try to move it
            if (carpark->cars[i].state == 1) {
                // vertical car, move upwards once and downwards once
                if (carpark->cars[i].orient) {

                    newPark = copyPark(carpark, ptr);
                    flag = moveUp(newPark, i) && !isExisted(parkArray, count, newPark);
                    if (flag) {
                        parkArray[count] = newPark; 
                        count++;
                    }
                    freeCopy(newPark, flag);

                    newPark = copyPark(carpark, ptr);
                    flag = moveDown(newPark, i) && !isExisted(parkArray, count, newPark);
                    if (flag) {
                        parkArray[count] = newPark;
                        count++;
                    }
                    freeCopy(newPark, flag);

                // horizontal car, move to the left once and to the right once
                } else {
                    newPark = copyPark(carpark, ptr);
                    flag = moveLeft(newPark, i) && !isExisted(parkArray, count, newPark);
                    if (flag) {
                        parkArray[count] = newPark; 
                        count++;
                    }
                    freeCopy(newPark, flag);

                    newPark = copyPark(carpark, ptr);
                    flag = moveRight(newPark, i) && !isExisted(parkArray, count, newPark);
                    if (flag) {
                        parkArray[count] = newPark;
                        count++;
                    }
                    freeCopy(newPark, flag);
                }
            }
        }

        ptr++;
        if (ptr < count) {
            carpark = parkArray[ptr];
        }
    }

    ptrs[0] = ptr;
    ptrs[1] = count;
    return carpark->carCurrNum == 0;
}

// copy information from the given car park
Carpark* copyPark(Carpark* carpark, int ptr) {
    Carpark* newPark = NULL;
    newPark = (Carpark*)malloc(sizeof(Carpark));

    newPark->height = carpark->height;
    newPark->width = carpark->width;
    char** board = NULL;
    board = (char**)malloc(sizeof(char*) * MAXLEN);
    for (int i = 0; i < MAXLEN; i++) {
        board[i] = (char*)malloc(sizeof(char) * MAXBUF);
    }
    for (int i = 0; i < MAXLEN; i++) {
        for (int j = 0; j < MAXBUF; j++) {
            board[i][j] = carpark->board[i][j]; 
        }
    }
    newPark->board = board;

    newPark->carInitNum = carpark->carInitNum;
    newPark->carCurrNum = carpark->carCurrNum;
    newPark->cars = (Car*)malloc(sizeof(Car) * newPark->carInitNum);
    for (int i = 0; i < newPark->carInitNum; i++) {
        newPark->cars[i].letter = carpark->cars[i].letter;
        newPark->cars[i].orient = carpark->cars[i].orient;
        newPark->cars[i].state = carpark->cars[i].state;
        newPark->cars[i].startX = carpark->cars[i].startX;
        newPark->cars[i].startY = carpark->cars[i].startY;
        newPark->cars[i].endX = carpark->cars[i].endX;
        newPark->cars[i].endY = carpark->cars[i].endY;
    }

    newPark->parent = ptr;

    return newPark;
}

// return true if a given carpark exists in parkArray
bool isExisted(Carpark** parkArray, int count, Carpark* carpark) {
    bool flag = false;

    for (int i = 0; i < count && !flag; i++) {
        flag = isExistedHelper(parkArray, i, carpark);
    }

    return flag;
}

bool isExistedHelper(Carpark** parkArray, int i, Carpark* carpark) {
    int height = carpark->height;
    int width = carpark->width;
    char** board = carpark->board;

    for (int m = 0; m < height; m++) {
        for (int n = 0; n < width; n++) {
            if (board[m][n] != parkArray[i]->board[m][n]) {
                return false;
            }
        }
    }

    return true;
}

// return true when moving a given car upward succesfully
bool moveUp(Carpark* carpark, int carIndex) {
    Car car = carpark->cars[carIndex];
    char** board = carpark->board;

    char next = board[car.startY - 1][car.startX];
    if (next == ROAD) {
        // when the next step is an exit, remove the car
        if (car.startY - 1 == 0) {
            removeCar(carpark, carIndex);
        } else {
            modifyBoard(carpark, car.letter, car.startX, car.startY - 1, car.endX, car.endY);
            modifyCar(carpark, carIndex, car.startX, car.startY - 1, car.endX, car.endY - 1);
        }
        return true;
    }

    return false;
}

// return true when moving a given car downward succesfully
bool moveDown(Carpark* carpark, int carIndex) {
    Car car = carpark->cars[carIndex];
    char** board = carpark->board;

    char next = board[car.endY + 1][car.startX];
    if (next == ROAD) {
        // when the next step is an exit, remove the car
        if (car.endY + 1 == carpark->height - 1) {
            removeCar(carpark, carIndex);
        } else {
            modifyBoard(carpark, car.letter, car.endX, car.endY + 1, car.startX, car.startY);
            modifyCar(carpark, carIndex, car.startX, car.startY + 1, car.endX, car.endY + 1);
        }
        return true;
    }

    return false;
}

// return true when moving a given car to the left succesfully
bool moveLeft(Carpark* carpark, int carIndex) {
    Car car = carpark->cars[carIndex];
    char** board = carpark->board;

    char next = board[car.startY][car.startX - 1];
    if (next == ROAD) {
        // when the next step is an exit, remove the car
        if (car.startX - 1 == 0) {
            removeCar(carpark, carIndex);
        } else {
            modifyBoard(carpark, car.letter, car.startX - 1, car.startY, car.endX, car.endY);
            modifyCar(carpark, carIndex, car.startX - 1, car.startY, car.endX - 1, car.endY);
        }
        return true;
    }

    return false;
}

// return true when moving a given car to the right succesfully
bool moveRight(Carpark* carpark, int carIndex) {
    Car car = carpark->cars[carIndex];
    char** board = carpark->board;

    char next = board[car.startY][car.endX + 1];
    if (next == ROAD) {
        // when the next step is an exit, remove the car
        if (car.endX + 1 == carpark->width - 1) {
            removeCar(carpark, carIndex);
        } else {
            modifyBoard(carpark, car.letter, car.endX + 1, car.endY, car.startX, car.startY);
            modifyCar(carpark, carIndex, car.startX + 1, car.startY, car.endX + 1, car.endY);
        }
        return true;
    }

    return false;
}

// modify the board at the given coordinates
void modifyBoard(Carpark* carpark, char letter, int toCarX, int toCarY, int toRoadX, int toRoadY) {
    carpark->board[toCarY][toCarX] = letter;
    carpark->board[toRoadY][toRoadX] = ROAD;
}

// modify the coordinates of a given car
void modifyCar(Carpark* carpark, int carIndex, int newStartX, int newStartY, int newEndX, int newEndY) {
    carpark->cars[carIndex].startX = newStartX;
    carpark->cars[carIndex].startY = newStartY;
    carpark->cars[carIndex].endX = newEndX;
    carpark->cars[carIndex].endY = newEndY;
}

// free heap space of carpark while flag = false 
void freeCopy(Carpark* newPark, bool flag) {
    if (flag) {
        return;
    }
    freeCarpark(newPark);
    newPark = NULL;
}

void removeCar(Carpark* carpark, int carIndex) {
    Car car = carpark->cars[carIndex];

    // delete the car on board
    if (car.orient) {
        for (int i = car.startY; i <= car.endY; i++) {
            carpark->board[i][car.startX] = ROAD;
        }
    } else {
        for (int i = car.startX; i <= car.endX; i++) {
            carpark->board[car.startY][i] = ROAD;
        }
    }

    // change the state of the car
    carpark->cars[carIndex].state = false;
    carpark->carCurrNum--;
}

// print all the steps of the solution
void printParks(Carpark** parkArray, int ptr, bool printSteps) {
    int count = 1;
    int tmpPtr = ptr;

    // count the number of all the pictures
    while (tmpPtr != 0) {
        tmpPtr = parkArray[tmpPtr]->parent;
        count++;
    }

    if (printSteps) {
        for (int i = count - 1; i >= 0; i--) {
            int tmpCount = i;
            tmpPtr = ptr;
            
            while (tmpCount != 0) {
                tmpPtr = parkArray[tmpPtr]->parent;
                tmpCount--;
            }
            printPark(parkArray[tmpPtr]);
            printf("\n");
        }
    }

    fprintf(stdout, "%d moves\n", count - 1);
}

// no need to add \n since every line in board ends with \r\n
void printPark(Carpark* carpark) {
    char** bp = carpark->board;
    for (int i = 0; i < carpark->height; i++) {
        fprintf(stdout, "%s", bp[i]);
    }
}

void freeSpace(Carpark** parkArray, int count) {
    // free every carpark
    for (int i = 0; i < count; i++) {
        freeCarpark(parkArray[i]);
    }

    // free carpark array
    free(parkArray);
    parkArray = NULL;
}

// free the space of a board allocated by malloc
void freeBoard(char** board) {
    for (int i = 0; i < MAXLEN; i++) {
        free(board[i]);
    }
    free(board);
}

// free the space of a carpark allocated by malloc
void freeCarpark(Carpark* carpark) {
    freeBoard(carpark->board);
    free(carpark->cars);
    free(carpark);
    carpark = NULL;
}

void test() {
    // initiate a new carpark object 
    Carpark* carpark = NULL;
    carpark = testGetCarpark();

    // tests for Initiation methods
    assert(carpark->board[3][1] == 'A');
    assert(carpark->board[5][5] == '#');
    assert(carpark->carCurrNum == 2);
    assert(carpark->cars[0].letter == 'A');
    assert(carpark->cars[0].state == 1);
    assert(carpark->cars[0].startX == 1);
    assert(carpark->cars[0].endX == 1);
    assert(carpark->cars[1].letter == 'B');
    assert(carpark->cars[1].orient == 0);
    assert(carpark->cars[1].startY == 1);
    assert(carpark->cars[1].endY == 1);
    assert(isSolvable(carpark));

    // tests for copyPark()
    Carpark* newPark = NULL;
    newPark = copyPark(carpark, 0);
    assert(newPark->parent == 0);
    assert(newPark != carpark);
    assert(newPark->width == 6);
    assert(newPark->carInitNum == 2);
    assert(newPark->board != carpark->board);
    assert(newPark->board[3][1] == 'A');
    assert(newPark->board[5][5] == '#');
    assert(newPark->cars != carpark->cars);
    assert(newPark->cars[0].letter == 'A');
    assert(newPark->cars[0].state == 1);
    assert(newPark->cars[0].startY == 2);
    assert(newPark->cars[1].letter == 'B');
    assert(newPark->cars[1].orient == 0);
    assert(newPark->cars[1].endX == 3);
    
    // tests for isExisted()
    Carpark** parkArray = NULL;
    parkArray = getParkArray(carpark);
    assert(isExisted(parkArray, 1, newPark));

    // tests for modifyBoard()
    modifyBoard(newPark, 'C', 4, 4, 1, 1);
    assert(newPark->board[4][4] == 'C');
    assert(newPark->board[1][1] == ROAD);
    modifyBoard(newPark, 'B', 1, 1, 4, 4);
    // tests for modifyCar()
    modifyCar(newPark, 1, 0, 1, 2, 1);
    assert(newPark->cars[1].startX == 0);
    assert(newPark->cars[1].endX == 2);
    modifyCar(newPark, 1, 1, 1, 3, 1);
    
    // tests for moveUp()
    assert(!moveUp(newPark, 0));
    // tests for moveRight()
    assert(moveRight(newPark, 1));
    assert(newPark->cars[1].startX == 2);
    assert(newPark->cars[1].endX == 4);
    assert(newPark->board[1][1] == ROAD);
    assert(newPark->board[1][4] == 'B');
    // tests for moveDown()
    assert(moveUp(newPark, 0));
    // tests for moveLeft()
    assert(!moveLeft(newPark, 1));
    assert(moveDown(newPark, 0));
    assert(moveLeft(newPark, 1));
    assert(newPark->board[1][1] == 'B');
    assert(newPark->cars[0].startY == 2);
    assert(newPark->cars[1].startX == 1);

    // tests for removeCar()
    removeCar(newPark, 1);
    for (int i = 1; i < 4; i++) {
        assert(newPark->board[1][i] == '.');
    }
    assert(newPark->carCurrNum == 1);
    assert(newPark->cars[1].state == 0);
    assert(moveUp(newPark, 0));
    assert(moveUp(newPark, 0));
    assert(newPark->carCurrNum == 0);
    assert(newPark->cars[0].state == 0);
    for (int i = 1; i < 4; i++) {
        assert(newPark->board[i][1] == '.');
    }
    assert(!isExisted(parkArray, 1, newPark));

    // tests for bfs()
    int ptrs[2];
    assert(bfs(parkArray, ptrs));

    freeSpace(parkArray, ptrs[1]);
    freeCarpark(newPark);
}

Carpark* testGetCarpark() {
    Carpark* carpark = NULL;
    carpark = (Carpark*)malloc(sizeof(Carpark));

    carpark->height = 6;
    carpark->width = 6;

    char** board = NULL;
    board = (char**)malloc(sizeof(char*) * MAXLEN);
    for (int i = 0; i < MAXLEN; i++) {
        board[i] = (char*)malloc(sizeof(char) * MAXBUF);
    }
    for (int i = 0; i < MAXLEN; i++) {
        if (i == 0) {
            strcpy(board[i], "#.####\r\n");
        } else if (i == 1) {
            strcpy(board[i], ".BBB.#\r\n");
        } else if (i == 2) {
            strcpy(board[i], "#A...#\r\n");
        } else if (i == 3) {
            strcpy(board[i], "#A...#\r\n");
        } else if (i == 4) {
            strcpy(board[i], "#A...#\r\n");
        } else if (i == 5) {
            strcpy(board[i], "######\r\n");
        }
    }
    carpark->board = board;

    carpark->carInitNum = getCarNum(carpark->board, carpark->height, carpark->width);
    carpark->carCurrNum = carpark->carInitNum;
    getCars(carpark);

    carpark->parent = -1;

    return carpark;
}

#include "carpark.h"

int main(int argc, const char* argv[]) {
    test();

    show(argc, argv);
    
    return 0;
}


/**
 * Print all the steps of the solution when the second parameter is -show
 * Otherwise, only print the amount of steps
 */
void show(int argc, const char* argv[]) {
    Carpark carpark;
    static Carpark parkArray[MAXPARKNUM];
    int ptr = 0;

    if (argc == 2) {
        getRoot(argv[1], &carpark);
    } else if (argc == 3 && !strcmp(argv[1], "-show")){
        getRoot(argv[2], &carpark);
    } else {
        fprintf(stderr, "Illegal input for the program!\n");
        exit(EXIT_FAILURE);
    }

    parkArray[0] = carpark;

    if (!bfs(parkArray, &ptr)) {
        fprintf(stderr, "No Solution?\n");
        exit(EXIT_FAILURE);
    }
    printParks(parkArray, ptr, argc == 3);
}

// read file and get the original carpark object
void getRoot(const char* fileName, Carpark* carpark) {
    FILE* fp = NULL;
    fp = fopen(fileName, "r");
    if (fp == NULL) {
        fprintf(stderr, "Fail to open file %s\n", fileName);
        exit(EXIT_FAILURE);
    }

    int height, width;
    fscanf(fp, "%dx%d ", &height, &width);
    carpark->height = height;
    carpark->width = width;
    
    getBoard(fp, carpark);
    if (hasWeirdChar(carpark) || hasExtraChar(carpark)) {
        if (hasExtraChar(carpark)) {
            fprintf(stderr, "Extra chars exist in the car park!\n");
        } else {
            fprintf(stderr, "Weird chars exist in the car park!\n");
        }
        exit(EXIT_FAILURE);
    }
    
    carpark->carInitNum = getCarNum(carpark);
    carpark->carCurrNum = carpark->carInitNum;
    getCars(carpark);

    carpark->parent = -1;
}

// get a 2-dimention char array storing data of the file
void getBoard(FILE* fp, Carpark* carpark) {
    // fill the array with 0 when initiating it
    for (int m = 0; m < MAXLEN; m++) {
        for (int n = 0; n < MAXBUF; n++) {
            carpark->board[m][n] = 0;
        }
    }
    
    for (int i = 0; i < MAXLEN; i++) {
        fgets(carpark->board[i], MAXBUF, fp);
    }
}

// return true if there are illegal chars in the board
bool hasWeirdChar(Carpark* carpark) {
    for (int i = 0; i < carpark->height; i++) {
        for (int j = 0; j < carpark->width; j++) {
            if (carpark->board[i][j] > 'Z' || carpark->board[i][j] < 'A') {
                if (carpark->board[i][j] != '#' && carpark->board[i][j] != '.') {
                    return true;
                }
            }
        }
    }

    return false;
}

// return true if there are extra chars outside the carpark area int the board
bool hasExtraChar(Carpark* carpark) {
    bool flag = false;

    for (int i = carpark->height; i < MAXLEN; i++) {
        for (int j = 0; j < MAXLEN; j++) {
            char c = carpark->board[i][j];
            if (c != '\r' && c != '\n' && c != '\0' && c != 0) {
                flag = true;
            }
        }
    }

    for (int i = 0; i < carpark->height; i++) {
        for (int j = carpark->width; j < MAXLEN; j++) {
            char c = carpark->board[i][j];
            if (c != '\r' && c != '\n' && c != '\0' && c != 0) {
                flag = true;
            }
        }
    }

    return flag;
}

// get the number of cars in the board
int getCarNum(Carpark* carpark) {
    int count = 0;

    // general case
    for (int i = 'A'; i < 'A' + MAXCARNUM; i++) {
        count += getCarNumHelper(carpark, i);
    }

    // no car
    if (count == 0) {
        fprintf(stderr, "No car in the car park!\n");
        exit(EXIT_FAILURE);
    }

    return count;
}

// return true if a letter is in the board
bool getCarNumHelper(Carpark* carpark, int i) {
    for (int m = 0; m < carpark->height; m++) {
        for (int n = 0; n < carpark->width; n++) {
            if (carpark->board[m][n] == i) {
                return true;
            }
        }
    }

    return false;
}

// get the car array from the board 
void getCars(Carpark* carpark) {
    int ptr = 0;

    for (int i = 'A'; i < 'A' + MAXCARNUM; i++) {
        findStart(carpark, i, &ptr);
    }

    if (!findEnd(carpark)) {
        fprintf(stderr, "Invalid car shape!\n");
        exit(EXIT_FAILURE);
    }

}

// the point where a letter appears for the first time is the head of a car
void findStart(Carpark* carpark, char letter, int* ptr) {
    for (int m = 0; m < carpark->height; m++) {
        for (int n = 0; n < carpark->width; n++) {
            if (carpark->board[m][n] == letter) {
                carpark->cars[*ptr].letter = letter;
                carpark->cars[*ptr].startX = n;
                carpark->cars[*ptr].startY = m;
                carpark->cars[*ptr].state = true;
                (*ptr)++;
                return;
            }
        }
    }
}

/**
 * Calculate the amount of every letter, then traverse a line or a row from the starting point.
 * If a consecutive line of the same letter can be found, then this car is valid.
 * if one of the letter is invalid, return false.
*/
bool findEnd(Carpark* carpark) {
    // an array letterNum storing frequency of letters
    int letterNum[MAXCARNUM];
    for (int i = 0; i < carpark->carInitNum; i++) {
        letterNum[i] = 0;
    }
    countLetter(carpark, letterNum);

    return getCarEnd(carpark, letterNum);
}

// get the frequency of letters 
void countLetter(Carpark* carpark, int* letterNum) {
    for (int m = 0; m < carpark->height; m++) {
        for (int n = 0; n < carpark->width; n++) {
            for (int i = 0; i < carpark->carInitNum; i++) {
                if (carpark->cars[i].letter == carpark->board[m][n]) {
                    letterNum[i]++;
                }
            } 
        }
    }
}

// Find the end of cars. Return false if fail.
bool getCarEnd(Carpark* carpark, int* letterNum) {
    int height = carpark->height;
    int width = carpark->width;
    int carNum = carpark->carInitNum;
    Car* cars = carpark->cars;

    for (int i = 0; i < carNum; i++) {
        int endX, endY;

        // vertical case
        if (cars[i].startY + 1 < height - 1 && carpark->board[cars[i].startY + 1][cars[i].startX] == cars[i].letter) {
            for (int z = 1; z < letterNum[i]; z++) {
                endY = cars[i].startY + z;
                if (endY < height - 1 && carpark->board[endY][cars[i].startX] != cars[i].letter) {
                    return false;
                } else if (endY >= height - 1){
                    return false;
                }
            }

            cars[i].endX = cars[i].startX;
            cars[i].endY = endY;
            cars[i].orient = true;

        // horizontal case
        } else if (cars[i].startX + 1 < width - 1 && carpark->board[cars[i].startY][cars[i].startX + 1] == cars[i].letter) {
            for (int z = 1; z < letterNum[i]; z++) {
                endX = cars[i].startX + z;
                if (endX < width - 1 && carpark->board[cars[i].startY][endX] != cars[i].letter) {
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

// Breadth First Search
// return true if a soluntion found, store pointer pointing to final carpark
bool bfs(Carpark* parkArray, int* ptr) {
    int count = 1;
    bool flag = false;

    // ptr equaling to count means no solution
    while (*ptr < count && parkArray[*ptr].carCurrNum != 0) {
        for (int i = 0; i < parkArray[*ptr].carInitNum; i++) {
            // if the car is still in the park, try to move it
            if (parkArray[*ptr].cars[i].state == 1) {
                // vertical car, move upwards once and downwards once
                if (parkArray[*ptr].cars[i].orient) {
                    copyPark(parkArray, *ptr, count);
                    flag = moveUp(parkArray, count, i) && !isExisted(parkArray, count);
                    count = flag ? count + 1 : count;

                    copyPark(parkArray, *ptr, count);
                    flag = moveDown(parkArray, count, i) && !isExisted(parkArray, count);
                    count = flag ? count + 1 : count; 

                // horizontal car, move to the left once and to the right once
                } else {
                    copyPark(parkArray, *ptr, count);
                    flag = moveLeft(parkArray, count, i) && !isExisted(parkArray, count);
                    count = flag ? count + 1 : count;

                    copyPark(parkArray, *ptr, count);
                    flag = moveRight(parkArray, count, i) && !isExisted(parkArray, count);
                    count = flag ? count + 1 : count;
                }
            }
        }

        (*ptr)++;
    }

    return parkArray[*ptr].carCurrNum == 0;
}

// copy properties from parent node into the fringe of the carpark array
void copyPark(Carpark* parkArray, int ptr, int count) {
    parkArray[count].height = parkArray[ptr].height;
    parkArray[count].width = parkArray[ptr].width;
    for (int i = 0; i < MAXLEN; i++) {
        for (int j = 0; j < MAXBUF; j++) {
            parkArray[count].board[i][j] = parkArray[ptr].board[i][j];
        }
    }

    parkArray[count].carInitNum = parkArray[ptr].carInitNum;
    parkArray[count].carCurrNum = parkArray[ptr].carCurrNum;
    for (int i = 0; i < parkArray[count].carInitNum; i++) {
        parkArray[count].cars[i].letter = parkArray[ptr].cars[i].letter;
        parkArray[count].cars[i].orient = parkArray[ptr].cars[i].orient;
        parkArray[count].cars[i].state = parkArray[ptr].cars[i].state;
        parkArray[count].cars[i].startX = parkArray[ptr].cars[i].startX;
        parkArray[count].cars[i].startY = parkArray[ptr].cars[i].startY;
        parkArray[count].cars[i].endX = parkArray[ptr].cars[i].endX;
        parkArray[count].cars[i].endY = parkArray[ptr].cars[i].endY;
    }

    parkArray[count].parent = ptr;
}

// return true if a given carpark exists in parkArray
bool isExisted(Carpark* parkArray, int count) {
    bool flag = false;

    for (int i = 0; i < count && !flag; i++) {
        flag = isExistedHelper(parkArray[i], parkArray[count]);
    }

    return flag;
}

bool isExistedHelper(Carpark carpark, Carpark newPark) {
    int height = carpark.height;
    int width = carpark.width;

    for (int m = 0; m < height; m++) {
        for (int n = 0; n < width; n++) {
            if (carpark.board[m][n] != newPark.board[m][n]) {
                return false;
            }
        }
    }

    return true;
}

// return true when moving a given car upward succesfully
bool moveUp(Carpark* parkArray, int count, int carIndex) {
    Car car = parkArray[count].cars[carIndex];

    char next = parkArray[count].board[car.startY - 1][car.startX];
    if (next == ROAD) {
        // when the next step is an exit, remove the car
        if (car.startY - 1 == 0) {
            removeCar(parkArray, count, carIndex);
        } else {
            modifyBoard(parkArray, count, car.letter, car.startX, car.startY - 1, car.endX, car.endY);
            modifyCar(parkArray, count, carIndex, car.startX, car.startY - 1, car.endX, car.endY - 1);
        }
        return true;
    }

    return false;
}

// return true when moving a given car downward succesfully
bool moveDown(Carpark* parkArray, int count, int carIndex) {
    Car car = parkArray[count].cars[carIndex];  
    
    char next = parkArray[count].board[car.endY + 1][car.startX];
    if (next == ROAD) {
        // when the next step is an exit, remove the car
        if (car.endY + 1 == parkArray[count].height - 1) {
            removeCar(parkArray, count, carIndex);
        } else {
            modifyBoard(parkArray, count, car.letter, car.endX, car.endY + 1, car.startX, car.startY);
            modifyCar(parkArray, count, carIndex, car.startX, car.startY + 1, car.endX, car.endY + 1);
        }
        return true;
    }

    return false;
}

// return true when moving a given car to the left succesfully
bool moveLeft(Carpark* parkArray, int count, int carIndex) {
    Car car = parkArray[count].cars[carIndex];

    char next = parkArray[count].board[car.startY][car.startX - 1];
    if (next == ROAD) {
        // when the next step is an exit, remove the car
        if (car.startX - 1 == 0) {
            removeCar(parkArray, count, carIndex);
        } else {
            modifyBoard(parkArray, count, car.letter, car.startX - 1, car.startY, car.endX, car.endY);
            modifyCar(parkArray, count, carIndex, car.startX - 1, car.startY, car.endX - 1, car.endY);
        }
        return true;
    }

    return false;
}

// return true when moving a given car to the right succesfully
bool moveRight(Carpark* parkArray, int count, int carIndex) {
    Car car = parkArray[count].cars[carIndex];

    char next = parkArray[count].board[car.startY][car.endX + 1];
    if (next == ROAD) {
        // when the next step is an exit, remove the car
        if (car.endX + 1 == parkArray[count].width - 1) {
            removeCar(parkArray, count, carIndex);
        } else {
            modifyBoard(parkArray, count, car.letter, car.endX + 1, car.endY, car.startX, car.startY);
            modifyCar(parkArray, count, carIndex, car.startX + 1, car.startY, car.endX + 1, car.endY);
        }
        return true;
    }

    return false;
}

void modifyBoard(Carpark* parkArray, int count, char letter, int toCarX, int toCarY, int toRoadX, int toRoadY) {
    parkArray[count].board[toCarY][toCarX] = letter;
    parkArray[count].board[toRoadY][toRoadX] = ROAD;
}

void modifyCar(Carpark* parkArray, int count, int carIndex, int newStartX, int newStartY, int newEndX, int newEndY) {
    parkArray[count].cars[carIndex].startX = newStartX;
    parkArray[count].cars[carIndex].startY = newStartY;
    parkArray[count].cars[carIndex].endX = newEndX;
    parkArray[count].cars[carIndex].endY = newEndY;
}

// remove a car from a car park
void removeCar(Carpark* parkArray, int count, int carIndex) {
    Car car = parkArray[count].cars[carIndex];

    // delete the car from the board
    if (car.orient) {
        for (int i = car.startY; i <= car.endY; i++) {
            parkArray[count].board[i][car.startX] = ROAD;
        }
    } else {
        for (int i = car.startX; i <= car.endX; i++) {
            parkArray[count].board[car.startY][i] = ROAD;
        }
    }

    // change the state of the car
    parkArray[count].cars[carIndex].state = false;
    parkArray[count].carCurrNum = parkArray[count].carCurrNum - 1;
}

// print all the steps of the solution
void printParks(Carpark* parkArray, int ptr, bool printSteps) {
    int count = 1;
    int tmpPtr = ptr;

    // count the number of all the pictures
    while (tmpPtr != 0) {
        tmpPtr = parkArray[tmpPtr].parent;
        count++;
    }

    if (printSteps) {
        for (int i = count - 1; i >= 0; i--) {
            int tmpCount = i;
            tmpPtr = ptr;
            
            while (tmpCount != 0) {
                tmpPtr = parkArray[tmpPtr].parent;
                tmpCount--;
            }
            printPark(parkArray[tmpPtr]);
            printf("\n");
        }
    }

    fprintf(stdout, "%d moves\n", count - 1);
}

void printPark(Carpark carpark) {
    for (int i = 0; i < carpark.height; i++) {
        for (int j = 0; j < carpark.width; j++) {
            fprintf(stdout, "%c", carpark.board[i][j]);
        }
        fprintf(stdout, "\n");
    }
}

void test() {
    // initiate a new carpark object 
    Carpark carpark;
    static Carpark parkArray[20000];

    carpark.height = 6;
    carpark.width = 6;

    for (int i = 0; i < MAXLEN; i++) {
        if (i == 0) {
            strcpy(carpark.board[i], "#.####\r\n");
        } else if (i == 1) {
            strcpy(carpark.board[i], ".BBB.#\r\n");
        } else if (i == 2) {
            strcpy(carpark.board[i], "#A...#\r\n");
        } else if (i == 3) {
            strcpy(carpark.board[i], "#A...#\r\n");
        } else if (i == 4) {
            strcpy(carpark.board[i], "#A...#\r\n");
        } else if (i == 5) {
            strcpy(carpark.board[i], "######\r\n");
        }
    }
    

    carpark.carInitNum = getCarNum(&carpark);
    carpark.carCurrNum = carpark.carInitNum;
    getCars(&carpark);
    carpark.parent = -1;

    // tests for Initiation methods
    assert(carpark.board[3][1] == 'A');
    assert(carpark.board[5][5] == '#');
    assert(carpark.carCurrNum == 2);
    assert(carpark.cars[0].letter == 'A');
    assert(carpark.cars[0].state == 1);
    assert(carpark.cars[0].startX == 1);
    assert(carpark.cars[0].endX == 1);
    assert(carpark.cars[1].letter == 'B');
    assert(carpark.cars[1].orient == 0);
    assert(carpark.cars[1].startY == 1);
    assert(carpark.cars[1].endY == 1);

    // tests for copyPark()
    parkArray[0] = carpark;
    copyPark(parkArray, 0, 1);
    assert(parkArray[1].parent == 0);
    assert(parkArray[1].width == 6);
    assert(parkArray[1].carInitNum == 2);
    assert(parkArray[1].board[3][1] == 'A');
    assert(parkArray[1].board[1][1] == 'B');
    assert(parkArray[1].board[5][5] == '#');
    assert(parkArray[1].board[4][4] == '.');
    assert(parkArray[1].cars[0].letter == 'A');
    assert(parkArray[1].cars[0].state == 1);
    assert(parkArray[1].cars[0].startY == 2);
    assert(parkArray[1].cars[1].letter == 'B');
    assert(parkArray[1].cars[1].orient == 0);
    assert(parkArray[1].cars[1].endX == 3);
    
    // tests for isExisted()
    assert(isExisted(parkArray, 1));

    // tests for modifyBoard()
    modifyBoard(parkArray, 1, 'C', 4, 4, 1, 1);
    assert(parkArray[1].board[4][4] == 'C');
    assert(parkArray[1].board[1][1] == ROAD);
    modifyBoard(parkArray, 1, 'B', 1, 1, 4, 4);

    // tests for modifyCar()
    modifyCar(parkArray, 1, 1, 0, 1, 2, 1);
    assert(parkArray[1].cars[1].startX == 0);
    assert(parkArray[1].cars[1].endX == 2);
    modifyCar(parkArray, 1, 1, 1, 1, 3, 1);
    
    // tests for moveUp()
    assert(!moveUp(parkArray, 1, 0));
    // tests for moveRight()
    assert(moveRight(parkArray, 1, 1));
    assert(parkArray[1].cars[1].startX == 2);
    assert(parkArray[1].cars[1].endX == 4);
    assert(parkArray[1].board[1][1] == ROAD);
    assert(parkArray[1].board[1][4] == 'B');
    // tests for moveDown()
    assert(moveUp(parkArray, 1, 0));
    // tests for moveLeft()
    assert(!moveLeft(parkArray, 1, 1));
    assert(moveDown(parkArray, 1, 0));
    assert(moveLeft(parkArray, 1, 1));
    assert(parkArray[1].board[1][1] == 'B');
    assert(parkArray[1].cars[0].startY == 2);
    assert(parkArray[1].cars[1].startX == 1);

    // tests for removeCar()
    removeCar(parkArray, 1, 1);
    for (int i = 1; i < 4; i++) {
        assert(parkArray[1].board[1][i] == '.');
    }
    assert(parkArray[1].carCurrNum == 1);
    assert(parkArray[1].cars[1].state == 0);
    assert(moveUp(parkArray, 1, 0));
    assert(moveUp(parkArray, 1, 0));
    assert(parkArray[1].carCurrNum == 0);
    assert(parkArray[1].cars[0].state == 0);
    for (int i = 1; i < 4; i++) {
        assert(parkArray[1].board[i][1] == '.');
    }
    assert(!isExisted(parkArray, 1));

    // tests for bfs()
    parkArray[0] = carpark;
    int ptr = 0;
    assert(bfs(parkArray, &ptr));
}

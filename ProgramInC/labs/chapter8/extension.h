#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <stdbool.h>
#include <string.h>

#define MAXCARNUM 26
#define MAXPARKNUM 30000
#define MAXLEN 20
#define MAXBUF 23
#define ROAD '.'

/** 
 * @brief coordinate system with origin at the upper left corner
 * (2, 5) means 3rd column and 6th row
 * start: the upper left corner of cars
 * end: the lower right corner of cars
 * orient: true if vertical, false if horizontal
 * state: true if car is in the car park, otherwise false
 */
typedef struct Car {
    char letter;
    bool orient;
    bool state;

    int startX;
    int startY;
    int endX;
    int endY;
} Car;

/**
 * visited: mark as true if this car park has been vsited
 * cars: numbered depending on alphabet
 * parent: the index of parent node in the array
 * board: pointer pointing to an array of char row
 */
typedef struct Carpark {
    int height;
    int width;
    char** board;

    int carInitNum;
    int carCurrNum;
    Car* cars;

    int parent;
} Carpark;

void show(int argc, const char* argv[]);
bool isSolvable(Carpark* carpark);
Carpark* getRoot(const char* fileName);
char** getBoard(FILE* fp);
bool hasWeirdChar(Carpark* carpark);
bool hasExtraChar(Carpark* carpark);
int getCarNum(char** board, int height, int width);
bool getCarNumHelper(char** board, int height, int width, int i);
void getCars(Carpark* carpark);
void findStart(char** board, int height, int width, char letter, int* ptr, Car* cars);
bool isCarsValid(char** board, int height, int width, Car* cars, int carNum);
void countLetter(char** board, int height, int width, Car* cars, int* letterNum, int carNum);
bool getCarEnd(char** board, int height, int width, Car* cars, int* letterNum, int carNum);
Carpark** getParkArray(Carpark* carpark);
bool bfs(Carpark** parkArray, int* ptrs);
Carpark* copyPark(Carpark* carpark, int ptr);
bool isExisted(Carpark** parkArray, int count, Carpark* carpark);
bool isExistedHelper(Carpark** parkArray, int i, Carpark* carpark);
bool moveUp(Carpark* carpark, int carIndex);
bool moveDown(Carpark* carpark, int carIndex);
bool moveLeft(Carpark* carpark, int carIndex);
bool moveRight(Carpark* carpark, int carIndex);
void modifyBoard(Carpark* carpark, char letter, int toCarX, int toCarY, int toRoadX, int toRoadY);
void modifyCar(Carpark* carpark, int carIndex, int newStartX, int newStartY, int newEndX, int newEndY);
void freeCopy(Carpark* newPark, bool flag);
void removeCar(Carpark* carpark, int carIndex);
void printParks(Carpark** parkArray, int ptr, bool printSteps);
void printPark(Carpark* carpark);
void freeSpace(Carpark** parkArray, int count);
void freeBoard(char** board);
void freeCarpark(Carpark* carpark);
void test();
Carpark* testGetCarpark();

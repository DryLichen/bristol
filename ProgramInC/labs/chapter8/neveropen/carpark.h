#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <stdbool.h>
#include <string.h>

#define MAXCARNUM 26
#define MAXPARKNUM 30000
#define MAXLEN 20
#define MAXBUF 32
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
    char board[MAXLEN][MAXBUF];

    int carInitNum;
    int carCurrNum;
    Car cars[MAXCARNUM];

    int parent;
} Carpark;

void show(int argc, const char* argv[]);
void getRoot(const char* fileName, Carpark* carpark);
void getBoard(FILE* fp, Carpark* carpark);
bool hasWeirdChar(Carpark* carpark);
bool hasExtraChar(Carpark* carpark);
int getCarNum(Carpark* carpark);
bool getCarNumHelper(Carpark* carpark, int i);
void getCars(Carpark* carpark);
void findStart(Carpark* carpark, char letter, int* ptr);
bool findEnd(Carpark* carpark);
void countLetter(Carpark* carpark, int* letterNum);
bool getCarEnd(Carpark* carpark, int* letterNum);
bool bfs(Carpark* parkArray, int* ptrs);
void copyPark(Carpark* parkArray, int ptr, int count);
bool isExisted(Carpark* parkArray, int count);
bool isExistedHelper(Carpark carpark, Carpark newPark);
bool moveUp(Carpark* parkArray, int count, int carIndex);
bool moveDown(Carpark* parkArray, int count, int carIndex);
bool moveLeft(Carpark* parkArray, int count, int carIndex);
bool moveRight(Carpark* parkArray, int count, int carIndex);
void modifyBoard(Carpark* parkArray, int count, char letter, int toCarX, int toCarY, int toRoadX, int toRoadY);
void modifyCar(Carpark* parkArray, int count, int carIndex, int newStartX, int newStartY, int newEndX, int newEndY);
void removeCar(Carpark* parkArray, int count, int carIndex);
void printParks(Carpark* parkArray, int ptr, bool printSteps);
void printPark(Carpark carpark);
void test();

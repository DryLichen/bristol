#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>

#define MAX 20

struct Tile { 
    char item;
    bool up;
    bool down;
    bool left;
    bool right;
};
typedef struct Tile Tile;

struct Board {
    Tile tiles[MAX][MAX];
    int width;
    int height;
    int entrenceX;
    int entrenceY;
};
typedef struct Board Board;

bool hasRoutes();
void test();
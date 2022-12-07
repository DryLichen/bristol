#include "maze.h"

int main(int argc, char const *argv[]) {
    if (argc != 2) {
        exit(EXIT_FAILURE);
    }
    strToBoard(argv[1]);

    return 0;
}

// convert a maze string to maze board
void strToBoard(const char *fileName) {
    FILE *fp = fopen(fileName, "r");
    if (fp == NULL) {
        fprintf("Can't read file: %s", fileName);
        exit(EXIT_FAILURE);
    }

    Board *board = (Board *)malloc(sizeof(Board));
    fscanf(fp, "%d", board->width);
    fscanf(fp, "%d", board->height);

    // read one sentence in a buffer
    char buffer[MAX + 1];
    for (int i = 0; i < board->height; i++) {
        fgets(buffer, MAX + 1, fp);
        for (int j = 0; j < board->width; j++) {
            board->tiles[i][j].item = *(buffer + j);
        }
    }

    getEntrance(board);

    fclose(fp);
}

// get the coordinates of the entrance
void getEntrance(Board *board) {
    // first, search from the top
    for (int i = 0; i < board->width; i++) {
        if (board->tiles[0][i].item == ' ') {
            board->entrenceX = i;
            board->entrenceY = 0;
            return;
        }
    }

    // not found, search from the left
    for (int i = 0; i < board->width; i++) {
        if (board->tiles[i][0].item == ' ') {
            board->entrenceX = 0;
            board->entrenceY = i;
            return;
        }
    }

    exit(EXIT_FAILURE);
}


bool hasRoutes(Board *board) {
    
}

void freeSpace(Board *board) {
    free(board);
}

void test() {

}

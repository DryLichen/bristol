#include <stdlib.h>
#include <time.h>

#define N 20
#define limit 10

int main(int argc, char const *argv[]) {
    int board[N][N];

    fillBoard(board);
    return 0;
}


void fillBoard(int* board) {
    srand(time(NULL));
    int r = rand();

    for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j++) {
            board[i][j] = r % limit;
        }
    }
}

void mutateBoard(int* board) {
    for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j++) {
            board[i][j] = r % limit;
        }
    }
}

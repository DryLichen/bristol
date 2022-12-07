/* Pseudo Linear Congruent Generator */

#include <stdio.h>
#include <stdlib.h>
#define A 7
#define C 5
#define M 11

int main() {
    int flag_num;
    int count = 0;
    int seed = 0;

    seed = (A * seed + C) % M;
    flag_num = seed;
    while (seed != flag_num || count == 0) {
        seed = (A * seed + C) % M;
        count++;
    }
    printf("the period of the LCG: %d\n", count);

    return 0;
}
#include <math.h>
#include <stdbool.h>
#include <stdlib.h>
#include <stdio.h>

#define e 2.71828182818281849364

int * get_denominator(double num);

int main(int argc, char const *argv[]) {
    int * pair = get_denominator(e);
    printf("%i/%i = %lf\n", pair[0], pair[1], e);
    free(pair);
    
    return 0;
}

// find the number pair and put them in an array
int * get_denominator(double num) {
    int * pair_num = (int *)malloc(sizeof(int) * 2);
    double diff;
    double last_diff = 100;

    for (size_t i = 1; i < 100000; i++) {
        for (size_t j = floor(num * i) - 1; j <= ceil(num * i) + 1; j++) {
            diff = pow(((double)j / i - e), 2);
            // reassign new values to numerator and denominator
            if (diff < last_diff) {
                last_diff = diff;
                pair_num[0] = j;
                pair_num[1] = i;
            }
        // printf("i: %d\n j: %d\n", i, j);
        }
    }

    return pair_num;
}
#include <math.h>
#include <time.h>
#include <stdlib.h>
#include <stdio.h>

double get_pie();

int main(int argc, char const *argv[]) {
    double p = get_pie();
    printf("simulation gets pie valued as:%lf\n", p);
    
    return 0;
}

double get_pie() {
    double pie;
    double square_count = 10000000, circle_count = 0;
    srand((unsigned)time(NULL));

    for (size_t i = 0; i < square_count; i++) {
        int x = rand() % 11;
        int y = rand() % 11;
        if (pow(x, 2) + pow(y, 2) <= 100) {
            circle_count++;
        }
    }

    pie = 4 * circle_count / square_count;
    return pie;
}
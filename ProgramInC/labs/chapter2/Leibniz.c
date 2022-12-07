#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#define PIE 3.14159265

int main(int argc, char const *argv[]) {
    int count = get_pie_count();
    printf("%d iterations are needed: \n", count);
    
    return 0;
}

int get_pie_count() {
    int count = 0;
    double fraction, divisor = 1;
    double pie = 0.0;

    while (1) {
        fraction = pow(-1, count) * 4 / divisor;
        pie += fraction;
        divisor += 2;
        count++;

        if (pie == PIE) {
            break;
        }
    }
    
    return count;
}

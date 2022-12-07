/* Triangle Number */
#include <stdio.h>
#include <stdlib.h>

int main() {
    int sum = 0;

    for (size_t i = 1; i <= 100; i++) {
        sum += i;
        printf("%d\t", sum);
    }
    return 0;
}

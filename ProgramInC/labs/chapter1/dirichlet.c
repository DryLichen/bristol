#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>

double get_three_fraction();

int main(int argc, char const *argv[]) {
    double fraction = get_three_fraction();

    printf("The fraction of prime numbers ending with 3 is: %f\n", fraction);
    
    return 0;
}

double get_three_fraction() {
    int prime_num = 1;
    double count = 0;

    for (size_t i = 0; i < 10000; i++) {
        bool flag = true;

        while (flag) {
            prime_num++;
            size_t j = 2;

            for ( ; j < prime_num; j++) {
                if (prime_num % j == 0) {
                    break;
                }
            }

            if (j == prime_num) {
                // whether the end is 3
                if (prime_num % 10 == 3) {
                    count++;
                }
                flag = false;
            }
        }
        printf("%d\n", i);
    }

    return count / 10000;
}
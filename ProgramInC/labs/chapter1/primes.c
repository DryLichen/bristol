#include <stdio.h>
#include <stdbool.h>

int get_prime(int position);

int main() {
    int position;
    printf("Please enter a number: ");
    scanf("%d", &position);

    int prime_num = get_prime(position);
    printf("\nThe %dth prime number is: %d\n", position, prime_num);

    return 0;
}

int get_prime(int position) {
    int prime_num = 0;

    // loop until get the nth item
    for (size_t i = 0; i < position; i++) {
        bool flag = true;   

        while (flag) {
            prime_num++;
            size_t j = 2;

            // end for loop when finding a factor
            for ( ; j < prime_num; j++) {
                if (prime_num % j == 0) {
                    break;
                }
            }

            // end while loop when getting a prime number
            if (j == prime_num) {
                flag = false;
            }
        }

    }
    
    return prime_num;
}
#include <stdlib.h> 
#include <stdio.h>

long longest();
long largest();

int main() {
    long longest_initial = longest();
    long largest_initial = largest();
    printf("The initial number produces the longest sequence is: %d\n", longest_initial);
    printf("The initial number produces the largest number is: %d\n", largest_initial);

    return 0;
}

long longest() {
    long long_num = 0;
    int long_count = 0;
    for (size_t i = 2; i < 10000000; i++) {
        long long num = i;
        int count = 1;

        while (num != 1) {
            if (num % 2 == 0) {
                num = num / 2;
            } else {
                num = 3 * num + 1;
            }

            count++;
        }

        if (long_count < count) {
            long_count = count;
            long_num = i;
        }
    }
    
    return long_num;                                                                                        
}

long largest() {
    long starter = 0, flag = 0;
    for (size_t i = 2; i < 10000000; i++) {
        long num = i, larg_num = 0;

        while (num != 1) {
            if (num % 2 == 0) {
                num /= 2;
            } else {
                num = 3 * num + 1;
            }

            // store the largest number
            if (larg_num < num) {
                larg_num = num;
            }
        }
        
        if (flag < larg_num) {
            flag = larg_num;
            starter = i;
        }
    }
    
    return starter;
}
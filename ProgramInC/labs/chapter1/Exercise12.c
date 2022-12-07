/**
 * 1.12 Cash Machine (ATM)
 */

#include <stdio.h>

int main () {
    int amount;

    while (1) {
        printf("How much money would you like ? ");
        scanf("%d", &amount);
        if (amount % 20 == 0) {
            printf("OK , dispensing ...\n");
            return 0;
        }

        int bottom = amount / 20 * 20;
        int top = bottom + 20;
        printf("I can give you %d or %d , try again .\n", bottom, top);
    }
    

    return 0;
}
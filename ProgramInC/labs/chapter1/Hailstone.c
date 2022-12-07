#include <stdlib.h>
#include <stdio.h>

int main() {
    int num;
    printf("Please enter the starter: ");
    scanf("%i", &num);
    hailstone(num);

    return 0;
}

void hailstone(int num) {
    while (1) {
        printf("%d\t", num);

        // terminate the program when it reaches 1
        if (num == 1) {
            break;
        }

        if (num % 2 == 0) {
            num = num / 2;
        } else {
            num = num * 3 + 1;
        }
    }
}
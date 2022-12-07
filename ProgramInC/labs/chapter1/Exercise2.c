#include <stdio.h>

int main(void) {
    int a, b, c;
    printf("Input three integers: ");

    int sum = 0;
    scanf("%d %d %d", &a, &b, &c);
    sum = 2 * (a + b + c) + c;

    printf("Twice the sum of integers plus %d is %d\n", c, sum);
}
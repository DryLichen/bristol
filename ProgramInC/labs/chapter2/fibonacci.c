#include <stdio.h>
#include <math.h>
#include <stdbool.h>

#define GOLD 1.61803398875

bool fibword_phi(int n);

int main(int argc, char const *argv[]) {
    int a;
    printf("Please enter a positive integer: ");
    scanf("%d", &a);
    printf("%dth digit is: %d\n", a, fibword_phi(a));
    return 0;
}

// n is assumed to be 1
bool fibword_phi(int n) {
    int a = 2 + floor(n * GOLD) - floor((n + 1) * GOLD);
    return a;
}
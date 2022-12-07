#include <stdio.h>
#include <stdlib.h>
#include <math.h>

double trigonometric(double x);

int main(int argc, char const *argv[]) {
    double x = 0;
    printf("Please enter a value: ");
    scanf("%lf", &x);

    double result = trigonometric(x);
    printf("sin^2(%lf) + cos^2(%lf) = %lf\n",x, x, result);

    return 0;
}

double trigonometric(double x) {
    double result = 0;
    result = pow(sin(x), 2) + pow(cos(x), 2);

    return result;
}

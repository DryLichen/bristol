#include <stdlib.h>
#include <stdio.h>
#include <assert.h>

int get_shape(double a, double b, double c);
void sort(double *a, double *b, double *c);
void test_sort();
void test_shape();

int main() {
    test_sort();
    test_shape();

    double a, b, c;
    int shape_num;
    while (1) {
        printf("Please enter a number of triples of side length, enter -999 to end :");
        scanf("%lf %lf %lf", &a, &b, &c);
        if (a == -999) {
            printf("End entering... ...\n");
            break;
        }
        shape_num = get_shape(a, b, c);
        printf("%i\n", shape_num);
    }

    return 0;
}

// get shape of a triangle
int get_shape(double a, double b, double c) {
    // sort three lengths
    sort(&a, &b, &c);

    // premeters testing
    if (a <= 0 || b <= 0 || c <= 0) {
        return -1;
    }
    if (a + b <= c) {
        return -1;
    }

    if (a == b && b == c) {
        return 1;  // equilateral
    } else if (a == b || a == c || b == c) {
        return 2;  //isosceles
    } else if (a*a + b*b == c*c) {
        return 3;  // right triangle
    } else {
        return 0;  // scalene
    }
}

// sort three values
void sort(double *a, double *b, double *c) {
    double tmp = 0;
    if (*a > *b) {
        tmp = *b;
        *b = *a;
        *a = tmp;
    }
    if (*a > *c) {
        tmp = *c;
        *c = *a;
        *a = tmp;
    }
    if (*b > *c) {
        tmp = *c;
        *c = *b;
        *b = tmp;
    }
}

void test_sort() {
    double a = 5;
    double b = 4;
    double c = 3;

    sort(&a, &b, &c);
    assert(a == 3 && b == 4 && c == 5);
}

void test_shape() {
    double a, b, c;
    int result;

    // unvalid peremeters
    a = -3;
    b = c = 5;
    result = get_shape(a, b, c);
    assert(result == -1);

    a = 1, b = 2, c = 3;
    result = get_shape(a, b, c);
    assert(result == -1);

    // equilateral
    a = b = c = 9.5;
    result = get_shape(a, b, c);
    assert(result = 1);

    // isosceles
    a = b = 6;
    c = 5;
    result = get_shape(a, b, c);
    assert(result == 2);

    // scalene
    a = 2, b = 3, c = 4;
    result = get_shape(a, b, c);
    assert(result == 0);

    // right triangle
    a = 3, b = 4, c = 5;
    result = get_shape(a, b, c);
    assert(result == 3);
}

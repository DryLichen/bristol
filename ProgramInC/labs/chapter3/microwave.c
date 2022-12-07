#include <stdio.h>

int get_button_num(int cook_time);

int main(int argc, char const *argv[]) {
    int cook_time;
    printf("Type the time required\n");
    scanf("%i", &cook_time);
    printf("Number of button presses = %i\n", get_button_num(cook_time));
    return 0;
}

int get_button_num(int cook_time) {
    // initialize buttons
    int buttons[] = {600, 60, 10};
    int a, b, c, sum;

    a = cook_time / buttons[0];
    b = cook_time % buttons[0] / buttons[1];
    c = (cook_time - a * buttons[0] - b * buttons[1]) / buttons[2];
    if (a * buttons[0] + b * buttons[1] + c * buttons[2] == cook_time) {
        sum = a + b + c;
    } else {
        sum = a + b + c + 1;
    }

    return sum;
}

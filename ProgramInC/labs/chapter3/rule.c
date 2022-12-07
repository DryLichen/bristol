#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>

int * get_default_line();
int * rule_100(int *arr, int len);
int get_item(int a, int b, int c);
void test();

int main() {
    test();

    char str[100];
    printf("You can enter a line or just press enter key: ");
    if (scanf("%s", str) == 1) {

    } else {
        
    }

    // option 1
    int len = strlen(str);
    if (len != 0) {
        int *arr = malloc(sizeof(int) * len);
        for (int i = 0; i < len; i++) {
            arr[i] = str[i] - '0';
        }

        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                printf("%i", arr[j]);
            }
            printf("\n");
            arr = rule_100(arr, len);
        }
    } else {  
        // option 2: default starter
        int *arr = get_default_line();
        for (int i = 0; i < 33; i++) {
            for (int j = 0; j < 33; j++) {
                printf("%i", arr[i]);
            }
            printf("\n");
            arr = rule_100(arr, 33);
        }
    }

    return 0;
}

// generate a defalut first line having a 1 in the middle
int * get_default_line() {
    int *arr = malloc(sizeof(int) * 33);

    for (int i = 0; i < 33; i++) {
        if (i == 16) {
            arr[i] = 1;
        } else {
            arr[i] = 0;
        }
    }

    return arr;
}

// get the next line
int * rule_100(int *arr, int len) {
    int *arr_next = malloc(sizeof(int) * len);

    // tail case
    arr_next[0] = get_item(0, arr[0], arr[1]);
    arr_next[len - 1] = get_item(arr[len - 2], arr[len - 1], 0);
    // general case
    for (int i = 1; i < len - 1; i++) {
        arr_next[0] = get_item(arr[i - 1], arr[i], arr[i + 1]);
    }
    
    return arr_next;
}

// get a number following rule 110
int get_item(int a, int b, int c) {
    int exp = a * 100 + b * 10 + c;
    switch (exp) {
        case 111:
            return 0;

        case 110:
            return 1;

        case 101:
            return 1;

        case 100:
            return 0;

        case 11:
            return 1;

        case 10:    
            return 1;
        
        case 1:
            return 1;

        case 0:
            return 0;
    }

    return 0;
}

// print to screen
void printLines() {

}

void test() {
    assert(is_one(1, 1, 0) == 1);
    assert(is_one(1, 0, 0) == 0);
    assert(is_one(0, 1, 1) == 1);


}
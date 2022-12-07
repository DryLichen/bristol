#include <stdio.h>
#include <time.h>
#include <stdlib.h>

// void shuffle(int num);
void shuffle(int *arr, int num);

int main(int argc, char const *argv[]) {
    int song_num;
    printf("How many songs required ? ");
    scanf("%i", &song_num);
    int arr[song_num];
    for (int i = 0; i < song_num; i++) {
        arr[i] = i + 1;
    }
    shuffle(arr, song_num);
    return 0;
}

// 3.3.1
/*
void shuffle(int num) {
    // initialize an array
    int arr[num];
    for (int i = 0; i < num; i++) {
        arr[i] = i + 1;
    }

    // shuffle the array
    int container;
    srand(time(NULL));
    for (int i = 0; i < num; i++) {
        int a = rand() % num;
        int b = rand() % num;
        container = arr[a];
        arr[a] = arr[b];
        arr[b] = container;
    }

    for (int i = 0; i < num; i++) {
        printf("%i ", arr[i]);
    }
}
*/

// 3.3.2 complexity is 0(N)
void shuffle(int *arr, int num) {
    int container;
    srand(time(NULL));
    for (int i = 0; i < num; i++) {
        int a = rand() % num;
        int b = rand() % num;
        container = arr[a];
        arr[a] = arr[b];
        arr[b] = container;
    }

    for (int i = 0; i < num; i++) {
        printf("%i ", arr[i]);
    }
}
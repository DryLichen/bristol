/* Find the max number */
#include <stdlib.h>
#include <stdio.h>

int main() {
    int num_cnt, num;

    printf("How many numbers do you wish to enter?");
    scanf("%d", &num_cnt);
    printf("Enter %d real numbers: ", num_cnt);

    int arr[num_cnt];
    for (size_t i = 0; i < num_cnt; i++) {
        scanf("%d",&num);
        arr[i] = num;
    }

    int container = arr[0];
    for (size_t i = 0; i < num_cnt; i++) {
        if (arr[i] > container) {
            container = arr[i];
        }
    }
    printf("Maximum value: %d\n", container);

    return 0;
}

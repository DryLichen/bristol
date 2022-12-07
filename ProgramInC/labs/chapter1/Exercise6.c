#include <stdlib.h>
#include <stdio.h>

int main() {
    int max;
    int minus_cnt = 0, plus_cnt = 0;
    printf("Please enter the max number: ");
    scanf("%d", &max);

    int median = max / 2;

    for (size_t i = 0; i < 5000; i++) {
        int rand_num = rand() % max;
        if (median < rand_num) {
            plus_cnt++;
        } else if(median > rand_num) {
            minus_cnt++;
        }
    }
    
    printf("The gap between plus_cnt and minus_cnt is: %d\n", plus_cnt - minus_cnt);
    return 0;
}

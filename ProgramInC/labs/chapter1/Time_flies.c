#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>

int * process_time(char *original_time);
int * get_time_lapse(int *processed_time);
int test();

int main() {
    // Make sure you have allocate space for char array, or you will get a segmentation fault
    char std_time[100];
    int *time_lapse = NULL;

    printf("Please enter the std time: ");
    /**
     * @error 
     * It's wrong because %s will stop when it encounters space or terminator
     */
    // scanf("%s", std_time);
    fgets(std_time, 100, stdin);
    time_lapse = get_time_lapse(process_time(std_time));
    printf("The time lapse is: %d:%d\n", time_lapse[0], time_lapse[1]);

    // free heap space
    free(time_lapse);

    return 0;
}

// process the input string time
int * process_time(char *std_time) {
    int *time_arr = (int *)malloc(sizeof(int) * 4);

    sscanf(std_time, "%d:%d %d:%d", time_arr, time_arr + 1, time_arr + 2, time_arr + 3);

    return time_arr;
}

// calculate the time lapse
int * get_time_lapse(int *processed_time) {
    int *lapse_array = (int *)malloc(sizeof(int) * 2);

    if (processed_time[0] <= processed_time[2]) {
        if (processed_time[1] <= processed_time[3]) {
            lapse_array[0] = processed_time[2] - processed_time[0];
            lapse_array[1] = processed_time[3] - processed_time[1];
        } else {
            lapse_array[0] = processed_time[2] - processed_time[0] - 1;
            lapse_array[1] = processed_time[3] - processed_time[1] + 60;
        }
    } else {
        if (processed_time[1] <= processed_time[3]) {
            lapse_array[0] = processed_time[2] - processed_time[0] + 24;
            lapse_array[1] = processed_time[3] - processed_time[1];
        } else {
            lapse_array[0] = processed_time[2] - processed_time[0] + 24 - 1;
            lapse_array[1] = processed_time[3] - processed_time[1] + 60;
        }
    }

    free(processed_time);
    return lapse_array;
}
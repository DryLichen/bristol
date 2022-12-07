/* Higher-Lower */

#include <stdio.h>
#include <stdlib.h>

int main() {
    int rand_num = rand() % 1000;
    int answer;

    for (size_t i = 0; i < 10; i++) {
        printf("PLease enter your answer: ");
        scanf("%i", &answer);
        if (answer == rand_num) {
            printf("\nBingo!\n");
            return 0;
        }

        if (answer < rand_num) {
            printf("\nToo small\n");
        } else {
            printf("\nToo large\n");
        }
    }
    
    printf("Sorry, you lost the game\n");
    return 0;
}

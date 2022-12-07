#include <stdio.h>
#include <stdbool.h>
#include <ctype.h>

bool isvowel(char c);

int main(int argc, char const *argv[]) {
    char c;
    printf("Please enter some characters: ");

    while ((c = getchar()) != EOF) {
        if (isvowel(c)) {
            putchar(toupper(c));
        } else {
            putchar(c);
        }
    }
    
    return 0;
}

bool isvowel(char c) {
    switch (c) {
        case 'a':
            return true;
        
        case 'e':
            return true;

        case 'i':
            return true;

        case 'o':
            return true;

        case 'u':
            return true;

        default:
            return false;
    }
}
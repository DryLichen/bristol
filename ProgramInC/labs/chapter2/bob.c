#include <stdio.h>
#include <stdbool.h>
#include <assert.h>

#define VOWELNUM 5

void get_bob();
void test();

int main() {
    test();
    get_bob();
    return 0;
}

// assume a is not less than 2
bool is_prime(int a) {
    for (int i = 2; i < a; i++) {
        if (a % i == 0) {
            return false;
        }
    }
    return true;
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

// convert ascii number to normal number for alphabet
int asciiToNum(int a) {
    return a - ('a' - 1) * 3;
}

// print bob names
void get_bob() {
    char vowel[VOWELNUM] = {'a', 'e', 'i', 'o', 'u'};

    // vcv
    for (int i = 0; i < VOWELNUM; i++) {
        for (int j = 'a'; j <= 'z'; j++) {
            if (is_prime(asciiToNum(2 * vowel[i] + j)) && !isvowel(j)) {
                printf("%c%c%c\n", vowel[i], j, vowel[i]);
            }
        }
    }

    //cvc
    for (int i = 0; i < VOWELNUM; i++) {
        for (int j = 'a'; j <= 'z'; j++) {
            if (is_prime(asciiToNum(vowel[i] + 2 * j)) && !isvowel(j)) {
                printf("%c%c%c\n", j, vowel[i], j);
            }
        }
    }
}

void test() {
    assert(is_prime(19));
}
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <assert.h>

bool isAnagram(const char *word1, char *word2);
void sort(char *chars);
void swap(char *str, int i, int j);
void test();

int main(int argc, char const *argv[]) {
    test();

    if (argc != 2) {
        fprintf(stderr, "Wrong number of peremeters!\n");
        exit(EXIT_FAILURE);
    }

    FILE *fp = fopen("words_alpha.txt", "r");
    if (fp == NULL) {
        fprintf(stderr, "Can't read file!\n");
        exit(EXIT_FAILURE);
    }

    char buff[255];
    while ((fscanf(fp, "%s", buff)) != EOF) {
        if (isAnagram(argv[1], buff)) {
            printf("%s\n", buff);
        }
    }
    
    fclose(fp);

    return 0;
}

// return true if two input words are anagrams
bool isAnagram(const char *word1, char *word2) {
    int length1 = strlen(word1); 
    int length2 = strlen(word2); 
    if (length1 != length2) {
        return false;
    }

    char chars1[length1];
    strcpy(chars1, word1);
    char chars2[length2];
    strcpy(chars2, word2);

    sort(chars1);
    sort(chars2);

    // compare sorted char arrays
    for (int i = 0; i < length1; i++) {
        if (chars1[i] != chars2[i]) {
            return false;
        }
    }

    return true;
}

// sort an array ascendingly
void sort(char *str) {
    int length = strlen(str);

    for (int i = 0; i < length - 1; i++) {
        for (int j = i + 1; j < length; j++) {
            if (str[j] < str[i]) {
                swap(str, i, j);
            }
        }
    }
}

void swap(char *str, int i, int j) {
    char tmp = str[i];
    str[i] = str[j];
    str[j] = tmp;
}

void test() {
    // sort tests
    char str[] = "sternaig";
    sort(str);
    assert(strcmp(str, "aeginrst") == 0);

    // isAnagram tests
    char str1[] = "sternaig";
    char str2[] = "gantries";
    assert(isAnagram(str1, str2));
}
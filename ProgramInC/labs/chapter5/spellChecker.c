#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>

#define ARRAYLEN 130000
#define BUFFLEN 20

char ** readFile(const char *fileName);
void clearSpace(char ** strArray, int len);
int getAddr(char *strArray[], int len, char *str);
void insert(char *strArray[], int *len, int addr, char *str);
void swap(char *strArray[], int i, int j);
void test();

int main(int argc, char const *argv[]) {
    // test();
    
    char **strArray = readFile(argv[1]);

    for (int i = 0; i < 100; i++) {
        printf("%s ", strArray[i]);
    }

    clearSpace(strArray, ARRAYLEN);
    return 0;
}

// read given file and return a string array sorted ascendingly
char ** readFile(const char *fileName) {
    FILE *fp = NULL;
    fp = fopen(fileName, "r");

    // allocate space for the string array
    char **strArray = NULL;
    strArray = (char **)malloc(sizeof(char *) * ARRAYLEN);
    for (int i = 0; i < ARRAYLEN; i++) {
        strArray[i] = (char *)malloc(sizeof(char) * BUFFLEN);
    }

    char buff[BUFFLEN];
    int count = 0;
    while ((fscanf(fp, "%s", buff)) != EOF) {
        insert(strArray, &count, getAddr(strArray, count, buff), buff);
    }

    fclose(fp);
    return strArray;
}

// free space of strArray allocated by readFile
// len is the length of outer array
void clearSpace(char ** strArray, int len) {
    for (int i = 0; i < len; i++) {
        free(strArray[i]);
    }
    free(strArray);
}

// return the index where the word should be inserted into
// len is the number of inserted elements
int getAddr(char *strArray[], int len, char *str) {
    if (len == 0) {
        return 0;
    }

    int bottom = 0, top = len - 1;

    while (bottom <= top) {
        int mid = (top + bottom) / 2;
        if (strcmp(strArray[mid], str) > 0) {
            top = mid - 1;
        } else if (strcmp(strArray[mid], str) < 0) {
            bottom = mid + 1;
        } else {
            return mid;
        }
    }

    return top;
}

// insert a word into the given address in an array
// len is the number of inserted elements
void insert(char *strArray[], int *len, int addr, char *str) {
    for (int i = *len; i >= addr; i--) {
        strArray[i] = strArray[i - 1];
    }
    strcpy(strArray[addr], str);
    // for (int i = addr; i < *len; i++) {
    //     swap(strArray, i, *len);
    // }
    *len++;
}

void swap(char *strArray[], int i, int j) {
    char *tmpStr = strArray[i];
    strArray[i] = strArray[j];
    strArray[j] = tmpStr;
}

void test() {
    char *str1 = "abc";
    char *str2 = "abcd";
    char *str3 = "cdf";
    char *str4 = "hfsh";
    char *str5 = "gjdfh";
    char *strArray[6] = {str1, str2, str3, str4, str5};
    int len = 5;

    // getAddr tests
    char *strInser1 = "abcd";
    char *strInser2 = "cd";
    assert(getAddr(strArray, len, strInser1) == 1);
    assert(getAddr(strArray, len, strInser2) == 2);

    // swap tests
    swap(strArray, 0, 3);
    assert(strcmp(strArray[0], "hfsh") == 0);
    assert(strcmp(strArray[3], "abc") == 0);
    assert(strcmp(strArray[4], "gjdfh") == 0);

    // insert tests
    insert(strArray, &len, getAddr(strArray, len, "hhhhhh"), "hhhhhh");
    assert(strcmp(strArray[2], "hhhhhh") == 0);
    assert(strcmp(strArray[5], "gjdfh") == 0);
    assert(len = 6);

    printf("All the tests are passed!\n");
}

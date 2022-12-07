#include <stdio.h>

int main(int argc, char const *argv[])
{
    int tmp = 0;
    int len = 0;
    char* str = "-4";
    len = sscanf(str, "%d", &tmp);
    printf("%d**", tmp);
    printf("%d", len);
    return 0;
}

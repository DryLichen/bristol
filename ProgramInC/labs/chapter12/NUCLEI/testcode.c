#include "lisp.h"
#include "specific.h"

lisp* lisps[4]= {-1};

int main(int argc, char const *argv[])
{
    char str[100] = "((12  33 4) ";
    char buff[200];
    char *p = strtok(str, " ");
    while (p != NULL) {
        
        p = strtok(NULL, " ");
    }
    
}

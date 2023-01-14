#include "lisp.h"
#include "specific.h"

lisp* lisps[4]= {-1};

int main(int argc, char const *argv[])
{
    for (int i = 0; i < 4; i++) {
        printf("%d", lisps[i]);
    }
}

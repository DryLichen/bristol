#include "../../IndexedArray/Realloc/specific.h"
#include "../../IndexedArray/arr.h"
#include "../../General/general.h"

#define INITSIZE 0

struct set {
    arr* arr;
    int num;
    int ptr;
};

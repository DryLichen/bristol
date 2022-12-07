#include "../arr.h"
#include "specific.h"

/* Creates the empty array */
arr* arr_init(void) {
    arr* l = NULL;
    l = (arr*)ncalloc(1, sizeof(arr));
    l->array = (int*)ncalloc(INITSIZE, sizeof(int));
    l->length = INITSIZE;
    
    return l;
}

/* Similar to l[n] = i, safely resizing if required */
void arr_set(arr *l, int n, int i) {
    int length = l->length;

    while (n >= length) {
        length *= 2;
        l->array = (int*)nremalloc(l->array, sizeof(int) * length);
    }
    
    l->array[n] = i;
    l->length = length;
}

/* Similar to = l[n] */
int arr_get(arr *l, int n) {
    if (n > l->length) {
        on_error("Index out of bound!");
    }

    return l->array[n];
}

/* Clears all space used, and sets pointer to NULL */
// use double pointer because we need to set the variable containing this pointer to null
void arr_free(arr **l) {
    free(*l);
    *l = NULL;
}

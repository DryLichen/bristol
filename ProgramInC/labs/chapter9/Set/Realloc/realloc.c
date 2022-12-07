#include "specific.h"
#include "../set.h"

/* Create empty set */
set* set_init(void) {
    set* setInit = NULL;
    setInit = (set*)ncalloc(1, sizeof(set));
    setInit->arr = arr_init();
    setInit->num = 0;
    setInit->ptr = 0; 

    return setInit;
}
/* Create new set, copied from another */
set* set_copy(set* s) {
    set* setNew = NULL;
    setNew = (set*)ncalloc(1, sizeof(set));


    setNew->arr->length = s->arr->length;
    setNew->num = s->num;
    setNew->ptr = s->ptr;

    return setNew;
}
/* Create new set, copied from an array of length n*/
set* set_fromarray(int* a, int n) {
    set* setNew = NULL;
    setNew = (set*)ncalloc(1, sizeof(set));

    memcpy(setNew->arr->array, a, sizeof(int) * n);
    setNew->arr->length = n;
    setNew->num = n;
    setNew->ptr = n;

    return setNew;
}

/* Basic Operations */
/* Add one element into the set */
void set_insert(set* s, int l) {
    if (set_contains(s, l)) {
        return;
    }

    arr_set(s->arr, s->ptr, l);
    (s->num)++;
    (s->ptr)++;
}

/* Return size of the set */
int set_size(set* s) {

}

/* Returns true if l is in the array, false elsewise */
int set_contains(set* s, int l) {
    for (int i = 0; i < s->arr->length; i++) {
        if (l == s->arr->array[i]) {
            return 1;
        }
    }
    
    return 0;
}

/* Remove l from the set (if it's in) */
void set_remove(set* s, int l) {

}

/* Remove one element from the set - there's no
   particular order for the elements, so any will do */
int set_removeone(set* s) {

}

/* Operations on 2 sets */
/* Create a new set, containing all elements from s1 & s2 */
set* set_union(set* s1, set* s2){

}
/* Create a new set, containing all elements in both s1 & s2 */
set* set_intersection(set* s1, set* s2);

/* Finish up */
/* Clears all space used, and sets pointer to NULL */
void set_free(set** s);
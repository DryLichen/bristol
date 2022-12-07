#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#define MINS 60
#define DAY (24 * MINS)

int get_mins(int h, int m);
int diff2times(int m1, int m2);
void test();

int main(int argc, char const *argv[]) {
    test();
    int h1, m1, h2, m2;
    if(scanf("%d:%d %d:%d", &h1, &m1, &h2, &m2) == 4) {
        int mins1 = get_mins(h1, m1);
        int mins2 = get_mins(h2, m2);
        int diff = diff2times(mins1, mins2);
        printf("Difference is %02i:%02i\n", diff / MINS, diff % MINS);
    }

    return 0;   
}

int get_mins(int h, int m) {
    int mins = h * MINS + m;
    return mins;
}

int diff2times(int m1, int m2) {
    return m1 < m2 ? m2 - m1 : DAY + m2 - m1;
}

void test() {
    assert(get_mins(1, 1) == 61);
    assert(get_mins(5, 6) == 306);
    assert(diff2times(70,80)==10);
    assert(diff2times(get_mins(23,59), get_mins(0,1))==2);
}

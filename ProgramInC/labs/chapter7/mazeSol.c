#include <stdio.h>
#include <stdbool.h>

#define Y 20
#define X 20

struct maze {
   char mz[Y][X];
   int w, h;
   int ex, ey;
};

typedef struct maze maze;
bool explore(maze* m, int x, int y);
bool isempty(maze* m, int x, int y);
void printmaze(maze* m);
void test(void);

int main(void) {
   test();
   return 0;
}

void printmaze(maze* m) {
   for(int y=0; y<m->h; y++){
      for(int x=0; x<m->w; x++){
         printf("%c", m->mz[y][x]);
      }
      printf("\n");
   }
   printf("\n");
}

bool explore(maze* m, int x, int y) {
    printmaze(m);
    if(x==m->ex && y==m->ey){
       return true;
    }
    if(!isempty(m, x, y)){
       return false;
    }

    m->mz[y][x] = '#';
    if(explore(m, x+1, y)){
       m->mz[y][x] = '>';
       return true;
    }
    if(explore(m, x, y+1)){
       m->mz[y][x] = 'v';
       return true;
    }
    if(explore(m, x, y-1)){
       m->mz[y][x] = '^';
       return true;
    }
    if(explore(m, x-1, y)){
       m->mz[y][x] = '<';
       return true;
    }
    m->mz[y][x] = ' ';
    return false;

}

bool isempty(maze* m, int x, int y) {
   if((x<0) || (x>=m->w)){
      return false;
   }
   if((y<0) || (y>=m->h)){
      return false;
   }
   return m->mz[y][x] == ' ';
}

void test(void) {
   maze m = {
      {{'#',' ','#','#','#'},
       {'#',' ',' ',' ',' '},
       {'#',' ','#',' ','#'},
       {'#',' ',' ',' ','#'},
       {'#','#','#','#','#'}},
       5, 5, 4, 1};
   printmaze(&m);
   if(explore(&m, 1, 0)){
      printmaze(&m);
   }
   else{
      printf("No Solution?\n");
   }
}
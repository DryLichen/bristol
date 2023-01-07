
ifInt.o:     file format elf32-littlearm


Disassembly of section .text:

00000000 <if_int>:
int if_int(int x, int y) {
    if(x ==y) {
   0:	e1500001 	cmp	r0, r1
        x += y;
   4:	01a00080 	lsleq	r0, r0, #1
    }
    return x;
   8:	e12fff1e 	bx	lr

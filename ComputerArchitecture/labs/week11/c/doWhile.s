
doWhile.o:     file format elf32-littlearm


Disassembly of section .text:

00000000 <do_while>:
int do_while (int a ,int b) {
    do {
        a = a + 1;
   0:	e2800001 	add	r0, r0, #1
    } while( a < b );
   4:	e1500001 	cmp	r0, r1
   8:	bafffffc 	blt	0 <do_while>
   c:	e12fff1e 	bx	lr

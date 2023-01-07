
while2.o:     file format elf32-littlearm


Disassembly of section .text:

00000000 <While_plus_plus_a>:
int While_plus_plus_a (int a ,int b,int c) {
    while( a < c) {
   0:	e1500002 	cmp	r0, r2
   4:	aa000003 	bge	18 <While_plus_plus_a+0x18>
        b = ++a + b;
   8:	e2800001 	add	r0, r0, #1
   c:	e0811000 	add	r1, r1, r0
    while( a < c) {
  10:	e1520000 	cmp	r2, r0
  14:	1afffffb 	bne	8 <While_plus_plus_a+0x8>
    }
    return b;
}
  18:	e1a00001 	mov	r0, r1
  1c:	e12fff1e 	bx	lr

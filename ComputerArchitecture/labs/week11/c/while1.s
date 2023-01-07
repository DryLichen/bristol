
while1.o:     file format elf32-littlearm


Disassembly of section .text:

00000000 <While_a_plus_plus>:
int While_a_plus_plus (int a ,int b ,int c) {
    while(a < c) {
   0:	e1500002 	cmp	r0, r2
   4:	aa000004 	bge	1c <While_a_plus_plus+0x1c>
        b = a++ + b;
   8:	e280c001 	add	ip, r0, #1
   c:	e0811000 	add	r1, r1, r0
  10:	e1a0000c 	mov	r0, ip
    while(a < c) {
  14:	e152000c 	cmp	r2, ip
  18:	1afffffa 	bne	8 <While_a_plus_plus+0x8>
    }
    return b;
  1c:	e1a00001 	mov	r0, r1
  20:	e12fff1e 	bx	lr

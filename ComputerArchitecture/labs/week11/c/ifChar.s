
ifChar.o:     file format elf32-littlearm


Disassembly of section .text:

00000000 <if_char>:
int if_char(char x, char y) {
    if(x == y) {
        x = 'a';
    }
    return x;
   0:	e1500001 	cmp	r0, r1
   4:	03a00061 	moveq	r0, #97	; 0x61
   8:	e12fff1e 	bx	lr


ifElse.o:     file format elf32-littlearm


Disassembly of section .text:

00000000 <if_else>:
char if_else (char a ,char b) {
    if( a == 'a') {
   0:	e3500061 	cmp	r0, #97	; 0x61
        a++;
    } else {
        b++;
   4:	12811001 	addne	r1, r1, #1
   8:	120110ff 	andne	r1, r1, #255	; 0xff
        a++;
   c:	03a00062 	moveq	r0, #98	; 0x62
    }
    return a+b;
  10:	e0800001 	add	r0, r0, r1
  14:	e20000ff 	and	r0, r0, #255	; 0xff
  18:	e12fff1e 	bx	lr

.section .text
.align 2
.global _start

_start: 
    ldr r0, =30 @ inputs
    ldr r1, =20 

_loop:
    cmp r0, r1  
    @ if r0 = r1, the value is the gcd, halt the program
    moveq r2, r0
    beq _end
    @ else, continue calculating the remainder

_remainder:
    cmp r0, r1
    subgt r0, r0, r1
    sublt r1, r1, r0
    b _loop

_end:   
    b _end
    
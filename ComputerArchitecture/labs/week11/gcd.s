.section .text
.align 2
.global _start


_start:
    @ read parameters
    ldr r0, =_inputs
    ldr r1, [r0]
    ldr r2, [r0, #4]!

_calc_GCD:
    @ make r3 < r4
    cmp r1, r2
    movge r3, r2
    movge r4, r1
    movlt r3, r1
    movlt r4, r2

    @ get the remainder
    bl _remainder
    str r7, [r0, #4]!

    @ when remainder equals zero, halt the program
    @ the gcd is stored in r3
    cmp r7, #0
    beq _end
    mov r4, r3
    mov r3, r7

_remainder:
    @ put the remainder of r4/r3 in r7
    udiv r5, r4, r3
    mul r6, r5, r3
    sub r7, r4, r6

    bx lr

_end:
    b _end


.section .data
_inputs:
.word 34,22

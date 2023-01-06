.section .text
.align 2
.global _start

_start:
    mov r0, #30
    mov r1, #10
    bl _calc_GCD  @ call subroutine 

    b _end

_calc_GCD:
    @ let r2 contains the greater num, r3 contains the smaller one
    @ r2 > r3
    cmp r0, r1
    movge r2, r0
    movge r3, r1
    movlt r2, r1
    movlt r3, r0

_loop:
    @ get the remainder of r2/r3
    udiv r4, r2, r3
    mul r5, r4, r3
    sub r6, r2, r5

    @ if the remainder equals zero, return to caller
    cmp r6, #0
    mov r2, r3
    moveq pc, lr
    
    @ else, reset r2 and r3
    mov r2, r3
    mov r3, r6

    b _loop

_end:
    b _end

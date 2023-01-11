@ unsigned int factorial(unsigned int n) {
@     if (n == 0) {
@       return 1;
@     }
@     return n * factorial(n - 1);
@ }

.section .text
.align 2
.global _start

_start:
    @ factorial 3
    ldr r0, =#3
    bl _factorial
    b _end

_factorial:
    @ if n == 0, return 1
    cmp r0, #0
    moveq r0, #1
    moveq pc, lr

    mov r1, r0
    sub r0, r0, #1
    stmfd sp!, {r1, lr}
    bl _factorial
    ldmfd sp!, {r1, lr}
    mul r0, r1, r0
    mov pc, lr

_end:
    b _end


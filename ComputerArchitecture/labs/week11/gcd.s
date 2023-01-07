.section .text
.align 2
.global _start


_start:
    @ read parameters
    ldr r0, =_inputs
    ldr r1, [r0]
    ldr r2, [r0, #4]

    @ make r3 < r4
    cmp r1, r2
    bgt _exchange
    mov r3, r1
    mov r4, r2

    @ get the remainder
    b _remainder

_exchange:
    mov r3, r2
    mov r4, r1

_remainder:
    udiv r5, r4, r3
    mul r6, r5, r3
    sub r7, r4, r6

    mov 

_end:
    b _end


.section .data
_inputs:
.word 34 22
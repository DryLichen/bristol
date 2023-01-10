.section .text
.align
.global _start

_start:
    ldr r0, =_data
    ldr r1, [r0]  @ w
    mov r2, r1  @ y
    lsl r1, r1, #16  @ 16.16
    lsl r2, r2, #8  @ 24.8

    bl _newton
    b _end

_newton:
    udiv r3, r1, r2  @ 16.8
    add r4, r3, r2  @ 16.8 + 24.8 = 24.8
    asr r4, r4, #1  @ 24.8  

    @ precision check
    sub r5, r2, r4
    cmp r5, #1  @ 24.8
    bxle lr
    mov r2, r4  
    b _newton

_end:
    b _end

.section .data
_data:
.word 4

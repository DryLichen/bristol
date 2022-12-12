.section .text
.align 2
.global _start

_start:
    mov r0, #1
    mov r1, #2
    mov r2, #3
    mov r3, #4

    // store the determinant of det in r4, r4 in 24.8 precision
    mul r4, r0, r3
    mul r5, r1, r2
    sub r0, r4, r5
    
    mov r5, #1
    lsl r5, r5, #16 //r5 is in 16.16
    sdiv r6, r5, r4

_end:
    b _end
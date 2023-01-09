.section .text
.align 2
.global _start

_start:
    mov r0, #1
    lsl r0, r0, #8  @ format 24.8
    mov r1, #2
    lsl r1, r1, #8  @ format 24.8
    mov r2, #3
    lsl r2, r2, #8  @ format 24.8
    mov r3, #4
    lsl r3, r3, #8  @ format 24.8

    @ get the original determinant and store it in r4
    bl _det  @ format 24.8
    
    @ r6 = 1 / det
    mov r5, #1  
    lsl r5, r5, #16  @ 16.16
    sdiv r6, r5, r4  @ 16.8

    @ 1 / det * every item in the original matrix
    mul r8, r0, r6
    lsr r0, r8, #8  @ 16.16 -> 24.8
    mul r8, r1, r6
    lsr r1, r8, #8  @ 16.16 -> 24.8
    mul r8, r2, r6
    lsr r2, r8, #8  @ 16.16 -> 24.8
    mul r8, r3, r6
    lsr r3, r8, #8  @ 16.16 -> 24.8

    @ get -b and -c
    ldr r8, =0xffffff00  @ -1 with 24.8 format
    mul r9, r1, r8
    mov r1, r9, lsr #8  @ 16.16 -> 24.8
    mul r9, r2, r8
    mov r2, r9, lsr #8  @ 16.16 -> 24.8
    
    @ swap a and d
    mov r4, r0
    mov r0, r3
    mov r3, r4

    b _end

_det:
    mul r4, r0, r3
    lsr r4, r4, #8  @ 16.16 -> 24.8
    mul r5, r1, r2
    lsr r5, r5, #8  @ 16.16 -> 24.8
    sub r4, r4, r5  @ 24.8
    mov pc, lr

_end:
    b _end
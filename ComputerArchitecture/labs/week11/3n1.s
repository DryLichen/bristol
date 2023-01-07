.section .text
.align 2
.global _start

_start:
    ldr r0, =_parameters  @ location of data
    ldr r1, [r0]  @ times
    ldr r2, [r0, #4]  @ input
    mov r3, #0  @ counter for 4-2-1
    mov r4, #1  @ counter for loop

_loop:
    add r4, r4, #1
    lsl r5, r4, #2
    @ halt the program when 1 appears for a certain number of times
    cmp r2, #1
    addeq r3, r3, #1
    cmp r3, r1
    beq _end

    @ check if it's odd or even
    tst r2, #0x1
    @ even
    beq _even
    @ odd
    add r2, r2, r2, lsl #1
    add r2, r2, #1

    @ store new input
    str r2, [r0, r5]
    b _loop

_even:
    asr r2, #1
    str r1, [r0, r5]
    b _loop

_end:
    b _end

.section .data
_parameters:
.word 3, 4  @ input

.section .text
.align 2
.global _start

_start:
    mov r0, #4
    mov r1, #0
    ldr r2, =_array

_loop:
    ldr r3, [r2], #4
    add r1, r1, r3
    subs r0, r0, #1
    bne _loop
    b _end

_end:
    b _end

.section .data
_array:
.word 0x1, 0x2, 0x3, 0x4

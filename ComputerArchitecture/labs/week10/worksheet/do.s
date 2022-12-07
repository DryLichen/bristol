.section .text
.align 2
.global _start

_start:
    mov r0, #1
    mov r1, 5

_loop:
    add r0, #1
    cmp r0, r1
    blt _loop

_end:
    b _ed
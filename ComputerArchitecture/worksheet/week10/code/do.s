.section .text
.align 2
.global _start

_start:
    mov r0, #1  @ a 
    mov r1, #5  @ b

_loop:
    add r0, #1
    cmp r0, r1
    blt _loop

_end:
    b _end
    
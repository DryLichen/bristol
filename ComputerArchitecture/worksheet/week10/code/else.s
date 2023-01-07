.section .text
.align 2
.global _start

_start:
    mov r0, #2  @ x
    mov r1, #2  @ y
    cmp r0, r1  
    addne r0, r1, r1, lsl #1
    rsbeq r1, r0, r0, lsl #4

_end:
    b _end

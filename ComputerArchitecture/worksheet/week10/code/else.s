.section .text
.align 2
.global _start

_start:
    mov r0, #1
    mov r1, #2
    cmp r0, r1
    addne r0, r1, r1 lsl #1

_end:
    b _end
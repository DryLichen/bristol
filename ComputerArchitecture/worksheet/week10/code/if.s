.section .text
.align 2
.global _start

_start:
    mov r0, #0
    mov r1, #1
    cmp r0, r1
    addeq r0, r0, r1

_end:
    b _end
    
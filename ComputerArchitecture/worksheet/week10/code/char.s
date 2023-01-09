.section .text
.align 2
.global _start

_start:
    mov r0, #0x66  @ ASCII for f, F is 0x46 -> need to substract 0x00000020
    mov r1, #0x75  @ ASCII for u
    mov r2, #0x6e  @ ASCII for n

    sub r0, r0, #0x20 
    sub r1, r1, #0x20
    sub r2, r2, #0x20

_end:
    b _end
    
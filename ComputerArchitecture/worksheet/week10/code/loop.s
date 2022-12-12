.section .text
.align 2
.global _start

_start:
    mov r0, #0
    mov r1, #0  @a
    mov r2, #4  @b
    mov r3, r1  @i

_loop:
    cmp r3, r2  @ i >= b then go to _end
    bge _end
    add r0, #1
    add r3, #1
    b _loop

_end:
    b _end

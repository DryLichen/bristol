.section .text
.align 2
.global _start

_start:
    ldr r0, _parameters


_loop:
    @halt the program when getting 1
    cmp r0, #1
    beq _end

    b _loop

_end:
    b _end

.section .data
_parameters:
.word 3, 3  @ input
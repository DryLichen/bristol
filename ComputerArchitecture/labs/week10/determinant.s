.section .text
.align 2
.global _start

_start: MOV r0, #2
        MOV r1, #-2
        MOV r2, #3
        MOV r3, #1
        MUL r4, r0, r3
        MUL r5, r1, r2
        SUB r0, r4, r5
_end:   b _end

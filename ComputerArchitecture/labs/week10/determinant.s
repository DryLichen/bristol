.section .text
.align 2
.global _start

@_start: MOV r0, #2
@        MOV r1, #-3
@        MOV r2, #3
@        MOV r3, #0
@        MUL r4, r0, r3
@        MUL r5, r1, r2
@        SUB r0, r4, r5
@_end:   b _end

_start: ldr r0, =0x1  @ a
        ldr r1, =0x2  @ b
        ldr r2, =0x3  @ c
        ldr r3, =0x4  @ d

        MUL r4, r0, r3
        MUL r5, r1, r2

        SUB r6, r4, r5

_end:   b _end

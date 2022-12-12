.section .text
.align 2
.global _start

_start: 
    LDR r0, =_matrix
    LDR r1, [r0]
    LDR r2, [r0, #4 * 1]
    LDR r3, [r0, #4 * 2]
    LDR r4, [r0, #4 * 3]
    BL _det
    B _end

_det:
    MUL r5, r1, r4
    MUL r6, r2, r3
    SUB r7, r5, r6 // two's complement
    MOV PC, LR

_end:
    b _end

.section .data
_matrix:
.word 1,2,3,4 // a,b,c, and d values

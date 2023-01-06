.section .text
.align 2
.global _start

_start:
    LDR r0, =_matrix
    LDR r1, [r0, #4 * 4]
    LDR r2, [r0, #4 * 5]
    LDR r3, [r0, #4 * 7]
    LDR r4, [r0, #4 * 8]
    BL _det_2
    MOV r8, r7

    LDR r1, [r0, #4 * 3]
    LDR r2, [r0, #4 * 5]
    LDR r3, [r0, #4 * 6]
    LDR r4, [r0, #4 * 8]
    BL _det_2
    MOV r9, r7

    LDR r1, [r0, #4 * 3]
    LDR r2, [r0, #4 * 4]
    LDR r3, [r0, #4 * 6]
    LDR r4, [r0, #4 * 7]
    BL _det_2
    MOV r10, r7

    LDR r1, [r0]
    MUL r2, r1, r8
    LDR r1, [r0, #4]
    MUL r3, r1, r9
    LDR r1, [r0, #4 * 2]
    MUL r4, r1, r10
    SUB r1, r2, r3
    ADD r1, r1, r4

    B _end

_det_2:
    MUL r5, r1, r4
    MUL r6, r2, r3
    SUB r7, r5, r6
    MOV PC, LR

_end:
    b _end

.section .data
_matrix:
.word 6,1,1,4,-2,5,2,8,7 @ a,b,c,d,e,f,g,h,i

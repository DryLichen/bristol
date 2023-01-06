.section .text
.align 2
.global _start

_start:
    @ 1. different signs
    @ ldr r0, =0x80000001
    @ ldr r1, =0xfffffffe
    @ ldr r2, =0x7fffffff
    @ ldr r3, =0x2

    @ 2. r0 and r3 are both negative
    @ ldr r0, =0x80000001
    @ ldr r1, =0xfffffff9
    @ ldr r2, =0x5
    @ ldr r3, =0xfffffffe

    @ 3. r0 and r3 and both positive
    ldr r0, =0x7fffffff
    ldr r1, =0xfffffffe
    ldr r2, =0x7fffffff
    ldr r3, =0x2

    MUL r4, r0, r3  @ a*d
    MUL r5, r1, r2  @ b*c

    SUB r6, r4, r5  @ a*d - b*c

    @ The result of a*d must be fit in 32 bits
    @ same sign: mul should be less than 0x7fffffff
    @ diff sign: mul should be larger than 0x80000000
    ldr r4, =0x80000000  @ the lowest negative 32-bits number
    ldr r5, =0x7fffffff  @ the largest psositive 32-bits number

    @ find the largest number between r0 and r3.
    @ Assign the largest number to r7 and the smallest to r6
    cmp r0, r3
    ble _r0_less_equal_r3
    mov r6, r3
    mov r7, r0
    
_r0_less_equal_r3:
    mov r6, r0
    mov r7, r3

_check_signs:
    @ r9 = 0x80000000 if r0 < 0 ; r9 = 0 if r0 > 0
    and r9, r0, r4
    @ r10 = 0x80000000 if r3 < 0 ; r10 = 0 if r3 > 0
    and r10, r3, r4

    @ check if a and d has the same sign
    cmp r9, r10
    beq _both_positive_negative
    
    @ a and d is of different sign
    @ if r4/(the smaller num) is less than the larger num, overflow will happen
    sdiv r11, r4, r6
    cmp r7, r11
    bgt _wrong_result

_both_positive_negative:
    tst r0, r4
    beq _both_positive
    @ both negative
    @ convert to positive nums
    mov r8, #-1
    mul r9, r6, r8
    mul r10, r7, r8
    mov r6, r9
    mov r7, r10

_both_positive:
    @ if r5/(the smaller num) is less than the larger num, carryout will happen 
    udiv r6, r5, r6
    cmp r7, r6
    bgt _wrong_result

    mov r8, #1
    b _end

_wrong_result:
    mov r8, #-1
    b _end

_end:
    b _end

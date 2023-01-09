.section .text
.align 2
.global _start

_start:
    ldr r0, =_data
    ldr r1, [r0]

    @ when detecting line feed, halt
    cmp r1, #0x0A
    beq _end

    @ check if the letter is in lower case
    cmp r1, #0x60
    bgt _ASCII_greater_than_0x60
    ble _ASCII_not_greater_than_0x60

_ASCII_greater_than_0x60:
    cmp r1, #0x7B
    sublt r1, r1, #0x20
    str r1, [r0]

_ASCII_not_greater_than_0x60:
    cmp r1, #0x20
    moveq r2, #1
    movne r2, #0

@ letters starting from the second location
_loop:
    ldr r1, [r0, #4]!
    @ when detecting line feed, halt
    cmp r1, #0x0A
    beq _end
    @ check if the letter is in lower case
    cmp r1, #0x60
    bgt _ASCII_greater_than_0x60_in_loop_
    ble _Skip_capitalization

_ASCII_greater_than_0x60_in_loop_:
    cmp r1, #0x7B
    blt _ASCII_greater_than_0x60_and_less_than_0x7B
    bge _Skip_capitalization

_ASCII_greater_than_0x60_and_less_than_0x7B:
    cmp r2, #0x1
    beq _ASCII_grater_than_0x60_and_less_than_0x7B_and_previous_letter_is_space
    bne _Skip_capitalization

_ASCII_grater_than_0x60_and_less_than_0x7B_and_previous_letter_is_space:
    sub r1, r1, #0x20
    str r1, [r0]

_Skip_capitalization:
    cmp r1, #0x20
    moveq r2, #1
    movne r2, #0

    b _loop
    
_end:
    b _end

.section .data
_data:
    @Hi I know how to code with ARM assembly
.word 0x48, 0x69, 0x20, 0x49, 0x20, 0x6B, 0x6E, 0x6F, 0x77, 0x20, 0x68, 0x6F, 0x77, 0x20, 0x74, 0x6F, 0x20, 0x63, 0x6F, 0x64, 0x65, 0x20, 0x77, 0x69, 0x74, 0x68, 0x20, 0x41, 0x52, 0x4D, 0x20, 0x61, 0x73, 0x73, 0x65, 0x6D, 0x62, 0x6C, 0x79, 0x0A

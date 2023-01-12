.section .text
.align 2
.global _start

_start:
    mov r1, #10
    mov r2, #0
    mov r3, #0
    mov r7, #5
    mov r4,#2
    cmp r1,#1
    ldreq r7,=0x0
    beq _end
    _loop:
    tst r1,#1
    ldrne r7,=0xffff
    bne _end
    udiveq r1,r1,r4
    cmpeq r1,#2
    ldreq r7,=0x0
    ldrlt r7,=0x0
    bgt _loop

    mov r10, #0

_end:
    b _end

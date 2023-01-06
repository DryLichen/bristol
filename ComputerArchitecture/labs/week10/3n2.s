.section .text
.align 2
.global _start

_start: mov r0, #5  @ input

_loop:  cmp r0, #1  @ if input equals one, halt the program
        beq _end

        @ check if it's add or even
        tst r0, #1

        asreq r0, r0, #1    @ even
        addne r0, r0, r0, lsl #1    @ odd
        addne r0, r0, #1    @ odd

        b _loop

_end:   b _end

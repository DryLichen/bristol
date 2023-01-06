.section .text
.align 2
.global _start

_start: ldr r0, =0x30   @ input number

_loop:  cmp r0, #1  @ if input equals 1, terminate program
        beq _end

        @ check if it's even or odd
        tst r0, #1  @ keep the lsb and change other bits to 0
        beq _even   @ if it's a even number, jump to even func

        @ odd function
        add r0, r0, r0, lsl #1
        add r0, r0, #1
        b _loop

_even:  asr r0, #1
        b _loop

_end:   b _end

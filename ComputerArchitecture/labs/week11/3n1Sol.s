s.section        .text
.align          2
.global         _start

_start:

@ 3.2 The 3n+1 problem - solution

        ldr     r0,=_n @ Load r0 with the address of the input number
        ldr     r1, [r0] @ Load r1 with the input number

        ldr r2,=_repetition_number @ Load r2 with the address of the number of the required repetitions of the sequence 1 - 4 - 2
        ldr r2,[r2] @ Load r2 with the number of the required repetitions of the sequence 1 - 4 - 2


        mov r3,#0 @ r3 is used as a state variable 
        @ r3 = 0 : initail state
        @ r3 = 1 : if r1 is equal to 1
        @ r3 = 4 : if r1 = 4 and r3 = 1
        @ r3 = 2: if r1 = 2 and r3 = 4
        mov r4,#0 @ r4 is used as the counter of the times the sequence 1 -4 -2 appears.

_loop:


        tst r1, #1 @ TST with 1, it does a logical AND with 1.
        @ This instruction will zero out all bits but the rightmost one (LSB)
        @ 0000 0111  &  0000 0001 = 0000 0001 for odd numbers -> z flag will be cleared 
        @ 0000 0100 &   0000 0001 = 0000 0000 for even numbers Z flag will be set


        moveq r1,r1, asr #1 @ If n is even, replace it by n/2.


        @if r1 is odd: r1 = 3 r1 + 1
        addne r1, r1, r1, lsl #1
        addne r1, r1, #1


        str r1, [r0,#4]! @ Store the current n to the address that is  one word after the current address in r0 and update r0.

        @ if n == 1, set r3. to 1
        cmp r1,#1 
        moveq r3,#1
        

        @ if n == 4 and r3 is 1 ( n was 1), set r3 to 4.
        cmp     r1, #4
        cmpeq   r3, #1
        moveq   r3, #4



        @ if n == 2 and r3 is 4 ( n was 4 before at it was 1), set r3 to 2.
        cmp     r1, #2
        cmpeq   r3, #4
        moveq   r3, #2


        @ if r0 was 1 then 4 then  2, then add one to the counter r4
        cmp r3,#2
        addeq r4,#1

        @ reset r3 if the sequence 1 4 2 has been seen
        moveq r3,#0

        @ if the sequence 1 4 2 has been seen number of times that is equal to r2, then end the program.
        cmp r4,r2
        beq _end


        b       _loop

_end:   b       _end

.section .data
_n:
.word 2
_data:
.skip 100
_repetition_number:
.word 2

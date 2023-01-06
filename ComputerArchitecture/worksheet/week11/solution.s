
@@@@@@@@@@@@@@@@@@@@@@
@First method 
@@@@@@@@@@@@@@@@@@@@@@@
.section .text
.align 2
.global _start
_start:



ldr r0,=_data @ Load r0 with the address of the firs character



ldr r1,[r0]

cmp r1,#0x5
beq _end

cmp r1,#0x60 @ 0x61 lowercase a
bgt _ASCII_grater_than_0x60
ble _ASCII_not_greater_than_0x60


_ASCII_grater_than_0x60: @ this lable and braniching to it is unnecessery. The execution will reach this code block if the ASCII code is less than 60 even with out this lable. I have wrote the code this way to increase clarity.
cmp r1,#0x7B @ 0x7A lowercase z
sublt r1,#0x20 @ If ASCII is less than 0x7B, since we already know that the ASCII code is grater than 0x60 
strlt r1,[r0]


_ASCII_not_greater_than_0x60:
cmp r1,#0x20 @ test to see if the current ASCII code is of a space, if so in th next instruction we mov constant 1 to r2 or else in the last instruction of this block we move 0 to r2.
moveq r2,#0x1
movne r2,#0x0


  

_loop:


ldr r1,[r0,#4]!
cmp r1,#0x5
beq _end

cmp r1,#0x60 @ 0x61 lowercase a
bgt _ASCII_grater_than_0x60_in_loop
ble _Skip_capitalization @ NOT_ASCII_greater_than_0x60


_ASCII_grater_than_0x60_in_loop:
cmp r1,#0x7B @ 0x7A lowercase z
blt _ASCII_grater_than_0x60_and_less_than_0x7B
bgt _Skip_capitalization @NOT_ASCII_grater_than_0x60_and_less_than_0x7B
beq _Skip_capitalization @NOT_ASCII_grater_than_0x60_and_less_than_0x7B


_ASCII_grater_than_0x60_and_less_than_0x7B:
cmp r2,#0x1
beq _ASCII_grater_than_0x60_and_less_than_0x7B_and_previous_letter_is_space
bne _Skip_capitalization @ Though the ASCII code is grater_than_0x60_and_less_than_0x7B, however the previous letter is not space.



_ASCII_grater_than_0x60_and_less_than_0x7B_and_previous_letter_is_space:
sub r1,#0x20
str r1,[r0]


_Skip_capitalization:

cmp r1,#0x20
moveq r2,#0x1
movne r2,#0x0

b _loop


_end:
b _end

.section .data
_data:
  @Hi I know how to code with ARM assembly
.word 0x48, 0x69, 0x20, 0x49, 0x20, 0x6B, 0x6E, 0x6F, 0x77, 0x20, 0x68, 0x6F, 0x77, 0x20, 0x74, 0x6F, 0x20, 0x63, 0x6F, 0x64, 0x65, 0x20, 0x77, 0x69, 0x74, 0x68, 0x20, 0x41, 0x52, 0x4D, 0x20, 0x61, 0x73, 0x73, 0x65, 0x6D, 0x62, 0x6C, 0x79, 0x5
       




       






@@@@@@@@@@@@@@@@@@@@@@
@Second method 
@@@@@@@@@@@@@@@@@@@@@@@
.section .text
.align 2
.global _start
_start:

ldr r0,=_data @ Load r0 with the address of the firs character


ldr r2,[r0]

cmp r2,#0x5
beq _finish

cmp r2,#0x60 @ 0x61 lowercase a
msrle cpsr,#0x40000000
cmpgt r2,#0x7B @ 0x7A lowercase z

sublt r2,#0x20

strlt r2,[r0]




_loop:

LDMIA r0, {r1-r12} @ load 12 characters



cmp r2,#0x5
beq _finish
cmp r2,#0x60 @ 0x61 lowercase a
msrle cpsr,#0x40000000
cmpgt r2,#0x7B @ 0x7A lowercase z
msrgt cpsr,#0x0
msreq cpsr,#0x0
cmplt r1,#0x20
subeq r2,#0x20


cmp r3,#0x5
beq _finish
cmp r3,#0x60 @ 0x61 lowercase a
msrle cpsr,#0x40000000
cmpgt r3,#0x7B @ 0x7A lowercase z
msrgt cpsr,#0x0
msreq cpsr,#0x0
cmplt r2,#0x20
subeq r3,#0x20



cmp r4,#0x5
beq _finish
cmp r4,#0x60 @ 0x61 lowercase a
msrle cpsr,#0x40000000
cmpgt r4,#0x7B @ 0x7A lowercase z
msrgt cpsr,#0x0
msreq cpsr,#0x0
cmplt r3,#0x20
subeq r4,#0x20


cmp r5,#0x5
beq _finish
cmp r5,#0x60 @ 0x61 lowercase a
msrle cpsr,#0x40000000
cmpgt r5,#0x7B @ 0x7A lowercase z
msrgt cpsr,#0x0
msreq cpsr,#0x0
cmplt r4,#0x20
subeq r5,#0x20


cmp r6,#0x5
beq _finish
cmp r6,#0x60 @ 0x61 lowercase a
msrle cpsr,#0x40000000
cmpgt r6,#0x7B @ 0x7A lowercase z
msrgt cpsr,#0x0
msreq cpsr,#0x0
cmplt r5,#0x20
subeq r6,#0x20


cmp r7,#0x5
beq _finish
cmp r7,#0x60 @ 0x61 lowercase a
msrle cpsr,#0x40000000
cmpgt r7,#0x7B @ 0x7A lowercase z
msrgt cpsr,#0x0
msreq cpsr,#0x0
cmplt r6,#0x20
subeq r7,#0x20


cmp r8,#0x5
beq _finish
cmp r8,#0x60 @ 0x61 lowercase a
msrle cpsr,#0x40000000
cmpgt r8,#0x7B @ 0x7A lowercase z
msrgt cpsr,#0x0
msreq cpsr,#0x0
cmplt r7,#0x20
subeq r8,#0x20


cmp r9,#0x5
beq _finish
cmp r9,#0x60 @ 0x61 lowercase a
msrle cpsr,#0x40000000
cmpgt r9,#0x7B @ 0x7A lowercase z
msrgt cpsr,#0x0
msreq cpsr,#0x0
cmplt r8,#0x20
subeq r9,#0x20


cmp r10,#0x5
beq _finish
cmp r10,#0x60 @ 0x61 lowercase a
msrle cpsr,#0x40000000
cmpgt r10,#0x7B @ 0x7A lowercase z
msrgt cpsr,#0x0
msreq cpsr,#0x0
cmplt r9,#0x20
subeq r10,#0x20

cmp r11,#0x5
beq _finish
cmp r11,#0x60 @ 0x61 lowercase a
msrle cpsr,#0x40000000
cmpgt r11,#0x7B @ 0x7A lowercase z
msrgt cpsr,#0x0
msreq cpsr,#0x0
cmplt r10,#0x20
subeq r11,#0x20


cmp r12,#0x5
beq _finish
cmp r12,#0x60 @ 0x61 lowercase a
msrle cpsr,#0x40000000
cmpgt r12,#0x7B @ 0x7A lowercase z
msrgt cpsr,#0x0
msreq cpsr,#0x0
cmplt r11,#0x20
subeq r12,#0x20



STMIA r0!, {r1-r12} @ store the 12 modified characters

sub r0,#4

b _loop


_finish:

STMIA r0!, {r1-r12} @ store the 12 modified characters

_end: b _end



.section .data
_data:
  @Hi I know how to code with ARM assembly
.word 0x48, 0x69, 0x20, 0x49, 0x20, 0x6B, 0x6E, 0x6F, 0x77, 0x20, 0x68, 0x6F, 0x77, 0x20, 0x74, 0x6F, 0x20, 0x63, 0x6F, 0x64, 0x65, 0x20, 0x77, 0x69, 0x74, 0x68, 0x20, 0x41, 0x52, 0x4D, 0x20, 0x61, 0x73, 0x73, 0x65, 0x6D, 0x62, 0x6C, 0x79, 0x5
       
       
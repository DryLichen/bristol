.section .text
.align 2
.global _start:

_start:
    @ 3.1 The determinant of a 3 x 3 matrix - solution

    @ In this code I load four elements of each 2 x 2 sub-matrix, then I call _det_2x2 to calculate its determinant.  Then I multiply the coefficients a, b, c by the determinants of the sub-matrices. 

    @I have sacrificed code efficiency to improve its readability. You will notice each value from the sub-matrices is loaded twice!  

    ldr r0,=_matrix @ Load r0 with the address of the element a
    ldr r1, [r0 , #4 * 4] @ Load e which is at 4 words i.e 16 bytes offset from the element a 
    ldr r2, [r0 , #5 * 4] @ Load f which is at 5 words i.e 20 bytes offset from the element a 
    ldr r3, [r0 , #7 * 4] @ Load h which is at 7 words i.e 28 bytes offset from the element a 
    ldr r4, [r0 , #8 * 4] @ Load i which is at 8 words i.e 32 bytes offset from the element a 
    bl _det_2x2 @ call _det_2x2 to calculate the determinate of the first sub-matrix


    ldr r1, [r0] @ Load the content of the element a 
    mul r8,r1,r7 @ Multiply the element "a" by the determinate of the first sub-matrix

    ldr r1, [r0 , #3 * 4] @ Load d which is at 3 words i.e 12 bytes offset from the element a
    ldr r2, [r0 , #5 * 4] @ Load f which is at 5 words i.e 20 bytes offset from the element a
    ldr r3, [r0 , #6 * 4] @ Load g which is at 6 words i.e 24 bytes offset from the element a
    ldr r4, [r0 , #8 * 4] @ Load i which is at 8 words i.e 32 bytes offset from the element a
    bl _ det_2x2 @ call _det_2x2 to calculate the determinate of the second sub-matrix

    ldr r1, [r0, #1 * 4] @ Load b which is at 1 word i.e 4 bytes offset from the element a
    mul r9,r1,r7 @ Multiply the element "b" by the determinate of the second sub-matrix

    ldr r1, [r0 , #3 * 4] @ Load d which is at 3 words i.e 12 bytes offset from the element a
    ldr r2, [r0 , #4 * 4] @ Load e which is at 4 words i.e 16 bytes offset from the element a
    ldr r3, [r0 , #6 * 4] @ Load g which is at 6 words i.e 24 bytes offset from the element a
    ldr r4, [r0 , #7 * 4] @ Load h which is at 7 words i.e 28 bytes offset from the element a
    bl _ det_2x2 @ call _det_2x2 to calculate the determinate of the third sub-matrix


    @ the determinant is a * the determinant of a's sub-matrix - b * the determinant of b's sub-matrix + c * the determinant of c's sub-matrix
    @ First, we sum a * the determinant of a's sub-matrix  and c * the determinant of c's sub-matrix
    @ Then we subtract b * the determinant of b's sub-matrix from the previous result.

    ldr r1, [r0, #2 * 4] @ Load c which is at 2 words i.e 8 bytes offset from the element a
    mul r10,r1,r7 @ Multiply the element "c" by the determinate of the third sub-matrix. 

    sub r7,r8,r9  @ subtract the multiplication of the element "b" by the determinate of the second sub-matrix from the multiplication of the element "a" by the determinate of the first sub-matrix

    add r7,r7,r10 @ add the multiplication of the element "c" by the determinate of the third sub-matrix to the result from the previous operation

    b _end


_ det_2x2:

    mul r5,r1,r4
    mul r6,r2,r3
    sub r7,r5,r6

    mov pc,lr


_end: 
    b _end



.section .data
_matrix:
.word 6,1,1,4,-2,5,2,8,7 @ a,b,c,d,e,f,g,h, and i values
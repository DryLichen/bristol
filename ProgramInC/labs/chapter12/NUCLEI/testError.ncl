# Lack the starting left bracket
(

    # Wrong function name
    #(SUPER A '2')

    # Lack the left bracket for functions
    #SET A '2')

    # Lack the right bracket for functions
    #(SET A '2'

    # Fail to parse a return function
    #(CAR (IF (EQUAL '1' 1')))

    # Fail to parse a return list function
    #(CAR '31)
    #(CDR "3")
    #(CONS '2')

    # Fail to interpret a return list function
    # uninitialized variable
    #(CAR A)
    # not a lisp
    #(CAR (EQUAL '0' '0'))

    # Fail to parse a return int function
    #(PLUS C "3")
    #(LENGTH gcgv)

    # Fail to interpret a return int function
    #(SET A (PLUS '34' '-3 0'))
    #(PRINT A)
    #(LENGTH (LESS '1' '0'))

    # Fail to parse a return bool function
    #(LESS A)
    #(EQUAL A)

    # Fail to interpret a return bool function
    #(LESS A B)
    #(LESS NIL '2')

    # False format in list structure function
    #(LENGTH '(1))
    #(SET A "NIL")
    #(LENGTH (CAR '(1 2)')

    # Fail to detect a lisp list
    #(SET A '())

    # Print uninitialized variable
    #(PRINT A)

    # Wrong format of variable
    #(SET AB NIL)
    #(SET a NIL)

    # Wrong format of string to print
    #(PRINT "AD)

    # Fail to detect bool function
    #(IF (LENGTH '(2 3)') ((SET A NIL)) ((SET A NIL)))
    #(WHILE (CAR '1') (PRINT "LOOP"))

    # Wrong format of if and loop
    #(IF (LESS '-2' '0') (PRINT "A")) (PRINT "b")))
    #(WHILE (LESS '-2' '0') (PRINT "LOOP"))

    # Fail to interpret but pass parser
    #(PLUS '(1 2)' '2')
    #(PLUS NIL '2')
    #(PLUS (LESS '1' '2') '0')

    # Fail to parse but pass interpreter
    #(IF (LESS '2' '2') ((SUPER HERO)) ((PRINT "NICE")))
)
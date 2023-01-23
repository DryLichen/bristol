(   # TESTS FOR <IF>
    (PRINT "")
    (PRINT "TESTS FOR <IF>")
    (SET A '1')
    (SET B '6')
    (IF (EQUAL A B) ((PRINT "A equals to B")) ((PRINT "A doesn't equal B")))
    (IF (LESS A B) ((PRINT "A is less than B")) ((PRINT "A isn't less than B")))

    # TESTS FOR <LOOP>
    (PRINT "")
    (PRINT "TESTS FOR <LOOP>")
    (WHILE (LESS A B) (
        (PRINT A)
        (SET A (PLUS A '1'))
    ))

    #TESTS FOR ELIF
    (PRINT "")
    (PRINT "TESTS FOR <ELIF>")
    (IF (EQUAL '3' '5') (
        (PRINT "3 equals to 5")
    ) ELIF (GREATER '3' '5') (
        (PRINT "3 is greater than 5")
    ) ELIF (LESS '3' '5') (
        (PRINT "3 is less than 5")
    ) (
        (PRINT "WHAT?")
    ))

    (IF (LESS '5' '5') (
        (PRINT "5 equals to 5")
    ) ELIF (GREATER '5' '5') (
        (PRINT "5 is greater than 5")
    ) ELIF (LESS '5' '5') (
        (PRINT "5 is less than 5")
    ) (
        (PRINT "WHAT?")
    ))

    (IF (LESS '3' '5') (
        (PRINT "3 is less than 5")
    ) ELIF (GREATER '3' '5') (
        (PRINT "3 is greater than 5")
    ) ELIF (EQUAL '3' '5') (
        (PRINT "3 equals to 5")
    ) (
        (PRINT "WHAT?")
    ))
)

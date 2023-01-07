int While_a_plus_plus (int a ,int b ,int c) {
    while(a < c) {
        b = a++ + b;
    }
    return b;
}
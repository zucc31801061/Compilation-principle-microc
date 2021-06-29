void main()
{
    int i = 0;
    switch (i) {
        case 1: print 1;
        case 2: print 2;
        default: print 3; //3
    }
    switch (i) {
        case 0: print i++;//0
        case 1: print i++;//1
        case 2: print 3; //2
    }
}
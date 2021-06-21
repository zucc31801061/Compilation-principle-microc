void main(int i){
    fun(i);
}
void fun(int n){
    if(n>0){
        fun(n-1);
    }
    else{
        print n;
    }
}
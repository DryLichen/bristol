#include<stdio.h>
#define LEN 10000000
 
int memo[LEN+1]={1,1};
int sequence_length(long n);
int length_while_n_less_than_LEN(int count,long n);
int main(void){
    int max =-99;
    long target=1;
    for(long i=2;i<=LEN;i++){
        int tmp=sequence_length(i);
        if(tmp>max){
            max=tmp;
            target=i;
        }
    }
    printf("the initial number is:%ld and the sequence length is:%d\n",target,max);  
    return 0;
}
int sequence_length(long n){
    if(n<=LEN){
        return length_while_n_less_than_LEN(0,n);
    }
    int count=0;
    while(n>LEN){
        if(n%2==0){
            n=n/2;
        }else{
            n=3*n+1;
        }  
        count++;      
    }
    return length_while_n_less_than_LEN(count,n);
}
int length_while_n_less_than_LEN(int count,long n){
    if(memo[n]!=0){
        return count+memo[n];
    }
    if(n%2==0){
        memo[n]=sequence_length(n/2)+1;
        return count+memo[n];
    }else{
        memo[n]=sequence_length(3*n+1)+1;
        return count+memo[n];
    }
}
 
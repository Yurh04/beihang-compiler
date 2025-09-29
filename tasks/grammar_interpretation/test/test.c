#include <stdio.h>
int func1(){
    static int a = 0,b = 0;
    a = a + b + 1;
    return a;
}
int main(){
    int i = 0;
    int a;
    a=10;
    for(i=1;i<2;i=i+1){
        if(a==10){
            continue;
        }else{
            a=a+1;
        }
        if(a>10){
            break;
        }
    }
    for(;;){
        break;
    }
    for(;;i=i+1){
        break;
    }
    for(i=1;;i=i+1){
        break;
    }
    for(i=0;;){
        break;
    }
    for(i=0;i<10;){
        break;
    }
    for(;i<7;){
        break;
    }
    for(;i<10;i=i+1){
        break;
    }
    int j;
    for(i=0,j=5;i<10;i=i+1){
        break;
    }
    printf("22379107\n");
    printf("%d\n",func1());
    printf("%d\n",func1()+i);
    printf("%d\n",10);
    {
        int i=10;
        printf("%d\n",i);
    }
    {}
    printf("%d\n",10);
    printf("%d\n",10);
    printf("%d\n",1*a+func1());
    printf("%d\n",i*a+10);
    printf("%d\n",func1()*3);
    return 0;
}
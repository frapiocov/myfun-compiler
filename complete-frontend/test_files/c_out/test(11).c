#include<stdio.h>
#include<stdlib.h>
#include<math.h>
#include<stdbool.h>
#include<stdlib.h>
#include<stddef.h>
#include<string.h>
#define STRING 100
char BUFFER[STRING];
char STRING_CAT[STRING];
char STRING_CAT_1[STRING];
char* concatena(char* dest, char* src){
    strcat(strcat(STRING_CAT, dest),src);
    strcpy(STRING_CAT_1,STRING_CAT);
    strcpy(STRING_CAT,"");
    return STRING_CAT_1;
}
char* convertInt(int intero){
    char * num = malloc(sizeof(char)*STRING);
    itoa(intero,BUFFER,10);
    strcpy(num,BUFFER);
    return num;
}
char* convertReal(double real){
    char * num = malloc(sizeof(char)*STRING);
    gcvt(real,10,BUFFER);
    strcpy(num,BUFFER);
    return num;
}

int main(int argc, char *argv[]){
int first = 0;

int second = 1;

int n;

int sum = 0;

int i = 0;

printf("%s ","Inserire la taglia: ");
scanf("%d", &n);
printf("%s ","Serie di Fibonacci: ");
while(i < n){

if(i <= 1){

sum = i;

} else {

sum = first + second;
first = second;
second = sum;

}
printf("%d\t",sum);
i = i + 1;

}

 return 0;}
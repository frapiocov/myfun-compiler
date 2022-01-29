# myfun-compiler
Iuliano Gerardo - Covino Francesco Pio

## 1. Hand Coded Lexer & Parser

Project SDK : jdk 15.0.1
Project Language Level: 15

### Used Grammar for hand-coded parser
Non Terminal = {S, Program, Stmt, Expr}  
Terminal = {EOF. ';', IF, THEN, ELSE, END, ID, ASSIGN, WHILE, LOOP, RELOP, NUMBER}  
S first production  
Productions:
```
 P = {
          S -> Program  EOF

          Program -> Program ; Stmt
                           |  Stmt
           Stmt -> IF Expr THEN Stmt ELSE Stmt END IF
                     | ID ASSIGN Expr

                     | WHILE Epr LOOP Stmt END LOOP

          Expr ->  Expr  RELOP Expr

          Expr ->   ID
                     |  NUMBER
       }
```

### Modified grammar for top-down parsing
```
P = {
        S -> Program  EOF
        Program -> Stmt R
        R -> ; Stmt R
                   | epsilon
  
        Stmt -> IF Expr THEN Stmt ELSE Stmt END IF
                   | ID ASSIGN Expr
                   | WHILE Epr LOOP Stmt END LOOP

        Expr -> ID Expr_
                   | NUMBER Expr_
        Expr_ -> RELOP Expr Expr_
                   | epsilon
     }   
```

### Lexical specifications: token list

1. Delimiters ( white spaces, tab `\t`,  `\r`, new line `\n`)
2. KeyWords ( `IF, ELSE, WHILE, FOR, RETURN, INT, DOUBLE...`)
3. Id
4. Literals ( whole numbers and decimals, the lexeme 1. is approximated to 1.0 while the lexeme .0 is considered an error. The Scientific notation has not been considered)
5. Separators ( round brackets `( )`, curly brackets `{ }`, comma `,`, semicolon `;`)
6. Relational Operators ( `>, <, <>, <=, =, >=` ), assign operation `<--`
7. Arithmetic Operators (`+ - / * %`), increase and decrease operators (`++ --`)

## 2. Complete front-end compiler
Programming language defined for the Compilers course year 2021/2022.
The compiler integrates the use of JFlex and JavaCup for the implementation of the Lexer (for lexical analysis)
and the Parser (for syntactic analysis).

The Class MyFun2C puts together the languages MyFun and C: it is a complete compiler that takes in input a MyFun code, after to have carried out the analysis
lexical, syntactic and semantic analysis, it compiles it into a C program. Once the compilation is complete, a terminal is opened from which the user must first change directory
with ``cd test_files`` and subsequently with ``cd exe_out`` and then start the application with ``<file_name>.exe`` and pressing enter. This allows you to dynamically interact with the program.


## Lexical specifications

| Token | Lexeme |
|-------|--------|
| FUN | "fun" |
| MAIN | "main" |
| LPAR | "(" |
| RPAR | ")" |
| END | "end" |
| ID | Fun id format |
| COLON | ":" |
| SEMI | ";" |
| COMMA | "," |
| NULL | "null" |
| VAR | "var" |
| OUT | "out" |
| OUTPAR | "@" |
| INTEGER | "integer" |
| BOOL | "bool" |
| REAL | "real" |
| STRING | "string" |
| BLPAR | "{" |
| BRPAR | "}" |
| ASSIGN | ":=" |
| WHILE | "while" |
| LOOP | "loop" |
| DO | "do" |
| IF | "if" |
| THEN | "then" |
| ELSE | "else" |
| READ | "%" |
| WRITE | "?" |
| WRITELN | "?." |
| WRITEB | "?," |
| WRITET | "?:" |
| RETURN | "return" |
| TRUE | "true" |
| FALSE | "false" |
| STR_CONCAT | "&" |
| PLUS | "+" |
| MINUS | "-" |
| TIMES | "*" |
| DIV | "/" |
| DIVINT | "div" |
| POW | "^" |
| AND | "and" |
| OR | "or" |
| GT | ">" | 
| GE | ">=" |
| LT | "<" |
| LE | "<=" |
| EQ | "=" |
| NE | "!=" |
| NE | "<>" |
| NOT | "not" |

## Syntactic Specifications: Grammar

```

Program ::= VarDeclList FunList Main:;

        VarDeclList ::= /* empty */
        | VarDecl VarDeclList
        ;

        Main ::= MAIN VarDeclList StatList END MAIN SEMI
        ;

        FunList ::= /* empty */
        | Fun FunList
        ;

        VarDecl ::= Type IdListInit SEMI
        | VAR IdListInitObbl SEMI
        ;

        Type ::= INTEGER
        | BOOL
        | REAL
        | STRING
        ;

        IdListInit ::= ID
        | IdListInit COMMA ID
        | ID ASSIGN Expr
        | IdListInit COMMA ID ASSIGN Expr
        ;

        IdListInitObbl ::= ID ASSIGN Const
        | IdListInitObbl COMMA ID ASSIGN Const
        ;

        Const ::= INTEGER_CONST
        | REAL_CONST
        | TRUE
        | FALSE
        | STRING_CONST
        ;

        Fun ::= FUN ID LPAR ParamDeclList RPAR COLON Type
        VarDeclList StatList END FUN SEMI
        | FUN ID LPAR ParamDeclList RPAR
        VarDeclList StatList END FUN SEMI
        ;

        ParamDeclList ::= /*empty */
        | NonEmptyParamDeclList
        ;

        NonEmptyParamDeclList ::= ParDecl
        | NonEmptyParamDeclList COMMA ParDecl
        ;

        ParDecl ::= Type ID
        | OUT Type ID
        ;

        StatList ::= Stat
        | Stat StatList
        ;

        Stat ::= IfStat SEMI
        | WhileStat SEMI
        | ReadStat SEMI
        | WriteStat SEMI
        | AssignStat SEMI
        | CallFun SEMI
        | RETURN Expr SEMI
        | /* empty */
        ;

        IfStat ::= IF Expr THEN VarDeclList StatList Else END IF
        ;

        Else ::= /* empty */
        | ELSE VarDeclList  StatList
        ;

        WhileStat ::= WHILE Expr LOOP VarDeclList StatList END LOOP
        ;

        ReadStat ::= READ IdList Expr
        | READ IdList
        ;

        IdList ::= ID
        | IdList COMMA ID
        ;

        WriteStat ::=  WRITE  Expr
        | WRITELN  Expr
        | WRITET  Expr
        | WRITEB  Expr
        ;

        AssignStat ::=  ID ASSIGN Expr
        ;

        CallFun ::= ID LPAR ExprList RPAR
        | ID LPAR RPAR
        ;

        ExprList ::= Expr
        | Expr COMMA ExprList
        | OUTPAR ID
        | OUTPAR ID COMMA ExprList
        ;

        Expr ::= TRUE
        | FALSE
        | INTEGER_CONST
        | REAL_CONST
        | STRING_CONST
        | ID
        | CallFun
        | Expr PLUS Expr
        | Expr MINUS Expr
        | Expr TIMES Expr
        | Expr DIV Expr
        | Expr DIVINT Expr
        | Expr AND Expr
        | Expr POW Expr
        | Expr STR_CONCAT Expr
        | Expr OR Expr
        | Expr GT Expr
        | Expr GE Expr
        | Expr LT Expr
        | Expr LE Expr
        | Expr EQ Expr
        | Expr  NE Expr
        | MINUS Expr
        %prec UMINUS
        | NOT Expr
        | LPAR Expr RPAR
        ;
```
### Inference rules
They can be found in the inference_rules.pdf file in the repository [here](complete-frontend\inference_rules.pdf).

### C code generation: from front-end to back-end
Using the Visitor pattern, the C code equivalent to the MyFun code is generated. In order to adapt at best and to favor the correctness of the
generation of C code, we have prepared some functions and variables that are added by default at the beginning of every generated C program.
1. Declaration of the main libraries of C  
```
#include<stdio.h>
#include<stdlib.h>
#include<math.h>
#include<stdbool.h>
#include<stdlib.h>
#include<stddef.h>
#include<string.h>
```    
2. MACRO that defines the default space to allocate for strings ```#define STRING 100 ```  
3. Global variables used for concatenation and conversion
```  
char BUFFER[STRING];
char STRING_CAT[STRING];
char STRING_CAT_1[STRING];
```  
4. A **concat** function that takes care of concatenating two arrays of characters
```  
char* concatena(char* dest, char* src){
    strcat(strcat(STRING_CAT, dest),src);
    strcpy(STRING_CAT_1,STRING_CAT);
    strcpy(STRING_CAT,"");
    return STRING_CAT_1;
}
```  
5. A **convertInt** function to convert an integer into an array of characters
```  
char* convertInt(int intero){
    char * num = malloc(sizeof(char)*STRING);
    itoa(intero,BUFFER,10);
    strcpy(num,BUFFER);
    return num;
}
```  
6. A **convertReal** function to convert a real into an array of characters
```  
char* convertReal(double real){
    char * num = malloc(sizeof(char)*STRING);
    gcvt(real,10,BUFFER);
    strcpy(num,BUFFER);
    return num;
}
```  

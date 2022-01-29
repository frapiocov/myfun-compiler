/*
	Program to implement Recursive Descent Parser in Java
	Author: Iuliano-Covino


	Grammar:
 N = {S, Program, Stmt, Expr},
 T = {EOF, ';', IF, THEN, ELSE, END, ID, ASSIGN, WHILE, LOOP, RELOP, NUMBER},
 S
 P = {
          S -> Program  EOF                                           S -> Program  EOF

          Program -> Program ; Stmt                                   Program -> Stmt R
          Program -> Stmt                                    --->     R -> ; Stmt R
                                                                      R -> epsilon

          Stmt -> IF Expr THEN Stmt ELSE Stmt END IF                  Stmt -> IF Expr THEN Stmt ELSE Stmt END IF
          Stmt -> ID ASSIGN Expr                                      Stmt -> ID ASSIGN Expr
          Stmt -> WHILE Epr LOOP Stmt END LOOP                        Stmt -> WHILE Epr LOOP Stmt END LOOP

          Expr ->  Expr  RELOP Expr                                   Expr -> ID Expr_
          Expr ->   ID                                       --->     Expr -> NUMBER Expr_
          Expr ->   NUMBER                                            Expr_ -> RELOP Expr Expr_
                                                                      Expr_ -> epsilon
       }

    S -> Program
    Program -> Program ; Stmt
    Program -> Stmt
                                    Program è ricorsiva sx
    Program -> Stmt (; Stmt)*
                                    Soluzione
    Program -> Stmt R
    R -> ; Stmt R
    R -> epsilon

 ------------------------------------------------------
    Stmt -> IF Expr THEN Stmt ELSE Stmt END IF
    Stmt -> ID Assign Expr
    Stmt -> WHILE Expr LOOP Stmt END LOOP
-----------------------------------------------------
    Expr ->  Expr  RELOP Expr
    Expr ->   ID
    Expr ->   NUMBER
                                    Expr è ricorsiva sx
    Expr -> Expr (RELOP Expr)*
                                    Soluzione
    Expr -> ID E'
    Expr -> NUMBER E'
    E' -> RELOP Expr E'
    E' -> epsilon

 */

import java.util.*;

public class Parser {
  static int ptr;
  static ArrayList<Token> tokensInput = new ArrayList<>();

  public static void main(String[] args) {
    Lexer lexer = new Lexer();
    lexer.initialize(args[0]);
    Token token;
    try {
      while( !((token = lexer.nextToken()).getName().equals("EOF")) ){
        tokensInput.add(token);
        System.out.println("TOKEN: " + token);
      }
      tokensInput.add(token); // aggiunge il token EOF a fine lettura
      System.out.println("TOKEN: " + token);
    } catch(Exception e) {
      e.printStackTrace();
    }
    ptr = 0;

    boolean isValid = S(); //viene applicata la grammatica

    if((isValid) & (ptr+1 == tokensInput.size())) {
      System.out.println("The input is VALID.");
    } else {
      System.out.println("The input present a SINTAX ERROR.");
    }
  }

  // Insieme delle funzioni
  // Una funzione per ogni produzione della grammatica

  /**
   * S-> Program EOF
   */
  static boolean S(){
    int fallback = ptr;

    if(!Program()) {
      ptr = fallback;
      return false;
    }
    if(!tokensInput.get(ptr).getName().equals("EOF")) {
      ptr = fallback;
      return false;
    }
    return true;
  }

  /**
   * Program -> Stmt R
   */
  static boolean Program(){
    int fallback = ptr;
    if(!Stmt()){
      ptr = fallback;
      return false;
    }
    if(!R()){
      ptr = fallback;
      return false;
    }
    return true;
  }

  /**
   *    R -> ; Stmt R
   *    R -> epsilon
   */
  static boolean R(){
    int fallback = ptr;
    boolean result = false;
    if(tokensInput.get(ptr).getName().equals(";")) {
      ptr++;
      if(Stmt()) {
        if (R()) {
          result = true;
        } else {
          ptr = fallback;
        }
      } else {
        ptr = fallback;
      }
    } else {
      result = true;
    }
    return result;
  }

  /**
   Stmt -> IF Expr THEN Stmt ELSE Stmt END IF
   Stmt -> ID ASSIGN Expr
   Stmt -> WHILE Epr LOOP Stmt END LOOP
   */
  static boolean Stmt(){
    int fallback = ptr;
    switch (tokensInput.get(ptr).getName()) {
      case "IF" -> {            // Stmt -> IF Expr THEN Stmt ELSE Stmt END IF
        ptr++;
        if (!Expr()) {
          ptr = fallback;
          return false;
        }
        if (!tokensInput.get(ptr++).getName().equals("THEN")) {
          ptr = fallback;
          return false;
        }
        if (!Stmt()) {
          ptr = fallback;
          return false;
        }
        if (!tokensInput.get(ptr++).getName().equals("ELSE")) {
          ptr = fallback;
          return false;
        }
        if (!Stmt()) {
          ptr = fallback;
          return false;
        }
        if (!tokensInput.get(ptr++).getName().equals("END")) {
          ptr = fallback;
          return false;
        }
        if (!tokensInput.get(ptr++).getName().equals("IF")) {
          ptr = fallback;
          return false;
        }
        return true;
      }
      case "ID" -> {       //Stmt -> ID ASSIGN Expr
        ptr++;
        if (!tokensInput.get(ptr++).getName().equals("ASSIGN")) {
          ptr = fallback;
          return false;
        }
        if (!Expr()) {
          ptr = fallback;
          return false;
        }
        return true;
      }
      case "WHILE" -> {     //Stmt -> WHILE Epr LOOP Stmt END LOOP
        ptr++;
        if (!Expr()) {
          ptr = fallback;
          return false;
        }
        if (!tokensInput.get(ptr++).getName().equals("LOOP")) {
          ptr = fallback;
          return false;
        }
        if (!Stmt()) {
          ptr = fallback;
          return false;
        }
        if (!tokensInput.get(ptr++).getName().equals("END")) {
          ptr = fallback;
          return false;
        }
        if (!tokensInput.get(ptr++).getName().equals("LOOP")) {
          ptr = fallback;
          return false;
        }
        return true;
      }
    }
    return false;
  }

  /**
   * Expr -> ID Expr_
   * Expr -> NUMBER Expr_
   */
  static boolean Expr() {
    int fallback = ptr;
    if(tokensInput.get(ptr).getName().equals("ID")){
      ptr++;
      if(!Expr_()){
        ptr = fallback;
        return false;
      }
      return true;
    } else if(tokensInput.get(ptr).getName().equals("NUMBER")){
      ptr++;
      if(!Expr_()){
        ptr = fallback;
        return false;
      }
      return true;
    } else {
      return false;
    }
  }

  /**
   * Expr_ -> RELOP Expr Expr_
   * Expr_ -> epsilon
   */
  static boolean Expr_(){
    int fallback = ptr;
    if(tokensInput.get(ptr).getName().equals("RELOP")){
      ptr++;
      if (!Expr()){
        ptr = fallback;
        return false;
      }
      if(!Expr_()){
        ptr = fallback;
        return false;
      }
    }
    return true;
  }

}
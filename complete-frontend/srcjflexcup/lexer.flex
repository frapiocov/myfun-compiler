// Lexer JFlex
// C:\JFLEX\bin\jflex -d src srcjflexcup\lexer.flex
// Iuliano - Covino
import java_cup.runtime.Symbol;
import lexical.StringTable;
import java.io.EOFException;


%%

// dichiarazioni per JFlex
%unicode
%class Lexer
%cupsym ParserSym
%cup 	// Dichiara di voler usare Java CUP
%line
%column

%{
    private StringBuffer string = new StringBuffer();
        private StringTable table;

           private Symbol generateTokenSym(int type) {
             return new Symbol(type);
           }
           private Symbol generateTokenSym(int type, Object value) {
             this.table.install(value.toString());
             return new Symbol(type, value);
           }

           // prepara file input per lettura e controlla errori
           public boolean initialize(String filePath) {
             try {
               this.zzReader = new java.io.FileReader(filePath);
               return true;
             } catch (java.io.FileNotFoundException e) {
               return false;
             }
           }

           public Lexer(StringTable table) {
             this.table = table;
           }

%}

%eofval{
	return generateTokenSym(ParserSym.EOF);
%eofval}


Identifier = [$_A-Za-z][$_@A-Za-z0-9]*

Integer = 0 | [1-9][0-9]*

Real = ((\+|-)?(0|[1-9]+)(\.[0-9]+))

CommentLine = {StartCommentLine} {CommentDescription}

WhiteSpace = {LineTerminator} | [ \t\f]

LineTerminator = \r|\n|\r\n
StartCommentLine = # | "//"
CommentDescription = .*


%state STRING1
%state STRING2
%state COMMENT_BLOCK

%%

// token e azioni ad essi associate
<YYINITIAL> {
/* Parole chiavi*/
"main" {return generateTokenSym( ParserSym.MAIN ); }
"fun" {return generateTokenSym( ParserSym.FUN ); }
"end" {return generateTokenSym( ParserSym.END ); }
"if" {return generateTokenSym( ParserSym.IF ); }
"then" {return generateTokenSym( ParserSym.THEN ); }
"else" {return generateTokenSym( ParserSym.ELSE ); }
"while" {return generateTokenSym( ParserSym.WHILE ); }
"true" {return generateTokenSym( ParserSym.TRUE, Boolean.parseBoolean("true")); }
"false" {return generateTokenSym( ParserSym.FALSE, Boolean.parseBoolean("false")); }
"null" {return generateTokenSym( ParserSym.NULL ); }
"return" { return generateTokenSym(ParserSym.RETURN ); }
"var" {return generateTokenSym(ParserSym.VAR); }
"out" {return generateTokenSym(ParserSym.OUT); }
"loop" {return generateTokenSym(ParserSym.LOOP); }
"@" {return generateTokenSym(ParserSym.OUTPAR);}

/* Assign */
":=" {return generateTokenSym( ParserSym.ASSIGN);}

/* Types Keywords */
"integer" {return generateTokenSym( ParserSym.INTEGER); }
"string" {return generateTokenSym( ParserSym.STRING); }
"real" {return generateTokenSym( ParserSym.REAL); }
"bool" {return generateTokenSym( ParserSym.BOOL); }

/* Separator */
"(" {return generateTokenSym(ParserSym.LPAR);}
")" {return generateTokenSym(ParserSym.RPAR);}
"," {return generateTokenSym(ParserSym.COMMA);}
";" {return generateTokenSym(ParserSym.SEMI);}
":" {return generateTokenSym(ParserSym.COLON);}

/* RelOp */
"and" { return generateTokenSym(ParserSym.AND); }
"or" { return generateTokenSym(ParserSym.OR); }
"not" {return generateTokenSym(ParserSym.NOT); }
"&" { return generateTokenSym(ParserSym.STR_CONCAT); }
"<" { return generateTokenSym(ParserSym.LT); }
"<=" { return generateTokenSym(ParserSym.LE); }
"<>" { return generateTokenSym(ParserSym.NE); }
"!=" { return generateTokenSym(ParserSym.NE); }
">" { return generateTokenSym(ParserSym.GT); }
">=" { return generateTokenSym(ParserSym.GE); }
"=" { return generateTokenSym(ParserSym.EQ); }

/* Arithmetic Op*/
"+" { return generateTokenSym(ParserSym.PLUS); }
"-" { return generateTokenSym(ParserSym.MINUS); }
"*" { return generateTokenSym(ParserSym.TIMES); }
"/" { return generateTokenSym(ParserSym.DIV); }
"div" { return generateTokenSym(ParserSym.DIVINT); }
"^" { return generateTokenSym(ParserSym.POW); }

/* Input Output*/
"%" {return generateTokenSym( ParserSym.READ);}
"?" {return generateTokenSym( ParserSym.WRITE);}
"?." {return generateTokenSym( ParserSym.WRITELN);}
"?," {return generateTokenSym( ParserSym.WRITEB);}
"?:" {return generateTokenSym( ParserSym.WRITET);}

/* String */
\" { string.setLength(0); yybegin(STRING1); }
"'" { string.setLength(0); yybegin(STRING2); }

/* Comment block */
"#*" {yybegin(COMMENT_BLOCK);}

/* Identifier */
{Identifier} { return generateTokenSym(ParserSym.ID, yytext()); }

/* Integer Constant*/
{Integer} {return generateTokenSym(ParserSym.INTEGER_CONST, Integer.parseInt(yytext()));}

/* Real Constant*/
{Real} {return generateTokenSym(ParserSym.REAL_CONST, Float.parseFloat(yytext()));}

/* Comment line */
{CommentLine} {/* ignore */}

/* Whitespace */
{WhiteSpace} {/* ignore */}

<<EOF>> { return generateTokenSym(ParserSym.EOF); }
}

<COMMENT_BLOCK> {
    # { /* chiusura commento */
        yybegin(YYINITIAL);
        /* ignore */
    }
    [^#] {
    /* ignore */
    }
    <<EOF>> {throw new EOFException("Commento non chiuso");}
}

<STRING1> {
  \" {
    yybegin(YYINITIAL);
    return generateTokenSym(ParserSym.STRING_CONST, string.toString());
  }
  [^\r\n\"\\]+ { string.append( yytext() ); }
  \t { string.append('\t'); }
  \n { string.append('\n'); }
  \r { string.append('\r'); }
  \' { string.append('\''); }
  \\  { string.append('\\'); }
  <<EOF>> {throw new EOFException("Stringa costante non completata"); }
}

<STRING2> {
  ' {
    yybegin(YYINITIAL);
    return generateTokenSym(ParserSym.STRING_CONST, string.toString());
  }
  [^'\n\r\\]+ { string.append( yytext() ); }
  \t { string.append('\t'); }
  \n { string.append('\n'); }
  \r { string.append('\r'); }
  \" { string.append('\"'); }
  \\  { string.append('\\'); }
  <<EOF>> {throw new EOFException("Stringa costante non completata");}
}

[^] { throw new RuntimeException("Error:(" + (yyline+1) + ":" + (yycolumn+1) + ") Simbolo non riconosciuto '"+yytext()+"'");}
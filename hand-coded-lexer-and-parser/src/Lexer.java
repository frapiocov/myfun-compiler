import java.io.*;
import java.util.HashMap;

public class Lexer {

  private File input; // file di input
  private static HashMap stringTable;  // hash map come tabella delle stringhe
  private int state; // memorizza lo stato corrente
  private int lastChar; // memorizza l'ultimo carattere letto prima del retrack
  private Reader reader; // per leggere il file carattere per carattere
  private Boolean flagRetract = false; // indica se è avvenuto il retrack
  private Boolean EOF = false; // supporta la gestione dell'EOF
  private char c; // memorizza il carattere letto corrente

  /* Costruttore, inizializza tutte gli attributi della classe */
  public Lexer(){
    stringTable = new HashMap();
    state = 0; // stato iniziale
    // riempimento della tabella delle stringhe con le keyword
    stringTable.put("<--", new Token("ASSIGN"));
    stringTable.put("if", new Token("IF"));
    stringTable.put("else", new Token("ELSE"));
    stringTable.put("do", new Token("DO"));
    stringTable.put("sizeof", new Token("SIZEOF"));
    stringTable.put("boolean", new Token("BOOLEAN"));
    stringTable.put("break", new Token("BREAK"));
    stringTable.put("case", new Token("CASE"));
    stringTable.put("catch", new Token("CATCH"));
    stringTable.put("char", new Token("CHAR"));
    stringTable.put("const", new Token("CONST"));
    stringTable.put("continue", new Token("CONTINUE"));
    stringTable.put("double", new Token("DOUBLE"));
    stringTable.put("for", new Token("FOR"));
    stringTable.put("switch", new Token("SWITCH"));
    stringTable.put("int", new Token("INT"));
    stringTable.put("void", new Token("VOID"));
    stringTable.put("while", new Token("WHILE"));
    stringTable.put("static", new Token("STATIC"));
    stringTable.put("return", new Token("RETURN"));
    stringTable.put("then", new Token("THEN"));
    stringTable.put("end", new Token("END"));
    stringTable.put("loop", new Token("LOOP"));
    stringTable.put(";",new Token(";"));
  }

  /* Prepara il file di input alla lettura carattere per carattere */
  public Boolean initialize(String filePath){
    try {
      input = new File(filePath);
      reader = new InputStreamReader(new FileInputStream(input));
      return true;
    } catch (FileNotFoundException e) {
      System.out.println("Errore lettura file di input: " + input.getName());
      e.printStackTrace();
      return false;
    }
  }

  /* Individua iterativamente il prossimo token nel file */
  public Token nextToken()throws Exception {
    // Se siamo nell'EOF la ricerca di token termina
    if(EOF) {
      EOF = false;
      return new Token("EOF");
    }

    // ad ogni chiamata del metodo lexer.nextToken()
    // vengono resettate tutte le variabili utilizzate
    state = 0;
    String lessema = ""; // il lessema riconosciuto
    c = 0;
    int carattere=0; // di ausilio per la lettura del carattere

    while (true) {
      // controlla se è stato richiamato il retrack
      if(flagRetract) {
        carattere = lastChar; // riprende il carattere precedente
        flagRetract = false;
      } else {
        lastChar = carattere;
        carattere = nextChar(); // continuo la lettura col carattere successivo
      }

      // controlla se il carattere appena letto è l'EOF
      if (carattere == -1) {
        EOF = true;
        switch (state) { // in base allo stato in cui si trovava in precedenza decide quale token tornare dopo l'EOF
          case 0: return new Token("EOF");
          case 1: return installID(lessema);
          case 13: return installNUMBER(lessema);
          case 3,4,5: return installRELOP(lessema);
        }
      } else { // in alternativa memorizza il carattere successivo in c
        c = (char) carattere;
      }

      /* Switch principale: se il primo carattere è
       * una lettera --> identifier, keyword
       * una cifra --> numero intero o decimale
       * un operatore di confronto
       * uno delimitatore ( spazio, ritorno a capo o tab )
       * un separatore
       * */
      switch (state) {
        // in base al primo carattere instrada il metodo
        case 0:
          if( Character.isLetter(c) ){ // identifier o keyword
            lessema += c;
            state = 1;
          } else if( Character.isDigit(c) ){ // numero intero o decimale (unsigned)
            lessema += c;
            state = 13;
          } else if( c == '>' ){ // operatore >
            lessema += c;
            state = 3;
          }else if( c == '=' ){ // operatore =
            lessema += c;
            return installRELOP(lessema);
          }else if( c == '<' ){ // operatore <
            lessema += c;
            state = 5;
          } else if((c == ' ') || (c == '\t') || (c == '\n') || (c == '\r')) { //delimitatori, appositamente ignorati
            break;
          } else if( c == '{' || c == '}' || c == '(' || c == ')' || c == ',' || c == ';' ){ //separatori
            lessema += c;
            return installSEPARATOR(lessema);
          } else if(c == '+' || c=='-' ){ // operatori aritmetici (++, --, +, -)
            lessema += c;
            state = 2;
          }else if(c == '/' || c=='*' || c == '%' ){ // operatori aritmetici (/, *, %)
            lessema += c;
            return installARITHOP(lessema);
          }else{
            throw new Exception("Carattere non valido: " + c);//se il carattere non è riconosciuto viene lanciata un'eccezione
          }
          break;
        /* identificatore o keyword */
        case 1:
          if(Character.isLetter(c)){ // finchè è una lettera torna nello stato 1
            state = 1;
            lessema += c;
          } else {
            retrack();
            return installID(lessema);
          }
          break;

        case 2:
          if(c == '+'){
            lessema += c;
            return installARITHOP(lessema);
          }else if(c == '-'){
            lessema += c;
            return installARITHOP(lessema);
          }else{
            retrack();
            return installARITHOP(lessema);
          }

          /* numero intero o decimale */
        case 13:
          if(Character.isDigit(c)){ // finché è una cifra torna nello stato 13
            lessema += c;
            state = 13;
          } else if(c == '.') { // il numero è decimale
            lessema += c;
            state = 14;
          } else {
            retrack();
            return installNUMBER(lessema);
          }
          break;
        // numero decimale con (.)
        case 14:
          if(Character.isDigit(c)){ // cifre dopo la virgola
            lessema += c;
            state = 15;
          } else { //se non è presente un numero dopo la virgola inserisce di default uno 0 e ritorna il token
            lessema += '0';
            retrack();
            return installNUMBER(lessema);
          }
          break;
        // cifre decimali dopo il .
        case 15:
          if(Character.isDigit(c)){
            lessema += c;
            state = 15;
          } else {
            retrack();
            return installNUMBER(lessema);
          }
          break;
        // operatore >
        case 3:
          if ( c == '=' ) { // operatore >=
            lessema += c;
            return installRELOP(lessema);
          } else {
            retrack();
            return installRELOP(lessema);
          }
        case 4:
          if(c == '-'){
            lessema += c;
            return installRELOP(lessema);
          }else {
            throw new Exception("Carattere non valido: " + c);
          }
          // operatore <
        case 5:
          if( c == '=' ){ // operatore <=
            lessema += c;
            return installRELOP(lessema);
          } else if( c == '>' ){ // operatore <>
            lessema += c;
            return installRELOP(lessema);
          } else if(c == '-'){
            lessema += c;
            state=4;
          }
          else{
            retrack();
            return installRELOP(lessema);
          }
        default: break; // caso di default
      }
    }
  }

  /* ritorna un token ID */
  private Token installID (String lessema){
    Token token;
    if (stringTable.containsKey(lessema))
      return (Token) stringTable.get(lessema);
    else {
      token = new Token("ID", lessema);
      stringTable.put(lessema, token);
      return token;
    }
  }
  /* ritorna un token RELOP operatori relazionali */
  private Token installRELOP (String lessema){
    Token token;
    if (stringTable.containsKey(lessema))
      return (Token) stringTable.get(lessema);
    else {
      token = new Token("RELOP", lessema);
      stringTable.put(lessema, token);
      return token;
    }
  }
  /* ritorna un token ARITHOP operatori aritmetici */
  private Token installARITHOP (String lessema){
    Token token;
    if (stringTable.containsKey(lessema)) {
      return (Token) stringTable.get(lessema);
    }
    else {
      token = new Token("ARITHOP", lessema);
      stringTable.put(lessema, token);
      return token;
    }
  }

  /* ritorna il token per un numero intero o decimale */
  private Token installNUMBER (String lessema){
    Token token;
    if (stringTable.containsKey(lessema))
      return (Token) stringTable.get(lessema);
    else {
      token = new Token("NUMBER", lessema);
      stringTable.put(lessema, token);
      return token;
    }
  }
  /* ritorna il token per un separatore */
  private Token installSEPARATOR (String lessema){
    Token token;
    if (stringTable.containsKey(lessema))
      return (Token) stringTable.get(lessema);
    else {
      token = new Token("SEPARATOR", lessema);
      stringTable.put(lessema, token);
      return token;
    }
  }
  /* ritorna al carattere precedente per processarlo */
  private void retrack () {
    flagRetract = true;
    lastChar = c; // memorizza l'ultimo carattere letto
  }
  /* ritorna il successivo carattere letto dal file */
  private int nextChar () throws IOException {
    return reader.read();
  }
}
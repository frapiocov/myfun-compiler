package semantic;

import java.util.Optional;

/**
 * SymbolTable
 */
public interface SymbolTable {

  void enterScope();

  void exitScope();

  int getScopeLevel();

  boolean probe(String lexeme);

  Optional<SymbolTableRecord> lookup(String lexeme);

  void addEntry(String lexeme, SymbolTableRecord str);

}
package nodetype;

import error.ErrorHandler;

import java.util.ArrayList;
import java.util.List;

public class TypeTable {

  private final List<TypeTableRecord> typeTable;

  private static final NodeType integer = new PrimitiveNodeType("integer");
  private static final NodeType real = new PrimitiveNodeType("real");
  private static final NodeType string = new PrimitiveNodeType("string");
  private static final NodeType bool = new PrimitiveNodeType("bool");


  public TypeTable() {
    this.typeTable = new ArrayList<>();

    /* ARITH_OP => ADD,DIFF,DIV,MUL,POW */
    this.typeTable.add(new TypeTableRecord("ARITH",integer,integer,integer));
    this.typeTable.add(new TypeTableRecord("ARITH",integer,real,real));
    this.typeTable.add(new TypeTableRecord("ARITH",real,integer,real));
    this.typeTable.add(new TypeTableRecord("ARITH",real,real,real));

    this.typeTable.add(new TypeTableRecord("DIVINT",integer,integer,integer));
    this.typeTable.add(new TypeTableRecord("DIVINT",integer,real,integer));
    this.typeTable.add(new TypeTableRecord("DIVINT",real,integer,integer));
    this.typeTable.add(new TypeTableRecord("DIVINT",real,real,integer));

    this.typeTable.add(new TypeTableRecord("STRCAT",string,string,string));
    this.typeTable.add(new TypeTableRecord("STRCAT",string,integer,string));
    this.typeTable.add(new TypeTableRecord("STRCAT",integer,string,string));
    this.typeTable.add(new TypeTableRecord("STRCAT",string,real,string));
    this.typeTable.add(new TypeTableRecord("STRCAT",real,string,string));

    /* REL_OP => EQ,LT,LE,GT,GE */
    this.typeTable.add(new TypeTableRecord("REL",integer,integer,bool));
    this.typeTable.add(new TypeTableRecord("REL",integer,real,bool));
    this.typeTable.add(new TypeTableRecord("REL",real,integer,bool));
    this.typeTable.add(new TypeTableRecord("REL",real,real,bool));
    this.typeTable.add(new TypeTableRecord("REL",string,string,bool));

    this.typeTable.add(new TypeTableRecord("AND",bool,bool,bool));
    this.typeTable.add(new TypeTableRecord("OR",bool,bool,bool));

    //Unary operation uminus, minus, not
    this.typeTable.add(new TypeTableRecord("NOT",bool,null,bool));
    this.typeTable.add(new TypeTableRecord("MINUS",integer,null,integer));
    this.typeTable.add(new TypeTableRecord("MINUS",real,null,real));
  }

  public NodeType check(String op, NodeType arg1, NodeType arg2){
    for(TypeTableRecord t : this.typeTable){
      if(t.equals(new TypeTableRecord(op,arg1,arg2,null))){
        return t.getResult();
      }
    }
    new ErrorHandler("Errore di tipo" + ", operazione: " + op + ", arg1: " + arg1.toString() + ", arg2: " + arg2.toString());
    return new PrimitiveNodeType("error");
  }

  public NodeType check(String op, NodeType arg1){
    for(TypeTableRecord t : this.typeTable){
      if(t.getOp().equals(op) && t.getType1().equals(arg1)){
        return t.getResult();
      }
    }
    new ErrorHandler("Errore di tipo, " + "operazione: " + op + ", arg1: " + arg1.toString());
    return new PrimitiveNodeType("error");
  }


}

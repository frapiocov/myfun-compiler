package visitor;

import error.ErrorHandler;
import nodetype.*;
import org.w3c.dom.Node;
import semantic.SymbolTable;
import semantic.SymbolTableRecord;
import syntax.*;
import syntax.expr.*;
import syntax.expr.arithop.*;
import syntax.expr.constant.*;
import syntax.expr.relop.*;
import syntax.statements.*;

import javax.swing.text.html.Option;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class TypeCheckerVisitor implements Visitor<NodeType, SymbolTable> {

  private static TypeTable typeTable = new TypeTable();

  private NodeType checkContext(List<? extends AbstractNode> nodes, SymbolTable arg) {
    NodeType returnSafe = new PrimitiveNodeType("");
    if (nodes != null) {
      for (AbstractNode s : nodes) {
        if (s != null) {
          returnSafe = s.accept(this, arg);
          if (!returnSafe.equals(new PrimitiveNodeType("notype"))) {
            new ErrorHandler("Error Type");
            return new PrimitiveNodeType("error");
          }
        }
      }
    }
    return new PrimitiveNodeType("notype");
  }

  private NodeType checkContext(LinkedList<Statement> nodes, SymbolTable arg) {
    NodeType returnSafe = new PrimitiveNodeType("");
    if (nodes != null) {
      for (Statement s : nodes) {
        if (s != null) {
          returnSafe = s.accept(this, arg);

          if (!(s instanceof ReturnOp) && !(s instanceof CallFunOp)) {
            if (!returnSafe.equals(new PrimitiveNodeType("notype"))) {
              new ErrorHandler("Error statement");
              return new PrimitiveNodeType("error");
            }
          }

        }
      }
    }
    return new PrimitiveNodeType("notype");
  }

  @Override
  public NodeType visit(Program program, SymbolTable arg) {

    arg.enterScope();
    NodeType varType = checkContext(program.getVarDeclOpList(), arg);
    NodeType funType = checkContext(program.getFunOpList(), arg);
    NodeType bodyType = program.getBodyOp().accept(this, arg);
    arg.exitScope();
    if (bodyType.equals(new PrimitiveNodeType("notype")) && varType.equals(new PrimitiveNodeType("notype")) && funType.equals(new PrimitiveNodeType("notype"))) {
      return new PrimitiveNodeType("notype");
    } else {
      new ErrorHandler("Program Error");
      return new PrimitiveNodeType("error");
    }
  }

  @Override
  public NodeType visit(AssignOp assignOp, SymbolTable arg) {
    NodeType idType = assignOp.getId().accept(this, arg);
    NodeType exprType = assignOp.getExpr().accept(this, arg);
    if(idType.equals(exprType))
      return new PrimitiveNodeType("notype");
    else {
      new ErrorHandler("Assign: Type Mismatch");
      return new PrimitiveNodeType("error");
    }
  }

  @Override
  public NodeType visit(BodyOp bodyOp, SymbolTable arg) {

    arg.enterScope();
    NodeType varType = checkContext(bodyOp.getVarDeclList(), arg);
    NodeType statType = checkContext(bodyOp.getStatList(), arg);
    arg.exitScope();
    if(varType.equals(statType))
      return new PrimitiveNodeType("notype");
    else {
      new ErrorHandler("BodyOp error");
      return new PrimitiveNodeType("error");
    }
  }

  @Override
  public NodeType visit(FunOp funOp, SymbolTable arg) {

    arg.enterScope();

    NodeType varList = checkContext(funOp.getBodyOp().getVarDeclList(), arg);
    NodeType statList = checkContext(funOp.getBodyOp().getStatList(), arg);
    //controlla che il tipo ritornato e quello dichiarato nella firma coincidano
    for(Statement s : funOp.getBodyOp().getStatList()){
      if(s instanceof ReturnOp){
        NodeType returnFun = s.accept(this,arg);
        String retOp = returnFun.toString();
        String retFun = "";
        if(funOp.getType() != null)
          retFun = funOp.getType().getValue();

        if(!(retOp.equals(retFun))) {
          new ErrorHandler("FunOp return " + retFun + "\nReturnOp is " + returnFun);
        }
      }
    }
    arg.exitScope();

    if(varList.equals(new PrimitiveNodeType("notype")) && statList.equals(new PrimitiveNodeType("notype")))
      return new PrimitiveNodeType("notype");

    new ErrorHandler("Fun Op Body errore di tipo");
    return new PrimitiveNodeType("error");
  }

  @Override
  public NodeType visit(IdInitOp idInitOp, SymbolTable arg) {
    NodeType idType = idInitOp.getId().accept(this, arg);

    if(idInitOp.getExpr() != null) {
      NodeType exprType = idInitOp.getExpr().accept(this,arg);
      if(idType.equals(exprType)) {
        return new PrimitiveNodeType("notype");
      } else {
        new ErrorHandler("IdInitOp: Type Mismatch");
        return new PrimitiveNodeType("error");
      }
    } else {
     return new PrimitiveNodeType("notype");
    }
  }

  @Override
  public NodeType visit(ParDeclOp parDeclOp, SymbolTable arg) {
    parDeclOp.getId().setNodeType(arg.lookup(parDeclOp.getId().getValue()).get().getNodeType());
    return new PrimitiveNodeType(parDeclOp.getType().getValue());
  }

  @Override
  public NodeType visit(VarDeclOp varDeclOp, SymbolTable arg) {
    return checkContext(varDeclOp.getIdInitList(),arg);
  }

  @Override
  public NodeType visit(CallFunOp callFunOp, SymbolTable arg) {
    SymbolTableRecord record = arg.lookup(callFunOp.getId().getValue()).get();

    //dal record della symbol table si estrae tipo di ritorno e tipi dei parametri
    FunctionNodeType funType = (FunctionNodeType) record.getNodeType();

    PrimitiveNodeType returnType = (PrimitiveNodeType) funType.getNodeType(); //tipo di ritorno della funzione
    CompositeNodeType paramsType = funType.getParamsType(); // tipi dei parametri della funzione

    List<NodeType> params = paramsType.getTypes(); //tipi della symbol table
    List<String> kinds = paramsType.getKinds(); //kind della symbol table (IN / OUT)

    //parametri chiamata a funzione
    List<Expr> funParams = callFunOp.getParams();
    if(funParams != null) {
      Collections.reverse(funParams);
      //per ogni espressione demando l'accept e costruisco una lista di nodetype
      List<NodeType> params2 = new LinkedList<>();
      //scorro i parametri
      int j = 0;
      for (Expr e : funParams) {
        if (checkConstants(e)) { //parametro di tipo OUT costante non può essere utilizzato
          if (kinds.get(j).equals("OUT"))
            new ErrorHandler("OUT param non può essere una costante");
        } else { //expr variabile
          if (kinds.get(j).equals("OUT") && !(e instanceof IdOutpar))
            new ErrorHandler("OUT param atteso non risultante");
        }
        j++;
        params2.add(e.accept(this, arg));
      }
      //controllo se il numero di parametri è rispettato
      if (params.size() != params2.size()) {
        new ErrorHandler("Errore CallFunOp: numero argomenti funzione non corrispondente");
      } else { //controlla i tipi di ogni parametro
        for (int i = 0; i < params.size(); i++) {
          PrimitiveNodeType first = (PrimitiveNodeType) params.get(i);
          PrimitiveNodeType second = (PrimitiveNodeType) params2.get(i);
          //controlla il matching
          if (!first.equals(second))
            new ErrorHandler("Error CallFunOp type mismatch argomenti");
        }
        //match corretti
        callFunOp.setNodeType(returnType);
        return returnType;
      }
    }
    callFunOp.setNodeType(returnType);
    return returnType;
  }


  //true se e è una costante
  public boolean checkConstants(Expr e) {
    if(e instanceof FalseConst ||  e instanceof IntegerConst || e instanceof RealConst || e instanceof StringConst || e instanceof TrueConst)
      return true;
    else
      return false;
  }

  @Override
  public NodeType visit(ElseOp elseOp, SymbolTable arg) {
    NodeType bodyType = elseOp.getBodyOp().accept(this, arg);
    if(bodyType.equals(new PrimitiveNodeType("notype")))
      return new PrimitiveNodeType("notype");
    else
      new ErrorHandler("ElseBody type error");
    return new PrimitiveNodeType("error");
  }

  @Override
  public NodeType visit(IfstatOp ifstatOp, SymbolTable arg) {
    NodeType exprType = ifstatOp.getExpr().accept(this, arg);
    NodeType bodyType = ifstatOp.getBodyOp().accept(this, arg);

    if(bodyType.equals(new PrimitiveNodeType("notype"))  && exprType.equals(new PrimitiveNodeType("bool")) ) {
      if (ifstatOp.getElseop() != null) {
        NodeType elseType = ifstatOp.getElseop().accept(this, arg);
        if(elseType.equals(new PrimitiveNodeType("notype")))
          return new PrimitiveNodeType("notype");
      } else {
        return new PrimitiveNodeType("notype");
      }
    } else {
      new ErrorHandler("IfStat error type");
    }
    return new PrimitiveNodeType("notype");
  }

  @Override
  public NodeType visit(ReadOp readOp, SymbolTable arg) {
    return new PrimitiveNodeType("notype");
  }

  @Override
  public NodeType visit(ReturnOp returnOp, SymbolTable arg) {
    return returnOp.getExpr().accept(this, arg);
  }

  @Override
  public NodeType visit(WhileOp whileOp, SymbolTable arg) {
    arg.enterScope();
    NodeType exprType = whileOp.getExpr().accept(this, arg);
    NodeType varType = checkContext(whileOp.getBodyOp().getVarDeclList(), arg);
    NodeType statType = checkContext(whileOp.getBodyOp().getStatList(), arg);
    arg.exitScope();
    if(statType.equals(new PrimitiveNodeType("notype")) && varType.equals(new PrimitiveNodeType("notype"))  && exprType.equals(new PrimitiveNodeType("bool")) ) {
        return new PrimitiveNodeType("notype");
    } else {
      new ErrorHandler("WhileStat error type");
    }
    return new PrimitiveNodeType("error");
  }

  @Override
  public NodeType visit(WriteOp writeOp, SymbolTable arg) {
    if(!writeOp.getExpr().accept(this, arg).equals(new PrimitiveNodeType("void")))
      return new PrimitiveNodeType("notype");
    else{
      new ErrorHandler("WriteOp: invalid use of void expression");
      return new PrimitiveNodeType("error");
    }
  }

  @Override
  public NodeType visit(Id id, SymbolTable arg) {
    Optional<SymbolTableRecord> record = arg.lookup(id.getValue());
    id.setNodeType(record.get().getNodeType());
    return record.get().getNodeType();
  }

  @Override
  public NodeType visit(IdOutpar id, SymbolTable arg) {
    Optional<SymbolTableRecord> record = arg.lookup(id.getValue());
    return record.get().getNodeType();
  }

  @Override
  public NodeType visit(MinusOp minusOp, SymbolTable arg) {
    NodeType arg1 = minusOp.getExpr().accept(this,arg);
    minusOp.setNodeType(typeTable.check("MINUS",arg1));
    return typeTable.check("MINUS",arg1);
  }

  @Override
  public NodeType visit(ParExprOp parExprOp, SymbolTable arg) {
    if(parExprOp.getExpr() instanceof CallFunOp ){
      CallFunOp callFunOp = (CallFunOp) parExprOp.getExpr();
      SymbolTableRecord record = arg.lookup(callFunOp.getId().getValue()).get();
      //dal record della symbol table si estrae tipo di ritorno e tipi dei parametri
      FunctionNodeType funType = (FunctionNodeType) record.getNodeType();
      PrimitiveNodeType returnType = (PrimitiveNodeType) funType.getNodeType(); //tipo di ritorno della funzione
      return returnType;
    }
    if(parExprOp.getExpr() instanceof Id ) {
      Id id = (Id) parExprOp.getExpr();
      PrimitiveNodeType pt = (PrimitiveNodeType) id.getNodeType();
      return pt;
    }

    return parExprOp.accept(this, arg);
  }

  @Override
  public NodeType visit(PrimitiveType primitiveType, SymbolTable arg) {
    return new PrimitiveNodeType(primitiveType.getValue());
  }

  @Override
  public NodeType visit(StrCatOp strCatOp, SymbolTable arg) {
    NodeType arg1 = strCatOp.getLeftValue().accept(this,arg);
    NodeType arg2 = strCatOp.getRightValue().accept(this,arg);
    strCatOp.setNodeType(typeTable.check("STRCAT",arg1,arg2));
    return typeTable.check("STRCAT",arg1,arg2);
  }

  @Override
  public NodeType visit(UminusOp uminusOp, SymbolTable arg) {
    NodeType arg1 = uminusOp.getExpr().accept(this,arg);
    return typeTable.check("MINUS",arg1);
  }

  @Override
  public NodeType visit(AndOp andOp, SymbolTable arg) {
    NodeType arg1 = andOp.getLeftValue().accept(this,arg);
    NodeType arg2 = andOp.getRightValue().accept(this,arg);
    andOp.setNodeType(typeTable.check("AND",arg1,arg2));
    return typeTable.check("AND",arg1,arg2);
  }

  @Override
  public NodeType visit(EQOp eqOp, SymbolTable arg) {
    NodeType arg1 = eqOp.getLeftValue().accept(this,arg);
    NodeType arg2 = eqOp.getRightValue().accept(this,arg);
    eqOp.setNodeType(typeTable.check("REL",arg1,arg2));
    return typeTable.check("REL",arg1,arg2);
  }

  @Override
  public NodeType visit(GEOp geOp, SymbolTable arg) {
    NodeType arg1 = geOp.getLeftValue().accept(this,arg);
    NodeType arg2 = geOp.getRightValue().accept(this,arg);
    geOp.setNodeType(typeTable.check("REL",arg1,arg2));
    return typeTable.check("REL",arg1,arg2);
  }

  @Override
  public NodeType visit(GTOp gtOp, SymbolTable arg) {
    NodeType arg1 = gtOp.getLeftValue().accept(this,arg);
    NodeType arg2 = gtOp.getRightValue().accept(this,arg);
    gtOp.setNodeType(typeTable.check("REL",arg1,arg2));
    return typeTable.check("REL",arg1,arg2);
  }

  @Override
  public NodeType visit(LEOp leOp, SymbolTable arg) {
    NodeType arg1 = leOp.getLeftValue().accept(this,arg);
    NodeType arg2 = leOp.getRightValue().accept(this,arg);
    leOp.setNodeType(typeTable.check("REL",arg1,arg2));
    return typeTable.check("REL",arg1,arg2);
  }

  @Override
  public NodeType visit(LTOp ltOp, SymbolTable arg) {
    NodeType arg1 = ltOp.getLeftValue().accept(this,arg);
    NodeType arg2 = ltOp.getRightValue().accept(this,arg);
    ltOp.setNodeType(typeTable.check("REL",arg1,arg2));
    return typeTable.check("REL",arg1,arg2);
  }

  @Override
  public NodeType visit(NEOp neOp, SymbolTable arg) {
    NodeType arg1 = neOp.getLeftValue().accept(this, arg);
    NodeType arg2 = neOp.getRightValue().accept(this, arg);
    neOp.setNodeType(typeTable.check("REL",arg1,arg2));
    return typeTable.check("REL",arg1,arg2);
  }

  @Override
  public NodeType visit(NotOp notOp, SymbolTable arg) {
    NodeType arg1 = notOp.getExpr().accept(this,arg);
    notOp.setNodeType(typeTable.check("NOT",arg1));
    return typeTable.check("NOT",arg1);
  }

  @Override
  public NodeType visit(OrOp orOp, SymbolTable arg) {
    NodeType arg1 = orOp.getLeftValue().accept(this,arg);
    NodeType arg2 = orOp.getRightValue().accept(this,arg);
    orOp.setNodeType(typeTable.check("OR",arg1,arg2));
    return typeTable.check("OR",arg1,arg2);
  }

  @Override
  public NodeType visit(TrueConst trueConst, SymbolTable arg) {
    trueConst.setNodeType(new PrimitiveNodeType("bool"));
    return new PrimitiveNodeType("bool");
  }

  @Override
  public NodeType visit(FalseConst falseConst, SymbolTable arg) {
    falseConst.setNodeType(new PrimitiveNodeType("bool"));
    return new PrimitiveNodeType("bool");
  }

  @Override
  public NodeType visit(IntegerConst integerConst, SymbolTable arg) {
    integerConst.setNodeType(new PrimitiveNodeType("integer"));
    return new PrimitiveNodeType("integer");
  }

  @Override
  public NodeType visit(RealConst realConst, SymbolTable arg) {
    realConst.setNodeType(new PrimitiveNodeType("real"));
    return new PrimitiveNodeType("real");
  }

  @Override
  public NodeType visit(StringConst stringConst, SymbolTable arg) {
    stringConst.setNodeType(new PrimitiveNodeType("string"));
    return new PrimitiveNodeType("string");
  }

  @Override
  public NodeType visit(AddOp addOp, SymbolTable arg) {
    NodeType arg1 = addOp.getLeftValue().accept(this,arg);
    NodeType arg2 = addOp.getRightValue().accept(this,arg);
    addOp.setNodeType(typeTable.check("ARITH",arg1,arg2));
    return typeTable.check("ARITH",arg1,arg2);
  }

  @Override
  public NodeType visit(DiffOp diffOp, SymbolTable arg) {
    NodeType arg1 = diffOp.getLeftValue().accept(this,arg);
    NodeType arg2 = diffOp.getRightValue().accept(this,arg);
    diffOp.setNodeType(typeTable.check("ARITH",arg1,arg2));
    return typeTable.check("ARITH",arg1,arg2);
  }

  @Override
  public NodeType visit(DivIntOp divIntOp, SymbolTable arg) {
    NodeType arg1 = divIntOp.getLeftValue().accept(this,arg);
    NodeType arg2 = divIntOp.getRightValue().accept(this,arg);
    divIntOp.setNodeType(typeTable.check("DIVINT",arg1,arg2));
    return typeTable.check("DIVINT",arg1,arg2);
  }

  @Override
  public NodeType visit(DivOp divOp, SymbolTable arg) {
    NodeType arg1 = divOp.getLeftValue().accept(this,arg);
    NodeType arg2 = divOp.getRightValue().accept(this,arg);
    divOp.setNodeType(typeTable.check("ARITH",arg1,arg2));
    return typeTable.check("ARITH",arg1,arg2);
  }

  @Override
  public NodeType visit(MulOp mulOp, SymbolTable arg) {
    NodeType arg1 = mulOp.getLeftValue().accept(this,arg);
    NodeType arg2 = mulOp.getRightValue().accept(this,arg);
    mulOp.setNodeType(typeTable.check("ARITH",arg1,arg2));
    return typeTable.check("ARITH",arg1,arg2);
  }

  @Override
  public NodeType visit(PowOp powOp, SymbolTable arg) {
    NodeType arg1 = powOp.getLeftValue().accept(this,arg);
    NodeType arg2 = powOp.getRightValue().accept(this,arg);
    powOp.setNodeType(typeTable.check("ARITH",arg1,arg2));
    return typeTable.check("ARITH",arg1,arg2);
  }
}

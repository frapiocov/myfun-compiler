package visitor;

import error.ErrorHandler;
import nodekind.NodeKind;
import nodetype.CompositeNodeType;
import nodetype.FunctionNodeType;
import nodetype.NodeType;
import nodetype.PrimitiveNodeType;
import semantic.SymbolTable;
import semantic.SymbolTableRecord;
import syntax.*;
import syntax.expr.*;
import syntax.expr.arithop.*;
import syntax.expr.constant.*;
import syntax.expr.relop.*;
import syntax.statements.*;

import java.util.*;

public class ScopeCheckerVisitor implements Visitor<Boolean, SymbolTable> {

  private boolean checkContext(List<? extends AbstractNode> nodes, SymbolTable arg) {
    if(nodes != null) {
      if (nodes.isEmpty()) {
        return true;
      } else {
        return nodes.stream().allMatch(node -> node.accept(this, arg));
      }
    }else return true;
  }
  // per ogni nodo di tipo statement controlla se viene accettato
  private boolean checkContext(LinkedList<Statement> nodes, SymbolTable arg) {
    Boolean returnSafe = true;
    if(nodes != null){
      for (Statement s:nodes){
        if(s != null)
         returnSafe = s.accept(this, arg);
         if(!returnSafe){
           return false;
         }
      }
    }
    return true;
  }


  @Override
  public Boolean visit(Program program, SymbolTable arg) {
    Collections.reverse(program.getFunOpList());
    Collections.reverse(program.getVarDeclOpList());
    arg.enterScope();
    boolean isVariableSafe = checkContext(program.getVarDeclOpList(), arg);
    boolean isFunctionSafe = checkContext(program.getFunOpList(), arg);
    boolean isBodySafe = program.getBodyOp().accept(this,arg);
    boolean isProgramSafe = isFunctionSafe && isVariableSafe && isBodySafe;
    if(!isProgramSafe){
      new ErrorHandler("Errore di compilazione");
    }
    arg.exitScope();
    return isProgramSafe;
  }

  @Override
  public Boolean visit(AssignOp assignOp, SymbolTable arg) {
    boolean idSafe = assignOp.getId().accept(this, arg);
    boolean exprSafe = assignOp.getExpr().accept(this, arg);
    boolean assignSafe = exprSafe && idSafe;
    if(!assignSafe){
      new ErrorHandler("Variabilee "+assignOp.getId().getValue()+" non dichiarata");
      return false;
    }else return true;
  }

  @Override
  public Boolean visit(BodyOp bodyOp, SymbolTable arg) {
      Collections.reverse(bodyOp.getStatList());
      Collections.reverse(bodyOp.getVarDeclList());
      arg.enterScope();
      Boolean varDeclSafe = checkContext(bodyOp.getVarDeclList(), arg);
      Boolean statListSafe = checkContext(bodyOp.getStatList(), arg);
      arg.exitScope();
    return varDeclSafe && statListSafe;
  }

  @Override
  public Boolean visit(StringConst stringConst, SymbolTable arg) {
    return true;
  }

  @Override
  public Boolean visit(FunOp funOp, SymbolTable arg) {
    Collections.reverse(funOp.getBodyOp().getVarDeclList());
    Collections.reverse(funOp.getBodyOp().getStatList());
    boolean funNameSafe = saveFunName(funOp, arg);

    arg.enterScope();
    boolean parDeclSafe = checkContext(funOp.getParDeclOp(), arg);
    boolean varDeclSafe = checkContext(funOp.getBodyOp().getVarDeclList(),arg);
    boolean statListSafe = checkContext(funOp.getBodyOp().getStatList(),arg);
    arg.exitScope();

    return varDeclSafe && statListSafe && parDeclSafe && funNameSafe;
  }

  @Override
  public Boolean visit(IdInitOp idInitOp, SymbolTable arg) {
    boolean idSafe = idInitOp.getId().accept(this, arg);
    if(idInitOp.getExpr() instanceof CallFunOp)
      System.out.println("Call instance");
    boolean expSafe = idInitOp.getExpr().accept(this, arg);
    boolean isSafe = expSafe && idSafe;
    if(!isSafe)
      new ErrorHandler("Errore ID-INIT");
    return isSafe;
  }

  @Override
  public Boolean visit(ParDeclOp parDeclOp, SymbolTable arg) {
    String lexema = parDeclOp.getId().getValue();
    String type = parDeclOp.getType().getValue();
    if(!arg.probe(lexema)) {
      arg.addEntry(lexema, new SymbolTableRecord(lexema, new PrimitiveNodeType(type), findKind(parDeclOp)));
      return true;
    } else {
      new ErrorHandler("Errore dichiarazione multipla parametri");
      return false;
    }

  }

  @Override
  public Boolean visit(VarDeclOp varDeclOp, SymbolTable arg) {
    String lexema;
    PrimitiveNodeType nodeType;
    boolean exprSafe = true, idSafe = true;

    for(IdInitOp initOp : varDeclOp.getIdInitList()){
      lexema=initOp.getId().getValue();
      if(!arg.probe(lexema)){
        if(!varDeclOp.getPrimitiveType().getValue().equals("var")){
          nodeType = new PrimitiveNodeType(varDeclOp.getPrimitiveType().getValue());
        }else{
          nodeType = new PrimitiveNodeType(inference(initOp.getExpr()));
        }
        arg.addEntry(lexema,new SymbolTableRecord(lexema,nodeType,NodeKind.VARIABLE));
      }else{
        new ErrorHandler("Errore dichiarazione multipla variabili");
        idSafe=false;
      }
      if(initOp.getExpr() != null) {
        if (!initOp.getExpr().accept(this, arg))
          exprSafe = false;
      }
    }

    return exprSafe && idSafe;
  }

  @Override
  public Boolean visit(CallFunOp callFunOp, SymbolTable arg) {
    boolean idSafe = callFunOp.getId().accept(this, arg);
    if(!idSafe)
      new ErrorHandler("Errore id chiamata funzione");

    LinkedList<Expr> params = callFunOp.getParams();
    boolean returnSafe = true;
    if(params != null){
      for (Expr e:params){
        if(e != null)
          returnSafe = e.accept(this, arg);
        if(!returnSafe){
          new ErrorHandler("Errore parametri chiamata funzione");
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public Boolean visit(ElseOp elseOp, SymbolTable arg) {
    boolean isSafe = elseOp.getBodyOp().accept(this, arg);
    if(!isSafe)
      new ErrorHandler("Errore ELSE");
    return isSafe;
  }

  @Override
  public Boolean visit(IfstatOp ifstatOp, SymbolTable arg) {
    Boolean ifSafe = ifstatOp.getBodyOp().accept(this, arg);
    if(!ifSafe)
      new ErrorHandler("Errore corpo IF");
    if(ifstatOp.getElseop() != null) {
      Boolean elseSafe = ifstatOp.getElseop().accept(this, arg);
      if(!elseSafe)
        new ErrorHandler("Errore corpo ELSE");
      return true;
    }
    return ifSafe;
  }

  @Override
  public Boolean visit(ReadOp readOp, SymbolTable arg) {
    boolean expSafe = true;
    if(readOp.getExpr() != null)
      expSafe = readOp.getExpr().accept(this, arg);

    boolean idSafe = checkContext(readOp.getIds(), arg);
    boolean isSafe = expSafe && idSafe;
    if(!isSafe)
      new ErrorHandler("Errore READ");
    return isSafe;
  }

  @Override
  public Boolean visit(ReturnOp returnOp, SymbolTable arg) {
    boolean exprSafe = returnOp.getExpr().accept(this, arg);
    if(!exprSafe)
      new ErrorHandler("Errore RETURN");
    return exprSafe;
  }

  @Override
  public Boolean visit(WhileOp whileOp, SymbolTable arg) {
    boolean exprSafe = whileOp.getExpr().accept(this, arg);
    boolean wSafe = whileOp.getBodyOp().accept(this, arg);
    if(!exprSafe)
      new ErrorHandler("Errore condizione WHILE");
    boolean isSafe = exprSafe && wSafe;
    if(!isSafe)
      new ErrorHandler("Errore corpo WHILE");
    return isSafe;
  }

  @Override
  public Boolean visit(WriteOp writeOp, SymbolTable arg) {
    boolean isSafe = writeOp.getExpr().accept(this, arg);
    if(!isSafe)
      new ErrorHandler("Errore WRITE OPERATION");
    return isSafe;
  }

  @Override
  public Boolean visit(Id id, SymbolTable arg) {
    Optional<SymbolTableRecord> tableRecord = arg.lookup(id.getValue());
    if(tableRecord.isEmpty()){
      new ErrorHandler("Variabile " + id.getValue() + " non dichiarata ");
      return false;
    }else return true;
  }

  @Override
  public Boolean visit(IdOutpar id, SymbolTable arg) {
    Optional<SymbolTableRecord> tableRecord = arg.lookup(id.getValue());
    if(tableRecord.isEmpty()){
      new ErrorHandler("Variabile out " + id.getValue() + " non dichiarata ");
      return false;
    }else return true;
  }

  @Override
  public Boolean visit(MinusOp minusOp, SymbolTable arg) {
    boolean lfSafe = minusOp.getExpr().accept(this, arg);
    if(!lfSafe)
      new ErrorHandler(" Errore espressione unaria MINUS ");
    return lfSafe;
  }

  @Override
  public Boolean visit(ParExprOp parExprOp, SymbolTable arg) {
    boolean isSafe = parExprOp.getExpr().accept(this, arg);
    if(!isSafe)
      new ErrorHandler("Errore PAR EXPR OPERATION ");
    return isSafe;
  }

  @Override
  public Boolean visit(PrimitiveType primitiveType, SymbolTable arg) {
    return true;
  }

  @Override
  public Boolean visit(StrCatOp strCatOp, SymbolTable arg) {
    boolean lfSafe = strCatOp.getLeftValue().accept(this, arg);
    boolean rgSafe = strCatOp.getRightValue().accept(this, arg);
    boolean resSafe = lfSafe && rgSafe;
    if(!resSafe)
      new ErrorHandler(" Errore espressione binaria STRING CONCAT & ");
    return resSafe;
  }

  @Override
  public Boolean visit(UminusOp uminusOp, SymbolTable arg) {
    boolean isSafe = uminusOp.getExpr().accept(this, arg);
    if(!isSafe)
      new ErrorHandler(" Errore espressione unaria UMINUS ");
    return isSafe;
  }

  @Override
  public Boolean visit(AndOp andOp, SymbolTable arg) {
    boolean lfSafe = andOp.getLeftValue().accept(this, arg);
    boolean rgSafe = andOp.getRightValue().accept(this, arg);
    boolean resSafe = lfSafe && rgSafe;
    if(!resSafe)
      new ErrorHandler(" Errore espressione binaria AND ");
    return resSafe;
  }

  @Override
  public Boolean visit(EQOp eqOp, SymbolTable arg) {
    boolean lfSafe = eqOp.getLeftValue().accept(this, arg);
    boolean rgSafe = eqOp.getRightValue().accept(this, arg);
    boolean resSafe = lfSafe && rgSafe;
    if(!resSafe)
      new ErrorHandler(" Errore espressione binaria EQUALS = ");
    return resSafe;
  }

  @Override
  public Boolean visit(GEOp geOp, SymbolTable arg) {
    boolean lfSafe = geOp.getLeftValue().accept(this, arg);
    boolean rgSafe = geOp.getRightValue().accept(this, arg);
    boolean resSafe = lfSafe && rgSafe;
    if(!resSafe)
      new ErrorHandler(" Errore espressione binaria GREATER THEN EQUALS >= ");
    return resSafe;
  }

  @Override
  public Boolean visit(GTOp gtOp, SymbolTable arg) {
    boolean lfSafe = gtOp.getLeftValue().accept(this, arg);
    boolean rgSafe = gtOp.getRightValue().accept(this, arg);
    boolean resSafe = lfSafe && rgSafe;
    if(!resSafe)
      new ErrorHandler(" Errore espressione binaria GREATER THEN > ");
    return resSafe;
  }

  @Override
  public Boolean visit(LEOp leOp, SymbolTable arg) {
    boolean lfSafe = leOp.getLeftValue().accept(this, arg);
    boolean rgSafe = leOp.getRightValue().accept(this, arg);
    boolean resSafe = lfSafe && rgSafe;
    if(!resSafe)
      new ErrorHandler(" Errore espressione binaria LESS THEN EQUALS <= ");
    return resSafe;
  }

  @Override
  public Boolean visit(LTOp ltOp, SymbolTable arg) {
    boolean lfSafe = ltOp.getLeftValue().accept(this, arg);
    boolean rgSafe = ltOp.getRightValue().accept(this, arg);
    boolean resSafe = lfSafe && rgSafe;
    if(!resSafe)
      new ErrorHandler(" Errore espressione binaria LESS THEN < ");
    return resSafe;
  }

  @Override
  public Boolean visit(NEOp neOp, SymbolTable arg) {
    boolean lfSafe = neOp.getLeftValue().accept(this, arg);
    boolean rgSafe = neOp.getRightValue().accept(this, arg);
    boolean resSafe = lfSafe && rgSafe;
    if(!resSafe)
      new ErrorHandler(" Errore espressione binaria NOT EQUALS != ");
    return resSafe;
  }

  @Override
  public Boolean visit(NotOp notOp, SymbolTable arg) {
    boolean isSafe = notOp.getExpr().accept(this, arg);
    if(!isSafe)
      new ErrorHandler("Errore espressione unaria NOT ");
    return isSafe;
  }

  @Override
  public Boolean visit(OrOp orOp, SymbolTable arg) {
    boolean leftSafe = orOp.getLeftValue().accept(this, arg);
    boolean rightSafe = orOp.getLeftValue().accept(this, arg);
    boolean isSafe = leftSafe && rightSafe;
    if(!isSafe)
      new ErrorHandler("Errore espressione binaria OR ");
    return isSafe;
  }

  @Override
  public Boolean visit(TrueConst trueConst, SymbolTable arg) {
    return true;
  }

  @Override
  public Boolean visit(FalseConst falseConst, SymbolTable arg) {
    return true;
  }

  @Override
  public Boolean visit(IntegerConst integerConst, SymbolTable arg) {
    return true;
  }

  @Override
  public Boolean visit(RealConst realConst, SymbolTable arg) {
    return true;
  }

  @Override
  public Boolean visit(AddOp addOp, SymbolTable arg) {
    boolean lSafe = addOp.getLeftValue().accept(this, arg);
    boolean rSafe = addOp.getRightValue().accept(this, arg);
    boolean isSafe = lSafe && rSafe;
    if (!isSafe)
      new ErrorHandler("Errore espressione binaria ADD + ");
    return isSafe;
  }

  @Override
  public Boolean visit(DiffOp diffOp, SymbolTable arg) {
    boolean lSafe = diffOp.getLeftValue().accept(this, arg);
    boolean rSafe = diffOp.getRightValue().accept(this, arg);
    boolean isSafe = lSafe && rSafe;
    if (!isSafe)
      new ErrorHandler("Errore espressione binaria DIFF - ");
    return isSafe;
  }

  @Override
  public Boolean visit(DivIntOp divIntOp, SymbolTable arg) {
    boolean lSafe = divIntOp.getLeftValue().accept(this, arg);
    boolean rSafe = divIntOp.getRightValue().accept(this, arg);
    boolean isSafe = lSafe && rSafe;
    if (!isSafe)
      new ErrorHandler("Errore espressione binaria DIVINT div ");
    return isSafe;
  }

  @Override
  public Boolean visit(DivOp divOp, SymbolTable arg) {
    boolean lSafe = divOp.getLeftValue().accept(this, arg);
    boolean rSafe = divOp.getRightValue().accept(this, arg);
    boolean isSafe = lSafe && rSafe;
    if (!isSafe)
      new ErrorHandler("Errore espressione binaria DIV / ");
    return isSafe;
  }

  @Override
  public Boolean visit(MulOp mulOp, SymbolTable arg) {
    boolean lSafe = mulOp.getLeftValue().accept(this, arg);
    boolean rSafe = mulOp.getRightValue().accept(this, arg);
    boolean isSafe = lSafe && rSafe;
    if (!isSafe)
      new ErrorHandler("Errore espressione binaria MUL * ");
    return isSafe;
  }

  @Override
  public Boolean visit(PowOp powOp, SymbolTable arg) {
    boolean lSafe = powOp.getLeftValue().accept(this, arg);
    boolean rSafe = powOp.getRightValue().accept(this, arg);
    boolean isSafe = lSafe && rSafe;
    if (!isSafe)
      new ErrorHandler("Errore espressione binaria POW ^ ");
    return isSafe;
  }

  private CompositeNodeType createComposite(List<ParDeclOp> list) {
    CompositeNodeType compositeNodeType = new CompositeNodeType(new ArrayList<NodeType>());
    for(ParDeclOp p : list){
      NodeType nodeType = new PrimitiveNodeType(p.getType().getValue());

      compositeNodeType.addNodeType(nodeType);
      compositeNodeType.addKind(p.getKind());
    }
    return compositeNodeType;
  }

  private NodeKind findKind(ParDeclOp parDeclOp){
    switch (parDeclOp.getKind()){
      case "IN": return NodeKind.VARIABLE_IN;
      case "OUT": return NodeKind.VARIABLE_OUT;
      default:return NodeKind.VARIABLE;
    }
  }

  private String inference(Expr expr){
    if(expr instanceof IntegerConst){
      return "integer";
    }
    if(expr instanceof RealConst){
      return "real";
    }
    if(expr instanceof StringConst){
      return "string";
    }
    if(expr instanceof TrueConst || expr instanceof FalseConst){
      return "bool";
    }
    return "error type";
  }

  private Boolean saveFunName(FunOp funOp, SymbolTable arg){
    String lexema = funOp.getId().getValue();
    if(!arg.probe(lexema)) {
      NodeType returnType;
      if (funOp.getType() != null) {
        returnType = new PrimitiveNodeType(funOp.getType().getValue());
      } else {
        returnType = new PrimitiveNodeType("void");
      }
      CompositeNodeType compositeNodeType;
      if (funOp.getParDeclOp() != null) {
        compositeNodeType = createComposite(funOp.getParDeclOp());
      } else {
        compositeNodeType = new CompositeNodeType(new ArrayList<NodeType>());
      }
      FunctionNodeType functionNodeType = new FunctionNodeType(compositeNodeType, returnType);
      arg.addEntry(lexema, new SymbolTableRecord(lexema, functionNodeType, NodeKind.FUNCTION));
      return true;
    }else{
      System.err.println("FunOp error");
      return false;
    }
  }
}
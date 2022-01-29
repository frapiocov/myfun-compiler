package syntax;

import visitor.Visitor;

import java.util.Collections;
import java.util.LinkedList;

public class Program extends AbstractNode{

  private LinkedList<VarDeclOp> varDeclOpList;
  private LinkedList<FunOp> funOpList;
  private BodyOp bodyOp;

  public Program(int leftLocation, int rightLocation,LinkedList<FunOp> funOpList, BodyOp bodyOp, LinkedList<VarDeclOp> varDeclOpList) {
    super(leftLocation, rightLocation);
    this.bodyOp = bodyOp;
    this.funOpList = funOpList;
    this.varDeclOpList = varDeclOpList;
  }

  public BodyOp getBodyOp() {
    return bodyOp;
  }

  public LinkedList<FunOp> getFunOpList() {
    return funOpList;
  }

  public LinkedList<VarDeclOp> getVarDeclOpList() {
    return varDeclOpList;
  }

  @Override
  public <T, P> T accept(Visitor<T, P> visitor, P arg) {
    return visitor.visit(this, arg);
  }
}

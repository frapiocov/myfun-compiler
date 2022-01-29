package syntax;

import visitor.Visitor;

import java.util.LinkedList;

public class BodyOp extends AbstractNode{

  private LinkedList<VarDeclOp> varDeclList;
  private LinkedList<Statement> statList;

  public BodyOp(int leftLocation, int rightLocation) {
    super(leftLocation, rightLocation);
  }

  public BodyOp(int leftLocation, int rightLocation, LinkedList<Statement> statList, LinkedList<VarDeclOp> varDeclList) {
    super(leftLocation, rightLocation);
    this.statList = statList;
    this.varDeclList = varDeclList;
  }

  public LinkedList<VarDeclOp> getVarDeclList() { return varDeclList; }

  public LinkedList<Statement> getStatList() {
    return statList;
  }

  @Override
  public <T, P> T accept(Visitor<T, P> visitor, P arg) {
    return visitor.visit(this, arg);
  }
}

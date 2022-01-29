package syntax;

import syntax.expr.PrimitiveType;
import visitor.Visitor;

import java.util.LinkedList;


public class VarDeclOp extends AbstractNode {

  private PrimitiveType primitiveType;
  private LinkedList<IdInitOp> idInitList;


  public VarDeclOp(int leftLocation, int rightLocation, PrimitiveType primitiveType, LinkedList<IdInitOp> idInitList) {
    super(leftLocation, rightLocation);
    this.primitiveType = primitiveType;
    this.idInitList = idInitList;
  }

  public PrimitiveType getPrimitiveType() {
    return primitiveType;
  }

  public LinkedList<IdInitOp> getIdInitList() {
    return idInitList;
  }

  @Override
  public <T, P> T accept(Visitor<T, P> visitor, P arg) {
    return visitor.visit(this, arg);
  }
}

package syntax.expr.relop;

import syntax.AbstractNode;
import syntax.Expr;
import visitor.Visitor;

public class EQOp extends AbstractNode implements Expr {

  private Expr leftValue;
  private Expr rightValue;

  public EQOp(int leftLocation, int rightLocation, Expr leftValue, Expr rightValue) {
    super(leftLocation, rightLocation);
    this.leftValue = leftValue;
    this.rightValue = rightValue;
  }

  public Expr getLeftValue() {
    return leftValue;
  }

  public Expr getRightValue() {
    return rightValue;
  }

  @Override
  public <T, P> T accept(Visitor<T, P> visitor, P arg) {
    return visitor.visit(this, arg);
  }
}

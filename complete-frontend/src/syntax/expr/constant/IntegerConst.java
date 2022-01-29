package syntax.expr.constant;

import syntax.AbstractNode;
import syntax.Expr;
import syntax.Leaf;
import visitor.Visitor;

public class IntegerConst extends AbstractNode implements Expr ,Leaf<Integer> {

  private int value;

  public IntegerConst(int leftLocation, int rightLocation, int value) {
    super(leftLocation, rightLocation);
    this.value = value;
  }

  @Override
  public Integer getValue() {
    return this.value;
  }

  @Override
  public <T, P> T accept(Visitor<T, P> visitor, P arg) {
    return visitor.visit(this, arg);
  }
}

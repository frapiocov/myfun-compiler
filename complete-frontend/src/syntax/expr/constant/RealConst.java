package syntax.expr.constant;

import syntax.AbstractNode;
import syntax.Expr;
import syntax.Leaf;
import visitor.Visitor;

public class RealConst extends AbstractNode implements Expr ,Leaf<Float> {

  private Float value;

  public RealConst(int leftLocation, int rightLocation, Float value) {
    super(leftLocation, rightLocation);
    this.value = value;
  }

  @Override
  public Float getValue() {
    return this.value;
  }

  @Override
  public <T, P> T accept(Visitor<T, P> visitor, P arg) {
    return visitor.visit(this, arg);
  }
}

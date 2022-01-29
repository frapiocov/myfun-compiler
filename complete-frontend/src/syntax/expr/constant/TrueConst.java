package syntax.expr.constant;

import syntax.AbstractNode;
import syntax.Expr;
import syntax.Leaf;
import visitor.Visitor;

public class TrueConst extends AbstractNode implements Expr, Leaf<Boolean> {

  private Boolean value;

  public TrueConst(int leftLocation, int rightLocation, Boolean value) {
    super(leftLocation, rightLocation);
    this.value = value;
  }

  @Override
  public Boolean getValue() {
    return value;
  }

  @Override
  public <T, P> T accept(Visitor<T, P> visitor, P arg) {
    return visitor.visit(this, arg);
  }
}

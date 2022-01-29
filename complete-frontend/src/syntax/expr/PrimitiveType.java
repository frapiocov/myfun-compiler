package syntax.expr;

import syntax.AbstractNode;
import visitor.Visitor;

public class PrimitiveType extends AbstractNode implements Type {

  private String value;

  public PrimitiveType(int leftLocation, int rightLocation, String value) {
    super(leftLocation, rightLocation);
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public <T, P> T accept(Visitor<T, P> visitor, P arg) {
    return visitor.visit(this, arg);
  }
}

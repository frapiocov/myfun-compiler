package syntax.expr;

import visitor.Visitor;

public class IdOutpar extends Id{

  private String outpar;

  public IdOutpar(int leftLocation, int rightLocation, String identifier) {
    super(leftLocation, rightLocation, identifier);
    this.outpar="OUT";
  }

  @Override
  public <T, P> T accept(Visitor<T, P> visitor, P arg) {
    return visitor.visit(this, arg);
  }
}

package syntax.expr;

import syntax.AbstractNode;
import syntax.Expr;
import syntax.Leaf;
import visitor.Visitor;

public class Id extends AbstractNode implements Leaf<String>,Expr {

  private String identifier;

  public Id(int leftLocation, int rightLocation, String identifier) {
    super(leftLocation, rightLocation);
    this.identifier = identifier;
  }

  @Override
  public String getValue() {
    return this.identifier;
  }

  @Override
  public <T, P> T accept(Visitor<T, P> visitor, P arg) {
    return visitor.visit(this, arg);
  }
}

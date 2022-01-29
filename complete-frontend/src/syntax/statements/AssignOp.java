package syntax.statements;

import syntax.AbstractNode;
import syntax.Expr;
import syntax.Statement;
import syntax.expr.Id;
import visitor.Visitor;

public class AssignOp extends AbstractNode implements Statement {

  private Id id;
  private Expr expr;

  public AssignOp(int leftLocation, int rightLocation, Id id, Expr expr) {
    super(leftLocation, rightLocation);
    this.id = id;
    this.expr = expr;
  }

  public Id getId() {
    return id;
  }

  public Expr getExpr() {
    return expr;
  }

  @Override
  public <T, P> T accept(Visitor<T, P> visitor, P arg) {
    return visitor.visit(this, arg);
  }
}

package syntax.expr;

import syntax.AbstractNode;
import syntax.Expr;
import visitor.Visitor;

public class UminusOp extends AbstractNode implements Expr {

    private Expr expr;

    public UminusOp(int leftLocation, int rightLocation, Expr expr) {
        super(leftLocation, rightLocation);
        this.expr = expr;
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public <T, P> T accept(Visitor<T, P> visitor, P arg) {
        return visitor.visit(this, arg);
    }
}

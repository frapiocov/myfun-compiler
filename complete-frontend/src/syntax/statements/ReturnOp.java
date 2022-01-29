package syntax.statements;

import syntax.AbstractNode;
import syntax.Expr;
import syntax.Statement;
import visitor.Visitor;

public class ReturnOp extends AbstractNode implements Statement {

    private Expr expr;

    public ReturnOp(int leftLocation, int rightLocation, Expr expr) {
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

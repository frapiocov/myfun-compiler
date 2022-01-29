package syntax.statements;

import syntax.AbstractNode;
import syntax.BodyOp;
import syntax.Expr;
import syntax.Statement;
import visitor.Visitor;

public class WhileOp extends AbstractNode implements Statement {

    private Expr expr;
    private BodyOp bodyOp;

    public WhileOp(int leftLocation, int rightLocation, Expr e, BodyOp b) {
        super(leftLocation, rightLocation);
        this.expr = e;
        this.bodyOp = b;
    }

    public Expr getExpr() {
        return expr;
    }

    public BodyOp getBodyOp() {
        return bodyOp;
    }


    @Override
    public <T, P> T accept(Visitor<T, P> visitor, P arg) {
        return visitor.visit(this, arg);
    }
}

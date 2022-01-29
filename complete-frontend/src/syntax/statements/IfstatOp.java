package syntax.statements;

import syntax.AbstractNode;
import syntax.BodyOp;
import syntax.Expr;
import syntax.Statement;
import visitor.Visitor;

public class IfstatOp extends AbstractNode implements Statement {

    private Expr expr;
    private BodyOp bodyOp;
    private ElseOp elseop;

    public IfstatOp(int leftLocation, int rightLocation, Expr e, BodyOp b, ElseOp el) {
        super(leftLocation, rightLocation);
        this.expr = e;
        this.bodyOp = b;
        this.elseop = el;
    }

    public Expr getExpr() {
        return expr;
    }

    public BodyOp getBodyOp() {
        return bodyOp;
    }

    public ElseOp getElseop() {
        return elseop;
    }

    @Override
    public <T, P> T accept(Visitor<T, P> visitor, P arg) {
        return visitor.visit(this, arg);
    }
}

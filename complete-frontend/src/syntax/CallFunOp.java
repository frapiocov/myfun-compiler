package syntax;

import syntax.expr.Id;
import visitor.Visitor;

import java.util.LinkedList;

public class CallFunOp extends AbstractNode implements Expr, Statement {

    private Id id;
    private LinkedList<Expr> params;

    public CallFunOp(int leftLocation, int rightLocation, Id id, LinkedList<Expr> params) {
        super(leftLocation, rightLocation);
        this.id = id;
        this.params = params;
    }

    public CallFunOp(int leftLocation, int rightLocation, Id id) {
        super(leftLocation, rightLocation);
        this.id = id;
    }

    public CallFunOp(int leftLocation, int rightLocation) {
        super(leftLocation, rightLocation);
    }

    public Id getId() {
        return id;
    }

    public LinkedList<Expr> getParams() {
        return params;
    }


    @Override
    public <T, P> T accept(Visitor<T, P> visitor, P arg) {
        return visitor.visit(this, arg);
    }
}

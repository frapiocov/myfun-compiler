package syntax.statements;

import syntax.AbstractNode;
import syntax.Expr;
import syntax.Statement;
import syntax.expr.Id;
import visitor.Visitor;

import java.util.LinkedList;

public class ReadOp extends AbstractNode implements Statement {
    private LinkedList<Id> ids;
    private Expr expr;

    public ReadOp(int leftLocation, int rightLocation, LinkedList<Id> ids, Expr expr) {
        super(leftLocation, rightLocation);
        this.ids = ids;
        this.expr = expr;
    }

    public ReadOp(int leftLocation, int rightLocation, LinkedList<Id> ids) {
        super(leftLocation, rightLocation);
        this.ids = ids;
    }

    public LinkedList<Id> getIds() {
        return ids;
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public <T, P> T accept(Visitor<T, P> visitor, P arg) {
        return visitor.visit(this, arg);
    }
}

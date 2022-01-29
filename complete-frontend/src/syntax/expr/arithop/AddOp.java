package syntax.expr.arithop;

import syntax.AbstractNode;
import syntax.Expr;
import visitor.Visitor;

public class AddOp extends AbstractNode implements Expr {

    private Expr leftValue;
    private Expr rightValue;

    public AddOp(int leftLocation, int rightLocation, Expr leftOperand, Expr rightOperand) {
        super(leftLocation, rightLocation);
        this.leftValue = leftOperand;
        this.rightValue = rightOperand;
    }

    public Expr getLeftValue() {
        return leftValue;
    }

    public Expr getRightValue() {
        return rightValue;
    }

    @Override
    public <T, P> T accept(Visitor<T, P> visitor, P arg) {
        return visitor.visit(this, arg);
    }
}

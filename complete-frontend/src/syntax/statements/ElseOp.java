package syntax.statements;

import syntax.AbstractNode;
import syntax.BodyOp;
import syntax.Statement;
import visitor.Visitor;

public class ElseOp extends AbstractNode implements Statement{

    private BodyOp bodyOp;

    public ElseOp(int leftLocation, int rightLocation, BodyOp bodyOp) {
        super(leftLocation, rightLocation);
        this.bodyOp = bodyOp;
    }

    public BodyOp getBodyOp() {
        return bodyOp;
    }

    @Override
    public <T, P> T accept(Visitor<T, P> visitor, P arg) {
        return visitor.visit(this, arg);
    }
}

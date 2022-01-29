package syntax;

import syntax.expr.Id;
import syntax.expr.PrimitiveType;
import visitor.Visitor;

import java.util.LinkedList;

public class FunOp extends AbstractNode {

    private Id id;
    private LinkedList<ParDeclOp> parDeclOp;
    private PrimitiveType type;
    private BodyOp bodyOp;

    public FunOp(int leftLocation, int rightLocation, Id id, LinkedList<ParDeclOp> parDeclOp, PrimitiveType type, BodyOp bodyOp) {
        super(leftLocation, rightLocation);
        this.id = id;
        this.parDeclOp = parDeclOp;
        this.type = type;
        this.bodyOp = bodyOp;
    }

    public FunOp(int leftLocation, int rightLocation, Id id, LinkedList<ParDeclOp> parDeclOp, BodyOp bodyOp) {
        super(leftLocation, rightLocation);
        this.id = id;
        this.parDeclOp = parDeclOp;
        this.bodyOp = bodyOp;
    }

    public Id getId() {
        return id;
    }

    public LinkedList<ParDeclOp> getParDeclOp() {
        return parDeclOp;
    }

    public PrimitiveType getType() {
        return type;
    }

    public BodyOp getBodyOp() {
        return bodyOp;
    }

    @Override
    public <T, P> T accept(Visitor<T, P> visitor, P arg) {
        return visitor.visit(this, arg);
    }
}

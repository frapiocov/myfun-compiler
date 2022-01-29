package syntax;

import syntax.expr.Id;
import syntax.expr.PrimitiveType;
import visitor.Visitor;

public class ParDeclOp extends AbstractNode{

    private String kind;
    private PrimitiveType type;
    private Id id;

    public ParDeclOp(int leftLocation, int rightLocation,String kind, PrimitiveType type, Id id) {
        super(leftLocation, rightLocation);
        this.kind = kind;
        this.type = type;
        this.id = id;
    }

    public PrimitiveType getType() {
        return type;
    }

    public Id getId() {
        return id;
    }

    public String getKind() { return kind; }

    @Override
    public <T, P> T accept(Visitor<T, P> visitor, P arg) {
        return visitor.visit(this, arg);
    }
}

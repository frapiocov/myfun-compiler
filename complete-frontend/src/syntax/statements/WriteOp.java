package syntax.statements;

import nodetype.NodeType;
import nodetype.PrimitiveNodeType;
import syntax.AbstractNode;
import syntax.CallFunOp;
import syntax.Expr;
import syntax.Statement;
import syntax.expr.Id;
import syntax.expr.MinusOp;
import syntax.expr.StrCatOp;
import syntax.expr.arithop.*;
import syntax.expr.constant.*;
import syntax.expr.relop.*;
import visitor.Visitor;

public class WriteOp extends AbstractNode implements Statement {

    private Expr expr;
    private String write;

    public WriteOp(int leftLocation, int rightLocation, Expr expr, String write) {
        super(leftLocation, rightLocation);
        this.expr = expr;
        this.write = write;
    }

    public Expr getExpr() {
        return expr;
    }

    public String getWriteC(String expr, Expr e) {
        String classe = e.getClass().toString();
        NodeType type;
        switch (classe){
            case "class syntax.CallFunOp"                   : {type = ((CallFunOp)e).getNodeType(); return "printf(\""+getFormat(type)+getLineTerminator()+"\","+expr.substring(0,expr.length()-1)+");"; }
            case "class syntax.expr.Id"                     : {type = ((Id)e).getNodeType(); return "printf(\""+getFormat(type)+getLineTerminator()+"\","+expr+");"; }
            case "class syntax.expr.MinusOp"                : {type = ((MinusOp)e).getNodeType(); return "printf(\""+getFormat(type)+getLineTerminator()+"\","+expr+");"; }
            case "class syntax.expr.constant.StringConst"   : {type = ((StringConst)e).getNodeType(); return "printf(\""+getFormat(type)+getLineTerminator()+"\","+expr+");"; }
            case "class syntax.expr.constant.IntegerConst"  : {type = ((IntegerConst)e).getNodeType(); return "printf(\""+getFormat(type)+getLineTerminator()+"\","+expr+");"; }
            case "class syntax.expr.constant.RealConst"     : {type = ((RealConst)e).getNodeType(); return "printf(\""+getFormat(type)+getLineTerminator()+"\","+expr+");"; }
            case "class syntax.expr.constant.TrueConst"     : {type = ((TrueConst)e).getNodeType(); return "printf(\""+getFormat(type)+getLineTerminator()+"\","+expr+");"; }
            case "class syntax.expr.constant.FalseConst"    : {type = ((FalseConst)e).getNodeType(); return "printf(\""+getFormat(type)+getLineTerminator()+"\","+expr+");"; }
            case "class syntax.expr.arithop.AddOp"          : {type = ((AddOp)e).getNodeType(); return "printf(\""+getFormat(type)+getLineTerminator()+"\","+expr+");"; }
            case "class syntax.expr.arithop.DiffOp"         : {type = ((DiffOp)e).getNodeType(); return "printf(\""+getFormat(type)+getLineTerminator()+"\","+expr+");"; }
            case "class syntax.expr.arithop.DivIntOp"       : {type = ((DivIntOp)e).getNodeType(); return "printf(\""+getFormat(type)+getLineTerminator()+"\","+expr+");"; }
            case "class syntax.expr.arithop.DivOp"          : {type = ((DivOp)e).getNodeType(); return "printf(\""+getFormat(type)+getLineTerminator()+"\","+expr+");"; }
            case "class syntax.expr.arithop.MulOp"          : {type = ((MulOp)e).getNodeType(); return "printf(\""+getFormat(type)+getLineTerminator()+"\","+expr+");"; }
            case "class syntax.expr.arithop.PowOp"          : {type = ((PowOp)e).getNodeType(); return "printf(\""+getFormat(type)+getLineTerminator()+"\","+expr+");"; }
            case "class syntax.expr.relop.AndOp"            : {type = ((AndOp)e).getNodeType(); return "printf(\""+getFormat(type)+getLineTerminator()+"\","+expr+");"; }
            case "class syntax.expr.relop.EQOp"             : {type = ((EQOp)e).getNodeType(); return "printf(\""+getFormat(type)+getLineTerminator()+"\","+expr+");"; }
            case "class syntax.expr.relop.GEOp"             : {type = ((GEOp)e).getNodeType(); return "printf(\""+getFormat(type)+getLineTerminator()+"\","+expr+");"; }
            case "class syntax.expr.relop.GtOp"             : {type = ((GTOp)e).getNodeType(); return "printf(\""+getFormat(type)+getLineTerminator()+"\","+expr+");"; }
            case "class syntax.expr.relop.LEOp"             : {type = ((LEOp)e).getNodeType(); return "printf(\""+getFormat(type)+getLineTerminator()+"\","+expr+");"; }
            case "class syntax.expr.relop.LTOp"             : {type = ((LTOp)e).getNodeType(); return "printf(\""+getFormat(type)+getLineTerminator()+"\","+expr+");"; }
            case "class syntax.expr.relop.NEOp"             : {type = ((NEOp)e).getNodeType(); return "printf(\""+getFormat(type)+getLineTerminator()+"\","+expr+");"; }
            case "class syntax.expr.relop.NotOp"            : {type = ((NotOp)e).getNodeType(); return "printf(\""+getFormat(type)+getLineTerminator()+"\","+expr+");"; }
            case "class syntax.expr.relop.OrOp"             : {type = ((OrOp)e).getNodeType(); return "printf(\""+getFormat(type)+getLineTerminator()+"\","+expr+");"; }
            case "class syntax.expr.StrCatOp"               : {type = ((StrCatOp)e).getNodeType(); return "printf(\""+getFormat(type)+getLineTerminator()+"\","+expr+");"; }
            default         : return "error write";
        }
    }

    @Override
    public <T, P> T accept(Visitor<T, P> visitor, P arg) {
        return visitor.visit(this, arg);
    }

    public String getFormat(NodeType type){
        PrimitiveNodeType nodeType =  (PrimitiveNodeType) type;
        switch (nodeType.getNodoType()){
            case "integer"  : return "%d";
            case "real"     : return "%f";
            case "bool"     : return "%i";
            case "string"   : return "%s";
            default         : return "%s";
        }
    }

    public String getLineTerminator(){
        switch (write){
            case "write"    : return "";
            case "writeln"  : return "\\n";
            case "writet"   : return "\\t";
            case "writeb"   : return " ";
            default: return "";
        }
    }
}

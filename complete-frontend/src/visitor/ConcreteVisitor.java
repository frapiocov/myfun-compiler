package visitor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import syntax.*;
import syntax.expr.*;
import syntax.expr.arithop.*;
import syntax.expr.constant.*;
import syntax.expr.relop.*;
import syntax.statements.*;

import java.util.function.Consumer;

public class ConcreteVisitor implements Visitor<Element, Document>{

    private Consumer<? super AbstractNode> addParent(Element parent, Document arg){
        return (AbstractNode node) -> parent.appendChild(node.accept(this, arg));
    }

    @Override
    public Element visit(Program program, Document arg) {
        Element element = arg.createElement("Program");
        program.getVarDeclOpList().forEach(addParent(element, arg));
        program.getFunOpList().forEach(addParent(element, arg));
        element.appendChild(program.getBodyOp().accept(this, arg));
        arg.appendChild(element);
        return element;
    }

    @Override
    public Element visit(AssignOp assignOp, Document arg) {
        Element element = arg.createElement("AssignOp");
        element.appendChild(assignOp.getId().accept(this, arg));
        element.appendChild(assignOp.getExpr().accept(this, arg));
        return element;
    }

    @Override
    public Element visit(BodyOp bodyOp, Document arg) {
        Element element = arg.createElement("BodyOp");
        if(bodyOp.getVarDeclList() != null){
            bodyOp.getVarDeclList().forEach(addParent(element, arg));
        }
        //se l'elemento in posizione 0 è null significa che la lista è stata creata ma non contine nessuno statement
        if( bodyOp.getStatList() != null && bodyOp.getStatList().get(0) != null){
            bodyOp.getStatList().forEach((Consumer<? super Statement>) addParent(element, arg));
        }
        return element;
    }

    @Override
    public Element visit(FunOp funOp, Document arg) {
        Element element = arg.createElement("FunOp");
        element.appendChild(funOp.getId().accept(this, arg));
        if(funOp.getParDeclOp() != null){
            funOp.getParDeclOp().forEach(addParent(element, arg));
        }
        if(funOp.getType() != null){
            element.appendChild(funOp.getType().accept(this, arg));
        }
        element.appendChild(funOp.getBodyOp().accept(this, arg));
        return element;
    }

    @Override
    public Element visit(IdInitOp idInitOp, Document arg) {
        Element element = arg.createElement("IdInitOp");
        element.appendChild(idInitOp.getId().accept(this, arg));
        if(idInitOp.getExpr() != null){
            element.appendChild(idInitOp.getExpr().accept(this, arg));
        }
        return element;
    }

    @Override
    public Element visit(ParDeclOp parDeclOp, Document arg) {
        Element element = arg.createElement("ParDeclOp");
        if(parDeclOp.getKind().equals("IN")){
            element.appendChild(arg.createElement("IN"));
        }else{
            element.appendChild(arg.createElement("OUT"));
        }
        element.appendChild((parDeclOp.getType().accept(this, arg)));
        element.appendChild(parDeclOp.getId().accept(this, arg));
        return element;
    }

    @Override
    public Element visit(VarDeclOp varDeclOp, Document arg) {
        Element element = arg.createElement("VarDeclOp");
        element.appendChild(varDeclOp.getPrimitiveType().accept(this, arg));
        varDeclOp.getIdInitList().forEach(addParent(element, arg));
        return element;
    }

    @Override
    public Element visit(CallFunOp callFunOp, Document arg) {
        Element element = arg.createElement("CallFunOp");
        element.appendChild(callFunOp.getId().accept(this, arg));
        if(callFunOp.getParams() != null){
            callFunOp.getParams().forEach((Consumer<? super Expr>) addParent(element, arg));
        }
        return element;
    }

    @Override
    public Element visit(ElseOp elseOp, Document arg) {
        Element element = arg.createElement("ElseOp");
        element.appendChild(elseOp.getBodyOp().accept(this, arg));
        return element;
    }

    @Override
    public Element visit(IfstatOp ifstatOp, Document arg) {
        Element element = arg.createElement("IfstatOp");
        element.appendChild(ifstatOp.getExpr().accept(this, arg));
        element.appendChild(ifstatOp.getBodyOp().accept(this, arg));
        if(ifstatOp.getElseop() != null){
            element.appendChild(ifstatOp.getElseop().accept(this, arg));
        }
        return element;
    }

    @Override
    public Element visit(ReadOp readOp, Document arg) {
        Element element = arg.createElement("ReadOp");
        readOp.getIds().forEach(addParent(element, arg));
        if(readOp.getExpr() != null){
            element.appendChild(readOp.getExpr().accept(this, arg));
        }
        return element;
    }

    @Override
    public Element visit(ReturnOp returnOp, Document arg) {
        Element element = arg.createElement("ReturnOp");
        element.appendChild(returnOp.getExpr().accept(this, arg));
        return element;
    }

    @Override
    public Element visit(WhileOp whileOp, Document arg) {
        Element element = arg.createElement("WhileOp");
        element.appendChild(whileOp.getExpr().accept(this, arg));
        element.appendChild(whileOp.getBodyOp().accept(this, arg));
        return element;
    }

    @Override
    public Element visit(WriteOp writeOp, Document arg) {
        Element element = arg.createElement("WriteOp");
        element.appendChild(writeOp.getExpr().accept(this, arg));
        return element;
    }

    @Override
    public Element visit(Id id, Document arg) {
        Element element = arg.createElement("Id");
        element.setAttribute("lessema", id.getValue());
        return element;
    }

    @Override
    public Element visit(IdOutpar id, Document arg) {
        Element element = arg.createElement("IdOutpar");
        element.setAttribute("lessema", id.getValue());
        return element;
    }

    @Override
    public Element visit(MinusOp minusOp, Document arg) {
        Element element = arg.createElement("MinusOp");
        element.appendChild(minusOp.getExpr().accept(this, arg));
        return element;
    }

    @Override
    public Element visit(ParExprOp parExprOp, Document arg) {
        Element element = arg.createElement("ParExprOp");
        element.appendChild(parExprOp.getExpr().accept(this, arg));
        return element;
    }

    @Override
    public Element visit(PrimitiveType primitiveType, Document arg) {
        Element element = arg.createElement("PrimitiveType");
        element.setAttribute("type", primitiveType.getValue());
        return element;
    }

    @Override
    public Element visit(StrCatOp strCatOp, Document arg) {
        Element element = arg.createElement("StrCatOp");
        element.appendChild(strCatOp.getLeftValue().accept(this, arg));
        element.appendChild(strCatOp.getRightValue().accept(this, arg));
        return element;
    }

    @Override
    public Element visit(UminusOp uminusOp, Document arg) {
        Element element = arg.createElement("UminusOp");
        element.appendChild(uminusOp.getExpr().accept(this, arg));
        return element;
    }

    @Override
    public Element visit(AndOp andOp, Document arg) {
        Element element = arg.createElement("AndOp");
        element.appendChild(andOp.getLeftValue().accept(this, arg));
        element.appendChild(andOp.getRightValue().accept(this, arg));
        return element;
    }

    @Override
    public Element visit(EQOp eqOp, Document arg) {
        Element element = arg.createElement("EqOp");
        element.appendChild(eqOp.getLeftValue().accept(this, arg));
        element.appendChild(eqOp.getRightValue().accept(this, arg));
        return element;
    }

    @Override
    public Element visit(GEOp geOp, Document arg) {
        Element element = arg.createElement("GEOp");
        element.appendChild(geOp.getLeftValue().accept(this, arg));
        element.appendChild(geOp.getRightValue().accept(this, arg));
        return element;
    }

    @Override
    public Element visit(GTOp gtOp, Document arg) {
        Element element = arg.createElement("GTOp");
        element.appendChild(gtOp.getLeftValue().accept(this, arg));
        element.appendChild(gtOp.getRightValue().accept(this, arg));
        return element;
    }

    @Override
    public Element visit(LEOp leOp, Document arg) {
        Element element = arg.createElement("LEOp");
        element.appendChild(leOp.getLeftValue().accept(this, arg));
        element.appendChild(leOp.getRightValue().accept(this, arg));
        return element;
    }

    @Override
    public Element visit(LTOp ltOp, Document arg) {
        Element element = arg.createElement("LTOp");
        element.appendChild(ltOp.getLeftValue().accept(this, arg));
        element.appendChild(ltOp.getRightValue().accept(this, arg));
        return element;
    }

    @Override
    public Element visit(NEOp neOp, Document arg) {
        Element element = arg.createElement("NEOp");
        element.appendChild(neOp.getLeftValue().accept(this, arg));
        element.appendChild(neOp.getRightValue().accept(this, arg));
        return element;
    }

    @Override
    public Element visit(NotOp notOp, Document arg) {
        Element element = arg.createElement("NotOp");
        element.appendChild(notOp.getExpr().accept(this, arg));
        return element;
    }

    @Override
    public Element visit(OrOp orOp, Document arg) {
        Element element = arg.createElement("OrOp");
        element.appendChild(orOp.getLeftValue().accept(this, arg));
        element.appendChild(orOp.getRightValue().accept(this, arg));
        return element;
    }

    @Override
    public Element visit(StringConst stringConst, Document arg) {
        Element element = arg.createElement("StringConst");
        element.setAttribute("value", stringConst.getValue());
        return element;
    }


    @Override
    public Element visit(TrueConst trueConst, Document arg) {
        Element element = arg.createElement("TrueConst");
        element.setAttribute("value", String.valueOf(trueConst.getValue()));
        return element;
    }

    @Override
    public Element visit(FalseConst falseConst, Document arg) {
        Element element = arg.createElement("FalseConst");
        element.setAttribute("value", String.valueOf(falseConst.getValue()));
        return element;
    }

    @Override
    public Element visit(IntegerConst integerConst, Document arg) {
        Element element = arg.createElement("IntegerConst");
        element.setAttribute("value", String.valueOf(integerConst.getValue()));
        return element;
    }

    @Override
    public Element visit(RealConst realConst, Document arg) {
        Element element = arg.createElement("RealConst");
        element.setAttribute("value", String.valueOf(realConst.getValue()));
        return element;
    }

    @Override
    public Element visit(AddOp addOp, Document arg) {
        Element element = arg.createElement("AddOp");
        element.appendChild(addOp.getLeftValue().accept(this, arg));
        element.appendChild(addOp.getRightValue().accept(this, arg));
        return element;
    }

    @Override
    public Element visit(DiffOp diffOp, Document arg) {
        Element element = arg.createElement("DiffOp");
        element.appendChild(diffOp.getLeftValue().accept(this, arg));
        element.appendChild(diffOp.getRightValue().accept(this, arg));
        return element;
    }

    @Override
    public Element visit(DivIntOp divIntOp, Document arg) {
        Element element = arg.createElement("DivIntOp");
        element.appendChild(divIntOp.getLeftValue().accept(this, arg));
        element.appendChild(divIntOp.getRightValue().accept(this, arg));
        return element;
    }

    @Override
    public Element visit(DivOp divOp, Document arg) {
        Element element = arg.createElement("DivOp");
        element.appendChild(divOp.getLeftValue().accept(this, arg));
        element.appendChild(divOp.getRightValue().accept(this, arg));
        return element;
    }

    @Override
    public Element visit(MulOp mulOp, Document arg) {
        Element element = arg.createElement("MulOp");
        element.appendChild(mulOp.getLeftValue().accept(this, arg));
        element.appendChild(mulOp.getRightValue().accept(this, arg));
        return element;
    }

    @Override
    public Element visit(PowOp powOp, Document arg) {
        Element element = arg.createElement("PowOp");
        element.appendChild(powOp.getLeftValue().accept(this, arg));
        element.appendChild(powOp.getRightValue().accept(this, arg));
        return element;
    }
}

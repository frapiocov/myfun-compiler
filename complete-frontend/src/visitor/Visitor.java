package visitor;

import syntax.*;
import syntax.expr.*;
import syntax.expr.arithop.*;
import syntax.expr.constant.*;
import syntax.expr.relop.*;
import syntax.statements.*;

public interface Visitor<T, P> {
  T visit(Program program, P arg);
  T visit(AssignOp assignOp, P arg);
  T visit(BodyOp bodyOp, P arg);
  T visit(FunOp funOp, P arg);
  T visit(IdInitOp idInitOp, P arg);
  T visit(ParDeclOp parDeclOp, P arg);
  T visit(VarDeclOp varDeclOp, P arg);
  T visit(CallFunOp callFunOp, P arg);
  T visit(ElseOp elseOp, P arg);
  T visit(IfstatOp ifstatOp, P arg);
  T visit(ReadOp readOp, P arg);
  T visit(ReturnOp returnOp, P arg);
  T visit(WhileOp whileOp, P arg);
  T visit(WriteOp writeOp, P arg);
  T visit(Id id, P arg);
  T visit(IdOutpar id, P arg);
  T visit(MinusOp minusOp, P arg);
  T visit(ParExprOp parExprOp, P arg);
  T visit(PrimitiveType primitiveType, P arg);
  T visit(StrCatOp strCatOp, P arg);
  T visit(UminusOp uminusOp, P arg);
  T visit(AndOp andOp, P arg);
  T visit(EQOp eqOp, P arg);
  T visit(GEOp geOp, P arg);
  T visit(GTOp gtOp, P arg);
  T visit(LEOp leOp, P arg);
  T visit(LTOp ltOp, P arg);
  T visit(NEOp neOp, P arg);
  T visit(NotOp notOp, P arg);
  T visit(OrOp orOp, P arg);
  T visit(TrueConst trueConst, P arg);
  T visit(FalseConst falseConst, P arg);
  T visit(IntegerConst integerConst, P arg);
  T visit(RealConst realConst, P arg);
  T visit(StringConst stringConst, P arg);
  T visit(AddOp addOp, P arg);
  T visit(DiffOp diffOp, P arg);
  T visit(DivIntOp divIntOp, P arg);
  T visit(DivOp divOp, P arg);
  T visit(MulOp mulOp, P arg);
  T visit(PowOp powOp, P arg);
}

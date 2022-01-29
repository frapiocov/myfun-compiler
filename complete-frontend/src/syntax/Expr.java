package syntax;

import nodetype.NodeType;
import visitor.Visitor;

public interface Expr {
  <T, P> T accept(Visitor<T, P> visitor, P arg);
}

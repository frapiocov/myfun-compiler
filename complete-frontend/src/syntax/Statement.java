package syntax;

import visitor.Visitor;

public interface Statement{
  <T, P> T accept(Visitor<T, P> visitor, P arg);
}

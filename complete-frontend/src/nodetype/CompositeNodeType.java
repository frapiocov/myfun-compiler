package nodetype;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CompositeNodeType implements NodeType {

  private final List<NodeType> types;
  private List<String> kinds;

  public CompositeNodeType(List<NodeType> types) {
    this.types = types;
    this.kinds = new ArrayList<String>();
  }

  @Override
  public PrimitiveNodeType checkAdd(PrimitiveNodeType type) {
    return null;
  }

  @Override
  public PrimitiveNodeType checkSub(PrimitiveNodeType type) {
    return null;
  }

  @Override
  public PrimitiveNodeType checkMul(PrimitiveNodeType type) {
    return null;
  }

  @Override
  public PrimitiveNodeType checkDiv(PrimitiveNodeType type) {
    return null;
  }

  @Override
  public PrimitiveNodeType checkRel(PrimitiveNodeType type) {
    return null;
  }

  public void addNodeType(NodeType type){
    this.types.add(type);
  }

  public List<NodeType> getTypes() {
    return this.types;
  }

  public List<String> getKinds() {
    return kinds;
  }

  public void setKinds(List<String> kinds) {
    this.kinds = kinds;
  }

  public void addKind(String s) {
    this.kinds.add(s);
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 11 * hash + Objects.hashCode(this.types);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (getClass() != obj.getClass()) {
      return false;
    } else {
      final CompositeNodeType other = (CompositeNodeType) obj;
      return Objects.equals(this.types, other.types);
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if(this.types!=null) {
      this.types.forEach(t -> sb.append(t.toString()+" "));
      this.kinds.forEach(t -> sb.append(t.toString()+" "));
      return sb.toString();
    }else
      return "";
  }
}
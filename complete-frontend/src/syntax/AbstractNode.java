package syntax;

import nodetype.NodeType;
import visitor.Visitable;

public abstract class AbstractNode implements Visitable {

  private int leftLocation;
  private int rightLocation;
  private NodeType type;

  public AbstractNode(int leftLocation, int rightLocation) {
    this.leftLocation = leftLocation;
    this.rightLocation = rightLocation;
  }

  public NodeType getNodeType() {
    return type;
  }

  public void setNodeType(NodeType type) {
    this.type = type;
  }

  public int getLeftLocation() {
    return leftLocation;
  }

  public int getRightLocation() {
    return rightLocation;
  }
}

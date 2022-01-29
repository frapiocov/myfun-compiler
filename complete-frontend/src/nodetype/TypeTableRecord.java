package nodetype;

public class TypeTableRecord {

  private String op;
  private NodeType type1;
  private NodeType type2;
  private NodeType result;

  public TypeTableRecord(String op, NodeType type1, NodeType type2, NodeType result) {
    this.op = op;
    this.type1 = type1;
    this.type2 = type2;
    this.result = result;
  }

  public String getOp() {
    return op;
  }

  public NodeType getType1() {
    return type1;
  }

  public NodeType getType2() {
    return type2;
  }

  public NodeType getResult() {
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if(obj==null)
      return false;
    if(obj instanceof TypeTableRecord){
      TypeTableRecord record = (TypeTableRecord) obj;
      return this.op.equals(record.op) && this.type1.equals(record.type1) && this.type2.equals(record.type2);
    }
    return false;
  }
}

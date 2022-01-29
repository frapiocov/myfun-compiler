package error;

import static java.lang.System.exit;

public class ErrorHandler {

  public ErrorHandler(String msg) {
    System.err.println(msg);
    exit(0);
  }
}

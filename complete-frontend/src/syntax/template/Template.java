package syntax.template;

import java.util.Optional;

public interface Template<T> {
  void write(String path, T model);
  Optional<T> create();
}

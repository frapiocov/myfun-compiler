package template;

import java.io.File;
import java.util.Optional;

public interface Template<T> {
    void write(File file, T model);
    Optional<File> create(String name);
}

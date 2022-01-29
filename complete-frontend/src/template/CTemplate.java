package template;

import java.io.File;
import java.io.FileWriter;
import java.util.Optional;

public class CTemplate implements Template<String>{
    @Override
    public void write(File file, String model) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(model); //just an example how you can write a String to it
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<File> create(String name) {
        File file = new File(name);
        return Optional.ofNullable(file);
    }
}

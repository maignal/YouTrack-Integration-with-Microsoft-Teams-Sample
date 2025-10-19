package cloud.youtrackintegration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private final Properties props = new Properties();

    public Config(String path) throws IOException {
        props.load(new FileInputStream(path));
    }

    public String get(String key) {
        return props.getProperty(key);
    }
}

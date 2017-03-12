package GitHR.Services;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component
public class PropertyService {

    static private final Properties properties;
    static private final String pathToFile = "./src/main/java/GitHR/config.properties";

    static {
        properties = new Properties();
        try (InputStream input = new FileInputStream(pathToFile)) {
            properties.load(input);

            System.out.println("Get Properties: ");
            System.out.println(properties.stringPropertyNames());

        } catch (IOException ex) {
            System.out.println("Problem with property file !!!");
            ex.printStackTrace();
        }
    }

    public Properties getProperties() {
        return properties;
    }

    public String getProperty(@NotNull String propertyName) {
        return properties.getProperty(propertyName);
    }
}

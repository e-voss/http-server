package ehv3.src.java;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

/**
 * @author Ethan Voss
 *
 * The sole purpose of this class is to parse the .properties file.
 *
 */

public class PropertiesReader {
    String[] result = new String[3];
    InputStream inputStream;

    public String[] getPropValues() {

        try {
            Properties prop = new Properties();
            String propFileName = "config.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            Date time = new Date(System.currentTimeMillis());

            // get the property value and print it out
            String port = prop.getProperty("port_num");
            String test1 = prop.getProperty("html1");
            String test2 = prop.getProperty("html2");

            result[0] = port;
            result[1] = test1;
            result[2] = test2;
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                System.out.println("Properties reader encountered IO Exception: ");
                e.printStackTrace();
            }
        }
        return result;
    }
}
package org.tastefuljava.simili.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

public class Configuration {

    public static Properties loadUserSettings() throws IOException {
        return loadProps(userFile());
    }

    public static void saveUserSettings(Properties props) throws IOException {
        saveProps(props, userFile());
    }

    public static Properties loadProps(File file) throws IOException {
        Properties props = new Properties();
        if (file.isFile()) {
            try (InputStream stream = new FileInputStream(file);
                    Reader in = new InputStreamReader(stream, "UTF-8")) {
                props.load(in);
            }
        }
        return props;
    }

    public static void saveProps(Properties props, File file)
            throws IOException {
        try (OutputStream stream = new FileOutputStream(file);
                Writer out = new OutputStreamWriter(stream)) {
            props.store(out, "Simili user settings");
        }
    }

    private static File userFile() {
        File home = new File(System.getProperty("user.home"));
        File dir = new File(home, ".simili");
        dir.mkdir();
        return new File(dir, "settings.properties");
    }
}

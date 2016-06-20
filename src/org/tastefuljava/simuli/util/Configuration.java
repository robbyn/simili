package org.tastefuljava.simuli.util;

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
import java.net.URL;
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

    public static Properties loadProps(URL url) throws IOException {
        Properties props = new Properties();
        try (InputStream stream = url.openStream();
                Reader in = new InputStreamReader(stream, "UTF-8")) {
            props.load(in);
        }
        return props;
    }

    public static File userHome() {
        return new File(System.getProperty("user.home"));
    }

    private static File userFile() {
        File home = userHome();
        File dir = new File(home, ".simili");
        dir.mkdir();
        return new File(dir, "settings.properties");
    }
}
